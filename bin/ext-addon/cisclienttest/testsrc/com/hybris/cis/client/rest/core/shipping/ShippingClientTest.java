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
package com.hybris.cis.client.rest.core.shipping;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.annotation.Resource;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hybris.cis.api.model.CisAddress;
import com.hybris.cis.api.model.CisAddressType;
import com.hybris.cis.api.shipping.model.CisPackage;
import com.hybris.cis.api.shipping.model.CisShipment;
import com.hybris.cis.api.shipping.model.CisWeightUnitsType;
import com.hybris.cis.client.rest.shipping.ShippingClient;
import com.hybris.commons.client.RestResponse;


/**
 * Validates that the "out-of-the-box" spring configuration will wire in the mock client if mock mode is set.
 * 
 */
@ManualTest
public class ShippingClientTest extends ServicelayerTest
{
	@Resource
	private ShippingClient shippingClient;

	private CisShipment cisShipment;

	private static final String CLIENT_REF = "randomClientRef";

	@Before
	public void initShipment()
	{
		this.cisShipment = new CisShipment();
		final CisAddress originAddress = new CisAddress();
		originAddress.setFirstName("firstName");
		originAddress.setLastName("lastName");
		originAddress.setPhone("1234567890");
		originAddress.setCompany("ABC");
		originAddress.setAddressLine1("502 MAIN ST N");
		originAddress.setZipCode("H2B1A0");
		originAddress.setCity("MONTREAL");
		originAddress.setState("QC");
		originAddress.setCountry("CA");
		originAddress.setType(CisAddressType.SHIP_FROM);
		this.cisShipment.getAddresses().add(originAddress);

		final CisAddress destAddress = new CisAddress();
		destAddress.setFirstName("firstName");
		destAddress.setLastName("lastName");
		destAddress.setPhone("1234567890");
		destAddress.setCompany("hybris");
		destAddress.setAddressLine1("502 MAIN ST N");
		destAddress.setZipCode("H2B1A0");
		destAddress.setCity("MONTREAL");
		destAddress.setState("QC");
		destAddress.setCountry("CA");
		destAddress.setType(CisAddressType.SHIP_TO);
		this.cisShipment.getAddresses().add(destAddress);

		this.cisShipment.setPackage(new CisPackage());
		this.cisShipment.getPackage().setInsuredValue("100");
		this.cisShipment.getPackage().setWidth("1");
		this.cisShipment.getPackage().setLength("1");
		this.cisShipment.getPackage().setHeight("1");
		this.cisShipment.getPackage().setUnit("cm");
		this.cisShipment.getPackage().setWeight("15");
		this.cisShipment.getPackage().setWeightUnit(CisWeightUnitsType.KG);

		this.cisShipment.setShipDate(new Date());

		this.cisShipment.setServiceMethod("DOM.EP");

	}

	//	@Test
	//	public void shouldtCreateShipment()
	//	{
	//		final RestResponse<CisShipment> response = shippingClient.createShipment("test", this.cisShipment);
	//		Assert.assertThat(response.getResult(), CoreMatchers.notNullValue());
	//		Assert.assertThat(response.getResult().getLabels(), CoreMatchers.notNullValue());
	//	}
	//
	//	@Test
	//	public void shouldGetLabel() throws URISyntaxException
	//	{
	//		final RestResponse<byte[]> response = shippingClient.getLabel("test", new URI(
	//				"http://localhost:9977/hybris-cis-mock-web/shipping/cisShippingMock/shipments/"
	//						+ "406951321983787352/labels/35ed62ae-fbc3-4287-9bea-9cff13f61a9e"));
	//		Assert.assertThat(response.getResult(), CoreMatchers.notNullValue());
	//	}

	@Test
	public void shouldCreateShipmentAndGetLabel() throws URISyntaxException
	{
		final RestResponse<CisShipment> response1 = this.shippingClient.createShipment(CLIENT_REF, this.cisShipment);
		Assert.assertEquals(Status.CREATED, response1.getStatus());

		final RestResponse<byte[]> response2 = this.shippingClient.getLabel(CLIENT_REF, new URI(response1.getResult().getLabels()
				.get(0).getHref()));
		Assert.assertThat(response2.getResult(), CoreMatchers.notNullValue());
	}
}
