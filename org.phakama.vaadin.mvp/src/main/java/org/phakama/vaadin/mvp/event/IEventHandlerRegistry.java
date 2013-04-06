package org.phakama.vaadin.mvp.event;

import java.io.Serializable;
import java.util.Collection;

public interface IEventHandlerRegistry extends Serializable {
	void register(IEventHandler handler);
	void unregister(IEventHandler handler);
	void clear();
	/**
	 * Gets the number of currently registered event handlers.
	 * @return the number of currently registered event handlers.
	 */
	int size();
	
	Collection<IEventDelegate> find(Class<? extends IEvent> eventClass);
	Collection<IEventDelegate> find(Class<? extends IEvent> eventClass, Collection<IEventHandler> handlers);
}