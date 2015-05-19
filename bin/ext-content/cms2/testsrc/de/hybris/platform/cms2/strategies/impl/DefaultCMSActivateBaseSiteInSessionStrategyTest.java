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
package de.hybris.platform.cms2.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.exceptions.BaseSiteActivationException;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentMatcher;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class DefaultCMSActivateBaseSiteInSessionStrategyTest
{
	private DefaultCMSActivateBaseSiteInSessionStrategy strategy;

	@Mock
	private CMSSiteModel siteModel;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private CatalogModel catalog;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private CMSSiteService cmsSiteService;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		// Create the DefaultCMSActivateBaseSiteInSessionStrategy passing in the cmsSiteService to use
		strategy = new LocalDefaultCMSActivateBaseSiteInSessionStrategy(cmsSiteService);
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
		final Throwable expected = new IllegalStateException("blah");
		Mockito.doThrow(expected).when(catalogVersionService).setSessionCatalogVersions(Collections.EMPTY_SET);
		try
		{
			strategy.activate(siteModel);
			Assert.fail("Should call a setSessionCatalogVersions with empty collection ");
		}
		catch (final BaseSiteActivationException ike)
		{
			Assert.assertEquals(expected, ike.getCause());
			//
		}
	}

	@Test
	public void testThrowAnExceptionWhenNotEmptyContentCatalogs() throws BusinessException
	{
		final ContentCatalogModel contentCatalogOne = Mockito.mock(ContentCatalogModel.class);
		final CatalogVersionModel catalogVersion = Mockito.mock(CatalogVersionModel.class);

		BDDMockito.given(siteModel.getStores()).willReturn(Collections.EMPTY_LIST);
		BDDMockito.given(siteModel.getContentCatalogs()).willReturn(Arrays.asList(contentCatalogOne));
		BDDMockito.given(contentCatalogOne.getActiveCatalogVersion()).willReturn(catalogVersion);

		final Throwable expected = new IllegalStateException("blah");
		Mockito.doThrow(expected).when(catalogVersionService)
				.setSessionCatalogVersions(Mockito.argThat(new CollectionArgumentMatcher<CatalogVersionModel>(catalogVersion)));
		try
		{
			strategy.activate(siteModel);
			Assert.fail("Should call a setSessionCatalogVersions with content catalogs from site ");
		}
		catch (final BaseSiteActivationException ike)
		{
			Assert.assertEquals(expected, ike.getCause());
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
	public void testShouldThrowAnExceptionForFewStoresForSite() throws BusinessException
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
		Mockito
				.doThrow(expected)
				.when(catalogVersionService)
				.setSessionCatalogVersions(
						Mockito.argThat(new CollectionArgumentMatcher<CatalogVersionModel>(catalogVersionOne, catalogVersionTwo)));
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


	@Test
	public void testNoCurrentCMSSiteAvailableAssureNoSetCurrentSiteCalledNoCMSSiteSetExpected() throws BusinessException
	{

		BDDMockito.given(siteModel.getStores()).willReturn(Collections.EMPTY_LIST);
		BDDMockito.given(cmsSiteService.getCurrentSite()).willReturn(null);
		BDDMockito.given(Boolean.valueOf(cmsSiteService.hasCurrentCatalogVersion())).willReturn(Boolean.TRUE);
		throwExceptionIfInteraction();

		strategy.activate(siteModel);
	}



	@Test
	public void testNoCurrentCMSSiteAvailableAndHasNotCurrentCatalogVersionNoCMSSiteSetExpected() throws BusinessException
	{

		BDDMockito.given(siteModel.getStores()).willReturn(Collections.EMPTY_LIST);
		BDDMockito.given(cmsSiteService.getCurrentSite()).willReturn(null);
		BDDMockito.given(Boolean.valueOf(cmsSiteService.hasCurrentCatalogVersion())).willReturn(Boolean.FALSE);
		throwExceptionIfInteraction();

		strategy.activate(siteModel);

	}

	@Test
	public void testCurrentCMSSiteNotEqualsGivenSiteNoCMSSiteSetExpected() throws BusinessException
	{
		final CMSSiteModel site = Mockito.mock(CMSSiteModel.class);

		BDDMockito.given(siteModel.getStores()).willReturn(Collections.EMPTY_LIST);
		BDDMockito.given(cmsSiteService.getCurrentSite()).willReturn(site);
		BDDMockito.given(site.getUid()).willReturn("site");
		BDDMockito.given(siteModel.getUid()).willReturn("other_site");

		BDDMockito.given(Boolean.valueOf(cmsSiteService.hasCurrentCatalogVersion())).willReturn(Boolean.TRUE);

		throwExceptionIfInteraction();

		strategy.activate(siteModel);
	}

	@Test
	public void testCurrentCMSSiteNotEqualsGivenSiteWith() throws BusinessException
	{
		final CMSSiteModel site = Mockito.mock(CMSSiteModel.class);
		final CatalogVersionModel version = Mockito.mock(CatalogVersionModel.class);


		BDDMockito.given(siteModel.getStores()).willReturn(Collections.EMPTY_LIST);
		BDDMockito.given(cmsSiteService.getCurrentSite()).willReturn(site);
		BDDMockito.given(site.getUid()).willReturn("site");
		BDDMockito.given(siteModel.getUid()).willReturn("other_site");


		BDDMockito.given(siteModel.getDefaultCatalog()).willReturn(catalog);
		BDDMockito.given(catalog.getId()).willReturn("id");
		BDDMockito.given(catalog.getActiveCatalogVersion().getVersion()).willReturn("version");

		BDDMockito.given(catalogVersionService.getCatalogVersion("id", "version")).willReturn(version);

		BDDMockito.given(Boolean.valueOf(cmsSiteService.hasCurrentCatalogVersion())).willReturn(Boolean.TRUE);
		final Throwable expected = new CMSItemNotFoundException("should not be called");
		BDDMockito.doThrow(expected).when(cmsSiteService).setCurrentCatalogVersion(version);
		try
		{
			strategy.activate(siteModel);
			Assert.fail("Should throw an exception if setCurrentCatalogVersion called ");
		}
		catch (final BaseSiteActivationException ike)
		{
			Assert.assertEquals(expected, ike.getCause());
			//
		}
	}

	@Test
	public void testCurrentCMSSiteEqualsGivenSiteNoCMSSiteSetExpected() throws BusinessException
	{
		final CMSSiteModel site = Mockito.mock(CMSSiteModel.class);

		BDDMockito.given(siteModel.getStores()).willReturn(Collections.EMPTY_LIST);
		BDDMockito.given(cmsSiteService.getCurrentSite()).willReturn(site);
		BDDMockito.given(site.getUid()).willReturn("site");
		BDDMockito.given(siteModel.getUid()).willReturn("site");

		BDDMockito.given(Boolean.valueOf(cmsSiteService.hasCurrentCatalogVersion())).willReturn(Boolean.TRUE);
		throwExceptionIfInteraction();

		strategy.activate(siteModel);

	}

	/**
	 * Assures if no interaction with {@link CMSSiteService#setCurrentSite(CMSSiteModel)} and
	 * {@link CMSSiteService#setCurrentCatalogVersion(CatalogVersionModel)} was recorded. It is up to you to handle this
	 * in the test
	 */
	private void throwExceptionIfInteraction() throws CMSItemNotFoundException
	{
		BDDMockito.doThrow(new IllegalStateException("should not be called")).when(cmsSiteService).setCurrentSite(siteModel);
		BDDMockito.doThrow(new IllegalStateException("should not be called")).when(cmsSiteService)
				.setCurrentCatalogVersion(Mockito.any(CatalogVersionModel.class));
	}

	private class CollectionArgumentMatcher<T> extends ArgumentMatcher<Collection>
	{

		private final T[] elems;

		CollectionArgumentMatcher(final T... elems)
		{
			this.elems = elems;
		}

		@Override
		public boolean matches(final Object argument)
		{
			return argument instanceof Collection && //
					((Collection) argument).size() == elems.length && //
					containsAll((Collection) argument);
		}

		private boolean containsAll(final Collection collection)
		{
			for (final T singleElem : elems)
			{
				if (!collection.contains(singleElem))
				{
					return false;
				}
			}
			return true;
		}
	}

	private static class LocalDefaultCMSActivateBaseSiteInSessionStrategy extends DefaultCMSActivateBaseSiteInSessionStrategy
	{
		private final CMSSiteService _cmsSiteService;

		private LocalDefaultCMSActivateBaseSiteInSessionStrategy(final CMSSiteService cmsSiteService)
		{
			_cmsSiteService = cmsSiteService;
		}

		@Override
		public CMSSiteService lookupCmsSiteService()
		{
			return _cmsSiteService;
		}
	}
}
