package org.pakhama.vaadin.mvp.event;

import java.lang.reflect.Method;

import org.pakhama.vaadin.mvp.exception.EventListenerFaultException;
import org.pakhama.vaadin.mvp.exception.EventRegistrationException;
import org.pakhama.vaadin.mvp.presenter.Presenter;
import org.pakhama.vaadin.mvp.view.View;

class EventBusImpl implements EventBus {
	private EventRegistry eventRegistry;
	private ViewRegistry viewRegistry;

	private EventRegistry provideEventRegistry() {
		if (this.eventRegistry == null) {
			this.eventRegistry = new EventRegistry();
		}

		return this.eventRegistry;
	}
	
	private ViewRegistry provideViewRegistry() {
		if (this.viewRegistry == null) {
			this.viewRegistry = new ViewRegistry();
		}

		return this.viewRegistry;
	}

	@Override
	public void register(Presenter<? extends View> presenter) {
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
					provideViewRegistry().register(presenter);
				}
			}
		}
	}

	@Override
	public void unregister(Presenter<? extends View> presenter) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter must not be null.");
		}

		provideEventRegistry().unregister(presenter);
		provideViewRegistry().unregister(presenter);
	}

	@Override
	public void fire(Event e) {
		if (e == null) {
			throw new IllegalArgumentException("The event parameter cannot be null.");
		}

		switch (e.getScope()) {
		case PARENT:
			if (e.getSource() instanceof Presenter<?>) {
				Presenter<?> parent = ((Presenter<?>)e.getSource()).getParent();
				if (parent != null) {
					try {
						provideEventRegistry().invoke(e, parent);
					} catch (Throwable t) {
						throw new EventListenerFaultException(t, e);
					}
					break;
				}
			} else if (e.getSource() instanceof View) {
				Presenter<?> parent = getPresenter((View) e.getSource());
				if (parent != null) {
					try {
						provideEventRegistry().invoke(e, parent);
					} catch (Throwable t) {
						throw new EventListenerFaultException(t, e);
					}
					break;
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

	@Override
	public Presenter<? extends View> getPresenter(View view) {
		return provideViewRegistry().getPresenter(view);
	}

}
