package org.pakhama.vaadin.mvp.exception;

public class ViewRebindException extends RuntimeException {
	private static final long serialVersionUID = -180214752321018987L;

	@Override
	public String getMessage() {
		return "A view cannot be re-bound to another event bus if it has been bound to one already.";
	}
}
