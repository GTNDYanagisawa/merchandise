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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.converters.populator.CatalogVersionPopulator;
import de.hybris.platform.commercefacades.catalog.converters.populator.CategoryHierarchyPopulator;
import de.hybris.platform.commercefacades.catalog.data.CatalogVersionData;

import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static org.fest.assertions.Assertions.assertThat;


@UnitTest
public class CatalogVersionPopulatorTest
{
	private final CatalogVersionPopulator catalogVersionPopulator = new CatalogVersionPopulator();

	@Mock
	private CategoryService mockCategoryService;
	@Mock
	private CategoryHierarchyPopulator mockCategoryHierarchyPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		catalogVersionPopulator.setCategoriesUrl("/categories/");
		catalogVersionPopulator.setCategoryHierarchyPopulator(mockCategoryHierarchyPopulator);
		catalogVersionPopulator.setCategoryService(mockCategoryService);
	}

	@Test
	public void testWithBasicOption()
	{
		final Date lastModifiedDate = new Date();

		final CatalogVersionModel catalogVersionModel = Mockito.mock(CatalogVersionModel.class);
		BDDMockito.when(catalogVersionModel.getVersion()).thenReturn("Online");
		BDDMockito.when(catalogVersionModel.getCategorySystemName()).thenReturn("Online");
		BDDMockito.when(catalogVersionModel.getModifiedtime()).thenReturn(lastModifiedDate);

		final CatalogVersionData catalogVersionData = new CatalogVersionData();
		catalogVersionData.setUrl("/hwcatalog/Online");
		final Collection<CatalogOption> options = Sets.newHashSet(CatalogOption.BASIC);

		catalogVersionPopulator.populate(catalogVersionModel, catalogVersionData, options);

		assertThat(catalogVersionData.getId()).isEqualTo("Online");
		assertThat(catalogVersionData.getName()).isEqualTo("Online");
		assertThat(catalogVersionData.getLastModified()).isEqualTo(lastModifiedDate);
		assertThat(catalogVersionData.getCategoriesHierarchyData()).isEmpty();
		assertThat(catalogVersionData.getUrl()).isEqualTo("/hwcatalog/Online");
	}

	@Test
	public void testWithCategoriesOption()
	{
		final Date lastModifiedDate = new Date();

		final CatalogVersionModel catalogVersionModel = Mockito.mock(CatalogVersionModel.class);
		BDDMockito.when(catalogVersionModel.getVersion()).thenReturn("Online");
		BDDMockito.when(catalogVersionModel.getCategorySystemName()).thenReturn("Online");
		BDDMockito.when(catalogVersionModel.getModifiedtime()).thenReturn(lastModifiedDate);

		final CatalogVersionData catalogVersionData = new CatalogVersionData();
		catalogVersionData.setUrl("/hwcatalog/Online");
		final Collection<CatalogOption> options = Sets.newHashSet(CatalogOption.CATEGORIES);

		final CategoryModel mockRootCategory = Mockito.mock(CategoryModel.class);
		BDDMockito.when(mockRootCategory.getCode()).thenReturn("HW-1000");
		BDDMockito.when(mockCategoryService.getRootCategoriesForCatalogVersion(catalogVersionModel)).thenReturn(
				Lists.newArrayList(mockRootCategory));

		catalogVersionPopulator.populate(catalogVersionModel, catalogVersionData, options);

		assertThat(catalogVersionData.getId()).isEqualTo("Online");
		assertThat(catalogVersionData.getName()).isEqualTo("Online");
		assertThat(catalogVersionData.getLastModified()).isEqualTo(lastModifiedDate);
		assertThat(catalogVersionData.getCategoriesHierarchyData()).hasSize(1);

		assertThat(catalogVersionData.getUrl()).isEqualTo("/hwcatalog/Online");
	}

	@Test
	public void testWithCategoriesAndProductsOptions()
	{
		final Date lastModifiedDate = new Date();

		final CatalogVersionModel catalogVersionModel = Mockito.mock(CatalogVersionModel.class);
		BDDMockito.when(catalogVersionModel.getVersion()).thenReturn("Online");
		BDDMockito.when(catalogVersionModel.getCategorySystemName()).thenReturn("Online");
		BDDMockito.when(catalogVersionModel.getModifiedtime()).thenReturn(lastModifiedDate);

		final CatalogVersionData catalogVersionData = new CatalogVersionData();
		catalogVersionData.setUrl("/hwcatalog/Online");
		final Collection<CatalogOption> options = Sets.newHashSet(CatalogOption.CATEGORIES, CatalogOption.PRODUCTS);

		final CategoryModel mockRootCategory = Mockito.mock(CategoryModel.class);
		BDDMockito.when(mockRootCategory.getCode()).thenReturn("HW-1000");
		BDDMockito.when(mockCategoryService.getRootCategoriesForCatalogVersion(catalogVersionModel)).thenReturn(
				Lists.newArrayList(mockRootCategory));

		catalogVersionPopulator.populate(catalogVersionModel, catalogVersionData, options);

		assertThat(catalogVersionData.getId()).isEqualTo("Online");
		assertThat(catalogVersionData.getName()).isEqualTo("Online");
		assertThat(catalogVersionData.getLastModified()).isEqualTo(lastModifiedDate);
		assertThat(catalogVersionData.getCategoriesHierarchyData()).hasSize(1);
		assertThat(catalogVersionData.getUrl()).isEqualTo("/hwcatalog/Online");
	}
}
