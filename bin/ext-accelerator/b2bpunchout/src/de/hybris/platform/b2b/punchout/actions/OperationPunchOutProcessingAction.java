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
package de.hybris.platform.b2b.punchout.actions;

import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;

import java.util.Date;

import org.cxml.CXML;
import org.springframework.beans.factory.annotation.Required;


public class OperationPunchOutProcessingAction implements PunchOutProcessingAction<CXML, CXML>
{
	private NewSessionPunchOutProcessingAction newSessionPunchOutProcessingAction;

	private PunchOutSessionService punchoutSessionService;

	@Override
	public void process(final CXML input, final CXML output)
	{
		final PunchOutSession punchoutSession = new PunchOutSession();
		punchoutSession.setTime(new Date());

		newSessionPunchOutProcessingAction.process(input, punchoutSession);

		punchoutSessionService.activate(punchoutSession);
	}

	public NewSessionPunchOutProcessingAction getNewSessionPunchOutProcessingAction()
	{
		return newSessionPunchOutProcessingAction;
	}

	@Required
	public void setNewSessionPunchOutProcessingAction(final NewSessionPunchOutProcessingAction newSessionPunchOutProcessingAction)
	{
		this.newSessionPunchOutProcessingAction = newSessionPunchOutProcessingAction;
	}

	public PunchOutSessionService getPunchOutSessionService()
	{
		return punchoutSessionService;
	}

	@Required
	public void setPunchOutSessionService(final PunchOutSessionService punchoutSessionService)
	{
		this.punchoutSessionService = punchoutSessionService;
	}
}
