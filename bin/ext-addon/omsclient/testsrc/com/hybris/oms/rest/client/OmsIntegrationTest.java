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
 * 
 *  
 */
package com.hybris.oms.rest.client;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.util.Collections;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hybris.oms.api.Pageable;
import com.hybris.oms.api.ats.AtsFacade;
import com.hybris.oms.api.inventory.InventoryFacade;
import com.hybris.oms.api.inventory.OmsInventory;
import com.hybris.oms.api.order.OrderFacade;
import com.hybris.oms.api.preference.PreferenceFacade;
import com.hybris.oms.api.shipment.ShipmentFacade;
import com.hybris.oms.domain.inventory.Location;
import com.hybris.oms.domain.order.Order;
import com.hybris.oms.domain.preference.TenantPreference;
import com.hybris.oms.domain.shipping.Shipment;
import com.hybris.oms.domain.shipping.ShipmentQueryObject;
import com.hybris.oms.ui.api.shipment.UIShipment;
import com.hybris.oms.ui.api.shipment.UiShipmentFacade;


/**
 * Manual test for calling an external OMS instance via REST client embedded in the platform.
 */
@ManualTest
public class OmsIntegrationTest extends ServicelayerBaseTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(OmsIntegrationTest.class.getName());

	private static final int QUANTITY = 100;

	private static final int RETRIES = 10;

	@Resource
	private InventoryFacade omsInventoryRestClient;

	@Resource
	private OrderFacade omsOrderRestClient;

	@Resource
	private ShipmentFacade omsShipmentRestClient;

	@Resource
	private UiShipmentFacade omsUiShipmentRestClient;

	@Resource
	private PreferenceFacade omsTenantRestClient;

	@Resource
	private AtsFacade omsAtsRestClient;

	private final TestHelper helper = new TestHelper();

	private Location location;

	private Order order;

	private OmsInventory inventory;

	private TenantPreference prefPayment;

	private TenantPreference prefTax;

	@Before
	public void setUp() throws Exception
	{
		location = helper.buildLocation();
		omsInventoryRestClient.createStockRoomLocation(location);
		inventory = helper.buildInventory(location.getLocationId(), QUANTITY);
		omsInventoryRestClient.createInventory(inventory);
		helper.delay();
	}

	@After
	public void tearDown()
	{
		if (prefPayment != null)
		{
			prefPayment.setValue(Boolean.TRUE.toString());
			omsTenantRestClient.updateTenantPreference(prefPayment);
		}
		if (prefTax != null)
		{
			prefTax.setValue(Boolean.TRUE.toString());
			omsTenantRestClient.updateTenantPreference(prefTax);
		}
	}

	@Test
	public void testOrderProcessing()
	{
		order = helper.buildOrder(inventory.getSkuId());
		omsOrderRestClient.createOrder(order);
		final ShipmentQueryObject query = new ShipmentQueryObject();
		query.setOrderIds(Collections.singletonList(order.getOrderId()));
		int count = 0;
		Pageable<UIShipment> shipments;
		do
		{
			helper.delay();
			shipments = omsUiShipmentRestClient.findUIShipmentsByQuery(query);
		}
		while (count++ < RETRIES && CollectionUtils.isEmpty(shipments.getResults()));
		Assert.assertNotNull(shipments.getResults());
		Assert.assertEquals(1, shipments.getResults().size());
		final String shipmentId = shipments.getResults().get(0).getShipmentId().toString();
		omsShipmentRestClient.confirmShipment(shipmentId);
		count = 0;
		Shipment shipment;
		do
		{
			helper.delay();
			shipment = omsShipmentRestClient.getShipmentById(shipmentId);
		}
		while (count++ < RETRIES && !"DONE".equals(shipment.getState()));
		Assert.assertEquals("DONE", shipment.getState());

		final byte[] shippingLabel = omsShipmentRestClient.retrieveShippingLabelsByShipmentId(shipmentId);
		Assert.assertNotNull(shippingLabel);
		Assert.assertTrue((new String(shippingLabel)).contains("PDF"));
	}
}
