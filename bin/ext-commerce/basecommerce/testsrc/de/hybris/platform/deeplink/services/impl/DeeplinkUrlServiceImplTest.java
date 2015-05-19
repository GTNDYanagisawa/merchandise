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
package de.hybris.platform.deeplink.services.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.deeplink.model.rules.DeeplinkUrlModel;
import de.hybris.platform.deeplink.model.rules.DeeplinkUrlRuleModel;
import de.hybris.platform.deeplink.services.DeeplinkUrlService;
import de.hybris.platform.deeplink.services.DeeplinkUrlService.LongUrlInfo;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DeeplinkUrlServiceImplTest extends ServicelayerBaseTest
{
	@Resource
	private DeeplinkUrlService deeplinkUrlService;
	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;

	private DeeplinkUrlModel deeplinkUrl;
	private DeeplinkUrlRuleModel deeplinkRule;

	@Before
	public void setUp() throws Exception
	{
		// Create DeeplinkUrl object
		deeplinkUrl = modelService.create(DeeplinkUrlModel.class);
		deeplinkUrl.setCode("foobar");
		deeplinkUrl.setBaseUrl("www.foobar.com/somestuff");
		deeplinkUrl.setName("FooBar");
		modelService.save(deeplinkUrl);

		// Create DeeplinkUrlRule object
		deeplinkRule = modelService.create(DeeplinkUrlRuleModel.class);
		deeplinkRule.setBaseUrlPattern(".*www\\.foobar\\.com.*");
		deeplinkRule.setDestUrlTemplate("www.foobar.com/product/$ctx.contextObject.code?deeplink=$ctx.deeplinkUrl.code");
		deeplinkRule.setUseForward(Boolean.FALSE);
		deeplinkRule.setApplicableType(typeService.getComposedTypeForCode("DeeplinkUrl"));
		deeplinkRule.setPriority(Integer.valueOf(1));
		modelService.save(deeplinkRule);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.deeplink.services.impl.DeeplinkUrlServiceImpl#generateShortUrl(de.hybris.platform.deeplink.model.rules.DeeplinkUrlModel, java.lang.Object)}
	 * .
	 */
	@Test
	public void testGenerateShortUrl()
	{
		final String shortUrl = deeplinkUrlService.generateShortUrl(deeplinkUrl, deeplinkUrl);
		assertThat(shortUrl, is(equalTo("www.foobar.com/somestuff?id=foobar-" + deeplinkUrl.getPk().toString())));
	}


	/**
	 * Test method for
	 * {@link de.hybris.platform.deeplink.services.impl.DeeplinkUrlServiceImpl#generateShortUrl(de.hybris.platform.deeplink.model.rules.DeeplinkUrlModel, java.lang.Object)}
	 * .
	 */
	@Test
	public void testGenerateShortUrlWithoutContextObject()
	{
		final String shortUrl = deeplinkUrlService.generateShortUrl(deeplinkUrl, null);
		assertThat(shortUrl, is(equalTo("www.foobar.com/somestuff?id=foobar")));
	}


	/**
	 * Test method for
	 * {@link de.hybris.platform.deeplink.services.impl.DeeplinkUrlServiceImpl#generateUrl(java.lang.String)}.
	 */
	@Test
	public void testGenerateUrl()
	{
		final LongUrlInfo generatedUrl = deeplinkUrlService.generateUrl("foobar-" + deeplinkUrl.getPk().toString());
		assertThat(generatedUrl, is(notNullValue()));
		assertThat(Boolean.valueOf(generatedUrl.isUseForward()), is(equalTo(Boolean.FALSE)));
		assertThat(generatedUrl.getUrl(),
				is(equalTo("www.foobar.com/product/" + deeplinkUrl.getCode() + "?deeplink=" + deeplinkUrl.getCode())));
	}
}
