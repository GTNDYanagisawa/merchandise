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

import static org.junit.Assert.assertNotNull;

import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsAgentGroupModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.ticket.strategies.TicketEventEmailStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.log4j.Logger;


/**
 * 
 */
public class AbstractTicketsystemTest extends ServicelayerTransactionalTest
{
	@Resource
	protected TicketBusinessService ticketBusinessService;
	@Resource
	protected UserService userService;
	@Resource
	protected ModelService modelService;
	@Resource
	protected I18NService i18nService;

	protected CsAgentGroupModel testGroup;

	protected EmployeeModel adminUser;

	protected UserModel testUser;

	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createHardwareCatalog();

		prepareApplicationContextAndSession();

		i18nService.setCurrentLocale(Locale.ENGLISH);

		createDefaultUsers();

		importCsv("/impex/testData.impex", "utf-8");
		importCsv("/impex/essentialdata_emailTemplates.impex", "utf-8");
		importCsv("/impex/eventRenderTemplates.impex", "utf-8");

		adminUser = (EmployeeModel) userService.getUserForUID("csagent");
		testGroup = (CsAgentGroupModel) userService.getUserGroupForUID("testTicketGroup1");

		testUser = userService.getUserForUID("ariel");
		assertNotNull("Could not find user 'ariel'", testUser);
	}


	/**
	 * Inner class used for mocking the mail strategy for most tests.
	 */
	public static class MockTicketEventEmailStrategy extends AbstractBusinessService implements TicketEventEmailStrategy
	{
		private static final Logger LOG = Logger.getLogger(MockTicketEventEmailStrategy.class);

		private final List<CsTicketEventModel> events = new ArrayList<CsTicketEventModel>();

		@Override
		public void sendEmailsForEvent(final CsTicketModel ticket, final CsTicketEventModel event)
		{
			LOG.info("Send email for event [" + event + "]");
			events.add(event);
		}

		public List<CsTicketEventModel> getEvents()
		{
			return Collections.unmodifiableList(events);
		}

		public void reset()
		{
			events.clear();
		}

	}

}
