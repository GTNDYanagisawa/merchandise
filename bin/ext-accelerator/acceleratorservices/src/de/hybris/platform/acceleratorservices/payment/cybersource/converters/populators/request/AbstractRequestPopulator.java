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
package de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.request;

import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.converters.Populator;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


public abstract class AbstractRequestPopulator<SOURCE, TARGET> implements Populator<SOURCE, TARGET>
{
	/**
	 * Common method to add a query parameter to the target {@link Map}<{@link String}, {@link String}>.
	 * 
	 * @param target
	 *           - the Map<String, String>.
	 * @param name
	 *           - the query parameter name.
	 * @param value
	 *           - the query parameter value.
	 */
	protected void addRequestQueryParam(final PaymentData target, final String name, final String value)
	{
		validateParameterNotNull(target, "Parameter target (PaymentData) cannot be null");

		final Map<String, String> parameterMap = (target.getParameters() != null ? target.getParameters()
				: new HashMap<String, String>());

		if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value))
		{
			parameterMap.put(name, value);
		}

		target.setParameters(parameterMap);
	}

	/**
	 * Common method to add a {@link Boolean} parameter to the target {@link Map}<{@link String}, {@link String}>.
	 * 
	 * @param target
	 *           - the Map<String, String>.
	 * @param name
	 *           - the query parameter name.
	 * @param value
	 *           - the {@link Boolean} query parameter value.
	 */
	protected void addBooleanParameter(final PaymentData target, final String name, final Boolean value)
	{
		validateParameterNotNull(target, "Parameter target (PaymentData) cannot be null");

		if (value != null)
		{
			addRequestQueryParam(target, name, String.valueOf(value));
		}
	}
}
