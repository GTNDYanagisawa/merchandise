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
import de.hybris.platform.converters.Populator;

import org.cxml.CXML;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates a new {@link PunchOutSession} from the input {@link CXML}.
 */
public class NewSessionPunchOutProcessingAction implements PunchOutProcessingAction<CXML, PunchOutSession>
{
	private Populator<CXML, PunchOutSession> punchOutSessionPopulator;

	@Override
	public void process(final CXML input, final PunchOutSession output)
	{
		getPunchOutSessionPopulator().populate(input, output);
	}

	public Populator<CXML, PunchOutSession> getPunchOutSessionPopulator()
	{
		return punchOutSessionPopulator;
	}

	@Required
	public void setPunchOutSessionPopulator(final Populator<CXML, PunchOutSession> punchOutSessionPopulator)
	{
		this.punchOutSessionPopulator = punchOutSessionPopulator;
	}
}
