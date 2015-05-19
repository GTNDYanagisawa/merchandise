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
package de.hybris.platform.b2b;

import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.util.Utilities;
import de.hybris.platform.workflow.jalo.WorkflowAction;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.junit.Ignore;


@Ignore
public abstract class WorkflowIntegrationTest extends B2BIntegrationTest
{

	private static final Logger LOG = Logger.getLogger(WorkflowIntegrationTest.class);

	public boolean waitForProcessAction(final String businessProcessCode, final String actionCode, final long maxWait)
			throws InterruptedException
	{
		final long start = System.currentTimeMillis();

		while (true)
		{
			final BusinessProcessModel bp = businessProcessService.getProcess(businessProcessCode);
			modelService.refresh(bp); // without refresh this object is stale

			final String currentAction = bp.getCurrentTasks() != null && bp.getCurrentTasks().iterator().hasNext() ? bp
					.getCurrentTasks().iterator().next().getAction() : null;
			if (StringUtils.equals(actionCode, currentAction))
			{
				return true;
			}

			if (System.currentTimeMillis() - start > maxWait)
			{
				throw new InterruptedException(String.format("BusinessProcess %s [%s] did not go into a specified action %s, "
						+ "current action %s, " + "waited for %s ", bp.getCode(), bp.getProcessState(), actionCode, currentAction,
						Utilities.formatTime(System.currentTimeMillis() - start)));
			}
			else
			{
				Thread.sleep(1000);
				if (bp instanceof B2BApprovalProcessModel)
				{
					final OrderModel order = ((B2BApprovalProcessModel) bp).getOrder();
					//					this.modelService.refresh(order);
					final WorkflowModel workflow = order.getWorkflow();
					if (LOG.isInfoEnabled() && workflow != null)
					{
						LOG.debug(String.format("Workflow %s [$s] for order %s [%s] has status %s", workflow.getCode(),
								workflow.getDescription(), order.getCode(), order.getStatus(), workflow.getStatus()));
					}
				}
				LOG.debug(String.format("Waited for process state of %s for %s current state is %s", actionCode,
						Utilities.formatTime(System.currentTimeMillis() - start), bp.getProcessState()));
			}
		}
	}


	public boolean waitForProcessToEnd(final String businessProcessCode, final long maxWait) throws InterruptedException
	{
		final long start = System.currentTimeMillis();
		while (true)
		{
			final B2BApprovalProcessModel bp = businessProcessService.getProcess(businessProcessCode);
			final String currentAction = bp.getCurrentTasks() != null && bp.getCurrentTasks().iterator().hasNext() ? bp
					.getCurrentTasks().iterator().next().getAction() : null;
			modelService.refresh(bp); // without refresh this object is stale

			if (ProcessState.SUCCEEDED.getCode().equals(bp.getProcessState().getCode())
					|| "FINISHED".equals(bp.getProcessState().getCode()))
			{
				return true;
			}
			else if (ProcessState.ERROR.getCode().equals(bp.getProcessState().getCode()))
			{
				return false;
			}


			if (System.currentTimeMillis() - start > maxWait)
			{
				LOG.debug("BusinessPRocess: " + ReflectionToStringBuilder.toString(bp));
				throw new InterruptedException(String.format("BusinessProcess %s did not end! Waited for %s", bp.getCode(),
						Utilities.formatTime(System.currentTimeMillis() - start)));
			}
			else
			{
				Thread.sleep(1000);
				final OrderModel order = bp.getOrder();
				if (LOG.isInfoEnabled() && order != null)
				{
					final WorkflowModel workflow = order.getWorkflow();
					if (workflow != null)
					{
						LOG.info(String.format(
								"Workflow %s [%s] for order %s [%s] has status %s, BisunessProcess State %s running action %s",
								workflow.getCode(), workflow.getDescription(), order.getCode(), order.getStatus(), workflow.getStatus(),
								bp.getProcessState().getCode(), currentAction));
					}
				}
				LOG.debug(String.format("Waited for process end for %s current state is %s",
						Utilities.formatTime(System.currentTimeMillis() - start), bp.getProcessState()));
			}
		}
	}

	public BusinessProcessModel waitForProcess(final String processCode, final long maxWait) throws InterruptedException
	{
		final long start = System.currentTimeMillis();
		BusinessProcessModel businessProcessModel = null;

		try
		{
			businessProcessModel = businessProcessService.getProcess(processCode);
			modelService.refresh(businessProcessModel); // without refresh this object is stale

			while ((businessProcessModel) == null)
			{
				if (System.currentTimeMillis() - start > maxWait)
				{
					break;
				}
				else
				{
					Thread.sleep(1000);
				}
			}
		}
		finally
		{
			if (LOG.isInfoEnabled())
			{
				LOG.info(String.format("Waited for process '%s' for %s milliseconds got process: %s [%s]", processCode,
						Long.valueOf(System.currentTimeMillis() - start), businessProcessModel.getCode(),
						businessProcessModel.getProcessState()));
			}
		}
		return businessProcessModel;
	}

	@Deprecated
	/**
	 * @deprecated This uses jalo workflow decision to test workflows decided upon via hmc
	 */
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


}
