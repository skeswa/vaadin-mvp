package org.pakhama.vaadin.mvp.view;

import com.vaadin.ui.Component;

public interface IView extends Component {
	/**
	 * Initializes the user interface artifacts so that they are ready to
	 * display data. This method should be used in place of the default
	 * constructor for initialization of the view. Furthermore, this method will
	 * always be invoked before its presenter's <code>init()</code> method.
	 */
	void init();
}
