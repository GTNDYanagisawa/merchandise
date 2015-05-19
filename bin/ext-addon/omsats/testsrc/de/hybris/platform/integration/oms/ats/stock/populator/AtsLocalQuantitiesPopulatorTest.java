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
 */
package de.hybris.platform.integration.oms.ats.stock.populator;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hybris.oms.domain.ats.AtsLocalQuantities;
import com.hybris.oms.domain.ats.AtsQuantity;
import com.hybris.oms.domain.types.Quantity;


@UnitTest
public class AtsLocalQuantitiesPopulatorTest
{
	private static final String LOCATION_ID = "testLocationId";
	private static final String ATS_ID = "testAtsId";
	private static final String SKU = "testSku";
	private static final String UNIT_CODE = "testUnitCode";
	private static final int QUANTITY = 50;

	private AtsLocalQuantitiesPopulator atsLocalQuantitiesPopulator;

	@Mock
	private ModelService modelService;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);

		atsLocalQuantitiesPopulator = new AtsLocalQuantitiesPopulator();
		atsLocalQuantitiesPopulator.setModelService(modelService);

		given(modelService.create(WarehouseModel.class)).willReturn(new WarehouseModel());
		given(modelService.create(ProductModel.class)).willReturn(new ProductModel());
		given(modelService.create(StockLevelModel.class)).willReturn(new StockLevelModel());
	}

	@Test
	public void shouldPopulate()
	{
		final AtsLocalQuantities atsLocalQuantities = new AtsLocalQuantities();
		atsLocalQuantities.setLocationId(LOCATION_ID);
		final AtsQuantity atsQuantity = new AtsQuantity();
		atsQuantity.setAtsId(ATS_ID);
		atsQuantity.setSku(SKU);
		final Quantity quantity = new Quantity();
		quantity.setUnitCode(UNIT_CODE);
		quantity.setValue(QUANTITY);
		atsQuantity.setQuantity(quantity);
		atsLocalQuantities.setAtsQuantities(Arrays.asList(atsQuantity));

		final Collection<StockLevelModel> stockLevelModels = new ArrayList<StockLevelModel>();
		atsLocalQuantitiesPopulator.populate(Arrays.asList(atsLocalQuantities), stockLevelModels);

		Assert.assertEquals(stockLevelModels.size(), 1);
		final StockLevelModel stockLevelModel = stockLevelModels.iterator().next();
		Assert.assertEquals(stockLevelModel.getWarehouse().getCode(), LOCATION_ID);
		Assert.assertEquals(stockLevelModel.getProduct().getCode(), SKU);
		Assert.assertEquals(stockLevelModel.getAvailable(), QUANTITY);
	}
}
