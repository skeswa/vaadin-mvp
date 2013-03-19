package org.pakhama.vaadin.mvp.presenter;

import java.io.Serializable;
import java.util.Collection;

import org.pakhama.vaadin.mvp.view.IView;

public interface IPresenterRegistry extends Serializable {
	void register(IPresenter<? extends IView> presenter, IPresenter<? extends IView> parent, IView view);
	void unregister(IPresenter<? extends IView> presenter);
	int size();
	
	IPresenter<? extends IView> find(IView view);
	
	IPresenter<? extends IView> parentOf(IPresenter<? extends IView> presenter);
	Collection<IPresenter<? extends IView>> siblingsOf(IPresenter<? extends IView> presenter);
	Collection<IPresenter<? extends IView>> childrenOf(IPresenter<? extends IView> presenter);
}
