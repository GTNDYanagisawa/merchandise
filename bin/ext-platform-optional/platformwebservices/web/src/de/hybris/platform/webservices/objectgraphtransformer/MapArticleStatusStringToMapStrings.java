/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.webservices.objectgraphtransformer;

import java.util.Map;

import de.hybris.platform.catalog.enums.ArticleStatus;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyInterceptor;


//Probably will be removed, waiting for DEL-260
public class MapArticleStatusStringToMapStrings implements PropertyInterceptor<Map<ArticleStatus, String>, Map<String, String>>
{

	@Override
	public Map<String, String> intercept(final PropertyContext ctx, final Map<ArticleStatus, String> source)
	{
		//TODO convert map to map
		return null;
	}

}
