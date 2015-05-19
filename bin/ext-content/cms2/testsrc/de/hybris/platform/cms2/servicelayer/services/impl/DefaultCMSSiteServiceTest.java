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
package de.hybris.platform.cms2.servicelayer.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCMSSiteServiceTest
{

	@Mock
	private CMSSiteModel cmsSiteMock;
	@Mock
	private BaseStoreModel baseStoreMock1;
	@Mock
	private BaseStoreModel baseStoreMock2;
	@Mock
	private CatalogModel catalogMock1;
	@Mock
	private ClassificationSystemModel classCatalogMock2;
	@Mock
	private CatalogModel catalogMock3;
	@Mock
	private ClassificationSystemModel classCatalogMock4;
	@Mock
	private ContentCatalogModel contentCatalogMock5;
	@Mock
	private ClassificationSystemModel classCatalogMock6;
	@Mock
	private ContentPageModel contentPageMock;
	@Mock
	private CMSPageService cmsPageServiceMock;
	@Mock
	private BaseSiteService baseSiteServiceMock;


	@InjectMocks
	private DefaultCMSSiteService cmsSiteService;
	List<BaseStoreModel> stores;
	List<CatalogModel> catalogsStore1;
	List<CatalogModel> catalogsStore2;

	@Before
	public void setUp() throws Exception
	{
		cmsSiteService = new DefaultCMSSiteService();
		MockitoAnnotations.initMocks(this);

		stores = new ArrayList<BaseStoreModel>();
		stores.add(baseStoreMock1);
		stores.add(baseStoreMock2);

		catalogsStore1 = new ArrayList<CatalogModel>();
		catalogsStore1.add(catalogMock1);
		catalogsStore1.add(classCatalogMock2);
		catalogsStore1.add(null);
		catalogsStore1.add(contentCatalogMock5);

		catalogsStore2 = new ArrayList<CatalogModel>();
		catalogsStore2.add(catalogMock3);
		catalogsStore2.add(classCatalogMock4);
		catalogsStore2.add(classCatalogMock6);
		catalogsStore2.add(null);
	}

	/**
	 * Test method for
	 * {@link BaseSiteService#getProductCatalogs(de.hybris.platform.basecommerce.model.site.BaseSiteModel)} .
	 */
	@Test
	public void testShouldReturnClassificationCatalogsForCmsSiteModel()
	{
		// given
		given(cmsSiteMock.getStores()).willReturn(stores);
		given(baseStoreMock1.getCatalogs()).willReturn(catalogsStore1);
		given(baseStoreMock2.getCatalogs()).willReturn(catalogsStore2);

		// when
		final List<CatalogModel> classificationCatalogs = cmsSiteService.getClassificationCatalogs(cmsSiteMock);

		// then
		assertThat(classificationCatalogs).isNotEmpty();
		assertThat(classificationCatalogs).hasSize(3);
		assertThat(classificationCatalogs.get(0)).isInstanceOf(ClassificationSystemModel.class);
		assertThat(classificationCatalogs.get(1)).isInstanceOf(ClassificationSystemModel.class);
		assertThat(classificationCatalogs.get(2)).isInstanceOf(ClassificationSystemModel.class);
	}

	/**
	 * Test method for
	 * {@link BaseSiteService#getProductCatalogs(de.hybris.platform.basecommerce.model.site.BaseSiteModel)} .
	 */
	@Test
	public void testShouldReturnEmptyListWhenStoreCatalogsIsNull()
	{
		// given
		given(cmsSiteMock.getStores()).willReturn(stores);
		given(baseStoreMock1.getCatalogs()).willReturn(null);
		given(baseStoreMock2.getCatalogs()).willReturn(null);

		// when
		final List<CatalogModel> classificationCatalogs = cmsSiteService.getClassificationCatalogs(cmsSiteMock);

		// then
		assertThat(classificationCatalogs).isEmpty();
	}

	/**
	 * Test method for
	 * {@link BaseSiteService#getProductCatalogs(de.hybris.platform.basecommerce.model.site.BaseSiteModel)} .
	 */
	@Test
	public void testShouldReturnEmptyListWhenSiteStoresIsNull()
	{
		// given
		given(cmsSiteMock.getStores()).willReturn(null);

		// when
		final List<CatalogModel> classificationCatalogs = cmsSiteService.getClassificationCatalogs(cmsSiteMock);

		// then
		assertThat(classificationCatalogs).isEmpty();
	}

	/**
	 * Test method for
	 * {@link BaseSiteService#getProductCatalogs(de.hybris.platform.basecommerce.model.site.BaseSiteModel)} .
	 */
	@Test
	public void testShouldReturnEmptyListWhenSiteStoresIsCollectionOfNulls()
	{
		// given
		final List<BaseStoreModel> stores = new ArrayList<BaseStoreModel>();
		stores.add(null);
		stores.add(null);
		given(cmsSiteMock.getStores()).willReturn(stores);

		// when
		final List<CatalogModel> classificationCatalogs = cmsSiteService.getClassificationCatalogs(cmsSiteMock);

		// then
		assertThat(classificationCatalogs).isEmpty();
	}

	/**
	 * Test method for
	 * {@link BaseSiteService#getProductCatalogs(de.hybris.platform.basecommerce.model.site.BaseSiteModel)} . .
	 */
	@Test
	public void testShouldReturnProductCatalogs()
	{
		// given
		given(cmsSiteMock.getStores()).willReturn(stores);
		given(baseStoreMock1.getCatalogs()).willReturn(catalogsStore1);
		given(baseStoreMock2.getCatalogs()).willReturn(catalogsStore2);

		// when
		final List<CatalogModel> productCatalogs = cmsSiteService.getProductCatalogs(cmsSiteMock);

		// then
		assertThat(productCatalogs).hasSize(2);
		assertThat(productCatalogs.get(0)).isInstanceOf(CatalogModel.class);
		assertThat(productCatalogs.get(1)).isInstanceOf(CatalogModel.class);
	}

	/**
	 * Test method for
	 * {@link BaseSiteService#getProductCatalogs(de.hybris.platform.basecommerce.model.site.BaseSiteModel)} . .
	 */
	@Test
	public void testShouldReturnEmptyListWhenStoresForSiteIsNull()
	{
		// given
		given(cmsSiteMock.getStores()).willReturn(null);

		// when
		final List<CatalogModel> productCatalogs = cmsSiteService.getProductCatalogs(cmsSiteMock);

		// then
		assertThat(productCatalogs).isEmpty();
	}

	/**
	 * Test method for
	 * {@link BaseSiteService#getProductCatalogs(de.hybris.platform.basecommerce.model.site.BaseSiteModel)} . .
	 */
	@Test
	public void testShouldReturnEmptyListWhenStoresForSiteIsCollectionOfNulls()
	{
		// given
		final List<BaseStoreModel> stores = new ArrayList<BaseStoreModel>();
		stores.add(null);
		stores.add(null);
		given(cmsSiteMock.getStores()).willReturn(stores);

		// when
		final List<CatalogModel> productCatalogs = cmsSiteService.getProductCatalogs(cmsSiteMock);

		// then
		assertThat(productCatalogs).isEmpty();
	}

	/**
	 * Test method for
	 * {@link BaseSiteService#getProductCatalogs(de.hybris.platform.basecommerce.model.site.BaseSiteModel)} .
	 */
	@Test
	public void testShouldReturnEmptyListWhenCatalogsForSiteIsNull()
	{
		// given
		given(cmsSiteMock.getStores()).willReturn(stores);
		given(baseStoreMock1.getCatalogs()).willReturn(null);
		given(baseStoreMock2.getCatalogs()).willReturn(null);

		// when
		final List<CatalogModel> productCatalogs = cmsSiteService.getProductCatalogs(cmsSiteMock);

		// then
		assertThat(productCatalogs).isEmpty();
	}

	/**
	 * Test method for
	 * {@link BaseSiteService#getProductCatalogs(de.hybris.platform.basecommerce.model.site.BaseSiteModel)} . .
	 */
	@Test
	public void testShouldReturnNullWhenStartingPageIsNull()
	{
		// given
		given(cmsSiteMock.getStartingPage()).willReturn(null);

		// when
		final String startPageLabelOrId = cmsSiteService.getStartPageLabelOrId(cmsSiteMock);

		// then
		assertThat(startPageLabelOrId).isNull();
	}

	/**
	 * Test method for
	 * {@link BaseSiteService#getProductCatalogs(de.hybris.platform.basecommerce.model.site.BaseSiteModel)} . .
	 */
	@Test
	public void testShouldReturnLabelOrIdForStartingPage()
	{
		// given
		given(cmsSiteMock.getStartingPage()).willReturn(contentPageMock);
		given(cmsPageServiceMock.getLabelOrId(contentPageMock)).willReturn("FooBar");

		// when
		final String startPageLabelOrId = cmsSiteService.getStartPageLabelOrId(cmsSiteMock);

		// then
		assertThat(startPageLabelOrId).isEqualTo("FooBar");
	}

	@Test
	public void testShouldReturnNullWhenCurrentSiteIsNull()
	{
		// given
		given(baseSiteServiceMock.getCurrentBaseSite()).willReturn(null);

		// when
		final CMSSiteModel site = cmsSiteService.getCurrentSite();

		// then
		assertThat(site).isNull();
	}

	@Test
	public void testShouldReturnNullWhenCurrentSiteIsCmsSite()
	{
		// given
		given(baseSiteServiceMock.getCurrentBaseSite()).willReturn(cmsSiteMock);

		// when
		final CMSSiteModel site = cmsSiteService.getCurrentSite();

		// then
		assertThat(site).isEqualTo(cmsSiteMock);
	}

	@Test
	public void testShouldReturnNullWhenCurrentSiteIsNotCmsSite()
	{
		// given
		given(baseSiteServiceMock.getCurrentBaseSite()).willReturn(new BaseSiteModel());

		// when
		try
		{
			cmsSiteService.getCurrentSite();
			Assert.fail("Should throw an IllegalArgumentException if site is not a CmsSite");
		}
		catch (final IllegalStateException ile)
		{
			//ok here
		}
	}

	@Test
	public void testContainsCatalogOnlyForContentIfItIsNull()
	{
		// given
		given(cmsSiteMock.getContentCatalogs()).willReturn(null);
		// when and assert
		assertThat(cmsSiteService.containsCatalog(cmsSiteMock, catalogMock1, true)).isFalse();
	}

	@Test
	public void testContainsCatalogOnlyForContentIfItEmpty()
	{
		// given
		given(cmsSiteMock.getContentCatalogs()).willReturn(Collections.EMPTY_LIST);
		// when and assert
		assertThat(cmsSiteService.containsCatalog(cmsSiteMock, catalogMock1, true)).isFalse();
	}

	@Test
	public void testContainsCatalogOnlyForContentIfItContainsGiven()
	{
		// given
		given(cmsSiteMock.getContentCatalogs()).willReturn(Arrays.asList(contentCatalogMock5));
		// when and assert
		assertThat(cmsSiteService.containsCatalog(cmsSiteMock, contentCatalogMock5, true)).isTrue();
	}


	@Test
	public void testContainsCatalogForProductAndContentIfItIsNull()
	{
		// given
		given(cmsSiteMock.getContentCatalogs()).willReturn(null);
		given(cmsSiteMock.getProductCatalogs()).willReturn(null);
		// when and assert
		assertThat(cmsSiteService.containsCatalog(cmsSiteMock, catalogMock1, false)).isFalse();
	}

	@Test
	public void testContainsCatalogForProductAndContentIfItEmpty()
	{
		// given
		given(cmsSiteMock.getContentCatalogs()).willReturn(Collections.EMPTY_LIST);
		// when and assert
		assertThat(cmsSiteService.containsCatalog(cmsSiteMock, catalogMock1, false)).isFalse();
	}

	@Test
	public void testContainsCatalogForProductAndContentIfItContainsGiven()
	{
		// given
		given(cmsSiteMock.getContentCatalogs()).willReturn(Arrays.asList(contentCatalogMock5));
		given(cmsSiteMock.getProductCatalogs()).willReturn(Arrays.asList(catalogMock1));
		// when and assert
		assertThat(cmsSiteService.containsCatalog(cmsSiteMock, contentCatalogMock5, false)).isTrue();
	}

}
