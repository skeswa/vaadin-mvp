package org.pakhama.vaadin.mvp.event;

import java.io.Serializable;
import java.util.Collection;

public interface IEventDelegateRegistry extends Serializable {
	void register(IEventHandler handler);
	void unregister(IEventHandler handler);
	void clear();
	
	Collection<IEventDelegate> find(Class<? extends IEvent> eventClass);
	Collection<IEventDelegate> find(Collection<IEventHandler> handlers);
}
