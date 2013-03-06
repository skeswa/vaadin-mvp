package org.pakhama.vaadin.mvp.event;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

class EventRegistryEntry {
	private static final int HASH_CODE_CONSTANT = 17;
	
	private WeakReference<Object> handlerInstance;
	private Method listenerMethod;
	private Class<? extends Event> eventType;
	
	public EventRegistryEntry(Object handlerInstance, Method listenerMethod, Class<? extends Event> event) {
		this.handlerInstance = new WeakReference<Object>(handlerInstance);
		this.listenerMethod = listenerMethod;
		this.eventType = event;
	}

	Method getListenerMethod() {
		return listenerMethod;
	}

	Object getHandlerInstance() {
		return handlerInstance.get();
	}
	
	Class<? extends Event> getEventType() {
		return eventType;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EventRegistryEntry)) {
			return false;
		} else {
			EventRegistryEntry other = (EventRegistryEntry) obj;
			if (other.getHandlerInstance() == getHandlerInstance()) {
				if (other.getListenerMethod().equals(this.listenerMethod)) {
					if (other.getEventType().equals(this.eventType)) {
						return true;
					}
				}
			}
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int result = HASH_CODE_CONSTANT;
		if (handlerInstance != null && handlerInstance.get() != null) {
			result += 31 * result + handlerInstance.get().hashCode();
		}
		if (listenerMethod != null) {
			result += 31 * result + listenerMethod.hashCode();
		}
		if (eventType != null) {
			result += 31 * result + eventType.hashCode();
		}
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append(": { Instance: ");
		if (handlerInstance != null && handlerInstance.get() != null) {
			builder.append(handlerInstance.get());
		} else {
			builder.append("null");
		}
		builder.append(", Listener Method: ");
		if (listenerMethod != null) {
			builder.append(listenerMethod);
		} else {
			builder.append("null");
		}
		builder.append(", Event: ");
		if (eventType != null) {
			builder.append(eventType);
		} else {
			builder.append("null");
		}
		builder.append(" }");
		
		return builder.toString();
	}
}
