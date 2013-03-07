package org.pakhama.vaadin.mvp.event;

public class RedColorChangeEvent extends ColorChangeEvent {
	private static final long serialVersionUID = -3170046095513449083L;

	public RedColorChangeEvent(Object source) {
		super(source);
		setScope(EventScope.PARENT);
	}
}
