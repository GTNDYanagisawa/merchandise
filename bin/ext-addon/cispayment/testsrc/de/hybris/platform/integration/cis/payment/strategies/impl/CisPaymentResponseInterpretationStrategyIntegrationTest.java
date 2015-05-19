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
package de.hybris.platform.integration.cis.payment.strategies.impl;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.enums.DecisionsEnum;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.integration.cis.payment.constants.CispaymentConstants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.testframework.TestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;

import com.hybris.cis.client.rest.payment.PaymentClient;
import com.hybris.commons.client.RestResponse;


@ManualTest
public class CisPaymentResponseInterpretationStrategyIntegrationTest extends ServicelayerTest
{
	@Resource
	private CisPaymentResponseInterpretationStrategy cisPaymentResponseInterpretation;
	@Resource
	private PaymentClient paymentClient;
	@Resource
	private BaseSiteService baseSiteService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/ondemandcommon/test/testAcceleratorData.csv", "UTF-8");
		final BaseSiteModel site = baseSiteService.getBaseSiteForUID("testSite");
		Assert.assertNotNull("no baseSite with uid 'testSite", site);
		site.setChannel(SiteChannel.B2C);
		baseSiteService.setCurrentBaseSite(site, false);
	}

	@Test
	public void testValidPaymentResponseInterpretation() throws Exception
	{
		final RestResponse hpfUrl = this.paymentClient.pspUrl("test");
		final String testClientRef = "JUNIT-TEST-CLIENT";
		final List<BasicNameValuePair> validFormData = CisPaymentIntegrationTestHelper.getValidFormDataMap();
		final Map<String, String> profileCreationResponse = CisPaymentIntegrationTestHelper.createNewProfile(hpfUrl.getLocation()
				.toASCIIString(), validFormData);
		final Map<String, PaymentErrorField> errors = new HashMap<String, PaymentErrorField>();

		final CreateSubscriptionResult subscriptionResult = cisPaymentResponseInterpretation.interpretResponse(
				profileCreationResponse, testClientRef, errors);
		Assert.assertNotNull(subscriptionResult);
		Assert.assertNotNull(subscriptionResult.getDecision());
		Assert.assertNotNull(subscriptionResult.getReasonCode());

		if (!subscriptionResult.getReasonCode().equals(CispaymentConstants.HYSTRIX_FALLBACK_ERR_CODE))
		{
			Assert.assertEquals(DecisionsEnum.ACCEPT.name(), subscriptionResult.getDecision());
		}
		else
		// Hystrix fallback unexpectedly executed. Assert Decision was set to ERROR
		{
			Assert.assertEquals(DecisionsEnum.ERROR.name(), subscriptionResult.getDecision());
		}
	}

	@Test
	public void testMissingPaymentDetailsResponseInterpretation() throws Exception
	{
		try
		{
			TestUtils.disableFileAnalyzer("expected stacktrace from hystrix");
			final Map<String, PaymentErrorField> errors = new HashMap<String, PaymentErrorField>();

			final RestResponse hpfUrl = this.paymentClient.pspUrl("test");
			final String testClientRef = "JUNIT-TEST-CLIENT";
			final List<BasicNameValuePair> formDataMissingDetails = CisPaymentIntegrationTestHelper.getFormDataMapMissingDetails();
			final Map<String, String> profileCreationResponse = CisPaymentIntegrationTestHelper.createNewProfile(hpfUrl
					.getLocation().toASCIIString(), formDataMissingDetails);

			final CreateSubscriptionResult subscriptionResult = cisPaymentResponseInterpretation.interpretResponse(
					profileCreationResponse, testClientRef, errors);
			Assert.assertNotNull(subscriptionResult);
			Assert.assertNotNull(subscriptionResult.getDecision());
			Assert.assertNotNull(subscriptionResult.getReasonCode());

			if (!subscriptionResult.getReasonCode().equals(CispaymentConstants.HYSTRIX_FALLBACK_ERR_CODE))
			{
				Assert.assertEquals(DecisionsEnum.REJECT.name(), subscriptionResult.getDecision());
			}
			else
			// Hystrix fallback unexpectedly executed. Assert Decision was set to ERROR
			{
				Assert.assertEquals(DecisionsEnum.ERROR.name(), subscriptionResult.getDecision());
			}
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}
}
