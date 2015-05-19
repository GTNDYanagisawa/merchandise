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
package de.hybris.platform.yb2bacceleratorcore.actions;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.enums.PermissionStatus;
import de.hybris.platform.b2b.enums.WorkflowTemplateType;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.actions.AbstractSimpleB2BApproveOrderDecisionAction;
import de.hybris.platform.b2b.process.approval.actions.B2BPermissionResultHelperImpl;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class StartWorkFlowForAdmin extends AbstractSimpleB2BApproveOrderDecisionAction
{
	private static final Logger LOG = Logger.getLogger(StartWorkFlowForAdmin.class);

	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private B2BWorkflowIntegrationService b2bWorkflowIntegrationService;
	private WorkflowProcessingService workflowProcessingService;
	private WorkflowService workflowService;
	private B2BPermissionResultHelperImpl permissionResultHelper;
	private UserService userService;

	@Override
	public Transition executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		final OrderModel order = process.getOrder();
		try
		{
			final B2BCustomerModel customer = (B2BCustomerModel) order.getUser();
			final B2BCustomerModel admin = findB2BAdministratorForCustomer(customer);

			if (admin != null)
			{
				// extract only the permissions that required approval by the administrator
				final Collection<B2BPermissionResultModel> permissionsToApprove = getPermissionResultHelper()
						.filterResultByPermissionStatus(order.getPermissionResults(), PermissionStatus.OPEN);

				// make the admin owner of the permissions
				for (final B2BPermissionResultModel b2bPermissionResultModel : permissionsToApprove)
				{
					b2bPermissionResultModel.setApprover(admin);
				}
				order.setPermissionResults(permissionsToApprove);

				// assign the administrator to a b2b approver group if he is not a member of this group
				assignToGroup(admin, userService.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP));

				// create the workflow for the admin
				final WorkflowModel workflow = createAndStartWorkflow(process, admin);
				order.setWorkflow(workflow);
				order.setStatus(OrderStatus.ASSIGNED_TO_ADMIN);
				this.modelService.saveAll();
				return Transition.OK;
			}
			else
			{
				LOG.error(String.format("Order %s placed by %s has failed to get approved, no approvers or administrators where"
						+ " " + "found in the system to approve it", order.getCode(), order.getUser().getUid()));

				order.setStatus(OrderStatus.B2B_PROCESSING_ERROR);
				modelService.save(order);
				return Transition.NOK;
			}
		}
		catch (final Exception e)
		{
			handleError(order, e);
			return Transition.NOK;
		}
	}

	protected void assignToGroup(final B2BCustomerModel admin, final UserGroupModel userGroup)
	{
		final Set<PrincipalGroupModel> groupModelSet = admin.getGroups();
		if (!groupModelSet.contains(userGroup))
		{
			final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(groupModelSet);
			groups.add(userGroup);
			admin.setGroups(groups);
		}
	}

	protected B2BCustomerModel findB2BAdministratorForCustomer(final B2BCustomerModel customer)
	{
		final List<B2BCustomerModel> b2bAdminGroupUsers = new ArrayList<B2BCustomerModel>(getB2bUnitService().getUsersOfUserGroup(
				getB2bUnitService().getParent(customer), B2BConstants.B2BADMINGROUP, true));
		// remove the user who placed the order.
		CollectionUtils.filter(b2bAdminGroupUsers, PredicateUtils.notPredicate(PredicateUtils.equalPredicate(customer)));
		return (CollectionUtils.isNotEmpty(b2bAdminGroupUsers) ? b2bAdminGroupUsers.get(0) : null);
	}

	protected WorkflowModel createAndStartWorkflow(final B2BApprovalProcessModel process, final B2BCustomerModel admin)
	{
		final String workflowTemplateCode = getB2bWorkflowIntegrationService().generateWorkflowTemplateCode(
				"B2B_APPROVAL_WORKFLOW", Collections.singletonList(admin));
		final WorkflowTemplateModel workflowTemplate = getB2bWorkflowIntegrationService().createWorkflowTemplate(
				Collections.singletonList(admin), workflowTemplateCode, "Generated B2B Order Approval Workflow",
				WorkflowTemplateType.ORDER_APPROVAL);
		final WorkflowModel workflow = getWorkflowService().createWorkflow(workflowTemplate.getName(), workflowTemplate,
				Collections.<ItemModel> singletonList(process), workflowTemplate.getOwner());
		getWorkflowProcessingService().startWorkflow(workflow);
		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Started workflow for order %s placed by %s assigned to administrator %s", process.getOrder()
					.getCode(), process.getOrder().getUser().getUid(), admin.getUid()));
		}

		return workflow;
	}

	protected void handleError(final OrderModel order, final Exception exception)
	{
		if (order != null)
		{
			this.setOrderStatus(order, OrderStatus.B2B_PROCESSING_ERROR);
		}
		LOG.error(exception.getMessage(), exception);
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	protected B2BWorkflowIntegrationService getB2bWorkflowIntegrationService()
	{
		return b2bWorkflowIntegrationService;
	}

	@Required
	public void setB2bWorkflowIntegrationService(final B2BWorkflowIntegrationService b2bWorkflowIntegrationService)
	{
		this.b2bWorkflowIntegrationService = b2bWorkflowIntegrationService;
	}

	protected WorkflowProcessingService getWorkflowProcessingService()
	{
		return workflowProcessingService;
	}

	@Required
	public void setWorkflowProcessingService(final WorkflowProcessingService workflowProcessingService)
	{
		this.workflowProcessingService = workflowProcessingService;
	}

	protected WorkflowService getWorkflowService()
	{
		return workflowService;
	}

	@Required
	public void setWorkflowService(final WorkflowService workflowService)
	{
		this.workflowService = workflowService;
	}

	protected B2BPermissionResultHelperImpl getPermissionResultHelper()
	{
		return permissionResultHelper;
	}

	@Required
	public void setPermissionResultHelper(final B2BPermissionResultHelperImpl permissionResultHelper)
	{
		this.permissionResultHelper = permissionResultHelper;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
