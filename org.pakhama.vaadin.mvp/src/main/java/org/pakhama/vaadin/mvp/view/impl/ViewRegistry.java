package org.pakhama.vaadin.mvp.view.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.pakhama.vaadin.mvp.exception.ViewRegistrationException;
import org.pakhama.vaadin.mvp.view.IView;
import org.pakhama.vaadin.mvp.view.IViewPrioritizer;
import org.pakhama.vaadin.mvp.view.IViewRegistry;

public class ViewRegistry implements IViewRegistry {
	private static final long serialVersionUID = -3658969717696659192L;

	private HashMap<Class<? extends IView>, ArrayList<Class<? extends IView>>> viewCache = new HashMap<Class<? extends IView>, ArrayList<Class<? extends IView>>>();

	@Override
	public void register(Class<? extends IView> viewImplClass) {
		if (viewImplClass == null) {
			throw new IllegalArgumentException("The viewImplClass parameter was null.");
		}
		if (viewImplClass.isInterface()) {
			throw new IllegalArgumentException("The viewImplClass parameter must represent a class implementing an extension of IView, not an interface.");
		}

		Class<? extends IView> viewType = getViewType(viewImplClass);
		if (viewType == null) {
			throw new ViewRegistrationException("Could not register view implementation type " + viewImplClass
					+ " since no valid implemented IView interface could be identified. View implementations must implement an extension of IView.");
		} else if (viewType.equals(IView.class)) {
			throw new ViewRegistrationException("The view implementation type " + viewImplClass
					+ " did not implement an extenstion of IView - it implemented IView directly. View implementations must implement an extension of IView.");
		}

		// Get the existing list of view implementations matching viewType
		ArrayList<Class<? extends IView>> viewImplList = this.viewCache.get(viewType);
		// If its null, create it
		if (viewImplList == null) {
			// Initialize a list to be small - we don't expect that many view
			// impls
			viewImplList = new ArrayList<Class<? extends IView>>(2);
			this.viewCache.put(viewType, viewImplList);
		}
		// Add this new implementation as the last element
		if (!viewImplList.contains(viewImplClass)) {
			viewImplList.add(viewImplClass);
		}
	}

	@Override
	public void unregister(Class<? extends IView> viewImplClass) {
		if (viewImplClass == null) {
			throw new IllegalArgumentException("The viewImplClass parameter was null.");
		}

		Class<? extends IView> viewType = getViewType(viewImplClass);
		if (viewType == null) {
			throw new IllegalArgumentException("No valid impelemented IView interface could be identified to be extended by " + viewImplClass
					+ ". View implementations must implement an extension of IView.");
		} else if (viewType.equals(IView.class)) {
			throw new IllegalArgumentException("The view implementation type " + viewImplClass
					+ " did not implement an extenstion of IView - it implemented IView directly. View implementations must implement an extension of IView.");
		}

		// Get the existing list of view implementations matching viewTypw
		ArrayList<Class<? extends IView>> viewImplList = this.viewCache.get(viewType);
		if (viewImplList != null) {
			viewImplList.remove(viewImplClass);
			if (viewImplList.size() == 0) {
				this.viewCache.remove(viewType);
			}
		}
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
		for (Class<? extends IView> key : this.viewCache.keySet()) {
			if (key != null) {
				ArrayList<Class<? extends IView>> entry = this.viewCache.get(key);
				if (entry != null) {
					entry.clear();
				}
			}
		}
		this.viewCache.clear();
	}

	@Override
	public Class<? extends IView> find(Class<? extends IView> viewClass) {
		if (viewClass == null) {
			throw new IllegalArgumentException("The viewClass parameter was null.");
		}
		if (!viewClass.isInterface()) {
			throw new IllegalArgumentException("The viewClass parameter must represent an interface, not a class.");
		}
		// Get the view implementation list, if its null or empty, return null;
		// otherwise, return the most recently added element
		ArrayList<Class<? extends IView>> viewImplList = this.viewCache.get(viewClass);
		if (viewImplList == null || viewImplList.size() == 0) {
			return null;
		} else {
			return viewImplList.get(viewImplList.size() - 1);
		}
	}

	@Override
	public Class<? extends IView> find(Class<? extends IView> viewClass, IViewPrioritizer viewPrioritizer) {
		if (viewClass == null) {
			throw new IllegalArgumentException("The viewClass parameter was null.");
		}
		if (!viewClass.isInterface()) {
			throw new IllegalArgumentException("The viewClass parameter must represent an interface, not a class.");
		}
		if (viewPrioritizer == null) {
			throw new IllegalArgumentException("The viewPrioritizer parameter was null.");
		}
		// Get the view implementation list, if its null or empty, return null;
		// otherwise, return the view of highest priority
		ArrayList<Class<? extends IView>> viewImplList = this.viewCache.get(viewClass);
		if (viewImplList == null || viewImplList.size() == 0) {
			return null;
		} else {
			// Loops through every view implementation, doing comparisons to
			// find the highest priority one
			Class<? extends IView> highestPriorityViewImpl = null;
			for (Class<? extends IView> viewImpl : viewImplList) {
				if (viewImpl != null) {
					if (highestPriorityViewImpl == null) {
						highestPriorityViewImpl = viewImpl;
					} else {
						if (viewPrioritizer.prioritize(viewImpl, highestPriorityViewImpl)) {
							highestPriorityViewImpl = viewImpl;
						}
					}
				}
			}
			return highestPriorityViewImpl;
		}
	}

	@Override
	public Collection<Class<? extends IView>> findAll(Class<? extends IView> viewClass) {
		if (viewClass == null) {
			throw new IllegalArgumentException("The viewClass parameter was null.");
		}
		if (!viewClass.isInterface()) {
			throw new IllegalArgumentException("The viewClass parameter must represent an interface, not a class.");
		}
		// Get the view implementation list, if its null or empty, return null;
		// otherwise return a copy
		ArrayList<Class<? extends IView>> viewImplList = this.viewCache.get(viewClass);
		if (viewImplList == null) {
			return null;
		} else {
			return new ArrayList<Class<? extends IView>>(viewImplList);
		}
	}

	@Override
	public int size() {
		return this.viewCache.size();
	}

}
