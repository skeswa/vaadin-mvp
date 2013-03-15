package org.pakhama.vaadin.mvp.event.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.pakhama.vaadin.mvp.annotation.field.EventDispatcherField;
import org.pakhama.vaadin.mvp.event.EventScope;
import org.pakhama.vaadin.mvp.event.IEvent;
import org.pakhama.vaadin.mvp.event.IEventBus;
import org.pakhama.vaadin.mvp.event.IEventDelegate;
import org.pakhama.vaadin.mvp.event.IEventDelegateRegistry;
import org.pakhama.vaadin.mvp.event.IEventDispatcher;
import org.pakhama.vaadin.mvp.event.IEventHandler;
import org.pakhama.vaadin.mvp.exception.EventPropagationException;
import org.pakhama.vaadin.mvp.exception.FieldInjectionException;
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

		try {
			injectField(EventDispatcherField.class, event, dispatcher);
		} catch (FieldInjectionException e) {
			// It is the developer's fault if the dispatcher field of the event
			// cannot be injected
		}

		Collection<IEventDelegate> delegates = null;
		switch (scope) {
		case CHILDREN:
			// This scope is only applicable for dispatchers of type IPresenter
			if (dispatcher instanceof IPresenter<?>) {
				Collection<IPresenter<?>> children = this.presenterRegistry.childrenOf((IPresenter<?>) dispatcher);
				if (children != null) {
					delegates = this.delegateRegistry.find(new ArrayList<IEventHandler>(children));
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
					delegates = this.delegateRegistry.find(new ArrayList<IEventHandler>(siblings));
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
					delegates = this.delegateRegistry.find(new ArrayList<IEventHandler>(siblings));
				}
			} else if (dispatcher instanceof IView) {
				IPresenter<?> parent = this.presenterRegistry.find((IView) dispatcher);
				if (parent != null) {
					LinkedList<IEventHandler> parentWrapper = new LinkedList<IEventHandler>();
					delegates = this.delegateRegistry.find(parentWrapper);
				}
			} else {
				throw new EventPropagationException("EventScope.SIBLINGS is only applicable for event handlers of type IPresenter.");
			}
			break;
		case UNIVERAL:
			break;
		}
	}

	@Override
	public IEventDelegateRegistry getRegistry() {
		return this.getRegistry();
	}

	private void injectField(Class<? extends Annotation> annotationType, Object instance, Object fieldValue) {
		if (annotationType == null) {
			throw new IllegalArgumentException("The annotationType parameter cannot be null.");
		}
		if (instance == null) {
			throw new IllegalArgumentException("The instance parameter cannot be null.");
		}
		if (fieldValue == null) {
			throw new IllegalArgumentException("The fieldValue parameter cannot be null.");
		}

		Class<?> targetClass = instance.getClass();
		while (!Object.class.equals(targetClass)) {
			Field[] fields = targetClass.getDeclaredFields();
			for (Field field : fields) {
				if (field.getAnnotation(annotationType) != null) {
					try {
						field.setAccessible(true);
						field.set(instance, fieldValue);
					} catch (SecurityException e) {
						throw new FieldInjectionException("Could not inject " + fieldValue + " into " + field + ". The field was inaccessible.", e);
					} catch (IllegalArgumentException e) {
						throw new FieldInjectionException("Could not inject " + fieldValue + " into " + field + ". The intended field value was incompatible with the field.",
								e);
					} catch (IllegalAccessException e) {
						throw new FieldInjectionException("Could not inject " + fieldValue + " into " + field + ". The field was inaccessible.", e);
					}
				}
			}
			targetClass = targetClass.getSuperclass();
		}

		throw new FieldInjectionException("No field in the class " + instance.getClass() + " was marked with the " + annotationType
				+ " annotation. A field with this annotation is a requirement for injection to be successful.");
	}

	// private EventHandlerRegistry eventRegistry;
	//
	// private EventHandlerRegistry provideEventRegistry() {
	// if (this.eventRegistry == null) {
	// this.eventRegistry = new EventHandlerRegistry();
	// }
	//
	// return this.eventRegistry;
	// }
	//
	// @Override
	// public void register(IPresenter<? extends IView> presenter) {
	// if (presenter == null) {
	// throw new
	// IllegalArgumentException("The presenter parameter cannot be null.");
	// }
	//
	// Method[] methods = presenter.getClass().getMethods();
	// EventListener listener = null;
	// for (Method method : methods) {
	// if (method != null) {
	// if ((listener = method.getAnnotation(EventListener.class)) != null) {
	// if (listener.event() == null) {
	// throw new EventListenerRegistrationException("Listener method " + method
	// +
	// " could not be registered because the event parameter of the @Listener annotation was null.");
	// }
	//
	// Class<?>[] paramTypes = method.getParameterTypes();
	// if (paramTypes.length > 1) {
	// throw new EventListenerRegistrationException("Listener method " + method
	// +
	// " could not be registered because an event listener method can only have its own Event, or nothing, as a parameter.");
	// } else if ((paramTypes.length == 1) &&
	// !listener.event().isAssignableFrom(paramTypes[0])) {
	// throw new EventListenerRegistrationException("Listener method " + method
	// +
	// " could not be registered because its only parameter did not match the event value of the @Listener parameter.");
	// }
	//
	// provideEventRegistry().register(presenter, method, listener.event());
	// }
	// }
	// }
	// }
	//
	// @Override
	// public void unregister(IPresenter<? extends IView> presenter) {
	// if (presenter == null) {
	// throw new
	// IllegalArgumentException("The presenter parameter must not be null.");
	// }
	//
	// provideEventRegistry().unregister(presenter);
	// }
	//
	// @Override
	// public void fire(Object source, Event e) {
	// if (source == null) {
	// throw new
	// IllegalArgumentException("The source parameter cannot be null.");
	// }
	// if (e == null) {
	// throw new
	// IllegalArgumentException("The event parameter cannot be null.");
	// }
	//
	// e.setSource(source);
	// switch (e.getScope()) {
	// case CHILDREN:
	// if (source instanceof Presenter<?>) {
	// try {
	// provideEventRegistry().invokeChildren(e);
	// } catch (Throwable t) {
	// throw new EventListenerFaultException(t, e);
	// }
	// break;
	// }
	// break;
	// case SIBLING:
	// if (source instanceof Presenter<?>) {
	// IPresenter<?> parent = ((Presenter<?>) source).getParent();
	// if (parent != null) {
	// try {
	// provideEventRegistry().invokeSiblings(e, parent);
	// } catch (Throwable t) {
	// throw new EventListenerFaultException(t, e);
	// }
	// break;
	// }
	// }
	// break;
	// case PARENT:
	// if (source instanceof Presenter<?>) {
	// IPresenter<?> parent = ((Presenter<?>) source).getParent();
	// if (parent != null) {
	// try {
	// provideEventRegistry().invokeParent(e, parent);
	// } catch (Throwable t) {
	// throw new EventListenerFaultException(t, e);
	// }
	// break;
	// }
	// } else if (source instanceof IView) {
	// Object parent = ((IView) source).getOwner();
	// if (parent != null) {
	// try {
	// provideEventRegistry().invokeParent(e, parent);
	// } catch (Throwable t) {
	// throw new EventListenerFaultException(t, e);
	// }
	// break;
	// }
	// }
	// break;
	// case ALL:
	// default:
	// try {
	// provideEventRegistry().invokeAll(e);
	// } catch (Throwable t) {
	// throw new EventListenerFaultException(t, e);
	// }
	// break;
	// }
	// }
	//
	// @Override
	// public void fire(Object source, EventScope scope, Event e) {
	// if (scope == null) {
	// throw new
	// IllegalArgumentException("The scope parameter cannot be null.");
	// }
	//
	// e.setScope(scope);
	// fire(source, e);
	// }

}
