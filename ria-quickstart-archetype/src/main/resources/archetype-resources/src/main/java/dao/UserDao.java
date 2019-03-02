package ${package}.dao;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.WriteResult;

import ${package}.model.User;
import com.mongodb.WriteResult;

/**
 * Dao for {@link User}.
 * 
 * @author Donato Rimenti
 */
@Repository
public class UserDao {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(UserDao.class);

	/**
	 * The collection name.
	 */
	private final static String COLLECTION_NAME = "user";

	/**
	 * The email field.
	 */
	private final static String EMAIL_FIELD = "email";

	/**
	 * The name field.
	 */
	private static final String NAME_FIELD = "name";

	/**
	 * The surname field.
	 */
	private static final String SURNAME_FIELD = "surname";

	/**
	 * The birthday field.
	 */
	private static final String BIRTHDAY_FIELD = "birthday";

	/**
	 * The creation time field.
	 */
	private static final String CREATION_TIME_FIELD = "creationTime";

	/**
	 * The update time field.
	 */
	private static final String UPDATE_TIME_FIELD = "updateTime";

	/**
	 * The authorities field which represents a user's permissions.
	 */
	private static final String AUTHORITIES_FIELD = "authorities";

	/**
	 * The role field which represents a user's role inside a permission.
	 */
	private static final String ROLE_FIELD = "role";

	/**
	 * The admin role value.
	 */
	private static final String ROLE_ADMIN = "ROLE_ADMIN";

	/**
	 * The mongo template.
	 */
	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * Removes all the users from the collection.
	 */
	public void deleteAll() {
		LOG.warn("UserDao.deleteAll()");
		mongoTemplate.getCollection(COLLECTION_NAME).drop();
	}

	/**
	 * Inserts a new user into the collection.
	 *
	 * @param user
	 *            the user to insert
	 */
	public void insertUser(User user) {
		LOG.trace("UserDao.insertUser(*)");
		mongoTemplate.insert(user);
	}

	/**
	 * Updates one user.
	 * 
	 * @param user
	 *            the user to update
	 */
	public void updateUser(User user) {
		LOG.info("UserDao.updateUser(*)");
		mongoTemplate.save(user);
	}

	/**
	 * Gets a user by its email.
	 *
	 * @param email
	 *            the email of the user to retrieve
	 * @return a user
	 */
	public User getUserByEmail(String email) {
		LOG.trace("UserDao.getUserByEmail(*)");
		Query query = new Query();
		query.addCriteria(Criteria.where(Fields.UNDERSCORE_ID).is(email));
		return mongoTemplate.findOne(query, User.class);
	}

	/**
	 * Updates the user last update time field with the current time.
	 *
	 * @param email
	 *            the email of the user to update
	 * @param time
	 *            the last update time to save
	 * @return the outcome of the operation
	 */
	public boolean saveUserUpdateTime(String email, LocalDateTime time) {
		LOG.trace("UserDao.saveUserUpdateTime(*)");
		Query query = new Query();
		query.addCriteria(Criteria.where(Fields.UNDERSCORE_ID).is(email));
		Update update = new Update();
		update.set(UPDATE_TIME_FIELD, time);
		WriteResult result = mongoTemplate.updateFirst(query, update, User.class);
		return result.getN() == 1;
	}

	/**
	 * Checks that a user with a given email is enabled.
	 * 
	 * @param userEmail
	 *            the email to check
	 * @return true if the account is enabled, false otherwise
	 */
	public boolean checkAccountEnabled(String userEmail) {
		LOG.warn("UserDao.checkAccountEnabled({})", userEmail);
		return mongoTemplate.findById(userEmail, User.class).isEnabled();
	}

}