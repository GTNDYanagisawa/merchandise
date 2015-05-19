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
package de.hybris.platform.configurablebundleservices.bundle.impl;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test suite for {@link BundleTemplateService}
 */
@UnitTest
public class DefaultBundleTemplateServiceTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DefaultBundleTemplateService bundleTemplateService;
	private BundleTemplateModel bundleTemplateModelRoot;
	private BundleTemplateModel bundleTemplateModelChild1;
	private BundleTemplateModel bundleTemplateModelChild2;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		bundleTemplateService = new DefaultBundleTemplateService();

		bundleTemplateModelRoot = mock(BundleTemplateModel.class);
		bundleTemplateModelChild1 = mock(BundleTemplateModel.class);
		bundleTemplateModelChild2 = mock(BundleTemplateModel.class);

		given(bundleTemplateModelRoot.getParentTemplate()).willReturn(null);
		given(bundleTemplateModelChild1.getParentTemplate()).willReturn(bundleTemplateModelRoot);
		given(bundleTemplateModelChild2.getParentTemplate()).willReturn(bundleTemplateModelChild1);
	}

	@Test
	public void testGetRootBundleTemplate()
	{
		BundleTemplateModel bundleTemplate = bundleTemplateService.getRootBundleTemplate(bundleTemplateModelRoot);
		Assert.assertEquals(bundleTemplateModelRoot, bundleTemplate);

		bundleTemplate = bundleTemplateService.getRootBundleTemplate(bundleTemplateModelChild1);
		Assert.assertEquals(bundleTemplateModelRoot, bundleTemplate);

		bundleTemplate = bundleTemplateService.getRootBundleTemplate(bundleTemplateModelChild2);
		Assert.assertEquals(bundleTemplateModelRoot, bundleTemplate);
	}

	@Test
	public void testGetBundleTemplatesByProductWhenNoProduct()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("productModel can not be null");

		bundleTemplateService.getBundleTemplatesByProduct(null);
	}

	@Test
	public void testGetSubsequentBundleTemplateWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		bundleTemplateService.getSubsequentBundleTemplate(null);
	}

	@Test
	public void testGetPreviousBundleTemplateWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		bundleTemplateService.getPreviousBundleTemplate(null);
	}

	@Test
	public void testGetRelativeBundleTemplateWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		bundleTemplateService.getRelativeBundleTemplate(null, 0);
	}

	@Test
	public void testGetRootBundleTemplateWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Bundle template cannot be null");

		bundleTemplateService.getRootBundleTemplate(null);
	}

	@Test
	public void testGetTemplatesForMasterOrderAndBundleNoWhenNoOrder()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterAbstractOrder can not be null");

		bundleTemplateService.getTemplatesForMasterOrderAndBundleNo(null, 1);
	}

	@Test
	public void testContainsComponenentProductsOfTypeWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		bundleTemplateService.containsComponenentProductsOfType(null, SubscriptionProductModel.class);
	}

	@Test
	public void testContainsComponenentProductsOfTypeWhenNoClazz()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("clazz can not be null");

		bundleTemplateService.containsComponenentProductsOfType(new BundleTemplateModel(), (Class<? extends ProductModel>[]) null);
	}

	@Test
	public void testContainsComponenentProductsOfType()
	{
		// no products assigned to template
		final BundleTemplateModel bundleTemplate = mock(BundleTemplateModel.class);
		boolean containsPlans = bundleTemplateService.containsComponenentProductsOfType(bundleTemplate,
				SubscriptionProductModel.class);
		assertFalse(containsPlans);

		// products assigned to template
		final ProductModel product = new ProductModel();
		final List<ProductModel> products = new ArrayList<ProductModel>();
		products.add(product);
		given(bundleTemplate.getProducts()).willReturn(products);
		containsPlans = bundleTemplateService.containsComponenentProductsOfType(bundleTemplate, SubscriptionProductModel.class);
		assertFalse(containsPlans);

		// plans assigned to template
		products.clear();
		final SubscriptionProductModel planProduct = new SubscriptionProductModel();
		products.add(planProduct);
		containsPlans = bundleTemplateService.containsComponenentProductsOfType(bundleTemplate, SubscriptionProductModel.class);
		assertTrue(containsPlans);
	}

	@Test
	public void testGetFirstComponentOfTypeWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		bundleTemplateService.getAllComponentsOfType(null, getClazz());
	}

	@Test
	public void testGetFirstComponentOfTypeWhenNoClazz()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("clazzes can not be null");

		bundleTemplateService.getAllComponentsOfType(new BundleTemplateModel(), (Class<? extends ProductModel>[]) null);
	}

	@Test
	public void testGetFirstComponentOfType()
	{
		final BundleTemplateModel parentTemplate = mock(BundleTemplateModel.class);

		// no child templates
		assertEquals(0, bundleTemplateService.getAllComponentsOfType(parentTemplate, getClazz()).size());

		// 1 child template that has products of type ProductModel
		final BundleTemplateModel productTemplate = mock(BundleTemplateModel.class);
		final List<BundleTemplateModel> childTemplates = new ArrayList<BundleTemplateModel>();
		childTemplates.add(productTemplate);
		given(parentTemplate.getChildTemplates()).willReturn(childTemplates);
		final ProductModel product = new ProductModel();
		final List<ProductModel> products = new ArrayList<ProductModel>();
		products.add(product);
		given(productTemplate.getProducts()).willReturn(products);

		assertEquals(0, bundleTemplateService.getAllComponentsOfType(parentTemplate, getClazz()).size());

		// 1st child template: products of type ProductModel
		// 2nd child template: products of type SubscriptionProductModel <- we want this
		final BundleTemplateModel planTemplate = mock(BundleTemplateModel.class);
		childTemplates.add(planTemplate);
		final SubscriptionProductModel planProduct = new SubscriptionProductModel();
		final List<ProductModel> planProducts = new ArrayList<ProductModel>();
		planProducts.add(planProduct);
		given(planTemplate.getProducts()).willReturn(planProducts);

		BundleTemplateModel bundleTemplate = bundleTemplateService.getAllComponentsOfType(parentTemplate, getClazz()).iterator()
				.next();
		assertEquals(planTemplate, bundleTemplate);

		// 1st child template: products of type ProductModel
		// 2nd child template: products of type SubscriptionProductModel <- we want this
		// 3rd child template: products of type SubscriptionProductModel
		final BundleTemplateModel tvTemplate = mock(BundleTemplateModel.class);
		childTemplates.add(tvTemplate);
		given(tvTemplate.getProducts()).willReturn(planProducts);

		bundleTemplate = bundleTemplateService.getAllComponentsOfType(parentTemplate, getClazz()).iterator().next();
		assertEquals(planTemplate, bundleTemplate);
	}

	protected Class getClazz()
	{
		return SubscriptionProductModel.class;
	}

}