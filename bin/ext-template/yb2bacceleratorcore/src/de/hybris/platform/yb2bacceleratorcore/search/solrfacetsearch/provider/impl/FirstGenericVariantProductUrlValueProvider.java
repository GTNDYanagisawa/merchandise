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
package de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider.impl;

import de.hybris.platform.b2b.model.GenericVariantProductModel;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider.AbstractMultidimensionalProductFieldValueProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Value Provider for url of multidimensional products.
 */
public class FirstGenericVariantProductUrlValueProvider extends AbstractMultidimensionalProductFieldValueProvider
{
	private UrlResolver<ProductModel> productModelUrlResolver;

	private Comparator<GenericVariantProductModel> genericVariantProductModelComparator;

	@Override
	public Object getFieldValue(final ProductModel product)
	{
		String url = null;
		if (isVariantBaseProduct(product))
		{
			final Collection<VariantProductModel> variants = product.getVariants();
			if (CollectionUtils.isNotEmpty(variants))
			{
				final List<GenericVariantProductModel> genericVariants = new ArrayList<>();
				for (final VariantProductModel variant : variants)
				{
					if (variant instanceof GenericVariantProductModel)
					{
						genericVariants.add((GenericVariantProductModel) variant);
					}
				}
				if (!genericVariants.isEmpty())
				{
					Collections.sort(genericVariants, genericVariantProductModelComparator);
					url = productModelUrlResolver.resolve(genericVariants.get(0));
				}
			}
		}
		return url;
	}

	public UrlResolver<ProductModel> getProductModelUrlResolver()
	{
		return productModelUrlResolver;
	}

	@Required
	public void setProductModelUrlResolver(final UrlResolver<ProductModel> productModelUrlResolver)
	{
		this.productModelUrlResolver = productModelUrlResolver;
	}

	public Comparator<GenericVariantProductModel> getGenericVariantProductModelComparator()
	{
		return genericVariantProductModelComparator;
	}

	@Required
	public void setGenericVariantProductModelComparator(
			final Comparator<GenericVariantProductModel> genericVariantProductModelComparator)
	{
		this.genericVariantProductModelComparator = genericVariantProductModelComparator;
	}


}
