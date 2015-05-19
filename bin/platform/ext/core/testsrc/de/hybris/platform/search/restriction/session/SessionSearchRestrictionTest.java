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
package de.hybris.platform.search.restriction.session;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SessionSearchRestrictionTest
{
	private static final String BLANK_STRING = "";
	private static final String RESTRICTION_QUERY = "some query";
	private static final String RESTRICTION_CODE = "some code";

	@Mock
	private ComposedTypeModel restrictedType;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenCodeIsBlank()
	{
		new SessionSearchRestriction(BLANK_STRING, RESTRICTION_QUERY, restrictedType);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenCodeIsNull()
	{
		new SessionSearchRestriction(null, RESTRICTION_QUERY, restrictedType);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenQueryIsBlank()
	{
		new SessionSearchRestriction(RESTRICTION_CODE, BLANK_STRING, restrictedType);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenQueryIsNull()
	{
		new SessionSearchRestriction(RESTRICTION_CODE, null, restrictedType);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenRestrictedTypeIsNull()
	{
		new SessionSearchRestriction(RESTRICTION_CODE, RESTRICTION_QUERY, null);
	}

	@Test
	public void shouldCreateSessionSearchRestrictionObject()
	{
		// when
		final SessionSearchRestriction sessionSearchRestriction = new SessionSearchRestriction(RESTRICTION_CODE, RESTRICTION_QUERY,
				restrictedType);

		// then
		assertThat(sessionSearchRestriction.getCode()).isEqualTo(RESTRICTION_CODE);
		assertThat(sessionSearchRestriction.getQuery()).isEqualTo(RESTRICTION_QUERY);
		assertThat(sessionSearchRestriction.getRestrictedType()).isEqualTo(restrictedType);
	}
}
