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
package com.hybris.cis.client.rest.tax.mock;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hybris.cis.api.model.CisAddress;
import com.hybris.cis.api.model.CisAddressType;
import com.hybris.cis.api.model.CisDecision;
import com.hybris.cis.api.model.CisLineItem;
import com.hybris.cis.api.model.CisOrder;
import com.hybris.cis.api.tax.model.CisTaxDoc;
import com.hybris.cis.client.rest.tax.TaxClient;
import com.hybris.commons.client.RestResponse;
import com.hybris.commons.mock.GenericMockFactory;


/**
 * Validates that the "out-of-the-box" spring configuration will wire in the mock client if mock mode is set.
 */
@IntegrationTest
public class TaxMockClientTest extends ServicelayerTest
{
	private TaxClient taxClient;

	private CisOrder usOrder;
	private RestResponse<CisTaxDoc> response;


	@Before
	public void before() throws Exception // NOPMD
	{
		this.usOrder = createSampleOrder();

		final GenericMockFactory taxClientFactory = (GenericMockFactory) Registry.getApplicationContext().getBean(
				"&taxClientFactory");
		taxClientFactory.setMockMode(true);

		taxClient = (TaxClient) taxClientFactory.getObject();
	}

	private CisOrder createSampleOrder()
	{
		final CisOrder usOrder = new CisOrder();
		final CisAddress shipToAddress = newAddress("1700 Broadway", "New York", "NY", "10019", "US");
		shipToAddress.setType(CisAddressType.SHIP_TO);

		final CisAddress originAddress = newAddress("1295 Charleston Rd", "Mountain View", "CA", "94043-1307", "US");
		originAddress.setType(CisAddressType.SHIP_FROM);

		final CisAddress acceptanceAddress = newAddress("2-24 29th St", "Fair Lawn", "NJ", "07410-3948", "US");
		acceptanceAddress.setType(CisAddressType.ADMIN_ORIGIN);

		final CisAddress billToAddress = newAddress("1700 Broadway", "New York", "NY", "10019", "US");
		billToAddress.setFirstName("TestFirstName");
		billToAddress.setLastName("TestLastName");
		billToAddress.setEmail("test@123.com");
		billToAddress.setType(CisAddressType.BILL_TO);

		final List<CisLineItem> lineItems = new ArrayList<CisLineItem>();
		final CisLineItem lineItem = new CisLineItem();
		lineItem.setId(new Integer(12));
		lineItem.setItemCode("100");
		lineItem.setQuantity(new Integer(1));
		lineItem.setUnitPrice(BigDecimal.TEN);
		lineItem.setProductDescription("Test item");
		lineItems.add(lineItem);

		final CisLineItem lineItem2 = new CisLineItem();
		lineItem2.setId(new Integer(34));
		lineItem2.setItemCode("200");
		lineItem2.setQuantity(new Integer(2));
		lineItem2.setUnitPrice(BigDecimal.TEN);
		lineItem2.setProductDescription("Test item");
		lineItems.add(lineItem2);

		usOrder.setId("UT" + new Date().getTime() + new Random(new Date().getTime()).nextInt(100));
		usOrder.getAddresses().add(shipToAddress);
		usOrder.getAddresses().add(originAddress);
		usOrder.getAddresses().add(acceptanceAddress);
		usOrder.getAddresses().add(billToAddress);
		usOrder.setLineItems(lineItems);

		usOrder.setCurrency("USD");
		return usOrder;

	}

	private CisAddress newAddress(final String addressLine1, final String city, final String state, final String zip,
			final String country)
	{
		final CisAddress address = new CisAddress();
		address.setAddressLine1(addressLine1);
		address.setAddressLine2("line2");
		address.setCity(city);
		address.setState(state);
		address.setZipCode(zip);
		address.setCountry(country);
		return address;
	}

	@Test
	public void shouldQuoteTax()
	{
		this.response = taxClient.quote("test", this.usOrder);
		Assert.assertEquals(CisDecision.ACCEPT, this.response.getResult().getDecision());
		Assert.assertNotNull(this.response.getResult().getId());
	}

	@Test
	public void shouldSubmitTax()
	{
		this.response = taxClient.post("test", this.usOrder);
		Assert.assertEquals(CisDecision.ACCEPT, this.response.getResult().getDecision());
	}

	@Test
	public void shouldInvoiceTax()
	{
		this.response = taxClient.invoice("test", this.usOrder);
		Assert.assertEquals(CisDecision.ACCEPT, this.response.getResult().getDecision());
	}

}
