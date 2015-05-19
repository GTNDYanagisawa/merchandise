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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTest;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.actions.B2BPermissionResultHelperImpl;
import de.hybris.platform.b2b.process.approval.actions.EscalationTaskRunner;
import de.hybris.platform.b2b.services.B2BApproverService;
import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BEmailService;
import de.hybris.platform.b2b.services.B2BEscalationService;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.b2b.util.B2BDateUtils;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.validation.services.ValidationService;
import de.hybris.platform.workflow.WorkflowActionService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowTemplateService;
import java.util.Locale;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@Ignore("BTOBA-30")
@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class B2BPermissionServiceTest extends B2BIntegrationTest
{
	@Resource
	public B2BApproverService<B2BCustomerModel> b2bApproverService;
	@Resource
	public B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;
	@Resource
	public B2BEscalationService b2bEscalationService;
	@Resource
	public CommonI18NService commonI18NService;
	@Resource
	public DefaultB2BPermissionService b2bPermissionService;
	@Resource
	public BaseDao baseDao;
	@Resource
	public ModelService modelService;
	@Resource
	public B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	@Resource
	public KeyGenerator orderCodeGenerator;
	@Resource
	public UserService userService;
	@Resource
	public BusinessProcessService businessProcessService;
	@Resource
	public B2BWorkflowIntegrationService b2bWorkflowIntegrationService;
	@Resource
	public ProcessParameterHelper processParameterHelper;
	@Resource
	public CartFactory b2bCartFactory;
	@Resource
	public B2BCartService b2bCartService;
	@Resource
	public B2BOrderService b2bOrderService;
	@Resource
	public ProductService productService;
	@Resource
	public EscalationTaskRunner escalationTaskRunner;
	@Resource
	public B2BCostCenterService b2bCostCenterService;
	@Resource
	public B2BPermissionResultHelperImpl permissionResultHelper;
	@Resource
	public SessionService sessionService;
	@Resource
	public I18NService i18nService;
	@Resource
	public KeyGenerator b2bProcessCodeGenerator;
	@Resource
	public WorkflowActionService workflowActionService;
	@Resource
	public B2BEmailService b2bEmailService;
	@Resource
	public CalculationService calculationService;
	@Resource
	public SearchRestrictionService searchRestrictionService;
	@Resource
	public WorkflowProcessingService workflowProcessingService;
	@Resource
	public WorkflowService newestWorkflowService;
	@Resource
	public FlexibleSearchService flexibleSearchService;
	@Resource
	public B2BDateUtils b2bDateUtils;
	@Resource
	public WorkflowTemplateService workflowTemplateService;
	@Resource
	public ValidationService validationService;

	@Before
	public void before() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/organizationdata.csv", "UTF-8");

		sessionService.getCurrentSession().setAttribute("user",
				this.modelService.<Object> toPersistenceLayer(userService.getAdminUser()));
		i18nService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));
	}


	@Test
	public void testNeedsApprovalShouldReturnFalse() throws Exception
	{
		final OrderModel order = this.createOrder("GC S Det", 1, OrderStatus.CREATED);
		Assert.assertNotNull(order);
		Thread.sleep(20000);
		this.modelService.refresh(order);
		Assert.assertFalse("order should not need approval", this.b2bPermissionService.needsApproval(order));
	}

	@Test
	public void testNeedsApprovalShouldReturnTrue() throws Exception
	{
		final OrderModel order = this.createOrder("GC S Det", 100, OrderStatus.CREATED);
		Assert.assertNotNull(order);
		Thread.sleep(20000);
		this.modelService.refresh(order);
		Assert.assertTrue("order should not need approval", this.b2bPermissionService.needsApproval(order));
	}

	@Test
	public void testShouldGetEligableApprovers() throws Exception
	{
		// order total is 10000
		final OrderModel order = this.createOrder("GC S Det", 10, OrderStatus.CREATED);
		Assert.assertNotNull(order);
		this.modelService.refresh(order);
		Assert.assertEquals("wrong number of eligable approvers found", 3, this.b2bPermissionService.getEligableApprovers(order)
				.size());
	}

	@Test
	public void testShouldGetNoEligableApprovers() throws Exception
	{
		// order total is 7.999999992E10! which exceeds the 9.99999999E8 threshold. 
		final OrderModel order = this.createOrder(999999999);
		Assert.assertNotNull(order);
		this.modelService.refresh(order);
		Assert.assertEquals("wrong number of eligable approvers found", 0, this.b2bPermissionService.getEligableApprovers(order)
				.size());
	}
}
