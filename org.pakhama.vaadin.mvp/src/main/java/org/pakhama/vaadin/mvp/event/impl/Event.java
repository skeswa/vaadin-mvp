package org.pakhama.vaadin.mvp.event.impl;

import org.pakhama.vaadin.mvp.event.IEvent;
import org.pakhama.vaadin.mvp.event.IEventDispatcher;

public class Event implements IEvent {
	private static final long serialVersionUID = 3610879244521284114L;

	private IEventDispatcher source;

	@Override
	public IEventDispatcher getSource() {
		return this.source;
	}

	@Override
	public void setSource(IEventDispatcher source) {
		if (source == null) {
			this.source = source;
		} else {
			throw new UnsupportedOperationException("The source of an event cannot be changed once it is set.");
		}
	}

}
