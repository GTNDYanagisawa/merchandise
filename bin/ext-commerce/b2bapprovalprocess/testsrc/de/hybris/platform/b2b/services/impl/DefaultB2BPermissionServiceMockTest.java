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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.dao.PrincipalGroupMembersDao;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.dao.impl.DefaultB2BWorkflowActionDao;
import de.hybris.platform.b2b.dao.impl.DefaultB2BWorkflowDao;
import de.hybris.platform.b2b.mail.OrderInfoContextDtoFactory;
import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.actions.B2BPermissionResultHelperImpl;
import de.hybris.platform.b2b.services.B2BApproverService;
import de.hybris.platform.b2b.services.B2BBudgetService;
import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.b2b.services.B2BCurrencyConversionService;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commons.renderer.RendererService;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * Mock test for the B2BPermissionService
 */

@UnitTest
public class DefaultB2BPermissionServiceMockTest extends HybrisMokitoTest
{

	DefaultB2BPermissionService defaultB2BPermissionService = new DefaultB2BPermissionService();
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

	@Before
	public void setUp() throws Exception
	{
		defaultB2BPermissionService.setB2bApproverService(mockB2BApproverService);
		defaultB2BPermissionService.setPermissionResultHelper(mockB2BPermissionResultHelperImpl);
		defaultB2BPermissionService.setBaseDao(mockBaseDao);
	}

	@Test
	public void testEvaluatePermissions() throws Exception
	{
		//TODO: implement test

	}

	@Test
	@Ignore
	public void testGetApproversForOpenPermissions() throws Exception
	{
		//final AbstractOrderModel mockAbstractOrderModel = Mockito.mock(AbstractOrderModel.class);
		final B2BCustomerModel mockB2BCustomerModel = Mockito.mock(B2BCustomerModel.class);
		//final B2BPermissionModel mockB2BPermissionModel = Mockito.mock(B2BPermissionModel.class);
		//final B2BPermissionResultModel mockB2BPermissionResultModel = Mockito.mock(B2BPermissionResultModel.class);

		Mockito.when(mockB2BApproverService.getAllApprovers(mockB2BCustomerModel)).thenReturn(
				Collections.singletonList(mockB2BCustomerModel));

		//		Mockito.when(mockB2BPermissionResultHelperImpl.extractPermissionTypes(Collections.singleton(mockB2BPermissionResultModel)))
		//				.thenReturn(Collections.singleton(mockB2BPermissionModel));


		// assert here
		//		defaultB2BPermissionService.getApproversForOpenPermissions(mockAbstractOrderModel, mockB2BCustomerModel,
		//				Collections.singleton(mockB2BPermissionResultModel));

	}

	@Test
	@Ignore
	public void testNeedsApproval() throws Exception
	{
		final OrderModel mockOrder = mock(OrderModel.class);
		defaultB2BPermissionService.needsApproval(mockOrder);
	}

	@Test
	public void testGetEligableApprovers() throws Exception
	{
		//TODO: implement test

	}

	@Test
	public void testGetOpenPermissions() throws Exception
	{
		//TODO: implement test

	}

	@Test
	public void testGetB2BPermissionByCode() throws Exception
	{
		final String code1 = "code1";
		final B2BPermissionModel mockB2BPermissionModel = Mockito.mock(B2BPermissionModel.class);
		Mockito.when(mockBaseDao.findFirstByAttribute("code", code1, B2BPermissionModel.class)).thenReturn(mockB2BPermissionModel);
		assertThat(defaultB2BPermissionService.getB2BPermissionForCode(code1), equalTo(mockB2BPermissionModel));
	}

	@Test
	public void testGetAllB2BPermissions() throws Exception
	{
		final B2BPermissionModel mockB2BPermissionModel = Mockito.mock(B2BPermissionModel.class);
		final List<B2BPermissionModel> permissions = new ArrayList<B2BPermissionModel>(1);
		permissions.add(mockB2BPermissionModel);
		when(mockBaseDao.findAll(-1, 0, B2BPermissionModel.class)).thenReturn(permissions);
		assertThat(defaultB2BPermissionService.getAllB2BPermissions(), equalTo(Collections.singleton(mockB2BPermissionModel)));
	}

	@Test
	public void testPermissionExists() throws Exception
	{
		//TODO: implement test

	}

	@Test
	public void testFindAllB2BPermissionTypes() throws Exception
	{
		//TODO: implement test

	}
}
