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
package de.hybris.platform.sap.core.configuration.http.validation;

import de.hybris.platform.sap.core.configuration.enums.HTTPAuthenticationType;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.util.localization.Localization;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Perform validation of HTTPDestination model.
 * 
 */
public class HTTPDestinationValidateInterceptor implements ValidateInterceptor<SAPHTTPDestinationModel>
{

	@Override
	public void onValidate(final SAPHTTPDestinationModel sapHTTPDestinationModel, final InterceptorContext ctx)
			throws InterceptorException
	{
		//------------------------------
		// Validate target URL
		//------------------------------

		// Check if URL is available
		if (sapHTTPDestinationModel.getTargetURL() == null || sapHTTPDestinationModel.getTargetURL().isEmpty())
		{
			throw new InterceptorException(Localization.getLocalizedString("validation.HttpDestination.EmptyTargetUrl"));
		}
		else
		{
			// Check if URL is well formed
			try
			{
				@SuppressWarnings("unused")
				final URL checkedURL = new URL(sapHTTPDestinationModel.getTargetURL());
			}
			catch (final MalformedURLException e)
			{
				throw new InterceptorException(Localization.getLocalizedString("validation.HttpDestination.WrongURLFormat"), e);
			}
		}

		//------------------------------
		// Validate authentication data
		//------------------------------

		// Check if user-id and password are available in case of BASIC AUTHENTICATION is selected
		if (sapHTTPDestinationModel.getAuthenticationType() != null
				&& sapHTTPDestinationModel.getAuthenticationType().getCode() != null
				&& sapHTTPDestinationModel.getAuthenticationType().getCode()
						.equals(HTTPAuthenticationType.BASIC_AUTHENTICATION.toString())
				&& (sapHTTPDestinationModel.getPassword() == null || sapHTTPDestinationModel.getPassword().isEmpty()
						|| sapHTTPDestinationModel.getUserid() == null || sapHTTPDestinationModel.getUserid().isEmpty()))
		{
			throw new InterceptorException(
					Localization.getLocalizedString("validation.HttpDestination.IncompleteAuthenticationData"));
		}
	}

}
