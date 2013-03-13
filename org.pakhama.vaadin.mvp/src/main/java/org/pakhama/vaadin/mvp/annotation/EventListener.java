package org.pakhama.vaadin.mvp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pakhama.vaadin.mvp.event.IEvent;

@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface EventListener {
	Class<? extends IEvent> event();
	Class<? extends IEvent>[] excludes() default {};
}
