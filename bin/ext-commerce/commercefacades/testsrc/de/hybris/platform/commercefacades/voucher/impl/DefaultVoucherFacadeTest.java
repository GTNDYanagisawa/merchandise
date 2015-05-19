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
package de.hybris.platform.commercefacades.voucher.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.voucher.VoucherModelService;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.model.VoucherModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultVoucherFacadeTest
{
	private static final String VOUCHER_CODE = "voucherCode";
	private static final String NOT_EXISTING_VOUCHER_CODE = "notExistingVoucherCode";
	private static final String RESTRICTED_VOUCHER_CODE = "restrictedVoucherCode";
	@Mock
	VoucherModel voucherModel;
	@Mock
	VoucherModel restrictedVoucherModel;
	@Mock
	VoucherData voucherData;
	@Mock
	CartModel cartModel;
	Collection<String> appliedVoucherCodes;
	private DefaultVoucherFacade voucherFacade;
	@Mock
	private VoucherService voucherService;
	@Mock
	private VoucherModelService voucherModelService;
	@Mock
	private CartService cartService;
	@Mock
	private Converter<VoucherModel, VoucherData> voucherConverter;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		voucherFacade = new DefaultVoucherFacade();
		voucherFacade.setCartService(cartService);
		voucherFacade.setVoucherService(voucherService);
		voucherFacade.setVoucherModelService(voucherModelService);
		voucherFacade.setVoucherConverter(voucherConverter);

		given(voucherService.getVoucher(VOUCHER_CODE)).willReturn(voucherModel);
		given(voucherService.getVoucher(RESTRICTED_VOUCHER_CODE)).willReturn(restrictedVoucherModel);
		given(Boolean.valueOf(voucherModelService.isApplicable(voucherModel, cartModel))).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(voucherModelService.isApplicable(restrictedVoucherModel, cartModel))).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(voucherModelService.isReservable(voucherModel, VOUCHER_CODE, cartModel))).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(voucherModelService.isReservable(restrictedVoucherModel, RESTRICTED_VOUCHER_CODE, cartModel)))
				.willReturn(Boolean.FALSE);
		given(voucherConverter.convert(voucherModel)).willReturn(voucherData);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(Boolean.valueOf(voucherService.redeemVoucher(VOUCHER_CODE, cartModel))).willReturn(Boolean.TRUE);

		appliedVoucherCodes = new ArrayList<String>();
		appliedVoucherCodes.add(VOUCHER_CODE);
	}

	@Test
	public void testCheckVoucherCode()
	{
		Assert.assertTrue(voucherFacade.checkVoucherCode(VOUCHER_CODE));
		Assert.assertFalse(voucherFacade.checkVoucherCode(NOT_EXISTING_VOUCHER_CODE));
	}

	@Test
	public void testGetVoucher() throws VoucherOperationException
	{
		final VoucherData result = voucherFacade.getVoucher(VOUCHER_CODE);
		Assert.assertEquals(voucherData, result);
	}

	@Test(expected = VoucherOperationException.class)
	public void testGetVoucherForNotExistingVoucher() throws VoucherOperationException
	{
		voucherFacade.getVoucher(NOT_EXISTING_VOUCHER_CODE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetVoucherWithNullParam() throws VoucherOperationException
	{
		voucherFacade.getVoucher(null);
	}

	@Test
	public void testGetVouchersForCart()
	{
		given(voucherService.getAppliedVoucherCodes(cartModel)).willReturn(Collections.EMPTY_LIST);
		de.hybris.platform.testframework.Assert.assertCollection(Collections.EMPTY_LIST, voucherFacade.getVouchersForCart());

		given(voucherService.getAppliedVoucherCodes(cartModel)).willReturn(appliedVoucherCodes);
		de.hybris.platform.testframework.Assert.assertCollectionElements(voucherFacade.getVouchersForCart(), voucherData);
	}

	@Test
	public void testApplyVoucher() throws VoucherOperationException, JaloPriceFactoryException
	{
		voucherFacade.applyVoucher(VOUCHER_CODE);
		verify(voucherService, times(1)).redeemVoucher(VOUCHER_CODE, cartModel);
	}

	@Test(expected = VoucherOperationException.class)
	public void testApplyVoucherFail() throws JaloPriceFactoryException, VoucherOperationException
	{
		given(Boolean.valueOf(voucherService.redeemVoucher(VOUCHER_CODE, cartModel))).willReturn(Boolean.FALSE);
		voucherFacade.applyVoucher(VOUCHER_CODE);
	}

	@Test(expected = VoucherOperationException.class)
	public void testApplyRestrictedVoucherFail() throws JaloPriceFactoryException, VoucherOperationException
	{
		voucherFacade.applyVoucher(RESTRICTED_VOUCHER_CODE);
	}

	@Test(expected = VoucherOperationException.class)
	public void testApplyVoucherWhenTotalPriceExceeded() throws JaloPriceFactoryException, VoucherOperationException
	{
		given(voucherModel.getValue()).willReturn(Double.valueOf(25.0d));
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(20.0d));
		given(voucherModel.getAbsolute()).willReturn(Boolean.TRUE);
		voucherFacade.applyVoucher(VOUCHER_CODE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testApplyVoucherWithNullParam() throws VoucherOperationException
	{
		voucherFacade.applyVoucher(null);
	}

	@Test(expected = VoucherOperationException.class)
	public void testApplyVoucherForNotExistingVoucher() throws VoucherOperationException
	{
		voucherFacade.applyVoucher(NOT_EXISTING_VOUCHER_CODE);
	}

	@Test
	public void testReleaseVoucher() throws VoucherOperationException, JaloPriceFactoryException
	{
		voucherFacade.releaseVoucher(VOUCHER_CODE);
		verify(voucherService, times(1)).releaseVoucher(VOUCHER_CODE, cartModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReleaseVoucherWithNullParam() throws VoucherOperationException
	{
		voucherFacade.releaseVoucher(null);
	}

	@Test(expected = VoucherOperationException.class)
	public void testReleaseVoucherForNotExistingVoucher() throws VoucherOperationException
	{
		voucherFacade.releaseVoucher(NOT_EXISTING_VOUCHER_CODE);
	}
}
