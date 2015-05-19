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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.enums.ABTestScopes;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultABTestServiceTest
{
	@Mock
	private SessionService sessionServiceMock;
	@Mock
	private ABTestCMSComponentContainerModel containerMock;
	@Mock
	private SimpleCMSComponentModel cmsComponentMock1;
	@Mock
	private SimpleCMSComponentModel cmsComponentMock2;
	@Mock
	private List<SimpleCMSComponentModel> cmsComponents;
	private PK pkStub;
	private DefaultABTestService abTestService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		abTestService = new DefaultABTestService();
		abTestService.setSessionService(sessionServiceMock);

		cmsComponents = new ArrayList<SimpleCMSComponentModel>();
		cmsComponents.add(cmsComponentMock1);
		cmsComponents.add(cmsComponentMock2);

		pkStub = PK.parse("1234567");
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultABTestService#getRandomCmsComponent(de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel)}
	 * .
	 */
	@Test
	public void shouldReturnNullWhenSimpleCMSComponentsAreNull()
	{
		// given
		given(containerMock.getSimpleCMSComponents()).willReturn(null);

		// when
		final SimpleCMSComponentModel cmsComponent = abTestService.getRandomCmsComponent(containerMock);

		// then
		assertThat(cmsComponent).isNull();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultABTestService#getRandomCmsComponent(de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel)}
	 * .
	 */
	@Test
	public void shouldReturnNullWhenSimpleCMSComponentsAreEmpty()
	{
		// given
		given(containerMock.getSimpleCMSComponents()).willReturn(Collections.EMPTY_LIST);

		// when
		final SimpleCMSComponentModel cmsComponent = abTestService.getRandomCmsComponent(containerMock);

		// then
		assertThat(cmsComponent).isNull();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultABTestService#getRandomCmsComponent(de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel)}
	 * .
	 */
	@Test
	public void shouldReturnComponentWithoutTouchingSessionWhenScopeIsRequest()
	{
		// given
		given(containerMock.getScope()).willReturn(ABTestScopes.REQUEST);
		given(containerMock.getSimpleCMSComponents()).willReturn(cmsComponents);

		// when
		final SimpleCMSComponentModel randomCmsComponent = abTestService.getRandomCmsComponent(containerMock);

		// then
		assertThat(randomCmsComponent).isNotNull();
		assertThat(cmsComponents).contains(randomCmsComponent);

		verify(sessionServiceMock, never()).getAttribute(anyString());
		verify(containerMock, never()).getPk();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultABTestService#getRandomCmsComponent(de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel)}
	 * .
	 */
	@Test
	public void shouldReturnComponentWithTouchingSessionWhenScopeIsSession()
	{
		// given
		given(containerMock.getScope()).willReturn(ABTestScopes.SESSION);
		given(containerMock.getSimpleCMSComponents()).willReturn(cmsComponents);
		given(sessionServiceMock.getAttribute(anyString())).willReturn(null);
		given(containerMock.getPk()).willReturn(pkStub);

		// when
		final SimpleCMSComponentModel randomCmsComponent = abTestService.getRandomCmsComponent(containerMock);

		// then
		assertThat(randomCmsComponent).isNotNull();
		assertThat(cmsComponents).contains(randomCmsComponent);

		verify(containerMock, times(1)).getPk();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultABTestService#getRandomCmsComponent(de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel)}
	 * .
	 */
	@Test
	public void shouldReturnZeroIndexComponentWithoutTouchingSessionWhenScopeIsNull()
	{
		// given
		given(containerMock.getScope()).willReturn(null);
		given(containerMock.getSimpleCMSComponents()).willReturn(cmsComponents);

		// when
		final SimpleCMSComponentModel randomCmsComponent = abTestService.getRandomCmsComponent(containerMock);

		// then
		assertThat(randomCmsComponent).isNotNull();
		assertThat(cmsComponents).contains(randomCmsComponent);

		verify(sessionServiceMock, never()).getAttribute(anyString());
		verify(containerMock, never()).getPk();
	}


	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultABTestService#getRandomCMSComponents(de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel)}
	 * .
	 */
	@Test
	public void shouldReturnSingleElementCollectionWhenRandomComponentHasBeenFound()
	{
		// given
		given(containerMock.getScope()).willReturn(ABTestScopes.REQUEST);
		given(containerMock.getSimpleCMSComponents()).willReturn(cmsComponents);

		// when
		final List<SimpleCMSComponentModel> randomCmsComponents = abTestService.getRandomCMSComponents(containerMock);

		// then
		assertThat(randomCmsComponents).isNotEmpty();
		assertThat(randomCmsComponents).hasSize(1);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultABTestService#getRandomCMSComponents(de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel)}
	 * .
	 */
	@Test
	public void shouldReturnEmptyCollectionWhenRandomComponentHasBeenNotFound()
	{
		// given
		given(containerMock.getSimpleCMSComponents()).willReturn(null);

		// when
		final List<SimpleCMSComponentModel> randomCmsComponents = abTestService.getRandomCMSComponents(containerMock);

		// then
		assertThat(randomCmsComponents).isEmpty();
	}
}
