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
package de.hybris.platform.ycommercewebservices.conv;

import com.thoughtworks.xstream.converters.Converter;


/**
 * Converters implementing this interface can redirect some operations to another converter (set as a target converter).
 */
public interface RedirectableConverter extends Converter
{
	/**
	 * Sets given converter as a target of redirection.
	 * 
	 * @param converter
	 *           converter to be used instead of current converter.
	 */
	void setTargetConverter(final Converter converter);

	/**
	 * @return {@link Class} that current converter is able to convert.
	 */
	Class getConvertedClass();
}
