package org.phakama.vaadin.mvp.event;

import java.io.Serializable;

public interface IEvent extends Serializable, Cloneable {
	boolean isForeign();
	void markForeign();
	IEventDispatcher getSource();
	void setSource(IEventDispatcher source);
	IEvent duplicate();
}
