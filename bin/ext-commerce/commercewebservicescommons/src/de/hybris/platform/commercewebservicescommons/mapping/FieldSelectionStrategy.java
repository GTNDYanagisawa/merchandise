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

package de.hybris.platform.commercewebservicescommons.mapping;

import ma.glasnost.orika.MappingContext;


/**
 * Strategy for selecting field based on mapping context
 */
public interface FieldSelectionStrategy
{
	/**
	 * This method is called at runtime to determine whether the mapping implied by the field names from mapping context
	 * should be performed
	 */
	boolean shouldMap(Object source, Object dest, MappingContext mappingContext);
}
