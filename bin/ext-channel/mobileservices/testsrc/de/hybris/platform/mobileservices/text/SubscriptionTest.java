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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.mobileservices.enums.MessageType;
import de.hybris.platform.mobileservices.enums.MobileMessageStatus;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;
import de.hybris.platform.mobileservices.model.text.MobileReceiveAndSubscribeActionModel;
import de.hybris.platform.mobileservices.model.text.PhoneNumberListModel;
import de.hybris.platform.mobileservices.model.text.PhoneNumberModel;
import de.hybris.platform.mobileservices.text.actions.MobileReceiveAndSubscribeActionPerformable;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.util.Utilities;

import java.util.Collection;
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
public class SubscriptionTest extends ServicelayerTest
{
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private ModelService modelService;

	@Resource
	private MobileReceiveAndSubscribeActionPerformable mobileReceiveAndSubscribeAction;

	@Resource
	private I18NService i18nService;

	private MobileReceiveAndSubscribeActionModel action;
	private PhoneNumberListModel list;
	private CountryModel cDE;

	private int msgCounter = 0;
	private static boolean taskEngineRunningBefore;


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
		//unset tenant
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

		this.list = createPhoneList("optList");
		this.action = modelService.create(MobileReceiveAndSubscribeActionModel.class);
		action.setCode("testOptAction");
		action.setList(list);
		action.setSuccessMessage("Success");
		action.setDefaultErrorMessage("Error");
		action.setRemove(Boolean.FALSE);

		modelService.saveAll(list, action);
	}

	@Test
	public void testOptIn()
	{
		assertEquals(Collections.EMPTY_LIST, list.getNumbers());

		MobileMessageContextModel msg = createProcessingMessage("dummy", "491771234567");
		modelService.save(msg);
		mobileReceiveAndSubscribeAction.performAction(action, msg);
		assertIncomingSuccess(msg, true);
		modelService.refresh(list);

		Collection<PhoneNumberModel> numbers = list.getNumbers();
		assertEquals(1, numbers.size());
		assertNumberInCollection(numbers, cDE, "491771234567");

		// send again
		msg = createProcessingMessage("foo", "491771234567");
		modelService.save(msg);
		mobileReceiveAndSubscribeAction.performAction(action, msg);
		assertIncomingSuccess(msg, false);
		modelService.refresh(list);

		numbers = list.getNumbers();
		assertEquals(1, numbers.size());
		assertNumberInCollection(numbers, cDE, "491771234567");

		// now try new number
		msg = createProcessingMessage("foo", "491776666666");
		modelService.save(msg);
		mobileReceiveAndSubscribeAction.performAction(action, msg);
		assertIncomingSuccess(msg, true);
		modelService.refresh(list);

		numbers = list.getNumbers();
		assertEquals(2, numbers.size());
		assertNumberInCollection(numbers, cDE, "491776666666");
	}

	protected void assertNumberInCollection(final Collection<PhoneNumberModel> numbers, final CountryModel country,
			final String normalized)
	{
		for (final PhoneNumberModel p : numbers)
		{
			if (country.equals(p.getCountry()) && normalized.equals(p.getNormalizedNumber()))
			{
				return;
			}
		}
		fail("could not find " + normalized + " in " + list);
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

	private void assertIncomingSuccess(final MobileMessageContextModel msg, final boolean firstTimeCaller)
	{
		assertEquals(MobileMessageStatus.SCHEDULED, msg.getStatus());
		if (firstTimeCaller)
		{
			assertEquals(this.action.getSuccessMessage(), msg.getOutgoingText());
		}
		else
		{
			assertEquals(this.action.getDefaultErrorMessage(), msg.getOutgoingText());
		}
		assertNull(msg.getMessageError());
		assertNull(msg.getMessageErrorDescription());
		assertNull(msg.getAggregatorError());
		assertNull(msg.getAggregatorErrorDescription());

		// kill task since we will call outgoing processor ourself
		killReceiveTask(msg);
	}


	protected MobileMessageContextModel createProcessingMessage(final String text, final String number)
	{
		final MobileMessageContextModel msg = modelService.create(MobileMessageContextModel.class);
		msg.setIncomingMessageId(Integer.toString(msgCounter++));
		msg.setStatus(MobileMessageStatus.PROCESSING);
		msg.setType(MessageType.INCOMING);
		msg.setIncomingText(text);
		msg.setPhoneNumber(number);
		msg.setPhoneCountryIsoCode("DE");

		return msg;
	}


	private PhoneNumberListModel createPhoneList(final String code)
	{
		final PhoneNumberListModel ret = modelService.create(PhoneNumberListModel.class);
		ret.setCode(code);
		return ret;
	}
}
