package org.pakhama.vaadin.mvp.event;

public class BlueColorChangeEvent extends ColorChangeEvent {
	private static final long serialVersionUID = -3170046095513449083L;

	public BlueColorChangeEvent() {
		setScope(EventScope.UNIVERAL);
	}
}
