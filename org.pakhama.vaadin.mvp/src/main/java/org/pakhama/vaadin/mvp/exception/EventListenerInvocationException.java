package org.pakhama.vaadin.mvp.exception;

import java.util.Collection;

public class EventListenerInvocationException extends RuntimeException {
	private static final long serialVersionUID = 215640467452512354L;

	private String message;
	private Collection<Throwable> causes;
	
	public EventListenerInvocationException(String message, Collection<Throwable> throwables) {
		this.causes = throwables;
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	public Collection<Throwable> getCauses() {
		return causes;
	}
}
