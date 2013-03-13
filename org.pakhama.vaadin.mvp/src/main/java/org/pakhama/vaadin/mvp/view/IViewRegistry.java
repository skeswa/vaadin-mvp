package org.pakhama.vaadin.mvp.view;

public interface IViewRegistry {
	void register(Class<? extends IView> viewImplClass);
	void unregister(Class<? extends IView> viewImplClass);
	void clear();
	
	Class<? extends IView> find(Class<? extends IView> viewClass);
}
