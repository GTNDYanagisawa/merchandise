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
package de.hybris.platform.commercefacades.catalog.converters.populator;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import de.hybris.platform.commercefacades.converter.PageablePopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link CategoryHierarchyData} from {@link CategoryModel} using specific {@link CatalogOption}s
 */
public class CategoryHierarchyPopulator implements PageablePopulator<CategoryModel, CategoryHierarchyData, CatalogOption>
{
	private AbstractPopulatingConverter<ProductModel, ProductData> productConverter;
	private ProductService productService;
	private AbstractUrlResolver<CategoryModel> categoryUrlResolver;

	@Override
	public void populate(final CategoryModel source, final CategoryHierarchyData target,
			final Collection<? extends CatalogOption> options, final PageOption page) throws ConversionException
	{
		target.setId(source.getCode());
		target.setName(source.getName());
		target.setLastModified(source.getModifiedtime());
		target.setUrl(categoryUrlResolver.resolve(source));
		target.setProducts(new ArrayList<ProductData>());
		target.setSubcategories(new ArrayList<CategoryHierarchyData>());

		if (options.contains(CatalogOption.PRODUCTS))
		{
			final List<ProductModel> products = getProductService().getProductsForCategory(source, page.getPageStart(),
					page.getPageSize());
			for (final ProductModel product : products)
			{
				final ProductData productData = getProductConverter().convert(product);
				target.getProducts().add(productData);
			}
		}

		if (page.includeInformationAboutPages())
		{
			final Integer totalNumber = getProductService().getAllProductsCountForCategory(source);
			final Integer numberOfPages = Integer.valueOf((int) (Math.ceil(totalNumber.doubleValue() / page.getPageSize())));
			target.setTotalNumber(totalNumber);
			target.setCurrentPage(Integer.valueOf(page.getPageNumber()));
			target.setPageSize(Integer.valueOf(page.getPageSize()));
			target.setNumberOfPages(numberOfPages);
		}

		if (options.contains(CatalogOption.SUBCATEGORIES))
		{
			recursive(target, source, true, options);
		}
	}

	protected void recursive(final CategoryHierarchyData categoryData2, final CategoryModel category, final boolean root,
			final Collection<? extends CatalogOption> options)
	{
		if (root)
		{
			for (final CategoryModel subc : category.getCategories())
			{
				recursive(categoryData2, subc, false, options);
			}
		}
		else
		{
			final CategoryHierarchyData categoryData = new CategoryHierarchyData();
			categoryData.setId(category.getCode());
			categoryData.setName(category.getName());
			categoryData.setLastModified(category.getModifiedtime());
			categoryData.setUrl(categoryUrlResolver.resolve(category));
			categoryData.setProducts(new ArrayList<ProductData>());
			categoryData.setSubcategories(new ArrayList<CategoryHierarchyData>());

			if (options.contains(CatalogOption.PRODUCTS))
			{
				final List<ProductModel> products = category.getProducts();
				for (final ProductModel product : products)
				{
					final ProductData productData = getProductConverter().convert(product);
					categoryData.getProducts().add(productData);
				}
			}
			categoryData2.getSubcategories().add(categoryData);
			for (final CategoryModel subc : category.getCategories())
			{
				recursive(categoryData, subc, false, options);
			}
		}
	}

	@Required
	public void setProductConverter(final AbstractPopulatingConverter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	@Required
	public void setCategoryUrlResolver(final AbstractUrlResolver<CategoryModel> categoryUrlResolver)
	{
		this.categoryUrlResolver = categoryUrlResolver;
	}

	protected AbstractPopulatingConverter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	public AbstractUrlResolver<CategoryModel> getCategoryUrlResolver()
	{
		return categoryUrlResolver;
	}
}
