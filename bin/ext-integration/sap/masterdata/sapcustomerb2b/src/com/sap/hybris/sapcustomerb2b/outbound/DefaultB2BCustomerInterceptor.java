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
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;


/**
 * If B2B customer has changes regarding name, title or session language trigger export of customer to Data Hub
 */
public class DefaultB2BCustomerInterceptor implements ValidateInterceptor<B2BCustomerModel>
{

	private B2BCustomerExportService b2bCustomerExportService;

	@Override
	public void onValidate(final B2BCustomerModel customerModel, final InterceptorContext ctx) throws InterceptorException
	{
		// check if customer if is null or empty
		if (customerModel.getCustomerID() == null || customerModel.getCustomerID().isEmpty())
		{
			return;
		}

		// check if either name, title or sessionLanguage is modified
		if (ctx.isModified(customerModel, CustomerModel.NAME) || ctx.isModified(customerModel, CustomerModel.TITLE)
				|| ctx.isModified(customerModel, CustomerModel.SESSIONLANGUAGE) || ctx.isModified(customerModel, CustomerModel.UID))
		{
			b2bCustomerExportService.prepareAndSend(customerModel);
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
