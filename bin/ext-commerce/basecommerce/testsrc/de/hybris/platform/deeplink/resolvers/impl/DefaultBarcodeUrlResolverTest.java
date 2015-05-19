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
package de.hybris.platform.deeplink.resolvers.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.PK.PKException;
import de.hybris.platform.deeplink.model.rules.DeeplinkUrlModel;
import de.hybris.platform.deeplink.pojo.DeeplinkUrlInfo;
import de.hybris.platform.deeplink.resolvers.BarcodeUrlResolver;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;


/**
 * The Class DefaultBarcodeUrlResolverTest.
 */
public class DefaultBarcodeUrlResolverTest extends ServicelayerTransactionalTest
{

	private final StringBuilder token = new StringBuilder();
	private BarcodeUrlResolver resolver;
	private ModelService modelService;
	private DeeplinkUrlModel deeplinkUrl;

	/**
	 * Sets up test.
	 * 
	 * @throws Exception
	 *            the exception
	 */
	@Before
	public void setUp() throws Exception
	{
		// Import core data
		createCoreData();

		// Create DeeplinkUrl object
		deeplinkUrl = getModelService().create(DeeplinkUrlModel.class);
		deeplinkUrl.setCode("foobar");
		deeplinkUrl.setBaseUrl("www.foobar.com");
		deeplinkUrl.setName("FooBar");
		modelService.save(deeplinkUrl);
	}

	/**
	 * Test method for.
	 * 
	 * {@link de.hybris.platform.deeplink.resolvers.impl.DefaultBarcodeUrlResolver#resolve(java.lang.String)}.
	 */
	@Test
	public void testResolve()
	{
		// Create fake token
		token.append(deeplinkUrl.getCode()).append("-").append(deeplinkUrl.getPk());

		final DeeplinkUrlInfo deeplinkUrlInfo = getResolver().resolve(token.toString());
		assertThat("Context object", (DeeplinkUrlModel) deeplinkUrlInfo.getContextObject(), is(equalTo(deeplinkUrl)));
		assertThat("DeeplinkUrl object", deeplinkUrlInfo.getDeeplinkUrl(), is(equalTo(deeplinkUrl)));
	}

	/**
	 * Test method for.
	 * 
	 * {@link de.hybris.platform.deeplink.resolvers.impl.DefaultBarcodeUrlResolver#resolve(java.lang.String)}.
	 */
	@Test
	public void testResolveWithoutPK()
	{
		// Create fake token
		token.append(deeplinkUrl.getCode());

		final DeeplinkUrlInfo deeplinkUrlInfo = getResolver().resolve(token.toString());
		assertThat("Context object", deeplinkUrlInfo.getContextObject(), is(nullValue()));
		assertThat("DeeplinkUrl object", deeplinkUrlInfo.getDeeplinkUrl(), is(equalTo(deeplinkUrl)));
	}

	/**
	 * Test method for.
	 * 
	 * {@link de.hybris.platform.deeplink.resolvers.impl.DefaultBarcodeUrlResolver#resolve(java.lang.String)}.
	 */
	@Test
	public void testResolveWithDeeplinkUrlNotFound()
	{
		// Create fake token
		token.append("SomeFakeCode").append("-").append(deeplinkUrl.getPk());

		final DeeplinkUrlInfo deeplinkUrlInfo = getResolver().resolve(token.toString());
		assertThat("Context object", (DeeplinkUrlModel) deeplinkUrlInfo.getContextObject(), is(equalTo(deeplinkUrl)));
		assertThat("DeeplinkUrl object", deeplinkUrlInfo.getDeeplinkUrl(), is(nullValue()));
	}

	/**
	 * Test method for.
	 * 
	 * {@link de.hybris.platform.deeplink.resolvers.impl.DefaultBarcodeUrlResolver#resolve(java.lang.String)}.
	 */
	@Test
	public void testResolveWithContextObjectNotFound()
	{
		// Create fake token
		token.append(deeplinkUrl.getCode()).append("-").append("8796093808841");

		final DeeplinkUrlInfo deeplinkUrlInfo = getResolver().resolve(token.toString());
		assertThat("Context object", deeplinkUrlInfo.getContextObject(), is(nullValue()));
		assertThat("DeeplinkUrl object", deeplinkUrlInfo.getDeeplinkUrl(), is(equalTo(deeplinkUrl)));
	}

	/**
	 * Test method for.
	 * 
	 * {@link de.hybris.platform.deeplink.resolvers.impl.DefaultBarcodeUrlResolver#resolve(java.lang.String)}.
	 */
	@Test(expected = PKException.class)
	public void testResolveWithBadPK()
	{
		// Create fake token
		token.append(deeplinkUrl.getCode()).append("-").append("foobar12345");

		getResolver().resolve(token.toString());
	}

	private ModelService getModelService()
	{
		if (modelService == null)
		{
			modelService = (ModelService) Registry.getApplicationContext().getBean("modelService");
		}
		return modelService;
	}

	private BarcodeUrlResolver getResolver()
	{
		if (resolver == null)
		{
			resolver = (BarcodeUrlResolver) Registry.getApplicationContext().getBean("barcodeUrlResolver");
		}
		return resolver;
	}
}
