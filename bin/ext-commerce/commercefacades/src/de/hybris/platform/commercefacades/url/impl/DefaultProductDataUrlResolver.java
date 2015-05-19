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
package de.hybris.platform.commercefacades.url.impl;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;

import org.springframework.beans.factory.annotation.Required;


/**
 * URL resolver for ProductData instances.
 * The pattern could be of the form:
 * /{category-path}/{product-name}/p/{product-code}
 */
public class DefaultProductDataUrlResolver extends AbstractUrlResolver<ProductData>
{
	private final String CACHE_KEY = DefaultProductDataUrlResolver.class.getName();

	private ProductService productService;
	private UrlResolver<ProductModel> productModelUrlResolver;

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected UrlResolver<ProductModel> getProductModelUrlResolver()
	{
		return productModelUrlResolver;
	}

	@Required
	public void setProductModelUrlResolver(final UrlResolver<ProductModel> productModelUrlResolver)
	{
		this.productModelUrlResolver = productModelUrlResolver;
	}

	@Override
	protected String getKey(final ProductData source)
	{
		return CACHE_KEY + "." + source.getCode();
	}

	@Override
	protected String resolveInternal(final ProductData source)
	{
		// Lookup the product
		final ProductModel product = getProductService().getProductForCode(source.getCode());

		return getProductModelUrlResolver().resolve(product);
	}
}
