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
package de.hybris.platform.yb2bacceleratorfacades.variant.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.GenericVariantProductModel;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.variants.model.VariantProductModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VariantOptionDataPricePopulatorTest {

    private static final String BASE_PRODUCT_CODE = "product";
    @InjectMocks
    private final VariantOptionDataPricePopulator variantOptionDataPricePopulator = new VariantOptionDataPricePopulator<>();
    private final PriceData priceData = new PriceData();
    @Mock
    PriceInformation priceInformation;
    @Mock
    PriceValue priceValue;
    @Mock
    private PriceDataFactory priceDataFactory;
    @Mock
    private CommercePriceService commercePriceService;

    @Before
    public void setUp() {
        priceValue = new PriceValue("ISO", 1D, true);

        when(commercePriceService.getWebPriceForProduct(any(VariantProductModel.class))).thenReturn(priceInformation);
        when(priceDataFactory.create(eq(PriceDataType.FROM), any(BigDecimal.class), eq(priceValue.getCurrencyIso()))).thenReturn(
                priceData);
        when(priceInformation.getPriceValue()).thenReturn(priceValue);

    }

    @Test
    public void shouldCreateNonEmptyPrice() {
        final VariantProductModel variantProductModel = mockNewVariantModel();
        final VariantOptionData optionData = new VariantOptionData();

        variantOptionDataPricePopulator.populate(variantProductModel, optionData);
        assertThat(optionData.getPriceData(), is(priceData));
    }


    private GenericVariantProductModel mockNewVariantModel() {
        GenericVariantProductModel variantModel;
        variantModel = new GenericVariantProductModel();
        variantModel.setStockLevels(new HashSet<StockLevelModel>());
        variantModel.setOthers((Collection) new ArrayList<>());
        variantModel.setCode(BASE_PRODUCT_CODE);

        return variantModel;
    }

}
