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

import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.service.TicketSearchService;
import de.hybris.platform.ticket.service.impl.DefaultTicketBusinessService;
import de.hybris.platform.ticket.strategies.TicketEventEmailStrategy;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * @author chris
 * 
 */
public class DefaultTicketSearchServiceTest extends AbstractTicketsystemTest
{
	private TicketSearchService ticketSearchService;
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
		ticketSearchService = getApplicationContext().getBean("defaultTicketSearchService", TicketSearchService.class);
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
	public void testDefaultTicketSearchService()
	{
		assertNotNull("Ticket Service Not Found", ticketSearchService);
	}

	@Test
	public void testSearchForTickets()
	{
		assertEquals("Unexpected number of results for searchForTickets(\"Headline\")", 3,
				ticketSearchService.searchForTickets("Headline").size());
		assertEquals("Unexpected number of results for searchForTickets(\"Ticket\")", 7,
				ticketSearchService.searchForTickets("Ticket").size());
		assertEquals("Unexpected number of results for searchForTickets(\"Closed\")", 1,
				ticketSearchService.searchForTickets("Closed").size());
		assertEquals("Unexpected number of results for searchForTickets(\"note\")", 4, ticketSearchService.searchForTickets("note")
				.size());
		//		assertEquals("Unexpected number of results for searchForTickets(\"Note\")", 4, ticketSearchService.searchForTickets("Note")
		//				.size());
	}

	@Test
	public void testSearchForTicketsBySearchStringState()
	{
		final Set<CsTicketState> stateNewOnly = new HashSet<CsTicketState>();
		stateNewOnly.add(CsTicketState.NEW);

		final Set<CsTicketState> stateOpenClosed = new HashSet<CsTicketState>();
		stateOpenClosed.add(CsTicketState.OPEN);
		stateOpenClosed.add(CsTicketState.CLOSED);

		assertEquals("Unexpected number of results for searchForTickets(\"Headline\")", 3,
				ticketSearchService.searchForTickets("Headline", stateNewOnly).size());
		assertEquals("Unexpected number of results for searchForTickets(\"Ticket\")", 5,
				ticketSearchService.searchForTickets("Ticket", stateNewOnly).size());
		assertEquals("Unexpected number of results for searchForTickets(\"Closed\")", 0,
				ticketSearchService.searchForTickets("Closed", stateNewOnly).size());
		assertEquals("Unexpected number of results for searchForTickets(\"note\")", 3,
				ticketSearchService.searchForTickets("note", stateNewOnly).size());
		assertEquals("Unexpected number of results for searchForTickets(\"Note\")", 1,
				ticketSearchService.searchForTickets("Note", stateOpenClosed).size());
	}

	@Test
	public void testSearchForTicketsByAgentAgentGroupState()
	{
		assertEquals("Unexpected number of results for searchForTickets(null, null, null)", 0, ticketSearchService
				.searchForTickets(null, null, null).size());
		assertEquals("Unexpected number of results for searchForTickets(csagent, null, null)", 6, ticketSearchService
				.searchForTickets(adminUser, null, null).size());
		assertEquals("Unexpected number of results for searchForTickets(null, testTicketGroup, null)", 6, ticketSearchService
				.searchForTickets(null, testGroup, null).size());
		assertEquals("Unexpected number of results for searchForTickets(null, null, CsTicketState.NEW)", 5, ticketSearchService
				.searchForTickets(null, null, CsTicketState.NEW).size());
		assertEquals("Unexpected number of results for searchForTickets(admin, testTicketGroup, null)", 5, ticketSearchService
				.searchForTickets(adminUser, testGroup, null).size());
		assertEquals("Unexpected number of results for searchForTickets(null, testTicketGroup, CsTicketState.NEW)", 4,
				ticketSearchService.searchForTickets(null, testGroup, CsTicketState.NEW).size());
		assertEquals("Unexpected number of results for searchForTickets(admin, null, CsTicketState.NEW)", 4, ticketSearchService
				.searchForTickets(adminUser, null, CsTicketState.NEW).size());
		assertEquals("Unexpected number of results for searchForTickets(admin, testTicketGroup, CsTicketState.NEW)", 3,
				ticketSearchService.searchForTickets(adminUser, testGroup, CsTicketState.NEW).size());
	}
}
