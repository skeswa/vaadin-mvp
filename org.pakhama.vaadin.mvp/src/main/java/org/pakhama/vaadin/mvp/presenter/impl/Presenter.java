package org.pakhama.vaadin.mvp.presenter.impl;

import org.pakhama.vaadin.mvp.annotation.field.EventBusField;
import org.pakhama.vaadin.mvp.annotation.field.PresenterFactoryField;
import org.pakhama.vaadin.mvp.annotation.field.ViewField;
import org.pakhama.vaadin.mvp.event.EventScope;
import org.pakhama.vaadin.mvp.event.IEvent;
import org.pakhama.vaadin.mvp.event.IEventBus;
import org.pakhama.vaadin.mvp.presenter.IPresenter;
import org.pakhama.vaadin.mvp.presenter.IPresenterFactory;
import org.pakhama.vaadin.mvp.view.IView;

public class Presenter<T extends IView> implements IPresenter<T> {
	private static final long serialVersionUID = 5131211825391491296L;

	@ViewField
	private T view;
	@EventBusField
	private IEventBus eventBus;
	@PresenterFactoryField
	private IPresenterFactory factory;

	@Override
	public T getView() {
		return view;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(':');
		builder.append('{');
		if (view == null) {
			builder.append("<null view>, ");
		} else {
			builder.append(view.getClass().getSimpleName());
			builder.append(", ");
		}
		if (eventBus == null) {
			builder.append("<null event bus>");
		} else {
			builder.append(eventBus.getClass().getSimpleName());
		}
		builder.append('}');

		return builder.toString();
	}

	@Override
	public void dispatch(IEvent event) {
		if (event == null) {
			throw new IllegalArgumentException("The event parameter was null.");
		}
		this.eventBus.propagate(event, this, EventScope.UNIVERAL);
	}

	@Override
	public void dispatch(IEvent event, EventScope scope) {
		if (event == null) {
			throw new IllegalArgumentException("The event parameter was null.");
		}
		if (scope == null) {
			throw new IllegalArgumentException("The scope parameter was null.");
		}

		this.eventBus.propagate(event, this, scope);
	}
	
	@Override
	public <E extends IPresenter<? extends IView>> E createChild(Class<E> presenterClass) {
		E child = this.factory.create(presenterClass, this);
		return child;
	}

	@Override
	public void onBind() {
		// Does nothing by default, intended to be overridden
	}

	@Override
	public void onUnbind() {
		this.eventBus = null;
		this.factory = null;
		this.view = null;

		try {
			finalize();
		} catch (Throwable e) {
			// Doesn't matter if finalize failed, just called to mark this class
			// as garbage
		}
	}
	
	@Override
	public int hashCode() {
		int result = 119 + getClass().getName().hashCode();
		result = (31 * result);
		if (view != null) {
			result += view.hashCode();
		}
		result = (31 * result);
		if (eventBus != null) {
			result += eventBus.hashCode();
		}
		result = (31 * result);
		if (factory != null) {
			result += factory.hashCode();
		}
		result = (31 * result);
		
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} 
		if (!(obj instanceof Presenter)) {
			return false;
		}
		
		return obj.hashCode() == hashCode();
	}
}
