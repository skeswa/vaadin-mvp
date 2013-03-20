package org.pakhama.vaadin.mvp.event;

import junit.framework.Assert;

import org.junit.Test;
import org.pakhama.vaadin.mvp.event.impl.EventDelegateRegistry;
import org.pakhama.vaadin.mvp.event.one.OneEventHandler;

public class EventDelegateRegistryTest {

	@Test
	public void testRegister() {
		IEventDelegateRegistry delegateRegistry = new EventDelegateRegistry();
		Assert.assertTrue(delegateRegistry.size() == 0);
		delegateRegistry.register(new OneEventHandler());
		Assert.assertTrue(delegateRegistry.size() == 1);
		delegateRegistry.register(new OneEventHandler());
		Assert.assertTrue(delegateRegistry.size() == 2);
		delegateRegistry.register(new OneEventHandler());
		Assert.assertTrue(delegateRegistry.size() == 3);
	}
	
	@Test
	public void testUnregister() {
		IEventDelegateRegistry delegateRegistry = new EventDelegateRegistry();
		
		OneEventHandler oneHandler1 = new OneEventHandler();
		OneEventHandler oneHandler2 = new OneEventHandler();
		OneEventHandler oneHandler3 = new OneEventHandler();
		
		Assert.assertTrue(delegateRegistry.size() == 0);
		delegateRegistry.register(oneHandler1);
		Assert.assertTrue(delegateRegistry.size() == 1);
		delegateRegistry.register(oneHandler2);
		Assert.assertTrue(delegateRegistry.size() == 2);
		delegateRegistry.register(oneHandler3);
		Assert.assertTrue(delegateRegistry.size() == 3);
		delegateRegistry.unregister(oneHandler3);
		Assert.assertTrue(delegateRegistry.size() == 2);
		delegateRegistry.unregister(oneHandler2);
		Assert.assertTrue(delegateRegistry.size() == 1);
		delegateRegistry.unregister(oneHandler1);
		Assert.assertTrue(delegateRegistry.size() == 0);
	}
	
	@Test
	public void testFind() {
	}
	
	@Test
	public void testFindWithHandlers() {
	}
}
