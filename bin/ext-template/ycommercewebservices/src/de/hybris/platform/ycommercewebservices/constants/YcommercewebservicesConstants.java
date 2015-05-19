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
package de.hybris.platform.ycommercewebservices.constants;

@SuppressWarnings("PMD")
public class YcommercewebservicesConstants extends GeneratedYcommercewebservicesConstants
{

	private YcommercewebservicesConstants()
	{
		//empty
	}

	public static final String CONTINUE_URL = "session_continue_url";
	public static final String CONTINUE_URL_PAGE = "session_continue_url_page";
	public static final String OPTIONS_SEPARATOR = ",";

	public static final String HTTP_REQUEST_PARAM_LANGUAGE = "lang";
	public static final String HTTP_REQUEST_PARAM_CURRENCY = "curr";

	public static final String ROOT_CONTEXT_PROPERTY = "commercewebservices.rootcontext";
	public static final String DEFAULT_ROOT_CONTEXT = "/rest/v1/,/rest/v2/";
	public static final String URL_SPECIAL_CHARACTERS_PROPERTY = "commercewebservices.url.special.characters";
	public static final String DEFAULT_URL_SPECIAL_CHARACTERS = "?,/";
}
