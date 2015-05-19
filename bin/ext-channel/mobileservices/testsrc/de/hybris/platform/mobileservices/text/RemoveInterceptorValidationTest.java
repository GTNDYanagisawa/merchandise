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
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.mobileservices.enums.MobileMessageStatus;
import de.hybris.platform.mobileservices.jalo.text.MobileActionKeyword;
import de.hybris.platform.mobileservices.model.text.MobileActionAssignmentModel;
import de.hybris.platform.mobileservices.model.text.MobileActionKeywordModel;
import de.hybris.platform.mobileservices.model.text.MobileAggregatorModel;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;
import de.hybris.platform.mobileservices.model.text.MobileReceiveGenericActionModel;
import de.hybris.platform.mobileservices.model.text.MobileShortcodeModel;
import de.hybris.platform.mobileservices.model.text.PhoneNumberListModel;
import de.hybris.platform.mobileservices.model.text.PhoneNumberModel;
import de.hybris.platform.mobileservices.text.validation.ValidationService;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


public class RemoveInterceptorValidationTest extends StatusRecordTestBase
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(RemoveInterceptorValidationTest.class.getName());

	@Resource
	protected FlexibleSearchService flexibleSearchService;

	@Resource
	protected ValidationService mobileValidationService;

	@Before
	public void createConfiguration() throws Exception
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		importCsv("/testdata/testdata13.csv", "UTF-8");
	}

	/* support functions */

	private MobileMessageContextModel prepareData()
	{
		// 1 send a normal message
		final String pk = messageReceived("ES", "ES", "111", "34699111222", "Hi");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsNotDefaultAction);
		// 2 change status after send
		message.setStatus(MobileMessageStatus.PROCESSING);
		modelService.save(message);
		final MobileMessageStatus newStatus = message.getStatus();
		// BAM-390 debug
		if (!MobileMessageStatus.PROCESSING.equals(newStatus))
		{
			dumpBAM390Info(message);
		}
		assertEquals(MobileMessageStatus.PROCESSING, newStatus);
		return message;
	}

	private MobileMessageContextModel prepareDataForShortcodeAssigmentRemoval()
	{
		// 1 send a normal message
		final String pk = messageReceived("ES", "ES", "333", "34699111222", "Hi");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsNotDefaultAction);

		// change status after send
		message.setStatus(MobileMessageStatus.PROCESSING);
		modelService.save(message);
		final MobileMessageStatus newStatus = message.getStatus();
		// BAM-390 debug
		if (!MobileMessageStatus.PROCESSING.equals(newStatus))
		{
			dumpBAM390Info(message);
		}
		assertEquals(MobileMessageStatus.PROCESSING, newStatus);
		return message;
	}


	private MobileMessageContextModel prepareDataForActionTest()
	{
		// 1 send a normal message
		final String pk = messageReceived("ES", "ES", "111", "34699111222", "Hu");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsNotDefaultAction);

		// Delete assigment while we can
		final MobileActionAssignmentModel huAction = findAssignmentByKeyword("hu");
		modelService.remove(huAction);
		// change status after send
		message.setStatus(MobileMessageStatus.PROCESSING);
		modelService.save(message);
		final MobileMessageStatus newStatus = message.getStatus();
		// BAM-390 debug
		if (!MobileMessageStatus.PROCESSING.equals(newStatus))
		{
			dumpBAM390Info(message);
		}
		assertEquals(MobileMessageStatus.PROCESSING, newStatus);
		return message;
	}

	private MobileMessageContextModel prepareDataForShortcodeTest()
	{
		// 1 send a normal message
		final String pk = messageReceived("ES", "ES", "111", "34699111222", "Hi");
		assertNotNull("Message count bot be passed to the reception layer", pk);
		final MobileMessageContextModel message = waitForMessageToBeProcessed(pk);
		assertMessageSuccessfulyProcessed(message, assertSent, assertNoError, assertIsNotDefaultAction);

		// change status after send
		message.setStatus(MobileMessageStatus.PROCESSING);
		// change shortcode to a shortcode that could be deleted, only that now it has a pending sms
		message.setMatchedShortcode(findShortcode("ES", "666"));
		message.setOutgoingShortcode(findShortcode("ES", "666"));
		modelService.save(message);
		final MobileMessageStatus newStatus = message.getStatus();
		// BAM-390 debug
		if (!MobileMessageStatus.PROCESSING.equals(newStatus))
		{
			dumpBAM390Info(message);
		}
		assertEquals(MobileMessageStatus.PROCESSING, newStatus);
		return message;
	}

	private MobileActionAssignmentModel findAssignmentByKeyword(final String keyword)
	{
		final String query =
				"select {m.pk} " +
						"from {" + MobileActionAssignmentModel._TYPECODE + " as m " +
						"JOIN " + MobileActionKeywordModel._TYPECODE + " as k " +
						"ON {m." + MobileActionAssignmentModel.KEYWORD + "} = {k.pk} " +
						"} where {k." + MobileActionKeyword.KEYWORDLOWERCASE + "} = ?key";
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		searchQuery.addQueryParameter("key", keyword.toLowerCase());
		final SearchResult<MobileActionAssignmentModel> results = flexibleSearchService.search(searchQuery);
		if (results.getCount() > 0)
		{
			return results.getResult().get(0);
		}
		return null;
	}

	private MobileActionKeywordModel findKeyword(final String keyword)
	{
		final String query =
				"select {k.pk} " +
						"from {" + MobileActionKeywordModel._TYPECODE + " as k }" +
						"where {k." + MobileActionKeyword.KEYWORDLOWERCASE + "} = ?key";
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		searchQuery.addQueryParameter("key", keyword.toLowerCase());
		final SearchResult<MobileActionKeywordModel> results = flexibleSearchService.search(searchQuery);
		if (results.getCount() > 0)
		{
			return results.getResult().get(0);
		}
		return null;
	}

	private MobileReceiveGenericActionModel findReceiveActionByCode(final String code)
	{
		final String query =
				"select {a.pk} " +
						"from {" + MobileReceiveGenericActionModel._TYPECODE + " as a }" +
						" where {a." + MobileReceiveGenericActionModel.CODE + "} =?code";
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		searchQuery.addQueryParameter("code", code);
		final SearchResult<MobileReceiveGenericActionModel> results = flexibleSearchService.search(searchQuery);
		if (results.getCount() > 0)
		{
			return results.getResult().get(0);
		}
		return null;
	}

	private PhoneNumberListModel findPhoneNumberList(final String code)
	{
		final String query =
				"select {a.pk} " +
						"from {" + PhoneNumberListModel._TYPECODE + " as a }" +
						" where {a." + PhoneNumberListModel.CODE + "} =?code";
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		searchQuery.addQueryParameter("code", code);
		final SearchResult<PhoneNumberListModel> results = flexibleSearchService.search(searchQuery);
		if (results.getCount() > 0)
		{
			return results.getResult().get(0);
		}
		return null;
	}

	private MobileShortcodeModel findShortcode(final String country, final String code)
	{
		final String query =
				"select {s.pk} " +
						"from {" + MobileShortcodeModel._TYPECODE + " as s " +
						"Join " + CountryModel._TYPECODE + " as c  " +
						"on {s." + MobileShortcodeModel.COUNTRY + "} = {c.pk}" +
						"} where {s." + MobileShortcodeModel.CODE + "} =?code " +
						"and {c." + CountryModel.ISOCODE + "} = ?country";
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		searchQuery.addQueryParameter("code", code);
		searchQuery.addQueryParameter("country", country);
		final SearchResult<MobileShortcodeModel> results = flexibleSearchService.search(searchQuery);
		if (results.getCount() > 0)
		{
			return results.getResult().get(0);
		}
		return null;
	}

	private MobileAggregatorModel findAggregator(final String code)
	{
		final String query =
				"select {a.pk} " +
						"from {" + MobileAggregatorModel._TYPECODE + " as a }" +
						" where {a." + MobileAggregatorModel.CODE + "} =?code";
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		searchQuery.addQueryParameter("code", code);
		final SearchResult<MobileAggregatorModel> results = flexibleSearchService.search(searchQuery);
		if (results.getCount() > 0)
		{
			return results.getResult().get(0);
		}
		return null;
	}

	private PhoneNumberModel findPhoneNumber(final String number)
	{
		final String query =
				"select {p.pk} " +
						"from {" + PhoneNumberModel._TYPECODE + " as p }" +
						" where {p." + PhoneNumberModel.NUMBER + "} =?number";
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		searchQuery.addQueryParameter("number", number);
		final SearchResult<PhoneNumberModel> results = flexibleSearchService.search(searchQuery);
		if (results.getCount() > 0)
		{
			return results.getResult().get(0);
		}
		return null;
	}




	/* action assigment deletion tests */

	@Test
	public void testAssignmentDelete()
	{
		final MobileActionAssignmentModel hoAction = findAssignmentByKeyword("ho");
		modelService.remove(hoAction);
	}

	@Test(expected = ModelRemovalException.class)
	public void testAssignmentDeleteInvalid() throws ModelRemovalException
	{
		prepareData();
		final MobileActionAssignmentModel hiAction = findAssignmentByKeyword("hi");
		modelService.remove(hiAction);
	}

	/* receive action delete tests */

	@Test
	public void testActionDelete()
	{
		final MobileReceiveGenericActionModel canBeDeleted = findReceiveActionByCode("test13_2");
		modelService.remove(canBeDeleted);
	}

	@Test(expected = ModelRemovalException.class)
	public void testActionDeleteHasAssigments() throws ModelRemovalException
	{
		final MobileReceiveGenericActionModel cannotBeDeleted = findReceiveActionByCode("test13_1");
		modelService.remove(cannotBeDeleted);
	}

	@Test(expected = ModelRemovalException.class)
	public void testActionDeleteHasShortcode() throws ModelRemovalException
	{
		final MobileReceiveGenericActionModel cannotBeDeleted = findReceiveActionByCode("test13_3");
		modelService.remove(cannotBeDeleted);
	}

	@Test(expected = ModelRemovalException.class)
	public void testActionDeleteHasMessages() throws ModelRemovalException
	{
		prepareDataForActionTest();
		final MobileReceiveGenericActionModel cannotBeDeleted = findReceiveActionByCode("test13_4");
		modelService.remove(cannotBeDeleted);
	}

	/* keyword delete tests */

	@Test
	public void testKeywordDelete()
	{
		final MobileActionKeywordModel keyword = findKeyword("xx");
		modelService.remove(keyword);

	}

	@Test(expected = ModelRemovalException.class)
	public void testKeywordDeleteInvalid()
	{
		final MobileActionKeywordModel keyword = findKeyword("he");
		modelService.remove(keyword);
	}

	/* phone number list tests */

	@Test
	public void testPhoneNumberListDelete()
	{
		final PhoneNumberListModel list = findPhoneNumberList("list1");
		modelService.remove(list);
	}

	@Test(expected = ModelRemovalException.class)
	public void testPhoneNumberListDeleteHasAssigmentBlock()
	{
		final PhoneNumberListModel list = findPhoneNumberList("list2");
		modelService.remove(list);
	}

	@Test(expected = ModelRemovalException.class)
	public void testPhoneNumberListDeleteHasAssigmentTestList()
	{
		final PhoneNumberListModel list = findPhoneNumberList("list3");
		modelService.remove(list);
	}

	@Test(expected = ModelRemovalException.class)
	public void testPhoneNumberListDeleteHasShortcodeBlock()
	{
		final PhoneNumberListModel list = findPhoneNumberList("list4");
		modelService.remove(list);
	}

	@Test(expected = ModelRemovalException.class)
	public void testPhoneNumberListDeleteHasShortcodeTestList()
	{
		final PhoneNumberListModel list = findPhoneNumberList("list5");
		modelService.remove(list);
	}

	/* shortcode deletion tests */

	@Test
	public void testMobileShortcodeDelete()
	{
		final MobileShortcodeModel shortcode = findShortcode("ES", "555");
		modelService.remove(shortcode);
	}

	@Test(expected = ModelRemovalException.class)
	public void testMobileShortcodeDeleteHasMessages()
	{
		prepareDataForShortcodeTest();
		final MobileShortcodeModel shortcode = findShortcode("ES", "666");
		modelService.remove(shortcode);
	}

	@Test
	public void testMobileShortcodeDeleteHasAssigments()
	{
		// Should not fail because assigments are part-of shortcode
		final MobileShortcodeModel shortcode = findShortcode("ES", "333");
		modelService.remove(shortcode);

	}

	public void testMobileShortcodeDeleteHasAssigmentsWhichCannotBeDeletedAsTheyHavePendingMessages()
	{
		final MobileMessageContextModel msg = prepareDataForShortcodeAssigmentRemoval();
		// Should not fail because assigments are part-of shortcode
		final MobileShortcodeModel shortcode = findShortcode("ES", "333");
		assertTrue("message did not belong to shortcode " + shortcode + " matched:" + msg.getMatchedShortcode() + " outgoing:"
				+ msg.getOutgoingShortcode(),
				shortcode.equals(msg.getMatchedShortcode()) || shortcode.equals(msg.getOutgoingShortcode()));
		final List<MobileMessageContextModel> inProgress = mobileValidationService.findInProgress(shortcode);
		assertTrue("message not in progress - found " + inProgress, inProgress.contains(msg));
		//more check and log information added, trying to find the reason for BAM-206
		try
		{
			modelService.remove(shortcode);
			fail("ModelRemovalException has not been thrown (message pending:"
					+ mobileValidationService.findInProgress(shortcode).contains(msg) + ")");
		}
		catch (final ModelRemovalException e)
		{
			// fine
		}
	}

	@Test(expected = ModelRemovalException.class)
	public void testMobileShortcodeDeleteHasReplyRoute()
	{
		final MobileShortcodeModel shortcode = findShortcode("ES", "444");
		modelService.remove(shortcode);
	}

	/* aggregator deletion tests */

	@Test
	public void testMobileAggregatorDelete()
	{
		final MobileAggregatorModel agg = findAggregator("removable");
		modelService.remove(agg);
	}

	@Test(expected = ModelRemovalException.class)
	public void testMobileAggregatorDeleteHasShortcodes()
	{
		final MobileAggregatorModel agg = findAggregator("shortcodes");
		modelService.remove(agg);
	}

	@Test(expected = ModelRemovalException.class)
	public void testMobileAggregatorDeleteHasMessages()
	{
		// Prepares a message with a shortcode belonging to this aggregator
		// the aggregator will not be deleted because it has a shortcode inside
		// and the shortcode won't be deleted because it has a message pending
		// hence probing that it is impossilbe to delete an aggregator with pending 
		// messages, provided that all messages have shortcodes and that all shortcodes
		// belongs to an aggregator
		prepareDataForShortcodeTest();
		final MobileAggregatorModel agg = findAggregator("test13");
		modelService.remove(agg);
	}


	/* phone number deletion tests */

	@Test
	public void testPhoneNumberDelete()
	{
		final PhoneNumberModel num = findPhoneNumber("647000000");
		modelService.remove(num);
	}

	/* as said by hybris arch, phone number should be deleted, even if they are used in lists */
	@Test
	public void testPhoneNumberDeleteInList()
	{
		final PhoneNumberModel num = findPhoneNumber("647000001");
		modelService.remove(num);
	}
}
