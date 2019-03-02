package ${package}.security;

import java.util.Objects;

import org.jasypt.util.password.PasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ${package}.dao.UserDao;

/**
 * Provides authentication checking against a custom {@link UserDao}
 * implementation. 
 * 
 * @author Donato Rimenti
 */
public class CustomAuthenticationProvider implements AuthenticationProvider {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

	/**
	 * The dao for the authentication.
	 */
	@Autowired
	private UserDao userDao;
	
	/**
	 * Encryptor used to check the password.
	 */
	@Autowired
	private PasswordEncryptor encryptor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.authentication.AuthenticationProvider#
	 * authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String user = authentication.getName();
		String pass = Objects.toString(authentication.getCredentials(), null);

		UserDetails loadedUser = userDao.getUserByEmail(user);
		if (loadedUser == null) {
			LOG.warn("User [{}] not found.", user);
			throw new UsernameNotFoundException("Wrong email or password.");
		}
		
		// Checks the user password.
		if (!encryptor.checkPassword(pass, loadedUser.getPassword())) {
			LOG.warn("Wrong password for user [{}].", user);
			throw new BadCredentialsException("Wrong email or password.");
		}
		
		// Checks if the account is enabled.
		if (!loadedUser.isEnabled()) {
			LOG.warn("User [{}] is not enabled.", user);
			throw new DisabledException("User not enabled.");
		}

		LOG.info("User [{}] succesfully logged in.", user);
		return new UsernamePasswordAuthenticationToken(loadedUser, pass, loadedUser.getAuthorities());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.authentication.AuthenticationProvider#
	 * supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
