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
package de.hybris.platform.ticket.jalo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.ticket.enums.CsEventReason;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsResolutionType;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.events.model.CsTicketChangeEventCsAgentGroupEntryModel;
import de.hybris.platform.ticket.events.model.CsTicketChangeEventCsTicketCategoryEntryModel;
import de.hybris.platform.ticket.events.model.CsTicketChangeEventCsTicketPriorityEntryModel;
import de.hybris.platform.ticket.events.model.CsTicketChangeEventCsTicketStateEntryModel;
import de.hybris.platform.ticket.events.model.CsTicketChangeEventEmployeeEntryModel;
import de.hybris.platform.ticket.events.model.CsTicketChangeEventEntryModel;
import de.hybris.platform.ticket.events.model.CsTicketChangeEventStringEntryModel;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.events.model.CsTicketResolutionEventModel;
import de.hybris.platform.ticket.model.CsAgentGroupModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketException;
import de.hybris.platform.ticket.service.TicketService;
import de.hybris.platform.ticket.service.impl.DefaultTicketBusinessService;
import de.hybris.platform.ticket.strategies.TicketEventEmailStrategy;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 */

public class DefaultTicketBusinessServiceTest extends AbstractTicketsystemTest
{
	private static final Logger LOG = Logger.getLogger(DefaultTicketBusinessServiceTest.class.getName());

	private MockTicketEventEmailStrategy emailService = null;

	private TicketEventEmailStrategy originalMailService = null;

	@Resource
	private TicketService ticketService;
	@Resource
	private UserService userService;

	private MockTicketEventEmailStrategy getEmailService()
	{
		if (emailService == null)
		{
			emailService = new MockTicketEventEmailStrategy();
			emailService.setModelService(modelService);
		}
		return emailService;
	}

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		originalMailService = ((DefaultTicketBusinessService) ticketBusinessService).getTicketEventEmailStrategy();
		((DefaultTicketBusinessService) ticketBusinessService).setTicketEventEmailStrategy(getEmailService());
	}

	@After
	public void tearDown()
	{
		// implement here code executed after each test
		getEmailService().reset();
		((DefaultTicketBusinessService) ticketBusinessService).setTicketEventEmailStrategy(originalMailService);
	}

	/**
	 * Check the service is available
	 */
	@Test
	public void testTicketBusinessService()
	{
		assertNotNull("Can not find ticket business Service", ticketBusinessService);
	}

	@Test
	public void testCreateTicket()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(CsTicketState.OPEN, ticket.getState());
		assertTrue(ticket.getCustomer().equals(testUser));
		assertNull(ticket.getOrder());
		assertTrue(ticket.getHeadline().equals(headline));
		assertTrue(StringUtils.isNotEmpty(ticket.getTicketID()));
		assertTrue(ticket.getAssignedAgent().equals(adminUser));
		assertTrue(ticket.getAssignedGroup().equals(testGroup));
		assertTrue(ticket.getPriority().equals(CsTicketPriority.LOW));
		assertTrue(ticket.getCategory().equals(CsTicketCategory.NOTE));
		assertEquals(1, eventsForTicket.size());
		assertTrue(eventsForTicket.get(0) instanceof CsCustomerEventModel);
		final CsCustomerEventModel creationEvent = (CsCustomerEventModel) eventsForTicket.get(0);
		assertEquals(note, creationEvent.getText());
		assertEquals(CsInterventionType.CALL, creationEvent.getInterventionType());
		assertEquals(CsEventReason.FIRSTCONTACT, creationEvent.getReason());
		assertEquals(1, getEmailService().getEvents().size());
	}

	@Test
	public void testCreateTicketWithNoAssignedAgent()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				null, null, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(CsTicketState.NEW, ticket.getState());
		assertTrue(ticket.getCustomer().equals(testUser));
		assertNull(ticket.getOrder());
		assertTrue(ticket.getHeadline().equals(headline));
		assertTrue(StringUtils.isNotEmpty(ticket.getTicketID()));
		assertNull(ticket.getAssignedAgent());
		assertNull(ticket.getAssignedGroup());
		assertTrue(ticket.getPriority().equals(CsTicketPriority.LOW));
		assertTrue(ticket.getCategory().equals(CsTicketCategory.NOTE));
		assertEquals(1, eventsForTicket.size());
		assertTrue(eventsForTicket.get(0) instanceof CsCustomerEventModel);
		final CsCustomerEventModel creationEvent = (CsCustomerEventModel) eventsForTicket.get(0);
		assertEquals(note, creationEvent.getText());
		assertEquals(CsInterventionType.CALL, creationEvent.getInterventionType());
		assertEquals(CsEventReason.FIRSTCONTACT, creationEvent.getReason());
		assertEquals(1, getEmailService().getEvents().size());
	}

	@Test
	public void testCreateTicketWithDefaultAssignee()
	{
		final CsAgentGroupModel group3 = (CsAgentGroupModel) userService.getUserGroupForUID("testTicketGroup3");
		final EmployeeModel agent3 = (EmployeeModel) userService.getUserForUID("agent3");
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				null, group3, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(CsTicketState.NEW, ticket.getState());
		assertTrue(ticket.getCustomer().equals(testUser));
		assertNull(ticket.getOrder());
		assertTrue(ticket.getHeadline().equals(headline));
		assertTrue(StringUtils.isNotEmpty(ticket.getTicketID()));
		assertEquals(agent3, ticket.getAssignedAgent());
		assertEquals(group3, ticket.getAssignedGroup());
		assertTrue(ticket.getPriority().equals(CsTicketPriority.LOW));
		assertTrue(ticket.getCategory().equals(CsTicketCategory.NOTE));
		assertEquals(1, eventsForTicket.size());
		assertTrue(eventsForTicket.get(0) instanceof CsCustomerEventModel);
		final CsCustomerEventModel creationEvent = (CsCustomerEventModel) eventsForTicket.get(0);
		assertEquals(note, creationEvent.getText());
		assertEquals(CsInterventionType.CALL, creationEvent.getInterventionType());
		assertEquals(CsEventReason.FIRSTCONTACT, creationEvent.getReason());
		assertEquals(1, getEmailService().getEvents().size());
	}

	@Test
	public void testCreateTicketWithOrder()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final OrderModel testOrder = testUser.getOrders().iterator().next();
		assertNotNull("Could not test order", testOrder);

		CsTicketModel ticket = modelService.create(CsTicketModel.class);
		ticket.setCustomer(testUser);
		ticket.setOrder(testOrder);
		ticket.setCategory(CsTicketCategory.NOTE);
		ticket.setPriority(CsTicketPriority.LOW);
		ticket.setAssignedAgent(adminUser);
		ticket.setAssignedGroup(testGroup);
		ticket.setHeadline(headline);

		final CsCustomerEventModel creationEvent = modelService.create(CsCustomerEventModel.class);
		creationEvent.setText(note);

		ticket = ticketBusinessService.createTicket(ticket, creationEvent);

		assertNotNull(ticket);

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(CsTicketState.OPEN, ticket.getState());
		assertTrue(ticket.getCustomer().equals(testUser));
		assertEquals(testOrder, ticket.getOrder());
		assertTrue(ticket.getHeadline().equals(headline));
		assertTrue(StringUtils.isNotEmpty(ticket.getTicketID()));
		assertTrue(ticket.getAssignedAgent().equals(adminUser));
		assertTrue(ticket.getAssignedGroup().equals(testGroup));
		assertTrue(ticket.getPriority().equals(CsTicketPriority.LOW));
		assertTrue(ticket.getCategory().equals(CsTicketCategory.NOTE));
		assertEquals(1, eventsForTicket.size());
		assertTrue(eventsForTicket.get(0) instanceof CsCustomerEventModel);
		assertEquals(note, eventsForTicket.get(0).getText());

		assertEquals(1, getEmailService().getEvents().size());
	}

	@Test
	public void testCreateTicketWithOrderForDifferentUser()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final UserModel otherUser = userService.getUserForUID("sbrueck");
		assertNotNull("Could not find user 'sbrueck'", otherUser);

		final OrderModel testOrder = otherUser.getOrders().iterator().next();
		assertNotNull("Could not test order", testOrder);

		CsTicketModel ticket = modelService.create(CsTicketModel.class);
		ticket.setCustomer(testUser);
		ticket.setOrder(testOrder);
		ticket.setCategory(CsTicketCategory.NOTE);
		ticket.setPriority(CsTicketPriority.LOW);
		ticket.setAssignedAgent(adminUser);
		ticket.setAssignedGroup(testGroup);
		ticket.setHeadline(headline);

		final CsCustomerEventModel creationEvent = modelService.create(CsCustomerEventModel.class);
		creationEvent.setText(note);

		try
		{
			ticket = ticketBusinessService.createTicket(ticket, creationEvent);
			fail("An exception should have been thrown");
		}
		catch (final ModelSavingException ex)//NOPMD
		{
			//LOG.info("Got expected exception [" + ex.getMessage() + "]", ex);
		}
	}

	@Test
	public void testChangeOrderOnValidTicketToInvalidOrder()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final OrderModel testOrder = testUser.getOrders().iterator().next();
		assertNotNull("Could not test order", testOrder);

		CsTicketModel ticket = modelService.create(CsTicketModel.class);
		ticket.setCustomer(testUser);
		ticket.setOrder(testOrder);
		ticket.setCategory(CsTicketCategory.NOTE);
		ticket.setPriority(CsTicketPriority.LOW);
		ticket.setAssignedAgent(adminUser);
		ticket.setAssignedGroup(testGroup);
		ticket.setHeadline(headline);

		final CsCustomerEventModel creationEvent = modelService.create(CsCustomerEventModel.class);
		creationEvent.setText(note);

		ticket = ticketBusinessService.createTicket(ticket, creationEvent);

		assertNotNull(ticket);

		final OrderModel otherOrder = userService.getUserForUID("sbrueck").getOrders().iterator().next();
		assertNotNull("Could not find order for user 'sbrueck'", otherOrder);

		ticket.setOrder(otherOrder);

		try
		{
			ticket = ticketBusinessService.updateTicket(ticket);
			fail("An exception should have been thrown");
		}
		catch (final ModelSavingException ex)//NOPMD
		{
			//LOG.info("Got expected exception [" + ex.getMessage() + "]", ex);
		}
		catch (final TicketException e)
		{
			LOG.error("Unexpected exception updating exception", e);
			fail("Not expected exception [" + e.getMessage() + "]");
		}
	}



	@Test
	public void testUpdateTicketPriorityThroughUpdateTicket()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		ticket.setPriority(CsTicketPriority.HIGH);

		try
		{
			ticketBusinessService.updateTicket(ticket);
		}
		catch (final TicketException e)
		{
			LOG.error("Unexpected exception while updating a ticket", e);
			fail("updateTicket Method was not expected to throw an exception");
		}

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(eventsForTicket.size(), 2);
		assertEquals("", eventsForTicket.get(1).getText());

		final CsTicketEventModel changeEvent = eventsForTicket.get(1);

		assertEquals(changeEvent.getEntries().size(), 1);

		final CsTicketChangeEventEntryModel entry = changeEvent.getEntries().iterator().next();
		assertNotNull(entry.getAlteredAttribute());
		assertEquals("priority", entry.getAlteredAttribute().getQualifier());
		assertTrue(entry instanceof CsTicketChangeEventCsTicketPriorityEntryModel);

		final CsTicketChangeEventCsTicketPriorityEntryModel prioChangeEntry = (CsTicketChangeEventCsTicketPriorityEntryModel) entry;
		assertEquals(CsTicketPriority.LOW, prioChangeEntry.getOldValue());
		assertEquals(CsTicketPriority.HIGH, prioChangeEntry.getNewValue());

		assertEquals(2, getEmailService().getEvents().size());
	}

	@Test
	public void testUpdateTicketStateUpdateTicket()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				null, null, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		ticket.setState(CsTicketState.OPEN);

		try
		{
			ticketBusinessService.updateTicket(ticket);
		}
		catch (final TicketException e)
		{
			fail("updateTicket Method was not expected to throw an exception");
		}

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(eventsForTicket.size(), 2);
		assertEquals("", eventsForTicket.get(1).getText());

		final CsTicketEventModel changeEvent = eventsForTicket.get(1);

		assertEquals(changeEvent.getEntries().size(), 1);

		final CsTicketChangeEventEntryModel entry = changeEvent.getEntries().iterator().next();
		assertNotNull(entry.getAlteredAttribute());
		assertEquals("state", entry.getAlteredAttribute().getQualifier());
		assertTrue(entry instanceof CsTicketChangeEventCsTicketStateEntryModel);

		final CsTicketChangeEventCsTicketStateEntryModel prioChangeEntry = (CsTicketChangeEventCsTicketStateEntryModel) entry;
		assertEquals(CsTicketState.NEW, prioChangeEntry.getOldValue());
		assertEquals(CsTicketState.OPEN, prioChangeEntry.getNewValue());

		assertEquals(2, getEmailService().getEvents().size());
	}

	@Test
	public void testUpdateTicketCategoryThroughUpdateTicket()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		ticket.setCategory(CsTicketCategory.COMPLAINT);

		try
		{
			ticketBusinessService.updateTicket(ticket);
		}
		catch (final TicketException e)
		{
			fail("updateTicket Method was not expected to throw an exception");
		}

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(eventsForTicket.size(), 2);
		assertEquals("", eventsForTicket.get(1).getText());

		final CsTicketEventModel changeEvent = eventsForTicket.get(1);

		assertEquals(changeEvent.getEntries().size(), 1);

		final CsTicketChangeEventEntryModel entry = changeEvent.getEntries().iterator().next();
		assertNotNull(entry.getAlteredAttribute());
		assertEquals("category", entry.getAlteredAttribute().getQualifier());
		assertTrue(entry instanceof CsTicketChangeEventCsTicketCategoryEntryModel);

		final CsTicketChangeEventCsTicketCategoryEntryModel catChangeEntry = (CsTicketChangeEventCsTicketCategoryEntryModel) entry;
		assertEquals(CsTicketCategory.NOTE, catChangeEntry.getOldValue());
		assertEquals(CsTicketCategory.COMPLAINT, catChangeEntry.getNewValue());

		assertEquals(2, getEmailService().getEvents().size());
	}

	@Test
	public void testUpdateTicketHeadlineThroughUpdateTicket()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		final String headline2 = "Updated headline";

		ticket.setHeadline(headline2);

		try
		{
			ticketBusinessService.updateTicket(ticket);
		}
		catch (final TicketException e)
		{
			fail("updateTicket Method was not expected to throw an exception");
		}

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(eventsForTicket.size(), 2);
		assertEquals("", eventsForTicket.get(1).getText());

		final CsTicketEventModel changeEvent = eventsForTicket.get(1);

		assertEquals(changeEvent.getEntries().size(), 1);

		final CsTicketChangeEventEntryModel entry = changeEvent.getEntries().iterator().next();
		assertNotNull(entry.getAlteredAttribute());
		assertEquals("headline", entry.getAlteredAttribute().getQualifier());
		assertTrue(entry instanceof CsTicketChangeEventStringEntryModel);

		final CsTicketChangeEventStringEntryModel headlineChangeEntry = (CsTicketChangeEventStringEntryModel) entry;
		assertEquals(headline, headlineChangeEntry.getOldValue());
		assertEquals(headline2, headlineChangeEntry.getNewValue());

		assertEquals(2, getEmailService().getEvents().size());
	}

	@Test
	public void testUpdateTicketAssignedAgentThroughUpdateTicket()
	{
		final UserModel newAgent = userService.getUserForUID("agent2");
		assertNotNull("Could not find user 'agent2'", newAgent);

		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		ticket.setAssignedAgent((EmployeeModel) newAgent);

		try
		{
			ticketBusinessService.updateTicket(ticket);
		}
		catch (final TicketException e)
		{
			fail("updateTicket Method was not expected to throw an exception");
		}

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(eventsForTicket.size(), 2);
		assertEquals("", eventsForTicket.get(1).getText());

		final CsTicketEventModel changeEvent = eventsForTicket.get(1);
		assertEquals(userService.getCurrentUser(), changeEvent.getAuthor());

		assertEquals(changeEvent.getEntries().size(), 1);

		final CsTicketChangeEventEntryModel entry = changeEvent.getEntries().iterator().next();
		assertNotNull(entry.getAlteredAttribute());
		assertEquals("assignedAgent", entry.getAlteredAttribute().getQualifier());
		assertTrue(entry instanceof CsTicketChangeEventEmployeeEntryModel);

		final CsTicketChangeEventEmployeeEntryModel agentChangeEntry = (CsTicketChangeEventEmployeeEntryModel) entry;
		assertEquals(adminUser, agentChangeEntry.getOldValue());
		assertEquals(newAgent, agentChangeEntry.getNewValue());

		assertEquals(2, getEmailService().getEvents().size());
	}

	@Test
	public void testUpdateTicketOrderThroughUpdateTicket()
	{
		final OrderModel testOrder = testUser.getOrders().iterator().next();
		assertNotNull("Could not test order", testOrder);

		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		ticket.setOrder(testOrder);

		try
		{
			ticketBusinessService.updateTicket(ticket);
		}
		catch (final TicketException e)
		{
			fail("updateTicket Method was not expected to throw an exception");
		}

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(eventsForTicket.size(), 2);
		assertEquals("", eventsForTicket.get(1).getText());

		final CsTicketEventModel changeEvent = eventsForTicket.get(1);
		assertEquals(userService.getCurrentUser(), changeEvent.getAuthor());

		assertEquals(changeEvent.getEntries().size(), 1);

		final CsTicketChangeEventEntryModel entry = changeEvent.getEntries().iterator().next();
		assertNotNull(entry.getAlteredAttribute());
		assertEquals("order", entry.getAlteredAttribute().getQualifier());

		assertNull(entry.getOldBinaryValue());
		assertEquals(testOrder, entry.getNewBinaryValue());

		assertEquals(2, getEmailService().getEvents().size());
	}

	@Test
	public void testUpdate2AttributesThroughUpdateTicket()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		final String headline2 = "Updated headline";

		ticket.setPriority(CsTicketPriority.HIGH);
		ticket.setHeadline(headline2);

		try
		{
			ticketBusinessService.updateTicket(ticket);
		}
		catch (final TicketException e)
		{
			fail("updateTicket Method was not expected to throw an exception");
		}

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(eventsForTicket.size(), 2);
		assertEquals("", eventsForTicket.get(1).getText());

		final CsTicketEventModel changeEvent = eventsForTicket.get(1);

		assertEquals(2, changeEvent.getEntries().size());

		int matches = 0;
		for (final CsTicketChangeEventEntryModel entry : changeEvent.getEntries())
		{
			if (entry instanceof CsTicketChangeEventStringEntryModel)
			{
				final CsTicketChangeEventStringEntryModel headlineChangeEntry = (CsTicketChangeEventStringEntryModel) entry;

				assertNotNull(entry.getAlteredAttribute());
				assertEquals("headline", entry.getAlteredAttribute().getQualifier());

				assertEquals(headline, headlineChangeEntry.getOldValue());
				assertEquals(headline2, headlineChangeEntry.getNewValue());
				matches++;
			}
			else if (entry instanceof CsTicketChangeEventCsTicketPriorityEntryModel)
			{
				final CsTicketChangeEventCsTicketPriorityEntryModel prioChangeEntry = (CsTicketChangeEventCsTicketPriorityEntryModel) entry;

				assertNotNull(entry.getAlteredAttribute());
				assertEquals("priority", entry.getAlteredAttribute().getQualifier());

				assertEquals(CsTicketPriority.LOW, prioChangeEntry.getOldValue());
				assertEquals(CsTicketPriority.HIGH, prioChangeEntry.getNewValue());
				matches++;
			}
			else
			{
				fail("Unexpected type of entry [" + entry + "]");
			}
		}

		assertEquals(2, matches);

		assertEquals(2, getEmailService().getEvents().size());
	}

	@Test
	public void testAssignTicket()
	{
		final UserModel newAgent = userService.getUserForUID("agent2");
		assertNotNull("Could not find user 'agent2'", newAgent);

		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		try
		{
			ticketBusinessService.assignTicketToAgent(ticket, (EmployeeModel) newAgent);
		}
		catch (final TicketException ex)
		{
			fail("Method should not have thrown an exception [" + ex.getMessage() + "]");
		}

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(eventsForTicket.size(), 2);
		assertEquals("", eventsForTicket.get(1).getText());

		final CsTicketEventModel changeEvent = eventsForTicket.get(1);
		assertEquals(userService.getCurrentUser(), changeEvent.getAuthor());

		assertEquals(changeEvent.getEntries().size(), 1);

		final CsTicketChangeEventEntryModel entry = changeEvent.getEntries().iterator().next();
		assertNotNull(entry.getAlteredAttribute());
		assertEquals("assignedAgent", entry.getAlteredAttribute().getQualifier());
		assertTrue(entry instanceof CsTicketChangeEventEmployeeEntryModel);

		final CsTicketChangeEventEmployeeEntryModel agentChangeEntry = (CsTicketChangeEventEmployeeEntryModel) entry;
		assertEquals(adminUser, agentChangeEntry.getOldValue());
		assertEquals(newAgent, agentChangeEntry.getNewValue());

		assertEquals(newAgent, ticket.getAssignedAgent());

		assertEquals(2, getEmailService().getEvents().size());
	}

	@Test
	public void testAssignTicketOnDirtyTicket()
	{
		final UserModel newAgent = userService.getUserForUID("agent2");
		assertNotNull("Could not find user 'agent2'", newAgent);

		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		ticket.setState(CsTicketState.OPEN);

		try
		{
			ticketBusinessService.assignTicketToAgent(ticket, (EmployeeModel) newAgent);
			fail("Method should have thrown an exception");
		}
		catch (final TicketException ex)
		{
			final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);
			assertEquals(eventsForTicket.size(), 1);
			assertEquals(1, getEmailService().getEvents().size());
		}
	}

	@Test
	public void testAssignExistingTicket()
	{
		final UserModel newAgent = userService.getUserForUID("agent2");
		assertNotNull("Could not find user 'agent2'", newAgent);

		final CsTicketModel ticket = ticketService.getTicketById("test-ticket-1");

		try
		{
			ticketBusinessService.assignTicketToAgent(ticket, (EmployeeModel) newAgent);
		}
		catch (final TicketException ex)
		{
			fail("Method should not have thrown an exception [" + ex.getMessage() + "]");
		}

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);
		final CsTicketEventModel changeEvent = eventsForTicket.get(1);

		assertEquals(eventsForTicket.size(), 2);
		assertEquals("", changeEvent.getText());

		assertEquals(userService.getCurrentUser(), changeEvent.getAuthor());
		assertEquals(changeEvent.getEntries().size(), 1);

		final CsTicketChangeEventEntryModel entry = changeEvent.getEntries().iterator().next();

		assertNotNull(entry.getAlteredAttribute());
		assertEquals("assignedAgent", entry.getAlteredAttribute().getQualifier());
		assertTrue(entry instanceof CsTicketChangeEventEmployeeEntryModel);

		final CsTicketChangeEventEmployeeEntryModel agentChangeEntry = (CsTicketChangeEventEmployeeEntryModel) entry;
		assertEquals(adminUser, agentChangeEntry.getOldValue());
		assertEquals(newAgent, agentChangeEntry.getNewValue());

		assertEquals(newAgent, ticket.getAssignedAgent());
	}

	@Test
	public void testSetTicketStateTicket()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				null, null, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		try
		{
			ticketBusinessService.setTicketState(ticket, CsTicketState.OPEN);
		}
		catch (final TicketException ex)
		{
			fail("Method should not have thrown an exception [" + ex.getMessage() + "]");
		}

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(eventsForTicket.size(), 2);
		assertEquals("", eventsForTicket.get(1).getText());

		final CsTicketEventModel changeEvent = eventsForTicket.get(1);

		assertEquals(changeEvent.getEntries().size(), 1);

		final CsTicketChangeEventEntryModel entry = changeEvent.getEntries().iterator().next();
		assertNotNull(entry.getAlteredAttribute());
		assertEquals("state", entry.getAlteredAttribute().getQualifier());
		assertTrue(entry instanceof CsTicketChangeEventCsTicketStateEntryModel);

		final CsTicketChangeEventCsTicketStateEntryModel stateChangeEntry = (CsTicketChangeEventCsTicketStateEntryModel) entry;
		assertEquals(CsTicketState.NEW, stateChangeEntry.getOldValue());
		assertEquals(CsTicketState.OPEN, stateChangeEntry.getNewValue());

		assertEquals(CsTicketState.OPEN, ticket.getState());

		assertEquals(2, getEmailService().getEvents().size());
	}

	@Test
	public void testSetTicketStateTicketWithNote()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";
		final String changeNote = "Ticket State Changed";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				null, null, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		try
		{
			ticketBusinessService.setTicketState(ticket, CsTicketState.OPEN, changeNote);
		}
		catch (final TicketException ex)
		{
			fail("Method should not have thrown an exception [" + ex.getMessage() + "]");
		}

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(eventsForTicket.size(), 2);
		assertEquals(changeNote, eventsForTicket.get(1).getText());

		final CsTicketEventModel changeEvent = eventsForTicket.get(1);

		assertEquals(changeEvent.getEntries().size(), 1);

		final CsTicketChangeEventEntryModel entry = changeEvent.getEntries().iterator().next();
		assertNotNull(entry.getAlteredAttribute());
		assertEquals("state", entry.getAlteredAttribute().getQualifier());
		assertTrue(entry instanceof CsTicketChangeEventCsTicketStateEntryModel);

		final CsTicketChangeEventCsTicketStateEntryModel stateChangeEntry = (CsTicketChangeEventCsTicketStateEntryModel) entry;
		assertEquals(CsTicketState.NEW, stateChangeEntry.getOldValue());
		assertEquals(CsTicketState.OPEN, stateChangeEntry.getNewValue());

		assertEquals(CsTicketState.OPEN, ticket.getState());

		assertEquals(2, getEmailService().getEvents().size());
	}

	@Test
	public void testSetTicketStateTicketOnDirtyTicket()
	{
		final UserModel newAgent = userService.getUserForUID("agent2");
		assertNotNull("Could not find user 'agent2'", newAgent);

		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		ticket.setAssignedAgent((EmployeeModel) newAgent);

		try
		{
			ticketBusinessService.setTicketState(ticket, CsTicketState.OPEN);
			fail("Method should have thrown an exception");
		}
		catch (final TicketException ex)
		{
			final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);
			assertEquals(eventsForTicket.size(), 1);
			assertEquals(1, getEmailService().getEvents().size());
		}
	}

	@Test
	public void testAssignTicketToGroup()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		final CsAgentGroupModel testGroup2 = modelService.create(CsAgentGroupModel.class);
		testGroup2.setUid("TestTicketGroup22");

		try
		{
			ticketBusinessService.assignTicketToGroup(ticket, testGroup2);
		}
		catch (final TicketException ex)
		{
			fail("Method should not have thrown an exception [" + ex.getMessage() + "]");
		}

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(eventsForTicket.size(), 2);
		assertEquals("", eventsForTicket.get(1).getText());

		final CsTicketEventModel changeEvent = eventsForTicket.get(1);
		assertEquals(userService.getCurrentUser(), changeEvent.getAuthor());

		assertEquals(changeEvent.getEntries().size(), 1);

		final CsTicketChangeEventEntryModel entry = changeEvent.getEntries().iterator().next();
		assertNotNull(entry.getAlteredAttribute());
		assertEquals("assignedGroup", entry.getAlteredAttribute().getQualifier());
		assertTrue(entry instanceof CsTicketChangeEventCsAgentGroupEntryModel);

		final CsTicketChangeEventCsAgentGroupEntryModel groupChangeEntry = (CsTicketChangeEventCsAgentGroupEntryModel) entry;
		assertEquals(testGroup, groupChangeEntry.getOldValue());
		assertEquals(testGroup2, groupChangeEntry.getNewValue());

		assertEquals(testGroup2, ticket.getAssignedGroup());

		assertEquals(2, getEmailService().getEvents().size());
	}

	@Test
	public void testAssignTicketToGroupOnDirtyTicket()
	{
		final CsAgentGroupModel testGroup2 = modelService.create(CsAgentGroupModel.class);
		testGroup2.setUid("TestTicketGroup2");

		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		ticket.setState(CsTicketState.OPEN);

		try
		{
			ticketBusinessService.assignTicketToGroup(ticket, testGroup2);
			fail("Method should have thrown an exception");
		}
		catch (final TicketException ex)
		{
			final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);
			assertEquals(eventsForTicket.size(), 1);
			assertEquals(1, getEmailService().getEvents().size());
		}
	}

	@Test
	public void testAddNoteToTicket()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		assertEquals(ticketService.getEventsForTicket(ticket).size(), 1);

		final String testnote = "Test note for ticket";

		ticketBusinessService.addNoteToTicket(ticket, CsInterventionType.CALL, CsEventReason.UPDATE, testnote, null);

		assertEquals(ticketService.getEventsForTicket(ticket).size(), 2);
		assertTrue(ticketService.getEventsForTicket(ticket).get(1) instanceof CsCustomerEventModel);

		final CsCustomerEventModel customerNote = (CsCustomerEventModel) ticketService.getEventsForTicket(ticket).get(1);

		assertEquals(testnote, customerNote.getText());
		assertEquals(userService.getCurrentUser(), customerNote.getAuthor());

		assertEquals(2, getEmailService().getEvents().size());
	}

	@Test
	public void testAddEmailToTicket()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		assertEquals(ticketService.getEventsForTicket(ticket).size(), 1);

		final String testSubject = "Test note for ticket";
		final String testBody = "Test note for ticket";

		ticketBusinessService.addCustomerEmailToTicket(ticket, CsEventReason.UPDATE, testSubject, testBody, null);

		assertEquals(ticketService.getEventsForTicket(ticket).size(), 2);
		assertTrue(ticketService.getEventsForTicket(ticket).get(1) instanceof CsCustomerEventModel);

		final CsCustomerEventModel customerEmail = (CsCustomerEventModel) ticketService.getEventsForTicket(ticket).get(1);

		assertEquals(testBody, customerEmail.getText());
		assertEquals(testSubject, customerEmail.getSubject());

		assertEquals(userService.getCurrentUser(), customerEmail.getAuthor());

		assertEquals(2, getEmailService().getEvents().size());
	}

	@Test
	public void testAddMultipleNoteToTicket()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		assertEquals(ticketService.getEventsForTicket(ticket).size(), 1);

		for (int i = 0; i < 100; i++)
		{
			final String testnote = "Test note for ticket [" + i + "]";
			ticketBusinessService.addNoteToTicket(ticket, CsInterventionType.CALL, CsEventReason.UPDATE, testnote, null);
		}

		assertEquals(ticketService.getEventsForTicket(ticket).size(), 101);

		for (int i = 1; i < 101; i++)
		{
			final String testnote = "Test note for ticket [" + (i - 1) + "]";
			assertTrue(ticketService.getEventsForTicket(ticket).get(i) instanceof CsCustomerEventModel);

			final CsCustomerEventModel customerNote = (CsCustomerEventModel) ticketService.getEventsForTicket(ticket).get(i);

			assertEquals(testnote, customerNote.getText());
			assertEquals(userService.getCurrentUser(), customerNote.getAuthor());
		}

		assertEquals(101, getEmailService().getEvents().size());

	}

	@Test
	public void testResolveTicket()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				null, null, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		assertEquals(1, ticketService.getEventsForTicket(ticket).size());

		ticket.setState(CsTicketState.OPEN);

		try
		{
			ticketBusinessService.updateTicket(ticket);
		}
		catch (final TicketException e)
		{
			fail("updateTicket Method was not expected to throw an exception");
		}

		final String resolutionNote = "Ticket has been resolved";

		try
		{
			ticketBusinessService.resolveTicket(ticket, CsInterventionType.EMAIL, CsResolutionType.CLOSED, resolutionNote);
		}
		catch (final TicketException e)
		{
			fail("resolveTicket Method was not expected to throw an exception");
		}

		assertEquals(3, ticketService.getEventsForTicket(ticket).size());
		assertTrue(ticketService.getEventsForTicket(ticket).get(2) instanceof CsTicketResolutionEventModel);

		final CsTicketResolutionEventModel resolutionEvent = (CsTicketResolutionEventModel) ticketService
				.getEventsForTicket(ticket).get(2);

		assertEquals(resolutionNote, resolutionEvent.getText());
		assertEquals(userService.getCurrentUser(), resolutionEvent.getAuthor());

		assertEquals(ticket.getResolution(), resolutionEvent);

		assertEquals(3, getEmailService().getEvents().size());
	}

	@Test
	public void testUnResolveTicket()
	{
		int eventCounter = 0;
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				null, null, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);

		assertNotNull(ticket);

		eventCounter++;

		ticket.setState(CsTicketState.OPEN);

		try
		{
			ticketBusinessService.updateTicket(ticket);

			eventCounter++;
		}
		catch (final TicketException e)
		{
			fail("updateTicket Method was not expected to throw an exception");
		}

		assertEquals(eventCounter, ticketService.getEventsForTicket(ticket).size());

		final String resolutionNote = "Ticket has been resolved";

		CsTicketResolutionEventModel resolutionEvent = null;
		try
		{
			ticketBusinessService.resolveTicket(ticket, CsInterventionType.EMAIL, CsResolutionType.CLOSED, resolutionNote);

			eventCounter++;

			assertEquals(eventCounter, ticketService.getEventsForTicket(ticket).size());
			assertTrue(ticketService.getEventsForTicket(ticket).get(eventCounter - 1) instanceof CsTicketResolutionEventModel);

			resolutionEvent = (CsTicketResolutionEventModel) ticketService.getEventsForTicket(ticket).get(eventCounter - 1);

			assertEquals(resolutionNote, resolutionEvent.getText());
			assertEquals(userService.getCurrentUser(), resolutionEvent.getAuthor());

			assertEquals(ticket.getResolution(), resolutionEvent);

			assertEquals(eventCounter, getEmailService().getEvents().size());

			try
			{
				final String reopenNote = "Ticket has NOT been resolved";
				ticketBusinessService.unResolveTicket(ticket, CsInterventionType.EMAIL, CsEventReason.COMPLAINT, reopenNote);

				eventCounter++;

				assertEquals(eventCounter, ticketService.getEventsForTicket(ticket).size());

				final CsCustomerEventModel unresolveEvent = (CsCustomerEventModel) ticketBusinessService.getLastEvent(ticket);
				assertEquals(reopenNote, unresolveEvent.getText());
				assertEquals(userService.getCurrentUser(), resolutionEvent.getAuthor());

				assertEquals(unresolveEvent.getReason(), CsEventReason.COMPLAINT);
				assertEquals(unresolveEvent.getInterventionType(), CsInterventionType.EMAIL);

				assertEquals(eventCounter, getEmailService().getEvents().size());
			}
			catch (final TicketException e)
			{
				assertEquals(eventCounter, ticketService.getEventsForTicket(ticket).size());
				assertEquals(eventCounter, getEmailService().getEvents().size());
			}
		}
		catch (final TicketException e)
		{
			assertNull(ticket.getResolution());
			assertEquals(eventCounter, ticketService.getEventsForTicket(ticket).size());
			assertEquals(eventCounter, getEmailService().getEvents().size());
		}
	}

	@Test
	public void testIsTicketClosed()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";
		final String resolutionNote = "Ticket has been resolved";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				null, null, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(1, eventsForTicket.size());

		boolean result = ticketBusinessService.isTicketClosed(ticket);

		assertFalse("Ticket should be open", result);

		ticket.setState(CsTicketState.OPEN);

		result = false;

		try
		{
			ticketBusinessService.updateTicket(ticket);

			ticketBusinessService.resolveTicket(ticket, CsInterventionType.EMAIL, CsResolutionType.CLOSED, resolutionNote);

			result = ticketBusinessService.isTicketClosed(ticket);
		}
		catch (final TicketException e)
		{
			fail("Method was not expected to throw an exception");
		}

		assertTrue("Ticket should be closed", result);
	}

	@Test
	public void testIsTicketResolvable()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				null, null, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		final List<CsTicketEventModel> eventsForTicket = ticketService.getEventsForTicket(ticket);

		assertEquals(1, eventsForTicket.size());

		boolean result = ticketBusinessService.isTicketResolvable(ticket);

		assertFalse("Ticket should not be resolvable", result);

		ticket.setState(CsTicketState.OPEN);

		result = false;

		try
		{
			ticketBusinessService.updateTicket(ticket);

			result = ticketBusinessService.isTicketResolvable(ticket);
		}
		catch (final TicketException e)
		{
			fail("Method was not expected to throw an exception");
		}

		assertTrue("Ticket should be resolvable", result);
	}
}
