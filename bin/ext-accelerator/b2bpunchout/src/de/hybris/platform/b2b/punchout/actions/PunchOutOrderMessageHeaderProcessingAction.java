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

import de.hybris.platform.b2b.punchout.Organization;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.services.PunchOutConfigurationService;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.core.model.order.CartModel;

import java.util.ArrayList;
import java.util.List;

import org.cxml.*;
import org.springframework.beans.factory.annotation.Required;


/**
 * Creates the header for a PunchOut message. The Header element contains addressing and authentication information. The
 * Header element is the same regardless of the specific Request or Response within the body of the cXML message.
 */
public class PunchOutOrderMessageHeaderProcessingAction implements PunchOutProcessingAction<CartModel, CXML>
{
	private PunchOutSessionService punchOutSessionService;
	private PunchOutConfigurationService punchOutConfigurationService;

	@Override
	public void process(final CartModel input, final CXML transaction)
	{
		final Header header = new Header();
		final PunchOutSession currentPunchOutSession = getPunchOutSessionService().getCurrentPunchOutSession();

		header.setFrom(createFrom(currentPunchOutSession));
		header.setTo(createTo(currentPunchOutSession));
		header.setSender(createSender(currentPunchOutSession));

		transaction.getHeaderOrMessageOrRequestOrResponse().add(header);
	}

	protected From createFrom(final PunchOutSession currentPunchOutSession)
	{
		final From headerFrom = new From();
		// Since it is a request the from well be initiated by this system, the original <To> in the request will become
		// the <From> in the response.
        headerFrom.getCredential().addAll(convertOrganizationsToCredentials(currentPunchOutSession.getTargetedTo()));

		return headerFrom;
	}

	protected To createTo(final PunchOutSession currentPunchOutSession)
	{
		final To headerTo = new To();
		// Since it is a request the from well be initiated by this system, the original <From> in the request will become
		// the <To> in the response.
        headerTo.getCredential().addAll(convertOrganizationsToCredentials(currentPunchOutSession.getInitiatedBy()));

		return headerTo;
	}

	protected Sender createSender(final PunchOutSession currentPunchOutSession)
	{
		final Sender headerSender = new Sender();
        headerSender.setUserAgent(currentPunchOutSession.getSentByUserAgent());
		// Sender stays the same as original but without the shared secret.
		headerSender.getCredential().addAll(convertOrganizationsToCredentials(currentPunchOutSession.getSentBy()));

        return headerSender;
	}

    /**
     * this method will convert an Organization into a cmxl credential ignoring the shared secret.
      * @param organizations intended to be converted.
     * @return the resulting list of credentials.
     */
    protected List<Credential> convertOrganizationsToCredentials(List<Organization> organizations)
	{
        List<Credential> credentials = new ArrayList<>();

        for (Organization organization : organizations) {
            final Credential credential = new Credential();
            credential.setDomain(organization.getDomain());
            credential.setIdentity(new Identity());
            credential.getIdentity().getContent().add(organization.getIdentity());

            credentials.add(credential);
        }

        return credentials;
	}

	protected PunchOutConfigurationService getPunchOutConfigurationService()
	{
		return punchOutConfigurationService;
	}

	@Required
	public void setPunchOutConfigurationService(final PunchOutConfigurationService punchOutConfigurationService)
	{
		this.punchOutConfigurationService = punchOutConfigurationService;
	}

	protected PunchOutSessionService getPunchOutSessionService()
	{
		return punchOutSessionService;
	}

	@Required
	public void setPunchOutSessionService(final PunchOutSessionService punchOutSessionService)
	{
		this.punchOutSessionService = punchOutSessionService;
	}
}
