package de.hybris.platform.integration.oms.adapter;

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


import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.omsorders.services.query.daos.SyncDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.internal.model.impl.AbstractModelService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hybris.oms.domain.address.Address;
import com.hybris.oms.domain.locationrole.LocationRole;
import com.hybris.oms.domain.order.Order;
import com.hybris.oms.domain.order.OrderLine;
import com.hybris.oms.domain.order.OrderLineQuantity;
import com.hybris.oms.domain.order.OrderLineQuantityStatus;
import com.hybris.oms.domain.order.PaymentInfo;
import com.hybris.oms.domain.shipping.Delivery;
import com.hybris.oms.domain.shipping.Shipment;
import com.hybris.oms.domain.shipping.ShippingAndHandling;
import com.hybris.oms.domain.types.Amount;
import com.hybris.oms.domain.types.Contact;
import com.hybris.oms.domain.types.Price;
import com.hybris.oms.domain.types.Quantity;


/**
 * Tests demonstrating usage of the adapter.
 */
@IntegrationTest
public class OmsOrderSyncAdapterIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private OmsSyncAdapter<Order, OrderModel> omsOrderSyncAdapter;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private SyncDao<OrderModel> orderSyncDao;

	@Resource
	private AbstractModelService modelService;

	private final static String ORDER_ID = "testOrderCode";


	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createDefaultUsers();
		importCsv("/omsorders/test/testAcceleratorData.csv", "UTF-8");
		importCsv("/omsorders/test/testOrders.csv", "UTF-8");

		final BaseSiteModel testSite = baseSiteService.getBaseSiteForUID("testSite");
		Assert.assertNotNull("BaseSite 'testSite' was null", testSite);
		Assert.assertFalse("BaseStore does not have stores", testSite.getStores().isEmpty());
		Assert.assertFalse("BaseStore does not have PointOfService", testSite.getStores().get(0).getPointsOfService().isEmpty());
		Assert.assertNotNull("BaseStore does not have an address", testSite.getStores().get(0).getPointsOfService().get(0)
				.getAddress());
		baseSiteService.setCurrentBaseSite(testSite, false);



	}

	@Test
	public void shouldUpdateOneOrder() throws Exception
	{
		final Date updateTime = new Date();
		final Date updatedRemoteTime = new Date(System.currentTimeMillis() - 2); // The update was done some time ago before the remote change.

		OrderModel orderToUpdate = getOrderForCode(ORDER_ID);
		orderToUpdate.setOrderUpdateRemoteTime(updatedRemoteTime);

		// Order saved as unSync
		modelService.save(orderToUpdate);

		final Order omsOrder = buildOrderWithOLQAndShipment("warehouse_e");
		// Process it as the 'syncher' would
		omsOrderSyncAdapter.update(omsOrder, updateTime);

		// Refresh it 
		orderToUpdate = getOrderForCode(ORDER_ID);

		assertEquals(orderToUpdate.getOrderUpdateRemoteTime(), updateTime);

	}

	protected OrderModel getOrderForCode(final String orderCode)
	{
		final OrderModel orderModel = orderSyncDao.findById(OrderModel.CODE, orderCode);
		Assert.assertNotNull("Order should have been loaded from database", orderModel);
		return orderModel;
	}


	protected Order buildOrder()
	{
		final String orderId = ORDER_ID;
		final Order order = new Order();
		order.setOrderId(orderId);

		order.setUsername("IntegrationTest");
		order.setFirstName("Chuck");
		order.setLastName("Norris");
		order.setEmailid("alex.marquez@hybris.com");
		order.setShippingFirstName("shippingFirstName");
		order.setShippingLastName("shippingLastName");
		order.setShippingTaxCategory("shippingTaxCategory");
		order.setIssueDate(Calendar.getInstance().getTime());
		order.setCurrencyCode("USD");

		final ShippingAndHandling shippingAndHandling = new ShippingAndHandling();
		shippingAndHandling.setOrderId(order.getOrderId());
		shippingAndHandling.setShippingPrice(new Price(new Amount("USD", Double.valueOf(2)),
				new Amount("USD", Double.valueOf(0.5)), new Amount("USD", Double.valueOf(0))));

		order.setShippingAndHandling(shippingAndHandling);

		final Address shippingAddress = new Address("502 MAIN ST N", "26th floor", "MONTREAL", "QC", "H2B1A0", null, null, "CA",
				"Canada", orderId, orderId);

		order.setContact(new Contact(null, "arvato", null, null, "1234567", null, null));
		order.setShippingAddress(shippingAddress);
		order.setShippingMethod("DOM.EP");
		order.setPriorityLevelCode("STANDARD");

		final OrderLine orderLine = this.buildOrderLine(order, ORDER_ID + "_" + 1, "sku", new Quantity("unit", 2), new Quantity(
				"unit", 2), new Amount("USD", Double.valueOf(1)), new Amount("USD", Double.valueOf(0.15)), "AE514",
				LocationRole.SHIPPING);

		order.setOrderLines(Lists.newArrayList(orderLine));

		final PaymentInfo paymentInfo = new PaymentInfo();

		paymentInfo.setPaymentInfoType("Visa");
		paymentInfo.setAuthUrl("http://authURL");
		paymentInfo.setBillingAddress(new Address("2207 7th Avenue", null, "New York", "NY", "10027", Double.valueOf(40.812356),
				Double.valueOf(-73.945857), "US", "U.S.A.", orderId, orderId));

		order.setPaymentInfos(Lists.newArrayList(paymentInfo));
		return order;
	}



	/**
	 * Build a valid sourced order.
	 */
	public Order buildOrderWithOLQAndShipment(final String locationId)
	{
		final Order order = this.buildOrder();
		final OrderLine orderLine = order.getOrderLines().get(0);

		orderLine.setQuantityUnassigned(new Quantity("unit", 0));

		final List<OrderLineQuantity> lstOLQ = new ArrayList<OrderLineQuantity>();
		final OrderLineQuantity olq = new OrderLineQuantity();
		olq.setOlqId("OlqId");
		olq.setQuantity(new Quantity("DDT", 2));
		olq.setLocation(locationId);

		final OrderLineQuantityStatus olqs = new OrderLineQuantityStatus();
		olqs.setActive(true);
		olqs.setDescription("SHIPPED");
		olqs.setStatusCode("SHIPPED");

		olq.setStatus(olqs);

		final Shipment shipment = new Shipment();
		shipment.setAmountCaptured(new Amount("USD", Double.valueOf(2)));
		shipment.setAuthUrls(Arrays.asList("authurl"));
		shipment.setCurrencyCode("USD");
		shipment.setDelivery(new Delivery());


		final Address shippingAddress = new Address("2207 7th Avenue", null, "New York", "NY", "10027", Double.valueOf(40.812356),
				Double.valueOf(-73.945857), "US", "U.S.A.", null, null);

		shipment.getDelivery().setDeliveryAddress(shippingAddress);

		shipment.setShipmentId("ShipmentId");
		shipment.setShippingMethod(order.getShippingMethod());
		shipment.setOrderId(order.getOrderId());
		shipment.setShippingAndHandling(order.getShippingAndHandling());
		shipment.setOlqsStatus("SHIPPED");
		shipment.setLocation(locationId);


		olq.setShipment(shipment);

		lstOLQ.add(olq);
		orderLine.setOrderLineQuantities(lstOLQ);
		order.setOrderLines(Lists.newArrayList(orderLine));

		final PaymentInfo paymentInfo = new PaymentInfo();

		paymentInfo.setPaymentInfoType("Visa");
		paymentInfo.setAuthUrl("http://paymentUrl");
		paymentInfo.setBillingAddress(new Address("2207 7th Avenue", null, "New York", "NY", "10027", Double.valueOf(40.812356),
				Double.valueOf(-73.945857), "US", "U.S.A.", null, null));

		order.setPaymentInfos(Lists.newArrayList(paymentInfo));

		return order;
	}


	public OrderLine buildOrderLine(final Order order, final String orderLineId, final String skuId, final Quantity quantity,
			final Quantity quantityUnassigned, final Amount unitPrice, final Amount unitTax, final String taxCategory,
			final LocationRole locationRole)
	{
		final OrderLine orderLine = new OrderLine();

		if (orderLineId == null || orderLineId.trim().length() == 0)
		{
			orderLine.setOrderLineId(UUID.randomUUID().toString());
		}
		else
		{
			orderLine.setOrderLineId(orderLineId);
		}

		orderLine.setSkuId(skuId);
		orderLine.setQuantity(quantity);
		orderLine.setQuantityUnassigned(quantityUnassigned);
		orderLine.setUnitPrice(unitPrice);
		orderLine.setUnitTax(unitTax);
		orderLine.setTaxCategory(taxCategory);
		final HashSet<LocationRole> roles = new HashSet<LocationRole>();
		roles.add(locationRole);
		orderLine.setLocationRoles(roles);

		return orderLine;
	}


}
