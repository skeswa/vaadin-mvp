package org.pakhama.vaadin.mvp.event.impl;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.pakhama.vaadin.mvp.event.IEvent;
import org.pakhama.vaadin.mvp.event.IEventDelegate;
import org.pakhama.vaadin.mvp.event.IEventHandler;

public class EventDelegate implements IEventDelegate {
	private static final long serialVersionUID = 3831425706540938586L;

	private final Method method;
	private final WeakReference<IEventHandler> handler;
	private final Class<? extends IEvent> eventType;
	private final ArrayList<Collection<IEventDelegate>> owners = new ArrayList<Collection<IEventDelegate>>();

	public EventDelegate(Method method, IEventHandler handler, Class<? extends IEvent> eventType) {
		if (method == null) {
			throw new IllegalArgumentException("The method parameter cannot be null when creating a " + getClass().getSimpleName() + ".");
		}
		if (handler == null) {
			throw new IllegalArgumentException("The eventBus parameter cannot be null when creating a " + getClass().getSimpleName() + ".");
		}
		if (eventType == null) {
			throw new IllegalArgumentException("The eventType parameter cannot be null when creating a " + getClass().getSimpleName() + ".");
		}

		this.method = method;
		this.handler = new WeakReference<IEventHandler>(handler);
		this.eventType = eventType;
	}

	@Override
	public Method getMethod() {
		return this.method;
	}

	@Override
	public IEventHandler getHandler() {
		return this.handler.get();
	}

	@Override
	public Class<? extends IEvent> getEventType() {
		return this.eventType;
	}
	
	public void addOwner(Collection<IEventDelegate> owner) {
		this.owners.add(owner);
	}
	
	public void removeOwner(Collection<IEventDelegate> owner) {
		this.owners.remove(owner);
	}
	
	@Override
	public void kill() {
		for (Collection<IEventDelegate> owner : this.owners) {
			owner.remove(this);
		}
		
		try {
			finalize();
		} catch (Throwable e) {
			// Doesn't really matter is this fails
		}
	}

	@Override
	public int hashCode() {
		int result = 91;
		result = result * 31;
		result += this.method.hashCode();
		result = result * 31;
		if (this.handler.get() != null) {
			result += this.handler.get().hashCode();
		}
		result = result * 31;
		result += this.eventType.hashCode();

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof EventDelegate)) {
			return false;
		}

		return obj.hashCode() == hashCode();
	}
}
