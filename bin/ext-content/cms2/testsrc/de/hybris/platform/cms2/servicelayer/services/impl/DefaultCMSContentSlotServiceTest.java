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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.ContentSlotNameModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCMSContentSlotServiceTest
{
	@InjectMocks
	private DefaultCMSContentSlotService cmsContentSlotService;
	@Mock
	private AbstractPageModel pageModelMock;
	@Mock
	private CMSPageService cmsPageServiceMock;
	@Mock
	private PageTemplateModel pageTemplateMock;
	@Mock
	private ContentSlotNameModel contentSlotName1;
	@Mock
	private ContentSlotNameModel contentSlotName2;
	@Mock
	private ContentSlotNameModel contentSlotName3;
	@Mock
	private ContentSlotForPageModel contentSlotForPageMock;
	@Mock
	private ContentSlotForTemplateModel contentSlotForTemplateMock;
	private List<ContentSlotNameModel> contentSlotNames;
	private List<ContentSlotForPageModel> contentSlotsForPage;
	private List<ContentSlotForTemplateModel> contentSlotsForPageTemplate;

	@Before
	public void setUp() throws Exception
	{
		cmsContentSlotService = new DefaultCMSContentSlotService();
		MockitoAnnotations.initMocks(this);

		contentSlotNames = new ArrayList<ContentSlotNameModel>();
		contentSlotNames.add(contentSlotName1);
		contentSlotNames.add(contentSlotName2);
		contentSlotNames.add(contentSlotName3);

		contentSlotsForPage = new ArrayList<ContentSlotForPageModel>();
		contentSlotsForPage.add(contentSlotForPageMock);

		contentSlotsForPageTemplate = new ArrayList<ContentSlotForTemplateModel>();
		contentSlotsForPageTemplate.add(contentSlotForTemplateMock);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSContentSlotService#getAvailableContentSlotsNames(de.hybris.platform.cms2.model.pages.AbstractPageModel)}
	 * .
	 */
	@Test
	public void shouldReturnEmptyStringInsteadOfAvailableContentSlotNamesWhenMasterTemplateIsNull()

	{
		// given
		given(pageModelMock.getMasterTemplate()).willReturn(null);

		// when
		final String contentSlotsNames = cmsContentSlotService.getAvailableContentSlotsNames(pageModelMock);

		// then
		assertThat(contentSlotsNames).isEmpty();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSContentSlotService#getAvailableContentSlotsNames(de.hybris.platform.cms2.model.pages.AbstractPageModel)}
	 * .
	 */
	@Test
	public void shouldReturnEmptyStringInsteadOfAvailableContentSlotNamesWhenAvailableContentSlotsIsNull()
	{
		// given
		given(pageModelMock.getMasterTemplate()).willReturn(pageTemplateMock);
		given(pageTemplateMock.getAvailableContentSlots()).willReturn(null);

		// when
		final String contentSlotsNames = cmsContentSlotService.getAvailableContentSlotsNames(pageModelMock);

		// then
		assertThat(contentSlotsNames).isEmpty();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSContentSlotService#getAvailableContentSlotsNames(de.hybris.platform.cms2.model.pages.AbstractPageModel)}
	 * .
	 */
	@Test
	public void shouldReturnEmptyStringInsteadOfAvailableContentSlotNamesWhenAvailableContentSlotsIsEmpty()
	{
		// given
		given(pageModelMock.getMasterTemplate()).willReturn(pageTemplateMock);
		given(pageTemplateMock.getAvailableContentSlots()).willReturn(Collections.EMPTY_LIST);

		// when
		final String contentSlotsNames = cmsContentSlotService.getAvailableContentSlotsNames(pageModelMock);

		// then
		assertThat(contentSlotsNames).isEmpty();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSContentSlotService#getAvailableContentSlotsNames(de.hybris.platform.cms2.model.pages.AbstractPageModel)}
	 * .
	 */
	@Test
	public void shouldReturnAvailableContentSlotNamesForAvailableContentSlotNames()
	{
		// given
		given(pageModelMock.getMasterTemplate()).willReturn(pageTemplateMock);
		given(pageTemplateMock.getAvailableContentSlots()).willReturn(contentSlotNames);
		given(contentSlotName1.getName()).willReturn("FooBar1");
		given(contentSlotName2.getName()).willReturn("FooBar2");
		given(contentSlotName3.getName()).willReturn("FooBar3");

		// when
		final String contentSlotsNames = cmsContentSlotService.getAvailableContentSlotsNames(pageModelMock);

		// then
		assertThat(contentSlotsNames).isNotEmpty();
		assertThat(contentSlotsNames).isEqualTo("FooBar1; FooBar2; FooBar3");
	}

	/******/

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSContentSlotService#getAvailableContentSlotsNames(de.hybris.platform.cms2.model.pages.AbstractPageModel)}
	 * .
	 */
	@Test
	public void shouldReturnEmptyStringInsteadOfMissingContentSlotNamesWhenMasterTemplateIsNull()

	{
		// given
		given(pageModelMock.getMasterTemplate()).willReturn(null);

		// when
		final String contentSlotsNames = cmsContentSlotService.getMissingContentSlotsNames(pageModelMock);

		// then
		assertThat(contentSlotsNames).isEmpty();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSContentSlotService#getAvailableContentSlotsNames(de.hybris.platform.cms2.model.pages.AbstractPageModel)}
	 * .
	 */
	@Test
	public void shouldReturnEmptyStringInsteadOfMissingContentSlotNamesWhenMissingContentSlotsIsNull()
	{
		// given
		given(pageModelMock.getMasterTemplate()).willReturn(pageTemplateMock);
		given(pageTemplateMock.getAvailableContentSlots()).willReturn(null);

		// when
		final String contentSlotsNames = cmsContentSlotService.getMissingContentSlotsNames(pageModelMock);

		// then
		assertThat(contentSlotsNames).isEmpty();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSContentSlotService#getAvailableContentSlotsNames(de.hybris.platform.cms2.model.pages.AbstractPageModel)}
	 * .
	 */
	@Test
	public void shouldReturnEmptyStringInsteadOfMissingContentSlotNamesWhenMissingContentSlotsIsEmpty()
	{
		// given
		given(pageModelMock.getMasterTemplate()).willReturn(pageTemplateMock);
		given(pageTemplateMock.getAvailableContentSlots()).willReturn(Collections.EMPTY_LIST);

		// when
		final String contentSlotsNames = cmsContentSlotService.getMissingContentSlotsNames(pageModelMock);

		// then
		assertThat(contentSlotsNames).isEmpty();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSContentSlotService#getAvailableContentSlotsNames(de.hybris.platform.cms2.model.pages.AbstractPageModel)}
	 * .
	 */
	@Test
	public void shouldReturnMissingContentSlotNamesForMissingContentSlotNames()
	{
		// given
		given(pageModelMock.getMasterTemplate()).willReturn(pageTemplateMock);
		given(pageTemplateMock.getAvailableContentSlots()).willReturn(contentSlotNames);
		given(contentSlotName1.getName()).willReturn("FooBar1");
		given(contentSlotName2.getName()).willReturn("FooBar2");
		given(contentSlotName3.getName()).willReturn("FooBar3");
		given(cmsPageServiceMock.getContentSlotsForPageTemplate(pageTemplateMock)).willReturn(contentSlotsForPageTemplate);
		given(cmsPageServiceMock.getOwnContentSlotsForPage(pageModelMock)).willReturn(contentSlotsForPage);
		given(contentSlotForPageMock.getPosition()).willReturn("FooBar1");
		given(contentSlotForTemplateMock.getPosition()).willReturn("FooBar3");

		// when
		final String contentSlotsNames = cmsContentSlotService.getMissingContentSlotsNames(pageModelMock);

		// then
		assertThat(contentSlotsNames).isNotEmpty();
		assertThat(contentSlotsNames).isEqualTo("FooBar2");
	}

}
