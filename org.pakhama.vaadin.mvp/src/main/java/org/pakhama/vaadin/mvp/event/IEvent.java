package org.pakhama.vaadin.mvp.event;

import java.io.Serializable;

public interface IEvent extends Serializable {
	IEventDispatcher getSource();
	void setSource(IEventDispatcher source);
}
