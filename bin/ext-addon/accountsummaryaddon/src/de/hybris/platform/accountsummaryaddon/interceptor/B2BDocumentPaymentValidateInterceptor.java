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
package de.hybris.platform.accountsummaryaddon.interceptor;

import de.hybris.platform.accountsummaryaddon.model.B2BDocumentPaymentInfoModel;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;


public class B2BDocumentPaymentValidateInterceptor implements ValidateInterceptor
{

	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof B2BDocumentPaymentInfoModel)
		{
			final B2BDocumentPaymentInfoModel documentPaymentInfoModel = (B2BDocumentPaymentInfoModel) model;

			if (documentPaymentInfoModel.getCcTransactionNumber() != null && documentPaymentInfoModel.getUseDocument() != null)
			{
				throw new InterceptorException(
						"Credit Card transaction number and Use Document cannot have values at the same time");
			}
			else if (documentPaymentInfoModel.getCcTransactionNumber() == null && documentPaymentInfoModel.getUseDocument() == null)
			{
				throw new InterceptorException("Credit Card transaction number or Use Document is required");
			}
		}
	}
}
