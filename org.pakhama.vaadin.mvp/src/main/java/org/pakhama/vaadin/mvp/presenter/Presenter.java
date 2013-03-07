package org.pakhama.vaadin.mvp.presenter;

import java.io.Serializable;

import org.pakhama.vaadin.mvp.event.EventBus;
import org.pakhama.vaadin.mvp.exception.IncompatibleViewException;
import org.pakhama.vaadin.mvp.view.View;

public abstract class Presenter<T extends View> implements Serializable {
	private static final long serialVersionUID = 5131211825391491296L;

	private Presenter<? extends View> parent;
	private T view;
	private EventBus eventBus;

	/**
	 * Gets this presenters view. The type of this view is specified by the type
	 * of the generic for this class.
	 * 
	 * @return this presenter's view
	 */
	public T getView() {
		return view;
	}

	@SuppressWarnings("unchecked")
	void setView(View view) {
		try {
			this.view = (T) view;
		} catch (ClassCastException e) {
			throw new IncompatibleViewException(view, this);
		}
	}

	/**
	 * Gets the event bus this presenter has been registered under.
	 * 
	 * @return this presenter's event bus
	 */
	public EventBus getEventBus() {
		return eventBus;
	}

	void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	/**
	 * Gets the parent presenter for this presenter. Not every presenter has a
	 * parent - whether or not a presenter has a parent is subject to whether
	 * the <code>PresenterFactory</code> that created it gave it a parent. If
	 * this presenter has no parent, this method will return null.
	 * 
	 * @return the parent presenter or null if no such parent presenter exists
	 */
	public Presenter<? extends View> getParent() {
		return this.parent;
	}

	void setParent(Presenter<? extends View> parent) {
		this.parent = parent;
	}

	/**
	 * This method is called when this presenter is ready to be initialized. At
	 * this point, this presenter has a refeence to its event bus, parent and
	 * view. <b>However,</b> before this method is called, all the getters for
	 * the previously mentioned field will return <b>null values</b>. The
	 * <code>init()</code> method of any presenter is always invoked after its
	 * view's <code>init()</code> method.
	 */
	public abstract void init();

	/**
	 * This method simply returns the chosen implementation of the this
	 * presenter's view interface. It should not return null, and the view
	 * implementation must implement the view interface of this presenter (as
	 * specified in the generic of this class).
	 * 
	 * @return
	 * 		the class of the view implementation for this presenter
	 */
	public abstract Class<? extends T> view();

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
//		builder.append(": { Parent: ");
//		if (parent != null) {
//			builder.append(parent);
//		} else {
//			builder.append("null");
//		}
//		builder.append(", View: ");
//		if (view != null) {
//			builder.append(view.getClass().getSimpleName());
//		} else {
//			builder.append("null");
//		}
//		builder.append(", Event Bus: ");
//		if (eventBus != null) {
//			builder.append(eventBus.getClass().getSimpleName());
//		} else {
//			builder.append("null");
//		}
//		builder.append(" }");

		return builder.toString();
	}
}
