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
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.variants.model.VariantProductModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VariantOptionDataUrlPopulatorTest {

    private static final int AVAILABLE_STOCK_LEVEL = 10;
    private static final String BASE_PRODUCT_CODE = "product";
    private final String url = "myurl";
    @InjectMocks
    VariantOptionDataUrlPopulator<VariantProductModel, VariantOptionData> populator = new VariantOptionDataUrlPopulator<>();
    @Mock
    private UrlResolver<ProductModel> productModelUrlResolver;

    @Before
    public void setUp() {
        when(productModelUrlResolver.resolve(any(VariantProductModel.class))).thenReturn(url);

    }

    @Test
    public void shouldPopulate() {
        final VariantOptionData optionData = new VariantOptionData();
        final VariantProductModel variantProductModel = mockNewVariantModel();


        populator.populate(variantProductModel, optionData);
        assertThat(optionData.getUrl(), is(url));

    }

    protected GenericVariantProductModel mockNewVariantModel() {
        GenericVariantProductModel variantModel;
        variantModel = new GenericVariantProductModel();
        variantModel.setStockLevels(new HashSet<StockLevelModel>());
        variantModel.setOthers((Collection) new ArrayList<>());
        variantModel.setCode(BASE_PRODUCT_CODE);

        return variantModel;
    }

    protected StockLevelModel mockStockLevelModel() {
        final StockLevelModel stockLevelModel = new StockLevelModel();
        stockLevelModel.setAvailable(AVAILABLE_STOCK_LEVEL);
        return stockLevelModel;
    }
}
