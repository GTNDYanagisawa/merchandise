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
package de.hybris.platform.configurablebundleservices.interceptor.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Tests for the product bundle validator {@link ProductBundleValidator}
 */
@UnitTest
public class ProductBundleValidatorTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private InterceptorContext ctx;

	private ProductBundleValidator productValidator;
	private BundleTemplateModel bundletemplate;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		productValidator = new ProductBundleValidator();
		bundletemplate = new BundleTemplateModel();
	}

	@Test
	public void testProductBundleValidatorActsOnlyOnProducts() throws InterceptorException
	{
		productValidator.onValidate(new BundleTemplateModel(), ctx);
	}

	@Test
	public void testProductHasNoTemplates() throws InterceptorException
	{
		final ProductModel product = new ProductModel();
		product.setBundleTemplates(null);

		productValidator.onValidate(product, ctx);
	}

	@Test
	public void testProductIsAssignedToChildTemplate() throws InterceptorException
	{
		final ProductModel product = new ProductModel();
		bundletemplate.setParentTemplate(new BundleTemplateModel());
		product.setBundleTemplates(Arrays.asList(new BundleTemplateModel[]
		{ bundletemplate }));

		productValidator.onValidate(product, ctx);
	}

	@Test
	public void testProductIsAssignedToProductTemplate() throws InterceptorException
	{
		final SubscriptionProductModel plan = new SubscriptionProductModel();
		thrown.expect(InterceptorException.class);
		thrown.expectMessage("is a parent template and cannot have products");

		bundletemplate.setParentTemplate(null);
		plan.setBundleTemplates(Arrays.asList(new BundleTemplateModel[]
		{ bundletemplate }));

		productValidator.onValidate(plan, ctx);
	}
}
