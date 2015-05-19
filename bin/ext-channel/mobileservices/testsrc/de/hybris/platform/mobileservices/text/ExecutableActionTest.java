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
 *
 *  
 */
package de.hybris.platform.mobileservices.text;

import static junit.framework.Assert.assertEquals;

import de.hybris.platform.deeplink.model.rules.DeeplinkUrlModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.mobileservices.enums.MobileMessageStatus;
import de.hybris.platform.mobileservices.model.text.MobileSendItemLinkActionModel;
import de.hybris.platform.mobileservices.model.text.MobileSendLinkActionModel;
import de.hybris.platform.mobileservices.model.text.MobileSendTextActionModel;
import de.hybris.platform.servicelayer.action.ActionService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.model.action.AbstractActionModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


public class ExecutableActionTest extends StatusRecordTestBase
{
	private static final Logger LOG = Logger.getLogger(ExecutableActionTest.class.getName()); //NOPMD

	@Resource
	private ActionService actionService;

	@Resource
	private ModelService modelService;

	@Resource
	FlexibleSearchService flexibleSearchService;


	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createHardwareCatalog();
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		importCsv("/testdata/testdata12.csv", "UTF-8");

		try
		{

			final DeeplinkUrlModel test = modelService.create(DeeplinkUrlModel.class);
			test.setBaseUrl("http://localhost:9001/mobile");
			test.setCode(Config.getString("mobile.deeplinkurl.defaultCampaignCode", "mobile"));
			test.setName(Config.getString("mobile.deeplinkurl.defaultCampaignCode", "mobile"));
			modelService.save(test);
		}
		catch (final ModelSavingException e)
		{
			// Catch already exists
		}


	}

	private AbstractActionModel getAction(final String code)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				"select {a.pk} from {AbstractAction as a} where {a.code} = ?code");
		flexibleSearchQuery.addQueryParameter("code", code);
		final SearchResult<AbstractActionModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
		final List<AbstractActionModel> matches = searchResult.getResult();
		assertEquals(1, matches.size());
		return matches.get(0);
	}

	@Test
	public void testText()
	{
		final MobileSendTextActionModel text = (MobileSendTextActionModel) getAction("sampleTextMessage");
		final MobileActionParameters params = new MobileActionParameters("7872248319");
		params.setCountryIsocode("GB");
		startMonitor();
		actionService.prepareAndTriggerAction(text, params);
		assertEquals("Message for action " + text.getCode() + " status mismatch", endMonitor(), MobileMessageStatus.SENT);
	}

	@Test
	public void testTextVar()
	{
		final MobileSendTextActionModel text = (MobileSendTextActionModel) getAction("sampleTextMessage");
		final Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("name", "mr tester");
		final MobileActionParameters params = new MobileActionParameters("7872248319", vars);
		params.setCountryIsocode("GB");
		startMonitor();
		actionService.prepareAndTriggerAction(text, params);
		assertEquals("Message for action " + text.getCode() + " status mismatch", endMonitor(), MobileMessageStatus.SENT);
	}

	@Test
	public void testTextLangVar()
	{
		final MobileSendTextActionModel text = (MobileSendTextActionModel) getAction("sampleTextMessage");
		final Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("name", "mr tester");
		final MobileActionParameters params = new MobileActionParameters("7872248319", vars);
		params.setLanguageIsocode("de");
		params.setCountryIsocode("GB");
		startMonitor();
		actionService.prepareAndTriggerAction(text, params);
		assertEquals("Message for action " + text.getCode() + " status mismatch", endMonitor(), MobileMessageStatus.SENT);
	}

	@Test
	public void testLink()
	{
		final MobileSendLinkActionModel text = (MobileSendLinkActionModel) getAction("sampleLinkMessage");
		final MobileActionParameters params = new MobileActionParameters("7872248319");
		params.setCountryIsocode("GB");
		startMonitor();
		actionService.prepareAndTriggerAction(text, params);
		assertEquals("Message for action " + text.getCode() + " status mismatch", endMonitor(), MobileMessageStatus.SENT);
	}

	@Test
	public void testItemLink()
	{
		final MobileSendItemLinkActionModel text = (MobileSendItemLinkActionModel) getAction("sampleItemLinkMessage");
		final MobileActionParameters params = new MobileActionParameters("7872248319");
		params.setCountryIsocode("GB");
		startMonitor();
		actionService.prepareAndTriggerAction(text, params);
		assertEquals("Message for action " + text.getCode() + " status mismatch", endMonitor(), MobileMessageStatus.SENT);
	}
}
