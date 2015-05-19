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
package de.hybris.platform.commercesearch.searchandizing;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService.Executor;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.Before;
import org.junit.Ignore;

import javax.annotation.Resource;
import java.util.Collections;


@Ignore
public abstract class AbstractSearchandisingIntegrationTest extends ServicelayerTest
{

	protected static final String CATALOG_ID = "electronicsProductCatalog";
	protected static final String BASE_STORE_UID = "electronics";
	protected final String BASE_SITE_UID = "electronics";

	@Resource
	protected ImpersonationService impersonationService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private UserService userService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private BaseStoreService baseStoreService;

	protected BaseSiteModel site;
	protected CatalogVersionModel catalaogVersion;
	protected LanguageModel language;
	protected CurrencyModel currency;
	protected UserModel user;


	protected <R, T extends Throwable> R executeInContext(final Executor<R, T> executor) throws T
	{
		final ImpersonationContext context = new ImpersonationContext();
		context.setSite(site);
		context.setUser(user);
		context.setCatalogVersions(Collections.singleton(catalaogVersion));
		context.setLanguage(language);
		context.setCurrency(currency);
		return impersonationService.executeInContext(context, executor);
	}

	@Before
	public void setUp()
	{
		site = baseSiteService.getBaseSiteForUID(BASE_SITE_UID);
		catalaogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, "Online");
		language = site.getDefaultLanguage();
		final BaseStoreModel store = baseStoreService.getBaseStoreForUid(BASE_STORE_UID);
		currency = store.getDefaultCurrency();
		user = userService.getUserForUID("anonymous");
	}

}
