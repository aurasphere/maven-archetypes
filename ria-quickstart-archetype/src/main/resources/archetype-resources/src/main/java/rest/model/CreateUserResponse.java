package ${package}.rest.model;

/**
 * Response for
 * {@link ${package}.rest.LoginRestController#createUser(${package}.model.User, javax.servlet.http.HttpServletRequest)}.
 * 
 * @author Donato Rimenti
 */
public class CreateUserResponse extends BaseRestResponse {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new CreateUserResponse.
	 */
	public CreateUserResponse() {
	}

	/**
	 * Instantiates a new CreateUserResponse.
	 *
	 * @param outcome
	 *            the {@link BaseRestResponse#outcome}
	 */
	public CreateUserResponse(boolean outcome) {
		super(outcome);
	}

	/**
	 * Instantiates a new CreateUserResponse.
	 *
	 * @param errorMessage
	 *            the {@link BaseRestResponse#errorMessage}.
	 */
	public CreateUserResponse(String errorMessage) {
		super(errorMessage);
	}
	
	
	/**
	 * Instantiates a new CreateUserResponse.
	 *
	 * @param errorMessage
	 *            the {@link BaseRestResponse#errorMessage}
	 * @param outcome
	 *            the {@link BaseRestResponse#outcome}
	 */
	public CreateUserResponse(String errorMessage, boolean outcome) {
		this.errorMessage = errorMessage;
		this.outcome = outcome;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CreateUserResponse [outcome=" + outcome + ", errorMessage=" + errorMessage + "]";
	}

}