package org.pakhama.vaadin.mvp.view.impl;

import org.pakhama.vaadin.mvp.annotation.field.EventBus;
import org.pakhama.vaadin.mvp.event.EventScope;
import org.pakhama.vaadin.mvp.event.IEvent;
import org.pakhama.vaadin.mvp.event.IEventBus;
import org.pakhama.vaadin.mvp.view.IView;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class View extends VerticalLayout implements IView {
	private static final long serialVersionUID = 5552315123806395809L;

	@EventBus
	private IEventBus eventBus;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(':');
		builder.append('{');
		if (eventBus == null) {
			builder.append("<null event bus>");
		} else {
			builder.append(eventBus.getClass().getName());
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

	public void onBind() {
		// Does nothing by default; intended to be overridden
		// Overriding this is intended to be optional
	}

	public void onUnbind() {
		// Get rid of instance variables
		this.eventBus = null;
		// Suicide as Vaadin component
		detach();
		// Request that this class be garbage collected
		try {
			finalize();
		} catch (Throwable e) {
			// Doesn't matter if finalize failed
		}
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public int hashCode() {
		int result = 277 + getClass().getName().hashCode();
		result = (31 * result);
		if (eventBus != null) {
			result += eventBus.hashCode();
		}
		result = (31 * result);
		result += getComponentCount();
		result = (31 * result);

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof View)) {
			return false;
		}

		return obj.hashCode() == hashCode();
	}
}
