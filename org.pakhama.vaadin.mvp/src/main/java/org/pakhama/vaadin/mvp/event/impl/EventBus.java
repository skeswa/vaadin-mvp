package org.pakhama.vaadin.mvp.event.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.pakhama.vaadin.mvp.event.EventScope;
import org.pakhama.vaadin.mvp.event.IEvent;
import org.pakhama.vaadin.mvp.event.IEventBus;
import org.pakhama.vaadin.mvp.event.IEventDelegate;
import org.pakhama.vaadin.mvp.event.IEventDispatcher;
import org.pakhama.vaadin.mvp.event.IEventHandler;
import org.pakhama.vaadin.mvp.event.IEventHandlerRegistry;
import org.pakhama.vaadin.mvp.exception.EventListenerInvocationException;
import org.pakhama.vaadin.mvp.exception.EventPropagationException;
import org.pakhama.vaadin.mvp.presenter.IPresenter;
import org.pakhama.vaadin.mvp.presenter.IPresenterRegistry;
import org.pakhama.vaadin.mvp.view.IView;

public class EventBus implements IEventBus {
	private static final long serialVersionUID = -5989527350073214759L;

	private IEventHandlerRegistry delegateRegistry;
	private IPresenterRegistry presenterRegistry;

	public EventBus(IEventHandlerRegistry delegateRegistry, IPresenterRegistry presenterRegistry) {
		if (delegateRegistry == null) {
			throw new IllegalArgumentException("The delegateRegistry parameter cannot be null.");
		}
		if (presenterRegistry == null) {
			throw new IllegalArgumentException("The presenterRegistry parameter cannot be null.");
		}

		this.delegateRegistry = delegateRegistry;
		this.presenterRegistry = presenterRegistry;
	}

	@Override
	public void propagate(IEvent event, IEventDispatcher dispatcher, EventScope scope) {
		if (event == null) {
			throw new IllegalArgumentException("The event parameter cannot be null.");
		}
		if (dispatcher == null) {
			throw new IllegalArgumentException("The dispatcher parameter cannot be null.");
		}
		if (scope == null) {
			throw new IllegalArgumentException("The registry parameter cannot be null.");
		}

		Collection<IEventDelegate> delegates = null;
		// Set the source of the event
		event.setSource(dispatcher);
		// Choose propagation strategy
		switch (scope) {
		case CHILDREN:
			// This scope is only applicable for dispatchers of type IPresenter
			if (dispatcher instanceof IPresenter<?>) {
				Collection<IPresenter<?>> children = this.presenterRegistry.childrenOf((IPresenter<?>) dispatcher);
				if (children != null) {
					delegates = this.delegateRegistry.find(event.getClass(), new ArrayList<IEventHandler>(children));
				}
			} else {
				throw new EventPropagationException("EventScope.CHILDREN is only applicable for event handlers of type IPresenter.");
			}
			break;
		case SIBLINGS:
			// This scope is only applicable for dispatchers of type IPresenter
			if (dispatcher instanceof IPresenter<?>) {
				Collection<IPresenter<?>> siblings = this.presenterRegistry.siblingsOf((IPresenter<?>) dispatcher);
				if (siblings != null) {
					delegates = this.delegateRegistry.find(event.getClass(), new ArrayList<IEventHandler>(siblings));
				}
			} else {
				throw new EventPropagationException("EventScope.SIBLINGS is only applicable for event handlers of type IPresenter.");
			}
			break;
		case PARENT:
			// This scope is only applicable for dispatchers of type IPresenter
			// OR IView
			if (dispatcher instanceof IPresenter<?>) {
				IPresenter<?> parent = this.presenterRegistry.parentOf((IPresenter<?>) dispatcher);
				if (parent != null) {
					// Since the find method only takes collections, we need to wrap the parent
					ArrayList<IEventHandler> parentWrapper = new ArrayList<IEventHandler>(1);
					parentWrapper.add(parent);
					delegates = this.delegateRegistry.find(event.getClass(), parentWrapper);
				}
			} else if (dispatcher instanceof IView) {
				IPresenter<?> parent = this.presenterRegistry.find((IView) dispatcher);
				if (parent != null) {
					// Since the find method only takes collections, we need to wrap the parent
					ArrayList<IEventHandler> parentWrapper = new ArrayList<IEventHandler>(1);
					parentWrapper.add(parent);
					delegates = this.delegateRegistry.find(event.getClass(), parentWrapper);
				}
			} else {
				throw new EventPropagationException("EventScope.PARENT is only applicable for event handlers of type IPresenter or IView.");
			}
			break;
		case UNIVERAL:
			delegates = this.delegateRegistry.find(event.getClass());
			break;
		}

		if (delegates != null) {
			for (IEventDelegate delegate : delegates) {
				try {
					delegate.invoke(event);
				} catch (IllegalArgumentException e) {
					throw new EventListenerInvocationException("The parameter requirements of an @EventListener could not be satisfied. The delegate that failed to invoke was " + delegate + ".", e);
				} catch (IllegalAccessException e) {
					throw new EventListenerInvocationException("The an @EventListener could not be accessed. The delegate that failed to invoke was " + delegate + ".", e);
				} catch (InvocationTargetException e) {
					throw new EventListenerInvocationException("There was an exception inside the body of an @EventListener method. The delegate that failed to invoke was " + delegate + ".", e);
				}
			}
		}
	}

	@Override
	public IEventHandlerRegistry getRegistry() {
		return this.delegateRegistry;
	}

}
