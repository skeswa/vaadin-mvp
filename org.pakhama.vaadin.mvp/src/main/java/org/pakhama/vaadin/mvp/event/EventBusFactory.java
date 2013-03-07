package org.pakhama.vaadin.mvp.event;

/**
 * Creates new instances of {@link IEventBus}. However, event busses are
 * intended to exist in a one-eventbus-per-session scheme. This is because
 * native event busses can only propagate events within themselves. This does
 * not mean that customized implementations of {@link IEventBus} will suffer
 * this same limitation.
 * 
 * @author Sandile
 */
public class EventBusFactory {
	/**
	 * Creates an implementation of {@link IEventBus} and returns it. 
	 * @return implementation of {@link IEventBus}
	 */
	public IEventBus create() {
		return new EventBusImpl();
	}

}
