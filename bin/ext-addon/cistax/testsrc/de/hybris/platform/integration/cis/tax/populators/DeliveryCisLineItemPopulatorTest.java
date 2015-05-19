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
package de.hybris.platform.integration.cis.tax.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.externaltax.TaxCodeStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.integration.cis.tax.strategies.ShippingItemCodeStrategy;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.cis.api.model.CisLineItem;


/**
 * 
 *
 */
@UnitTest
public class DeliveryCisLineItemPopulatorTest
{
	private final static String DELIVERY_PRODUCT_CODE = "test-delivery-code";
	private final static String DELIVERY_TAX_CODE = "test-delivery-tax-code";

	private DeliveryCisLineItemPopulator deliveryCisLineItemPopulator;

	@Mock
	private TaxCodeStrategy taxCodeStrategy;
	@Mock
	private ShippingItemCodeStrategy shippingItemCodeStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		deliveryCisLineItemPopulator = new DeliveryCisLineItemPopulator();
		deliveryCisLineItemPopulator.setTaxCodeStrategy(taxCodeStrategy);
		deliveryCisLineItemPopulator.setShippingItemCodeStrategy(shippingItemCodeStrategy);
	}

	@Test
	public void shouldPopulate()
	{
		final CisLineItem cisLineItem = new CisLineItem();
		final AbstractOrderModel abstractOrder = Mockito.mock(AbstractOrderModel.class);
		final DeliveryModeModel deliveryMode = Mockito.mock(DeliveryModeModel.class);

		given(abstractOrder.getDeliveryCost()).willReturn(Double.valueOf(9.99));
		given(deliveryMode.getCode()).willReturn(DELIVERY_PRODUCT_CODE);
		given(abstractOrder.getDeliveryMode()).willReturn(deliveryMode);
		given(taxCodeStrategy.getTaxCodeForCodeAndOrder(abstractOrder.getDeliveryMode().getCode(), abstractOrder)).willReturn(
				DELIVERY_TAX_CODE);
		given(shippingItemCodeStrategy.getShippingItemCode(abstractOrder)).willReturn(Integer.valueOf(2));
		deliveryCisLineItemPopulator.populate(abstractOrder, cisLineItem);

		Assert.assertEquals(BigDecimal.valueOf(abstractOrder.getDeliveryCost().doubleValue()), cisLineItem.getUnitPrice());
		Assert.assertEquals(DELIVERY_TAX_CODE, cisLineItem.getTaxCode());
	}
}
