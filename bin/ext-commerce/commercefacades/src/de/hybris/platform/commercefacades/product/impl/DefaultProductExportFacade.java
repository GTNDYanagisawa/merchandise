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
package de.hybris.platform.commercefacades.product.impl;


import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.product.ProductExportFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.converters.populator.ProductCategoriesPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductResultData;
import de.hybris.platform.commerceservices.product.ExportProductService;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ProductExportFacade}
 */
public class DefaultProductExportFacade implements ProductExportFacade
{
	private ProductService productService;
	private ExportProductService exportProductService;
	private ModelService modelService;
	private Converter<ProductModel, ProductData> productConverter;

	private ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator;
	private ProductCategoriesPopulator productCategoriesPopulator;
	private CatalogVersionService catalogVersionService;

	/**
	 * Get an attribute value from a product. If the attribute value is null and the product is a variant then the same
	 * attribute will be requested from the base product.
	 * 
	 * @param productModel
	 *           the product
	 * @param attribute
	 *           the name of the attribute to lookup
	 * @return the value of the attribute
	 */
	protected Object getProductAttribute(final ProductModel productModel, final String attribute)
	{
		final Object value = getModelService().getAttributeValue(productModel, attribute);
		if (value == null && productModel instanceof VariantProductModel)
		{
			final ProductModel baseProduct = ((VariantProductModel) productModel).getBaseProduct();
			if (baseProduct != null)
			{
				return getProductAttribute(baseProduct, attribute);
			}
		}
		return value;
	}


	@Override
	public ProductResultData getAllProductsForOptions(final String catalog, final String version,
			final Collection<ProductOption> options, final int start, final int count)
	{
		final Collection<CatalogVersionModel> exportedCatalogVersions = getExportedCatalogVersions(catalog, version);


		final SearchPageData<ProductModel> searchResult = exportProductService
				.getAllProducts(exportedCatalogVersions, start, count);
		final List<ProductModel> models = searchResult.getResults();
		final List<ProductData> productsData = convertModelsToDTOs(options, models);
		final ProductResultData result = createResultDataWithPagination(start, count, productsData, searchResult.getPagination()
				.getPageSize(), searchResult.getPagination().getTotalNumberOfResults());
		return result;
	}

	protected Collection<CatalogVersionModel> getExportedCatalogVersions(final String catalog, final String version)
	{
		if (catalog == null && version == null)
		{
			return catalogVersionService.getSessionCatalogVersions();
		}
		else
		{
			return Collections.singletonList(catalogVersionService.getCatalogVersion(catalog, version));
		}
	}


	@Override
	public ProductResultData getOnlyModifiedProductsForOptions(final String catalog, final String version,
			final Date modifiedTime, final Collection<ProductOption> options, final int start, final int count)
	{
		final Collection<CatalogVersionModel> exportedCatalogVersions = getExportedCatalogVersions(catalog, version);
		final SearchPageData<ProductModel> modifiedProducts = exportProductService.getModifiedProducts(exportedCatalogVersions,
				modifiedTime, start, count);
		final List<ProductModel> models = modifiedProducts.getResults();
		final List<ProductData> productsData = convertModelsToDTOs(options, models);
		final ProductResultData result = createResultDataWithPagination(start, count, productsData, modifiedProducts
				.getPagination().getPageSize(), modifiedProducts.getPagination().getTotalNumberOfResults());
		return result;
	}

	protected ProductResultData createResultDataWithPagination(final int start, final int count,
			final List<ProductData> productsData, final int pageSize, final long totalNumberOfResults)
	{
		final ProductResultData result = new ProductResultData();
		result.setProducts(productsData);
		result.setCount(pageSize);
		result.setRequestedCount(count);
		result.setRequestedStart(start);
		result.setTotalCount((int) totalNumberOfResults);
		return result;
	}

	protected List<ProductData> convertModelsToDTOs(final Collection<ProductOption> options, final List<ProductModel> models)
	{
		final List<ProductData> productsData = new ArrayList<ProductData>();
		for (final ProductModel model : models)
		{

			final ProductData data = getProductConverter().convert(model);

			if (options != null)
			{
				getProductConfiguredPopulator().populate(model, data, options);
			}

			getProductCategoriesPopulator().populate(model, data);

			productsData.add(data);
		}
		return productsData;
	}

	protected ExportProductService getExportProductService()
	{
		return exportProductService;
	}

	@Required
	public void setExportProductService(final ExportProductService exportProductService)
	{
		this.exportProductService = exportProductService;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	protected ConfigurablePopulator<ProductModel, ProductData, ProductOption> getProductConfiguredPopulator()
	{
		return productConfiguredPopulator;
	}

	@Required
	public void setProductConfiguredPopulator(
			final ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator)
	{
		this.productConfiguredPopulator = productConfiguredPopulator;
	}

	protected ProductCategoriesPopulator getProductCategoriesPopulator()
	{
		return productCategoriesPopulator;
	}

	@Required
	public void setProductCategoriesPopulator(final ProductCategoriesPopulator productCategoriesPopulator)
	{
		this.productCategoriesPopulator = productCategoriesPopulator;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
}
