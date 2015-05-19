/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */

/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.docu

import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.DocumentationSuiteGuard
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.docu.TestUtilMethodInvEvent.MethodInvEventType
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Collect summary for tests and delegate to strategies which write summary to files
 */
class BaseWSTestWatcher extends TestWatcher implements TestUtilMethodInvListener {

	def  saveStrategies = [new SaveByResourcesWSOutputStrategy(),new DefaultSaveWSOuptutStrategy()]


	@Override
	public void onEvent(TestUtilMethodInvEvent ev) {

		if(summary == null)
			throw new IllegalStateException();

		switch (ev.type) {
			case MethodInvEventType.GET_CONNECTION:
				summary.addRequest(ev.resource, ev.accept, ev.method);
				break;
			case MethodInvEventType.GET_RESPONSE:
				summary.addResponse(ev.response);
				break;
			default:
				break;
		}
	}

	private SummaryOfRunningTest summary = null;

	@Override
	protected void starting(Description desc) {
        if(DocumentationSuiteGuard.isSuiteRunning()) {
			summary = new SummaryOfRunningTest();
			summary.testName = desc.getMethodName();
			TestUtilCustomDelegatingMetaClass.addEventListener(this);
		}
	}

	@Override
	protected void failed(Throwable t, Description desc) {
		if(summary != null) {
			for(SaveWSOutputStrategy saveStrategy:saveStrategies) {
				saveStrategy.saveFailedTest(summary,t)
			}
			TestUtilCustomDelegatingMetaClass.removeEventListener(this);
			summary = null;
		}
	}

	@Override
	protected void succeeded(Description desc) {
		if(summary != null) {
			for(SaveWSOutputStrategy saveStrategy:saveStrategies) {
				saveStrategy.saveSucceededTest(summary);
			}
			TestUtilCustomDelegatingMetaClass.removeEventListener(this);
			summary = null;
		}
	}
}