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
package de.hybris.platform.integration.commons.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultOndemandDeliveryAddressStrategyTest
{
	private DefaultOndemandDeliveryAddressStrategy ondemandDeliveryAddressStrategy;


	@Before
	public void setUp() throws Exception
	{
		ondemandDeliveryAddressStrategy = new DefaultOndemandDeliveryAddressStrategy();
	}

	//Test case for scenario where Order Model has Delivery Address
	@Test
	public void testGetDeliveryAddressForOrder()
	{
		final AbstractOrderModel orderModel = mock(AbstractOrderModel.class);
		final MockAddressModel addressModel = new MockAddressModel(12345L);
		given(orderModel.getDeliveryAddress()).willReturn(addressModel);
		assertEquals(addressModel, ondemandDeliveryAddressStrategy.getDeliveryAddressForOrder(orderModel));
	}

	//Test case for scenario where Order Model has Delivery Address and also pick up store entries
	@Test
	public void testGetDeliveryAddressForOrderWithShipAndPickupEntries()
	{
		final AbstractOrderModel orderModel = mock(AbstractOrderModel.class);
		final MockAddressModel addressModel = new MockAddressModel(12345L);
		given(orderModel.getDeliveryAddress()).willReturn(addressModel);

		final AbstractOrderEntryModel shippingEntryModel = mock(AbstractOrderEntryModel.class);
		given(shippingEntryModel.getDeliveryAddress()).willReturn(addressModel);

		final AbstractOrderEntryModel pickupEntryModel1 = mock(AbstractOrderEntryModel.class);
		final PointOfServiceModel pointOfServiceModel1 = mock(PointOfServiceModel.class);
		given(pointOfServiceModel1.getAddress()).willReturn(new MockAddressModel(8888L));
		given(pickupEntryModel1.getDeliveryPointOfService()).willReturn(pointOfServiceModel1);

		final AbstractOrderEntryModel pickupEntryModel2 = mock(AbstractOrderEntryModel.class);
		final PointOfServiceModel pointOfServiceModel2 = mock(PointOfServiceModel.class);
		given(pointOfServiceModel2.getAddress()).willReturn(new MockAddressModel(9999L));
		given(pickupEntryModel2.getDeliveryPointOfService()).willReturn(pointOfServiceModel2);

		final List<AbstractOrderEntryModel> entryList = new ArrayList<AbstractOrderEntryModel>();
		entryList.add(shippingEntryModel);
		entryList.add(pickupEntryModel1);
		entryList.add(pickupEntryModel2);

		given(orderModel.getEntries()).willReturn(entryList);

		assertEquals(addressModel, ondemandDeliveryAddressStrategy.getDeliveryAddressForOrder(orderModel));
	}


	//Test case for scenario where Order Model with only one pickup entry without delivery address
	@Test
	public void testGetDeliveryAddressForOrderWithOnlyOnePickupEntry()
	{
		final AbstractOrderModel orderModel = mock(AbstractOrderModel.class);
		final MockAddressModel addressModel = new MockAddressModel(999L);
		given(orderModel.getDeliveryAddress()).willReturn(null);

		final AbstractOrderEntryModel pickupEntryModel1 = mock(AbstractOrderEntryModel.class);
		final PointOfServiceModel pointOfServiceModel1 = mock(PointOfServiceModel.class);
		given(pointOfServiceModel1.getAddress()).willReturn(addressModel);
		given(pickupEntryModel1.getDeliveryPointOfService()).willReturn(pointOfServiceModel1);

		final List<AbstractOrderEntryModel> entryList = new ArrayList<AbstractOrderEntryModel>();
		entryList.add(pickupEntryModel1);

		given(orderModel.getEntries()).willReturn(entryList);

		assertEquals(addressModel, ondemandDeliveryAddressStrategy.getDeliveryAddressForOrder(orderModel));
	}


	/**
	 * Test case for scenario where Order Model with multiple pickup entries. The address of the entry with highest value
	 * should be returned
	 */
	@Test
	public void testGetDeliveryAddressForOrderWithMultiplePickupEntries()
	{
		final AbstractOrderModel orderModel = mock(AbstractOrderModel.class);
		final MockAddressModel pickupEntryAddressModel1 = new MockAddressModel(8888L);
		final MockAddressModel pickupEntryAddressModel2 = new MockAddressModel(9999L);
		given(orderModel.getDeliveryAddress()).willReturn(null);

		final AbstractOrderEntryModel pickupEntryModel1 = mock(AbstractOrderEntryModel.class);
		final PointOfServiceModel pointOfServiceModel1 = mock(PointOfServiceModel.class);
		given(pointOfServiceModel1.getAddress()).willReturn(pickupEntryAddressModel1);
		given(pickupEntryModel1.getDeliveryPointOfService()).willReturn(pointOfServiceModel1);
		given(pickupEntryModel1.getTotalPrice()).willReturn(Double.valueOf(100D));

		final AbstractOrderEntryModel pickupEntryModel2 = mock(AbstractOrderEntryModel.class);
		final PointOfServiceModel pointOfServiceModel2 = mock(PointOfServiceModel.class);
		given(pointOfServiceModel2.getAddress()).willReturn(pickupEntryAddressModel2);
		given(pickupEntryModel2.getDeliveryPointOfService()).willReturn(pointOfServiceModel2);
		given(pickupEntryModel2.getTotalPrice()).willReturn(Double.valueOf(50D));

		final AbstractOrderEntryModel pickupEntryModel3 = mock(AbstractOrderEntryModel.class);
		final PointOfServiceModel pointOfServiceModel3 = mock(PointOfServiceModel.class);
		given(pointOfServiceModel3.getAddress()).willReturn(pickupEntryAddressModel2);
		given(pickupEntryModel3.getDeliveryPointOfService()).willReturn(pointOfServiceModel3);
		given(pickupEntryModel3.getTotalPrice()).willReturn(Double.valueOf(150D));

		final List<AbstractOrderEntryModel> entryList = new ArrayList<AbstractOrderEntryModel>();
		entryList.add(pickupEntryModel1);
		entryList.add(pickupEntryModel2);
		entryList.add(pickupEntryModel3);

		given(orderModel.getEntries()).willReturn(entryList);

		assertEquals(pickupEntryAddressModel2, ondemandDeliveryAddressStrategy.getDeliveryAddressForOrder(orderModel));
	}

	protected static class MockAddressModel extends AddressModel
	{
		private final long id;

		public MockAddressModel(final long id)
		{
			this.id = id;
		}

		@Override
		public de.hybris.platform.core.PK getPk()
		{
			return de.hybris.platform.core.PK.fromLong(id);
		}
	}


}
