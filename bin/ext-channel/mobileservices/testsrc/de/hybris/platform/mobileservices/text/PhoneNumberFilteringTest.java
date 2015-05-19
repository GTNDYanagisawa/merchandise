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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.mobileservices.enums.EnginesType;
import de.hybris.platform.mobileservices.enums.MessageType;
import de.hybris.platform.mobileservices.enums.MobileActionAssignmentStateType;
import de.hybris.platform.mobileservices.enums.MobileMessageError;
import de.hybris.platform.mobileservices.enums.MobileMessageStatus;
import de.hybris.platform.mobileservices.enums.PhoneNumberFormat;
import de.hybris.platform.mobileservices.model.text.MobileActionAssignmentModel;
import de.hybris.platform.mobileservices.model.text.MobileActionKeywordModel;
import de.hybris.platform.mobileservices.model.text.MobileAggregatorModel;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;
import de.hybris.platform.mobileservices.model.text.MobileReceiveAndSendTextActionModel;
import de.hybris.platform.mobileservices.model.text.MobileShortcodeModel;
import de.hybris.platform.mobileservices.model.text.PhoneNumberListModel;
import de.hybris.platform.mobileservices.model.text.PhoneNumberModel;
import de.hybris.platform.mobileservices.text.processing.IncomingMessageProcessor;
import de.hybris.platform.mobileservices.text.processing.OutgoingMessageProcessor;
import de.hybris.platform.mobileservices.text.testimpl.BAM390DetectorTask;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.util.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;


/**
 * 
 */
public class PhoneNumberFilteringTest extends ServicelayerTest
{

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private ModelService modelService;

	@Resource
	private I18NService i18nService;

	@Resource
	private IncomingMessageProcessor incomingMessageProcessor;

	@Resource
	private OutgoingMessageProcessor outgoingMessageProcessor;

	@Resource
	private BAM390DetectorTask dataModelConsitencyChecker;

	private CountryModel cDE;
	private MobileAggregatorModel aggregator;
	private MobileShortcodeModel shortcode;
	private MobileActionKeywordModel keyword;
	private MobileActionAssignmentModel actionAssignment;
	private MobileReceiveAndSendTextActionModel action;
	private MobileReceiveAndSendTextActionModel sorryAction;

	private PhoneNumberListModel blockList;
	private PhoneNumberListModel testList;

	private static boolean taskEngineRunningBefore;

	private int msgCounter = 0;

	MessageTestingUtilities testUtils;

	@BeforeClass
	public static void stopTaskEngine()
	{
		Registry.activateStandaloneMode();
		Utilities.setJUnitTenant();

		// we don't want asynchronous tasks here so we stop engine for the time of testing
		final ApplicationContext applicationContext = Registry.getGlobalApplicationContext();
		final TaskService taskService = (TaskService) applicationContext.getBean("defaultTaskService");
		assertNotNull(taskService);

		taskEngineRunningBefore = taskService.getEngine().isRunning();
		if (taskEngineRunningBefore)
		{
			taskService.getEngine().stop();
		}
	}

	@AfterClass
	public static void restartTaskEngine()
	{
		if (taskEngineRunningBefore)
		{
			final ApplicationContext applicationContext = Registry.getGlobalApplicationContext();
			final TaskService taskService = (TaskService) applicationContext.getBean("defaultTaskService");
			assertNotNull(taskService);
			taskService.getEngine().start();
		}
	}

	@Before
	public void setUp()
	{
		testUtils = new MessageTestingUtilities(modelService, dataModelConsitencyChecker);

		this.msgCounter = 0;

		try
		{
			cDE = i18nService.getCountry("DE");
		}
		catch (final UnknownIdentifierException e)
		{
			cDE = modelService.create(CountryModel.class);
			cDE.setIsocode("DE");
			modelService.save(cDE);
		}

		this.blockList = createPhoneList("testBlocklist");
		this.testList = createPhoneList("testList");

		this.aggregator = modelService.create(MobileAggregatorModel.class);
		aggregator.setCode("testAggregator");
		aggregator.setEngine(EnginesType.TESTSENDSMSENGINE);

		this.shortcode = modelService.create(MobileShortcodeModel.class);
		shortcode.setAggregator(aggregator);
		shortcode.setCode("5555");
		shortcode.setCountry(cDE);
		shortcode.setPhoneNumberFilters(Collections.singleton(blockList));
		shortcode.setTestingPhoneNumbers(Collections.singleton(testList));

		this.action = modelService.create(MobileReceiveAndSendTextActionModel.class);
		action.setCode("testSendTextAction");
		action.setContent("foo bar");

		this.sorryAction = modelService.create(MobileReceiveAndSendTextActionModel.class);
		sorryAction.setCode("testSorryAction");
		sorryAction.setContent("Sorry, cannot undertand you.");
		shortcode.setDefaultAction(sorryAction);

		this.keyword = modelService.create(MobileActionKeywordModel.class);
		keyword.setKeyword("HELLO");

		this.actionAssignment = modelService.create(MobileActionAssignmentModel.class);
		actionAssignment.setShortcode(shortcode);
		actionAssignment.setAction(action);
		actionAssignment.setKeyword(keyword);
		actionAssignment.setState(MobileActionAssignmentStateType.ACTIVE);

		modelService.saveAll();
	}

	@Test
	public void testTestlitsts() throws RetryLaterException
	{
		final PhoneNumberModel nr1 = createPhoneNumber("0177-1234567", true, blockList);
		final PhoneNumberModel nr2 = createPhoneNumber("+491775555555", false, blockList);
		final PhoneNumberModel nr3 = createPhoneNumber("0177-2222222", true, testList);
		final PhoneNumberModel nr4 = createPhoneNumber("+491773333333", false, testList);

		modelService.saveAll(nr1, nr2, nr3, nr4);

		// now we've got nr1 and nr2 on blocklist and nr3 and nr4 on test list

		assertEquals("491771234567", nr1.getNormalizedNumber());
		assertEquals("491775555555", nr2.getNormalizedNumber());
		assertEquals("491772222222", nr3.getNormalizedNumber());
		assertEquals("491773333333", nr4.getNormalizedNumber());

		// test once with testmode=false -> blocklist should eat this
		MobileMessageContextModel msg = createIncomingMessage("HELLO Test", nr1.getNormalizedNumber());
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertBlocked(msg, null);

		actionAssignment.setState(MobileActionAssignmentStateType.TESTMODE);
		modelService.save(actionAssignment);

		// test again with testmode=true -> shortcode blocklist is still effective so it is dropped again
		msg = createIncomingMessage("HELLO Test", nr1.getNormalizedNumber());
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertBlocked(msg, null);

		// test with number which on neither one -> should be hitting test list check and be answered with sorry action
		msg = createIncomingMessage("HELLO Test", "441713333333");
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertTestModeDefaultReply(msg);

		// now positive test from list 1
		msg = createIncomingMessage("HELLO Test", nr3.getNormalizedNumber());
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertIncomingSuccess(msg);

		// now positive test from list 2
		msg = createIncomingMessage("HELLO Test", nr4.getNormalizedNumber());
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertIncomingSuccess(msg);

		//test outgoing NOT being affected: nr1 is on blocklist and must be dropped
		msg = createOutgoingMessage(action.getContent(), "DE", nr1.getNormalizedNumber(), true);
		modelService.save(msg);
		outgoingMessageProcessor.process(msg);
		assertBlocked(msg, actionAssignment);

		// test outgoing NOT being affected: nr3 is not on the blocklist (but on test list ) and 
		// must be let through
		msg = createOutgoingMessage(action.getContent(), "DE", nr3.getNormalizedNumber(), true);
		modelService.save(msg);
		outgoingMessageProcessor.process(msg);
		assertOutgoingSuccess(msg);

		// set action specific test lists
		final PhoneNumberListModel testList1 = createPhoneList("actionTestList1");
		final PhoneNumberListModel testList2 = createPhoneList("actionTestList2");
		final PhoneNumberListModel actionBlocklist = createPhoneList("actionTestBlocklist");
		final PhoneNumberModel nr5 = createPhoneNumber("(0177)7777777", true, testList1);
		final PhoneNumberModel nr6 = createPhoneNumber("(0177)8888888", true, testList2);
		final PhoneNumberModel nr7 = createPhoneNumber("(0177)9999999", true, actionBlocklist);

		modelService.saveAll(testList1, testList2, nr5, nr6, nr7);

		// make sure new numbers are getting default response as long as the new test lists
		// are not in place
		msg = createIncomingMessage("HELLO test", nr5.getNormalizedNumber());
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertTestModeDefaultReply(msg);

		// now install lists
		actionAssignment.setTestingPhoneNumbers(Arrays.asList(testList1, testList2));
		actionAssignment.setPhoneNumberFilters(Collections.singletonList(actionBlocklist));
		modelService.save(actionAssignment);

		// having new test lists installed nr5 must pass now
		msg = createIncomingMessage("HELLO test", nr5.getNormalizedNumber());
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertIncomingSuccess(msg);

		// ... same for nr6
		msg = createIncomingMessage("HELLO test", nr6.getNormalizedNumber());
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertIncomingSuccess(msg);

		// nr7 is on action blocklist, but in testmode==true this is not effective - however since it's not on
		// the test list it will be answered with the default reply
		msg = createIncomingMessage("HELLO test", nr7.getNormalizedNumber());
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertTestModeDefaultReply(msg);

		// now add nr7 to test list
		final List<PhoneNumberListModel> lists = new ArrayList<PhoneNumberListModel>(nr7.getLists());
		lists.add(testList2);
		nr7.setLists(lists);
		modelService.save(nr7);

		// nr7 is on action blocklist, but in testmode==true this is not effective; since now nr7 is
		// on test list too it has to go through
		msg = createIncomingMessage("HELLO test", nr7.getNormalizedNumber());
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertIncomingSuccess(msg);


		// let's switch off testmode -> now nr7 must be dropped completely since it's on blocklist
		actionAssignment.setState(MobileActionAssignmentStateType.ACTIVE);
		modelService.save(actionAssignment);

		msg = createIncomingMessage("HELLO test", nr7.getNormalizedNumber());
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertBlocked(msg, actionAssignment);

	}

	@Test
	public void testBlocklists() throws RetryLaterException
	{
		final PhoneNumberModel nr1 = createPhoneNumber("0177-1234567", true, blockList);
		final PhoneNumberModel nr2 = createPhoneNumber("+491775555555", false, blockList);

		modelService.saveAll(nr1, nr2);

		assertEquals("491771234567", nr1.getNormalizedNumber());
		assertEquals("491775555555", nr2.getNormalizedNumber());

		// test incoming for nr1

		MobileMessageContextModel msg = createIncomingMessage("HELLO Test", "491771234567");
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertBlocked(msg, null);

		// test incoming for nr2

		msg = createIncomingMessage("HELLO Test", "491775555555");
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertBlocked(msg, null);

		// fake a replied message for nr1
		msg = createOutgoingMessage(action.getContent(), "DE", "491771234567", true);
		modelService.save(msg);
		// test outgoing filtering -> should be blocked as well since number is on block list
		outgoingMessageProcessor.process(msg);
		assertBlocked(msg, actionAssignment);

		// fake a replied message for nr2
		msg = createOutgoingMessage(action.getContent(), "DE", "491775555555", true);
		modelService.save(msg);
		// test outgoing filtering -> should be blocked as well since number is on block list
		outgoingMessageProcessor.process(msg);
		assertBlocked(msg, actionAssignment);

		// positive test now

		msg = createIncomingMessage("HELLO Test", "491771111111");
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertIncomingSuccess(msg);
		outgoingMessageProcessor.process(msg);
		assertOutgoingSuccess(msg);

		// now test action specific blocking

		final PhoneNumberListModel testActionBlocklist = createPhoneList("testActionBlocklist");
		final PhoneNumberListModel testActionBlocklist2 = createPhoneList("testActionBlocklist2");
		final PhoneNumberModel nr3 = createPhoneNumber("(0177)7777777", true, testActionBlocklist);
		final PhoneNumberModel nr4 = createPhoneNumber("(0177)8888888", true, testActionBlocklist2);
		actionAssignment.setPhoneNumberFilters(Arrays.asList(testActionBlocklist, testActionBlocklist2));

		modelService.saveAll(testActionBlocklist, testActionBlocklist2, nr3, nr4, actionAssignment);


		// test action specific blocking for nr 3
		msg = createIncomingMessage("HELLO Test", "491777777777");
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertBlocked(msg, actionAssignment);

		// test action specific blocking for nr 4
		msg = createIncomingMessage("HELLO Test", "491778888888");
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertBlocked(msg, actionAssignment);
	}


	@Test
	public void testEmptyLists() throws RetryLaterException
	{
		// testMode == false, blocklist={}, testlist={} -> anything goes
		assertTrue(testList.getNumbers().isEmpty());
		assertTrue(blockList.getNumbers().isEmpty());

		MobileMessageContextModel msg = createIncomingMessage("HELLO Test", "491771234567");
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertIncomingSuccess(msg);
		outgoingMessageProcessor.process(msg);
		assertOutgoingSuccess(msg);

		// testMode == true, blocklist={}, testlist={} -> nothing is allowed since testlist is empty

		actionAssignment.setState(MobileActionAssignmentStateType.TESTMODE);
		modelService.save(actionAssignment);

		// in test mode everything which is not on test list must get default reply (simulate that action
		// is not there)
		msg = createIncomingMessage("HELLO Test", "491771234567");
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertTestModeDefaultReply(msg);

		// fake a replied message
		msg = createOutgoingMessage(action.getContent(), "DE", "491771234567", true);
		modelService.save(msg);

		// test outgoing filtering -> should pass since blocklist is empty
		// and test lists are not effective at the shortcode
		outgoingMessageProcessor.process(msg);
		assertOutgoingSuccess(msg);

		/* MOBILE-162 Action in test mode. No list on assignment. Empty whitelist on shortcode */

		// Prepare scenario. Ensure assignment is in test mode, and shortcode list is used

		actionAssignment.setState(MobileActionAssignmentStateType.TESTMODE);
		actionAssignment.setTestingPhoneNumbers(new ArrayList<PhoneNumberListModel>());
		modelService.save(actionAssignment);
		msg = createIncomingMessage("HELLO Test", "491771234567");
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertTestModeDefaultReply(msg);
		// fake response
		msg = createOutgoingMessage(action.getContent(), "DE", "491771234567", true);
		modelService.save(msg);
		outgoingMessageProcessor.process(msg);
		assertOutgoingSuccess(msg);

		// scenario B. No lists whatsoever

		actionAssignment.setState(MobileActionAssignmentStateType.TESTMODE);
		actionAssignment.setTestingPhoneNumbers(new ArrayList<PhoneNumberListModel>());
		shortcode.setTestingPhoneNumbers(new ArrayList<PhoneNumberListModel>());
		modelService.save(actionAssignment);
		modelService.save(shortcode);
		msg = createIncomingMessage("HELLO Test", "491771234567");
		modelService.save(msg);
		incomingMessageProcessor.process(msg);
		assertTestModeDefaultReply(msg);
		// fake response
		msg = createOutgoingMessage(action.getContent(), "DE", "491771234567", true);
		modelService.save(msg);
		outgoingMessageProcessor.process(msg);
		assertOutgoingSuccess(msg);




	}

	private void assertIncomingSuccess(final MobileMessageContextModel msg)
	{
		assertEquals(MobileMessageStatus.SCHEDULED, msg.getStatus());
		assertEquals(shortcode, msg.getMatchedShortcode());
		assertEquals(actionAssignment, msg.getMatchedActionAssignment());
		assertEquals(action.getContent(), msg.getOutgoingText());
		assertNull(msg.getMessageError());
		assertNull(msg.getMessageErrorDescription());
		assertNull(msg.getAggregatorError());
		assertNull(msg.getAggregatorErrorDescription());

		// kill task since we will call outgoing processor ourself
		killReceiveTask(msg);
	}

	private void assertTestModeDefaultReply(final MobileMessageContextModel msg)
	{
		assertEquals(MobileMessageStatus.SCHEDULED, msg.getStatus());
		assertEquals(shortcode, msg.getMatchedShortcode());
		assertNull(msg.getMatchedActionAssignment()); // default reply means no action assignment
		assertEquals(sorryAction.getContent(), msg.getOutgoingText());
		assertEquals(Boolean.TRUE, msg.getUsingDefaultAction());
		assertNull(msg.getMessageError());
		assertNotNull(msg.getMessageErrorDescription());
		assertNull(msg.getAggregatorError());
		assertNull(msg.getAggregatorErrorDescription());

		// kill task since we will call outgoing processor ourself
		killReceiveTask(msg);
	}

	private void assertBlocked(final MobileMessageContextModel msg, final MobileActionAssignmentModel assignment)
	{
		assertEquals(MobileMessageStatus.DISCARDED, msg.getStatus());
		assertEquals(shortcode, msg.getMatchedShortcode());
		assertEquals(assignment, msg.getMatchedActionAssignment());
		assertEquals(MobileMessageError.FILTERED, msg.getMessageError());
		assertNotNull(msg.getMessageErrorDescription());
		assertNull(msg.getAggregatorError());
		assertNull(msg.getAggregatorErrorDescription());

		assertEquals("there should be no tasks for discarded message",
				Collections.EMPTY_LIST, getTasksForMessage(msg));
	}

	private void assertOutgoingSuccess(final MobileMessageContextModel msg)
	{
		assertFalse("This shouldn't happen", testUtils.isModelServiceBogus(msg));
		assertFalse("We are in BAM-390 scenario", MessageTestingUtilities.isTextServiceIllegalStateReplyFailure(msg));
		assertEquals(MobileMessageStatus.SENT, msg.getStatus());
		assertEquals(shortcode, msg.getOutgoingShortcode());
		assertNull(msg.getMessageError());
		assertNull(msg.getMessageErrorDescription());
		assertNull(msg.getAggregatorError());
		assertNull(msg.getAggregatorErrorDescription());
	}

	private PhoneNumberListModel createPhoneList(final String code)
	{
		final PhoneNumberListModel ret = modelService.create(PhoneNumberListModel.class);
		ret.setCode(code);
		return ret;
	}

	private PhoneNumberModel createPhoneNumber(final String number, final boolean local, final PhoneNumberListModel... lists)
	{
		final PhoneNumberModel ret = modelService.create(PhoneNumberModel.class);
		ret.setNumber(number);
		ret.setCountry(cDE);
		ret.setFormat(local ? PhoneNumberFormat.LOCAL : PhoneNumberFormat.INTERNATIONAL);
		if (lists != null)
		{
			ret.setLists(Arrays.asList(lists));
		}
		return ret;
	}

	protected MobileMessageContextModel createIncomingMessage(final String text, final String number)
	{
		final MobileMessageContextModel msg = modelService.create(MobileMessageContextModel.class);
		msg.setIncomingMessageId(Integer.toString(msgCounter++));
		msg.setStatus(MobileMessageStatus.RECEIVED);
		msg.setType(MessageType.INCOMING);
		msg.setIncomingText(text);
		msg.setPhoneNumber(number);
		msg.setShortcode(shortcode.getCode());
		msg.setCountryIsoCode(shortcode.getCountry().getIsocode());

		return msg;
	}

	protected MobileMessageContextModel createOutgoingMessage(final String outgoingText, final String phoneCountry,
			final String number, final boolean twoWay)
	{
		final MobileMessageContextModel msg = modelService.create(MobileMessageContextModel.class);
		msg.setStatus(MobileMessageStatus.SCHEDULED);
		if (twoWay)
		{
			msg.setShortcode(shortcode.getCode());
			msg.setCountryIsoCode(shortcode.getCountry().getIsocode());
			msg.setIncomingText("dummy");
			msg.setIncomingMessageId(Integer.toString(msgCounter++));
			msg.setType(MessageType.TWO_WAY);
			msg.setMatchedAction(action);
			msg.setMatchedShortcode(shortcode);
			msg.setMatchedActionAssignment(actionAssignment);
		}
		else
		{
			msg.setType(MessageType.OUTGOING);

		}
		msg.setOutgoingText(outgoingText);
		msg.setPhoneNumber(number);
		msg.setPhoneCountryIsoCode(phoneCountry);
		return msg;
	}

	private List<TaskModel> getTasksForMessage(final MobileMessageContextModel msg)
	{
		final SearchResult<TaskModel> searchResult = flexibleSearchService.search(
				"select {pk} " +
						"from {" + TaskModel._TYPECODE + "} " +
						"where {" + TaskModel.CONTEXTITEM + "}=?msg",
				Collections.singletonMap("msg", msg)
				);
		return searchResult.getResult();
	}

	private void killReceiveTask(final MobileMessageContextModel msg)
	{
		final List<TaskModel> tasks = getTasksForMessage(msg);
		if (tasks.isEmpty())
		{
			throw new IllegalStateException("no task found for message " + msg);
		}
		else
		{
			for (final TaskModel t : tasks)
			{
				modelService.remove(t);
			}
		}
	}
}
