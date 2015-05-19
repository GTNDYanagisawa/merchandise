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
package com.sap.hybris.sapcustomerb2b.outbound;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;


/**
 * If B2B customer has changes regarding name, title or session language trigger export of customer to Data Hub
 */
public class DefaultB2BCustomerAddressInterceptor implements ValidateInterceptor<AddressModel>
{

	private B2BCustomerExportService b2bCustomerExportService;

	@Override
	public void onValidate(final AddressModel addressModel, final InterceptorContext ctx) throws InterceptorException
	{

		B2BCustomerModel b2bCustomerModel = null;

		//get the customer related to the address
		final ItemModel owner = addressModel.getOwner();
		if (owner instanceof B2BCustomerModel)
		{
			b2bCustomerModel = (B2BCustomerModel) owner;

			//check if the address is replicated from ERP

			// check if customer if is null or empty
			if (b2bCustomerModel.getCustomerID() == null || b2bCustomerModel.getCustomerID().isEmpty())
			{
				return;
			}

			//read the address to determine the phone number
			final String expectedPublicKey = b2bCustomerModel.getCustomerID() + "|" + b2bCustomerModel.getCustomerID()
					+ "|BUS1006001|null";
			if (!expectedPublicKey.equals(addressModel.getPublicKey()))
			{
				return;
			}

			// check if either name, title or sessionLanguage is modified
			if (ctx.isModified(addressModel, AddressModel.PHONE1))
			{
				// We have to send all addresses of the B2BCustomer, so we reuse the B2BCustomerExportService
				b2bCustomerExportService.prepareAndSend(b2bCustomerModel);
			}
		}

	}

	/**
	 * @return customerExportService
	 */
	public B2BCustomerExportService getB2bCustomerExportService()
	{
		return b2bCustomerExportService;
	}

	/**
	 * set customerExportService
	 * 
	 * @param b2bCustomerExportService
	 */
	public void setB2bCustomerExportService(final B2BCustomerExportService b2bCustomerExportService)
	{
		this.b2bCustomerExportService = b2bCustomerExportService;
	}

}
