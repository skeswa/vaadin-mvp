package org.pakhama.vaadin.mvp.view;

import org.pakhama.vaadin.mvp.event.EventBus;

import com.vaadin.ui.Component;

public interface View extends Component {
	/**
	 * Binds this view to the same event bus that its presenter was registered
	 * under. The previously mentioned event bus is passed as a parameter
	 * through this method. This method is guaranteed to be called before this
	 * view's <code>init()</code> method.
	 * 
	 * @param eventBus
	 *            the event bus under which this view's presenter is registered
	 */
	void bind(EventBus eventBus);

	/**
	 * Initializes the user interface artifacts so that they are ready to
	 * display data. This method should be used in place of the default
	 * constructor for initialization of the view. Furthermore, this method will
	 * always be invoked before its presenter's <code>init()</code> method.
	 */
	void init();
}
