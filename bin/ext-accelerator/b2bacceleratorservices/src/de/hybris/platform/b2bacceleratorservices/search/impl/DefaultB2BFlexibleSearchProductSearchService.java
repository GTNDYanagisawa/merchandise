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
package de.hybris.platform.b2bacceleratorservices.search.impl;

import de.hybris.platform.b2bacceleratorservices.product.B2BProductService;
import de.hybris.platform.b2bacceleratorservices.search.B2BProductSearchService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;


public class DefaultB2BFlexibleSearchProductSearchService implements B2BProductSearchService<ProductData>
{

	private B2BProductService b2BProductService;

	@Override
	public SearchPageData<ProductModel> findProductsBySkus(final Collection<String> skus, final PageableData pageableData)
	{
		return b2BProductService.findProductsForSkus(skus, pageableData);
	}

	@Required
	public void setB2BProductService(final B2BProductService b2BProductService)
	{
		this.b2BProductService = b2BProductService;
	}

}
