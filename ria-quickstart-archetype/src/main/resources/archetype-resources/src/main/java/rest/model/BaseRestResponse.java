package ${package}.rest.model;

import java.io.Serializable;

/**
 * Base response for the REST services of this application.
 * 
 * @author Donato Rimenti
 */
public class BaseRestResponse implements Serializable {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The outcome of the operation.
	 */
	protected boolean outcome;

	/**
	 * The error message.
	 */
	protected String errorMessage;

	/**
	 * Instantiates a new BaseRestResponse.
	 */
	public BaseRestResponse() {
	}

	/**
	 * Instantiates a new BaseRestResponse.
	 *
	 * @param outcome
	 *            the {@link #outcome}
	 */
	public BaseRestResponse(boolean outcome) {
		this.outcome = outcome;
	}

	/**
	 * Instantiates a new BaseRestResponse.
	 *
	 * @param errorMessage
	 *            the {@link #errorMessage}
	 */
	public BaseRestResponse(String errorMessage) {
		this.outcome = false;
		this.errorMessage = errorMessage;
	}

	/**
	 * Instantiates a new BaseRestResponse.
	 *
	 * @param outcome
	 *            the {@link #outcome}
	 * @param errorMessage
	 *            the {@link #errorMessage}
	 */
	public BaseRestResponse(boolean outcome, String errorMessage) {
		this.outcome = outcome;
		this.errorMessage = errorMessage;
	}

	/**
	 * Checks if is outcome.
	 *
	 * @return true, if everything is ok
	 */
	public boolean isOutcome() {
		return outcome;
	}

	/**
	 * Sets the {@link #outcome}.
	 *
	 * @param outcome
	 *            the new {@link #outcome}
	 */
	public void setOutcome(boolean outcome) {
		this.outcome = outcome;
	}
	
	/**
	 * Gets the {@link #errorMessage}.
	 *
	 * @return the {@link #errorMessage}
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Sets the {@link #errorMessage}.
	 *
	 * @param errorMessage
	 *            the new {@link #errorMessage}
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result + (outcome ? 1231 : 1237);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseRestResponse other = (BaseRestResponse) obj;
		if (errorMessage == null) {
			if (other.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(other.errorMessage))
			return false;
		if (outcome != other.outcome)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BaseRestResponse [outcome=" + outcome + ", errorMessage=" + errorMessage + "]";
	}

}
