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
package de.hybris.platform.acceleratorservices.process.email.actions;


import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;

import org.springframework.beans.factory.annotation.Required;


/**
 * A process action to send emails.
 */
public class SendEmailAction extends AbstractProceduralAction
{
	private EmailService emailService;

	protected EmailService getEmailService()
	{
		return emailService;
	}

	@Required
	public void setEmailService(final EmailService emailService)
	{
		this.emailService = emailService;
	}

	@Override
	public void executeAction(final de.hybris.platform.processengine.model.BusinessProcessModel businessProcessModel)
			throws RetryLaterException
	{
		for (final EmailMessageModel email : businessProcessModel.getEmails())
		{
			getEmailService().send(email);
		}
	}
}
