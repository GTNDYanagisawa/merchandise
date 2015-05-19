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
package de.hybris.platform.commercefacades.voucher.converters.populator;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.voucher.model.PromotionVoucherModel;
import de.hybris.platform.voucher.model.VoucherModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link de.hybris.platform.commercefacades.voucher.converters.populator.VoucherPopulator}
 */
@UnitTest
public class VoucherPopulatorTest
{
	private static final String CODE = "code";
	private static final String VOUCHER_CODE = "voucherCode";
	private static final String VOUCHER_NAME = "voucherName";
	private static final String VOUCHER_DESC = "voucherDesc";
	private static final String VOUCHER_VALUE_STING = "voucherValue";
	private static final Double VOUCHER_VALUE = Double.valueOf(20d);

	private VoucherPopulator voucherPopulator;
	@Mock
	private VoucherModel voucherModel;
	@Mock
	private PromotionVoucherModel promotionVoucherModel;
	@Mock
	private AbstractConverter<CurrencyModel, CurrencyData> currencyConverter;
	@Mock
	private CurrencyData currencyData;
	@Mock
	private CurrencyModel currencyModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		voucherPopulator = new VoucherPopulator();
		voucherPopulator.setCurrencyConverter(currencyConverter);

		given(voucherModel.getCode()).willReturn(CODE);
		given(voucherModel.getName()).willReturn(VOUCHER_NAME);
		given(voucherModel.getDescription()).willReturn(VOUCHER_DESC);
		given(voucherModel.getValue()).willReturn(VOUCHER_VALUE);
		given(voucherModel.getValueString()).willReturn(VOUCHER_VALUE_STING);
		given(voucherModel.getFreeShipping()).willReturn(Boolean.TRUE);
		given(voucherModel.getCurrency()).willReturn(currencyModel);
		given(currencyConverter.convert(currencyModel)).willReturn(currencyData);

		given(promotionVoucherModel.getCode()).willReturn(CODE);
		given(promotionVoucherModel.getName()).willReturn(VOUCHER_NAME);
		given(promotionVoucherModel.getDescription()).willReturn(VOUCHER_DESC);
		given(promotionVoucherModel.getValue()).willReturn(VOUCHER_VALUE);
		given(promotionVoucherModel.getValueString()).willReturn(VOUCHER_VALUE_STING);
		given(promotionVoucherModel.getVoucherCode()).willReturn(VOUCHER_CODE);
		given(promotionVoucherModel.getFreeShipping()).willReturn(Boolean.FALSE);
		given(promotionVoucherModel.getCurrency()).willReturn(null);

	}

	@Test
	public void testPopulate()
	{
		final VoucherData voucherData = new VoucherData();
		voucherPopulator.populate(voucherModel, voucherData);

		Assert.assertEquals(CODE, voucherData.getCode());
		Assert.assertEquals(VOUCHER_NAME, voucherData.getName());
		Assert.assertEquals(VOUCHER_DESC, voucherData.getDescription());
		Assert.assertEquals(VOUCHER_VALUE, voucherData.getValue());
		Assert.assertEquals(VOUCHER_VALUE_STING, voucherData.getValueFormatted());
		Assert.assertEquals(VOUCHER_VALUE_STING, voucherData.getValueString());
		Assert.assertNull(voucherData.getVoucherCode());
		Assert.assertTrue(voucherData.isFreeShipping());
		Assert.assertEquals(currencyData, voucherData.getCurrency());
	}

	@Test
	public void testPromotionVoucherPopulate()
	{
		final VoucherData voucherData = new VoucherData();
		voucherPopulator.populate(promotionVoucherModel, voucherData);

		Assert.assertEquals(CODE, voucherData.getCode());
		Assert.assertEquals(VOUCHER_NAME, voucherData.getName());
		Assert.assertEquals(VOUCHER_DESC, voucherData.getDescription());
		Assert.assertEquals(VOUCHER_VALUE, voucherData.getValue());
		Assert.assertEquals(VOUCHER_VALUE_STING, voucherData.getValueFormatted());
		Assert.assertEquals(VOUCHER_VALUE_STING, voucherData.getValueString());
		Assert.assertEquals(VOUCHER_CODE, voucherData.getVoucherCode());
		Assert.assertFalse(voucherData.isFreeShipping());
		Assert.assertNull(voucherData.getCurrency());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateWithNullSource()
	{
		final VoucherData voucherData = new VoucherData();
		voucherPopulator.populate(null, voucherData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateWithNullTarget()
	{
		voucherPopulator.populate(voucherModel, null);
	}
}
