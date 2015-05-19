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
import de.hybris.platform.b2b.model.B2BCommentModel;
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
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.validation.services.ValidationService;
import de.hybris.platform.workflow.WorkflowActionService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.jalo.WorkflowAction;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@Ignore("BTOBA-30")
@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class B2BQuoteOrderApprovalProcessTest extends WorkflowIntegrationTest
{
	private static final Logger LOG = Logger.getLogger(B2BQuoteOrderApprovalProcessTest.class);
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
	@Resource
	SearchRestrictionService searchRestrictionService;

	@Before
	public void before() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
		//importCsv("/b2bapprovalprocess/test/organizationdata.csv", "UTF-8");

		sessionService.getCurrentSession().setAttribute("user",
				this.modelService.<Object> toPersistenceLayer(userService.getAdminUser()));
		i18nService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));
	}


	@Test
	public void shouldStartSalesQuotesProcessAndAssertApprovalFromMerchant() throws Exception
	{
		final OrderModel order = createOrder("GC CEO", 140, OrderStatus.PENDING_QUOTE);

		Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(b2bOrderService.isQuoteAllowed(order)));

		final B2BApprovalProcessModel b2bApprovalProcessModel = baseDao.findFirstByAttribute(B2BApprovalProcessModel.ORDER, order,
				B2BApprovalProcessModel.class);
		Assert.assertNotNull(b2bApprovalProcessModel);

		if (this.waitForProcessAction(b2bApprovalProcessModel.getCode(), "waitProcessSalesQuote", 60000))
		{
			modelService.refresh(order);
			final WorkflowModel workflow = order.getWorkflow();
			Assert.assertNotNull(workflow);
			modelService.refresh(workflow);
			final Collection<WorkflowActionModel> actions = workflowActionService.getStartWorkflowActions(workflow);
			Assert.assertEquals(1, actions.size());
			this.approveWorkflowAction(actions.iterator().next());
		}

		modelService.refresh(b2bApprovalProcessModel);
		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 60000);
		modelService.refresh(order);
		Assert.assertEquals(OrderStatus.APPROVED_QUOTE, order.getStatus());
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel.getProcessState());
	}

	@Test
	@Ignore("hybris calculation does not support product without prices")
	public void shouldStartSalesQuotesProcessForOrderWithProductWithNoPriceAndAssertApprovalFromMerchant() throws Exception
	{


		final ProductModel testProduct0 = productService.getProductForCode("testProduct0");
		final ProductModel productWithOutPrice = modelService.create(ProductModel.class);
		productWithOutPrice.setCode("productWithOutPrice");
		productWithOutPrice.setCatalogVersion(testProduct0.getCatalogVersion());
		productWithOutPrice.setUnit(testProduct0.getUnit());
		this.modelService.save(productWithOutPrice);

		final OrderModel order = createOrder(this.login("GC CEO"), 140, OrderStatus.PENDING_QUOTE, productWithOutPrice);
		calculationService.calculate(order);
		Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(b2bOrderService.isQuoteAllowed(order)));

		final B2BApprovalProcessModel b2bApprovalProcessModel = baseDao.findFirstByAttribute(B2BApprovalProcessModel.ORDER, order,
				B2BApprovalProcessModel.class);
		Assert.assertNotNull(b2bApprovalProcessModel);

		if (this.waitForProcessAction(b2bApprovalProcessModel.getCode(), "waitProcessSalesQuote", 60000))
		{
			final WorkflowModel workflow = order.getWorkflow();
			modelService.refresh(workflow);
			final Collection<WorkflowActionModel> actions = workflowActionService.getStartWorkflowActions(workflow);
			Assert.assertEquals(1, actions.size());
			this.approveWorkflowAction(actions.iterator().next());
		}

		modelService.refresh(b2bApprovalProcessModel);
		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 60000);
		modelService.refresh(order);
		Assert.assertEquals(OrderStatus.APPROVED_QUOTE, order.getStatus());
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel.getProcessState());
	}

	@Test
	public void testDeleteQuoteAndOrder() throws Exception
	{
		List<OrderModel> pendingQuoteOrders = null;
		final OrderModel order = createOrder("GC S HH", 140, OrderStatus.PENDING_QUOTE);
		login("GC S HH");

		Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(b2bOrderService.isQuoteAllowed(order)));

		final B2BApprovalProcessModel b2bApprovalProcessModel = baseDao.findFirstByAttribute(B2BApprovalProcessModel.ORDER, order,
				B2BApprovalProcessModel.class);
		Assert.assertNotNull(b2bApprovalProcessModel);

		if (this.waitForProcessAction(b2bApprovalProcessModel.getCode(), "waitProcessSalesQuote", 60000))
		{
			modelService.refresh(order);
			final WorkflowModel workflow = order.getWorkflow();
			Assert.assertNotNull(workflow);
			modelService.refresh(workflow);
			final Collection<WorkflowActionModel> actions = workflowActionService.getStartWorkflowActions(workflow);
			Assert.assertEquals(1, actions.size());
		}
		modelService.refresh(b2bApprovalProcessModel);
		modelService.refresh(order);

		pendingQuoteOrders = b2bOrderService.getPendingQuoteOrders(userService.getUserForUID("GC S HH"));
		Assert.assertEquals(1, pendingQuoteOrders.size());

		b2bOrderService.deleteOrder(order.getCode());

		pendingQuoteOrders = b2bOrderService.getPendingQuoteOrders(userService.getUserForUID("GC S HH"));
		Assert.assertEquals(0, pendingQuoteOrders.size());
	}

	@Override
	public void approveWorkflowAction(final WorkflowActionModel workflowActionModel)
	{
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				userService.setCurrentUser(userService.getAdminUser());
				final Collection<WorkflowDecisionModel> decisions = workflowActionModel.getDecisions();
				for (final WorkflowDecisionModel workflowDecisionModel : decisions)
				{
					if (StringUtils.equals(workflowDecisionModel.getQualifier(),
							B2BWorkflowIntegrationService.DECISIONCODES.APPROVE.name()))
					{
						workflowActionModel.setSelectedDecision(workflowDecisionModel);
						modelService.save(workflowActionModel);
						((WorkflowAction) modelService.getSource(workflowActionModel)).decide();
						if (LOG.isDebugEnabled())
						{
							LOG.debug(String.format("Approving %s workflow action", workflowActionModel.getCode()));
						}
						break;
					}
				}
			}
		});
	}


	public void approveQuoteWorkflowAction(final WorkflowActionModel workflowActionModel, final OrderModel order)
	{
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				userService.setCurrentUser(userService.getAdminUser());
				final B2BCommentModel comment = modelService.create(B2BCommentModel.class);
				comment.setComment("The Quote is accepted");
				comment.setOwner(userService.getCurrentUser());
				order.setB2bcomments(Collections.singleton(comment));


				final Collection<WorkflowDecisionModel> decisions = workflowActionModel.getDecisions();
				for (final WorkflowDecisionModel workflowDecisionModel : decisions)
				{
					if (StringUtils.equals(workflowDecisionModel.getQualifier(),
							B2BWorkflowIntegrationService.DECISIONCODES.APPROVE.name()))
					{
						workflowActionModel.setSelectedDecision(workflowDecisionModel);
						modelService.save(workflowActionModel);
						((WorkflowAction) modelService.getSource(workflowActionModel)).decide();
						if (LOG.isDebugEnabled())
						{
							LOG.debug(String.format("Approving %s workflow action", workflowActionModel.getCode()));
						}
						break;
					}
				}
			}
		});
	}

}
