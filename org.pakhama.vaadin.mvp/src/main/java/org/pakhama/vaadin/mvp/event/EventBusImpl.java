package org.pakhama.vaadin.mvp.event;

import java.lang.reflect.Method;

import org.pakhama.vaadin.mvp.event.annotation.Listener;
import org.pakhama.vaadin.mvp.exception.EventListenerFaultException;
import org.pakhama.vaadin.mvp.exception.EventPropagationException;
import org.pakhama.vaadin.mvp.exception.EventRegistrationException;
import org.pakhama.vaadin.mvp.presenter.Presenter;
import org.pakhama.vaadin.mvp.view.IView;

class EventBusImpl implements IEventBus {
	private EventRegistry eventRegistry;

	private EventRegistry provideEventRegistry() {
		if (this.eventRegistry == null) {
			this.eventRegistry = new EventRegistry();
		}

		return this.eventRegistry;
	}

	@Override
	public void register(Presenter<? extends IView> presenter) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter cannot be null.");
		}

		Method[] methods = presenter.getClass().getMethods();
		Listener listener = null;
		for (Method method : methods) {
			if (method != null) {
				if ((listener = method.getAnnotation(Listener.class)) != null) {
					if (listener.event() == null) {
						throw new EventRegistrationException("Listener method " + method
								+ " could not be registered because the event parameter of the @Listener annotation was null.");
					}

					Class<?>[] paramTypes = method.getParameterTypes();
					if (paramTypes.length > 1) {
						throw new EventRegistrationException("Listener method " + method
								+ " could not be registered because an event listener method can only have its own Event, or nothing, as a parameter.");
					} else if ((paramTypes.length == 1) && !listener.event().isAssignableFrom(paramTypes[0])) {
						throw new EventRegistrationException("Listener method " + method
								+ " could not be registered because its only parameter did not match the event value of the @Listener parameter.");
					}

					provideEventRegistry().register(presenter, method, listener.event());
				}
			}
		}
	}

	@Override
	public void unregister(Presenter<? extends IView> presenter) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter must not be null.");
		}

		provideEventRegistry().unregister(presenter);
	}

	@Override
	public void fire(Event e) {
		if (e == null) {
			throw new IllegalArgumentException("The event parameter cannot be null.");
		}

		switch (e.getScope()) {
		case PARENT:
			if (e.getSource() instanceof Presenter<?>) {
				Presenter<?> parent = ((Presenter<?>) e.getSource()).getParent();
				if (parent != null) {
					try {
						provideEventRegistry().invoke(e, parent);
					} catch (Throwable t) {
						throw new EventListenerFaultException(t, e);
					}
					break;
				} else {
					throw new EventPropagationException("The source of this event (" + e.getSource()
							+ ") had no parent. The source of the event needs a source to propagate to its parent.");
				}
			} else if (e.getSource() instanceof IView) {
				Object parent = ((IView) e.getSource()).getOwner();
				if (parent != null) {
					try {
						provideEventRegistry().invoke(e, parent);
					} catch (Throwable t) {
						throw new EventListenerFaultException(t, e);
					}
					break;
				} else {
					throw new EventPropagationException("The source of this event (" + e.getSource()
							+ ") had no parent. The source of the event needs a source to propagate to its parent.");
				}
			}
		case ALL:
		default:
			try {
				provideEventRegistry().invokeAll(e);
			} catch (Throwable t) {
				throw new EventListenerFaultException(t, e);
			}
			break;
		}
	}

}
