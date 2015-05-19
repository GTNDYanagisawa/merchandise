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


public class DateBoostValueValidatorTest
{
	DateBoostValueValidator dateBoostValueValidator;

	@Mock
	protected L10NService l10nService;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		dateBoostValueValidator = new DateBoostValueValidator();
		dateBoostValueValidator.setL10nService(l10nService);
	}

	@Test
	public void shouldThrowExceptionWhenValueIsNotParsabeBoolean() throws InterceptorException
	{
		//given
		String value = "today";
		when(l10nService.getLocalizedString(DateBoostValueValidator.NOT_PARSABLE, new String[]
		{ value })).thenReturn(DateBoostValueValidator.NOT_PARSABLE);
		//when
		//then
		expectedException.expect(InterceptorException.class);
		expectedException.expectMessage(DateBoostValueValidator.NOT_PARSABLE);
		dateBoostValueValidator.validate(value);

	}


	@Test
	public void shouldPassValidation() throws InterceptorException
	{
		//given
		String value = "1995-12-31T23:59:59Z";
		try
		{
			//when
			dateBoostValueValidator.validate(value);

		}
		catch (InterceptorException e)
		{
			fail("InterceptorException not expected");
		}
	}
}
