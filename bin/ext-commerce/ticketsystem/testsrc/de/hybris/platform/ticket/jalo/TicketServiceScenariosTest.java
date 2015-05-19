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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.ticket.enums.CsEventReason;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsResolutionType;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.events.model.CsTicketChangeEventEmployeeEntryModel;
import de.hybris.platform.ticket.events.model.CsTicketChangeEventEntryModel;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsAgentGroupModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketException;
import de.hybris.platform.ticket.service.TicketService;
import de.hybris.platform.ticket.service.impl.DefaultTicketBusinessService;
import de.hybris.platform.ticket.strategies.TicketEventEmailStrategy;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Test Case that tests some business meaningful scenarios.
 * 
 * Scenario 1: - Ticket is created as a result of a call by a customer, reporting a issue. - Ticket is moved to the Open
 * status - A Note is added to the Ticket as a result of some action taking to resolve the issue. - The Ticket is
 * closed.
 * 
 * Scenario 2: - A customer calls regarding a currently open ticket - The ticket is found via the reference provided by
 * the customer - A Note is added to the Ticket recording the additional call
 * 
 * Scenario 3: - An email is received and a new ticket is created as a result - Customer is called regarding the
 * email/ticket - Ticket closed as a result of the call
 * 
 * Scenario 4: - A customer calls regarding a previously closed ticket - The ticket is found via the reference provided
 * by the customer - Ticket is reopened
 * 
 * Scenario 5: - A customer calls regarding a undelivered item on their order - A ticket is raised against the order -
 * The Ticket status is updated to Open - Ticket is assigned to an Agent
 * 
 * Scenario 6: - Problem Ticket Scenario, Ticket is raised and then bounced around. Nothing is resolved. Real World of
 * Call Centres.
 * 
 * @author chris
 */
public class TicketServiceScenariosTest extends AbstractTicketsystemTest
{
	private static final Logger LOG = Logger.getLogger(TicketServiceScenariosTest.class.getName());

	private TicketService ticketService;
	private ModelService modelService;
	private MockTicketEventEmailStrategy emailService = null;
	private TicketEventEmailStrategy originalMailService = null;

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

		ticketService = getApplicationContext().getBean("defaultTicketService", TicketService.class);
		modelService = getApplicationContext().getBean("modelService", ModelService.class);

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
	 * This is a sample test method.
	 */
	@Test
	public void testTicketServiceScenarios()
	{
		assertNotNull("Ticket Service Not Found", ticketService);
	}

	/**
	 * Scenario 1: - Ticket is created as a result of a call by a customer, reporting a issue. - Ticket is moved to the
	 * Open status - A Note is added to the Ticket as a result of some action taking to resolve the issue. - The Ticket
	 * is closed.
	 */
	@Test
	public void testScenario1()
	{
		//		- Ticket is created as a result of a call by a customer, reporting a issue. 
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);

		assertNotNull("Ticket was null, ticket creation failed?", ticket);

		//		- Ticket is moved to the Open status 

		assertEquals("Ticket is not in Open status", CsTicketState.OPEN, ticket.getState());
		assertEquals("Unexpected number of Events on ticket", 1, ticket.getEvents().size());

		//		- A Note is added to the Ticket as a result of some action taking to resolve the issue. 

		final String resolutionNote = "Ticket has been resolved";

		try
		{
			ticketBusinessService.resolveTicket(ticket, CsInterventionType.EMAIL, CsResolutionType.CLOSED, resolutionNote);
		}
		catch (final TicketException e)
		{
			LOG.error("Unexpected exception while resolving a ticket", e);
			fail("resolveTicket Method was not expected to throw an exception");
		}

		assertEquals("Ticket is not in Closed status", CsTicketState.CLOSED, ticket.getState());
		assertEquals("Unexpected number of Events on ticket", 2, ticket.getEvents().size());

	}

	/**
	 * Scenario 2: - A customer calls regarding a currently open ticket - The ticket is found via the reference provided
	 * by the customer - A Note is added to the Ticket recording the additional call
	 */
	@Test
	public void testScenario2()
	{
		//		- A customer calls regarding a currently open ticket 
		//		- The ticket is found via the reference provided by the customer 
		final CsTicketModel ticket = ticketService.getTicketById("test-ticket-6");

		assertNotNull("Ticket was null, could not find ticket with id 'test-ticket-6'?", ticket);
		assertEquals("Ticket is not in Open status", CsTicketState.OPEN, ticket.getState());

		//		- A Note is added to the Ticket recording the additional call
		final String testnote = "Customer called regarding ticket 'test-ticket-6', provided update";
		ticketBusinessService.addNoteToTicket(ticket, CsInterventionType.CALL, CsEventReason.UPDATE, testnote, null);

		assertEquals("Unexpected number of Events on ticket", 2, ticket.getEvents().size());
	}

	/**
	 * Scenario 3: - An email is received and a new ticket is created as a result - Customer is called regarding the
	 * email/ticket - Ticket closed as a result of the call
	 */
	@Test
	public void testScenario3()
	{
		//		- An email is received and a new ticket is created as a result 
		final CsAgentGroupModel testGroup2 = modelService.create(CsAgentGroupModel.class);
		testGroup2.setUid("TestTicketGroup22");

		final String headline = "Email recieved from customer";
		final String note = "Customer requests call regarding order";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.EMAIL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull("Ticket creation failed", ticket);

		assertEquals("Unexpected number of ticket events", 1, ticket.getEvents().size());

		final String testSubject = "Regarding My Order";
		final String testBody = "Please call me";

		final List<CsTicketState> allowedStatesFromOpen = ticketBusinessService.getTicketNextStates(ticket);

		ticketBusinessService.addCustomerEmailToTicket(ticket, CsEventReason.UPDATE, testSubject, testBody, null);

		assertEquals("Unexpected number of Events on ticket", 2, ticket.getEvents().size());
		assertTrue("Ticket event was not instanceof CsCustomerEventModel",
				ticket.getEvents().get(1) instanceof CsCustomerEventModel);

		//		- Customer is called regarding the email/ticket 

		final String testnote = "Customer called regarding ticket '" + ticket.getTicketID() + "', provided update";
		ticketBusinessService.addNoteToTicket(ticket, CsInterventionType.CALL, CsEventReason.UPDATE, testnote, null);

		assertEquals("Unexpected number of Events on ticket", 3, ticket.getEvents().size());
		assertTrue("Event is not a CsCustomerEventModel", ticket.getEvents().get(2) instanceof CsCustomerEventModel);

		// 	- Ticket closed as a result of the call

		final String resolutionNote = "Customer was called, ticket can be closed";

		assertTrue("Ticket should be able to be resolved at this point", ticketBusinessService.isTicketResolvable(ticket));

		try
		{
			ticketBusinessService.resolveTicket(ticket, CsInterventionType.CALL, CsResolutionType.CLOSED, resolutionNote);

			assertEquals("Unexpected number of Events on ticket", 4, ticket.getEvents().size());
		}
		catch (final TicketException e)
		{
			fail("resolveTicket Method was not expected to throw an exception");
		}

		if (allowedStatesFromOpen.isEmpty() && ticketBusinessService.isTicketClosed(ticket))
		{
			assertEquals("Ticket is not in Closed status", CsTicketState.CLOSED, ticket.getState());
			assertEquals("Unexpected number of Events on ticket", 4, ticket.getEvents().size());
			assertFalse("Ticket is marked as changed/dirty", getContext(ticket).getValueHistory().isDirty());
		}
		else
		{
			assertEquals("Ticket is in Closed status, configuration suggests this should not be allowed", CsTicketState.NEW,
					ticket.getState());
			assertEquals("Unexpected number of Events on ticket", 3, ticket.getEvents().size());
		}
	}

	/**
	 * Scenario 4: - A customer calls regarding a previously closed ticket - The ticket is found via the reference
	 * provided by the customer - Ticket is reopened
	 */
	@Test
	public void testScenario4()
	{
		//		- A customer calls regarding a previously closed ticket 
		//		- The ticket is found via the reference provided by the customer

		final CsTicketModel ticket = ticketService.getTicketById("test-ticket-7");

		assertNotNull("Ticket was null, could not find ticket with id 'test-ticket-7'?", ticket);
		assertEquals("Ticket is not in Closed status", CsTicketState.CLOSED, ticket.getState());

		//		- Ticket is reopened

		final int currentEventCount = ticket.getEvents().size();

		ticket.setState(CsTicketState.OPEN);

		try
		{
			ticketBusinessService.updateTicket(ticket);
		}
		catch (final TicketException e)
		{
			LOG.error("Unexpected exception in test", e);
			fail(e.getMessage());
		}

		assertEquals("Ticket is not in Open status", CsTicketState.OPEN, ticket.getState());
		assertEquals("Unexpected number of Events on ticket", currentEventCount + 1, ticket.getEvents().size());

	}

	/**
	 * Scenario 5: - A customer calls regarding a undelivered item on their order - A ticket is raised against the order
	 * - The Ticket status is updated to Open - Ticket is assigned to an Agent
	 */
	@Test
	public void testScenario5()
	{
		//		- A customer calls regarding a undelivered item on their order 
		//		- A ticket is raised against the order
		final UserModel newAgent = userService.getUser("agent2");
		assertNotNull("Could not find user 'agent2'", newAgent);

		final String headline = "Customer called regarding order";
		final String note = "Items were missing from delivery";

		final OrderModel order = testUser.getOrders().iterator().next();

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, order, CsTicketCategory.COMPLAINT,
				CsTicketPriority.HIGH, adminUser, null, headline, CsInterventionType.CALL, CsEventReason.COMPLAINT, note);

		assertNotNull("Ticket was null, ticket creation failed?", ticket);

		assertEquals("Ticket is not in Open status", CsTicketState.OPEN, ticket.getState());
		assertEquals("Unexpected number of Events on ticket", 1, ticket.getEvents().size());

		//		- Ticket is assigned to an Agent

		ticket.setAssignedAgent((EmployeeModel) newAgent);

		try
		{
			ticketBusinessService.updateTicket(ticket);
		}
		catch (final TicketException e)
		{
			LOG.error("Unexpected exception in test", e);
			fail(e.getMessage());
		}

		assertEquals("Unexpected number of Events", 2, ticket.getEvents().size());
		assertEquals("Unexpected Description text", "", ticket.getEvents().get(1).getText());

		final CsTicketEventModel changeEvent = ticket.getEvents().get(1);
		assertEquals("Unexpected Event Author", userService.getCurrentUser(), changeEvent.getAuthor());

		assertEquals("Unexpected number of change events", 1, changeEvent.getEntries().size());

		final CsTicketChangeEventEntryModel entry = changeEvent.getEntries().iterator().next();
		assertNotNull("Altered attribute is unexpectedly null", entry.getAlteredAttribute());
		assertEquals("Found unexpected altered attribute", "assignedAgent", entry.getAlteredAttribute().getQualifier());
		assertTrue("Change Event not instanceof CsTicketChangeEventEmployeeEntryModel",
				entry instanceof CsTicketChangeEventEmployeeEntryModel);

		final CsTicketChangeEventEmployeeEntryModel agentChangeEntry = (CsTicketChangeEventEmployeeEntryModel) entry;
		assertEquals("Old value of change event was not adminuser", adminUser, agentChangeEntry.getOldValue());
		assertEquals("New value of change event was not newAgent", newAgent, agentChangeEntry.getNewValue());

	}

	/**
	 * Scenario 6: - Problem Ticket Scenario, Ticket is raised and then bounced around.
	 */
	@Test
	public void testScenario6()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.COMPLAINT, note);

		assertNotNull("Ticket was null, ticket creation failed?", ticket);

		assertEquals("Ticket is not in Open status", CsTicketState.OPEN, ticket.getState());
		assertEquals("Unexpected number of Events on ticket", 1, ticket.getEvents().size());

		final EmployeeModel agent2 = (EmployeeModel) userService.getUser("agent2");
		assertNotNull("Could not find user 'agent2'", agent2);

		EmployeeModel assignTo = agent2;

		for (int i = 0; i < 8; i++)
		{
			//randomly assign the ticket
			try
			{
				ticketBusinessService.assignTicketToAgent(ticket, assignTo);
			}
			catch (final TicketException e)
			{
				LOG.error("Unexpected exception in test", e);
				fail(e.getMessage());
			}

			assertEquals("Ticket was not assigned", assignTo, ticket.getAssignedAgent());

			assignTo = (assignTo == agent2 ? adminUser : agent2);
		}

		//assigned to unassigned
		try
		{
			ticketBusinessService.assignTicketToAgent(ticket, null);
		}
		catch (final TicketException e)
		{
			LOG.error("Unexpected exception in test", e);
			fail(e.getMessage());
		}

		assertEquals("Unexpected number of Events", 10, ticket.getEvents().size());

		final CsAgentGroupModel testGroup2 = modelService.create(CsAgentGroupModel.class);
		testGroup2.setUid("TestTicketGroup22");
		try
		{
			ticketBusinessService.assignTicketToGroup(ticket, testGroup2);
		}
		catch (final TicketException e)
		{
			LOG.error("Unexpected exception in test", e);
			fail(e.getMessage());
		}

		assertEquals("Unexpected number of Events", 11, ticket.getEvents().size());

		ticket.setState(CsTicketState.CLOSED);

		try
		{
			ticketBusinessService.updateTicket(ticket);
		}
		catch (final TicketException e)
		{
			LOG.error("Unexpected exception in test", e);
			fail(e.getMessage());
		}

		assertEquals("Ticket is not in Closed status", CsTicketState.CLOSED, ticket.getState());
		assertEquals("Unexpected number of Events", 12, ticket.getEvents().size());
	}

	private ItemModelContextImpl getContext(final AbstractItemModel model)
	{
		return (ItemModelContextImpl) ModelContextUtils.getItemModelContext(model);
	}

}
