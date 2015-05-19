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
package de.hybris.platform.b2b.services.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.dao.PrincipalGroupMembersDao;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.dao.impl.DefaultB2BWorkflowActionDao;
import de.hybris.platform.b2b.dao.impl.DefaultB2BWorkflowDao;
import de.hybris.platform.b2b.mail.OrderInfoContextDtoFactory;
import de.hybris.platform.b2b.mail.impl.OrderInfoContextDto;
import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.actions.B2BPermissionResultHelperImpl;
import de.hybris.platform.b2b.services.B2BApproverService;
import de.hybris.platform.b2b.services.B2BBudgetService;
import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.b2b.services.B2BCurrencyConversionService;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderscheduling.ScheduleOrderService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.impl.DefaultTypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.spring.TenantScope;
import de.hybris.platform.workflow.WorkflowActionService;
import de.hybris.platform.workflow.WorkflowAttachmentService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowTemplateService;

import java.io.StringWriter;

import javax.mail.internet.InternetAddress;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.transaction.PlatformTransactionManager;


@Ignore
@UnitTest
public class DefaultB2BEmailServiceMockTest extends HybrisMokitoTest
{

	private final DefaultB2BEmailService b2BEmailService = new DefaultB2BEmailService();
	@Mock
	public UserService mockUserService;
	@Mock
	public BaseDao mockBaseDao;
	@Mock
	public ModelService mockModelService;
	@Mock
	public B2BUnitService<B2BUnitModel, B2BCustomerModel> mockB2bUnitService;
	@Mock
	public SessionService mockSessionService;
	@Mock
	public CommonI18NService mockCommonI18NService;
	@Mock
	public DefaultB2BWorkflowDao mockB2BWorkflowDao;
	@Mock
	public DefaultB2BWorkflowActionDao mockB2BWorkflowActionDao;
	@Mock
	public TenantScope mockTenantScope;
	@Mock
	public WorkflowActionService mockwWorkflowActionService;
	@Mock
	public WorkflowAttachmentService mockwWorkflowAttachmentService;
	@Mock
	public WorkflowProcessingService mockWorkflowProcessingService;
	@Mock
	public WorkflowService mockWorkflowService;
	@Mock
	public WorkflowTemplateService mockWorkflowTemplateService;
	@Mock
	public PlatformTransactionManager mockTxManager;
	@Mock
	public OrderInfoContextDtoFactory orderInfoContextDtoFactory;
	@Mock
	public RendererService rendererService;
	@Mock
	public B2BCurrencyConversionService mockB2BCurrencyConversionService;
	@Mock
	public B2BBudgetService<B2BBudgetModel, B2BCustomerModel> b2BudgetService;
	@Mock
	public B2BCartService mockB2BCartService;
	@Mock
	public B2BCustomerService<B2BCustomerModel, B2BUnitModel> mockB2BCustomerService;
	@Mock
	public ScheduleOrderService mockScheduleOrderService;
	@Mock
	public B2BApproverService mockB2BApproverService;
	@Mock
	public B2BPermissionResultHelperImpl mockB2BPermissionResultHelperImpl;
	@Mock
	public DefaultTypeService mockTypeService;
	@Mock
	public PrincipalGroupMembersDao mockPrincipalGroupMemberDao;
	@Mock
	public RendererTemplateModel rendererTemplateModel;
	@Mock
	public StringWriter mailMessageWriter;

	@Before
	public void setUp() throws Exception
	{
		Registry.activateMasterTenant();
		b2BEmailService.setModelService(mockModelService);
		b2BEmailService.setSessionService(mockSessionService);
		b2BEmailService.setTenantScope(mockTenantScope);
		b2BEmailService.setTxManager(mockTxManager);
		b2BEmailService.setOrderInfoContextDtoFactory(orderInfoContextDtoFactory);
		b2BEmailService.setRendererService(rendererService);
		Mockito.when(rendererService.getRendererTemplateForCode(anyString())).thenReturn(rendererTemplateModel);

	}

	@Test
	public void testCreateOrderApprovalEmail() throws Exception
	{
		final InternetAddress from = new InternetAddress("test@test.com");
		final String emailTemplateCode = "order_confirmation";
		final String subject = "Order Approved";
		final OrderModel order = mock(OrderModel.class);
		final OrderInfoContextDto ctx = mock(OrderInfoContextDto.class);
		final B2BCustomerModel user = mock(B2BCustomerModel.class);
		Mockito.when(user.getEmail()).thenReturn("test@test.com");
		Mockito.when(ctx.getOrderNumber()).thenReturn("123");
		Mockito.when(orderInfoContextDtoFactory.createOrderInfoContextDto(order)).thenReturn(ctx);
		b2BEmailService.createOrderApprovalEmail(emailTemplateCode, order, user, from, subject);
		doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation)
			{
				final Object[] args = invocation.getArguments();
				((StringWriter) args[2]).append("fake email body");
				invocation.getMock();
				return null;
			}
		}).when(rendererService).render(any(RendererTemplateModel.class), any(OrderInfoContextDto.class), any(StringWriter.class));

	}

	@Test
	public void testCreateOrderRejectionEmail() throws Exception
	{
		final InternetAddress from = new InternetAddress("test@test.com");
		final String emailTemplateCode = "order_rejection";
		final String subject = "Order Rejected";
		final OrderModel order = mock(OrderModel.class);
		final OrderInfoContextDto ctx = mock(OrderInfoContextDto.class);
		final B2BCustomerModel user = mock(B2BCustomerModel.class);
		Mockito.when(user.getEmail()).thenReturn("test@test.com");
		Mockito.when(ctx.getOrderNumber()).thenReturn("123");
		Mockito.when(orderInfoContextDtoFactory.createOrderInfoContextDto(order)).thenReturn(ctx);
		b2BEmailService.createOrderRejectionEmail(emailTemplateCode, order, user, from, subject);

	}
}
