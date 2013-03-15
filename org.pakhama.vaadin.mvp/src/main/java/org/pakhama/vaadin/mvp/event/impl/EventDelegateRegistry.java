package org.pakhama.vaadin.mvp.event.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.pakhama.vaadin.mvp.annotation.event.EventListener;
import org.pakhama.vaadin.mvp.event.IEvent;
import org.pakhama.vaadin.mvp.event.IEventDelegate;
import org.pakhama.vaadin.mvp.event.IEventDelegateRegistry;
import org.pakhama.vaadin.mvp.event.IEventHandler;
import org.pakhama.vaadin.mvp.exception.EventListenerRegistrationException;

public class EventDelegateRegistry implements IEventDelegateRegistry {
	private static final long serialVersionUID = -6658219565978880435L;

	// TODO: We need a Map<Class<? extends IEvent>, Map<IEventHandler, Collection<IEventDelegate>>>
	private HashMap<Class<? extends IEvent>, HashSet<IEventDelegate>> eventMap = new HashMap<Class<? extends IEvent>, HashSet<IEventDelegate>>();
	private HashMap<IEventHandler, HashSet<IEventDelegate>> handlerMap = new HashMap<IEventHandler, HashSet<IEventDelegate>>();

	@Override
	public void register(IEventHandler handler) {
		if (handler == null) {
			throw new IllegalArgumentException("The handler parameter cannot be null.");
		}

		processHandler(handler);
	}

	@Override
	public void unregister(IEventHandler handler) {
		if (handler == null) {
			throw new IllegalArgumentException("The handler parameter cannot be null.");
		}
		
		if (this.handlerMap.get(handler) != null) {
			ArrayList<IEventDelegate> clone = new ArrayList<IEventDelegate>(this.handlerMap.get(handler));
			for (IEventDelegate delegate : clone) {
				delegate.kill();
			}
			this.handlerMap.remove(handler);
		}
	}

	@Override
	public void clear() {
		this.eventMap.clear();
	}

	@Override
	public Collection<IEventDelegate> find(Class<? extends IEvent> eventClass) {
		if (eventClass == null) {
			throw new IllegalArgumentException("The eventClass parameter cannot be null.");
		}

		return this.eventMap.get(eventClass);
	}

	@Override
	public Collection<IEventDelegate> find(Collection<IEventHandler> handlers) {
		if (handlers == null) {
			throw new IllegalArgumentException("The handlers parameter cannot be null.");
		}
		// Get the delegates for each handler, and throw them into a list of all the delegates
		ArrayList<IEventDelegate> allDelegates = new ArrayList<IEventDelegate>();
		for (IEventHandler handler : handlers) {
			if (handler != null) {
				Collection<IEventDelegate> delegates = this.handlerMap.get(handler);
				if (delegates != null) {
					allDelegates.addAll(delegates);
				}
			}
		}
		
		return allDelegates;
	}

	@SuppressWarnings("unchecked")
	protected void processHandler(IEventHandler handler) {
		Method[] methods = handler.getClass().getMethods();
		EventListener listenerAnnotation = null;
		for (Method method : methods) {
			if ((listenerAnnotation = method.getAnnotation(EventListener.class)) != null) {
				Class<? extends IEvent> eventType = listenerAnnotation.event();
				HashSet<Class<? extends IEvent>> exclusionSet = convertExcludesArray(listenerAnnotation.excludes());
				if (!isValidEventListenerMethod(eventType, method)) {
					throw new EventListenerRegistrationException(
							"Could not register method "
									+ method
									+ " as an @EventListener listening for "
									+ eventType
									+ ". Methods listenering for events must have either the event they are listening for as a method parameter or no parameters at all. Also, listener methods must be publicly accessible.");
				} else {
					// Is a valid listener method
					Class<? extends IEvent> targetClass = eventType;
					EventDelegate newDelegate = null;
					HashSet<IEventDelegate> delegates = null;
					while (targetClass != null) {
						// Do not consider event type if it is excluded
						if (!exclusionSet.contains(targetClass)) {
							newDelegate = new EventDelegate(method, handler, targetClass);
							// Get or create the set of delegates for this
							// target event type
							delegates = this.eventMap.get(targetClass);
							if (delegates == null) {
								delegates = new HashSet<IEventDelegate>();
								this.eventMap.put(targetClass, delegates);
							}
							// Add new delegate to if it doesn't already exist
							if (!delegates.contains(newDelegate)) {
								newDelegate.addOwner(delegates);
								delegates.add(newDelegate);
							}

							// Get or create the set of delegates for this
							// target handler instance
							delegates = this.handlerMap.get(handler);
							if (delegates == null) {
								delegates = new HashSet<IEventDelegate>();
								this.handlerMap.put(handler, delegates);
							}
							// Add new delegate to if it doesn't already exist
							if (!delegates.contains(newDelegate)) {
								newDelegate.addOwner(delegates);
								delegates.add(newDelegate);
							}
						}

						// Iterate to next applicable event type
						if (IEvent.class.isAssignableFrom(targetClass.getSuperclass()) && !IEvent.class.equals(targetClass.getSuperclass())) {
							targetClass = (Class<? extends IEvent>) targetClass.getSuperclass();
						} else {
							targetClass = null;
						}
					}
				}
			}
		}
	}

	private boolean isValidEventListenerMethod(Class<? extends IEvent> eventType, Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length > 1) {
			return false;
		}
		if (parameterTypes.length == 1 && !eventType.isAssignableFrom(parameterTypes[0])) {
			return false;
		}

		return method.isAccessible();
	}

	private HashSet<Class<? extends IEvent>> convertExcludesArray(Class<? extends IEvent>[] excludesArray) {
		HashSet<Class<? extends IEvent>> excludesSet = new HashSet<Class<? extends IEvent>>();
		if (excludesArray != null) {
			for (Class<? extends IEvent> exclusion : excludesArray) {
				if (exclusion != null) {
					excludesSet.add(exclusion);
				}
			}
		}

		return excludesSet;
	}

}
