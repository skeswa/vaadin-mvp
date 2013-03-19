package org.pakhama.vaadin.mvp.event.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.pakhama.vaadin.mvp.event.EventScope;
import org.pakhama.vaadin.mvp.event.IEvent;
import org.pakhama.vaadin.mvp.event.IEventBus;
import org.pakhama.vaadin.mvp.event.IEventDelegate;
import org.pakhama.vaadin.mvp.event.IEventDelegateRegistry;
import org.pakhama.vaadin.mvp.event.IEventDispatcher;
import org.pakhama.vaadin.mvp.event.IEventHandler;
import org.pakhama.vaadin.mvp.exception.EventListenerInvocationException;
import org.pakhama.vaadin.mvp.exception.EventPropagationException;
import org.pakhama.vaadin.mvp.presenter.IPresenter;
import org.pakhama.vaadin.mvp.presenter.IPresenterRegistry;
import org.pakhama.vaadin.mvp.view.IView;

public class EventBus implements IEventBus {
	private static final long serialVersionUID = -5989527350073214759L;

	private IEventDelegateRegistry delegateRegistry;
	private IPresenterRegistry presenterRegistry;

	public EventBus(IEventDelegateRegistry delegateRegistry, IPresenterRegistry presenterRegistry) {
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
		case SIBLING:
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
				Collection<IPresenter<?>> siblings = this.presenterRegistry.siblingsOf((IPresenter<?>) dispatcher);
				if (siblings != null) {
					delegates = this.delegateRegistry.find(event.getClass(), new ArrayList<IEventHandler>(siblings));
				}
			} else if (dispatcher instanceof IView) {
				IPresenter<?> parent = this.presenterRegistry.find((IView) dispatcher);
				if (parent != null) {
					LinkedList<IEventHandler> parentWrapper = new LinkedList<IEventHandler>();
					delegates = this.delegateRegistry.find(event.getClass(), parentWrapper);
				}
			} else {
				throw new EventPropagationException("EventScope.SIBLINGS is only applicable for event handlers of type IPresenter.");
			}
			break;
		case UNIVERAL:
			delegates = this.delegateRegistry.find(event.getClass());
			break;
		}

		if (delegates != null) {
			EventListenerInvocationException ex = new EventListenerInvocationException("There were event handlers that were not invoked. ", new ArrayList<Throwable>());
			for (IEventDelegate delegate : delegates) {
				try {
					invokeDelegate(delegate, event);
				} catch (IllegalArgumentException e) {
					ex.getCauses().add(e);
				} catch (IllegalAccessException e) {
					ex.getCauses().add(e);
				} catch (InvocationTargetException e) {
					ex.getCauses().add(e);
				}
			}
			
			if (ex.getCauses().size() != 0) {
				throw ex;
			}
		}
	}

	@Override
	public IEventDelegateRegistry getRegistry() {
		return this.getRegistry();
	}

	private void invokeDelegate(IEventDelegate delegate, IEvent event) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (delegate.getMethod().getParameterTypes().length == 0) {
			delegate.getMethod().invoke(delegate.getHandler());
		} else if (delegate.getMethod().getParameterTypes().length == 1) {
			delegate.getMethod().invoke(delegate.getHandler(), event);
		} else {
			throw new IllegalArgumentException("The event handler method " + delegate.getMethod() + " had more than one argument. Event handler methods must specify either no arguments, or one argument which shares the event of its @EventListener annotation.");
		}
	}
}
