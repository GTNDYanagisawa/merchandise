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
package de.hybris.platform.commerceservices.i18n;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.i18n.impl.CommerceLanguageResolver;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CommerceLanguageResolverTest
{

	private static final String COMMON_ISO = "commonOne";
	private static final String COMMERCE_ISO = "commerceOne";

	private final CommerceLanguageResolver resolver = new CommerceLanguageResolver();

	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;

	@Mock
	private CommonI18NService commonI18NService;

	private final LanguageModel commerceOneLangauge = new LanguageModel();

	private final LanguageModel commonOneLangauge = new LanguageModel();

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		resolver.setCommerceCommonI18NService(commerceCommonI18NService);
		resolver.setCommonI18NService(commonI18NService);

		commerceOneLangauge.setIsocode(COMMERCE_ISO);
		commonOneLangauge.setIsocode(COMMON_ISO);

		BDDMockito.when(commonI18NService.getAllLanguages()).thenReturn(Arrays.asList(commerceOneLangauge));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullCode()
	{
		resolver.getLanguage(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyCode()
	{
		resolver.getLanguage("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsoCodeNotExistsInCommerce()
	{
		resolver.getLanguage(COMMON_ISO);
	}

	@Test
	public void testIsoCodeExistsInCommon()
	{
		BDDMockito.when(commonI18NService.getAllLanguages()).thenReturn(Arrays.asList(commonOneLangauge));
		Assert.assertEquals(commonOneLangauge, resolver.getLanguage(COMMON_ISO));
	}


	@Test
	public void testIsoCodeExistsInCommerce()
	{
		Assert.assertEquals(commerceOneLangauge, resolver.getLanguage(COMMERCE_ISO));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsoCodeNotExists()
	{
		resolver.getLanguage("blup");
	}
}
