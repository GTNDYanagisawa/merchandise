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
package de.hybris.platform.acceleratorservices.urldecoder;

/**
 * Interface for 'decoding' a Frontend Url into a related business object.
 * 
 * 
 */
public interface FrontendUrlDecoder<T>
{
	T decode(final String url);
}
