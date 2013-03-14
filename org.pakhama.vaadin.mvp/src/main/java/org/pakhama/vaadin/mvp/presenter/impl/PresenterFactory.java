package org.pakhama.vaadin.mvp.presenter.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import org.pakhama.vaadin.mvp.annotation.field.EventBusField;
import org.pakhama.vaadin.mvp.annotation.field.ParentPresenterField;
import org.pakhama.vaadin.mvp.annotation.field.PresenterFactoryField;
import org.pakhama.vaadin.mvp.annotation.field.ViewField;
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
			throw new ViewConstructionException("IView implementation " + viewClass
					+ " could not have its required event bus field, annotated with @EventBusField, initialized.", e);
		}
		// Attempt to inject the event bus, view implementation and presenter
		// factory into the presenter instance
		try {
			injectPresenterFields(presenterInstance, viewInstance);
		} catch (FieldInjectionException e) {
			throw new PresenterConstructionException("IPresenter implementation " + presenterClass + " could not have its required fields initialized.", e);
		}
		// Register the presenter-view pair to the presenter registry
		this.presenterRegistry.register(presenterInstance, null, viewInstance);
		// Fire the initialization methods to begin the life cycle of the
		// respective view and presenter
		viewInstance.onBind();
		presenterInstance.onBind();
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
			throw new ViewConstructionException("IView implementation " + viewClass
					+ " could not have its required event bus field, annotated with @EventBusField, initialized.", e);
		}
		// Attempt to inject the event bus, view implementation and presenter
		// factory into the presenter instance
		try {
			injectPresenterFields(presenterInstance, viewInstance);
		} catch (FieldInjectionException e) {
			throw new PresenterConstructionException("IPresenter implementation " + presenterClass + " could not have its required fields initialized.", e);
		}
		// Attempt to inject the parent presenter into the presenter instance
		try {
			injectField(ParentPresenterField.class, presenterInstance, parent);
		} catch (FieldInjectionException e) {
			throw new ViewConstructionException("IPresenter implementation " + presenterClass
					+ " could not have its parent presenter field, annotated with @ParentPresenterField, initialized.", e);
		}
		// Register the presenter-view pair to the presenter registry
		this.presenterRegistry.register(presenterInstance, parent, viewInstance);
		// Fire the initialization methods to begin the life cycle of the
		// respective view and presenter
		viewInstance.onBind();
		presenterInstance.onBind();
		// Return the newly created presenter instance
		return (T) presenterInstance;
	}

	private void injectPresenterFields(IPresenter<? extends IView> presenter, IView view) {
		if (presenter == null) {
			throw new IllegalArgumentException("The presenter parameter cannot be null.");
		}

		boolean viewFound = false, eventBusFound = false, factoryFound = false;

		Class<?> targetClass = presenter.getClass();
		while (!Object.class.equals(targetClass)) {
			Field[] fields = targetClass.getDeclaredFields();
			for (Field field : fields) {
				if (field.getAnnotation(ViewField.class) != null) {
					try {
						field.setAccessible(true);
						field.set(presenter, view);
					} catch (SecurityException e) {
						throw new FieldInjectionException("Could not inject " + view + " into " + field + ". The field was inaccessible.", e);
					} catch (IllegalArgumentException e) {
						throw new FieldInjectionException("Could not inject " + view + " into " + field + ". The intended field value was incompatible with the field.", e);
					} catch (IllegalAccessException e) {
						throw new FieldInjectionException("Could not inject " + view + " into " + field + ". The field was inaccessible.", e);
					}
					viewFound = true;
				} else if (field.getAnnotation(EventBusField.class) != null) {
					try {
						field.setAccessible(true);
						field.set(presenter, this.eventBus);
					} catch (SecurityException e) {
						throw new FieldInjectionException("Could not inject " + this.eventBus + " into " + field + ". The field was inaccessible.", e);
					} catch (IllegalArgumentException e) {
						throw new FieldInjectionException("Could not inject " + this.eventBus + " into " + field
								+ ". The intended field value was incompatible with the field.", e);
					} catch (IllegalAccessException e) {
						throw new FieldInjectionException("Could not inject " + this.eventBus + " into " + field + ". The field was inaccessible.", e);
					}
					eventBusFound = true;
				} else if (field.getAnnotation(PresenterFactoryField.class) != null) {
					try {
						field.setAccessible(true);
						field.set(presenter, this);
					} catch (SecurityException e) {
						throw new FieldInjectionException("Could not inject " + this + " into " + field + ". The field was inaccessible.", e);
					} catch (IllegalArgumentException e) {
						throw new FieldInjectionException("Could not inject " + this + " into " + field + ". The intended field value was incompatible with the field.", e);
					} catch (IllegalAccessException e) {
						throw new FieldInjectionException("Could not inject " + this + " into " + field + ". The field was inaccessible.", e);
					}
					factoryFound = true;
				}

				if (viewFound && eventBusFound && factoryFound) {
					return;
				}
			}
			targetClass = targetClass.getSuperclass();
		}

		throw new FieldInjectionException(
				"Three fields, each respectively marked with the @ViewField, @EventBusField and @PresenterFactoryField annotations, must exist in an IPresenter implementation for it to be successfully created by this factory.");
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
						throw new FieldInjectionException("Could not inject " + fieldValue + " into " + field + ". The intended field value was incompatible with the field.",
								e);
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
