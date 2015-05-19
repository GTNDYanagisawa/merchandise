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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Encoder;


/**
 * This Helper class contains methods useful for supporting integration tests in the Cis payment extension.
 */
public final class CisPaymentIntegrationTestHelper
{
	private static final Logger LOG = LoggerFactory.getLogger(CisPaymentIntegrationTestHelper.class);

	public static Map<String, String> createNewProfile(final String cybersourceUrl, final List<BasicNameValuePair> formData)
			throws Exception // NO PMD
	{
		final DefaultHttpClient client = new DefaultHttpClient();
		final HttpPost postRequest = new HttpPost(cybersourceUrl);
		postRequest.getParams().setBooleanParameter("http.protocol.handle-redirects", true);
		postRequest.setEntity(new UrlEncodedFormEntity(formData, "UTF-8"));
		// Execute HTTP Post Request
		final HttpResponse response = client.execute(postRequest);
		final Map<String, String> responseParams = new HashMap<String, String>();
		BufferedReader bufferedReader = null;
		try
		{
			bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

			while (bufferedReader.ready())
			{
				final String currentLine = bufferedReader.readLine();
				if (currentLine.contains("value=\"") && currentLine.contains("name=\""))
				{
					final String[] splittedLine = currentLine.split("name=\"");
					final String key = splittedLine[1].split("value=\"")[0].replace("\"", "").replace(">", "").trim();
					final String value = splittedLine[1].split("value=\"")[1].replace("\"", "").replace(">", "").trim();
					responseParams.put(key, value);
				}
			}
		}
		finally
		{
			IOUtils.closeQuietly(bufferedReader);
		}

		return responseParams;
	}

	public static List<BasicNameValuePair> getValidFormDataMap()
	{
		final Map<String, String> formData = insertSignature("5.00", "usd", "sale, subscription", "0.00", "20091206", "on-demand",
				"0", "false");

		formData.put("billTo_firstName", "Someone");
		formData.put("billTo_lastName", "Different");
		formData.put("billTo_street1", "1700 Broadway");
		formData.put("billTo_city", "New York");
		formData.put("billTo_state", "NY");
		formData.put("billTo_postalCode", "10019");
		formData.put("billTo_country", "US");
		formData.put("billTo_email", "some.different@hybris.com");
		formData.put("card_cardType", "001");
		formData.put("card_accountNumber", "4111111111111111");
		formData.put("card_expirationMonth", "01");
		formData.put("card_expirationYear", "2016");
		formData.put("card_cvNumber", "123");

		return convertMap(formData);
	}

	public static List<BasicNameValuePair> getFormDataMapMissingDetails()
	{
		final Map<String, String> formData = insertSignature("5.00", "usd", "sale, subscription", "0.00", "20091206", "on-demand",
				"0", "false");

		formData.put("billTo_firstName", "Someone");
		formData.put("billTo_lastName", "Different");
		formData.put("billTo_street1", "1700 Broadway");
		formData.put("billTo_city", "New York");
		formData.put("billTo_state", "NY");
		formData.put("billTo_postalCode", "10019");
		formData.put("billTo_country", "US");
		formData.put("billTo_email", "some.different@hybris.com");
		formData.put("card_cardType", "001");
		formData.put("card_accountNumber", "1111111111111111"); // Invalid Visa card number
		formData.put("card_expirationMonth", "01");
		formData.put("card_expirationYear", "2016");
		formData.put("card_cvNumber", "123");


		return convertMap(formData);
	}

	public static Map<String, String> insertSignature(String amount, String currency, final String orderPage_transactionType,
			final String subscriptionAmount, final String subscriptionStartDate, final String subscriptionFrequency,
			final String subscriptionNumberOfPayments, final String subscriptionAutomaticRenew)
	{
		final Map<String, String> result = new HashMap<String, String>();

		try
		{
			if (amount == null)
			{
				amount = "0.00";
			}
			if (currency == null)
			{
				currency = "usd";
			}

			final String time = String.valueOf(System.currentTimeMillis());
			final String merchantID = getMerchantID();
			final String data = merchantID + amount + currency + time + orderPage_transactionType;
			final String subscriptionData = subscriptionAmount + subscriptionStartDate + subscriptionFrequency
					+ subscriptionNumberOfPayments + subscriptionAutomaticRenew;
			final String serialNumber = getSerialNumber();

			result.put("amount", amount);
			result.put("orderPage_transactionType", orderPage_transactionType);
			result.put("currency", currency);
			result.put("orderPage_timestamp", time);
			result.put("merchantID", merchantID);
			result.put("orderPage_signaturePublic", getPublicDigest(data));
			result.put("orderPage_version", "7");
			result.put("orderPage_serialNumber", serialNumber);
			result.put("recurringSubscriptionInfo_amount", subscriptionAmount);
			result.put("recurringSubscriptionInfo_numberOfPayments", subscriptionNumberOfPayments);
			result.put("recurringSubscriptionInfo_frequency", subscriptionFrequency);
			result.put("recurringSubscriptionInfo_automaticRenew", subscriptionAutomaticRenew);
			result.put("recurringSubscriptionInfo_startDate", subscriptionStartDate);
			result.put("recurringSubscriptionInfo_signaturePublic", getPublicDigest(subscriptionData));

			return result;
		}
		catch (final Exception e)
		{
			LOG.warn("Unexpected exception while inserting signature.", e);
			return new HashMap<String, String>();
		}
	}

	public static String getMerchantID()
	{
		return "hybris1";
	}

	public static String getSharedSecret()
	{
		return "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC7UPjuToQMxnm0uakR8w6B7EorviwbKTBr38kk/rSlABI0J+j+d7qhW8SlBSQLD0cQZTVS4XyaNefWLwxsActih9JUSmiKvIu3Qxh+m6fptHnxjT+AiBC7sQiDddTbFkrhFWq+Tc2Obbp+6QyJbHU0rBK0fMv9VLnEvrooS2Q7QwIDAQAB";
	}

	public static String getSerialNumber()
	{
		return "3451441340300176056166";
	}

	public static String getPublicDigest(final String customValues) throws Exception
	{
		final String pub = getSharedSecret();
		final BASE64Encoder encoder = new BASE64Encoder();
		final Mac sha1Mac = Mac.getInstance("HmacSHA1");
		final SecretKeySpec publicKeySpec = new SecretKeySpec(pub.getBytes(), "HmacSHA1");
		sha1Mac.init(publicKeySpec);
		final byte[] publicBytes = sha1Mac.doFinal(customValues.getBytes());
		final String publicDigest = encoder.encodeBuffer(publicBytes);

		return publicDigest.replaceAll("\n", "");
	}

	public static List<BasicNameValuePair> convertMap(final Map<String, String> formData)
	{
		final Set<String> fields = formData.keySet();
		final List<BasicNameValuePair> resultList = new ArrayList<BasicNameValuePair>();
		for (final String field : fields)
		{
			resultList.add(new BasicNameValuePair(field, formData.get(field)));
		}

		return resultList;
	}

}
