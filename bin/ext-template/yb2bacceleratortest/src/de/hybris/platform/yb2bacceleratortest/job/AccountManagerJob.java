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
package de.hybris.platform.yb2bacceleratortest.job;

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2011 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
import static de.hybris.platform.b2b.services.B2BWorkflowIntegrationService.ACTIONCODES.APPROVAL;
import static de.hybris.platform.b2b.services.B2BWorkflowIntegrationService.DECISIONCODES.APPROVE;
import static de.hybris.platform.b2b.services.B2BWorkflowIntegrationService.DECISIONCODES.REJECT;
import static de.hybris.platform.yb2bacceleratortest.constants.YB2BAcceleratorTestConstants.ACCOUNTMANAGERUID;

import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.orderscheduling.model.OrderScheduleCronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.model.WorkflowActionModel;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * 
 * Approves or rejects order attached to cronJob. Rejects if the word REJECT is contained within the PONumber
 * 
 */
public class AccountManagerJob extends AbstractJobPerformable<OrderScheduleCronJobModel>
{
	private final static Logger LOG = Logger.getLogger(AccountManagerJob.class);
	private UserService userService;
	private B2BWorkflowIntegrationService b2bWorkflowIntegrationService;

	/**
	 * 
	 * Approves or rejects order attached to cronJob.
	 * 
	 */
	@Override
	public PerformResult perform(final OrderScheduleCronJobModel cronJob)
	{
		LOG.debug("Perform Acct Mgr Role for order " + cronJob.getOrder().getCode());
		try
		{
			acctMgrApproveOrRejectAction(cronJob.getOrder().getCode(),
					!StringUtils.contains(cronJob.getOrder().getPurchaseOrderNumber(), "REJECT"));
		}
		catch (final Exception e)
		{
			LOG.warn(e);
		}

		try
		{
			deactivate(cronJob);
		}
		catch (final Exception e)
		{
			LOG.warn(e);
		}
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	public void deactivate(final OrderScheduleCronJobModel cronJob)
	{


		LOG.debug("cancelling cronjob " + cronJob.getCode());
		for (final TriggerModel trigger : cronJob.getTriggers())
		{
			trigger.setActive(Boolean.FALSE);
			modelService.save(trigger);

		}
		cronJob.setActive(Boolean.FALSE);
		modelService.save(cronJob);

	}

	public void acctMgrApproveOrRejectAction(final String orderCode, final boolean approve)
	{
		final String decision = approve ? APPROVE.toString() : REJECT.toString();
		LOG.info(String.format("Attempting to apply decision: %s  on order: %s", orderCode, decision));

		final EmployeeModel employee = getUserService().getUserForUID(ACCOUNTMANAGERUID, EmployeeModel.class);

		final Collection<WorkflowActionModel> workFlowActionModelList = new ArrayList<WorkflowActionModel>(
				getB2bWorkflowIntegrationService().getWorkflowActionsForUser(employee));

		LOG.debug(ACCOUNTMANAGERUID + " has actions count:" + workFlowActionModelList.size());
		CollectionUtils.filter(workFlowActionModelList, new Predicate()
		{
			@Override
			public boolean evaluate(final Object object)
			{
				final WorkflowActionModel workflowActionModel = (WorkflowActionModel) object;

				if (APPROVAL.name().equals(workflowActionModel.getQualifier()))
				{
					return CollectionUtils.exists(workflowActionModel.getAttachmentItems(), new Predicate()
					{
						@Override
						public boolean evaluate(final Object object)
						{
							if (object instanceof OrderModel)
							{
								LOG.debug("This approval action is for order " + ((OrderModel) object).getCode() + " vs " + orderCode);
								return (orderCode.equals(((OrderModel) object).getCode()));
							}
							return false;
						}
					});
				}
				else
				{
					return false;
				}
			}
		});

		LOG.debug(String.format("Employee %s has %s actions to %s for this order %s", employee.getUid(),
				Integer.toString(workFlowActionModelList.size()), decision, orderCode));

		for (final WorkflowActionModel workflowActionModel : workFlowActionModelList)
		{
			getB2bWorkflowIntegrationService().decideAction(workflowActionModel, decision);
			LOG.debug("Decided for ActionCode" + workflowActionModel.getCode() + " to " + decision);
		}
	}

	public B2BWorkflowIntegrationService getB2bWorkflowIntegrationService()
	{
		return b2bWorkflowIntegrationService;
	}

	public void setB2bWorkflowIntegrationService(final B2BWorkflowIntegrationService b2bWorkflowIntegrationService)
	{
		this.b2bWorkflowIntegrationService = b2bWorkflowIntegrationService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


}
