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

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class IntegerBoostValueValidatorTest
{
	IntegerBoostValueValidator positiveIntegerBoostValueValidator;

	@Mock
	protected L10NService l10nService;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		positiveIntegerBoostValueValidator = new IntegerBoostValueValidator();
		positiveIntegerBoostValueValidator.setL10nService(l10nService);


	}

	@Test
	public void shouldThrowExceptionWhenValueIsNotParsabeInteger() throws InterceptorException
	{
		//given
		String value = "1.1";
		when(l10nService.getLocalizedString(IntegerBoostValueValidator.NOT_PARSABLE, new String[]
		{ value })).thenReturn(IntegerBoostValueValidator.NOT_PARSABLE);

		//when
		//then
		expectedException.expect(InterceptorException.class);
		expectedException.expectMessage(IntegerBoostValueValidator.NOT_PARSABLE);
		positiveIntegerBoostValueValidator.validate(value);
	}

	@Test
	public void shouldPassValidation()
	{
		//given
		String value = Integer.toString(Integer.MAX_VALUE);
		try
		{
			//when
			positiveIntegerBoostValueValidator.validate(value);
		}
		catch (InterceptorException e)
		{
			fail("InterceptorException not expected");
		}
	}
}
