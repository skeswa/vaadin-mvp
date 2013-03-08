package org.pakhama.vaadin.mvp;

import org.pakhama.vaadin.mvp.event.BlueColorChangeEvent;
import org.pakhama.vaadin.mvp.event.ColorChangeEvent;
import org.pakhama.vaadin.mvp.event.EventScope;
import org.pakhama.vaadin.mvp.event.GreenColorChangeEvent;
import org.pakhama.vaadin.mvp.event.RedColorChangeEvent;
import org.pakhama.vaadin.mvp.event.annotation.Listener;
import org.pakhama.vaadin.mvp.presenter.Presenter;
import org.pakhama.vaadin.mvp.view.ColorSamplerView;
import org.pakhama.vaadin.mvp.view.IColorSamplerView;

public class ColorSamplePresenter extends Presenter<IColorSamplerView> {
	private static final long serialVersionUID = 6228261391518269110L;
	
	private ColorSampleTestManager testManager;
	private FirstChildPresenter first;

	@Override
	public void init() {
	}

	@Override
	public Class<? extends IColorSamplerView> view() {
		return ColorSamplerView.class;
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
	
	public void createChildren() {
		first = (FirstChildPresenter) createChild(FirstChildPresenter.class);
		SecondChildPresenter second = (SecondChildPresenter) createChild(SecondChildPresenter.class);
		ThirdChildPresenter third = (ThirdChildPresenter) createChild(ThirdChildPresenter.class);
		first.setTestManager(testManager);
		second.setTestManager(testManager);
		third.setTestManager(testManager);
	}
	
	public void triggerTrigger() {
		first.triggerTrigger();
	}
	
	public void triggerTriggerTrigger() {
		GreenColorChangeEvent e = new GreenColorChangeEvent(EventScope.CHILDREN);
		fire(e);
	}
}
