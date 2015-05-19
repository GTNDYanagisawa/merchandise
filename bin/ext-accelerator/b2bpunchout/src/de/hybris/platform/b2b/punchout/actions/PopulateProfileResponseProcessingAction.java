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

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutResponseCode;
import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;
import de.hybris.platform.converters.Populator;

import org.cxml.CXML;
import org.cxml.ProfileRequest;
import org.cxml.ProfileResponse;
import org.cxml.Response;
import org.springframework.beans.factory.annotation.Required;


/**
 * This implementation of {@link PunchOutProcessingAction} is meant to process the body of a Profile Request.
 */
public class PopulateProfileResponseProcessingAction implements PunchOutProcessingAction<CXML, CXML>
{

	private Populator<CXML, ProfileResponse> profileResponsePopulator;

	@Override
	public void process(final CXML input, final CXML output)
	{

		final CXMLElementBrowser cxmlBrowser = new CXMLElementBrowser(input);

		final ProfileRequest profileRequest = cxmlBrowser.findRequestByType(ProfileRequest.class);

		if (profileRequest == null)
		{
			throw new PunchOutException(PunchOutResponseCode.CONFLICT,
					"Profile request is invalid. No ProfileRequest element in CXML");
		}
		final Response response = new Response();
		output.getHeaderOrMessageOrRequestOrResponse().add(response);
		final ProfileResponse profileResponse = new ProfileResponse();
		response
				.getProfileResponseOrPunchOutSetupResponseOrProviderSetupResponseOrGetPendingResponseOrSubscriptionListResponseOrSubscriptionContentResponseOrSupplierListResponseOrSupplierDataResponseOrAuthResponseOrDataResponseOrOrganizationDataResponse()
				.add(profileResponse);

		getProfileResponsePopulator().populate(input, profileResponse);

	}

	public Populator<CXML, ProfileResponse> getProfileResponsePopulator()
	{
		return profileResponsePopulator;
	}

	@Required
	public void setProfileResponsePopulator(final Populator<CXML, ProfileResponse> profileResponsePopulator)
	{
		this.profileResponsePopulator = profileResponsePopulator;
	}

}
