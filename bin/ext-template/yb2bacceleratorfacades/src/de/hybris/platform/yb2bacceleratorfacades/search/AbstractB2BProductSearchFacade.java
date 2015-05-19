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
package de.hybris.platform.yb2bacceleratorfacades.search;

import de.hybris.platform.b2b.model.GenericVariantProductModel;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


public abstract class AbstractB2BProductSearchFacade<ITEM extends ProductData> implements B2BProductSearchFacade<ITEM>
{
	private ProductFacade productFacade;

	private ProductService productService;

	private ConfigurablePopulator<ProductModel, ITEM, ProductOption> productConfiguredPopulator;

	public void populateVariantProducts(final ProductSearchPageData<SearchStateData, ITEM> pageData)
	{
		if ((pageData != null) && (pageData.getResults() != null))
		{
			if (CollectionUtils.isNotEmpty(pageData.getResults()))
			{
				final Collection<ProductOption> optionsWithVariants = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE,
						ProductOption.SUMMARY, ProductOption.DESCRIPTION, ProductOption.GALLERY, ProductOption.CATEGORIES,
						ProductOption.REVIEW, ProductOption.PROMOTIONS, ProductOption.CLASSIFICATION, ProductOption.STOCK,
						ProductOption.VOLUME_PRICES, ProductOption.PRICE_RANGE, ProductOption.VARIANT_MATRIX_BASE,
						ProductOption.VARIANT_MATRIX_PRICE, ProductOption.VARIANT_MATRIX_MEDIA, ProductOption.VARIANT_MATRIX_STOCK,
						ProductOption.VARIANT_MATRIX_URL, ProductOption.VARIANT_MATRIX_ALL_OPTIONS);

				final Collection<ProductOption> optionsWithoutVariants = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE,
						ProductOption.STOCK);

				for (final ITEM productData : pageData.getResults())
				{

					final ProductModel productModel = productService.getProductForCode(productData.getCode());
					if (CollectionUtils.isNotEmpty(productModel.getVariants()))
					{
						// check if product has at least one generic variant
						GenericVariantProductModel firstVariant = null;
						for (final VariantProductModel variant : productModel.getVariants())
						{
							if (variant instanceof GenericVariantProductModel)
							{
								firstVariant = (GenericVariantProductModel) variant;
								break;
							}
						}

						if (firstVariant != null)
						{
							final ProductData firstVariantData = productFacade.getProductForOptions(firstVariant, null);
							this.productConfiguredPopulator.populate(firstVariant, productData, optionsWithVariants);
							// set url from first variant into base product, to enable links to product details and order form
							productData.setUrl(firstVariantData.getUrl());
						}
					}
					else
					{
						this.productConfiguredPopulator.populate(productModel, productData, optionsWithoutVariants);
					}
				}
			}
		}
		else
		{
			throw new IllegalArgumentException("Cannot populate ProductSearchPageData with null value or null results.");
		}
	}

	protected ProductFacade getProductFacade()
	{
		return this.productFacade;
	}

	protected ProductService getProductService()
	{
		return this.productService;
	}

	protected ConfigurablePopulator<ProductModel, ITEM, ProductOption> getProductConfiguredPopulator()
	{
		return this.productConfiguredPopulator;
	}


	@Required
	public void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	@Required
	public void setProductConfiguredPopulator(
			final ConfigurablePopulator<ProductModel, ITEM, ProductOption> productConfiguredPopulator)
	{
		this.productConfiguredPopulator = productConfiguredPopulator;
	}
}
