package org.pakhama.vaadin.mvp.event;

import org.pakhama.vaadin.mvp.presenter.Presenter;
import org.pakhama.vaadin.mvp.view.IView;

public interface IEventBus {
	void register(Presenter<? extends IView> presenter);
	void unregister(Presenter<? extends IView> presenter);
	
	void fire(Event e);
}
