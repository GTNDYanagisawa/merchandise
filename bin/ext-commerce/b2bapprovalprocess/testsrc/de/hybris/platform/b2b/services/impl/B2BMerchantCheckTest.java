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
import de.hybris.platform.b2b.WorkflowIntegrationTest;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.enums.B2BPeriodRange;
import de.hybris.platform.b2b.model.B2BCreditLimitModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.actions.B2BPermissionResultHelperImpl;
import de.hybris.platform.b2b.process.approval.actions.EscalationTaskRunner;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
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
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.StandardDateRange;
import de.hybris.platform.validation.services.ValidationService;
import de.hybris.platform.workflow.WorkflowActionService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class B2BMerchantCheckTest extends WorkflowIntegrationTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(B2BMerchantCheckTest.class);
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
	public void beforeTest() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
		importCsv("/b2bapprovalprocess/test/creditlimit.impex", "UTF-8");

		sessionService.getCurrentSession().setAttribute("user",
				this.modelService.<Object> toPersistenceLayer(userService.getAdminUser()));
		i18nService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("EUR"));
	}

	@Test
	public void shouldStartApprovalProcessAndAssertApprovalFromMerchant() throws Exception
	{
		login("GC S HH");

		//Set up credit limit data for test	
		final B2BUnitModel unitLoggedIn = b2bUnitService.getUnitForUid("GC Sales Hamburg");
		final B2BCreditLimitModel creditLimitToUse = b2bUnitService.getParent(unitLoggedIn).getCreditLimit();
		creditLimitToUse.setActive(Boolean.TRUE);
		creditLimitToUse.setDateRange(B2BPeriodRange.DAY);
		creditLimitToUse.setDatePeriod(null);
		modelService.save(creditLimitToUse);

		final OrderModel order = createOrder(this.login("GC S HH"), 140, OrderStatus.CREATED,
				productService.getProductForCode("testProduct0"));
		Assert.assertNotNull(order);

		final B2BApprovalProcessModel b2bApprovalProcessModel = getB2BApprovalProcessModelForOrder(order);

		final B2BUnitModel unitWithCreditLimit = b2bUnitService.getUnitWithCreditLimit(order.getUnit());

		Assert.assertEquals("GC Sales DE", unitWithCreditLimit.getUid());

		if (this.waitForProcessAction(b2bApprovalProcessModel.getCode(), "waitProcess", 20000))
		{
			modelService.refresh(order);
			final Collection<WorkflowActionModel> actions = b2bWorkflowIntegrationService.getStartWorkflowActions(order
					.getWorkflow());
			Assert.assertEquals(1, actions.size());
			b2bWorkflowIntegrationService.decideAction(actions.iterator().next(),
					B2BWorkflowIntegrationService.DECISIONCODES.APPROVE.name());
		}

		if (this.waitForProcessAction(b2bApprovalProcessModel.getCode(), "waitProcessCreditLimit", 20000))
		{
			modelService.refresh(order);
			final Collection<WorkflowActionModel> actions = b2bWorkflowIntegrationService.getStartWorkflowActions(order
					.getWorkflow());
			Assert.assertEquals(1, actions.size());
			this.approveWorkflowAction(actions.iterator().next());
		}

		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 20000);
		this.modelService.refresh(order);
		this.modelService.refresh(b2bApprovalProcessModel);
		Assert.assertEquals(OrderStatus.APPROVED, order.getStatus());
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel.getProcessState());
	}

	@Test
	@Ignore("breaks in https://bamboo.hybris.com/download/HYBRISACCELERATORR-B2BACCELERATOR-BUILD, the test needs to be refactored.")
	public void shouldTriggerCreditAlertOnceForTimePeriod() throws Exception
	{
		/**
		 * Create and order between 8000 - 10000 EUR for unit GC Sales DE Alert should be created for GC Sales Rep
		 */

		login("GC Sales DE Boss");

		final OrderModel order = createOrder("GC Sales DE Boss", 900, OrderStatus.CREATED);
		b2bCartService.removeSessionCart();
		Assert.assertNotNull(order);

		//Set up credit limit data for test - should not have alert sent date
		final B2BUnitModel unitLoggedIn = b2bUnitService.getUnitForUid("GC Sales DE");
		final B2BCreditLimitModel creditLimit = unitLoggedIn.getCreditLimit();
		creditLimit.setActive(Boolean.TRUE);
		creditLimit.setDateRange(B2BPeriodRange.DAY);
		creditLimit.setAmount(BigDecimal.valueOf(10000D));
		creditLimit.setAlertThreshold(BigDecimal.valueOf(8000D));
		creditLimit.setDatePeriod(null);
		creditLimit.setAlertSentDate(null);
		modelService.save(creditLimit);

		final B2BApprovalProcessModel b2bApprovalProcessModel = getB2BApprovalProcessModelForOrder(order);
		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 20000);
		this.modelService.refresh(order);
		this.modelService.refresh(b2bApprovalProcessModel);
		Assert.assertEquals(OrderStatus.APPROVED, order.getStatus());
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel.getProcessState());
		//Check Alert Sent Date exist now
		modelService.refresh(creditLimit);
		Assert.assertNotNull("AlertSendDate should have been set", creditLimit.getAlertSentDate());

		// create a second order 100 total against the same cost center which makes the orders total of 9010 for the credit limit
		//the order still  exceeds Alert Limit (8000) but the total of all orders is below credit limit (1000)

		final OrderModel order2 = createOrder("GC Sales DE Boss", 10, OrderStatus.CREATED);
		Assert.assertNotNull(order2);

		final B2BApprovalProcessModel b2bApprovalProcessModel2 = getB2BApprovalProcessModelForOrder(order2);
		this.waitForProcessToEnd(b2bApprovalProcessModel2.getCode(), 20000);
		this.modelService.refresh(order2);
		this.modelService.refresh(b2bApprovalProcessModel2);
		Assert.assertEquals(OrderStatus.APPROVED, order2.getStatus());
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel2.getProcessState());

		//Alert Sent Date was in past, so it's updated with Current Date
		modelService.refresh(creditLimit);


		Assert.assertNotNull("AlertSentDate should not be null", creditLimit.getAlertSentDate());
		Assert.assertTrue(
				String.format("AlertSentDate %s shold be in the same day as %s", creditLimit.getAlertSentDate(), new Date()),
				DateUtils.isSameDay(creditLimit.getAlertSentDate(), new Date()));

	}

	@Test
	public void shouldTriggerCreditAlert() throws Exception
	{
		//Step 1 There should not be any template for Alert
		final EmployeeModel employeeModel = userService.getAdminUser();
		WorkflowTemplateModel workflowTemplateModel = null;
		try
		{
			workflowTemplateModel = workflowTemplateService.getWorkflowTemplateForCode("B2B-Alert-GC Acct Mgr");
		}
		catch (final UnknownIdentifierException uie)
		{
			//Do nothing
		}

		Assert.assertNull(workflowTemplateModel);

		/**
		 * Create and order between 8000 - 10000 EUR for unit GC Sales DE Alert should be created for GC Sales Rep
		 */
		final OrderModel order = createOrder("GC Sales DE Boss", 900, OrderStatus.CREATED);
		Assert.assertNotNull(order);

		//Set up credit limit data for test so that it generates an Alert
		final B2BUnitModel unitLoggedIn = b2bUnitService.getUnitForUid("GC Sales DE");
		final B2BCreditLimitModel creditLimitToUse = unitLoggedIn.getCreditLimit();
		creditLimitToUse.setActive(Boolean.TRUE);
		creditLimitToUse.setDateRange(B2BPeriodRange.DAY);
		creditLimitToUse.setDatePeriod(null);
		creditLimitToUse.setAlertSentDate(null);
		modelService.save(creditLimitToUse);

		final B2BApprovalProcessModel b2bApprovalProcessModel = getB2BApprovalProcessModelForOrder(order);

		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 20000);
		this.modelService.refresh(order);
		this.modelService.refresh(b2bApprovalProcessModel);

		// verify that creditCheckAlert was sent - template and workflow should exist for an Alert
		workflowTemplateModel = workflowTemplateService.getWorkflowTemplateForCode("B2B-Alert-GC Acct Mgr");
		Assert.assertNotNull(workflowTemplateModel);
		final List<WorkflowModel> workflowModelList = newestWorkflowService.getWorkflowsForTemplateAndUser(workflowTemplateModel,
				employeeModel);
		Assert.assertTrue(CollectionUtils.isNotEmpty(workflowModelList));

		Assert.assertEquals(OrderStatus.APPROVED, order.getStatus());
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel.getProcessState());

	}

	@Test
	public void shouldStartApprovalProcessAndIgnoreExpriedCreditLimit() throws Exception
	{
		final String userId = "GC S HH";
		login(userId);

		//Set up credit limit data for test
		final B2BUnitModel unitLoggedIn = b2bUnitService.getUnitForUid("GC Sales Hamburg");
		final B2BCreditLimitModel creditLimitToUse = b2bUnitService.getParent(unitLoggedIn).getCreditLimit();
		creditLimitToUse.setActive(Boolean.TRUE);
		final StandardDateRange datePeriodValid = new StandardDateRange(DateUtils.addDays(new Date(), -10), DateUtils.addDays(
				new Date(), +5));
		creditLimitToUse.setDateRange(null);
		creditLimitToUse.setDatePeriod(datePeriodValid);

		modelService.save(creditLimitToUse);

		//Credit Limit is active, so unit is returned
		final B2BUnitModel unitWithCreditLimit = b2bUnitService.getUnitWithCreditLimit(unitLoggedIn);
		Assert.assertNotNull(unitWithCreditLimit);

		//Update credit limit with past start/end dates as date range
		final B2BCreditLimitModel creditLimit = unitWithCreditLimit.getCreditLimit();
		creditLimit.setDateRange(null);

		final StandardDateRange datePeriodPast = new StandardDateRange(DateUtils.addDays(new Date(), -10), DateUtils.addDays(
				new Date(), -5));
		creditLimit.setDatePeriod(datePeriodPast);
		modelService.save(creditLimit);

		//Create order which crosses credit limit
		final OrderModel order = createOrder(this.login("GC S HH"), 140, OrderStatus.CREATED,
				productService.getProductForCode("testProduct0"));
		Assert.assertNotNull(order);

		final B2BApprovalProcessModel b2bApprovalProcessModel = getB2BApprovalProcessModelForOrder(order);

		//Process does not stop at waitProcessCreditLimit, so it should continue after waitProcess
		if (this.waitForProcessAction(b2bApprovalProcessModel.getCode(), "waitProcess", 20000))
		{
			modelService.refresh(order);
			final Collection<WorkflowActionModel> actions = b2bWorkflowIntegrationService.getStartWorkflowActions(order
					.getWorkflow());
			Assert.assertEquals(1, actions.size());
			b2bWorkflowIntegrationService.approveWorkflowAction(actions.iterator().next());
		}

		//Credit limit is expired, so unit is null
		final B2BUnitModel unitWithCreditLimitNull = b2bUnitService.getUnitWithCreditLimit(order.getUnit());
		Assert.assertNull(unitWithCreditLimitNull);

		//Process finishes and order placed above is approved - Order total is more than credit limit amount, but 
		//credit limit is expired, so its ignored
		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 20000);
		this.modelService.refresh(order);
		this.modelService.refresh(b2bApprovalProcessModel);
		Assert.assertEquals(OrderStatus.APPROVED, order.getStatus());
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel.getProcessState());

	}

	@Test
	public void shouldStartApprovalProcessAndIgnoreInactiveCreditLimit() throws Exception
	{
		final String userId = "GC S HH";
		login(userId);

		//Set up credit limit data for test
		final B2BUnitModel unitLoggedIn = b2bUnitService.getUnitForUid("GC Sales Hamburg");
		final B2BCreditLimitModel creditLimitToUse = b2bUnitService.getParent(unitLoggedIn).getCreditLimit();
		creditLimitToUse.setActive(Boolean.TRUE);
		creditLimitToUse.setDateRange(B2BPeriodRange.DAY);
		creditLimitToUse.setDatePeriod(null);
		modelService.save(creditLimitToUse);

		//Credit Limit is active, so unit is returned
		final B2BUnitModel unitWithCreditLimit = b2bUnitService.getUnitWithCreditLimit(unitLoggedIn);
		Assert.assertNotNull(unitWithCreditLimit);

		//Update credit limit with past start/end dates as date range
		final B2BCreditLimitModel creditLimit = unitWithCreditLimit.getCreditLimit();
		creditLimit.setActive(Boolean.FALSE);

		modelService.save(creditLimit);

		//Create order which crosses credit limit
		final OrderModel order = createOrder(this.login("GC S HH"), 140, OrderStatus.CREATED,
				productService.getProductForCode("testProduct0"));
		Assert.assertNotNull(order);

		final B2BApprovalProcessModel b2bApprovalProcessModel = getB2BApprovalProcessModelForOrder(order);

		//Process does not stop at waitProcessCreditLimit, so it should continue after waitProcess
		if (this.waitForProcessAction(b2bApprovalProcessModel.getCode(), "waitProcess", 20000))
		{
			modelService.refresh(order);
			final Collection<WorkflowActionModel> actions = b2bWorkflowIntegrationService.getStartWorkflowActions(order
					.getWorkflow());
			Assert.assertEquals(1, actions.size());
			b2bWorkflowIntegrationService.approveWorkflowAction(actions.iterator().next());
		}

		//Credit limit is inactive, so unit is null
		final B2BUnitModel unitWithCreditLimitNull = b2bUnitService.getUnitWithCreditLimit(order.getUnit());
		Assert.assertNull(unitWithCreditLimitNull);

		//Process finishes and order placed above is approved - Order total is more than credit limit amount, but 
		//credit limit is inactive, so its ignored
		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 20000);
		this.modelService.refresh(order);
		this.modelService.refresh(b2bApprovalProcessModel);
		Assert.assertEquals(OrderStatus.APPROVED, order.getStatus());
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel.getProcessState());
	}

	private B2BApprovalProcessModel getB2BApprovalProcessModelForOrder(final OrderModel order)
	{
		final B2BApprovalProcessModel b2bApprovalProcessModel = baseDao.findFirstByAttribute(B2BApprovalProcessModel.ORDER, order,
				B2BApprovalProcessModel.class);
		Assert.assertNotNull(b2bApprovalProcessModel);
		return b2bApprovalProcessModel;
	}

}
