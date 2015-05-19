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
package de.hybris.platform.site.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 *
 */
@UnitTest
public class DefaultBaseSiteServiceTest
{
	private final BaseSiteService service = new DefaultBaseSiteService()
	{
		@Override
		protected boolean isPlainCatalogModel(final CatalogModel catalog)
		{
			return !(catalog instanceof ClassificationSystemModel);
		}
	};


	@Test
	public void testGetProductCatalogs()
	{

		final BaseSiteModel baseSite = Mockito.mock(BaseSiteModel.class);

		final BaseStoreModel baseStoreOne = Mockito.mock(BaseStoreModel.class);
		final BaseStoreModel baseStoreTwo = Mockito.mock(BaseStoreModel.class);

		final CatalogModel catalogOne = Mockito.mock(CatalogModel.class);
		final CatalogModel catalogTwo = Mockito.mock(ClassificationSystemModel.class);
		final CatalogModel catalogThree = Mockito.mock(CatalogModel.class);

		BDDMockito.given(baseSite.getStores()).willReturn(Arrays.asList(baseStoreOne, baseStoreTwo));
		BDDMockito.given(baseStoreOne.getCatalogs()).willReturn(Arrays.asList(catalogOne, catalogTwo, catalogThree));
		BDDMockito.given(baseStoreTwo.getCatalogs()).willReturn(Arrays.asList(catalogOne, catalogTwo));

		final List<CatalogModel> resultList = service.getProductCatalogs(baseSite);
		Assert.assertEquals(2, resultList.size());
		Assert.assertEquals(Arrays.asList(catalogOne, catalogThree), resultList);
	}

	@Test
	public void testGetProductCatalogsForEmptyStores()
	{

		final BaseSiteModel baseSite = Mockito.mock(BaseSiteModel.class);

		final BaseStoreModel baseStoreOne = Mockito.mock(BaseStoreModel.class);
		final BaseStoreModel baseStoreTwo = Mockito.mock(BaseStoreModel.class);

		BDDMockito.given(baseSite.getStores()).willReturn(Arrays.asList(baseStoreOne, baseStoreTwo));

		final List<CatalogModel> resultList = service.getProductCatalogs(baseSite);
		Assert.assertEquals(0, resultList.size());
	}
}
