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
	
	protected Presenter<? extends IView> getPresenter() {
		return presenter;
	}

	void setEventBus(IEventBus eventBus) {
		if (this.eventBus == null) {
			this.eventBus = eventBus;
		}
	}

	protected IEventBus getEventBus() {
		return eventBus;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
