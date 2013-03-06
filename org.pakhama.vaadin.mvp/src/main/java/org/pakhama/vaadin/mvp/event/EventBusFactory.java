package org.pakhama.vaadin.mvp.event;

public class EventBusFactory {
	public EventBus create() {
		return new EventBusImpl();
	}
	
}
