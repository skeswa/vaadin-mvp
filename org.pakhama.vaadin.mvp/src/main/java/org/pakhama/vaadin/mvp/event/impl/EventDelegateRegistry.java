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
	/**
	 * This map is the union of every entry in the the
	 * {@link EventDelegateRegistry#handlerMap}. This is specifically useful for
	 * finding all delegates that must be fired for a specific event type
	 * without an expensive looping function.
	 */
	private HashMap<Class<? extends IEvent>, HashSet<IEventDelegate>> unifiedHandlerMap = new HashMap<Class<? extends IEvent>, HashSet<IEventDelegate>>();
	/**
	 * This map keeps a reference of every {@link IEventDelegate} that pertain
	 * to a specific {@link IEventHandler} instance.
	 */
	private HashMap<IEventHandler, HashMap<Class<? extends IEvent>, HashSet<IEventDelegate>>> handlerMap = new HashMap<IEventHandler, HashMap<Class<? extends IEvent>, HashSet<IEventDelegate>>>();

	@Override
	public void register(IEventHandler handler) {
		if (handler == null) {
			throw new IllegalArgumentException("The handler parameter cannot be null.");
		}
		// Register the handler in both of the delegate map fields
		registerHandler(handler);
	}

	@Override
	public void unregister(IEventHandler handler) {
		if (handler == null) {
			throw new IllegalArgumentException("The handler parameter cannot be null.");
		}

		HashMap<Class<? extends IEvent>, HashSet<IEventDelegate>> delegateMap = this.handlerMap.get(handler);
		// Kill every delegate for this handler
		if (delegateMap != null) {
			for (Class<? extends IEvent> key : delegateMap.keySet()) {
				HashSet<IEventDelegate> delegateSet = delegateMap.get(key);
				if (delegateSet != null) {
					// Clone the delegateSet for iteration since we'll be
					// removing things form it
					IEventDelegate[] delegateArray = delegateSet.toArray(new IEventDelegate[] {});
					for (IEventDelegate delegate : delegateArray) {
						if (delegate != null) {
							// Delegate removes itself from every place it's
							// registered
							delegate.suicide();
						}
					}
				}
			}
			this.handlerMap.remove(handler);
		}
	}

	@Override
	public void clear() {
		this.unifiedHandlerMap.clear();
	}

	@Override
	public Collection<IEventDelegate> find(Class<? extends IEvent> eventClass) {
		if (eventClass == null) {
			throw new IllegalArgumentException("The eventClass parameter cannot be null.");
		}

		return this.unifiedHandlerMap.get(eventClass);
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
		ArrayList<IEventDelegate> allDelegates = new ArrayList<IEventDelegate>();
		HashMap<Class<? extends IEvent>, HashSet<IEventDelegate>> delegateMap = null;
		HashSet<IEventDelegate> delegates = null;
		for (IEventHandler handler : handlers) {
			if (handler != null) {
				delegateMap = this.handlerMap.get(handler);
				if (delegateMap != null) {
					delegates = delegateMap.get(eventClass);
					if (delegates != null) {
						// We don't call contains() here because we know that
						// delegates will never repeat - they are guaranteed to
						// have different event handlers
						allDelegates.addAll(delegates);
					}
				}
			}
		}

		return allDelegates;
	}

	private void registerHandler(IEventHandler handler) {
		Method[] methods = handler.getClass().getMethods();
		EventListener listenerAnnotation = null;
		for (Method method : methods) {
			if ((listenerAnnotation = method.getAnnotation(EventListener.class)) != null) {
				Class<? extends IEvent> eventType = listenerAnnotation.event();
				// Convert the excludes() array to a hash set so we can keep
				// calling contains(...)
				HashSet<Class<? extends IEvent>> exclusionSet = convertExcludesArray(listenerAnnotation.excludes());
				// Yell at the developer if they format the event listeners
				// wrong
				validateEventListenerMethod(eventType, method);
				// Create and register a new delegate for this handler/method
				EventDelegate newDelegate = new EventDelegate(method, handler, eventType);
				registerDelegate(this.unifiedHandlerMap, exclusionSet, newDelegate);
				registerDelegate(ensureHandlerDelegateMap(handler), exclusionSet, newDelegate);
			}
		}
	}

	private void validateEventListenerMethod(Class<? extends IEvent> eventType, Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length > 1) {
			throw new EventListenerRegistrationException(
					"The event listener mothod "
							+ method.getName()
							+ "(...) could not be registered. Event listener methods must have at most one parameter - that one parameter has an IS-A relationship with the event parameter of the the @EventListener annotation.");
		} else if (eventType.isInterface()) {
			throw new EventListenerRegistrationException(
					"The event listener mothod "
							+ method.getName()
							+ "(...) could not be registered. The event parameter of the @EventListener annotation must NOT be an interface. It should be a class implementation of org.pakhama.vaadin.mvp.event.IEvent.");
		} else if (parameterTypes.length == 1 && !eventType.isAssignableFrom(parameterTypes[0])) {
			throw new EventListenerRegistrationException(
					"The event listener mothod "
							+ method.getName()
							+ "(...) could not be registered. Event listener methods with one parameter must have an IS-A relationship with the event parameter of the the @EventListener annotation.");
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

	private HashMap<Class<? extends IEvent>, HashSet<IEventDelegate>> ensureHandlerDelegateMap(IEventHandler handler) {
		HashMap<Class<? extends IEvent>, HashSet<IEventDelegate>> delegateMap = this.handlerMap.get(handler);
		if (delegateMap == null) {
			delegateMap = new HashMap<Class<? extends IEvent>, HashSet<IEventDelegate>>();
			this.handlerMap.put(handler, delegateMap);
		}
		return delegateMap;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends IEvent> findClosestRegisteredSuperType(Class<? extends IEvent> eventType) {
		Class<?> target = eventType.getSuperclass();
		while (IEvent.class.isAssignableFrom(target)) {
			if (this.unifiedHandlerMap.containsKey(target)) {
				return (Class<? extends IEvent>) target;
			} else {
				target = target.getSuperclass();
			}
		}
		return null;
	}

	private void registerDelegate(HashMap<Class<? extends IEvent>, HashSet<IEventDelegate>> map, HashSet<Class<? extends IEvent>> exclusionSet, EventDelegate delegate) {
		HashSet<IEventDelegate> delegateSet = map.get(delegate.getEventType());
		if (delegateSet == null) {
			// This delegate bucket doesn't yet exist, so we have to find the
			// closest super bucket and steal all its delegates
			delegateSet = new HashSet<IEventDelegate>();
			Class<? extends IEvent> closestRegisteredSuperType = findClosestRegisteredSuperType(delegate.getEventType());
			if (closestRegisteredSuperType != null) {
				// Add the contents of the super delegate set if it exists
				HashSet<IEventDelegate> superDelegateSet = map.get(closestRegisteredSuperType);
				if (superDelegateSet != null) {
					for (IEventDelegate superDelegate : superDelegateSet) {
						if (superDelegate != null) {
							delegateSet.add(superDelegate);
							if (superDelegate instanceof EventDelegate) {
								((EventDelegate) superDelegate).addOwner(delegateSet);
							}
						}
					}
				}
			}
			// Add this new delegate to the new delegate set
			delegateSet.add(delegate);
			delegate.addOwner(delegateSet);

			// Finally, add this delegate set to the eventMap
			map.put(delegate.getEventType(), delegateSet);
		} else {
			if (!delegateSet.contains(delegate)) {
				delegateSet.add(delegate);
				delegate.addOwner(delegateSet);
			}
		}

		// Now we have to update all the sub delegate sets, while keeping
		// exclusions in mind
		for (Class<? extends IEvent> key : map.keySet()) {
			if (key != null) {
				// If this key is a sub type of the new event type, and is not
				// being excluded, add the new delegate to it
				if (!exclusionSet.contains(key)) {
					if (delegate.getEventType().isAssignableFrom(key) && !delegate.getEventType().equals(key)) {
						HashSet<IEventDelegate> subDelegateSet = map.get(key);
						if (subDelegateSet != null) {
							if (!subDelegateSet.contains(delegate)) {
								subDelegateSet.add(delegate);
								delegate.addOwner(subDelegateSet);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public int size() {
		return this.handlerMap.keySet().size();
	}
}
