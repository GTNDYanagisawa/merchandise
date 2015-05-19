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
package de.hybris.platform.secureportaladdon.cockpit.config.impl;

import de.hybris.platform.cockpit.components.notifier.Notification;
import de.hybris.platform.cockpit.events.impl.ItemChangedEvent;
import de.hybris.platform.cockpit.model.listview.TableModel;
import de.hybris.platform.cockpit.services.config.ColumnConfiguration;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.secureportaladdon.constants.SecureportaladdonConstants;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationModel;
import de.hybris.platform.workflow.WorkflowAttachmentService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;


/**
 * Custom TaskCellRenderer used to override the decision drop-down event handler. This is done so that we can make sure
 * that we have a valid B2BUnit assigned to the new customer when approving, or a decline reason when rejected.
 */
public class TaskCellRenderer extends de.hybris.platform.cockpit.services.config.impl.TaskCellRenderer
{

	public TaskCellRenderer(final ColumnConfiguration colConf)
	{
		super(colConf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.cockpit.services.config.impl.TaskCellRenderer#doDecideAction(de.hybris.platform.workflow.model
	 * .WorkflowActionModel, de.hybris.platform.workflow.model.WorkflowDecisionModel,
	 * de.hybris.platform.cockpit.model.listview.TableModel, org.zkoss.zk.ui.Component)
	 */
	@Override
	protected void doDecideAction(final WorkflowActionModel action, final WorkflowDecisionModel decision, final TableModel model,
			final Component parent)
	{

		// Don't do anything special if we are not addressing the registration workflow
		if (!isB2BRegistrationWorkflowAction(action))
		{
			super.doDecideAction(action, decision, model, parent);
			return;
		}

		if (isRejectedDecision(decision) && !validateRejectDecision(action))
		{
			showNotification(model, parent, Labels.getLabel("notifications.missing.reject.reason"));
			return;
		}

		if (isApprovedDecision(decision) && !validateApproveDecision(action))
		{
			showNotification(model, parent, Labels.getLabel("notifications.missing.default.b2bunit"));
			return;
		}

		super.doDecideAction(action, decision, model, parent);

	}

	/**
	 * @return True if the reject decision can proceed, i.e. we have a reject reason
	 */
	protected boolean validateRejectDecision(final WorkflowActionModel action)
	{
		// TODO: Add system property to make reject reason optional or mandatory, default to mandatory
		return StringUtils.isNotBlank(getB2BRegistrationModel(action).getRejectReason());
	}

	/**
	 * @return True if the approve decision can proceed, i.e. we have a default b2b unit assigned to the customer
	 */
	protected boolean validateApproveDecision(final WorkflowActionModel action)
	{
		return getB2BRegistrationModel(action).getDefaultB2BUnit() != null;
	}

	/**
	 * @return True if the user chose to reject the registration
	 */
	protected boolean isRejectedDecision(final WorkflowDecisionModel decision)
	{
		return StringUtils.equalsIgnoreCase(decision.getCode(),
				SecureportaladdonConstants.Workflows.Decisions.REGISTRATION_REJECTED);
	}

	/**
	 * @return True if the user chose to approve the registration
	 */
	protected boolean isApprovedDecision(final WorkflowDecisionModel decision)
	{
		return StringUtils.equalsIgnoreCase(decision.getCode(),
				SecureportaladdonConstants.Workflows.Decisions.REGISTRATION_APPROVED);
	}

	/**
	 * @return True if this action is part of the B2B registration process, false otherwise
	 */
	protected boolean isB2BRegistrationWorkflowAction(final WorkflowActionModel action)
	{
		return StringUtils.equalsIgnoreCase(action.getTemplate().getCode(),
				SecureportaladdonConstants.Workflows.Actions.REGISTRATION_APPROVAL);
	}

	/**
	 * Gets the {@link B2BRegistrationModel} instance attached to the {@link WorkflowActionModel}
	 * 
	 * @param action
	 *           The action on which we are taking a decision
	 * @return The attached B2BRegistrationModel instance
	 */
	protected B2BRegistrationModel getB2BRegistrationModel(final WorkflowActionModel action)
	{
		final List<ItemModel> models = getWorkflowAttachmentService().getAttachmentsForAction(action,
				B2BRegistrationModel.class.getName());
		return (B2BRegistrationModel) models.iterator().next();
	}

	/**
	 * Displays a notification to the user with the provided message.
	 * 
	 * @param model
	 *           The table model
	 * @param message
	 *           The message that we display to the user
	 * @param parent
	 *           The parent control of this renderer
	 */
	protected void showNotification(final TableModel model, final Component parent, final String message)
	{
		final Notification notification = new Notification(message);
		model.fireEvent("shownotification", notification);
		UISessionUtils.getCurrentSession().sendGlobalEvent(new ItemChangedEvent(parent, null, Collections.EMPTY_LIST));
	}

	/**
	 * @return WorkflowAttachmentService instance from the {@link Registry}
	 */
	protected WorkflowAttachmentService getWorkflowAttachmentService()
	{
		return Registry.getApplicationContext().getBean(WorkflowAttachmentService.class);
	}

}