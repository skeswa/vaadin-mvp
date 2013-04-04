package org.pakhama.vaadin.mvp.event;

public interface IScopedEventDispatcher extends IEventDispatcher {
	int dispatch(IEvent event, EventScope scope);
}
