/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.configurablebundlecockpits.servicelayer.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Tests for default implementation of BundleNavigationService
 */
@UnitTest
public class DefaultBundleNavigationServiceTest
{

	private final DefaultBundleNavigationService bundleNavigationService = new DefaultBundleNavigationService();

	@Mock
	private BundleTemplateService bundleTemplateService;

	private BundleTemplateModel bundleTemplate;

	@Mock
	private ModelService modelService;
	private List<ProductModel> products;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		bundleNavigationService.setBundleTemplateService(bundleTemplateService);
		bundleNavigationService.setModelService(modelService);
		bundleTemplate = new BundleTemplateModel();
		bundleTemplate.setProducts(createInitProducts());
	}

	private List<ProductModel> createInitProducts()
	{
		products = new ArrayList<ProductModel>();
		final ProductModel product1 = new ProductModel();
		product1.setCode("product1");
		final ProductModel product2 = new ProductModel();
		product2.setCode("product2");

		products.add(product1);
		products.add(product2);
		return products;
	}

	@Test
	public void testAddProductsToEnd()
	{
		final int initSize = bundleTemplate.getProducts().size();
		final List<ProductModel> products = new ArrayList<ProductModel>();
		final ProductModel product0 = new ProductModel();
		product0.setCode("product0");
		products.add(product0);
		bundleNavigationService.add(bundleTemplate, products);
		Assert.assertEquals(initSize + products.size(), bundleTemplate.getProducts().size());

		Assert.assertEquals(product0.getCode(), bundleTemplate.getProducts().get(bundleTemplate.getProducts().size() - 1).getCode());

	}

	@Test
	public void testAddProductToEnd()
	{
		final int initSize = bundleTemplate.getProducts().size();
		final ProductModel product0 = new ProductModel();
		product0.setCode("product0");

		bundleNavigationService.add(bundleTemplate, product0);
		Assert.assertEquals(initSize + 1, bundleTemplate.getProducts().size());

		Assert.assertEquals(product0.getCode(), bundleTemplate.getProducts().get(bundleTemplate.getProducts().size() - 1).getCode());

	}

	@Test
	public void testRemoveProduct()
	{
		final int initSize = products.size();
		bundleNavigationService.remove(bundleTemplate, products.get(0));


		Assert.assertEquals(initSize - 1, bundleTemplate.getProducts().size());

		Assert.assertEquals(products.get(1).getCode(), bundleTemplate.getProducts().get(bundleTemplate.getProducts().size() - 1)
				.getCode());

	}

	@Test
	@Ignore
	public void testMoveProductWithinBundle()
	{
		final ProductModel source = products.get(0);
		final ProductModel target = products.get(1);

		bundleNavigationService.move(bundleTemplate, source, target);

		Assert.assertEquals(source.getCode(), bundleTemplate.getProducts().get(1).getCode());


	}

	@Test
	public void testMoveProductAcrossBundles()
	{
		final ProductModel product = bundleTemplate.getProducts().get(0);

		final BundleTemplateModel targetBundleTemplate = new BundleTemplateModel();
		targetBundleTemplate.setProducts(new ArrayList<ProductModel>());
		bundleNavigationService.move(bundleTemplate, product, targetBundleTemplate);
		Assert.assertEquals(1, bundleTemplate.getProducts().size());
		Assert.assertEquals(1, targetBundleTemplate.getProducts().size());

	}


}
