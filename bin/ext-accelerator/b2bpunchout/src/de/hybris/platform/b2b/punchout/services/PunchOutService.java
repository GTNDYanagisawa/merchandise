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

import de.hybris.platform.core.model.order.CartModel;

import org.cxml.CXML;
import org.cxml.OrderRequest;
import org.cxml.ProfileResponse;
import org.cxml.PunchOutOrderMessage;


public interface PunchOutService
{
	/**
	 * Processes a new PunchOut setup request.
	 * 
	 * @param request
	 *           the request
	 * @return the {@link CXML} response
	 */
	CXML processPunchOutSetUpRequest(final CXML request);

	/**
	 * Creates the cmxl message for a {@link PunchOutOrderMessage} to send an Order, using the session cart.
	 * 
	 * @return the {@link CXML} object representing an OrderMessage filled with information from the session cart.
	 */
	CXML processPunchOutOrderMessage();


	/**
	 * Processes a purchase order ({@link OrderRequest}) by populating a cart and handles the response in the form of
	 * {@link CXML}.
	 * 
	 * @param requestBody
	 *           the request {@link CXML} object
	 * @param cartModel
	 *           the shopping cart data to populate
	 * @return the {@link CXML} response
	 */
	CXML processPurchaseOrderRequest(CXML requestBody, CartModel cartModel);

	/**
	 * Retrieve the user id of the originator of the cXML request.<br>
	 * <i>f.y.i.: as there may be multiple credentials declared, the first valid one will be used.</i>
	 * 
	 * @param request
	 *           The cXML request.
	 * @return The identity in the "From" tag.
	 */
	String retrieveIdentity(final CXML request);

	/**
	 * Processes a profile request by returned a ProfileResponse with populated supported transactions.
	 * 
	 * @param request
	 *           the profile request
	 * @return the resulting {@link CXML} instance with a {@link ProfileResponse} part of it
	 */
	CXML processProfileRequest(CXML request);

	/**
	 * Creates the cancel message for Ariba.
	 * 
	 * @returnthe the {@link CXML} with the cancel message.
	 */
	CXML processCancelPunchOutOrderMessage();
}
