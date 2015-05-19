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
package de.hybris.platform.basecommerce.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.exceptions.BaseSiteActivationException;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class DefaultActivateBaseSiteInSessionStrategyTest
{
	private final DefaultActivateBaseSiteInSessionStrategy strategy = new DefaultActivateBaseSiteInSessionStrategy();

	@Mock
	private BaseSiteModel siteModel;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		strategy.setCatalogVersionService(catalogVersionService);
	}

	@Test(expected = BaseSiteActivationException.class)
	public void testNullSite()
	{
		strategy.activate(null);
	}


	@Test
	public void testThrowAnExceptionWhenNoStoreForSite()
	{
		BDDMockito.given(siteModel.getStores()).willReturn(Collections.EMPTY_LIST);
		final Throwable exception = new IllegalStateException("blah");
		Mockito.doThrow(exception).when(catalogVersionService).setSessionCatalogVersions(Collections.EMPTY_SET);
		try
		{
			strategy.activate(siteModel);
			Assert.fail("Should call a setSessionCatalogVersions with empty collection ");
		}
		catch (final BaseSiteActivationException ike)
		{
			Assert.assertEquals(exception, ike.getCause());
			//
		}
	}


	@Test
	public void testFewStoresForSiteNoActiveCatalogVersion()
	{
		final BaseStoreModel storeOne = Mockito.mock(BaseStoreModel.class);
		final CatalogModel storeCatalogOne = Mockito.mock(CatalogModel.class);
		final BaseStoreModel storeTwo = Mockito.mock(BaseStoreModel.class);

		BDDMockito.given(storeOne.getCatalogs()).willReturn(Arrays.asList(storeCatalogOne));
		BDDMockito.given(siteModel.getStores()).willReturn(Arrays.asList(storeOne, storeTwo));

		try
		{
			strategy.activate(siteModel);
			Assert.fail("No active catalog version for at least one catalog should throw a  ModelNotFoundException");
		}
		catch (final BaseSiteActivationException mnf)
		{
			Assert.assertEquals(ModelNotFoundException.class, mnf.getCause().getClass());
			//
		}
	}

	@Test
	public void testShouldThrowAnExceptionForFewStoresForSite()
	{
		final BaseStoreModel storeOne = Mockito.mock(BaseStoreModel.class);
		final CatalogModel storeCatalogOne = Mockito.mock(CatalogModel.class);
		final CatalogVersionModel catalogVersionOne = Mockito.mock(CatalogVersionModel.class);
		final BaseStoreModel storeTwo = Mockito.mock(BaseStoreModel.class);
		final CatalogModel storeCatalogTwo = Mockito.mock(CatalogModel.class);
		final CatalogVersionModel catalogVersionTwo = Mockito.mock(CatalogVersionModel.class);

		BDDMockito.given(storeCatalogOne.getActiveCatalogVersion()).willReturn(catalogVersionOne);
		BDDMockito.given(storeCatalogTwo.getActiveCatalogVersion()).willReturn(catalogVersionTwo);

		BDDMockito.given(storeOne.getCatalogs()).willReturn(Arrays.asList(storeCatalogOne));
		BDDMockito.given(storeTwo.getCatalogs()).willReturn(Arrays.asList(storeCatalogTwo));

		BDDMockito.given(siteModel.getStores()).willReturn(Arrays.asList(storeOne, storeTwo));

		final Throwable expected = new IllegalStateException("blah");
		Mockito.doThrow(expected).when(catalogVersionService)
				.setSessionCatalogVersions(Mockito.argThat(new ArgumentMatcher<Collection>()
				{
					@Override
					public boolean matches(final Object argument)
					{
						return argument instanceof Collection && //
								((Collection) argument).size() == 2 && //
								((Collection) argument).contains(catalogVersionOne) && //
								((Collection) argument).contains(catalogVersionTwo);
					}
				}));

		try
		{
			strategy.activate(siteModel);
			Assert.fail("Should throw an exception for given catalog versions");
		}
		catch (final BaseSiteActivationException ike)
		{
			Assert.assertEquals(expected, ike.getCause());
			//
		}

	}
}
