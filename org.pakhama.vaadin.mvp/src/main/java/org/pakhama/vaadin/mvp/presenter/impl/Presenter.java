package org.pakhama.vaadin.mvp.presenter;

import org.pakhama.vaadin.mvp.annotation.Injection;
import org.pakhama.vaadin.mvp.event.Event;
import org.pakhama.vaadin.mvp.event.EventScope;
import org.pakhama.vaadin.mvp.event.IEventBus;
import org.pakhama.vaadin.mvp.event.annotation.Listener;
import org.pakhama.vaadin.mvp.view.IView;

/**
 * Presenters are responsible for applying data to their corresponding views
 * (what the user is able to see). Presenters are created with the Presenters
 * often know what data to paint by receiving and handling events. Events are
 * handled by methods in the presenter with the {@link EventListener} annotation. All
 * presenters have a view which implements the {@link IView} interface.
 * Presenters can create other presenters called Child Presenters. A child
 * presenter has the ability to propagate events directly to its parent
 * presenter.
 * 
 * @author Sandile
 * 
 * @param <T>
 *            the view type of this presenter
 */
public abstract class Presenter<T extends IView> implements IPresenter<T> {
	private static final long serialVersionUID = 5131211825391491296L;

	private IPresenter<? extends IView> parent;
	@Injection
	private T view;
	@Injection
	private IEventBus eventBus;
	@Injection
	private IPresenterFactory factory;
	
	/**
	 * This method returns the type of the implementation of the this
	 * presenter's {@link IView}. It should not return <code>null</code>, and
	 * the view implementation must implement the view type of this presenter as
	 * specified in the generic of this class.
	 * 
	 * @return the class of the view implementation for this presenter
	 */
	protected abstract Class<? extends T> view();

	@Override
	public T getView() {
		return view;
	}

	void setPresenterFactory(PresenterFactory factory) {
		if (this.factory == null) {
			this.factory = factory;
		}
	}

	/**
	 * Propagates the event e to every listener method listening for it that is
	 * also registered in this presenter's event bus. The event is propagated
	 * according to the <code>getScope()</code> method of the event which
	 * returns an {@link EventScope}.
	 * 
	 * @param e
	 *            the event to be fired
	 */
	protected void fire(Event e) {
		this.eventBus.fire(this, e);
	}

	/**
	 * Propagates the event e to every listener method listening for it that is
	 * also registered in this presenter's event bus. The event is propagated
	 * according to the scope parameter <b>regardless</b> of the scope of the
	 * actual event. The scope parameter will <b>override the fired event's
	 * current scope</b>.
	 * 
	 * @param e
	 *            the event to be fired
	 * @param scope
	 *            the scope to fire this event with
	 */
	protected void fire(org.pakhama.vaadin.mvp.event.Event e, EventScope scope) {
		this.eventBus.fire(this, scope, e);
	}

	void setEventBus(IEventBus eventBus) {
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
	public IPresenter<? extends IView> getParent() {
		return this.parent;
	}

	void setParent(IPresenter<? extends IView> parent) {
		this.parent = parent;
	}

	@Override
	public <E extends Presenter<? extends IView>> E createChild(Class<E> presenterClass) {
		return this.factory.create(presenterClass, null);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(':');
		builder.append('{');
		if (view == null) {
			builder.append("<null view>, ");
		} else {
			builder.append(view.getClass().getSimpleName());
			builder.append(", ");
		}
		if (eventBus == null) {
			builder.append("<null event bus>, ");
		} else {
			builder.append(eventBus.getClass().getSimpleName());
			builder.append(", ");
		}
		if (parent == null) {
			builder.append("<null parent>");
		} else {
			builder.append(parent.getClass().getSimpleName());
		}
		builder.append('}');

		return builder.toString();
	}
}
