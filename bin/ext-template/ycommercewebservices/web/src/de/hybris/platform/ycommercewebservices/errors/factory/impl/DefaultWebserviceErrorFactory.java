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
 */

package de.hybris.platform.ycommercewebservices.errors.factory.impl;

import de.hybris.platform.commercewebservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.converters.AbstractErrorConverter;
import de.hybris.platform.commercewebservicescommons.errors.factory.WebserviceErrorFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default factory used to create a list of errors by using configured converters.
 */
public class DefaultWebserviceErrorFactory implements WebserviceErrorFactory
{
	private List<AbstractErrorConverter> converters;

	@Override
	public List<ErrorWsDTO> createErrorList(final Object obj)
	{
		final List<ErrorWsDTO> errors = createTarget();
		final Iterator<AbstractErrorConverter> it = converters.iterator();
		while (it.hasNext())
		{
			final AbstractErrorConverter converter = it.next();
			if (converter.supports(obj.getClass()))
			{
				errors.addAll(converter.convert(obj));
			}
		}
		return errors;
	}

	protected List<ErrorWsDTO> createTarget()
	{
		return new LinkedList<>();
	}

	@Required
	public void setConverters(final List<AbstractErrorConverter> converters)
	{
		this.converters = converters;
	}
}
