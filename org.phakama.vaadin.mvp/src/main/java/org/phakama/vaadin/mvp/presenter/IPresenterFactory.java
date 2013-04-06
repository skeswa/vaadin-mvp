package org.phakama.vaadin.mvp.presenter;

import java.io.Serializable;

import org.phakama.vaadin.mvp.view.IView;

public interface IPresenterFactory extends Serializable {
	<T extends IPresenter<? extends IView>> T create(Class<T> presenterClass);
	<T extends IPresenter<? extends IView>> T create(Class<T> presenterClass, IPresenter<? extends IView> parent);
	
	IPresenterRegistry getRegistry();
}
