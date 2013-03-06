package org.pakhama.vaadin.mvp.event;

import java.io.Serializable;

public abstract class Event implements Serializable {
	private static final long serialVersionUID = -1668154897552260787L;
	private static final int HASH_CODE_CONSTANT = 29;
	
	private Object source;
	private EventScope scope = EventScope.ALL;
	
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
		builder.append(getClass().getName());
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
