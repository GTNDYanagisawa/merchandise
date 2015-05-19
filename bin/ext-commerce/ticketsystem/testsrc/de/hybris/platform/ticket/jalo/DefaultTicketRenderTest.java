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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.ticket.enums.CsEventReason;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.impl.DefaultTicketBusinessService;
import de.hybris.platform.ticket.strategies.TicketEventEmailStrategy;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class DefaultTicketRenderTest extends AbstractTicketsystemTest
{
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
	public void testCreateTicketNoteRender()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		final CsTicketModel ticket = ticketBusinessService.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW,
				adminUser, testGroup, headline, CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, note);
		assertNotNull(ticket);

		assertEquals(CsTicketState.OPEN, ticket.getState());
		assertTrue(ticket.getCustomer().equals(testUser));
		assertNull(ticket.getOrder());
		assertTrue(ticket.getHeadline().equals(headline));
		assertTrue(StringUtils.isNotEmpty(ticket.getTicketID()));
		assertTrue(ticket.getAssignedAgent().equals(adminUser));
		assertTrue(ticket.getAssignedGroup().equals(testGroup));
		assertTrue(ticket.getPriority().equals(CsTicketPriority.LOW));
		assertTrue(ticket.getCategory().equals(CsTicketCategory.NOTE));
		assertEquals(1, ticket.getEvents().size());
		assertTrue(ticket.getEvents().get(0) instanceof CsCustomerEventModel);
		final CsCustomerEventModel creationEvent = (CsCustomerEventModel) ticket.getEvents().get(0);
		assertEquals(note, creationEvent.getText());
		assertEquals(CsInterventionType.CALL, creationEvent.getInterventionType());
		assertEquals(CsEventReason.FIRSTCONTACT, creationEvent.getReason());

		assertEquals("Ticket Created: " + note,
				ticketBusinessService.renderTicketEventText(ticketBusinessService.getLastEvent(ticket)));
	}


}