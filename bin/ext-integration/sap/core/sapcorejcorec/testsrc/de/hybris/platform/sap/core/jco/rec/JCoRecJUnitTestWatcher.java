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
 *
 * 
 */
package de.hybris.platform.sap.core.jco.rec;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;


/**
 * Testwatcher for class SapcoreJCoRecJUnitTest. <br>
 * 
 */
public class JCoRecJUnitTestWatcher extends TestWatcher
{

	/**
	 * Parent JUnit Test.
	 */
	private SapcoreJCoRecJUnitTest parent = null;

	/**
	 * @param parent
	 *           parent JUnitTest in order to inform if test has failed.
	 */
	public JCoRecJUnitTestWatcher(final SapcoreJCoRecJUnitTest parent)
	{
		this.parent = parent;
	}

	@Override
	protected void failed(final Throwable e, final Description description)
	{
		parent.testFailed();
		super.failed(e, description);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.rules.TestWatcher#succeeded(org.junit.runner.Description)
	 */
	@Override
	protected void succeeded(final Description description)
	{
		parent.testSucceeded();
		super.succeeded(description);
	}

}
