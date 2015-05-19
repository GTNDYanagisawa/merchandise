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

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.daos.OrderEntryDao;
import de.hybris.platform.configurablebundleservices.model.AutoPickBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
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
 * JUnit test suite for {@link DefaultCartBundleComponentEditableChecker}
 */
@UnitTest
public class DefaultCartBundleComponentEditableCheckerTest
{

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DefaultCartBundleComponentEditableChecker bundleComponentEditableChecker;
	private CartModel masterCartModel;
	private CartModel childCartModelMonthly;
	private CartEntryModel cartEntryModel;
	@Mock
	private OrderEntryDao cartEntryDao;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		bundleComponentEditableChecker = new DefaultCartBundleComponentEditableChecker();
		bundleComponentEditableChecker.setOrderEntryDao(cartEntryDao);

		cartEntryModel = mock(CartEntryModel.class);
		masterCartModel = mock(CartModel.class);
		childCartModelMonthly = mock(CartModel.class);
		given(childCartModelMonthly.getParent()).willReturn(masterCartModel);

		final List<AbstractOrderModel> childCarts = new ArrayList<AbstractOrderModel>();
		childCarts.add(childCartModelMonthly);
		given(masterCartModel.getChildren()).willReturn(childCarts);
	}

	@Test
	public void TestCheckIsComponentDependencyMetWhenNoMasterCart() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("is not a master abstractOrder");

		bundleComponentEditableChecker.checkIsComponentDependencyMet(childCartModelMonthly, new BundleTemplateModel(), 1);
	}

	@Test
	public void TestCheckIsComponentSelectionCriteriaMetWhenNoMasterCart() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("is not a master abstractOrder");

		bundleComponentEditableChecker.checkIsComponentSelectionCriteriaMet(childCartModelMonthly, new BundleTemplateModel(), 1);
	}

	@Test
	public void testCanEdit()
	{
		final BundleTemplateModel bundleTemplateDevice = mock(BundleTemplateModel.class);
		final BundleTemplateModel bundleTemplatePlan = mock(BundleTemplateModel.class);
		final BundleTemplateModel bundleTemplateFee = mock(BundleTemplateModel.class);
		final BundleTemplateModel bundleTemplateAddOn = mock(BundleTemplateModel.class);

		// bundleTemplateAddOn has no selection dependency
		boolean isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.TRUE, Boolean.valueOf(isEditable));

		// bundleTemplateAddOn has a selection dependency which is empty
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.TRUE, Boolean.valueOf(isEditable));

		// bundleTemplateAddOn has a selection dependency (to device component) but the dependent component has no cart entries
		final Collection<BundleTemplateModel> requiredTemplates = new HashSet<BundleTemplateModel>();
		requiredTemplates.add(bundleTemplateDevice);
		given(bundleTemplateAddOn.getRequiredBundleTemplates()).willReturn(requiredTemplates);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.FALSE, Boolean.valueOf(isEditable));

		// device component gets pick 1 to 2 selection criteria and 1 cart entry
		final PickNToMBundleSelectionCriteriaModel pickNToMBundleSelectionCriteria = mock(PickNToMBundleSelectionCriteriaModel.class);
		given(bundleTemplateDevice.getBundleSelectionCriteria()).willReturn(pickNToMBundleSelectionCriteria);
		given(pickNToMBundleSelectionCriteria.getN()).willReturn(Integer.valueOf(1));
		given(pickNToMBundleSelectionCriteria.getM()).willReturn(Integer.valueOf(2));
		final List<CartEntryModel> deviceEntries = new ArrayList<CartEntryModel>();
		deviceEntries.add(cartEntryModel);
		given(cartEntryDao.findEntriesByMasterCartAndBundleNoAndTemplate(masterCartModel, 1, bundleTemplateDevice)).willReturn(
				deviceEntries);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.TRUE, Boolean.valueOf(isEditable));

		// bundleTemplateAddOn gets another selection dependency (to plan component) which has no pick criteria and no cart entries
		requiredTemplates.add(bundleTemplatePlan);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.FALSE, Boolean.valueOf(isEditable));

		// plan component gets a pick exactly 1 criteria but has still no cart entries yet
		final PickExactlyNBundleSelectionCriteriaModel pickExactlyNBundleSelectionCriteria = mock(PickExactlyNBundleSelectionCriteriaModel.class);
		given(bundleTemplatePlan.getBundleSelectionCriteria()).willReturn(pickExactlyNBundleSelectionCriteria);
		given(pickExactlyNBundleSelectionCriteria.getN()).willReturn(Integer.valueOf(1));
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.FALSE, Boolean.valueOf(isEditable));

		// add a product to plan component
		final List<CartEntryModel> planEntries = new ArrayList<CartEntryModel>();
		planEntries.add(cartEntryModel);
		given(cartEntryDao.findEntriesByMasterCartAndBundleNoAndTemplate(masterCartModel, 1, bundleTemplatePlan)).willReturn(
				planEntries);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.TRUE, Boolean.valueOf(isEditable));

		// bundleTemplateAddOn gets a 3rd selection dependency (to fee component) which has no pick criteria and no cart entries
		requiredTemplates.add(bundleTemplateFee);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.FALSE, Boolean.valueOf(isEditable));

		// fee component gets an cart entry but no selection criteria yet
		final List<CartEntryModel> feeEntries = new ArrayList<CartEntryModel>();
		feeEntries.add(cartEntryModel);
		given(cartEntryDao.findEntriesByMasterCartAndBundleNoAndTemplate(masterCartModel, 1, bundleTemplateFee)).willReturn(
				feeEntries);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.TRUE, Boolean.valueOf(isEditable));

		// fee component gets an auto-pick criteria, but there are no dependent products (auto-picks) in the fee component
		final AutoPickBundleSelectionCriteriaModel autoPickBundleSelectionCriteria = mock(AutoPickBundleSelectionCriteriaModel.class);
		given(bundleTemplateFee.getBundleSelectionCriteria()).willReturn(autoPickBundleSelectionCriteria);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.FALSE, Boolean.valueOf(isEditable));

		// add auto-pick products to fee component
		final List<ProductModel> products = new ArrayList<ProductModel>();
		products.add(new ProductModel());
		given(bundleTemplateFee.getProducts()).willReturn(products);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.TRUE, Boolean.valueOf(isEditable));

		// add a 2nd product to plan component (only 1 pick allowed)
		final CartEntryModel cartEntryModel2 = mock(CartEntryModel.class);
		planEntries.add(cartEntryModel2);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.FALSE, Boolean.valueOf(isEditable));
	}

}