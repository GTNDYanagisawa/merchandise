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
package de.hybris.platform.secureportaladdon.email.process.actions;

import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.email.actions.GenerateEmailAction;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.secureportaladdon.constants.SecureportaladdonConstants;
import de.hybris.platform.secureportaladdon.services.B2BRegistrationService;
import de.hybris.platform.task.RetryLaterException;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation of {@link GenerateEmailAction} responsible for creating an email message for all users associated to
 * the user group 'b2bregistrationapprovergroup'
 */
public class RegistrationPendingApprovalGenerateEmailAction extends GenerateEmailAction
{

	private static final Logger LOG = Logger.getLogger(RegistrationPendingApprovalGenerateEmailAction.class);

	private B2BRegistrationService registrationService;

	/**
	 * @param registrationService
	 *           the registrationService to set
	 */
	@Required
	public void setRegistrationService(final B2BRegistrationService registrationService)
	{
		this.registrationService = registrationService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.acceleratorservices.process.email.actions.GenerateEmailAction#executeAction(de.hybris.platform
	 * .processengine.model.BusinessProcessModel)
	 */
	@Override
	public Transition executeAction(final BusinessProcessModel businessProcessModel) throws RetryLaterException
	{
		final Transition transition = super.executeAction(businessProcessModel);

		if (transition == Transition.OK)
		{

			// Get all employees that have the right to approve registration
			final List<EmployeeModel> employees = registrationService
					.getEmployeesInUserGroup(SecureportaladdonConstants.UserGroups.REGISTRATION_APPROVER_GROUP);

			// Get the emails of said employees
			final List<EmailAddressModel> recipients = registrationService.getEmailAddressesOfEmployees(employees);

			if (CollectionUtils.isEmpty(recipients))
			{
				LOG.warn(String.format("There are no employees within the user group '%s'. No notification will be sent.",
						SecureportaladdonConstants.UserGroups.REGISTRATION_APPROVER_GROUP));
				return Transition.NOK;
			}

			// Get the email message (there should only be one!) and set recipients
			final EmailMessageModel emailMessage = businessProcessModel.getEmails().iterator().next();
			emailMessage.setToAddresses(recipients);
			modelService.save(emailMessage);

		}

		return transition;

	}
}
