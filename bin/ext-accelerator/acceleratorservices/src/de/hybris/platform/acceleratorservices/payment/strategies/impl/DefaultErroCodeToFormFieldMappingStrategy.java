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
package de.hybris.platform.acceleratorservices.payment.strategies.impl;

import de.hybris.platform.acceleratorservices.payment.strategies.ErroCodeToFormFieldMappingStrategy;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Required;


public class DefaultErroCodeToFormFieldMappingStrategy implements ErroCodeToFormFieldMappingStrategy
{
	private Map<Integer, List<String>> errorCodeToFieldMapping;

	@Override
	public List<String> getFieldForErrorCode(final Integer code)
	{
		return this.errorCodeToFieldMapping.get(code);
	}

	protected Map<Integer, List<String>> getErrorCodeToFieldMapping()
	{
		return errorCodeToFieldMapping;
	}

	@Required
	public void setErrorCodeToFieldMapping(final Map<Integer, List<String>> errorCodeToFieldMapping)
	{
		this.errorCodeToFieldMapping = errorCodeToFieldMapping;
	}

}
