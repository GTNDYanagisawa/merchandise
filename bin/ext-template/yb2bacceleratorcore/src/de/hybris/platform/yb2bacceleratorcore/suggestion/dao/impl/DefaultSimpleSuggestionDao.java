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
package de.hybris.platform.yb2bacceleratorcore.suggestion.dao.impl;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.yb2bacceleratorcore.suggestion.dao.SimpleSuggestionDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link SimpleSuggestionDao}.
 * 
 * Finds products that are related products that the user has bought.
 */
public class DefaultSimpleSuggestionDao extends AbstractItemDao implements SimpleSuggestionDao
{
	private static final int DEFAULT_LIMIT = 100;
	private static final String REF_QUERY_PARAM_CATEGORY = "category";
	private static final String REF_QUERY_PARAM_USER = "user";
	private static final String REF_QUERY_PARAM_TYPE = "referenceType";
	private static final String REF_QUERY_PARAM_TYPES = "referenceTypes";

	private static final String REF_QUERY_START = "SELECT {p.PK} FROM {Product AS p "
			+ " LEFT JOIN ProductReference AS r ON {p.PK}={r.target} LEFT JOIN OrderEntry AS e ON {r.source}={e.product}"
			+ " LEFT JOIN Order AS o ON {e.order}={o.PK}" + " LEFT JOIN CategoryProductRelation AS c2p ON {r.source}={c2p.target}"
			+ " LEFT JOIN Category AS c ON {c2p.source}={c.PK} } WHERE {o.user}=?user AND {c.PK}=?category";
	private static final String REF_QUERY_TYPE = " AND {r.referenceType}=?referenceType";
	private static final String REF_QUERY_TYPES = " AND {r.referenceType} IN (?referenceTypes)";
	private static final String REF_QUERY_SUB = " AND NOT EXISTS (	{{"
			+ " SELECT 1 FROM {OrderEntry AS e2 LEFT JOIN Order AS o2 ON {e2.order}={o2.PK} } "
			+ " WHERE {e2.product}={r.target} AND {o2.user}=?user }}) ";
	private static final String REF_QUERY_END = " ORDER BY {o.creationTime} DESC";


	@Override
	public List<ProductModel> findProductsRelatedToPurchasedProductsByCategory(final CategoryModel category, final UserModel user,
			final List<ProductReferenceTypeEnum> referenceTypes, final boolean excludePurchased, final Integer limit)
	{
		Assert.notNull(category);
		Assert.notNull(user);

		final int maxResultCount = limit == null ? DEFAULT_LIMIT : limit.intValue();

		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(REF_QUERY_START);
		if (excludePurchased)
		{
			builder.append(REF_QUERY_SUB);
		}
		if (CollectionUtils.isNotEmpty(referenceTypes))
		{
			builder.append(REF_QUERY_TYPES);
			params.put(REF_QUERY_PARAM_TYPES, referenceTypes);
		}
		builder.append(REF_QUERY_END);

		params.put(REF_QUERY_PARAM_USER, user);
		params.put(REF_QUERY_PARAM_CATEGORY, category);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		query.setNeedTotal(false);
		query.setCount(maxResultCount);

		final SearchResult<ProductModel> result = getFlexibleSearchService().search(query);
		return result.getResult();
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public List<ProductModel> findProductsRelatedToPurchasedProductsByCategory(final CategoryModel category, final UserModel user,
			final ProductReferenceTypeEnum referenceType, final boolean excludePurchased, final Integer limit)
	{
		Assert.notNull(category);
		Assert.notNull(user);

		final int maxResultCount = limit == null ? DEFAULT_LIMIT : limit.intValue();

		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(REF_QUERY_START);
		if (excludePurchased)
		{
			builder.append(REF_QUERY_SUB);
		}
		if (referenceType != null)
		{
			builder.append(REF_QUERY_TYPE);
			params.put(REF_QUERY_PARAM_TYPE, referenceType);
		}
		builder.append(REF_QUERY_END);

		params.put(REF_QUERY_PARAM_USER, user);
		params.put(REF_QUERY_PARAM_CATEGORY, category);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		query.setNeedTotal(false);
		query.setCount(maxResultCount);

		final SearchResult<ProductModel> result = getFlexibleSearchService().search(query);
		return result.getResult();
	}
}
