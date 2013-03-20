package org.pakhama.vaadin.mvp.event.one;

import org.pakhama.vaadin.mvp.annotation.event.EventListener;
import org.pakhama.vaadin.mvp.event.IEventHandler;

public class OneEventHandler implements IEventHandler {
	private static final long serialVersionUID = -2637746704496710598L;

	@EventListener(event = YetAnotherOneTestEvent.class, excludes = { FinallyAnotherOneTestEvent.class })
	public void onYetAnotherOne() {
	}
	
	@EventListener(event = AnotherOneTestEvent.class)
	public void onAnotherOne() {
	}
	
	@EventListener(event = FinallyAnotherOneTestEvent.class)
	public void onFinallyAnotherOne() {
	}
}
