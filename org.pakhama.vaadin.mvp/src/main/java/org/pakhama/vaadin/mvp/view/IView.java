package org.pakhama.vaadin.mvp.view;

import org.pakhama.vaadin.mvp.event.IEventDispatcher;

import com.vaadin.ui.Component;

public interface IView extends IEventDispatcher {
	Component getComponent();
	void onBind();
	void onUnbind();
}
