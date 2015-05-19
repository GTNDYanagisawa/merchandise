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
package de.hybris.platform.cms2.servicelayer.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCMSComponentServiceTest
{
	@Mock
	private AbstractCMSComponentModel componentMock;

	@InjectMocks
	private final CMSComponentService cmsComponentService = new DefaultCMSComponentService();


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService#isCmsComponentRestricted(de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel)}
	 * .
	 */
	@Test
	public void shouldBeNotRestrictedWhenRestrictionsAreNull()
	{
		// given
		given(componentMock.getRestrictions()).willReturn(null);

		// when
		final boolean restricted = cmsComponentService.isComponentRestricted(componentMock);

		// then
		assertThat(restricted).isFalse();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService#isCmsComponentRestricted(de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel)}
	 * .
	 */
	@Test
	public void shouldBeNotRestrictedWhenRestrictionsAreEmptyList()
	{
		// given
		given(componentMock.getRestrictions()).willReturn(Collections.EMPTY_LIST);

		// when
		final boolean restricted = cmsComponentService.isComponentRestricted(componentMock);

		// then
		assertThat(restricted).isFalse();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService#isCmsComponentRestricted(de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel)}
	 * .
	 */
	@Test
	public void shouldBeRestrictedWhenRestrictionsAreNotEmptyList()
	{
		// given
		final List<AbstractRestrictionModel> restrictionsMock = mock(ArrayList.class);
		given(Boolean.valueOf(restrictionsMock.isEmpty())).willReturn(Boolean.FALSE);
		given(componentMock.getRestrictions()).willReturn(restrictionsMock);

		// when
		final boolean restricted = cmsComponentService.isComponentRestricted(componentMock);

		// then
		assertThat(restricted).isTrue();
	}

}
