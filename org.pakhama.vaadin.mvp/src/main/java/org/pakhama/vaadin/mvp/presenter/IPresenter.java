package org.pakhama.vaadin.mvp.presenter;

import org.pakhama.vaadin.mvp.event.IEventDispatcher;
import org.pakhama.vaadin.mvp.event.IEventHandler;
import org.pakhama.vaadin.mvp.view.IView;

public interface IPresenter<T extends IView> extends IEventHandler, IEventDispatcher {
	T getView();
	<E extends IPresenter<? extends IView>> E createChild(Class<E> presenterClass);
	
	void onBind();
	void onUnbind();
}
