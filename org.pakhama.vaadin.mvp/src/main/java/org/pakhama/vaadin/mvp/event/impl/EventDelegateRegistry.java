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

	private HashMap<Class<? extends IEvent>, HashSet<IEventDelegate>> eventMap = new HashMap<Class<? extends IEvent>, HashSet<IEventDelegate>>();
	private HashMap<IEventHandler, HashMap<Class<? extends IEvent>, ArrayList<IEventDelegate>>> handlerMap = new HashMap<IEventHandler, HashMap<Class<? extends IEvent>, ArrayList<IEventDelegate>>>();

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

		HashMap<Class<? extends IEvent>, ArrayList<IEventDelegate>> delegateMap = this.handlerMap.get(handler);
		if (delegateMap != null) {
			for (Class<? extends IEvent> key : delegateMap.keySet()) {
				ArrayList<IEventDelegate> delegateList = delegateMap.get(key);
				if (delegateList != null) {
					while (delegateList.size() > 0) {
						delegateList.get(0).kill();
					}
				}
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
	public Collection<IEventDelegate> find(Class<? extends IEvent> eventClass, Collection<IEventHandler> handlers) {
		if (eventClass == null) {
			throw new IllegalArgumentException("The eventClass parameter cannot be null.");
		}
		if (handlers == null) {
			throw new IllegalArgumentException("The handlers parameter cannot be null.");
		}
		// Get the delegates for each handler, and throw them into a list of all
		// the delegates
		HashSet<IEventDelegate> allDelegates = new HashSet<IEventDelegate>();
		HashMap<Class<? extends IEvent>, ArrayList<IEventDelegate>> delegateMap = null;
		ArrayList<IEventDelegate> delegates = null;
		for (IEventHandler handler : handlers) {
			if (handler != null) {
				delegateMap = this.handlerMap.get(handler);
				if (delegateMap != null) {
					delegates = delegateMap.get(eventClass);
					if (delegates != null) {
						allDelegates.addAll(delegates);
					}
				}
			}
		}

		return allDelegates;
	}

	protected void processHandler(IEventHandler handler) {
		Method[] methods = handler.getClass().getMethods();
		EventListener listenerAnnotation = null;
		for (Method method : methods) {
			if ((listenerAnnotation = method.getAnnotation(EventListener.class)) != null) {
				Class<? extends IEvent> eventType = listenerAnnotation.event();
				HashSet<Class<? extends IEvent>> exclusionSet = convertExcludesArray(listenerAnnotation.excludes());
				if (!isValidEventListenerMethod(eventType, method)) {
					throw new EventListenerRegistrationException("Could not register method " + method + " as an @EventListener listening for " + eventType
							+ ". Methods listening for events must either have the event they are listening for as a method parameter OR have no parameters at all.");
				} else {
					// Is a valid listener method
					// Get delegates, the delegate for the most super event type
					// is the last and the first is of the least super
					ArrayList<EventDelegate> newDelegates = createDelegates(eventType, exclusionSet, handler, method);
					// Taking care of the event map
					EventDelegate currentDelegate = null;
					while (newDelegates.size() > 0) {
						currentDelegate = newDelegates.get(0);
						// Register event delegates - keeping event inheritance
						// in mind
						addToEventMap(currentDelegate.getEventType(), newDelegates);
						// Register handler specific delegates - keeping event
						// inheritance in mind
						addToHandlerMap(handler, currentDelegate.getEventType(), newDelegates);
						// Remove the current delegate and move up to the
						// delegate of its event type's super (if it exists)
						newDelegates.remove(0);
					}
				}
			}
		}
	}

	private boolean isValidEventListenerMethod(Class<? extends IEvent> eventType, Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length > 1) {
			return false;
		} else if (parameterTypes.length == 0) {
			return true;
		} else if (eventType.isAssignableFrom(parameterTypes[0])) {
			return true;
		} else {
			return false;
		}
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

	@SuppressWarnings("unchecked")
	private ArrayList<EventDelegate> createDelegates(Class<? extends IEvent> eventType, Collection<Class<? extends IEvent>> exclusionSet, IEventHandler handler,
			Method method) {
		ArrayList<EventDelegate> newDelegates = new ArrayList<EventDelegate>();
		Class<?> targetClass = eventType;
		while (IEvent.class.isAssignableFrom(targetClass)) {
			if (!exclusionSet.contains(targetClass)) {
				newDelegates.add(new EventDelegate(method, handler, (Class<? extends IEvent>) targetClass));
			}

			targetClass = targetClass.getSuperclass();
		}

		return newDelegates;
	}

	private void addToEventMap(Class<? extends IEvent> eventType, Collection<EventDelegate> delegates) {
		HashSet<IEventDelegate> delegateSet = this.eventMap.get(eventType);
		if (delegateSet == null) {
			delegateSet = new HashSet<IEventDelegate>();
			this.eventMap.put(eventType, delegateSet);
		}

		for (EventDelegate delegate : delegates) {
			delegateSet.add(delegate);
			delegate.addOwner(delegateSet);
		}
	}

	private void addToHandlerMap(IEventHandler handler, Class<? extends IEvent> eventType, Collection<EventDelegate> delegates) {
		HashMap<Class<? extends IEvent>, ArrayList<IEventDelegate>> delegateMap = this.handlerMap.get(handler);
		if (delegateMap == null) {
			delegateMap = new HashMap<Class<? extends IEvent>, ArrayList<IEventDelegate>>();
			this.handlerMap.put(handler, delegateMap);
		}

		ArrayList<IEventDelegate> delegateList = delegateMap.get(eventType);
		if (delegateList == null) {
			delegateList = new ArrayList<IEventDelegate>();
			delegateMap.put(eventType, delegateList);
		}

		for (EventDelegate delegate : delegates) {
			delegateList.add(delegate);
			delegate.addOwner(delegateList);
		}
	}

	@Override
	public int size() {
		return this.handlerMap.keySet().size();
	}
}
