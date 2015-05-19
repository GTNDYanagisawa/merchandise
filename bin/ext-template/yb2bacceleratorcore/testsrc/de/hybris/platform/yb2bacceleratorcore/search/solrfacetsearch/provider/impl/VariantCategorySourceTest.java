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


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.GenericVariantProductModel;
import de.hybris.platform.b2b.model.VariantValueCategoryModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider.ProductSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VariantCategorySourceTest
{



	private static final Integer SEQUENCE_FIRST = Integer.valueOf(1);
	private static final Integer SEQUENCE_SECOND = Integer.valueOf(2);
	private VariantCategorySource categorySource;
	private static final String SUPER_CATEGORY_NAME = "categoryName";

	@Mock
	private ProductSource productSource;

	@Mock
	private GenericVariantProductModel genericVariant;
	@Mock
	private ProductModel baseProduct;

	@Mock
	private IndexedProperty indexedProperty;
	@Mock
	private IndexConfig indexConfig;

	@Mock
	private CategoryModel facetParent;
	@Mock
	private CategoryModel nonFacetParent;
	@Mock
	private VariantValueCategoryModel childFacetValue1;
	@Mock
	private VariantValueCategoryModel childFacetValue2;
	@Mock
	private VariantValueCategoryModel notChildFacetValue1;

	@Before
	public void setUp() throws Exception
	{
		when(productSource.getProducts(genericVariant)).thenReturn(Sets.newHashSet(genericVariant, baseProduct));

		// the variant has 3 variant value categories but the parent of one of them is not the one to be faceted
		when(genericVariant.getSupercategories()).thenReturn(
				Sets.newHashSet((CategoryModel) childFacetValue1, childFacetValue2, notChildFacetValue1));

		when(facetParent.getName()).thenReturn(SUPER_CATEGORY_NAME);
		when(indexedProperty.getName()).thenReturn(SUPER_CATEGORY_NAME);
		final ArrayList<CategoryModel> superCategories = new ArrayList<CategoryModel>();
		superCategories.add(facetParent);

		// Different sequences same parent
		when(childFacetValue1.getSupercategories()).thenReturn(superCategories);
		when(childFacetValue1.getSequence()).thenReturn(SEQUENCE_FIRST);
		when(childFacetValue2.getSupercategories()).thenReturn(superCategories);
		when(childFacetValue2.getSequence()).thenReturn(SEQUENCE_SECOND);

		//non faceted child
		when(notChildFacetValue1.getSupercategories()).thenReturn(Collections.singletonList(nonFacetParent));
		when(notChildFacetValue1.getSequence()).thenReturn(SEQUENCE_FIRST);


		categorySource = new VariantCategorySource();
		categorySource.setProductSource(productSource);
	}

	@Test
	public void mustGetAllRightVariantValueCategoriesForProduct()
	{
		final Collection<CategoryModel> expectedCategories = Sets.newHashSet((CategoryModel) childFacetValue1, childFacetValue2);

		final Collection<CategoryModel> foundCategories = categorySource.getCategoriesForConfigAndProperty(indexConfig,
				indexedProperty, genericVariant);

		assertEquals(expectedCategories, foundCategories);
	}
}
