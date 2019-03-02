package ${package}.service;

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jasypt.util.password.PasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ${package}.dao.UserDao;
import ${package}.model.User;
import ${package}.rest.model.CreateUserResponse;
import ${package}.service.utilities.CustomMimeMessagePreparator;

/**
 * Provides the business logic for login related operations.
 * 
 * @author Donato Rimenti
 */
@Service
public class LoginService {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(LoginService.class);

	/**
	 * Dao for users.
	 */
	@Autowired
	private UserDao userDao;

	/**
	 * Encryptor used to digest the password.
	 */
	@Autowired
	private PasswordEncryptor encryptor;

	/**
	 * Server secret used for token generation.
	 */
	@Autowired
	@Qualifier("serverSecret")
	private String serverSecret;

	/**
	 * Used to send confirmation emails after registration.
	 */
	@Autowired
	private JavaMailSender mailSender;

	/**
	 * Template generation for emails.
	 */
	@Autowired
	private VelocityEngine velocityEngine;

	/**
	 * Used to handle asynchronous task.
	 */
	@Autowired
	private TaskExecutor taskExecutor;

	/**
	 * Email sender for the registration confirmation.
	 */
	@Autowired
	@Qualifier("appSenderEmail")
	private String appSenderEmail;

	/**
	 * Tries to create a user. If the creation fails (for example if the user
	 * already exists), returns a negative response with an error message.
	 * 
	 * @param user
	 *            the user to create
	 * @return a response which contains an error message if anything goes wrong
	 */
	public CreateUserResponse createUser(User user) {
		LOG.trace("ReportsService.createUser(*)");
		// Input validations.
		String email = user.getEmail();
		Objects.requireNonNull(user);
		Objects.requireNonNull(user.getName());
		Objects.requireNonNull(user.getSurname());
		Objects.requireNonNull(email);

		// Sets creation times and privileges.
		LocalDateTime updateTime = LocalDateTime.now();
		user.setCreationTime(updateTime);
		user.setUpdateTime(updateTime);
		user.setAuthorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

		// Password hashing.
		String originalUserPassword = user.getPassword();
		user.setPassword(encryptor.encryptPassword(originalUserPassword));

		// Tries to insert the user. If the user is already there, returns an
		// error message.
		String userEmail = user.getEmail();
		try {
			userDao.insertUser(user);
		} catch (DuplicateKeyException e) {
			LOG.warn("Duplicate user [{}].", userEmail);
			return new CreateUserResponse("This email is already registered.");
		}

		// Generates a unique token and sends a confirmation email.
		String token = generateUniqueToken(updateTime, userEmail);
		sendConfirmationEmail(userEmail, token);

		// Everything went well, return a positive response.
		return new CreateUserResponse("Confirmation email sent.", true);
	}

	/**
	 * Generates a new unique token for the confirmation email.
	 * 
	 * @param generationTime
	 *            the time of the generation which needs to be the same of the
	 *            last user update
	 * @param userEmail
	 *            the email of the user for the token
	 * @return a unique token used for authentication
	 */
	private String generateUniqueToken(LocalDateTime generationTime, String userEmail) {
		LOG.trace("ReportsService.generateUniqueToken(*, {})", userEmail);
		String token = encryptor.encryptPassword(userEmail + generationTime + serverSecret);

		// Tries to URLEncode the token in order to prevent escape errors when
		// using them in links.
		try {
			token = URLEncoder.encode(token, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			LOG.error("Error during URLEncoding. Trying wih an URL-unsafe token for user [{}].",
					userEmail, e);
		}

		return token;
	}

	/**
	 * Sends a new confirmation email for account registration. The difference
	 * with {@link #sendConfirmationEmail(String, String)} is that this method
	 * saves a new update time for the user and generates a new token according
	 * to this time in order to renew the token validity.
	 * 
	 * @param userEmail
	 *            the email of the user to whom the confirmation needs to be
	 *            send
	 */
	public void sendConfirmationEmailAgain(String userEmail) {
		LOG.info("ReportsService.sendConfirmationEmailAgain({})", userEmail);
		// Saves a new update time in order to renew the token.
		LocalDateTime updateTime = LocalDateTime.now();
		userDao.saveUserUpdateTime(userEmail, updateTime);

		// Creates an unique token and sends the email again.
		String token = generateUniqueToken(updateTime, userEmail);
		sendConfirmationEmail(userEmail, token);
	}

	/**
	 * Sends a new confirmation email for account registration.
	 * 
	 * @param userEmail
	 *            the email of the user to whom the confirmation needs to be
	 *            send
	 * @see #sendConfirmationEmailAgain(String)
	 */
	private void sendConfirmationEmail(String userEmail, String token) {
		LOG.info("ReportsService.sendConfirmationEmail({})", userEmail);

		// Checks that the account is disabled before sending the activation
		// mail.
		boolean accountEnabled = userDao.checkAccountEnabled(userEmail);
		if (accountEnabled) {
			throw new IllegalStateException("User is already enabled!");
		}

		// Message from template.
		VelocityContext context = new VelocityContext();
		context.put("appPath", getAppUrl());
		context.put("userEmail", userEmail);
		context.put("userToken", token);

		Template template = velocityEngine.getTemplate("registration-confirmation.vm");
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		MimeMessagePreparator preparator = new CustomMimeMessagePreparator(appSenderEmail, userEmail,
				"Account creation", writer.toString());

		// Sends the message. XXX: This is sync to add some delay and prevent
		// spam but it may be prone to congestion depending on the SMTP server
		// workload.
		this.mailSender.send(preparator);
	}

	/**
	 * Builds the app URL which will be in the form:
	 * <code>[protocol]://[host]:[port]/[context_path]</code>
	 * 
	 * @return the app URL
	 */
	private String getAppUrl() {
		// Request data to get the context.
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

		// Builds the application path.
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();
	}

	/**
	 * Sends a password recovery email for a user.
	 * 
	 * @param userEmail
	 *            the email of the user whose password needs to be recovered
	 */
	public void sendRecoverPasswordEmail(String userEmail) {
		LOG.info("ReportsService.sendRecoverPasswordEmail({})", userEmail);

		// We compute this synchronously because we can't access the request
		// from another thread.
		final String appUrl = getAppUrl();

		// All of this is executed asynchronously in order to prevent
		// enumeration attacks.
		taskExecutor.execute(new Runnable() {

			@Override
			public void run() {
				LOG.info("ReportsService.sendRecoverPasswordEmail({}).asyncTask", userEmail);
				// Saves a new update time in order to renew the token.
				LocalDateTime updateTime = LocalDateTime.now();
				boolean outcome = userDao.saveUserUpdateTime(userEmail, updateTime);

				// Checks that the recipient is registered before sending the
				// email.
				if (!outcome) {
					LOG.error("User [{}] asked for password recovery but doesn't exist.");
					return;
				}

				// Creates an unique token and sends the email again.
				String token = generateUniqueToken(updateTime, userEmail);

				// Message from template.
				VelocityContext context = new VelocityContext();
				context.put("appPath", appUrl);
				context.put("userEmail", userEmail);
				context.put("userToken", token);

				Template template = velocityEngine.getTemplate("password-recovery.vm");
				StringWriter writer = new StringWriter();
				template.merge(context, writer);

				MimeMessagePreparator preparator = new CustomMimeMessagePreparator(appSenderEmail, userEmail,
						"Password recovery", writer.toString());

				// Sends the email.
				mailSender.send(preparator);
			}
		});
	}

	/**
	 * Checks that the token passed as argument is valid for the user. If so,
	 * the user account is activated.
	 * 
	 * @param email
	 *            the email to activate
	 * @param token
	 *            the token for the activation
	 * @return the outcome of the operation
	 */
	public boolean activateAccount(String email, String token) {
		LOG.info("ReportsService.activateAccount(*, *)", email, token);
		User user = userDao.getUserByEmail(email);

		// This may happen if the request is forged manually.
		if (user == null) {
			LOG.warn("User with email [{}] does not exist.", email);
			return false;
		}
		// Checks if already enabled.
		if (user.isEnabled()) {
			LOG.warn("User with email [{}] is already enabled.", email);
			return false;
		}

		// Token validation ok.
		if (checkTokenValid(token, user)) {
			user.setUpdateTime(LocalDateTime.now());
			user.setEnabled(true);
			userDao.updateUser(user);
			return true;
		}

		return false;
	}

	/**
	 * Stores a new password for a user with the given email.
	 * 
	 * @param email
	 *            the email of the user
	 * @param token
	 *            the security token for this operation
	 * @param newPassword
	 *            the new password of the user
	 * @return the outcome of the operation
	 */
	public boolean passwordRecovery(String email, String token, String newPassword) {
		LOG.info("ReportsService.passwordRecovery(*, *, *)", email, token);
		User user = userDao.getUserByEmail(email);

		// Token validation ok.
		if (!checkTokenValid(token, user)) {
			return false;
		}

		// Password hashing.
		user.setPassword(encryptor.encryptPassword(newPassword));
		user.setUpdateTime(LocalDateTime.now());

		// Let's also enable the user if it's not. It doesn't make any sense
		// to send another mail.
		user.setEnabled(true);
		userDao.updateUser(user);
		return true;

	}

	/**
	 * Checks if a given token is valid and not expired for a user.
	 * 
	 * @param token
	 *            the token to check
	 * @param user
	 *            the user of the token
	 * @return true if the token is valid and not expired, false otherwise
	 */
	private boolean checkTokenValid(String token, User user) {
		// Performs validation on the token.
		LocalDateTime lastUpdateTime = user.getUpdateTime();
		boolean tokenValid = encryptor.checkPassword(user.getEmail() + lastUpdateTime + serverSecret, token);
		boolean tokenExpired = Duration.between(lastUpdateTime, LocalDateTime.now()).toHours() > 0;

		// Validation ok.
		if (tokenValid && !tokenExpired) {
			LOG.trace("Valid token and not expired.");
			return true;
		}

		LOG.warn("Token is not valid or expired for email [{}].", user.getEmail());
		return false;
	}

}