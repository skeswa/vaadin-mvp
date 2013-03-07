package org.pakhama.vaadin.mvp.event;

public class EventBusFactory {
	public IEventBus create() {
		return new EventBusImpl();
	}
	
}
