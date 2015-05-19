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
package de.hybris.platform.ticket.event.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.basecommerce.jalo.AbstractOrderManagementTest;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.fraud.events.OrderFraudEmployeeNotificationEvent;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.ticket.dao.TicketDao;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.model.CsAgentGroupModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.util.localization.Localization;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author krzysztof.kwiatosz
 * 
 */
@Ignore
public class DefaultOrderFraudEmployeeNotificationEventListenerTest extends AbstractOrderManagementTest
{


	private static final String TEST_FRAUD_GROUP = "fraudTestGroup";
	@Resource
	private DefaultOrderFraudEmployeeNotificationEventListener defaultOrderFraudEmployeeNotificationEventListener; //NOPMD
	@Resource
	private TicketDao ticketDao;
	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;
	@Resource
	private TicketBusinessService ticketBusinessService; //NOPMD


	private CsAgentGroupModel defaultFraudGroup;
	private CsAgentGroupModel testFraudGroup;

	private OrderFraudEmployeeNotificationEvent event;


	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		//listener = new DefaultOrderFraudEmployeeNotificationEventListener();

		testFraudGroup = modelService.create(CsAgentGroupModel.class);
		testFraudGroup.setUid(TEST_FRAUD_GROUP);

		defaultFraudGroup = modelService.create(CsAgentGroupModel.class);
		defaultFraudGroup.setUid(DefaultOrderFraudEmployeeNotificationEventListener.DEFAULT_FRAUD_USER_GROUP);

		final DomainModel domain = modelService.create(DomainModel.class);
		domain.setCode("ticketSystemDomain");
		domain.setName("Ticket System Domain");

		final ComponentModel component = modelService.create(ComponentModel.class);
		component.setCode("ticketSystem");
		component.setName("Ticket System");
		component.setDomain(domain);

		final Collection<PrincipalModel> testGroup = Collections.<PrincipalModel> singletonList(defaultFraudGroup);

		component.setReadPermitted(testGroup);
		component.setWritePermitted(testGroup);
		component.setCreatePermitted(testGroup);
		component.setRemovePermitted(testGroup);

		final CommentTypeModel commentType = modelService.create(CommentTypeModel.class);
		commentType.setCode("ticketCreationEvent");
		commentType.setName("Ticket Created Event");
		commentType.setDomain(domain);
		commentType.setMetaType(typeService.getComposedTypeForCode(CsCustomerEventModel._TYPECODE));

		modelService.saveAll(testFraudGroup, defaultFraudGroup, domain, component, commentType);

		event = new OrderFraudEmployeeNotificationEvent(order);

	}

	@Test
	public void testCreateCsTicketDefaultAgentGroup()
	{
		final CsTicketModel csTicketModel = assertFraudTicket();
		Assert.assertEquals(defaultFraudGroup, csTicketModel.getAssignedGroup());
	}

	@Test
	public void testCreateCsTicketCustomAgentGroup()
	{
		defaultOrderFraudEmployeeNotificationEventListener.setFraudUserGroup(TEST_FRAUD_GROUP);
		final CsTicketModel csTicketModel = assertFraudTicket();
		Assert.assertEquals(testFraudGroup, csTicketModel.getAssignedGroup());
	}

	@Test
	public void testCreateCsTicketDefaultPriority()
	{
		final CsTicketModel csTicketModel = assertFraudTicket();
		Assert.assertEquals(DefaultOrderFraudEmployeeNotificationEventListener.DEFAULT_PRIORITY, csTicketModel.getPriority());
	}

	@Test
	public void testCreateCsTicketCustomPriority()
	{
		defaultOrderFraudEmployeeNotificationEventListener.setPriority(CsTicketPriority.LOW);
		final CsTicketModel csTicketModel = assertFraudTicket();
		Assert.assertEquals(CsTicketPriority.LOW, csTicketModel.getPriority());
	}

	@Test
	public void testCreateCsTicketDefaultText()
	{
		final String defaultHeadline = Localization.getLocalizedString("csticket.fraud.headline.default", new Object[]
		{ order.getCode() });
		final String defaultContent = Localization.getLocalizedString("csticket.fraud.content.default");
		final CsTicketModel csTicketModel = assertFraudTicket();
		Assert.assertEquals(defaultHeadline, csTicketModel.getHeadline());
		assertThat(csTicketModel.getComments()).hasSize(1);
		Assert.assertEquals(defaultContent, csTicketModel.getComments().get(0).getText());
	}


	@Test(expected = IllegalArgumentException.class)
	public void testCreateCsTicketForNullEvent()
	{
		defaultOrderFraudEmployeeNotificationEventListener.onEvent(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateCsTicketForNullOrder()
	{
		defaultOrderFraudEmployeeNotificationEventListener.onEvent(new OrderFraudEmployeeNotificationEvent(null));
	}

	private CsTicketModel assertFraudTicket()
	{
		assertThat(ticketDao.findTicketsByOrder(order)).isEmpty();
		defaultOrderFraudEmployeeNotificationEventListener.onEvent(event);
		final List<CsTicketModel> findTicketsByOrder = ticketDao.findTicketsByOrder(order);
		assertThat(findTicketsByOrder).hasSize(1);
		final CsTicketModel csTicketModel = findTicketsByOrder.get(0);
		Assert.assertEquals(order, csTicketModel.getOrder());
		Assert.assertEquals(order.getUser(), csTicketModel.getCustomer());
		Assert.assertEquals(CsTicketCategory.FRAUD, csTicketModel.getCategory());
		return csTicketModel;
	}


	@Override
	public Logger getLogger()
	{
		return Logger.getLogger(this.getClass());
	}
}
