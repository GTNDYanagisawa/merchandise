/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.configurablebundleservices.interceptor.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test that the pick exactly N selection criteria has at least 1 selection
 */
@UnitTest
public class PickExactlyNBundleSelectionCriteriaValidateInterceptorTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private InterceptorContext ctx;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void validateSelectionNumberGreaterThanOrEqual1() throws InterceptorException
	{
		callInterceptor(Integer.valueOf(1));
	}

	/**
	 * @throws InterceptorException
	 * 
	 */
	private void callInterceptor(final Integer n) throws InterceptorException
	{
		final PickExactlyNBundleSelectionCriteriaValidateInterceptor interceptor = new PickExactlyNBundleSelectionCriteriaValidateInterceptor();
		final PickExactlyNBundleSelectionCriteriaModel model = new PickExactlyNBundleSelectionCriteriaModel();
		model.setN(n);

		interceptor.onValidate(model, ctx);
	}

	@Test
	public void validateSelectionNumberLesserThan1() throws InterceptorException
	{
		thrown.expect(InterceptorException.class);
		thrown.expectMessage("number must be greater than or equal to 1");

		callInterceptor(Integer.valueOf(0));

	}
}
