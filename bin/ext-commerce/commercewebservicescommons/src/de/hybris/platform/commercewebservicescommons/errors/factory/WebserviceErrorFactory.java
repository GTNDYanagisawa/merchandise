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

package de.hybris.platform.commercewebservicescommons.errors.factory;

import de.hybris.platform.commercewebservicescommons.dto.error.ErrorWsDTO;

import java.util.List;


/**
 * Factory that produces list of errors from given object.
 */
public interface WebserviceErrorFactory
{
	public List<ErrorWsDTO> createErrorList(final Object obj);
}
