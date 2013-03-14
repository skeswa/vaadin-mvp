package org.pakhama.vaadin.mvp.view;

import java.io.Serializable;

public interface IViewFactory extends Serializable {
	<T extends IView> T create(Class<T> viewClass);
	IViewRegistry getRegistry();
}
