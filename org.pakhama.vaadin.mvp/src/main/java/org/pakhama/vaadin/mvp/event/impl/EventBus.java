package org.pakhama.vaadin.mvp.event.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.pakhama.vaadin.mvp.event.EventScope;
import org.pakhama.vaadin.mvp.event.IEvent;
import org.pakhama.vaadin.mvp.event.IEventBus;
import org.pakhama.vaadin.mvp.event.IEventDelegate;
import org.pakhama.vaadin.mvp.event.IEventHandler;
import org.pakhama.vaadin.mvp.event.IEventHandlerRegistry;
import org.pakhama.vaadin.mvp.event.IUniversalEventBus;
import org.pakhama.vaadin.mvp.exception.EventListenerInvocationException;
import org.pakhama.vaadin.mvp.exception.EventPropagationException;
import org.pakhama.vaadin.mvp.presenter.IPresenter;
import org.pakhama.vaadin.mvp.presenter.IPresenterRegistry;
import org.pakhama.vaadin.mvp.view.IView;

public class EventBus implements IEventBus {
	private static final long serialVersionUID = -5989527350073214759L;

	private IEventHandlerRegistry delegateRegistry;
	private IPresenterRegistry presenterRegistry;
	private IUniversalEventBus universalEventBus;

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
	public void propagate(IEvent event, EventScope scope) {
		if (event == null) {
			throw new IllegalArgumentException("The event parameter cannot be null.");
		}
		if (event.getSource() == null) {
			throw new IllegalArgumentException("The event source cannot be null.");
		}
		if (scope == null) {
			throw new IllegalArgumentException("The registry parameter cannot be null.");
		}

		Collection<IEventDelegate> delegates = null;
		// Choose propagation strategy
		switch (scope) {
		case CHILDREN:
			// This scope is only applicable for dispatchers of type IPresenter
			if (event.getSource() instanceof IPresenter<?>) {
				Collection<IPresenter<?>> children = this.presenterRegistry.childrenOf((IPresenter<?>) event.getSource());
				if (children != null) {
					delegates = this.delegateRegistry.find(event.getClass(), new ArrayList<IEventHandler>(children));
				}
			} else {
				throw new EventPropagationException("EventScope.CHILDREN is only applicable for event handlers of type IPresenter.");
			}
			break;
		case SIBLINGS:
			// This scope is only applicable for dispatchers of type IPresenter
			if (event.getSource() instanceof IPresenter<?>) {
				Collection<IPresenter<?>> siblings = this.presenterRegistry.siblingsOf((IPresenter<?>) event.getSource());
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
			if (event.getSource() instanceof IPresenter<?>) {
				IPresenter<?> parent = this.presenterRegistry.parentOf((IPresenter<?>) event.getSource());
				if (parent != null) {
					// Since the find method only takes collections, we need to wrap the parent
					ArrayList<IEventHandler> parentWrapper = new ArrayList<IEventHandler>(1);
					parentWrapper.add(parent);
					delegates = this.delegateRegistry.find(event.getClass(), parentWrapper);
				}
			} else if (event.getSource() instanceof IView) {
				IPresenter<?> parent = this.presenterRegistry.find((IView) event.getSource());
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
		case APPLICATION:
			delegates = this.delegateRegistry.find(event.getClass());
			break;
		case UNIVERSAL:
			// Keep in mind, UNIVERSAL just means APPLICATION for this bus
			delegates = this.delegateRegistry.find(event.getClass());
			if (this.universalEventBus != null) {
				this.universalEventBus.propagate(event, this);
			}
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

	@Override
	public void onBind(IUniversalEventBus universalEventBus) {
		this.universalEventBus = universalEventBus;
	}

	@Override
	public void onUnbind() {
		this.universalEventBus = null;
	}

}
