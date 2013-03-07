package org.pakhama.vaadin.mvp;

import org.pakhama.vaadin.mvp.event.ColorChangeEvent;
import org.pakhama.vaadin.mvp.event.Listener;
import org.pakhama.vaadin.mvp.event.RedColorChangeEvent;
import org.pakhama.vaadin.mvp.presenter.Presenter;
import org.pakhama.vaadin.mvp.view.IOtherView;
import org.pakhama.vaadin.mvp.view.OtherView;

public class OtherPresenter extends Presenter<IOtherView> {
	private static final long serialVersionUID = -215300368367853025L;
	
	private ColorSampleTestManager testManager;

	@Override
	public void init() {
	}

	@Override
	public Class<? extends IOtherView> view() {
		return OtherView.class;
	}
	
	@Listener(event = ColorChangeEvent.class)
	public void colorChange() {
		if (testManager != null) {
			testManager.flagOtherColor();
		}
	}
	
	@Listener(event = RedColorChangeEvent.class)
	public void red() {
		if (testManager != null) {
			testManager.flagOtherRed();
		}
	}

	public ColorSampleTestManager getTestManager() {
		return testManager;
	}

	public void setTestManager(ColorSampleTestManager testManager) {
		this.testManager = testManager;
	}
}
