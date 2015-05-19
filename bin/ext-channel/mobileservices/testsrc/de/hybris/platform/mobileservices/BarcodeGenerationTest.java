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
package de.hybris.platform.mobileservices;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.jalo.CategoryManager;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.deeplink.model.media.BarcodeMediaModel;
import de.hybris.platform.deeplink.model.rules.DeeplinkUrlModel;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.mobileservices.facade.BarcodeMediaService;
import de.hybris.platform.mobileservices.jalo.MobileManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.Config;

import java.util.Collections;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class BarcodeGenerationTest extends ServicelayerTransactionalTest
{
	@Resource
	BarcodeMediaService barcodeMediaService;
	@Resource
	FlexibleSearchService flexibleSearchService;
	@Resource
	ModelService modelService;

	private long deeplink = -1;

	@Before
	public void setUp() throws Exception
	{

		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		createCoreData();
		createHardwareCatalog();
		final String encoding = "utf-8";
		ImpExManager.getInstance().importData(
				MobileManager.class.getResourceAsStream("/sampledata/mobile.deeplinkurl.csv"), encoding,
				CSVConstants.HYBRIS_FIELD_SEPARATOR, CSVConstants.HYBRIS_QUOTE_CHARACTER, true);

		deeplink = getDeeplinkPk();

	}




	@Test
	public void testCatalog()
	{
		testItem(deeplink, getTestDataCatalog());

	}

	@Test
	public void testProduct()
	{
		testItem(deeplink, getTestDataProduct());

	}

	@Test
	public void testCategory()
	{
		testItem(deeplink, getTestDataCategory());
	}

	private void testItem(final long deeplink, final ItemModel item)
	{
		final CatalogVersion defaultCatalogVersion = CatalogManager.getInstance().getCatalog("hwcatalog").getCatalogVersion(
				"Online");
		CatalogManager.getInstance().setSessionCatalogVersions(Collections.singletonList(defaultCatalogVersion));
		final String url = barcodeMediaService.barcode(deeplink, item);
		assertFalse("Barcode media should return an url", StringUtils.isEmpty(url));
		final BarcodeMediaModel media = getBarcodeMedia(deeplink, item.getPk().getLongValue());
		assertNotNull("Media should not be null", media);
		assertTrue(media.getContextItem().equals(item));
		assertTrue(media.getDeeplinkUrl().getPk().getLongValue() == deeplink);
	}

	private long getDeeplinkPk()
	{
		final String yql = "select {c:pk} from {DeeplinkUrl as c} where {c:code} like ?code";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(yql);
		final String campaignCode = Config.getString("mobile.deeplinkurl.defaultCampaignCode", "mobile");
		query.addQueryParameter("code", campaignCode);
		final SearchResult<DeeplinkUrlModel> results = flexibleSearchService.search(query);
		return results.getResult().iterator().next().getPk().getLongValue();
	}


	private CategoryModel getTestDataCategory()
	{
		final Category category = CategoryManager.getInstance().getCategoriesByCode("HW1000").iterator().next();
		Assert.assertNotNull(category);
		return modelService.get(category);


	}

	private ProductModel getTestDataProduct()
	{
		final Product product = (Product) ProductManager.getInstance().getProductsByCode("HW2310-1004").iterator().next();
		Assert.assertNotNull(product);
		return modelService.get(product);
	}

	private CatalogVersionModel getTestDataCatalog()
	{
		final CatalogVersion version = CatalogManager.getInstance().getCatalog("hwcatalog").getCatalogVersion("Online");
		Assert.assertNotNull(version);
		return modelService.get(version);
	}

	private BarcodeMediaModel getBarcodeMedia(final long deeplink, final long item)
	{
		final String yql = "select {b:pk} from {BarcodeMedia as b} where {b:code} like ?code";
		final String code = new StringBuffer().append(deeplink).append(item).append("_2d_qr").toString();
		final FlexibleSearchQuery query = new FlexibleSearchQuery(yql);
		query.addQueryParameter("code", code);
		final SearchResult<BarcodeMediaModel> results = flexibleSearchService.search(query);
		return results.getResult().iterator().next();

	}







}
