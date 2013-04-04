package org.pakhama.vaadin.mvp.event;

import java.io.Serializable;

public interface IEventBus extends Serializable {
	int propagate(IEvent event, EventScope scope);

	void onBind(IUniversalEventBus universalEventBus);
	void onUnbind();

	IEventHandlerRegistry getRegistry();
}
