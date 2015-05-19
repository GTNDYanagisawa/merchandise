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
package de.hybris.platform.commercefacades.order.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



@UnitTest
public class PickupOrderEntryGroupPopulatorTest
{
	private static final Double POS1_LON = Double.valueOf(123.0);
	private static final Double POS2_LON = Double.valueOf(103.0);
	private static final Double POS1_LAT = Double.valueOf(13.0);
	private static final Double POS2_LAT = Double.valueOf(23.0);
	private static final String POS1_NAME = "POS1";
	private static final String POS2_NAME = "POS2";
	private static final Long ENTRY1_QTY = Long.valueOf(1);
	private static final Long ENTRY2_QTY = Long.valueOf(2);
	private static final Long ENTRY3_QTY = Long.valueOf(3);
	private static final Long ENTRY4_QTY = Long.valueOf(4);


	private final PickupOrderEntryGroupPopulator populator = new PickupOrderEntryGroupPopulator();

	@Mock
	private Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter;
	@Mock
	private AbstractOrderModel abstractOrderModel;
	@Mock
	private PriceDataFactory priceDataFactory;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		populator.setPointOfServiceConverter(pointOfServiceConverter);
		populator.setPriceDataFactory(priceDataFactory);
	}

	@Test
	public void testPopulate()
	{
		final AbstractOrderEntryModel entryModel1 = mock(AbstractOrderEntryModel.class);
		final AbstractOrderEntryModel entryModel2 = mock(AbstractOrderEntryModel.class);
		final AbstractOrderEntryModel entryModel3 = mock(AbstractOrderEntryModel.class);
		final AbstractOrderEntryModel entryModel4 = mock(AbstractOrderEntryModel.class);

		final OrderEntryData entryData1 = mock(OrderEntryData.class);
		final OrderEntryData entryData2 = mock(OrderEntryData.class);
		final OrderEntryData entryData3 = mock(OrderEntryData.class);
		final OrderEntryData entryData4 = mock(OrderEntryData.class);

		final PointOfServiceModel pointOfServiceModel1 = mock(PointOfServiceModel.class);
		final PointOfServiceModel pointOfServiceModel2 = mock(PointOfServiceModel.class);

		given(pointOfServiceModel1.getLatitude()).willReturn(POS1_LAT);
		given(pointOfServiceModel1.getLongitude()).willReturn(POS1_LON);
		given(pointOfServiceModel2.getLatitude()).willReturn(POS2_LAT);
		given(pointOfServiceModel2.getLongitude()).willReturn(POS2_LON);
		given(pointOfServiceModel1.getName()).willReturn(POS1_NAME);
		given(pointOfServiceModel2.getName()).willReturn(POS2_NAME);
		given(entryModel1.getDeliveryPointOfService()).willReturn(pointOfServiceModel1);
		given(entryModel2.getDeliveryPointOfService()).willReturn(pointOfServiceModel2);
		given(entryModel3.getDeliveryPointOfService()).willReturn(pointOfServiceModel2);

		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		entries.add(entryModel1);
		entries.add(entryModel2);
		entries.add(entryModel3);
		entries.add(entryModel4);
		given(abstractOrderModel.getEntries()).willReturn(entries);

		given(entryModel1.getOrder()).willReturn(abstractOrderModel);
		given(entryModel2.getOrder()).willReturn(abstractOrderModel);
		given(entryModel3.getOrder()).willReturn(abstractOrderModel);
		given(entryModel4.getOrder()).willReturn(abstractOrderModel);

		given(entryModel1.getQuantity()).willReturn(ENTRY1_QTY);
		given(entryModel2.getQuantity()).willReturn(ENTRY2_QTY);
		given(entryModel3.getQuantity()).willReturn(ENTRY3_QTY);
		given(entryModel4.getQuantity()).willReturn(ENTRY4_QTY);

		final List<OrderEntryData> entryDatas = new ArrayList<OrderEntryData>();
		entryDatas.add(entryData1);
		entryDatas.add(entryData2);
		entryDatas.add(entryData3);
		entryDatas.add(entryData4);

		final PointOfServiceData pointOfServiceData1 = mock(PointOfServiceData.class);
		final PointOfServiceData pointOfServiceData2 = mock(PointOfServiceData.class);
		given(pointOfServiceData1.getName()).willReturn(POS1_NAME);
		given(pointOfServiceData2.getName()).willReturn(POS2_NAME);
		given(pointOfServiceConverter.convert(pointOfServiceModel1)).willReturn(pointOfServiceData1);
		given(pointOfServiceConverter.convert(pointOfServiceModel2)).willReturn(pointOfServiceData2);


		final AbstractOrderData abstractOrderData = new AbstractOrderData();
		abstractOrderData.setEntries(entryDatas);
		populator.populate(abstractOrderModel, abstractOrderData);

		Assert.assertNotNull(abstractOrderData.getPickupOrderGroups());
		Assert.assertEquals(2, abstractOrderData.getPickupOrderGroups().size());
		Assert.assertEquals(pointOfServiceData1, abstractOrderData.getPickupOrderGroups().get(0).getDeliveryPointOfService());
		Assert.assertEquals(1, abstractOrderData.getPickupOrderGroups().get(0).getEntries().size());
		Assert.assertEquals(pointOfServiceData2, abstractOrderData.getPickupOrderGroups().get(1).getDeliveryPointOfService());
		Assert.assertEquals(2, abstractOrderData.getPickupOrderGroups().get(1).getEntries().size());
		Assert.assertTrue(abstractOrderData.getPickupOrderGroups().get(0).getEntries().iterator().next() == entryData1);
		Assert.assertEquals(Long.valueOf(6), abstractOrderData.getPickupItemsQuantity());
	}
}
