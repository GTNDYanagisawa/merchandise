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
package com.hybris.cis.client.rest.core.tax;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.servicelayer.ServicelayerTest;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hybris.cis.api.model.CisDecision;
import com.hybris.cis.api.model.CisOrder;
import com.hybris.cis.api.tax.model.CisTaxDoc;
import com.hybris.cis.api.test.util.TestUtils;
import com.hybris.cis.client.rest.tax.TaxClient;
import com.hybris.commons.client.RestResponse;


/**
 * Validates that the "out-of-the-box" spring configuration will wire in the mock client if mock mode is set.
 */
@ManualTest
public class TaxClientTest extends ServicelayerTest
{
	@Resource
	private TaxClient taxClient;

	private CisOrder usOrder;
	private RestResponse<CisTaxDoc> response;


	@Before
	public void before() throws Exception // NOPMD
	{
		this.usOrder = TestUtils.createSampleOrder();
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
