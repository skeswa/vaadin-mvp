package org.pakhama.vaadin.mvp.event;

public enum EventScope {
	/**
	 * If the event creator is a view, propagate the Event only to its
	 * presenter. If the event creator is a presenter, propagate the Event only
	 * to its parent presenter; if there is no parent presenter, the scope
	 * reverts to <code>EventScope.ALL</code>.
	 */
	PARENT,
	/**
	 * Propagate the event to all registered event listeners. This scope is the
	 * default for all events.
	 */
	ALL
}
