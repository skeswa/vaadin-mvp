package org.pakhama.vaadin.mvp.event.impl;

import org.pakhama.vaadin.mvp.event.IEvent;
import org.pakhama.vaadin.mvp.event.IEventDispatcher;

public class Event implements IEvent {
	private static final long serialVersionUID = 3610879244521284114L;

	private IEventDispatcher source;
	private boolean foreign = false;

	@Override
	public IEventDispatcher getSource() {
		return this.source;
	}

	@Override
	public void setSource(IEventDispatcher source) {
		if (source != null) {
			this.source = source;
		}
	}

	@Override
	public boolean isForeign() {
		return foreign;
	}

	@Override
	public void markForeign() {
		this.foreign = true;
	}

	@Override
	public IEvent copy() {
		try {
			Event copy = getClass().newInstance();
			copy.source = this.source;
			copy.foreign = this.foreign;
			return copy;
		} catch (Exception e) {
			throw new EventCopyException(e);
		}
	}
	
	private class EventCopyException extends RuntimeException {
		private static final long serialVersionUID = -5820248944626201307L;

		public EventCopyException(Throwable cause) {
			initCause(cause);
		}
		
		@Override
		public String getMessage() {
			return "Could not clone this event.";
		}
	}

}
