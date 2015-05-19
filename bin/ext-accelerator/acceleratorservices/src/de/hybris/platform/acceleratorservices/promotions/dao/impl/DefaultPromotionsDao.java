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
package de.hybris.platform.acceleratorservices.promotions.dao.impl;

import de.hybris.platform.acceleratorservices.promotions.dao.PromotionsDao;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DefaultPromotionsDao extends AbstractItemDao implements PromotionsDao
{
	private final static String FIND_PROMOTION_FOR_CODE = "SELECT {" + AbstractPromotionModel.PK + "} FROM {"
			+ AbstractPromotionModel._TYPECODE + "} WHERE {" + AbstractPromotionModel.CODE + "} = ?code AND {"
			+ AbstractPromotionModel.IMMUTABLEKEYHASH + "} is null";

	@Override
	public AbstractPromotionModel getPromotionForCode(final String code)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		final List<AbstractPromotionModel> promos = doSearch(FIND_PROMOTION_FOR_CODE, params, AbstractPromotionModel.class);
		if (promos != null && !promos.isEmpty())
		{
			return promos.get(0);
		}
		return null;
	}

	protected <T> List<T> doSearch(final String query, final Map<String, Object> params, final Class<T> resultClass)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		if (params != null)
		{
			fQuery.addQueryParameters(params);
		}

		fQuery.setResultClassList(Collections.singletonList(resultClass));

		final SearchResult<T> searchResult = search(fQuery);
		return searchResult.getResult();
	}
}
