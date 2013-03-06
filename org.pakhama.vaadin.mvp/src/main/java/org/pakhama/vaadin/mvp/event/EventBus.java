package org.pakhama.vaadin.mvp.event;

import org.pakhama.vaadin.mvp.presenter.Presenter;
import org.pakhama.vaadin.mvp.view.View;

public interface EventBus {
	void register(Presenter<? extends View> presenter);
	void unregister(Presenter<? extends View> presenter);
	
	Presenter<? extends View> getPresenter(View view);
	
	void fire(Event e);
}
