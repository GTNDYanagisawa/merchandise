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
package de.hybris.platform.b2b.punchout.services.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutResponseCode;
import de.hybris.platform.b2b.punchout.PunchOutResponseMessage;
import de.hybris.platform.b2b.punchout.actions.PunchOutProcessingAction;
import de.hybris.platform.b2b.punchout.services.CXMLBuilder;
import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;
import de.hybris.platform.b2b.punchout.services.PunchOutService;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.cxml.CXML;
import org.cxml.Credential;
import org.cxml.From;
import org.cxml.Header;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link PunchOutService}.
 */
public class DefaultPunchOutService implements PunchOutService
{
	private static final Logger LOG = Logger.getLogger(PunchOutService.class);

	private List<PunchOutProcessingAction<CXML, CXML>> setUpRequestProcessingActions;
	private List<PunchOutProcessingAction<CartModel, CXML>> punchOutTransactionActions;
	private List<PunchOutProcessingAction<CXML, CartModel>> purchaseOrderProcessingActions;
	private List<PunchOutProcessingAction<CXML, CXML>> profileRequestProcessingActions;

	@Resource
	private CartService cartService;

	@Resource
	private CommerceCartService commerceCartService;

	@Resource
	private PunchOutCredentialService punchOutCredentialService;

	@Override
	public CXML processPunchOutSetUpRequest(final CXML request)
	{
		final CXML response = CXMLBuilder.newInstance().withResponseCode(PunchOutResponseCode.SUCCESS)
				.withResponseMessage(PunchOutResponseMessage.SUCCESS).create();
		for (final PunchOutProcessingAction<CXML, CXML> action : getSetUpRequestProcessingActions())
		{
			try
			{
				action.process(request, response);
			}
			catch (final PunchOutException error)
			{
				final String message = String
						.format(
								"The request processing is canceled due to the following exception, the cxml error code is %s. This was written to the response.",
								String.valueOf(error.getErrorCode()));
				LOG.error(message, error);

				return CXMLBuilder.newInstance().withResponseCode(error.getErrorCode()).withResponseMessage(error.getMessage())
						.create();
			}
		}
		return response;
	}

	@Override
	public CXML processPunchOutOrderMessage()
	{
		final CartModel cartModel = getCartModel();

		final CXML message = CXMLBuilder.newInstance().create();
		for (final PunchOutProcessingAction<CartModel, CXML> action : getPunchOutTransactionActions())
		{
			action.process(cartModel, message);
		}

		getCommerceCartService().removeAllEntries(cartModel);

		return message;
	}

	@Override
	public CXML processCancelPunchOutOrderMessage()
	{
		final CartModel cartModel = getCartModel();
		getCommerceCartService().removeAllEntries(cartModel);

		return processPunchOutOrderMessage();
	}

	@Override
	public CXML processPurchaseOrderRequest(final CXML requestBody, final CartModel cartModel)
	{
		for (final PunchOutProcessingAction<CXML, CartModel> action : getPurchaseOrderProcessingActions())
		{
			action.process(requestBody, cartModel);
		}
		return CXMLBuilder.newInstance().withResponseCode(PunchOutResponseCode.SUCCESS)
				.withResponseMessage(PunchOutResponseMessage.OK).create();
	}

	@Override
	public String retrieveIdentity(final CXML request)
	{
		String userId = null;
		final CXMLElementBrowser cXmlBrowser = new CXMLElementBrowser(request);

		final Header header = cXmlBrowser.findHeader();
		Validate.notNull(header, "Punchout cXML request incomplete. Missing Header node.");

		final From from = header.getFrom();

		B2BCustomerModel customer = null;
		for (final Credential credential : from.getCredential())
		{
			customer = this.punchOutCredentialService.getCustomerForCredentialNoAuth(credential);
			if (customer != null)
			{
				userId = customer.getUid();
				break;
			}
		}

		return userId;
	}

	@Override
	public CXML processProfileRequest(final CXML request)
	{
		final CXML response = CXMLBuilder.newInstance().create();
		for (final PunchOutProcessingAction<CXML, CXML> action : getProfileRequestProcessingActions())
		{
			action.process(request, response);
		}
		return response;
	}

	protected CartModel getCartModel()
	{
		if (getCartService().hasSessionCart())
		{
			return getCartService().getSessionCart();
		}
		else
		{
			throw new IllegalStateException(
					"There was no cart in the session found, please call the method only if a cart is present.");
		}
	}

	@Required
	public void setSetUpRequestProcessingActions(final List<PunchOutProcessingAction<CXML, CXML>> setUpRequestProcessingActions)
	{
		this.setUpRequestProcessingActions = setUpRequestProcessingActions;
	}

	protected List<PunchOutProcessingAction<CartModel, CXML>> getPunchOutTransactionActions()
	{
		return punchOutTransactionActions;
	}

	@Required
	public void setPunchOutTransactionActions(final List<PunchOutProcessingAction<CartModel, CXML>> createRequisitionReponseActions)
	{
		this.punchOutTransactionActions = createRequisitionReponseActions;
	}

	public List<PunchOutProcessingAction<CXML, CartModel>> getPurchaseOrderProcessingActions()
	{
		return purchaseOrderProcessingActions;
	}

	@Required
	public void setPurchaseOrderProcessingActions(
			final List<PunchOutProcessingAction<CXML, CartModel>> purchaseOrderProcessingActions)
	{
		this.purchaseOrderProcessingActions = purchaseOrderProcessingActions;
	}

	public List<PunchOutProcessingAction<CXML, CXML>> getSetUpRequestProcessingActions()
	{
		return setUpRequestProcessingActions;
	}

	public List<PunchOutProcessingAction<CXML, CXML>> getProfileRequestProcessingActions()
	{
		return profileRequestProcessingActions;
	}

	@Required
	public void setProfileRequestProcessingActions(final List<PunchOutProcessingAction<CXML, CXML>> profileRequestProcessingActions)
	{
		this.profileRequestProcessingActions = profileRequestProcessingActions;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	public PunchOutCredentialService getPunchOutCredentialService()
	{
		return punchOutCredentialService;
	}

	@Required
	public void setPunchOutCredentialService(final PunchOutCredentialService punchOutCredentialService)
	{
		this.punchOutCredentialService = punchOutCredentialService;
	}

}
