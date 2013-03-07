package org.pakhama.vaadin.mvp;

import org.pakhama.vaadin.mvp.event.BlueColorChangeEvent;
import org.pakhama.vaadin.mvp.event.ColorChangeEvent;
import org.pakhama.vaadin.mvp.event.GreenColorChangeEvent;
import org.pakhama.vaadin.mvp.event.Listener;
import org.pakhama.vaadin.mvp.event.RedColorChangeEvent;
import org.pakhama.vaadin.mvp.presenter.Presenter;
import org.pakhama.vaadin.mvp.view.ColorSamplerView;
import org.pakhama.vaadin.mvp.view.ColorSamplerViewImpl;

public class ColorSamplePresenter extends Presenter<ColorSamplerView> {
	private static final long serialVersionUID = 6228261391518269110L;
	
	private ColorSampleTestManager testManager;

	@Override
	public void init() {
	}

	@Override
	public Class<? extends ColorSamplerView> view() {
		return ColorSamplerViewImpl.class;
	}

	@Listener(event = ColorChangeEvent.class)
	public void onColorChanged(ColorChangeEvent event) {
		if (testManager != null) {
			testManager.flagColor();
		}
	}
	
	@Listener(event = RedColorChangeEvent.class)
	public void onRed() {
		if (testManager != null) {
			testManager.flagRed();
		}
	}
	
	@Listener(event = GreenColorChangeEvent.class)
	public void onGreen() {
		if (testManager != null) {
			testManager.flagGreen();
		}
	}
	
	@Listener(event = BlueColorChangeEvent.class)
	public void onBlue() {
		if (testManager != null) {
			testManager.flagBlue();
		}
	}

	public ColorSampleTestManager getTestManager() {
		return testManager;
	}

	public void setTestManager(ColorSampleTestManager testManager) {
		this.testManager = testManager;
	}
}
