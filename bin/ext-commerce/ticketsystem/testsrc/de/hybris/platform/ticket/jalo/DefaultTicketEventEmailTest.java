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

import de.hybris.basecommerce.SimpleSmtpServerUtils;
import de.hybris.basecommerce.SimpleSmtpServerUtils.SimpleSmtpServer;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ticket.enums.CsEventReason;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsResolutionType;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketException;
import de.hybris.platform.util.Config;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.dumbster.smtp.SmtpMessage;


@Ignore
public class DefaultTicketEventEmailTest extends AbstractTicketsystemTest
{
	private static final Logger LOG = Logger.getLogger(DefaultTicketEventEmailTest.class.getName());

	protected SimpleSmtpServer smtpServer;

	private String origMailPortNumber;
	private String origMailHost;

	@Before
	public void initSMTPserver() throws Exception
	{
		super.setUp();
		LOG.info("setting up simple SMTP server");

		//remembering original config
		origMailPortNumber = Config.getParameter(Config.Params.MAIL_SMTP_PORT);
		origMailHost = Config.getParameter(Config.Params.MAIL_SMTP_SERVER);

		smtpServer = SimpleSmtpServerUtils.startServer(2500, true);
		final int port = smtpServer.getPort();

		Config.setParameter(Config.Params.MAIL_SMTP_SERVER, "localhost");
		Config.setParameter(Config.Params.MAIL_SMTP_PORT, String.valueOf(port));

		LOG.info("Using test smtp server settings: localhost:" + port);
	}

	@After
	public void tearDown()
	{
		//restoring original config
		Config.setParameter(Config.Params.MAIL_SMTP_SERVER, origMailHost);
		Config.setParameter(Config.Params.MAIL_SMTP_PORT, origMailPortNumber);
		LOG.info("restoring original smtp server config: " + origMailHost + ":" + origMailPortNumber);
		if (smtpServer != null && !smtpServer.isStopped())
		{
			smtpServer.stop();
		}
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

		assertEquals(1, smtpServer.getReceivedEmailSize());

		final Iterator emailIter = smtpServer.getReceivedEmail();
		final SmtpMessage email = (SmtpMessage) emailIter.next();
		assertEquals("Ticket Created", email.getHeaderValue("Subject"));
		assertTrue(email.getBody().contains(ticket.getTicketID()));
		assertTrue(email.getHeaderValue("To").contains(testUser.getDefaultPaymentAddress().getEmail()));
	}

	@Test
	public void testUpdateTicketAssignedAgentThroughUpdateTicket()
	{
		final UserModel newAgent = userService.getUser("agent2");
		assertNotNull("Could not find user 'agent2'", newAgent);

		if (newAgent.getDefaultPaymentAddress() == null)
		{
			final AddressModel address = modelService.create(AddressModel.class);
			address.setEmail("foo@bar.com");

			newAgent.setDefaultPaymentAddress(address);
		}

		final CsTicketModel ticket = ticketBusinessService
				.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW, adminUser, testGroup, "Ticket Headline",
						CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, "Ticket Creation Notes");
		assertNotNull(ticket);

		ticket.setAssignedAgent((EmployeeModel) newAgent);

		try
		{
			ticketBusinessService.updateTicket(ticket);
		}
		catch (final TicketException e)
		{
			LOG.error("Unexpected exception while updating a ticket", e);
			fail("updateTicket Method was not expected to throw an exception");
		}

		assertEquals(2, smtpServer.getReceivedEmailSize());

		final Iterator emailIter = smtpServer.getReceivedEmail();

		SmtpMessage email = (SmtpMessage) emailIter.next();
		assertTrue("Ticket Created".equals(email.getHeaderValue("Subject")));
		assertTrue(email.getBody().contains(ticket.getTicketID()));
		assertTrue(email.getHeaderValue("To").contains(testUser.getDefaultPaymentAddress().getEmail()));

		email = (SmtpMessage) emailIter.next();
		assertTrue("Ticket Assigned".equals(email.getHeaderValue("Subject")));
		assertTrue(email.getBody().contains(ticket.getTicketID()));
		assertTrue(email.getHeaderValue("To").contains(newAgent.getDefaultPaymentAddress().getEmail()));
	}

	@Test
	public void testSendTicketEmail()
	{
		final CsTicketModel ticket = ticketBusinessService
				.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW, adminUser, testGroup, "Ticket Headline",
						CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, "Ticket Creation Notes");
		assertNotNull(ticket);

		final String emailSubject = "Email subject";
		final String emailBody = "Hello, this is the body of the email";

		ticketBusinessService.addCustomerEmailToTicket(ticket, CsEventReason.UPDATE, emailSubject, emailBody, null);

		assertEquals(2, smtpServer.getReceivedEmailSize());

		final Iterator emailIter = smtpServer.getReceivedEmail();

		SmtpMessage email = (SmtpMessage) emailIter.next();
		assertTrue("Ticket Created".equals(email.getHeaderValue("Subject")));
		assertTrue(email.getBody().contains(ticket.getTicketID()));
		assertTrue(email.getHeaderValue("To").contains(testUser.getDefaultPaymentAddress().getEmail()));

		email = (SmtpMessage) emailIter.next();
		assertTrue(email.getBody().contains(emailBody));
		assertTrue(email.getHeaderValue("To").contains(testUser.getDefaultPaymentAddress().getEmail()));
		assertTrue(email.getHeaderValue("Subject").equals(emailSubject));
	}

	@Test
	public void testResolveTicket()
	{
		final CsTicketModel ticket = ticketBusinessService
				.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW, adminUser, testGroup, "Ticket Headline",
						CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, "Ticket Creation Notes");
		assertNotNull(ticket);

		final String resolutionNote = "Hello, this is the resolution note";

		try
		{
			ticketBusinessService.resolveTicket(ticket, CsInterventionType.EMAIL, CsResolutionType.CLOSED, resolutionNote);
		}
		catch (final TicketException e)
		{
			LOG.error("Unexpected exception while resolving a ticket", e);
			fail("resolveTicket Method was not expected to throw an exception");
		}

		assertEquals(2, smtpServer.getReceivedEmailSize());

		final Iterator emailIter = smtpServer.getReceivedEmail();

		SmtpMessage email = (SmtpMessage) emailIter.next();
		assertTrue("Ticket Created".equals(email.getHeaderValue("Subject")));
		assertTrue(email.getBody().contains(ticket.getTicketID()));
		assertTrue(email.getHeaderValue("To").contains(testUser.getDefaultPaymentAddress().getEmail()));

		email = (SmtpMessage) emailIter.next();
		assertFalse(email.getBody().contains(resolutionNote));
		assertTrue(email.getBody().contains(ticket.getTicketID()));
		assertTrue(email.getHeaderValue("To").contains(testUser.getDefaultPaymentAddress().getEmail()));
		assertTrue("Ticket Resolved".equals(email.getHeaderValue("Subject")));
	}

	@Test
	public void testAddNoteToTicket()
	{
		final CsTicketModel ticket = ticketBusinessService
				.createTicket(testUser, CsTicketCategory.NOTE, CsTicketPriority.LOW, adminUser, testGroup, "Ticket Headline",
						CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, "Ticket Creation Notes");
		assertNotNull(ticket);

		final String note = "Hello, this is the resolution note";

		ticketBusinessService.addNoteToTicket(ticket, CsInterventionType.EMAIL, CsEventReason.UPDATE, note, null);

		assertEquals(1, smtpServer.getReceivedEmailSize());

		final Iterator emailIter = smtpServer.getReceivedEmail();

		final SmtpMessage email = (SmtpMessage) emailIter.next();
		assertTrue("Ticket Created".equals(email.getHeaderValue("Subject")));
		assertTrue(email.getBody().contains(ticket.getTicketID()));
		assertTrue(email.getHeaderValue("To").contains(testUser.getDefaultPaymentAddress().getEmail()));
	}

}
