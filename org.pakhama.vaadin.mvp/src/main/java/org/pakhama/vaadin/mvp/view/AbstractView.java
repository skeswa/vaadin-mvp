package org.pakhama.vaadin.mvp.view;

import org.pakhama.vaadin.mvp.event.EventBus;
import org.pakhama.vaadin.mvp.exception.ViewRebindException;

import com.vaadin.ui.VerticalLayout;

public abstract class AbstractView extends VerticalLayout implements View {
	private static final long serialVersionUID = 5552315123806395809L;

	private EventBus eventBus;

	@Override
	public void bind(EventBus eventBus) {
		if (this.eventBus == null) {
			this.eventBus = eventBus;
		} else {
			if (this.eventBus != eventBus) {
				throw new ViewRebindException();
			}
		}
	}

	protected EventBus getEventBus() {
		return eventBus;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append(": { Event Bus: ");
		if (eventBus != null) {
			builder.append(eventBus);
		} else {
			builder.append("null");
		}
		builder.append(" }");
		
		return builder.toString();
	}
}
