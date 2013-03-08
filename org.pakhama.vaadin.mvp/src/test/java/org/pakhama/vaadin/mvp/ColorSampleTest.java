package org.pakhama.vaadin.mvp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pakhama.vaadin.mvp.event.EventBusFactory;
import org.pakhama.vaadin.mvp.presenter.PresenterFactory;

public class ColorSampleTest {
	private EventBusFactory eventBusFactory;
	private PresenterFactory presenterFactory;
	private ColorSampleTestManager manager;

	@Before
	public void setUp() throws Exception {
		this.eventBusFactory = new EventBusFactory();
		this.presenterFactory = new PresenterFactory(this.eventBusFactory.create());
		this.manager = new ColorSampleTestManager();
	}

	@After
	public void tearDown() throws Exception {
		this.eventBusFactory = null;
		this.presenterFactory = null;
		this.manager = null;
	}

	@Test
	public void test() {
		ColorSamplePresenter presenter = this.presenterFactory.create(ColorSamplePresenter.class);
		OtherPresenter otherPresenter = this.presenterFactory.create(OtherPresenter.class);
		
		otherPresenter.setTestManager(this.manager);
		presenter.setTestManager(this.manager);
		presenter.getView().trigger();
		
		Assert.assertTrue(this.manager.isBlueFlagged());
		Assert.assertFalse(this.manager.isGreenFlagged());
		Assert.assertTrue(this.manager.isRedFlagged());
		Assert.assertTrue(this.manager.isOtherColorFlagged());
		Assert.assertFalse(this.manager.isOtherRedFlagged());
		
		this.manager.unflag();
		
		presenter.createChildren();
		presenter.triggerTrigger();
		Assert.assertFalse(this.manager.isFirstChildFlagged());
		Assert.assertTrue(this.manager.isSecondChildFlagged());
		Assert.assertTrue(this.manager.isThirdChildFlagged());
		Assert.assertFalse(this.manager.isBlueFlagged());
		Assert.assertFalse(this.manager.isGreenFlagged());
		Assert.assertFalse(this.manager.isRedFlagged());
		Assert.assertFalse(this.manager.isOtherColorFlagged());
		Assert.assertFalse(this.manager.isOtherRedFlagged());
		
		this.manager.unflag();
		
		presenter.triggerTriggerTrigger();
		Assert.assertTrue(this.manager.isFirstChildFlagged());
		Assert.assertTrue(this.manager.isSecondChildFlagged());
		Assert.assertTrue(this.manager.isThirdChildFlagged());
		Assert.assertFalse(this.manager.isBlueFlagged());
		Assert.assertFalse(this.manager.isGreenFlagged());
		Assert.assertFalse(this.manager.isRedFlagged());
		Assert.assertFalse(this.manager.isOtherColorFlagged());
		Assert.assertFalse(this.manager.isOtherRedFlagged());
	}
}
