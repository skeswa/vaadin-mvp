package org.pakhama.vaadin.mvp.presenter;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import org.pakhama.vaadin.mvp.event.IEventBus;
import org.pakhama.vaadin.mvp.exception.InaccessiblePresenterException;
import org.pakhama.vaadin.mvp.exception.PresenterConstructionException;
import org.pakhama.vaadin.mvp.view.IView;
import org.pakhama.vaadin.mvp.view.View;
import org.pakhama.vaadin.mvp.view.ViewFactory;

/**
 * Creates new instances of {@link Presenter}. To create a presenter factory,
 * you must first create an {@link IEventBus} for the presenter factory to
 * register new presenters under.
 * 
 * @author Sandile
 */
public class PresenterFactory implements Serializable {
	private static final long serialVersionUID = 2864312773902372753L;

	private IEventBus eventBus;
	private ViewFactory viewFactory;

	public PresenterFactory(IEventBus eventBus) {
		if (eventBus == null) {
			throw new IllegalArgumentException("The eventBus parameter cannot be null when creating a " + getClass().getSimpleName() + ".");
		}

		this.eventBus = eventBus;
		this.viewFactory = new ViewFactory();
	}

	@SuppressWarnings("unchecked")
	<T extends IPresenter<? extends IView>> T create(Class<T> presenterClass, IPresenter<? extends IView> parent) {
		IPresenter<? extends IView> childPresenter = create(presenterClass);
		if (childPresenter instanceof Presenter<?>) {
			((Presenter<?>) childPresenter).setParent(parent);
		}
		return (T) childPresenter;
	}

	/**
	 * Creates a new instance of the provided presenter class. New presenters
	 * are automatically registered against the event bus used to construct this
	 * factory. The view instance for each presenter instance is also
	 * automatically injected.
	 * 
	 * @param presenterClass
	 *            the type of presenter to create a new instance of
	 * @return the new instance of the provided presenter type
	 */
	@SuppressWarnings("unchecked")
	public <T extends IPresenter<? extends IView>> T create(Class<T> presenterClass) {
		if (presenterClass == null) {
			throw new IllegalArgumentException("The presenterClass parameter cannot be null.");
		}
		if (!Presenter.class.isAssignableFrom(presenterClass)) {
			throw new IllegalArgumentException("The presenterClass parameter must be a sub type of " + Presenter.class.getName() + ".");
		}

		Class<?> viewClass = PresenterFactory.getTypeOfGeneric(presenterClass);
		if (!IView.class.isAssignableFrom(viewClass)) {
			throw new IllegalArgumentException("The viewClass, specified in the generic of the presenterClass parameter, must implement " + IView.class.getName() + ".");
		}

		IPresenter<? extends IView> presenterInstance = null;
		try {
			presenterInstance = presenterClass.newInstance();
		} catch (InstantiationException e) {
			throw new PresenterConstructionException("Presenter sub-types must have default constructors.");
		} catch (IllegalAccessException e) {
			throw new InaccessiblePresenterException();
		}

		if (presenterInstance instanceof Presenter<?>) {
			((Presenter<?>) presenterInstance).setPresenterFactory(this);
			((Presenter<?>) presenterInstance).setEventBus(this.eventBus);
			
			Class<? extends IView> viewInstanceClass = ((Presenter<?>) presenterInstance).view();
			if (viewInstanceClass == null) {
				throw new PresenterConstructionException("The view() method of this presenter type (" + presenterClass.getName()
						+ ") returned null. It must return the class of the implementation of the presenter's view (" + viewClass + ").");
			}
			if (!viewClass.isAssignableFrom(viewInstanceClass)) {
				throw new PresenterConstructionException("The view() method of this presenter type (" + presenterClass.getName()
						+ ") returned a type that didn't implement this presenter's view (" + viewClass + ").");
			}
			IView viewInstance = this.viewFactory.create(this.eventBus, presenterInstance, viewInstanceClass);
			viewInstance.init();
			
			((Presenter<?>) presenterInstance).setView((View) viewInstance);
		}

		this.eventBus.register(presenterInstance);
		presenterInstance.init();

		return (T) presenterInstance;
	}

	private static Class<?> getTypeOfGeneric(Class<?> type) {
		return (Class<?>) ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments()[0];
	}
}