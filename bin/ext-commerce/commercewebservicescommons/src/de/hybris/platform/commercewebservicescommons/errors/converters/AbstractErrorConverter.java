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

package de.hybris.platform.commercewebservicescommons.errors.converters;

import de.hybris.platform.commercewebservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.converters.impl.AbstractConverter;

import java.util.LinkedList;
import java.util.List;


/**
 * Abstract error converter for converting supported objects to a list of {@link de.hybris.platform.commercewebservicescommons.dto.error.ErrorWsDTO}.
 */
public abstract class AbstractErrorConverter extends AbstractConverter<Object, List<ErrorWsDTO>>
{
	public abstract boolean supports(Class clazz);

	@Override
	protected List<ErrorWsDTO> createTarget()
	{
		return new LinkedList<>();
	}

	protected ErrorWsDTO createTargetElement()
	{
		return new ErrorWsDTO();
	}

}
