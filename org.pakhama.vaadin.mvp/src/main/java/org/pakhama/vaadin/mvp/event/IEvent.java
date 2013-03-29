package org.pakhama.vaadin.mvp.event;

import java.io.Serializable;

public interface IEvent extends Serializable {
	boolean isForeign();
	void markForeign();
	IEventDispatcher getSource();
	void setSource(IEventDispatcher source);
	IEvent copy();
}
