package org.pakhama.vaadin.mvp.presenter.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import org.pakhama.vaadin.mvp.annotation.field.EventBusField;
import org.pakhama.vaadin.mvp.annotation.field.PresenterFactoryField;
import org.pakhama.vaadin.mvp.event.IEventBus;
import org.pakhama.vaadin.mvp.exception.FieldInjectionException;
import org.pakhama.vaadin.mvp.exception.InaccessiblePresenterException;
import org.pakhama.vaadin.mvp.exception.PresenterConstructionException;
import org.pakhama.vaadin.mvp.exception.ViewConstructionException;
import org.pakhama.vaadin.mvp.presenter.IPresenter;
import org.pakhama.vaadin.mvp.presenter.IPresenterFactory;
import org.pakhama.vaadin.mvp.presenter.IPresenterRegistry;
import org.pakhama.vaadin.mvp.view.IView;
import org.pakhama.vaadin.mvp.view.IViewFactory;

public class PresenterFactory implements IPresenterFactory {
	private static final long serialVersionUID = 2864312773902372753L;

	private IEventBus eventBus;
	private IViewFactory viewFactory;
	private IPresenterRegistry presenterRegistry;

	public PresenterFactory(IEventBus eventBus, IViewFactory viewFactory, IPresenterRegistry presenterRegistry) {
		if (eventBus == null) {
			throw new IllegalArgumentException("The eventBus parameter cannot be null when creating a " + getClass().getSimpleName() + ".");
		}
		if (viewFactory == null) {
			throw new IllegalArgumentException("The viewFactory parameter cannot be null when creating a " + getClass().getSimpleName() + ".");
		}
		if (presenterRegistry == null) {
			throw new IllegalArgumentException("The presenterRegistry parameter cannot be null when creating a " + getClass().getSimpleName() + ".");
		}

		this.eventBus = eventBus;
		this.viewFactory = viewFactory;
		this.presenterRegistry = presenterRegistry;
	}

	@Override
	public IPresenterRegistry getRegistry() {
		return this.presenterRegistry;
	}

	@SuppressWarnings("unchecked")
	public <T extends IPresenter<? extends IView>> T create(Class<T> presenterClass) {
		if (presenterClass == null) {
			throw new IllegalArgumentException("The presenterClass parameter cannot be null.");
		}
		if (!IPresenter.class.isAssignableFrom(presenterClass)) {
			throw new IllegalArgumentException("The presenterClass parameter must implement " + IPresenter.class.getName() + ".");
		}
		// Attempt to create a new presenter instance
		IPresenter<? extends IView> presenterInstance = null;
		try {
			presenterInstance = presenterClass.newInstance();
		} catch (InstantiationException e) {
			throw new PresenterConstructionException(presenterClass
					+ " could not be instantiated. Presenter implementations must have publicly accessible default constructors.");
		} catch (IllegalAccessException e) {
			throw new InaccessiblePresenterException(e);
		}
		// Attempt to derive the the class of the presenter instance's view
		Class<? extends IView> viewClass = (Class<? extends IView>) getTypeOfGeneric(presenterClass);
		if (viewClass == null) {
			throw new PresenterConstructionException("Could not determine the view class for the corresponding IPresenter sub class " + presenterClass + ".");
		}
		// Attempt to create a new instance of the presenter instance's view via
		// the view factory
		IView viewInstance = this.viewFactory.create(viewClass);
		if (viewInstance == null) {
			throw new ViewConstructionException("Could not create a new instance of view class " + viewClass + ". This is the result of the view factory returning null.");
		}
		// Attempt to inject the event bus into the view instance
		try {
			injectField(EventBusField.class, viewInstance, this.eventBus);
		} catch (FieldInjectionException e) {
			// Swallow the exception, its ok if they don't have the event bus
			// field
		}
		// Attempt to inject the presenter factory into the view instance
		try {
			injectField(PresenterFactoryField.class, viewInstance, this);
		} catch (FieldInjectionException e) {
			// Swallow the exception, its ok if they don't have the presenter
			// factory field
		}
		// Register the presenter-view pair to the proper registries
		this.presenterRegistry.register(presenterInstance, null, viewInstance);
		this.eventBus.getRegistry().register(presenterInstance);
		// Fire the initialization methods to begin the life cycle of the
		// respective view and presenter
		viewInstance.onBind();
		((IPresenter<IView>) presenterInstance).onBind(viewInstance);
		// Return the newly created presenter instance
		return (T) presenterInstance;
	}

	@SuppressWarnings("unchecked")
	public <T extends IPresenter<? extends IView>> T create(Class<T> presenterClass, IPresenter<? extends IView> parent) {
		if (presenterClass == null) {
			throw new IllegalArgumentException("The presenterClass parameter cannot be null.");
		}
		if (parent == null) {
			throw new IllegalArgumentException("The parent parameter cannot be null.");
		}
		if (!IPresenter.class.isAssignableFrom(presenterClass)) {
			throw new IllegalArgumentException("The presenterClass parameter must implement " + IPresenter.class.getName() + ".");
		}
		// Attempt to create a new presenter instance
		IPresenter<? extends IView> presenterInstance = null;
		try {
			presenterInstance = presenterClass.newInstance();
		} catch (InstantiationException e) {
			throw new PresenterConstructionException(presenterClass
					+ " could not be instantiated. Presenter implementations must have publicly accessible default constructors.");
		} catch (IllegalAccessException e) {
			throw new InaccessiblePresenterException(e);
		}
		// Attempt to derive the the class of the presenter instance's view
		Class<? extends IView> viewClass = (Class<? extends IView>) getTypeOfGeneric(presenterClass);
		if (viewClass == null) {
			throw new PresenterConstructionException("Could not determine the view class for the corresponding IPresenter sub class " + presenterClass + ".");
		}
		// Attempt to create a new instance of the presenter instance's view via
		// the view factory
		IView viewInstance = this.viewFactory.create(viewClass);
		if (viewInstance == null) {
			throw new ViewConstructionException("Could not create a new instance of view class " + viewClass + ". This is the result of the view factory returning null.");
		}
		// Attempt to inject the event bus into the view instance
		try {
			injectField(EventBusField.class, viewInstance, this.eventBus);
		} catch (FieldInjectionException e) {
			// Swallow the exception, its ok if they don't have the event bus
			// field
		}
		// Attempt to inject the presenter factory into the view instance
		try {
			injectField(PresenterFactoryField.class, viewInstance, this);
		} catch (FieldInjectionException e) {
			// Swallow the exception, its ok if they don't have the presenter
			// factory field
		}
		// Register the presenter-view pair to the presenter registry
		this.presenterRegistry.register(presenterInstance, parent, viewInstance);
		// Fire the initialization methods to begin the life cycle of the
		// respective view and presenter
		viewInstance.onBind();
		((IPresenter<IView>) presenterInstance).onBind(viewInstance);
		// Return the newly created presenter instance
		return (T) presenterInstance;
	}

	private void injectField(Class<? extends Annotation> annotationType, Object instance, Object fieldValue) {
		if (annotationType == null) {
			throw new IllegalArgumentException("The annotationType parameter cannot be null.");
		}
		if (instance == null) {
			throw new IllegalArgumentException("The instance parameter cannot be null.");
		}
		if (fieldValue == null) {
			throw new IllegalArgumentException("The fieldValue parameter cannot be null.");
		}

		Class<?> targetClass = instance.getClass();
		while (!Object.class.equals(targetClass)) {
			Field[] fields = targetClass.getDeclaredFields();
			for (Field field : fields) {
				if (field.getAnnotation(annotationType) != null) {
					try {
						field.setAccessible(true);
						field.set(instance, fieldValue);
					} catch (SecurityException e) {
						throw new FieldInjectionException("Could not inject " + fieldValue + " into " + field + ". The field was inaccessible.", e);
					} catch (IllegalArgumentException e) {
						throw new FieldInjectionException("Could not inject " + fieldValue + " into " + field
								+ ". The intended field value was incompatible with the field.", e);
					} catch (IllegalAccessException e) {
						throw new FieldInjectionException("Could not inject " + fieldValue + " into " + field + ". The field was inaccessible.", e);
					}
				}
			}
			targetClass = targetClass.getSuperclass();
		}

		throw new FieldInjectionException("No field in the class " + instance.getClass() + " was marked with the " + annotationType
				+ " annotation. A field with this annotation is a requirement for injection to be successful.");
	}

	private Class<?> getTypeOfGeneric(Class<?> type) {
		return (Class<?>) ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments()[0];
	}

}
