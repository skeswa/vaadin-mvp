package org.pakhama.vaadin.mvp.presenter;

import org.pakhama.vaadin.mvp.view.IView;

public interface IPresenterFactory {
	<T extends IPresenter<? extends IView>> T create(Class<T> presenterClass);
}
