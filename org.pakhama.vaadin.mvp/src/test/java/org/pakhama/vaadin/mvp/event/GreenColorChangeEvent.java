package org.pakhama.vaadin.mvp.event;

public class GreenColorChangeEvent extends Event {
	private static final long serialVersionUID = -3170046095513449083L;

	public GreenColorChangeEvent(Object source) {
		super(source, EventScope.PARENT);
	}
}
