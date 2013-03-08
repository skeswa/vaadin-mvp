package org.pakhama.vaadin.mvp.event;

public class GreenColorChangeEvent extends ColorChangeEvent {
	private static final long serialVersionUID = -3170046095513449083L;

	public GreenColorChangeEvent(EventScope scope) {
		setScope(scope);
	}
}
