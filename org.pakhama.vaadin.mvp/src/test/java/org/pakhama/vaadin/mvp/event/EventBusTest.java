package org.pakhama.vaadin.mvp.event;

import junit.framework.Assert;

import org.junit.Test;
import org.pakhama.vaadin.mvp.event.impl.EventBus;
import org.pakhama.vaadin.mvp.event.impl.EventHandlerRegistry;
import org.pakhama.vaadin.mvp.event.two.YetAnotherTwoTestEvent;
import org.pakhama.vaadin.mvp.presenter.IPresenterFactory;
import org.pakhama.vaadin.mvp.presenter.IPresenterRegistry;
import org.pakhama.vaadin.mvp.presenter.impl.PresenterFactory;
import org.pakhama.vaadin.mvp.presenter.impl.registry.PresenterRegistry;
import org.pakhama.vaadin.mvp.presenter.two.AnotherTwoPresenter;
import org.pakhama.vaadin.mvp.presenter.two.TwoPresenter;
import org.pakhama.vaadin.mvp.util.EventTrackerUtil;
import org.pakhama.vaadin.mvp.view.IViewFactory;
import org.pakhama.vaadin.mvp.view.IViewRegistry;
import org.pakhama.vaadin.mvp.view.impl.ViewFactory;
import org.pakhama.vaadin.mvp.view.impl.ViewRegistry;

public class EventBusTest {

	@Test
	public void testBasic() {
		EventTrackerUtil tracker = new EventTrackerUtil();
		IViewRegistry viewRegistry = new ViewRegistry();
		IViewFactory viewFactory = new ViewFactory(viewRegistry);
		IPresenterRegistry presenterRegistry = new PresenterRegistry();
		IEventHandlerRegistry delegateRegistry = new EventHandlerRegistry();
		IEventBus eventBus = new EventBus(delegateRegistry, presenterRegistry);
		IPresenterFactory factory = new PresenterFactory(eventBus, viewFactory, presenterRegistry);

		AnotherTwoPresenter anotherTwoPresenter = factory.create(AnotherTwoPresenter.class);
		anotherTwoPresenter.setEventTracker(tracker);
		TwoPresenter twoPresenter = factory.create(TwoPresenter.class);
		twoPresenter.dispatch(new YetAnotherTwoTestEvent());

		Assert.assertTrue(tracker.yetAnotherTwoReceived);
		Assert.assertTrue(tracker.anotherTwoReceived);
		Assert.assertTrue(tracker.twoReceived);
	}

	@Test
	public void testPropagateUniversal() {
		EventTrackerUtil tracker1 = new EventTrackerUtil();
		EventTrackerUtil tracker2 = new EventTrackerUtil();
		EventTrackerUtil tracker3 = new EventTrackerUtil();

		IViewRegistry viewRegistry = new ViewRegistry();
		IViewFactory viewFactory = new ViewFactory(viewRegistry);
		IPresenterRegistry presenterRegistry = new PresenterRegistry();
		IEventHandlerRegistry delegateRegistry = new EventHandlerRegistry();
		IEventBus eventBus = new EventBus(delegateRegistry, presenterRegistry);
		IPresenterFactory factory = new PresenterFactory(eventBus, viewFactory, presenterRegistry);

		AnotherTwoPresenter anotherTwoPresenter = factory.create(AnotherTwoPresenter.class);
		anotherTwoPresenter.setEventTracker(tracker1);
		anotherTwoPresenter = factory.create(AnotherTwoPresenter.class);
		anotherTwoPresenter.setEventTracker(tracker2);
		anotherTwoPresenter = factory.create(AnotherTwoPresenter.class);
		anotherTwoPresenter.setEventTracker(tracker3);
		anotherTwoPresenter = factory.create(AnotherTwoPresenter.class);
		anotherTwoPresenter.dispatch(new YetAnotherTwoTestEvent());

		Assert.assertTrue(tracker1.yetAnotherTwoReceived);
		Assert.assertTrue(tracker2.yetAnotherTwoReceived);
		Assert.assertTrue(tracker3.yetAnotherTwoReceived);
		Assert.assertTrue(tracker1.anotherTwoReceived);
		Assert.assertTrue(tracker2.anotherTwoReceived);
		Assert.assertTrue(tracker3.anotherTwoReceived);
		Assert.assertTrue(tracker1.twoReceived);
		Assert.assertTrue(tracker2.twoReceived);
		Assert.assertTrue(tracker3.twoReceived);
	}

	@Test
	public void testPropagateParent() {
		// Test a view and a presenter
		EventTrackerUtil tracker1 = new EventTrackerUtil();
		EventTrackerUtil tracker2 = new EventTrackerUtil();

		IViewRegistry viewRegistry = new ViewRegistry();
		IViewFactory viewFactory = new ViewFactory(viewRegistry);
		IPresenterRegistry presenterRegistry = new PresenterRegistry();
		IEventHandlerRegistry delegateRegistry = new EventHandlerRegistry();
		IEventBus eventBus = new EventBus(delegateRegistry, presenterRegistry);
		IPresenterFactory factory = new PresenterFactory(eventBus, viewFactory, presenterRegistry);

		AnotherTwoPresenter anotherTwoPresenter = factory.create(AnotherTwoPresenter.class);
		anotherTwoPresenter.setEventTracker(tracker1);
		anotherTwoPresenter = anotherTwoPresenter.createChild(AnotherTwoPresenter.class);
		anotherTwoPresenter.setEventTracker(tracker2);
		anotherTwoPresenter.dispatch(new YetAnotherTwoTestEvent(), EventScope.PARENT);

		Assert.assertTrue(tracker1.yetAnotherTwoReceived);
		Assert.assertTrue(tracker1.anotherTwoReceived);
		Assert.assertTrue(tracker1.twoReceived);

		anotherTwoPresenter.getView().dispatch(new YetAnotherTwoTestEvent(), EventScope.PARENT);

		Assert.assertTrue(tracker2.yetAnotherTwoReceived);
		Assert.assertTrue(tracker2.anotherTwoReceived);
		Assert.assertTrue(tracker2.twoReceived);
	}

	@Test
	public void testPropagateSiblings() {
		EventTrackerUtil tracker1 = new EventTrackerUtil();
		EventTrackerUtil tracker2 = new EventTrackerUtil();
		EventTrackerUtil tracker3 = new EventTrackerUtil();

		IViewRegistry viewRegistry = new ViewRegistry();
		IViewFactory viewFactory = new ViewFactory(viewRegistry);
		IPresenterRegistry presenterRegistry = new PresenterRegistry();
		IEventHandlerRegistry delegateRegistry = new EventHandlerRegistry();
		IEventBus eventBus = new EventBus(delegateRegistry, presenterRegistry);
		IPresenterFactory factory = new PresenterFactory(eventBus, viewFactory, presenterRegistry);

		AnotherTwoPresenter parentTwoPresenter = factory.create(AnotherTwoPresenter.class);
		parentTwoPresenter.createChild(AnotherTwoPresenter.class).setEventTracker(tracker1);
		parentTwoPresenter.createChild(AnotherTwoPresenter.class).setEventTracker(tracker2);
		AnotherTwoPresenter anotherTwoPresenter = parentTwoPresenter.createChild(AnotherTwoPresenter.class);
		anotherTwoPresenter.setEventTracker(tracker3);
		anotherTwoPresenter.dispatch(new YetAnotherTwoTestEvent(), EventScope.SIBLINGS);

		Assert.assertTrue(tracker1.yetAnotherTwoReceived);
		Assert.assertTrue(tracker1.anotherTwoReceived);
		Assert.assertTrue(tracker1.twoReceived);
		Assert.assertTrue(tracker2.yetAnotherTwoReceived);
		Assert.assertTrue(tracker2.anotherTwoReceived);
		Assert.assertTrue(tracker2.twoReceived);
		Assert.assertFalse(tracker3.yetAnotherTwoReceived);
		Assert.assertFalse(tracker3.anotherTwoReceived);
		Assert.assertFalse(tracker3.twoReceived);
	}

	@Test
	public void testPropagateChildren() {
		EventTrackerUtil tracker1 = new EventTrackerUtil();
		EventTrackerUtil tracker2 = new EventTrackerUtil();
		EventTrackerUtil tracker3 = new EventTrackerUtil();
		EventTrackerUtil tracker4 = new EventTrackerUtil();

		IViewRegistry viewRegistry = new ViewRegistry();
		IViewFactory viewFactory = new ViewFactory(viewRegistry);
		IPresenterRegistry presenterRegistry = new PresenterRegistry();
		IEventHandlerRegistry delegateRegistry = new EventHandlerRegistry();
		IEventBus eventBus = new EventBus(delegateRegistry, presenterRegistry);
		IPresenterFactory factory = new PresenterFactory(eventBus, viewFactory, presenterRegistry);

		AnotherTwoPresenter parentTwoPresenter = factory.create(AnotherTwoPresenter.class);
		parentTwoPresenter.setEventTracker(tracker1);
		parentTwoPresenter.createChild(AnotherTwoPresenter.class).setEventTracker(tracker2);
		parentTwoPresenter.createChild(AnotherTwoPresenter.class).setEventTracker(tracker3);
		parentTwoPresenter.createChild(AnotherTwoPresenter.class).setEventTracker(tracker4);
		parentTwoPresenter.dispatch(new YetAnotherTwoTestEvent(), EventScope.CHILDREN);

		Assert.assertTrue(tracker2.yetAnotherTwoReceived);
		Assert.assertTrue(tracker2.anotherTwoReceived);
		Assert.assertTrue(tracker2.twoReceived);
		Assert.assertTrue(tracker3.yetAnotherTwoReceived);
		Assert.assertTrue(tracker3.anotherTwoReceived);
		Assert.assertTrue(tracker3.twoReceived);
		Assert.assertTrue(tracker4.yetAnotherTwoReceived);
		Assert.assertTrue(tracker4.anotherTwoReceived);
		Assert.assertTrue(tracker4.twoReceived);
		Assert.assertFalse(tracker1.yetAnotherTwoReceived);
		Assert.assertFalse(tracker1.anotherTwoReceived);
		Assert.assertFalse(tracker1.twoReceived);
	}
}
