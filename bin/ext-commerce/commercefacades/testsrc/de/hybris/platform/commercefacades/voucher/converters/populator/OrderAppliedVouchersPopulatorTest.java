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

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.testframework.Assert;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.model.VoucherModel;


/**
 * Test suite for {@link OrderAppliedVouchersPopulator}
 */
@UnitTest
public class OrderAppliedVouchersPopulatorTest
{
	private OrderAppliedVouchersPopulator orderAppliedVouchersPopulator;
	@Mock
	private OrderModel orderModel;
	@Mock
	private Converter<VoucherModel, VoucherData> voucherConverter;
	@Mock
	private Populator<AbstractOrderModel, VoucherData> appliedVoucherPopulatorList;
	@Mock
	private VoucherService voucherService;
	@Mock
	private VoucherModel voucherModel;
	@Mock
	private VoucherData voucherData;
	@Mock
	private VoucherModel anotherVoucherModel;
	@Mock
	private VoucherData anotherVoucherData;
	private Collection<DiscountModel> discounts;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		orderAppliedVouchersPopulator = new OrderAppliedVouchersPopulator();
		orderAppliedVouchersPopulator.setVoucherConverter(voucherConverter);
		orderAppliedVouchersPopulator.setVoucherService(voucherService);
		orderAppliedVouchersPopulator.setAppliedVoucherPopulatorList(appliedVoucherPopulatorList);

		discounts = new ArrayList<DiscountModel>();
		discounts.add(voucherModel);
		given(voucherConverter.convert(voucherModel)).willReturn(voucherData);
		given(voucherConverter.convert(anotherVoucherModel)).willReturn(anotherVoucherData);
		given(voucherService.getAppliedVouchers(orderModel)).willReturn(discounts);
	}

	@Test
	public void testPopulate()
	{
		final OrderData orderData = new OrderData();
		orderAppliedVouchersPopulator.populate(orderModel, orderData);
		Assert.assertCollectionElements(orderData.getAppliedVouchers(), voucherData);

		discounts.add(anotherVoucherModel);
		orderAppliedVouchersPopulator.populate(orderModel, orderData);
		Assert.assertCollectionElements(orderData.getAppliedVouchers(), voucherData, anotherVoucherData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateWithNullSource()
	{
		final OrderData orderData = new OrderData();
		orderAppliedVouchersPopulator.populate(null, orderData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateWithNullTarget()
	{
		orderAppliedVouchersPopulator.populate(orderModel, null);
	}

}
