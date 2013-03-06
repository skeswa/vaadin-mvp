package org.pakhama.vaadin.mvp.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.pakhama.vaadin.mvp.presenter.Presenter;
import org.pakhama.vaadin.mvp.view.View;

class ViewRegistry {
	private Map<View, Presenter<? extends View>> views;
	
	public ViewRegistry() {
		this.views = new ConcurrentHashMap<View, Presenter<? extends View>>();
	}
	
	void register(Presenter<? extends View> presenter) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter cannot be null.");
		}
		
		this.views.put(presenter.getView(), presenter);
	}
	
	void unregister(Presenter<? extends View> presenter) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter cannot be null.");
		}
		
		this.views.remove(presenter.getView());
	}
	
	Presenter<? extends View> getPresenter(View view) {
		if (view == null) {
			throw new IllegalArgumentException("The view parameter cannot be null.");
		}
		
		return this.views.get(view);
	}
}
