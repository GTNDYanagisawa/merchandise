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
package de.hybris.platform.acceleratorservices.payment.strategies;

import java.util.List;

/**
 *   A mapping between Payment Service Providers error codes and Spring form ids.
 */
public interface ErroCodeToFormFieldMappingStrategy
{
	/**
	 * Gets a spring form field id that maps to an error code from a payment provider
	 * @param code An error code from PSP
	 * @return A form field id
	 */
	List<String> getFieldForErrorCode(Integer code);
}
