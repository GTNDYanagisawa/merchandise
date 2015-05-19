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
package de.hybris.platform.b2b.punchout.services;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.cxml.CXML;
import org.cxml.Header;
import org.cxml.Request;
import org.cxml.Response;


/**
 * Allows for browsing and finding elements part of a {@link CXML} instance.
 */
public class CXMLElementBrowser
{

	private final CXML cxml;

	/**
	 * Constructor.
	 * 
	 * @param cxml
	 *           the {@link CXML} instance
	 */
	public CXMLElementBrowser(final CXML cxml)
	{
		this.cxml = cxml;
	}

	/**
	 * @return the header as a child of {@link CXML} or null if not found
	 */
	public Header findHeader()
	{
		return findElementInList(cxml.getHeaderOrMessageOrRequestOrResponse(), Header.class);
	}

	/**
	 * 
	 * @param clazz
	 *           the request type class
	 * @return the instance of the request type or null if not found
	 */
	public <T> T findRequestByType(final Class<T> clazz)
	{
		final Request request = findRequest();

		if (request == null)
		{
			return null;
		}
		return findElementInList(
				request
						.getProfileRequestOrOrderRequestOrMasterAgreementRequestOrPurchaseRequisitionRequestOrPunchOutSetupRequestOrProviderSetupRequestOrStatusUpdateRequestOrGetPendingRequestOrSubscriptionListRequestOrSubscriptionContentRequestOrSupplierListRequestOrSupplierDataRequestOrSubscriptionStatusUpdateRequestOrCopyRequestOrCatalogUploadRequestOrAuthRequestOrDataRequestOrOrganizationDataRequest(),
				clazz);
	}

	/**
	 * @return the request element or null if not found
	 */
	public Request findRequest()
	{
		return findElementInList(cxml.getHeaderOrMessageOrRequestOrResponse(), Request.class);
	}

	/**
	 * Finds a response by type.
	 * 
	 * @param clazz
	 *           the element type class
	 * @return the response element type or null if not found
	 */
	public <T> T findResponseByType(final Class<T> clazz)
	{
		final Response response = findResponse();
		if (response == null)
		{
			return null;
		}

		return findElementInList(
				response
						.getProfileResponseOrPunchOutSetupResponseOrProviderSetupResponseOrGetPendingResponseOrSubscriptionListResponseOrSubscriptionContentResponseOrSupplierListResponseOrSupplierDataResponseOrAuthResponseOrDataResponseOrOrganizationDataResponse(),
				clazz);
	}

	/**
	 * @return the response instance of a {@link CXML} or null if not found
	 */
	public Response findResponse()
	{
		return findElementInList(cxml.getHeaderOrMessageOrRequestOrResponse(), Response.class);
	}

	protected <T> T findElementInList(final List<?> list, final Class<T> clazz)
	{
		return (T) CollectionUtils.find(list, PredicateUtils.instanceofPredicate(clazz));
	}

	/**
	 * Checks whether there is {@link Response} instance.
	 * 
	 * @return true if {@link CXML} contains element of type {@link Response}
	 */
	public boolean hasResponse()
	{
		return findResponse() != null;
	}
}
