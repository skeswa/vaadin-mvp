package org.pakhama.vaadin.mvp.ui;

import org.pakhama.vaadin.mvp.event.IEventBus;
import org.pakhama.vaadin.mvp.event.IEventHandlerRegistry;
import org.pakhama.vaadin.mvp.event.impl.EventBus;
import org.pakhama.vaadin.mvp.event.impl.EventHandlerRegistry;
import org.pakhama.vaadin.mvp.presenter.IPresenter;
import org.pakhama.vaadin.mvp.presenter.IPresenterFactory;
import org.pakhama.vaadin.mvp.presenter.IPresenterRegistry;
import org.pakhama.vaadin.mvp.presenter.impl.PresenterFactory;
import org.pakhama.vaadin.mvp.presenter.impl.registry.PresenterRegistry;
import org.pakhama.vaadin.mvp.view.IView;
import org.pakhama.vaadin.mvp.view.IViewFactory;
import org.pakhama.vaadin.mvp.view.IViewRegistry;
import org.pakhama.vaadin.mvp.view.impl.ViewFactory;
import org.pakhama.vaadin.mvp.view.impl.ViewRegistry;

import com.vaadin.ui.UI;

public abstract class IMVPUI extends UI {
	private static final long serialVersionUID = -915879429646798067L;
	
	private final IViewRegistry viewRegistry;
	private final IViewFactory viewFactory;
	private final IPresenterRegistry presenterRegistry;
	private final IEventHandlerRegistry eventHandlerRegistry;
	private final IEventBus eventBus;
	private final IPresenterFactory presenterFactory;
	
	public IMVPUI() {
		this.viewRegistry = new ViewRegistry();
		this.viewFactory = new ViewFactory(this.viewRegistry);
		this.presenterRegistry = new PresenterRegistry();
		this.eventHandlerRegistry = new EventHandlerRegistry();
		this.eventBus = new EventBus(this.eventHandlerRegistry, this.presenterRegistry);
		this.presenterFactory = new PresenterFactory(this.eventBus, this.viewFactory, this.presenterRegistry);
	}
	
	public <T extends IPresenter<? extends IView>> T createPresenter(Class<T> presenterClass) {
		return this.presenterFactory.create(presenterClass);
	}
	
	public IEventBus getEventBus() {
		return this.eventBus;
	}
	
	IViewRegistry getViewRegistry() {
		return this.viewRegistry;
	}
	
	IPresenterRegistry getPresenterRegistry() {
		return this.presenterRegistry;
	}
}