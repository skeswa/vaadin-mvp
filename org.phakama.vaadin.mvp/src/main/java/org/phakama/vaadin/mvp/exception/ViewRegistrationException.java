package org.phakama.vaadin.mvp.exception;

public class ViewRegistrationException extends RuntimeException {
	private static final long serialVersionUID = -655555238520780989L;
	
	private final String message;

	public ViewRegistrationException(String message) {
		this.message = message;
	}
	
	public ViewRegistrationException(String message, Throwable cause) {
		initCause(cause);
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
