package org.pakhama.vaadin.mvp.view;

import java.io.Serializable;

public interface IViewRegistry extends Serializable {
	void register(Class<? extends IView> viewImplClass);
	void unregister(Class<? extends IView> viewImplClass);
	void clear();
	
	Class<? extends IView> find(Class<? extends IView> viewClass);
}
