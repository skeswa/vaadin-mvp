package org.pakhama.vaadin.mvp.event;

import java.io.Serializable;

import org.pakhama.vaadin.mvp.presenter.Presenter;

/**
 * This is the super type for all events. Events are fired with the
 * {@link EventBus} <code>fire()</code> method. Events can be listened to by
 * with {@link Presenter} methods labeled with the {@link Listener} annotation.
 * 
 * @author Sandile
 */
public abstract class Event implements Serializable {
	private static final long serialVersionUID = -1668154897552260787L;
	private static final int HASH_CODE_CONSTANT = 29;

	private Object source;
	private EventScope scope = EventScope.ALL;

	/**
	 * Initializes the event with the source parameter. The source is intended to be a reference to the object that fired this Event. This constructor initializes the scope of the event as  
	 * @param source
	 */
	public Event(Object source) {
		this.source = source;
	}

	public Event(Object source, EventScope scope) {
		this.source = source;
		this.scope = scope;
	}

	public Object getSource() {
		return source;
	}

	public void setScope(EventScope scope) {
		this.scope = scope;
	}

	public EventScope getScope() {
		return scope;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!getClass().isAssignableFrom(obj.getClass())) {
			return false;
		}
		if (!((Event) obj).getScope().equals(this.scope)) {
			return false;
		}
		if (((Event) obj).getSource() == null) {
			if (this.source != null) {
				return false;
			} else {
				return true;
			}
		} else {
			return ((Event) obj).getSource().equals(this.source);
		}
	}

	@Override
	public int hashCode() {
		int result = HASH_CODE_CONSTANT;
		if (source != null) {
			result += 31 * result + source.hashCode();
		}
		if (scope != null) {
			result += 31 * result + scope.hashCode();
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(": { Source: ");
		if (source != null) {
			builder.append(source);
		} else {
			builder.append("null");
		}
		builder.append(", Scope: ");
		if (scope != null) {
			builder.append(scope);
		} else {
			builder.append("null");
		}
		builder.append(" }");

		return builder.toString();
	}
}
