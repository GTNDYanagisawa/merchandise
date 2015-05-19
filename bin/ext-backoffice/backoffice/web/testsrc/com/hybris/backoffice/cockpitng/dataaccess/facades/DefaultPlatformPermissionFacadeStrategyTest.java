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
package com.hybris.backoffice.cockpitng.dataaccess.facades;

import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.*;

import de.hybris.platform.servicelayer.user.UserService;
import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.hybris.backoffice.cockpitng.dataaccess.facades.permissions.DefaultPlatformPermissionFacadeStrategy;
import sun.util.calendar.LocalGregorianCalendar;


public class DefaultPlatformPermissionFacadeStrategyTest
{
	@Mock
	private transient CatalogTypeService catalogTypeService;
	@Mock
	private transient CommonI18NService commonI18NService;
    @Mock
    private UserService userService;
    @Mock
    private UserModel user;


	private final transient Set<Locale> readableLanguages = new HashSet<Locale>();
	private transient LanguageModel userProfileLanEnglish = new LanguageModel();
	private transient CatalogVersionModel catalog;
	private transient ProductModel product;
	private transient Locale englishLocale;
	private transient Locale germanLocale;
	private static final String ENGLISH_ISO_CODE = "en";
	private static final String GERMAN_ISO_CODE = "de";

	private final transient DefaultPlatformPermissionFacadeStrategy permissionFacade = new DefaultPlatformPermissionFacadeStrategy()
	{
		@Override
		public Set<Locale> getAllReadableLocalesForCurrentUser()
		{
			final Locale english = new Locale(ENGLISH_ISO_CODE);
			readableLanguages.add(english);
			return readableLanguages;
		}

		@Override
		public Set<Locale> getAllWritableLocalesForCurrentUser()
		{
			final Locale english = new Locale(ENGLISH_ISO_CODE);
			readableLanguages.add(english);
			return readableLanguages;
		}
	};


    @Before
	public void setUp()
	{
		catalogTypeService = Mockito.mock(CatalogTypeService.class);
		commonI18NService = Mockito.mock(CommonI18NService.class);
        userService = Mockito.mock(UserService.class);
        user = Mockito.mock(UserModel.class);
		permissionFacade.setCatalogTypeService(catalogTypeService);
		permissionFacade.setCommonI18NService(commonI18NService);
        permissionFacade.setUserService(userService);

		catalog = new CatalogVersionModel();

        final LanguageModel catalogVersionLanEnglish = new LanguageModel();

		catalogVersionLanEnglish.setIsocode(ENGLISH_ISO_CODE);

		final LanguageModel catalogVersionLanGerman = new LanguageModel();
		catalogVersionLanGerman.setIsocode(GERMAN_ISO_CODE);

		final Collection<LanguageModel> allLang = new ArrayList<LanguageModel>();
		allLang.add(catalogVersionLanEnglish);
		allLang.add(catalogVersionLanGerman);

		catalog.setLanguages(allLang);

		product = new ProductModel();
		product.setCatalogVersion(catalog);

		Mockito.when(Boolean.valueOf(catalogTypeService.isCatalogVersionAwareModel(product))).thenReturn(Boolean.TRUE);
		Mockito.when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(product)).thenReturn(catalog);
        Mockito.when(userService.getCurrentUser()).thenReturn(user);
        Mockito.when(userService.isAdmin(user)).thenReturn(false);

		englishLocale = new Locale(ENGLISH_ISO_CODE);
		germanLocale = new Locale(GERMAN_ISO_CODE);

		Mockito.when(commonI18NService.getLocaleForLanguage(userProfileLanEnglish)).thenReturn(englishLocale);
		Mockito.when(commonI18NService.getLocaleForLanguage(catalogVersionLanEnglish)).thenReturn(englishLocale);
		Mockito.when(commonI18NService.getLocaleForLanguage(catalogVersionLanGerman)).thenReturn(germanLocale);
	}

	@Test
	public void testGetReadableLocalesForInstance()
	{
		final Set<Locale> expectedLocales = permissionFacade.getReadableLocalesForInstance(product);
		Assert.assertNotNull(expectedLocales);
		Assert.assertTrue(expectedLocales.contains(englishLocale));
	}

	@Test
	public void testGetWritableLocalesForInstance()
	{
		final Set<Locale> expectedLocales = permissionFacade.getWritableLocalesForInstance(product);
		Assert.assertNotNull(expectedLocales);
		Assert.assertTrue(expectedLocales.contains(englishLocale));
	}

	@Test
	public void testCatalogVersionAndReadableLanguageAreNull()
	{
		final DefaultPlatformPermissionFacadeStrategy permissionFacade = new DefaultPlatformPermissionFacadeStrategy()
		{
			@Override
			public Set<Locale> getAllReadableLocalesForCurrentUser()
			{
				return null;
			}

			@Override
			public Set<Locale> getAllWritableLocalesForCurrentUser()
			{
				return null;
			}
		};

		Mockito.when(Boolean.valueOf(catalogTypeService.isCatalogVersionAwareModel(product))).thenReturn(Boolean.FALSE);
		permissionFacade.setCatalogTypeService(catalogTypeService);

		final Set<Locale> expectedReadableLocales = permissionFacade.getReadableLocalesForInstance(product);
		Assert.assertTrue(CollectionUtils.isEmpty(expectedReadableLocales));

		final Set<Locale> expectedWritableLocales = permissionFacade.getReadableLocalesForInstance(product);
		Assert.assertTrue(CollectionUtils.isEmpty(expectedWritableLocales));

	}

	@Test
	public void testCatalogVersionLanguagesNotNullAndAllLanguageIsNull()
	{
		final DefaultPlatformPermissionFacadeStrategy permissionFacade = new DefaultPlatformPermissionFacadeStrategy()
		{
			@Override
			public Set<Locale> getAllReadableLocalesForCurrentUser()
			{
				return null;
			}

			@Override
			public Set<Locale> getAllWritableLocalesForCurrentUser()
			{
				return null;
			}

			@Override
			protected Set<Locale> getLocalesForLanguage(final Collection<LanguageModel> languages)
			{
				final Set<Locale> localesForLanguage = new HashSet<Locale>();
				localesForLanguage.add(englishLocale);
				localesForLanguage.add(germanLocale);

				return localesForLanguage;
			}
		};

		permissionFacade.setCatalogTypeService(catalogTypeService);
		permissionFacade.setCommonI18NService(commonI18NService);

		final Set<Locale> expectedReadableLocales = permissionFacade.getReadableLocalesForInstance(product);
		Assert.assertNotNull(expectedReadableLocales);
		Assert.assertTrue(expectedReadableLocales.size() == 2);

		final Set<Locale> expectedWritableLocales = permissionFacade.getWritableLocalesForInstance(product);
		Assert.assertNotNull(expectedWritableLocales);
		Assert.assertTrue(expectedWritableLocales.size() == 2);
	}

	@Test
	public void testCatalogVersionLanguagesNullAndAllLanguageNotNull()
	{
		Mockito.when(Boolean.valueOf(catalogTypeService.isCatalogVersionAwareModel(product))).thenReturn(Boolean.FALSE);

		final Set<Locale> expectedReadableLocales = permissionFacade.getReadableLocalesForInstance(product);
		Assert.assertNotNull(expectedReadableLocales);
		Assert.assertTrue(expectedReadableLocales.size() == 1);

		final Set<Locale> expectedWritableLocales = permissionFacade.getWritableLocalesForInstance(product);
		Assert.assertNotNull(expectedWritableLocales);
		Assert.assertTrue(expectedWritableLocales.size() == 1);
	}

    @Test
    public void testGetReadableLocalesForInstanceAsAdmin()
    {
        final Set<Locale> locales = new HashSet<Locale>();
        locales.add(Locale.ENGLISH);
        locales.add(Locale.GERMAN);
        locales.add(Locale.JAPAN);

        final DefaultPlatformPermissionFacadeStrategy permissionFacade = new DefaultPlatformPermissionFacadeStrategy()
        {
            @Override
            public Set<Locale> getAllReadableLocalesForCurrentUser()
            {
                return locales;
            }

            @Override
            public Set<Locale> getAllWritableLocalesForCurrentUser()
            {
                return locales;
            }
        };

        permissionFacade.setCatalogTypeService(catalogTypeService);
        permissionFacade.setCommonI18NService(commonI18NService);
        permissionFacade.setUserService(userService);

        Mockito.when(userService.isAdmin(user)).thenReturn(true);

        LanguageModel german = new LanguageModel();
        german.setIsocode(GERMAN_ISO_CODE);
        catalog.setLanguages(Collections.singletonList(german));

        final Set<Locale> readableLocalesForInstance = permissionFacade.getReadableLocalesForInstance(product);

        //Even though catalog is restricted for geramn language only - in case of admin, facade returns all readable languages of the admin user - all defined. In this test - English
        Assert.assertTrue(readableLocalesForInstance.containsAll(locales));
    }

}
