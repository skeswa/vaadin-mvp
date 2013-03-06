package org.pakhama.vaadin.mvp.exception;

import org.pakhama.vaadin.mvp.event.Event;

public class EventListenerFaultException extends RuntimeException {
	private static final long serialVersionUID = -5085716498192847755L;

	private Event event;
	
	public EventListenerFaultException(Throwable cause, Event event) {
		initCause(cause);
		this.event = event;
	}
	
	@Override
	public String getMessage() {
		return "An exception occurred while executing event " + event + ".";
	}
}
