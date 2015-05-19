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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSContentSlotDao;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCMSPageServiceTest
{
	@InjectMocks
	private DefaultCMSPageService cmsPageService;
	@Mock
	private CMSContentSlotDao cmsContentSlotDaoMock;
	@Mock
	private PageTemplateModel pageTemplateModelMock;
	@Mock
	private AbstractPageModel pageModelMock;

	@Before
	public void setUp() throws Exception
	{
		cmsPageService = new DefaultCMSPageService();
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSPageService#getContentSlotsForPageTemplate(de.hybris.platform.cms2.model.pages.PageTemplateModel)}
	 * .
	 */
	@Test
	public void testShouldCallCmsContentSlotDaoAndFindAllContentPagesByCatalogVersion()
	{
		// given
		given(cmsContentSlotDaoMock.findAllContentSlotRelationsForPageTemplate(pageTemplateModelMock)).willReturn(
				Collections.EMPTY_LIST);

		// when
		cmsPageService.getContentSlotsForPageTemplate(pageTemplateModelMock);

		verify(cmsContentSlotDaoMock, times(1)).findAllContentSlotRelationsForPageTemplate(pageTemplateModelMock);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSPageService#getContentSlotsForPageTemplate(de.hybris.platform.cms2.model.pages.PageTemplateModel)}
	 * .
	 */
	@Test
	public void testShouldCallCmsContentSlotDaoAndGetOwnContentSlotsForPage()
	{
		// given
		given(cmsContentSlotDaoMock.findAllContentSlotRelationsByPage(pageModelMock)).willReturn(Collections.EMPTY_LIST);

		// when
		cmsPageService.getOwnContentSlotsForPage(pageModelMock);

		verify(cmsContentSlotDaoMock, times(1)).findAllContentSlotRelationsByPage(pageModelMock);
	}

	@Test
	public void testShouldReturnFrontendTemplateName()
	{
		// given
		given(pageTemplateModelMock.getFrontendTemplateName()).willReturn("FooBar");

		// when
		final String frontendTemplateName = cmsPageService.getFrontendTemplateName(pageTemplateModelMock);

		// then
		assertThat(frontendTemplateName).isEqualTo("FooBar");
		verify(pageTemplateModelMock, times(2)).getFrontendTemplateName();
	}

}
