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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Tests for the change product price bundle rule validator {@link AbstractBundleRuleValidator}
 */
@UnitTest
public class AbstractBundleRuleValidatorTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private InterceptorContext ctx;

	private AbstractBundleRuleValidator bundleRuleValidator;
	private DisableProductBundleRuleModel disableProductBundleRule;
	private ChangeProductPriceBundleRuleModel productPriceBundleRule;

	private ProductModel pm;
	private ProductModel unknownProduct;
	private CatalogVersionModel catalogVersion;
	private CatalogModel catalog;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		bundleRuleValidator = new AbstractBundleRuleValidator();
		disableProductBundleRule = new DisableProductBundleRuleModel();
		productPriceBundleRule = new ChangeProductPriceBundleRuleModel();
		pm = mock(ProductModel.class);
		unknownProduct = mock(ProductModel.class);
		catalogVersion = mock(CatalogVersionModel.class);
		catalog = mock(CatalogModel.class);
		given(unknownProduct.getName()).willReturn("UnknownProduct");
		given(catalogVersion.getCatalog()).willReturn(catalog);
		given(unknownProduct.getCatalogVersion()).willReturn(catalogVersion);
	}

	@Test
	public void testHasTargetProducts() throws InterceptorException
	{
		disableProductBundleRule.setName("Rule 1");
		final Collection<ProductModel> cm = new HashSet();
		cm.add(pm);
		disableProductBundleRule.setTargetProducts(cm);

		bundleRuleValidator.onValidate(disableProductBundleRule, ctx);
	}

	@Test
	public void testDisableRuleTargetProductsInComponentWithProducts() throws InterceptorException
	{
		disableProductBundleRule.setBundleTemplate(setUpBasicChildTemplate());
		final List<ProductModel> targetProducts = new ArrayList();
		targetProducts.add(pm);

		// rule and component have the same products
		final BundleTemplateModel bundleTemplate = disableProductBundleRule.getBundleTemplate();
		bundleTemplate.setProducts(targetProducts);
		disableProductBundleRule.setTargetProducts(targetProducts);
		disableProductBundleRule.setConditionalProducts(targetProducts);
		bundleRuleValidator.onValidate(disableProductBundleRule, ctx);

		// rule has a product that is unknown to the component
		targetProducts.add(unknownProduct);
		final List<ProductModel> products = new ArrayList();
		products.add(pm);
		bundleTemplate.setProducts(products);
		thrown.expect(InterceptorException.class);
		thrown.expectMessage("The following target products are not included in the product set of the current component");
		bundleRuleValidator.onValidate(disableProductBundleRule, ctx);
	}

	@Test
	public void testDisableRuleConditionalProductsInComponentWithProducts() throws InterceptorException
	{
		disableProductBundleRule.setBundleTemplate(setUpBasicChildTemplate());
		final List<ProductModel> targetProducts = new ArrayList();
		targetProducts.add(pm);

		// rule and component have the same products
		final BundleTemplateModel bundleTemplate = disableProductBundleRule.getBundleTemplate();
		bundleTemplate.setProducts(targetProducts);
		disableProductBundleRule.setTargetProducts(targetProducts);
		disableProductBundleRule.setConditionalProducts(targetProducts);
		bundleRuleValidator.onValidate(disableProductBundleRule, ctx);

		// rule has a conditional product that is unknown in the component
		final List<ProductModel> products = new ArrayList();
		products.add(pm);
		products.add(unknownProduct);
		disableProductBundleRule.setConditionalProducts(products);
		thrown.expect(InterceptorException.class);
		thrown.expectMessage("The following conditional products are not included in the product sets of the current bundle");
		bundleRuleValidator.onValidate(disableProductBundleRule, ctx);
	}

	@Test
	public void testPriceRuleTargetProductsInComponentWithProducts() throws InterceptorException
	{
		setUpBasicPriceRule();

		productPriceBundleRule.setBundleTemplate(setUpBasicChildTemplate());
		final List<ProductModel> targetProducts = new ArrayList();
		targetProducts.add(pm);

		// rule and component have the same products
		final BundleTemplateModel bundleTemplate = productPriceBundleRule.getBundleTemplate();
		bundleTemplate.setProducts(targetProducts);
		productPriceBundleRule.setTargetProducts(targetProducts);
		productPriceBundleRule.setConditionalProducts(targetProducts);
		bundleRuleValidator.onValidate(productPriceBundleRule, ctx);

		// rule has a product that is unknown to the component
		targetProducts.add(unknownProduct);
		final List<ProductModel> products = new ArrayList();
		products.add(pm);
		bundleTemplate.setProducts(products);
		thrown.expect(InterceptorException.class);
		thrown.expectMessage("The following target products are not included in the product set of the current component");
		bundleRuleValidator.onValidate(productPriceBundleRule, ctx);
	}

	private void setUpBasicPriceRule()
	{
		productPriceBundleRule.setPrice(new BigDecimal("1"));
		productPriceBundleRule.setCurrency(new CurrencyModel());
	}

	private BundleTemplateModel setUpBasicChildTemplate()
	{
		final BundleTemplateModel childTemplate = new BundleTemplateModel();
		final BundleTemplateModel rootTemplate = new BundleTemplateModel();
		final List<BundleTemplateModel> childTemplates = new ArrayList<BundleTemplateModel>();
		childTemplates.add(childTemplate);
		rootTemplate.setChildTemplates(childTemplates);
		childTemplate.setParentTemplate(rootTemplate);
		childTemplate.setProducts(Collections.EMPTY_LIST);

		return childTemplate;
	}


}
