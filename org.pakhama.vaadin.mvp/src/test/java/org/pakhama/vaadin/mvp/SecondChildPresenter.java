package org.pakhama.vaadin.mvp;

import org.pakhama.vaadin.mvp.event.ColorChangeEvent;
import org.pakhama.vaadin.mvp.event.annotation.Listener;
import org.pakhama.vaadin.mvp.presenter.Presenter;
import org.pakhama.vaadin.mvp.view.IOtherView;
import org.pakhama.vaadin.mvp.view.OtherView;

public class SecondChildPresenter extends Presenter<IOtherView> {
	private static final long serialVersionUID = -215300368367853025L;
	
	private ColorSampleTestManager testManager;

	@Override
	public void init() {
	}

	@Override
	public Class<? extends IOtherView> view() {
		return OtherView.class;
	}
	
	@EventListener(event = ColorChangeEvent.class)
	public void colorChange() {
		if (testManager != null) {
			testManager.flagSecondChild();
		}
	}

	public ColorSampleTestManager getTestManager() {
		return testManager;
	}

	public void setTestManager(ColorSampleTestManager testManager) {
		this.testManager = testManager;
	}
}
