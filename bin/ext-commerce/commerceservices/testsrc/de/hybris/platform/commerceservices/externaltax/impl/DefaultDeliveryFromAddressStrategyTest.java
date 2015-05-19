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
package de.hybris.platform.commerceservices.externaltax.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.given;

@UnitTest
public class DefaultDeliveryFromAddressStrategyTest
{
	private DefaultDeliveryFromAddressStrategy defaultDeliveryFromAddressStrategy;

	@Mock
	private BaseStoreService baseStoreService;

    @Mock
    private AbstractOrderModel abstractOrderModel;
    @Mock
    private AbstractOrderEntryModel abstractOrderEntryModel;
    @Mock
    private AddressModel deliveryAddressModel;
    @Mock
    private BaseStoreModel baseStoreModel;
    @Mock
    private WarehouseModel warehouseModel1;
    @Mock
    private WarehouseModel warehouseModel2;
    @Mock
    private PointOfServiceModel pointOfServiceModel;
    @Mock
    private AddressModel originAddress;

    private List<WarehouseModel> warehouseModelCollection;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultDeliveryFromAddressStrategy = new DefaultDeliveryFromAddressStrategy();
		defaultDeliveryFromAddressStrategy.setBaseStoreService(baseStoreService);

        warehouseModelCollection = new ArrayList<>();
        warehouseModelCollection.add(warehouseModel1);
        warehouseModelCollection.add(warehouseModel2);

        given(abstractOrderModel.getEntries()).willReturn(Collections.singletonList(abstractOrderEntryModel));
        given(warehouseModel1.getDefault()).willReturn(Boolean.TRUE);
	}

    @Test
    public void testGetDeliveryFromAddressForOrderWithDelivery()
    {
        given(abstractOrderModel.getDeliveryAddress()).willReturn(deliveryAddressModel);
        given(abstractOrderModel.getStore()).willReturn(baseStoreModel);
        given(baseStoreModel.getWarehouses()).willReturn(warehouseModelCollection);
        given(warehouseModel1.getPointsOfService()).willReturn(Collections.singletonList(pointOfServiceModel));
        given(pointOfServiceModel.getAddress()).willReturn(originAddress);

        Assert.assertEquals(originAddress, defaultDeliveryFromAddressStrategy.getDeliveryFromAddressForOrder(abstractOrderModel));
    }

    @Test
    public void testGetDeliveryFromAddressForOrderWithoutDelivery()
    {
        given(abstractOrderModel.getDeliveryAddress()).willReturn(null);
        given(abstractOrderEntryModel.getDeliveryPointOfService()).willReturn(pointOfServiceModel);
        given(pointOfServiceModel.getAddress()).willReturn(originAddress);

        Assert.assertEquals(originAddress, defaultDeliveryFromAddressStrategy.getDeliveryFromAddressForOrder(abstractOrderModel));
    }
}