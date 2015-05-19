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
package de.hybris.platform.addonsupport.setup.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commerceservices.setup.data.ImpexMacroParameterData;


public class ParameterMapMergeImpexMacroParametersPopulator implements Populator<Object, ImpexMacroParameterData>
{
	private Map<String, String> parametersToMerge;

	@Override
	public void populate(final Object source, final ImpexMacroParameterData target) throws ConversionException
	{

		if (target.getAdditionalParameterMap() == null)
		{
			target.setAdditionalParameterMap(new HashMap<String, String>());
		}

		for (final String key : getParametersToMerge().keySet())
		{
			target.getAdditionalParameterMap().put(key, getParametersToMerge().get(key));
		}
	}

	/**
	 * @return the parametersToMerge
	 */
	public Map<String, String> getParametersToMerge()
	{
		return parametersToMerge;
	}

	/**
	 * @param parametersToMerge
	 *           the parametersToMerge to set
	 */
	@Required
	public void setParametersToMerge(final Map<String, String> parametersToMerge)
	{
		this.parametersToMerge = parametersToMerge;
	}
}
