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
package de.hybris.platform.solrfacetsearch.provider.impl;


import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ItemIdentityProviderTest
{

	private static final String PRODUCT_ITEM_TYPE = "ProductModel";
	private final String PRODUCT_CATALOG_ID = "product_catalog_id";
	private final String PRODUCT_VERSION = "product_version";
	private final String PRODUCT_KEY = "product_key";
	private static final String DEFAULT_CATALOG_VERSION_CONTAINER_ATTRIBUTE = "catalogVersion";
	private static final String PRODUCT_KEY_ATTRIBUTE = "code";
	private static final Set<String> PRODUCT_CATALOG_VERSION_UNIQUE_KEY_ATTRIBUTE_SET = new HashSet<String>(
			Arrays.asList(PRODUCT_KEY_ATTRIBUTE));
	private static final String CATEGORY_ITEM_KEY_ATTRIBUTE = "uid";
	private static final Set<String> CATEGORY_ITEM_CATALOG_VERSION_UNIQUE_KEY_ATTRIBUTE_SET = new HashSet<String>(
			Arrays.asList(CATEGORY_ITEM_KEY_ATTRIBUTE));

	private static final String CATEGORY_ITEM_TYPE = "CategoryModel";
	private final String CATEGORY_ITEM_CATALOG_ID = "category_catalog_id";
	private final String CATEGORY_ITEM_VERSION = "category_version";
	private final String CATEGORY_ITEM_KEY = "category_key";

	private static final String USER_ITEM_TYPE = "UserModel";
	private final long ITEM_PK = 12345L;

	private ItemIdentityProvider itemIdentityProvider;

	@Mock
	private ModelService modelService;
	@Mock
	private CatalogTypeService catalogTypeService;


	@Before
	public void setUp() throws Exception
	{
		itemIdentityProvider = new ItemIdentityProvider();
		prepareMockObjects();
	}

	@Test
	public void getIdentifierForProductModelTest()
	{
		final CatalogVersionModel productCatalogVersion = mock(CatalogVersionModel.class);
		final CatalogModel productCatalog = mock(CatalogModel.class);
		final ProductModel product = mock(ProductModel.class);

		Mockito.when(product.getCode()).thenReturn(PRODUCT_KEY);
		Mockito.when(product.getItemtype()).thenReturn(PRODUCT_ITEM_TYPE);
		Mockito.when(product.getCatalogVersion()).thenReturn(productCatalogVersion);

		Mockito.when(productCatalogVersion.getVersion()).thenReturn(PRODUCT_VERSION);
		Mockito.when(productCatalogVersion.getCatalog()).thenReturn(productCatalog);
		Mockito.when(productCatalog.getId()).thenReturn(PRODUCT_CATALOG_ID);

		Mockito.when(modelService.getAttributeValue(product, DEFAULT_CATALOG_VERSION_CONTAINER_ATTRIBUTE)).thenReturn(
				productCatalogVersion);//product.getCatalogVersion());
		Mockito.when(modelService.getAttributeValue(product, PRODUCT_KEY_ATTRIBUTE)).thenReturn(PRODUCT_KEY);

		final String productIdentifier = itemIdentityProvider.getIdentifier(null, product);
		Assertions.assertThat(productIdentifier).isEqualTo(PRODUCT_CATALOG_ID + "/" + PRODUCT_VERSION + "/" + PRODUCT_KEY);
	}

	@Test
	public void getIdentifierForNonProductModelAndCatalogAwareClassTest()
	{
		final CatalogVersionModel categoryItemCatalogVersion = mock(CatalogVersionModel.class);
		final CatalogModel categoryItemCatalog = mock(CatalogModel.class);
		final CategoryModel categoryModel = mock(CategoryModel.class);

		Mockito.when(categoryModel.getName()).thenReturn(CATEGORY_ITEM_KEY);
		Mockito.when(categoryModel.getItemtype()).thenReturn(CATEGORY_ITEM_TYPE);
		Mockito.when(categoryModel.getCatalogVersion()).thenReturn(categoryItemCatalogVersion);

		Mockito.when(categoryItemCatalogVersion.getVersion()).thenReturn(CATEGORY_ITEM_VERSION);
		Mockito.when(categoryItemCatalogVersion.getCatalog()).thenReturn(categoryItemCatalog);
		Mockito.when(categoryItemCatalog.getId()).thenReturn(CATEGORY_ITEM_CATALOG_ID);

		Mockito.when(modelService.getAttributeValue(categoryModel, DEFAULT_CATALOG_VERSION_CONTAINER_ATTRIBUTE)).thenReturn(
				categoryItemCatalogVersion);
		Mockito.when(modelService.getAttributeValue(categoryModel, CATEGORY_ITEM_KEY_ATTRIBUTE)).thenReturn(CATEGORY_ITEM_KEY);
		Mockito.when(Boolean.valueOf(catalogTypeService.isCatalogVersionAwareModel(categoryModel))).thenReturn(Boolean.TRUE);

		final String productIdentifier = itemIdentityProvider.getIdentifier(null, categoryModel);
		Assertions.assertThat(productIdentifier).isEqualTo(
				CATEGORY_ITEM_CATALOG_ID + "/" + CATEGORY_ITEM_VERSION + "/" + CATEGORY_ITEM_KEY);
	}

	@Test
	public void getIdentifierForNonProductModelNonCatalogAwareClassTest()
	{
		final UserModel user = mock(UserModel.class);
		final PK pk = PK.fromLong(ITEM_PK);//cannot mock final class

		Mockito.when(user.getItemtype()).thenReturn(USER_ITEM_TYPE);
		Mockito.when(user.getPk()).thenReturn(pk);

		final String productIdentifier = itemIdentityProvider.getIdentifier(null, user);
		Assertions.assertThat(productIdentifier).isEqualTo(ITEM_PK + "");
	}

	private void prepareMockObjects()
	{
		MockitoAnnotations.initMocks(this);

		final ComposedTypeModel productComposedTypeModel = mock(ComposedTypeModel.class);
		final ComposedTypeModel cmsItemComposedTypeModel = mock(ComposedTypeModel.class);

		Mockito.when(productComposedTypeModel.getItemtype()).thenReturn(PRODUCT_ITEM_TYPE);
		Mockito.when(cmsItemComposedTypeModel.getItemtype()).thenReturn(CATEGORY_ITEM_TYPE);

		itemIdentityProvider.setCatalogTypeService(catalogTypeService);
		itemIdentityProvider.setModelService(modelService);

		Mockito.when(catalogTypeService.getCatalogVersionContainerAttribute(PRODUCT_ITEM_TYPE)).thenReturn(
				DEFAULT_CATALOG_VERSION_CONTAINER_ATTRIBUTE);
		Mockito.when(catalogTypeService.getCatalogVersionUniqueKeyAttribute(PRODUCT_ITEM_TYPE)).thenReturn(
				PRODUCT_CATALOG_VERSION_UNIQUE_KEY_ATTRIBUTE_SET);

		Mockito.when(catalogTypeService.getCatalogVersionContainerAttribute(CATEGORY_ITEM_TYPE)).thenReturn(
				DEFAULT_CATALOG_VERSION_CONTAINER_ATTRIBUTE);
		Mockito.when(catalogTypeService.getCatalogVersionUniqueKeyAttribute(CATEGORY_ITEM_TYPE)).thenReturn(
				CATEGORY_ITEM_CATALOG_VERSION_UNIQUE_KEY_ATTRIBUTE_SET);
	}

}
