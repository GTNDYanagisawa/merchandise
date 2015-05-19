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

package de.hybris.platform.commercesearch.searchandizing.boost.validators.impl;

import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;


public class CommonBoostValueValidatorTest
{
	CommonBoostValueValidator commonBoostValueValidator;

	@Mock
	protected L10NService l10nService;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		commonBoostValueValidator = new CommonBoostValueValidator();
		commonBoostValueValidator.setL10nService(l10nService);
	}

	@Test
	public void shouldThrowExceptionWhenValueIsEmpty() throws InterceptorException
	{
		//given
		String value = " ";
		when(l10nService.getLocalizedString(CommonBoostValueValidator.VALUE_EMPTY)).thenReturn(CommonBoostValueValidator
				.VALUE_EMPTY);
		//when
		//then
		expectedException.expect(InterceptorException.class);
		expectedException.expectMessage(CommonBoostValueValidator.VALUE_EMPTY);
		commonBoostValueValidator.validate(value);

	}


	@Test
	public void shouldPassValidation() throws InterceptorException
	{
		//given
		String value1 = "value1";
		String value2 = "   value2   ";
		try
		{
			//when
			commonBoostValueValidator.validate(value1);
			commonBoostValueValidator.validate(value2);
		}
		catch (InterceptorException e)
		{
			fail("InterceptorException not expected");
		}

	}
}
