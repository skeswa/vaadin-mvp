package org.pakhama.vaadin.mvp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.pakhama.vaadin.mvp.event.EventTestSuite;
import org.pakhama.vaadin.mvp.presenter.PresenterTestSuite;
import org.pakhama.vaadin.mvp.view.ViewTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ ViewTestSuite.class, PresenterTestSuite.class, EventTestSuite.class })
public class MVPTestSuite {
}
