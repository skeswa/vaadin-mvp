package org.pakhama.vaadin.mvp.presenter;

import java.lang.reflect.ParameterizedType;

import org.pakhama.vaadin.mvp.event.EventBus;
import org.pakhama.vaadin.mvp.exception.InaccessiblePresenterException;
import org.pakhama.vaadin.mvp.exception.InaccessibleViewException;
import org.pakhama.vaadin.mvp.exception.PresenterConstructionException;
import org.pakhama.vaadin.mvp.exception.ViewConstructionException;
import org.pakhama.vaadin.mvp.view.View;

public class PresenterFactory {
	private EventBus eventBus;
	private Presenter<? extends View> parent;
	
	public PresenterFactory(Presenter<? extends View> presenter) {
		if (eventBus == null) {
			throw new IllegalArgumentException("The presenter parameter cannot be null when creating a " + getClass().getSimpleName() + ".");
		}
		
		this.parent = presenter;
		this.eventBus = presenter.getEventBus();
	}
	
	public PresenterFactory(EventBus eventBus) {
		if (eventBus == null) {
			throw new IllegalArgumentException("The eventBus parameter cannot be null when creating a " + getClass().getSimpleName() + ".");
		}
		
		this.eventBus = eventBus;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Presenter<? extends View>> T create(Class<T> presenterClass) {
		if (presenterClass == null) {
			throw new IllegalArgumentException("The presenterClass parameter cannot be null.");
		}
		if (!Presenter.class.isAssignableFrom(presenterClass)) {
			throw new IllegalArgumentException("The presenterClass parameter must be a sub type of " + Presenter.class.getName() + ".");
		}
		
		Class<?> viewClass = PresenterFactory.getTypeOfGeneric(presenterClass);
		if (!View.class.isAssignableFrom(viewClass)) {
			throw new IllegalArgumentException("The viewClass, specified in the generic of the presenterClass parameter, must implement " + View.class.getName() + ".");
		}
		
		Presenter<? extends View> presenterInstance = null;
		try {
			presenterInstance = presenterClass.newInstance();
		} catch (InstantiationException e) {
			throw new PresenterConstructionException("Presenter sub-types must have default constructors.");
		} catch (IllegalAccessException e) {
			throw new InaccessiblePresenterException();
		}
		
		Class<?> viewInstanceClass = presenterInstance.view();
		if (viewInstanceClass == null) {
			throw new PresenterConstructionException("The view() method of this presenter type (" + presenterClass.getName() + ") returned null. It must return the class of the implementation of the presenter's view (" + viewClass + ").");
		}
		if (!viewClass.isAssignableFrom(viewInstanceClass)) {
			throw new PresenterConstructionException("The view() method of this presenter type (" + presenterClass.getName() + ") returned a type that didn't implement this presenter's view (" + viewClass + ").");
		}
		
		View viewInstance = null;
		try {
			viewInstance = (View) viewInstanceClass.newInstance();
		} catch (InstantiationException e) {
			throw new ViewConstructionException("Views must have a default constructor and cannot be abstract.", e);
		} catch (IllegalAccessException e) {
			throw new InaccessibleViewException();
		} catch (ClassCastException e) {
			throw new ViewConstructionException("The provided view of type " + viewInstanceClass.getName() + " could not be cast to " + View.class.getName() + ".", e);
		}
		
		presenterInstance.setEventBus(this.eventBus);
		presenterInstance.setParent(this.parent);
		presenterInstance.setView(viewInstance);
		viewInstance.bind(this.eventBus);
		
		this.eventBus.register(presenterInstance);
		
		viewInstance.init();
		presenterInstance.init();
		
		return (T) presenterInstance;
	}
	
	private static Class<?> getTypeOfGeneric(Class<?> type) {
		return (Class<?>) ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments()[0];
	}
}
