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
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.oms.domain.order.OrderLineQuantity;
import com.hybris.oms.domain.order.OrderLineQuantityStatus;
import com.hybris.oms.domain.shipping.Shipment;
import com.hybris.oms.domain.types.Quantity;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OmsOLQReversePopulatorTest
{
	@InjectMocks
	private final Populator<OrderLineQuantity, ConsignmentEntryModel> omsOLQReversePopulator = new OmsOLQReversePopulator();

	@Mock
	private GenericDao<ConsignmentModel> consignmentDao;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testOmsOLQReversePopulator() throws Exception
	{
		final ConsignmentEntryModel consignmentEntryModel = new ConsignmentEntryModel();
		final OrderLineQuantity orderLineQuantity = buildOrderLineQuantity();

		final Map<String, String> params = Collections.singletonMap(ConsignmentModel.CODE, orderLineQuantity.getShipment()
				.getShipmentId());
		BDDMockito.when(consignmentDao.find(params)).thenReturn(Arrays.asList(new ConsignmentModel()));

		omsOLQReversePopulator.populate(orderLineQuantity, consignmentEntryModel);

		assertNotNull(consignmentEntryModel.getConsignment());
		assertEquals(orderLineQuantity.getQuantity().getValue(), consignmentEntryModel.getQuantity().intValue());
		assertEquals(orderLineQuantity.getQuantity().getValue(), consignmentEntryModel.getShippedQuantity().intValue());

	}

	/**
	 * Build a valid OrderLineQuantity.
	 */
	public OrderLineQuantity buildOrderLineQuantity()
	{
		final OrderLineQuantity olq = new OrderLineQuantity();
		olq.setOlqId("OlqId");
		olq.setQuantity(new Quantity("DDT", 2));
		olq.setLocation("locationId");

		final OrderLineQuantityStatus olqs = new OrderLineQuantityStatus();
		olqs.setActive(true);
		olqs.setDescription("Sourced");
		olqs.setStatusCode("SOURCED");

		olq.setStatus(olqs);

		final Shipment shipment = new Shipment();
		shipment.setShipmentId("shipmentId");

		olq.setShipment(shipment);

		return olq;
	}

}
