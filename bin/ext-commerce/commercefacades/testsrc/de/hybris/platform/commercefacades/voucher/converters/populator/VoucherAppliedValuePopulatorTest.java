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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.voucher.VoucherModelService;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.jalo.util.VoucherValue;
import de.hybris.platform.voucher.model.VoucherModel;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;


/**
 * Test suite for {@link VoucherAppliedValuePopulator}
 */
@UnitTest
public class VoucherAppliedValuePopulatorTest {
    private static final String VOUCHER_CODE = "voucherCode";
    private static final String CODE = "code";
    private VoucherAppliedValuePopulator voucherAppliedValuePopulator;
    @Mock
    private VoucherService voucherService;
    @Mock
    private VoucherModelService voucherModelService;
    @Mock
    private PriceDataFactory priceDataFactory;
    @Mock
    private AbstractOrderModel orderModel;
    @Mock
    private VoucherModel voucherModel;
    @Mock
    private PriceData priceData;
    @Mock
    private VoucherValue voucherValue;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        voucherAppliedValuePopulator = new VoucherAppliedValuePopulator();
        voucherAppliedValuePopulator.setVoucherService(voucherService);
        voucherAppliedValuePopulator.setVoucherModelService(voucherModelService);
        voucherAppliedValuePopulator.setPriceDataFactory(priceDataFactory);
    }

    @Test
    public void testPopulate() {
        final VoucherData voucherData = new VoucherData();
        voucherData.setVoucherCode(VOUCHER_CODE);
        voucherData.setCode(CODE);
        given(voucherService.getVoucher(VOUCHER_CODE)).willReturn(voucherModel);
        given(voucherModelService.getAppliedValue(voucherModel, orderModel)).willReturn(voucherValue);
        given(priceDataFactory.create(any(PriceDataType.class), any(BigDecimal.class), anyString())).willReturn(priceData);

        voucherAppliedValuePopulator.populate(orderModel, voucherData);
        Assert.assertEquals(priceData, voucherData.getAppliedValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateWithNullSource() {
        final VoucherData voucherData = new VoucherData();
        voucherAppliedValuePopulator.populate(null, voucherData);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateWithNullTarget() {
        voucherAppliedValuePopulator.populate(orderModel, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateWithNullVoucherCodeAttribute() {
        final VoucherData voucherData = new VoucherData();
        voucherAppliedValuePopulator.populate(orderModel, voucherData);
    }
}
