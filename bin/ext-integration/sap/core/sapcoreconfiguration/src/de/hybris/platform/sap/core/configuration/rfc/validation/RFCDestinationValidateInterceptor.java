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
package de.hybris.platform.sap.core.configuration.rfc.validation;

import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.util.localization.Localization;


/**
 * Perform validation of SAPRFCDestination model data.
 * 
 */
public class RFCDestinationValidateInterceptor implements ValidateInterceptor<SAPRFCDestinationModel>
{


	@Override
	public void onValidate(final SAPRFCDestinationModel sapRFCDestinationModel, final InterceptorContext ctx)
			throws InterceptorException
	{
		// If SNC is selected the SNCPartnerName and the quality of protections
		// (QoP) must not be empty
		if (sapRFCDestinationModel.getSncMode()
				&& (sapRFCDestinationModel.getSncPartnerName() == null || sapRFCDestinationModel.getSncPartnerName().isEmpty()
						|| sapRFCDestinationModel.getSncQoP() == null || sapRFCDestinationModel.getSncQoP().toString().isEmpty()))
		{
			throw new InterceptorException(Localization.getLocalizedString("validation.RFCDestination.IncompleteSncData"));
		}

		// check required fields in case of server connection
		if (sapRFCDestinationModel.getConnectionType() != null && sapRFCDestinationModel.getConnectionType().booleanValue())
		{
			// Target host and Instance number are required
			if (sapRFCDestinationModel.getTargetHost() == null || sapRFCDestinationModel.getTargetHost().isEmpty()
					|| sapRFCDestinationModel.getInstance() == null || sapRFCDestinationModel.getInstance().isEmpty())
			{
				throw new InterceptorException(
						Localization.getLocalizedString("validation.RFCDestination.IncompleteServerConnectionData"));
			}
		}
		// check required fields for group connection
		else
		{
			if (sapRFCDestinationModel.getMessageServer() == null || sapRFCDestinationModel.getMessageServer().isEmpty()
					|| sapRFCDestinationModel.getGroup() == null || sapRFCDestinationModel.getGroup().isEmpty()
				    || sapRFCDestinationModel.getSid() == null || sapRFCDestinationModel.getSid().isEmpty())
			{
				throw new InterceptorException(
						Localization.getLocalizedString("validation.RFCDestination.IncompleteGroupConnectionData"));
			}
		}

		// check required fields if connection pooling is enabled
		if (sapRFCDestinationModel.getPooledConnectionMode() != null
				&& sapRFCDestinationModel.getPooledConnectionMode().booleanValue()
				&& (sapRFCDestinationModel.getPoolSize() == null || sapRFCDestinationModel.getPoolSize().isEmpty()
						|| sapRFCDestinationModel.getMaxConnections() == null || sapRFCDestinationModel.getMaxConnections().isEmpty()
						|| sapRFCDestinationModel.getMaxWaitTime() == null || sapRFCDestinationModel.getMaxWaitTime().isEmpty()))
		{
			throw new InterceptorException(Localization.getLocalizedString("validation.RFCDestination.IncompletePoolingData"));
		}
	}
}
