package org.pakhama.vaadin.mvp.event;

import java.io.Serializable;

public interface IEventBus extends Serializable {
	void propagate(IEvent event, IEventDispatcher dispatcher, EventScope scope);
	
	IEventHandlerRegistry getRegistry();
}
