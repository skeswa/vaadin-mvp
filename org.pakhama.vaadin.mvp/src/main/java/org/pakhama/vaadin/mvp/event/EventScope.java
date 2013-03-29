package org.pakhama.vaadin.mvp.event;

/**
 * The
 * <code>EventScope<code> defines general event propagation restrictions for the event bus to adhere to in the <code>fire()</code>
 * method. All {@link IEvent}s specify an {@link EventScope} by definition. The
 * default {@link EventScope} is typically <code>EventScope.ALL</code>.
 * 
 * @author Sandile
 */
public enum EventScope {
	/**
	 * If the event source is a view, propagate the Event only to its presenter.
	 * If the event source is a presenter, propagate the Event only to its
	 * parent presenter. If the parent of the event source is null, the event
	 * will not be propagated.
	 */
	PARENT,
	/**
	 * If the event source is a presenter and has a parent, then the event will
	 * be propagated to only other children of the source's parent. If the
	 * parent of the event source is null, then the event will not be
	 * propagated.
	 */
	SIBLINGS,
	/**
	 * If the event source is a presenter, then this event will be propagated to
	 * only the children of the event source.
	 */
	CHILDREN,
	/**
	 * Propagate the event to all event listeners registered under this
	 * session's event bus. This scope is the default for all events.
	 */
	APPLICATION,
	/**
	 * Propagate the event to all event listeners registered in <b><i>every
	 * session's event bus.</i></b> This scope should be employed with care,
	 * since propagating an event of this scope is very expensive.
	 */
	UNIVERSAL
}
