package org.pakhama.vaadin.mvp.view;

import org.pakhama.vaadin.mvp.event.IEventBus;
import org.pakhama.vaadin.mvp.presenter.Presenter;

import com.vaadin.ui.VerticalLayout;

public abstract class View extends VerticalLayout implements IView {
	private static final long serialVersionUID = 5552315123806395809L;

	private IEventBus eventBus;
	private Presenter<? extends IView> presenter;
	
	void setPresenter(Presenter<? extends IView> presenter) {
		if (this.presenter == null) {
			this.presenter = presenter;
		}
	}

	void setEventBus(IEventBus eventBus) {
		if (this.eventBus == null) {
			this.eventBus = eventBus;
		}
	}
	
	protected void fire(org.pakhama.vaadin.mvp.event.Event e) {
		this.eventBus.fire(this, e);
	}
	
	@Override
	public Object getOwner() {
		return presenter;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(':');
		builder.append('{');
		if (presenter == null) {
			builder.append("<null presenter>, ");
		} else {
			builder.append(presenter.getClass().getName());
			builder.append(", ");
		}
		if (eventBus == null) {
			builder.append("<null event bus>");
		} else {
			builder.append(eventBus.getClass().getName());
		}
		builder.append('}');
		
		return builder.toString();
	}
}
