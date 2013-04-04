package org.pakhama.vaadin.mvp.event;

import java.io.Serializable;

public interface IEventDispatcher extends Serializable {
	int dispatch(IEvent event);
}
