package org.pakhama.vaadin.mvp.view.impl;

import java.util.HashMap;

import org.pakhama.vaadin.mvp.exception.ViewRegistrationException;
import org.pakhama.vaadin.mvp.view.IView;
import org.pakhama.vaadin.mvp.view.IViewRegistry;

public class ViewRegistry implements IViewRegistry {
	private static final long serialVersionUID = -3658969717696659192L;
	
	private HashMap<Class<? extends IView>, Class<? extends IView>> viewCache = new HashMap<Class<? extends IView>, Class<? extends IView>>();

	@Override
	public void register(Class<? extends IView> viewImplClass) {
		if (viewImplClass == null) {
			throw new IllegalArgumentException("The viewImplClass parameter was null.");
		}

		Class<? extends IView> viewType = getViewType(viewImplClass);
		if (viewType == null) {
			throw new ViewRegistrationException("Could not register view implementation type " + viewImplClass
					+ " since no valid impelemented IView interface could be identified. View implementations must implement an extension of IView.");
		}

		this.viewCache.put(viewType, viewImplClass);
	}

	@Override
	public void unregister(Class<? extends IView> viewImplClass) {
		if (viewImplClass == null) {
			throw new IllegalArgumentException("The viewImplClass parameter was null.");
		}
		
		this.viewCache.put(viewImplClass, viewImplClass);
	}

	@SuppressWarnings("unchecked")
	private Class<? extends IView> getViewType(Class<? extends IView> viewImplClass) {
		Class<?>[] interfaceClasses = viewImplClass.getInterfaces();
		for (Class<?> interfaceClass : interfaceClasses) {
			if (interfaceClass != null && IView.class.isAssignableFrom(interfaceClass)) {
				return (Class<? extends IView>) interfaceClass;
			}
		}
		return null;
	}

	@Override
	public void clear() {
		this.viewCache.clear();
	}

	@Override
	public Class<? extends IView> find(Class<? extends IView> viewClass) {
		if (viewClass == null) {
			throw new IllegalArgumentException("The viewClass parameter was null.");
		}
		return this.viewCache.get(viewClass);
	}
}
