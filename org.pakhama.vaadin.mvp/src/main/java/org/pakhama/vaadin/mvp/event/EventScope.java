package org.pakhama.vaadin.mvp.event;

import org.pakhama.vaadin.mvp.exception.EventPropagationException;

/**
 * The
 * <code>EventScope<code> defines general event propagation restrictions for the event bus to adhere to in the <code>fire()</code>
 * method. All {@link Event}s specify an {@link EventScope} by definition. The
 * default {@link EventScope} is typically <code>EventScope.ALL</code>.
 * 
 * @author Sandile
 */
public enum EventScope {
	/**
	 * If the event creator is a view, propagate the Event only to its
	 * presenter. If the event creator is a presenter, propagate the Event only
	 * to its parent presenter. In any case, if there is no parent, or the
	 * parent is null, a {@link EventPropagationException} will be thrown.
	 */
	PARENT,
	/**
	 * Propagate the event to all event listeners registered under the event bus
	 * firing the event. This scope is the default for all events.
	 */
	ALL
}
