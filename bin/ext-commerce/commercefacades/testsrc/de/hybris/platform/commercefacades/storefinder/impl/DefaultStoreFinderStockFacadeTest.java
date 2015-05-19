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
package de.hybris.platform.commercefacades.storefinder.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storefinder.data.StoreFinderStockSearchPageData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceStockData;
import de.hybris.platform.commercefacades.storelocator.data.StoreStockHolder;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.StoreFinderService;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit Test for {@link DefaultStoreFinderStockFacade}
 * 
 */
@UnitTest
public class DefaultStoreFinderStockFacadeTest
{
	private static final String POS_NAME = "testPosName";
	private static final Double LAT = Double.valueOf(35.0);
	private static final Double LON = Double.valueOf(135.0);
	private static final String PRODUCT_CODE = "testProductCode";
	private static final String SAMPLE_LOCATION = "sampleLocation";
	private static final double NEARBY_RADIUS = 0d;

	private DefaultStoreFinderStockFacade<PointOfServiceStockData> facade;

	@Mock
	private Converter<StoreStockHolder, PointOfServiceStockData> storeStockConverter;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private StoreFinderService<PointOfServiceDistanceData, StoreFinderSearchPageData<PointOfServiceDistanceData>> storeFinderService;
	@Mock
	private PointOfServiceService pointOfServiceService;
	@Mock
	private ProductService productService;
	@Mock
	private Converter<PointOfServiceDistanceData, PointOfServiceData> pointOfServiceDistanceDataConverter;

	@Mock
	private PointOfServiceModel pointOfServiceModel;
	@Mock
	private PageableData pageableData;
	@Mock
	private ProductData productData;
	@Mock
	private BaseStoreModel baseStoreModel;
	@Mock
	private StoreFinderSearchPageData<PointOfServiceDistanceData> searchPageData;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		facade = new DefaultStoreFinderStockFacade<PointOfServiceStockData>();
		facade.setBaseStoreService(baseStoreService);
		facade.setPointOfServiceDistanceDataConverter(pointOfServiceDistanceDataConverter);
		facade.setPointOfServiceService(pointOfServiceService);
		facade.setProductService(productService);
		facade.setStoreFinderService(storeFinderService);
		facade.setStoreStockConverter(storeStockConverter);

		given(pointOfServiceService.getPointOfServiceForName(POS_NAME)).willReturn(pointOfServiceModel);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(storeFinderService.positionSearch(eq(baseStoreModel), any(GeoPoint.class), eq(pageableData))).willReturn(
				searchPageData);
		given(storeFinderService.positionSearch(eq(baseStoreModel), any(GeoPoint.class), eq(pageableData), eq(NEARBY_RADIUS)))
				.willReturn(searchPageData);
		given(storeFinderService.locationSearch(baseStoreModel, SAMPLE_LOCATION, pageableData)).willReturn(searchPageData);
		given(productData.getCode()).willReturn(PRODUCT_CODE);
		given(Integer.valueOf(pageableData.getPageSize())).willReturn(Integer.valueOf(3));
		given(Integer.valueOf(pageableData.getCurrentPage())).willReturn(Integer.valueOf(0));
		given(pointOfServiceModel.getLatitude()).willReturn(LAT);
		given(pointOfServiceModel.getLongitude()).willReturn(LON);
	}


	@Test
	public void testproductPOSSearchEmpty()
	{
		given(pointOfServiceService.getPointOfServiceForName(POS_NAME)).willReturn(null);

		final StoreFinderStockSearchPageData<PointOfServiceStockData> result = facade.productPOSSearch(POS_NAME, productData,
				pageableData);

		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.getResults().size());
	}


	@Test
	public void testproductPOSSearch()
	{
		final PointOfServiceDistanceData distanceData1 = mock(PointOfServiceDistanceData.class);
		final PointOfServiceDistanceData distanceData2 = mock(PointOfServiceDistanceData.class);
		final PointOfServiceDistanceData distanceData3 = mock(PointOfServiceDistanceData.class);
		final List<PointOfServiceDistanceData> distanceData = new ArrayList<PointOfServiceDistanceData>();
		distanceData.add(distanceData1);
		distanceData.add(distanceData2);
		distanceData.add(distanceData3);

		final PointOfServiceModel posModel2 = mock(PointOfServiceModel.class);
		given(distanceData2.getPointOfService()).willReturn(posModel2);

		final PointOfServiceData posData = mock(PointOfServiceData.class);
		given(posData.getFormattedDistance()).willReturn("");
		given(pointOfServiceDistanceDataConverter.convert(any(PointOfServiceDistanceData.class))).willReturn(posData);

		final ProductModel productModel2 = mock(ProductModel.class);
		given(productService.getProductForCode(PRODUCT_CODE)).willReturn(productModel2);

		final PointOfServiceStockData resultData2 = mock(PointOfServiceStockData.class);
		given(storeStockConverter.convert(any(StoreStockHolder.class))).willReturn(resultData2);

		given(searchPageData.getResults()).willReturn(distanceData);

		final StoreFinderStockSearchPageData<PointOfServiceStockData> result = facade.productPOSSearch(POS_NAME, productData,
				pageableData);

		Assert.assertNotNull(result);
		Assert.assertEquals(resultData2, result.getResults().get(1));
	}


	@Test
	public void testProductSearch()
	{
		final PointOfServiceDistanceData distanceData1 = mock(PointOfServiceDistanceData.class);
		final PointOfServiceDistanceData distanceData2 = mock(PointOfServiceDistanceData.class);
		final List<PointOfServiceDistanceData> distanceData = new ArrayList<PointOfServiceDistanceData>();
		distanceData.add(distanceData1);
		distanceData.add(distanceData2);

		final PointOfServiceModel posModel2 = mock(PointOfServiceModel.class);
		given(distanceData2.getPointOfService()).willReturn(posModel2);

		final PointOfServiceData posData = mock(PointOfServiceData.class);
		given(posData.getFormattedDistance()).willReturn("");
		given(pointOfServiceDistanceDataConverter.convert(any(PointOfServiceDistanceData.class))).willReturn(posData);

		final ProductModel productModel2 = mock(ProductModel.class);
		given(productService.getProductForCode(PRODUCT_CODE)).willReturn(productModel2);

		final PointOfServiceStockData resultData2 = mock(PointOfServiceStockData.class);
		given(storeStockConverter.convert(any(StoreStockHolder.class))).willReturn(resultData2);

		given(searchPageData.getResults()).willReturn(distanceData);

		final StoreFinderStockSearchPageData<PointOfServiceStockData> result = facade.productSearch(SAMPLE_LOCATION, productData,
				pageableData);

		Assert.assertNotNull(result);
		Assert.assertEquals(resultData2, result.getResults().get(1));
	}



	@Test
	public void testProductSearchForCoords()
	{
		final PointOfServiceDistanceData distanceData1 = mock(PointOfServiceDistanceData.class);
		final List<PointOfServiceDistanceData> distanceData = new ArrayList<PointOfServiceDistanceData>();
		distanceData.add(distanceData1);

		final PointOfServiceModel posModel1 = mock(PointOfServiceModel.class);
		given(distanceData1.getPointOfService()).willReturn(posModel1);

		final PointOfServiceData posData = mock(PointOfServiceData.class);
		given(posData.getFormattedDistance()).willReturn("");
		given(pointOfServiceDistanceDataConverter.convert(any(PointOfServiceDistanceData.class))).willReturn(posData);

		final ProductModel productModel1 = mock(ProductModel.class);
		given(productService.getProductForCode(PRODUCT_CODE)).willReturn(productModel1);

		final PointOfServiceStockData resultData1 = mock(PointOfServiceStockData.class);
		given(storeStockConverter.convert(any(StoreStockHolder.class))).willReturn(resultData1);

		given(searchPageData.getResults()).willReturn(distanceData);

		final StoreFinderStockSearchPageData<PointOfServiceStockData> result = facade.productSearch(mock(GeoPoint.class), productData, pageableData);

		Assert.assertNotNull(result);
		Assert.assertEquals(resultData1, result.getResults().get(0));

	}
}
