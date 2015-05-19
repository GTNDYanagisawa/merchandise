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
package de.hybris.platform.commercefacades.catalog.converters.populators;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.catalog.converters.populator.CategoryHierarchyPopulator;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import de.hybris.platform.commercefacades.product.converters.populator.ProductBasicPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.VariantSelectedPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;

import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


@UnitTest
public class CategoryHierarchyPopulatorTest
{
	private final CategoryHierarchyPopulator categoryHierarchyPopulator = new CategoryHierarchyPopulator();

	@Mock
	private ProductModel productModel; //NOPMD

	@Mock
	private ProductBasicPopulator productBasicPopulator; //NOPMD
	@Mock
	private VariantSelectedPopulator variantSelectedPopulator; //NOPMD
	@Mock
	private ProductBasicPopulator productPrimaryImagePopulator; //NOPMD
	@InjectMocks
	private final ProductPopulator productPopulator = new ProductPopulator();
	private final AbstractPopulatingConverter<ProductModel, ProductData> productConverter = new ConverterFactory<ProductModel, ProductData, ProductPopulator>()
			.create(ProductData.class, productPopulator);
	@Mock
	private ProductService productService;
	@Mock
	private AbstractUrlResolver<CategoryModel> categoryUrlResolver;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		categoryHierarchyPopulator.setProductConverter(productConverter);
		categoryHierarchyPopulator.setProductService(productService);
		categoryHierarchyPopulator.setCategoryUrlResolver(categoryUrlResolver);
	}

	@Test
	public void testWithBasicOption()
	{
		final Date lastModifiedDate = new Date();
		final CategoryHierarchyData categoryHierarchyData = new CategoryHierarchyData();
		final Collection<? extends CatalogOption> options = Sets.newHashSet(CatalogOption.BASIC);
		final CategoryModel categoryModel = Mockito.mock(CategoryModel.class);
		BDDMockito.when(categoryModel.getCode()).thenReturn("HW-1000");
		BDDMockito.when(categoryModel.getName()).thenReturn("Monitors");
		BDDMockito.when(categoryModel.getModifiedtime()).thenReturn(lastModifiedDate);


		categoryHierarchyPopulator.populate(categoryModel, categoryHierarchyData, options, PageOption.createWithoutLimits());

		assertThat(categoryHierarchyData.getId()).isEqualTo("HW-1000");
		assertThat(categoryHierarchyData.getName()).isEqualTo("Monitors");
		assertThat(categoryHierarchyData.getLastModified()).isEqualTo(lastModifiedDate);
		assertThat(categoryHierarchyData.getProducts()).isNullOrEmpty();
		assertThat(categoryHierarchyData.getSubcategories()).isNullOrEmpty();
	}

	@Test
	public void testWithProductsOption()
	{
		final Date lastModifiedDate = new Date();
		final CategoryHierarchyData categoryHierarchyData = new CategoryHierarchyData();
		categoryHierarchyData.setUrl("/hwcatalog/Online/categories");
		final Collection<? extends CatalogOption> options = Sets.newHashSet(CatalogOption.PRODUCTS);
		final CategoryModel categoryModel = Mockito.mock(CategoryModel.class);
		BDDMockito.when(categoryModel.getCode()).thenReturn("HW-1000");
		BDDMockito.when(categoryModel.getName()).thenReturn("Monitors");
		BDDMockito.when(categoryModel.getModifiedtime()).thenReturn(lastModifiedDate);
		final ProductModel mockProduct = Mockito.mock(ProductModel.class);

		BDDMockito.when(categoryModel.getProducts()).thenReturn(Lists.newArrayList(mockProduct));
		BDDMockito.when(productService.getProductsForCategory(categoryModel, 0, Integer.MAX_VALUE)).thenReturn(
				Lists.newArrayList(mockProduct));

		final CategoryModel mockSubcategory = Mockito.mock(CategoryModel.class);
		final CategoryModel mockSubcategoryOfSubcategory = Mockito.mock(CategoryModel.class);

		BDDMockito.when(mockSubcategory.getCategories()).thenReturn(Lists.newArrayList(mockSubcategoryOfSubcategory));
		BDDMockito.when(mockSubcategoryOfSubcategory.getProducts()).thenReturn(Lists.newArrayList(mockProduct));
		BDDMockito.when(categoryModel.getCategories()).thenReturn(Lists.newArrayList(mockSubcategory));
		BDDMockito.when(categoryUrlResolver.resolve(categoryModel)).thenReturn("/hwcatalog/Online/categories/HW-1000");

		categoryHierarchyPopulator.populate(categoryModel, categoryHierarchyData, options, PageOption.createWithoutLimits());

		assertThat(categoryHierarchyData.getId()).isEqualTo("HW-1000");
		assertThat(categoryHierarchyData.getName()).isEqualTo("Monitors");
		assertThat(categoryHierarchyData.getLastModified()).isEqualTo(lastModifiedDate);
		assertThat(categoryHierarchyData.getProducts()).hasSize(1);
		assertThat(categoryHierarchyData.getSubcategories()).isNullOrEmpty();
		assertThat(categoryHierarchyData.getUrl()).isEqualTo("/hwcatalog/Online/categories/HW-1000");

	}

	@Test
	public void testWithProductsAndSubcategoriesOptions()
	{
		final Date lastModifiedDate = new Date();
		final CategoryHierarchyData categoryHierarchyData = new CategoryHierarchyData();
		categoryHierarchyData.setUrl("/hwcatalog/Online/categories");
		final Collection<? extends CatalogOption> options = Sets.newHashSet(CatalogOption.PRODUCTS, CatalogOption.SUBCATEGORIES);
		final CategoryModel categoryModel = Mockito.mock(CategoryModel.class);
		BDDMockito.when(categoryModel.getCode()).thenReturn("HW-1000");
		BDDMockito.when(categoryModel.getName()).thenReturn("Monitors");
		BDDMockito.when(categoryModel.getModifiedtime()).thenReturn(lastModifiedDate);
		final ProductModel mockProduct = Mockito.mock(ProductModel.class);

		BDDMockito.when(categoryModel.getProducts()).thenReturn(Lists.newArrayList(mockProduct));
		BDDMockito.when(productService.getProductsForCategory(categoryModel, 0, Integer.MAX_VALUE)).thenReturn(
				Lists.newArrayList(mockProduct));

		final CategoryModel mockSubcategory = Mockito.mock(CategoryModel.class);
		final CategoryModel mockSubcategoryOfSubcategory = Mockito.mock(CategoryModel.class);

		BDDMockito.when(mockSubcategory.getCategories()).thenReturn(Lists.newArrayList(mockSubcategoryOfSubcategory));
		BDDMockito.when(mockSubcategoryOfSubcategory.getProducts()).thenReturn(Lists.newArrayList(mockProduct));
		BDDMockito.when(categoryModel.getCategories()).thenReturn(Lists.newArrayList(mockSubcategory));
		BDDMockito.when(categoryUrlResolver.resolve(categoryModel)).thenReturn("/hwcatalog/Online/categories/HW-1000");


		categoryHierarchyPopulator.populate(categoryModel, categoryHierarchyData, options, PageOption.createWithoutLimits());

		assertThat(categoryHierarchyData.getId()).isEqualTo("HW-1000");
		assertThat(categoryHierarchyData.getName()).isEqualTo("Monitors");
		assertThat(categoryHierarchyData.getLastModified()).isEqualTo(lastModifiedDate);
		assertThat(categoryHierarchyData.getProducts()).hasSize(1);
		assertThat(categoryHierarchyData.getSubcategories()).hasSize(1);
		assertThat(categoryHierarchyData.getSubcategories().iterator().next().getSubcategories()).hasSize(1);
		assertThat(categoryHierarchyData.getUrl()).isEqualTo("/hwcatalog/Online/categories/HW-1000");

	}
}
