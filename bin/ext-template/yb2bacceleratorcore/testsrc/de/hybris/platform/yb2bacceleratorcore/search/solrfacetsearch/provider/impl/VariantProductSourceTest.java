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
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;


@UnitTest
public class VariantProductSourceTest
{
	private VariantProductSource variantProductSource;

	@Mock
	private VariantProductModel model;
	@Mock
	private IndexedProperty indexedProperty;
	@Mock
	private IndexConfig indexConfig;
	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private ModelService modelService;

	@Mock
	private ProductModel baseProduct;

	@Mock
	private VariantProductModel variant1;
	@Mock
	private VariantProductModel variant2;
	@Mock
	private VariantProductModel variant3;




	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		configure();
	}

	protected void configure()
	{
		variantProductSource = new VariantProductSource();

		given(model.getBaseProduct()).willReturn(baseProduct);
		given(baseProduct.getVariants()).willReturn(Sets.newHashSet(variant3));
		given(model.getVariants()).willReturn(Sets.newHashSet(variant1, variant2));

	}

	@Test
	public void mustGetAllTheProductsInAMultivatirantProduct()
	{

		final Collection<ProductModel> expectedModels = Sets.newHashSet(baseProduct, model, variant1, variant2, variant3);

		final Collection<ProductModel> foundModels = variantProductSource.getProducts(model);

		assertEquals(expectedModels, foundModels);
	}





}
