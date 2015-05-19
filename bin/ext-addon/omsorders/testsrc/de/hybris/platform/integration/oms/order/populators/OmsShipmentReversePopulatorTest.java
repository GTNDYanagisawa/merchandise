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
package de.hybris.platform.integration.oms.order.populators;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.oms.domain.address.Address;
import com.hybris.oms.domain.shipping.Delivery;
import com.hybris.oms.domain.shipping.Shipment;
import com.hybris.oms.domain.shipping.ShippingAndHandling;
import com.hybris.oms.domain.types.Amount;
import com.hybris.oms.domain.types.Price;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OmsShipmentReversePopulatorTest
{
	@InjectMocks
	private final Populator<Shipment, ConsignmentModel> omsShipmentPopulator = new OmsShipmentReversePopulator();
	@Mock
	private Converter<Address, AddressModel> omsAddressReverseConverter;
	@Mock
	private GenericDao<OrderModel> orderDao;
	@Mock
	private PointOfServiceService pointOfServiceService;
	@Mock
	private DeliveryModeService deliveryModeService;
	@Mock
	private Map<String, ConsignmentStatus> consignmentStatusMapper;
	@Mock
	private WarehouseService warehouseService;
	@Mock
	private ModelService modelService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

	}

	@Test
	public void testOmsShipmentReversePopulator() throws Exception
	{
		final Shipment shipment = buildShipment();
		final ConsignmentModel consigment = new ConsignmentModel();

		BDDMockito.when(pointOfServiceService.getPointOfServiceForName(shipment.getLocation())).thenReturn(
				new PointOfServiceModel());

		BDDMockito.when(omsAddressReverseConverter.convert(shipment.getDelivery().getDeliveryAddress())).thenReturn(
				new AddressModel());
		BDDMockito.when(consignmentStatusMapper.get(shipment.getOlqsStatus())).thenReturn(ConsignmentStatus.SHIPPED);
		BDDMockito.when(warehouseService.getWarehouseForCode(shipment.getLocation())).thenReturn(new WarehouseModel());
		BDDMockito.when(orderDao.find(Collections.singletonMap(OrderModel.CODE, shipment.getOrderId()))).thenReturn(
				Arrays.asList(new OrderModel()));
		BDDMockito.when(modelService.create(AddressModel.class)).thenReturn(new AddressModel());

		omsShipmentPopulator.populate(shipment, consigment);

		assertNotNull(consigment.getDeliveryPointOfService());
		assertNotNull(consigment.getShippingAddress());
		assertEquals(ConsignmentStatus.SHIPPED, consigment.getStatus());
		assertEquals(shipment.getDelivery().getTrackingID(), consigment.getTrackingID());
		assertEquals(shipment.getDelivery().getTrackingUrl(), consigment.getTrackingURL());
		assertEquals(shipment.getDelivery().getActualDeliveryDate(), consigment.getShippingDate());
		assertNotNull(consigment.getWarehouse());
		assertEquals(shipment.getShipmentId(), consigment.getCode());
		assertNotNull(consigment.getOrder());

	}

	private Shipment buildShipment()
	{
		final Shipment shipment = new Shipment();

		shipment.setAmountCaptured(new Amount("USD", Double.valueOf(2)));
		shipment.setAuthUrls(Arrays.asList("authurl"));
		shipment.setCurrencyCode("USD");

		final Delivery delivery = new Delivery();
		delivery.setDeliveryId("d1");
		delivery.setTrackingID("trackingID");
		delivery.setTrackingUrl("trackingURL");
		delivery.setActualDeliveryDate(new Date());

		shipment.setDelivery(delivery);

		shipment.setShipmentId("ShipmentId");
		shipment.setShippingMethod("DOM.EP");
		shipment.setOrderId("OrderId");
		shipment.setLocation("Location");
		final ShippingAndHandling shippingAndHandling = new ShippingAndHandling();
		shippingAndHandling.setOrderId("OrderId");
		shippingAndHandling.setShippingPrice(new Price(new Amount("USD", Double.valueOf(2d)),
				new Amount("USD", Double.valueOf(0.5)), new Amount("USD", Double.valueOf(0))));
		shipment.setShippingAndHandling(shippingAndHandling);

		return shipment;

	}

}
