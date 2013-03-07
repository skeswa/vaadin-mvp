package org.pakhama.vaadin.mvp.event;

import java.util.HashMap;
import java.util.Map;

import org.pakhama.vaadin.mvp.presenter.Presenter;
import org.pakhama.vaadin.mvp.view.IView;

class ViewRegistry {
	private Map<IView, Presenter<? extends IView>> views;
	
	public ViewRegistry() {
		this.views = new HashMap<IView, Presenter<? extends IView>>();
	}
	
	void register(Presenter<? extends IView> presenter) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter cannot be null.");
		}
		
		this.views.put(presenter.getView(), presenter);
	}
	
	void unregister(Presenter<? extends IView> presenter) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter cannot be null.");
		}
		
		this.views.remove(presenter.getView());
	}
	
	Presenter<? extends IView> getPresenter(IView view) {
		if (view == null) {
			throw new IllegalArgumentException("The view parameter cannot be null.");
		}
		
		return this.views.get(view);
	}
}
