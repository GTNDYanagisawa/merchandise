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
 *
 *
 */
package de.hybris.platform.commercewebservicescommons.cache;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CommerceCacheKeyGeneratorTest
{
	private static final String BASE_SITE_UID = "baseSiteName";
	private static final String ANOTHER_BASE_SITE_UID = "baseSiteName1";
	private static final String LANGUAGE_ISOCODE = "en";
	private static final String ANOTHER_LANGUAGE_ISOCODE = "de";
	private static final String CURRENCY_ISOCODE = "USD";
	private static final String ANOTHER_CURRENCY_ISOCODE = "EUR";
	private static final String USER_UID = "test@test.com";
	private static final String ANOTHER_USER_UID = "test1@test.com";
	private static final String stringValue = "key1";
	private static final Integer intValue = Integer.valueOf(1);
	private static final Double doubleValue = Double.valueOf("1");
	private static final Boolean booleanValue = Boolean.TRUE;

	private final CommerceCacheKeyGenerator keyGenerator = new CommerceCacheKeyGenerator();

	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private BaseSiteModel baseSite;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private LanguageModel language;
	@Mock
	private CurrencyModel currency;
	@Mock
	private UserService userService;
	@Mock
	private UserModel user;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		keyGenerator.setBaseSiteService(baseSiteService);
		keyGenerator.setCommonI18NService(commonI18NService);
		keyGenerator.setUserService(userService);

		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSite);
		given(baseSite.getUid()).willReturn(BASE_SITE_UID);
		given(commonI18NService.getCurrentCurrency()).willReturn(currency);
		given(currency.getIsocode()).willReturn(CURRENCY_ISOCODE);
		given(commonI18NService.getCurrentLanguage()).willReturn(language);
		given(language.getIsocode()).willReturn(LANGUAGE_ISOCODE);
		given(userService.getCurrentUser()).willReturn(user);
		given(user.getUid()).willReturn(USER_UID);
	}

	@Test
	public void testGenerate()
	{
		final Object key1 = keyGenerator.generate(false, false, stringValue, intValue);
		final Object key2 = keyGenerator.generate(false, false, stringValue, intValue);
		final Object key3 = keyGenerator.generate(false, false, stringValue, doubleValue);

		assertTrue(key1.equals(key2));
		assertFalse(key1.equals(key3));
	}

	@Test
	public void testGenerateMethodFromInterface()
	{
		final Object key1 = keyGenerator.generate(null, (Method) null, stringValue, intValue);
		final Object key2 = keyGenerator.generate(false, false, stringValue, intValue);

		assertTrue(key1.equals(key2));
	}

	@Test
	public void testGenerateWithNullValue()
	{
		final Object key2 = keyGenerator.generate(false, false, (Integer) null);
		final Object key1 = keyGenerator.generate(false, false, intValue);
		assertFalse(key1.equals(key2));
	}

	@Test
	public void testDifferentTypes()
	{
		final Object key1 = keyGenerator.generate(false, false, "1");
		final Object key2 = keyGenerator.generate(false, false, Integer.valueOf(1));
		final Object key3 = keyGenerator.generate(false, false, Long.valueOf(1));

		assertFalse(key1.equals(key2));
		assertFalse(key1.equals(key3));
		assertFalse(key2.equals(key3));
	}

	@Test
	public void testDifferentBaseSite()
	{
		final Object key1 = keyGenerator.generate(false, false, doubleValue);
		given(baseSite.getUid()).willReturn(ANOTHER_BASE_SITE_UID);
		final Object key2 = keyGenerator.generate(false, false, doubleValue);

		assertFalse(key1.equals(key2));
	}

	@Test
	public void testDifferentLanguages()
	{
		final Object key1 = keyGenerator.generate(false, false, booleanValue);
		given(language.getIsocode()).willReturn(ANOTHER_LANGUAGE_ISOCODE);
		final Object key2 = keyGenerator.generate(false, false, booleanValue);

		assertFalse(key1.equals(key2));
	}

	@Test
	public void testUserFlag()
	{
		final Object key1 = keyGenerator.generate(true, false, stringValue, intValue);
		final Object key2 = keyGenerator.generate(true, false, stringValue, intValue);
		final Object key3 = keyGenerator.generate(false, false, stringValue, intValue);

		assertTrue(key1.equals(key2));
		assertFalse(key1.equals(key3));
	}

	@Test
	public void testDifferentUserUid()
	{
		final Object key1 = keyGenerator.generate(true, false, stringValue, intValue);
		given(user.getUid()).willReturn(ANOTHER_USER_UID);
		final Object key2 = keyGenerator.generate(true, false, stringValue, intValue);

		assertFalse(key1.equals(key2));
	}

	@Test
	public void testCurrencyFlag()
	{
		final Object key1 = keyGenerator.generate(false, true, intValue);
		final Object key2 = keyGenerator.generate(false, true, intValue);
		final Object key3 = keyGenerator.generate(false, false, intValue);

		assertTrue(key1.equals(key2));
		assertFalse(key1.equals(key3));
	}

	@Test
	public void testDifferentCurrency()
	{
		final Object key1 = keyGenerator.generate(false, true, intValue);
		given(currency.getIsocode()).willReturn(ANOTHER_CURRENCY_ISOCODE);
		final Object key2 = keyGenerator.generate(false, true, intValue);

		assertFalse(key1.equals(key2));
	}
}
