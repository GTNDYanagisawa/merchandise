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
package de.hybris.platform.mcc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Constants;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.mcc.enums.CockpitLinkArea;
import de.hybris.platform.mcc.model.AbstractLinkEntryModel;
import de.hybris.platform.mcc.model.DividerModel;
import de.hybris.platform.mcc.model.DynamicLinkModel;
import de.hybris.platform.mcc.model.StaticLinkModel;
import de.hybris.platform.mcc.services.LinkEntryService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class LinkEntriesTest extends ServicelayerTransactionalTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private LinkEntryService linkEntryService;

	@Resource
	private I18NService i18nService;

	@Resource
	private UserService userService;


	@Before
	public void setUp() throws Exception
	{
		//linkEntryService = (LinkEntryService) Registry.getApplicationContext().getBean("linkEntryService");
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		importCsv("/mcc/import/testdata_mcc.csv", "utf-8");
	}


	@Test
	public void testGetAllLinks()
	{
		final List<AbstractLinkEntryModel> links = linkEntryService.getActiveLinkEntries("platform", false);
		assertTrue("Four links expexted, got " + links.size(), (links.size() == 4));
	}


	@Test
	public void testLinkDetails()
	{
		final List<AbstractLinkEntryModel> links = linkEntryService.getActiveLinkEntries("platform", true);
		assertTrue("Four links expexted, got " + links.size(), (links.size() == 2));
		for (final AbstractLinkEntryModel link : links)
		{
			if ("test_baselink".equals(link.getCode()))
			{
				assertTrue("test_baselink expected to be StaticLink", (link instanceof StaticLinkModel));
				assertTrue("Two sublinks expected for test_baselink", (((StaticLinkModel) link).getSublinks().size() == 2));
				assertFalse("test_baselink should not be visible for anonymous user", linkEntryService.isVisible(link));
			}
			else if ("test_adminDivider".equals(link.getCode()))
			{
				assertTrue("test_adminDivider expected to be Divider", (link instanceof DividerModel));
				assertTrue("test_adminDivider should have one read principal", link.getReadPrincipals().size() == 1);
				assertTrue("test_adminDivider should have 'customergroup' as read principal",
						Constants.USER.CUSTOMER_USERGROUP.equals(link.getReadPrincipals().get(0).getUid()));
				assertTrue("test_adminDivider should be visible to anonymous user", linkEntryService.isVisible(link));
			}
		}
	}


	@Test
	public void testDynamicLink()
	{
		final DynamicLinkModel dynamicLink = modelService.create(DynamicLinkModel.class);
		dynamicLink.setArea(CockpitLinkArea.PLATFORM);
		dynamicLink.setCode("test_dynamiclink");
		dynamicLink.setExtensionName("hac");
		final List<PrincipalModel> principals = new ArrayList<PrincipalModel>();
		principals.add(userService.getUserGroupForUID(Constants.USER.CUSTOMER_USERGROUP));
		dynamicLink.setReadPrincipals(principals);
		dynamicLink.setSortorder(Integer.valueOf(5));
		final LanguageModel enModel = i18nService.getLanguage("en");
		final Locale loc = new Locale(enModel.getIsocode());
		dynamicLink.setTitle("New dynamic link...", loc);
		dynamicLink.setUrl("{hac}");

		modelService.save(dynamicLink);
		assertThat("DynamicLinkModel was not saved successfully", dynamicLink.getPk(), is(notNullValue()));

		final List<AbstractLinkEntryModel> links = linkEntryService.getActiveLinkEntries("platform", true);
		assertTrue("Five links expexted, got " + links.size(), (links.size() == 3));
		for (final AbstractLinkEntryModel link : links)
		{
			if ("test_dynamiclink".equals(link.getCode()))
			{
				final DynamicLinkModel dlinkModel = (DynamicLinkModel) link;
				dlinkModel.setVisibleScript("return java.lang.Boolean.TRUE;");
				dlinkModel
						.setScript("java.util.Map links = new java.util.HashMap(); links.put(\"Title 1\", \"link1\"); return links;");
				modelService.save(dlinkModel);

				assertTrue("Dynamic link should be visible at this moment", linkEntryService.isVisible(dlinkModel));

				dlinkModel.setVisibleScript("return java.lang.Boolean.FALSE;");
				modelService.save(dlinkModel);
				assertFalse("Dynamic link should not be visible at this moment", linkEntryService.isVisible(dlinkModel));

				final Map<String, String> dynamicSublinks = linkEntryService.getDynamicLinks(dlinkModel.getScript());
				assertTrue("Dynamic link's script should return one sublink", dynamicSublinks.size() == 1);
				assertTrue("Dynamic link's sublink should have title 'Title 1'", dynamicSublinks.containsKey("Title 1"));
				assertTrue("Dynamic link's sublink should have URL 'link1'", dynamicSublinks.containsValue("link1"));
			}
		}

		modelService.remove(dynamicLink);
		final List<AbstractLinkEntryModel> newLinks = linkEntryService.getActiveLinkEntries("platform", true);
		assertTrue("Four links expexted, got " + newLinks.size(), (newLinks.size() == 2));
	}

}
