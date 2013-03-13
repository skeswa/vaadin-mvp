package org.pakhama.vaadin.mvp.event;

import java.io.Serializable;

public interface IEventDispatcher extends Serializable {
	void dispatch(IEvent event);
	void dispatch(IEvent event, EventScope scope);
}
