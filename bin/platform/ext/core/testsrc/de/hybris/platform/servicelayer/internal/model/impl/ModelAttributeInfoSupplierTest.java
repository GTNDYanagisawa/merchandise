/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.servicelayer.internal.model.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.internal.converter.util.ModelUtils;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * Test class for {@link de.hybris.platform.servicelayer.internal.model.impl.ModelAttributeInfoSupplier}
 */
@IntegrationTest
public class ModelAttributeInfoSupplierTest extends ServicelayerBaseTest
{
	private DiscountModel discountModel;

	@Resource
	TypeService typeService;

	@Resource
	ModelService modelService;

	@Before
	public void setUp()
	{
		final CurrencyModel currencyModel = modelService.create(CurrencyModel.class);
		currencyModel.setIsocode("EUR");

		discountModel = modelService.create(DiscountModel.class);
		discountModel.setCode("discount1");
		discountModel.setCurrency(currencyModel);

		modelService.saveAll(currencyModel, discountModel);
	}

	@Test(expected = AttributeNotSupportedException.class)
	public void testModelAttributeInfoSupplierInstantiationFailedTypeIsPK()
	{
		assertTrue(ModelUtils.existsField(discountModel.getClass(), ItemModel.PK.toUpperCase()));
		final ModelAttributeInfoSupplier info = new ModelAttributeInfoSupplier(discountModel, ItemModel.PK);
		fail("Instantiation of " + info + " Should NOT be successful in this case");
	}

	@Test(expected = AttributeNotSupportedException.class)
	public void testModelAttributeInfoSupplierInstantiationFailedAttributeNotExists()
	{
		final String qualifier = "BLABLA";
		assertFalse(ModelUtils.existsField(discountModel.getClass(), qualifier));
		final ModelAttributeInfoSupplier info = new ModelAttributeInfoSupplier(discountModel, qualifier);
		fail("Instantiation of " + info + " Should NOT be successful in this case");
	}

	@Test
	public void testModelAttributeInfoSupplierInstantiationSuccessful()
	{
		final String qualifier = DiscountModel.CURRENCY;
		assertTrue(ModelUtils.existsField(discountModel.getClass(), qualifier.toUpperCase()));
		final ModelAttributeInfoSupplier info = new ModelAttributeInfoSupplier(discountModel, qualifier);
		Assert.assertNotNull(info);
		assertFalse(info.isPrimitive());
		assertFalse(info.isPreFetched());
		assertTrue(info.isReferenceAttribute());
		//info.getModelAttributeValue()
	}

	@Test
	public void testIsPrimitive()
	{
		//currently it seems - that we have only non-primitive members in our models. E.g. below Priority - Integer, not int
		ModelAttributeInfoSupplier info = new ModelAttributeInfoSupplier(discountModel, DiscountModel.PRIORITY);
		assertFalse(info.isPrimitive());
		info = new ModelAttributeInfoSupplier(discountModel, DiscountModel.CURRENCY);
		assertFalse(info.isPrimitive());
	}

	@Test
	public void testIsPreFetched()
	{
		final ModelAttributeInfoSupplier info = new ModelAttributeInfoSupplier(discountModel, DiscountModel.NAME);
		assertFalse(info.isPreFetched());
	}

	@Test
	public void testIsReferenceAttribute()
	{
		ModelAttributeInfoSupplier info = new ModelAttributeInfoSupplier(discountModel, DiscountModel.CODE);
		assertFalse(info.isReferenceAttribute());
		info = new ModelAttributeInfoSupplier(discountModel, DiscountModel.CURRENCY);
		assertTrue(info.isReferenceAttribute());
	}

	@Test
	public void testGetModelAttributeValue()
	{
		final ModelAttributeInfoSupplier info = new ModelAttributeInfoSupplier(discountModel, DiscountModel.CODE);
		assertEquals(discountModel.getCode(), info.getModelAttributeValue());
	}

}
