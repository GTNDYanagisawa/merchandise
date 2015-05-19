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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.GenericVariantProductModel;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VariantOptionDataStockPopulatorTest
{

	private static final int AVAILABLE_STOCK_LEVEL = 10;
	private static final String BASE_PRODUCT_CODE = "product";

	@InjectMocks
	VariantOptionDataStockPopulator<VariantProductModel, VariantOptionData> populator = new VariantOptionDataStockPopulator<>();

	@Test
	public void shouldPopulate()
	{
		final VariantOptionData optionData = new VariantOptionData();
		final VariantProductModel variantProductModel = mockNewVariantModel();
		variantProductModel.getStockLevels().add(mockStockLevelModel());
		variantProductModel.getStockLevels().add(mockStockLevelModel());

		populator.populate(variantProductModel, optionData);

		assertThat(optionData.getStock().getStockLevel(), is(AVAILABLE_STOCK_LEVEL * 2L)); // there are two stock level with the same stock level

	}

	protected GenericVariantProductModel mockNewVariantModel()
	{
		GenericVariantProductModel variantModel;
		variantModel = new GenericVariantProductModel();
		variantModel.setStockLevels(new HashSet<StockLevelModel>());
		variantModel.setOthers((Collection) new ArrayList<>());
		variantModel.setCode(BASE_PRODUCT_CODE);

		return variantModel;
	}

	protected StockLevelModel mockStockLevelModel()
	{
		final StockLevelModel stockLevelModel = new StockLevelModel();
		stockLevelModel.setAvailable(AVAILABLE_STOCK_LEVEL);
		return stockLevelModel;
	}
}
