package org.pakhama.vaadin.mvp.event.two;

import org.pakhama.vaadin.mvp.event.IEvent;
import org.pakhama.vaadin.mvp.event.IEventDispatcher;

public class TwoTestEvent implements IEvent {
	private static final long serialVersionUID = 1L;
	
	private IEventDispatcher source;

	@Override
	public IEventDispatcher getSource() {
		return this.source;
	}

	@Override
	public void setSource(IEventDispatcher source) {
		this.source = source;
	}

}
