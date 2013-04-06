package org.phakama.vaadin.mvp.event;

import java.io.Serializable;

public interface IUniversalEventBus extends Serializable {
	int propagate(IEvent event, IEventBus origin);

	void register(IEventBus eventBus);

	void unregister(IEventBus eventBus);
}
