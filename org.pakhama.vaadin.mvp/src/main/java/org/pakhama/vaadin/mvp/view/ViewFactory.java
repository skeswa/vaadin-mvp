package org.pakhama.vaadin.mvp.view;

import org.pakhama.vaadin.mvp.exception.InaccessibleViewException;
import org.pakhama.vaadin.mvp.exception.ViewConstructionException;
import org.pakhama.vaadin.mvp.presenter.Presenter;

public class ViewFactory {
	
	public IView create(Presenter<? extends IView> presenter, Class<? extends IView> viewType) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter paramter must not be null.");
		}
		if (viewType == null) {
			throw new IllegalArgumentException("The viewType paramter must not be null.");
		}
		
		IView viewInstance = null;
		try {
			viewInstance = (IView) viewType.newInstance();
		} catch (InstantiationException e) {
			throw new ViewConstructionException("Views must have a default constructor and cannot be abstract.", e);
		} catch (IllegalAccessException e) {
			throw new InaccessibleViewException();
		} catch (ClassCastException e) {
			throw new ViewConstructionException("The provided view of type " + viewType.getName() + " could not be cast to " + IView.class.getName() + ".", e);
		}
		
		if (viewInstance instanceof View) {
			((View) viewInstance).setEventBus(presenter.getEventBus());
			((View) viewInstance).setPresenter(presenter);
		}
		
		return viewInstance;
	}
}
