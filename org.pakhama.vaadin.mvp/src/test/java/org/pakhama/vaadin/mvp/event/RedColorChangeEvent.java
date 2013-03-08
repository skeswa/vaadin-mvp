package org.pakhama.vaadin.mvp.event;

public class RedColorChangeEvent extends ColorChangeEvent {
	private static final long serialVersionUID = -3170046095513449083L;

	public RedColorChangeEvent() {
		setScope(EventScope.PARENT);
	}
}
