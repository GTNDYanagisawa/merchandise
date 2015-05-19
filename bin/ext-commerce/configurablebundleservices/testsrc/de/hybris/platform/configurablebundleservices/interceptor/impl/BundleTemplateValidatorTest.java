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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Tests for the bundle template validator {@link BundleTemplateValidator}
 */
@UnitTest
public class BundleTemplateValidatorTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private InterceptorContext ctx;

	private BundleTemplateValidator vlad;
	private BundleTemplateModel bundletemplate;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		vlad = new BundleTemplateValidator();
		bundletemplate = new BundleTemplateModel();
	}

	@Test
	public void testBundleTemplateValidateActsOnlyOnBundleTemplates() throws InterceptorException
	{
		vlad.onValidate(new SubscriptionProductModel(), ctx);
	}

	@Test
	public void testParentTemplateCannotHaveProducts() throws InterceptorException
	{
		bundletemplate.setChildTemplates(Arrays.asList(new BundleTemplateModel[]
		{ new BundleTemplateModel() }));
		bundletemplate.setProducts(Arrays.asList(new ProductModel[]
		{ new ProductModel() }));

		thrown.expect(InterceptorException.class);
		thrown.expectMessage("not possible for a Bundle Template to contain both Products and Child Bundle Templates");

		vlad.onValidate(bundletemplate, ctx);

	}

	@Test
	public void testChildTemplatesCanHaveProducts() throws InterceptorException
	{
		bundletemplate.setChildTemplates(Arrays.asList(new BundleTemplateModel[] {}));
		bundletemplate.setProducts(Arrays.asList(new ProductModel[]
		{ new ProductModel() }));

		vlad.onValidate(bundletemplate, ctx);
	}

	@Test
	public void testParentTemplatesCannotHaveProducts() throws InterceptorException
	{
		bundletemplate.setChildTemplates(Arrays.asList(new BundleTemplateModel[]
		{ new BundleTemplateModel() }));
		bundletemplate.setProducts(Arrays.asList(new ProductModel[] {}));
		vlad.onValidate(bundletemplate, ctx);

	}

	@Test
	public void testParentTemplatCannotHaveSelectionCriteria() throws InterceptorException
	{
		bundletemplate.setParentTemplate(null);
		bundletemplate.setBundleSelectionCriteria(new BundleSelectionCriteriaModel());

		thrown.expect(InterceptorException.class);
		thrown.expectMessage("cannot have selection criteria");

		vlad.onValidate(bundletemplate, ctx);
	}

	@Test
	public void testChildTemplateCanHaveSelectionCriteria() throws InterceptorException
	{
		bundletemplate.setParentTemplate(new BundleTemplateModel());
		bundletemplate.setBundleSelectionCriteria(new BundleSelectionCriteriaModel());
		vlad.onValidate(bundletemplate, ctx);
	}

	@Test
	public void testParentTemplateWithNoSelectionCriteriaIsOK() throws InterceptorException
	{
		bundletemplate.setParentTemplate(null);
		bundletemplate.setBundleSelectionCriteria(null);

		vlad.onValidate(bundletemplate, ctx);

	}

	@Test
	public void testChildTemplateWithNoSelectionCriteriaIsOK() throws InterceptorException
	{
		bundletemplate.setParentTemplate(new BundleTemplateModel());
		bundletemplate.setBundleSelectionCriteria(null);
		vlad.onValidate(bundletemplate, ctx);
	}

	@Test
	public void testParentTemplateWithNoSelectionDependencyIsOK() throws InterceptorException
	{
		bundletemplate.setParentTemplate(null);
		vlad.onValidate(bundletemplate, ctx);
	}

	@Test
	public void testParentTemplateCannotHaveRequiredTemplates() throws InterceptorException
	{
		bundletemplate.setParentTemplate(null);

		final Collection<BundleTemplateModel> requiredTemplates = new HashSet<BundleTemplateModel>();
		requiredTemplates.add(new BundleTemplateModel());
		bundletemplate.setRequiredBundleTemplates(requiredTemplates);

		thrown.expect(InterceptorException.class);
		thrown.expectMessage("nor required components");

		vlad.onValidate(bundletemplate, ctx);

	}

	@Test
	public void testParentTemplateCannotHaveDependentTemplates() throws InterceptorException
	{
		bundletemplate.setParentTemplate(null);

		final Collection<BundleTemplateModel> dependentTemplates = new HashSet<BundleTemplateModel>();
		dependentTemplates.add(new BundleTemplateModel());
		bundletemplate.setDependentBundleTemplates(dependentTemplates);

		thrown.expect(InterceptorException.class);
		thrown.expectMessage("cannot have dependent");

		vlad.onValidate(bundletemplate, ctx);

	}

	@Test
	public void testParentTemplateHasNoBundleRules() throws InterceptorException
	{
		bundletemplate.setParentTemplate(null);
		bundletemplate.setDisableProductBundleRules(new ArrayList<DisableProductBundleRuleModel>());
		bundletemplate.setChangeProductPriceBundleRules(new ArrayList<ChangeProductPriceBundleRuleModel>());
		vlad.onValidate(bundletemplate, ctx);
	}

	@Test
	public void testParentTemplateCannotHaveDisablePriceRule() throws InterceptorException
	{
		bundletemplate.setParentTemplate(null);
		final List<DisableProductBundleRuleModel> disableRules = new ArrayList<DisableProductBundleRuleModel>();
		disableRules.add(new DisableProductBundleRuleModel());
		bundletemplate.setDisableProductBundleRules(disableRules);

		thrown.expect(InterceptorException.class);
		thrown.expectMessage("cannot have a disable rule");

		vlad.onValidate(bundletemplate, ctx);
	}

	@Test
	public void testParentTemplateCannotHaveChangePriceRule() throws InterceptorException
	{
		bundletemplate.setParentTemplate(null);
		final List<ChangeProductPriceBundleRuleModel> changeRules = new ArrayList<ChangeProductPriceBundleRuleModel>();
		changeRules.add(new ChangeProductPriceBundleRuleModel());
		bundletemplate.setChangeProductPriceBundleRules(changeRules);

		thrown.expect(InterceptorException.class);
		thrown.expectMessage("cannot have a disable rule or a change price rule");

		vlad.onValidate(bundletemplate, ctx);
	}

	@Test
	public void testRemoveProductThatIsInAssignedPriceRule() throws InterceptorException
	{
		bundletemplate.setParentTemplate(new BundleTemplateModel());
		bundletemplate.setBundleSelectionCriteria(null);
		given(Boolean.valueOf(ctx.isModified(bundletemplate, BundleTemplateModel.PRODUCTS))).willReturn(Boolean.TRUE);

		// no price rules assigned
		vlad.onValidate(bundletemplate, ctx);

		// assign a price rule which has no target products
		final ChangeProductPriceBundleRuleModel priceRule = new ChangeProductPriceBundleRuleModel();
		final List<ChangeProductPriceBundleRuleModel> priceRules = new ArrayList<ChangeProductPriceBundleRuleModel>();
		priceRules.add(priceRule);
		bundletemplate.setChangeProductPriceBundleRules(priceRules);
		vlad.onValidate(bundletemplate, ctx);

		// assign product1 to the template
		final ProductModel product1 = new ProductModel();
		final List<ProductModel> products = new ArrayList<ProductModel>();
		products.add(product1);
		bundletemplate.setProducts(products);
		vlad.onValidate(bundletemplate, ctx);

		// assign product1 to the price rule
		final List<ProductModel> targetProducts = new ArrayList<ProductModel>();
		targetProducts.add(product1);
		priceRule.setTargetProducts(targetProducts);
		vlad.onValidate(bundletemplate, ctx);

		// remove product1 from template -> exception
		products.clear();
		thrown.expect(InterceptorException.class);
		thrown.expectMessage("Product cannot be removed from this component as it is still in the list of target products of a price rule that is assigned to this component");
		vlad.onValidate(bundletemplate, ctx);
	}

	@Test
	public void testOnly2TemplateLevelsAllowed1() throws InterceptorException
	{
		// simple parent template
		vlad.onValidate(bundletemplate, ctx);

		// add child template
		final List<BundleTemplateModel> childTemplates = new ArrayList<BundleTemplateModel>();
		childTemplates.add(new BundleTemplateModel());
		bundletemplate.setChildTemplates(childTemplates);
		vlad.onValidate(bundletemplate, ctx);

		// convert parent template into a child template
		bundletemplate.setParentTemplate(new BundleTemplateModel());
		thrown.expect(InterceptorException.class);
		thrown.expectMessage("is a child template (parent template is not empty!) and cannot have child templates on its own");
		vlad.onValidate(bundletemplate, ctx);
	}

	@Test
	public void testOnly2TemplateLevelsAllowed2() throws InterceptorException
	{
		// simple parent template
		bundletemplate.setId("parentTemplate");
		vlad.onValidate(bundletemplate, ctx);

		// add child template
		final List<BundleTemplateModel> childTemplates = new ArrayList<BundleTemplateModel>();
		final BundleTemplateModel childTemplate = new BundleTemplateModel();
		childTemplate.setId("childTemplate");
		childTemplate.setParentTemplate(bundletemplate);
		childTemplates.add(childTemplate);
		bundletemplate.setChildTemplates(childTemplates);
		vlad.onValidate(bundletemplate, ctx);

		// create new template and add it as a child to the child template: check the root template
		final BundleTemplateModel childChildTemplate = new BundleTemplateModel();
		childChildTemplate.setId("childChildTemplate");
		childChildTemplate.setParentTemplate(childTemplate);
		final List<BundleTemplateModel> childChildTemplates = new ArrayList<BundleTemplateModel>();
		childChildTemplates.add(childChildTemplate);
		childTemplate.setChildTemplates(childChildTemplates);
		thrown.expect(InterceptorException.class);
		thrown.expectMessage("has 2 or more levels of child templates");
		vlad.onValidate(bundletemplate, ctx);
	}

	@Test
	public void testOnly2TemplateLevelsAllowed3() throws InterceptorException
	{
		// simple parent template
		vlad.onValidate(bundletemplate, ctx);

		// add child template
		final List<BundleTemplateModel> childTemplates = new ArrayList<BundleTemplateModel>();
		final BundleTemplateModel childTemplate = new BundleTemplateModel();
		childTemplate.setParentTemplate(bundletemplate);
		childTemplates.add(childTemplate);
		bundletemplate.setChildTemplates(childTemplates);
		vlad.onValidate(bundletemplate, ctx);

		// create new template and add it as a child to the child template: check the lowest level child template
		final BundleTemplateModel childChildTemplate = new BundleTemplateModel();
		childChildTemplate.setParentTemplate(childTemplate);
		thrown.expect(InterceptorException.class);
		thrown.expectMessage("cannot be assigned as a child to template");
		vlad.onValidate(childChildTemplate, ctx);
	}
}
