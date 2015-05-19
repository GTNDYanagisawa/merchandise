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
package de.hybris.platform.b2b.punchout.populators.impl;

import de.hybris.platform.b2b.punchout.Address;
import de.hybris.platform.b2b.punchout.Organization;
import de.hybris.platform.b2b.punchout.PostalAddress;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.cxml.*;


/**
 * Populator from {@link CXML} to {@link PunchOutSession}.
 */
public class DefaultPunchOutSessionPopulator implements Populator<CXML, PunchOutSession>
{
	private static final Logger LOG = Logger.getLogger(DefaultPunchOutSessionPopulator.class);

	@Override
	public void populate(final CXML source, final PunchOutSession target) throws ConversionException
	{

		final CXMLElementBrowser cXmlBrowser = new CXMLElementBrowser(source);

		final PunchOutSetupRequest request = cXmlBrowser.findRequestByType(PunchOutSetupRequest.class);
		target.setOperation(request.getOperation());
		populateBuyerCookie(target, request);
		populateShippingInfo(target, request);
		target.setBrowserFormPostUrl(request.getBrowserFormPost().getURL().getvalue());

		populateOrganizationInfo(cXmlBrowser.findHeader(), target);

	}

	protected void populateOrganizationInfo(final Header header, final PunchOutSession punchoutSession)
	{

		punchoutSession.setInitiatedBy(convertCredentialsToOrganizations(header.getFrom().getCredential()));
		punchoutSession.setTargetedTo(convertCredentialsToOrganizations(header.getTo().getCredential()));
		punchoutSession.setSentBy(convertCredentialsToOrganizations(header.getSender().getCredential()));
        punchoutSession.setSentByUserAgent(header.getSender().getUserAgent());
	}

	protected List<Organization> convertCredentialsToOrganizations(List<Credential> credentials)
	{
		List<Organization> organizationList = new ArrayList<Organization>();

		for (final Credential credential : credentials)
		{

			final Organization organization = new Organization();
			organization.setDomain(credential.getDomain());
			organization.setIdentity(credential.getIdentity().getContent().get(0).toString());
			organization.setSharedsecret(getSharedSecret(credential));
			organizationList.add(organization);

		}

		return organizationList;
	}

	private String getSharedSecret(Credential credential)
	{
		if (CollectionUtils.isNotEmpty(credential.getSharedSecretOrDigitalSignatureOrCredentialMac()))
		{
			if (credential.getSharedSecretOrDigitalSignatureOrCredentialMac().get(0) instanceof SharedSecret)
			{
				SharedSecret sharedSecret = (SharedSecret) credential.getSharedSecretOrDigitalSignatureOrCredentialMac().get(0);
				if (CollectionUtils.isNotEmpty(sharedSecret.getContent()))
				{
					return (String) sharedSecret.getContent().get(0);
				}
			}
			else
			{
				LOG.warn("The Shared Secret, Digital Signature or Credential Mac was not populated in the Organization details. Please verify your implementation");
			}

		}

        return null;
	}

	private void populateShippingInfo(final PunchOutSession output, final PunchOutSetupRequest request)
	{
		final ShipTo shipTo = request.getShipTo();
		if (shipTo != null)
		{
			final org.cxml.Address requestAddress = shipTo.getAddress();
			output.setShippingAddress(new Address());
			output.getShippingAddress().setId(requestAddress.getAddressID());
			if (requestAddress.getEmail() != null)
			{
				output.getShippingAddress().setEmail(requestAddress.getEmail().getvalue());
			}
			if (requestAddress.getName() != null)
			{
				output.getShippingAddress().setName(requestAddress.getName().getvalue());
			}
			if (requestAddress.getPhone() != null)
			{
				output.getShippingAddress().setPhone(requestAddress.getPhone().getTelephoneNumber().getNumber());
			}
			output.getShippingAddress().setPostalAddress(toPostalAddress(requestAddress.getPostalAddress()));

		}
	}

	protected PostalAddress toPostalAddress(final org.cxml.PostalAddress postalAddress)
	{
		final PostalAddress result = new PostalAddress();

		result.setCity(postalAddress.getCity());
		result.setCountry(postalAddress.getCountry().getIsoCountryCode());
		if (CollectionUtils.isNotEmpty(postalAddress.getDeliverTo()))
		{
			result.setDeliverTo(postalAddress.getDeliverTo().iterator().next().getvalue());
		}
		result.setName(postalAddress.getName());
		result.setPostalCode(postalAddress.getPostalCode());
		result.setState(postalAddress.getState());
		if (CollectionUtils.isNotEmpty(postalAddress.getStreet()))
		{
			result.setStreet(postalAddress.getStreet().iterator().next().getvalue());
		}

		return result;
	}

	protected void populateBuyerCookie(final PunchOutSession output, final PunchOutSetupRequest request)
	{
		if (request.getBuyerCookie() != null)
		{
			final String buyerCookieId = (String) request.getBuyerCookie().getContent().iterator().next();

			output.setBuyerCookie(buyerCookieId);
		}
	}

}
