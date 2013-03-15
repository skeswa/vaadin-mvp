package org.pakhama.vaadin.mvp.event;

import java.io.Serializable;
import java.lang.reflect.Method;

public interface IEventDelegate extends Serializable {
	Method getMethod();
	Class<? extends IEvent> getEventType();
	IEventHandler getHandler();
	
	void kill();
}
