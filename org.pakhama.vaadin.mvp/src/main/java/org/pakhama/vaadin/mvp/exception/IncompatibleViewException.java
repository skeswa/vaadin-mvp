package org.pakhama.vaadin.mvp.exception;

import org.pakhama.vaadin.mvp.presenter.Presenter;
import org.pakhama.vaadin.mvp.view.View;

public class IncompatibleViewException extends RuntimeException {
	private static final long serialVersionUID = -6778323253404048450L;
	
	private String message;
	
	public IncompatibleViewException(View incompatibleView, Presenter<? extends View> presenter) {
		this.message = "View type " + incompatibleView.getClass() + " is not compatible with presenter type " + presenter.getClass() + ".";
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
