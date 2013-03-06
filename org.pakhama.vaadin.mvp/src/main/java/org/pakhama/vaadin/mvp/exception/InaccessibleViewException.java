package org.pakhama.vaadin.mvp.exception;

public class InaccessibleViewException extends RuntimeException {
	private static final long serialVersionUID = -655555021520780989L;

	@Override
	public String getMessage() {
		return "Views must be accessible by the Presenter Factory.";
	}
}