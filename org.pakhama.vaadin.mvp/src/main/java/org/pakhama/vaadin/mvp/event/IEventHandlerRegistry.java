package org.pakhama.vaadin.mvp.event;

import java.util.Collection;

public interface IEventHandlerRegistry {
	void register(IEventHandler handler);
	void unregister(IEventHandler handler);
	void clear();
	
	Collection<IEventDelegate> find(Class<? extends IEvent> eventClass);
}
