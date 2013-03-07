package org.pakhama.vaadin.mvp.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pakhama.vaadin.mvp.event.Event;

@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Listener {
	Class<? extends Event> event();
}
