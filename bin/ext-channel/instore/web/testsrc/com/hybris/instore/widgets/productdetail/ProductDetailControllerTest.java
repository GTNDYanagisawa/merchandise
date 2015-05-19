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
package com.hybris.instore.widgets.productdetail;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storefinder.StoreFinderStockFacade;
import de.hybris.platform.commercefacades.storefinder.data.StoreFinderStockSearchPageData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceStockData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvents;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;
import com.hybris.instore.InStoreService;


@NullSafeWidget(value = false)
@DeclaredInput(socketType = ProductData.class, value = ProductDetailController.SOCKET_IN_PRODUCT)
@DeclaredViewEvents(
{ @DeclaredViewEvent(componentID = ProductDetailController.COMP_ID_SIMILAR_BTN, eventName = Events.ON_CLICK),
		@DeclaredViewEvent(componentID = ProductDetailController.COMP_ID_NEARBYATSGROUPBOX, eventName = "onClose_later"),
		@DeclaredViewEvent(componentID = ProductDetailController.COMP_ID_REFRESH_STOCK_BUTTON, eventName = Events.ON_CLICK),
		@DeclaredViewEvent(componentID = ProductDetailController.COMP_ID_BACKBUTTON, eventName = Events.ON_CLICK) })
public class ProductDetailControllerTest extends AbstractWidgetUnitTest<ProductDetailController>
{
	@InjectMocks
	private final ProductDetailController productDetailController = new ProductDetailController();

	@SuppressWarnings("unused")
	@Mock
	private ProductFacade productFacade;
	@SuppressWarnings("unused")
	@Mock
	private StoreFinderStockFacade<PointOfServiceStockData, StoreFinderStockSearchPageData<PointOfServiceStockData>> storeFinderStockFacade;
	@SuppressWarnings("unused")
	@Mock
	private StoreFinderStockFacade<PointOfServiceStockData, StoreFinderStockSearchPageData<PointOfServiceStockData>> liveATSStoreFinderStockFacade;
	@SuppressWarnings("unused")
	@Mock
	private Label titleLabel;
	@SuppressWarnings("unused")
	@Mock
	private Label subtitleLabel;
	@SuppressWarnings("unused")
	@Mock
	private Label priceLabel;
	@SuppressWarnings("unused")
	@Mock
	private Label priceInfoLabel;
	@SuppressWarnings("unused")
	@Mock
	private Image imageContainer;
	@SuppressWarnings("unused")
	@Mock
	private Div ratingPlaceHolder;
	@SuppressWarnings("unused")
	@Mock
	private Div ratingSizeableContainer;
	@SuppressWarnings("unused")
	@Mock
	private Div atscontainer;
	@SuppressWarnings("unused")
	@Mock
	private Label localAtsLabel;
	@SuppressWarnings("unused")
	@Mock
	private Label nearbyAtsLabel;
	@SuppressWarnings("unused")
	@Mock
	private Listbox nearbyStoresList;
	@SuppressWarnings("unused")
	@Mock
	private Div globalAtsStatus;
	@SuppressWarnings("unused")
	@Mock
	private Groupbox nearbyAtsGroupbox;


	@Mock
	private InStoreService inStoreService;



	@Override
	protected ProductDetailController getWidgetController()
	{
		return productDetailController;
	}

	@Test
	public void getProductWithAvailabilityOptionsTest()
	{
		final ProductData mockProductData = Mockito.mock(ProductData.class);
		Mockito.when(mockProductData.getAvailableForPickup()).thenReturn(Boolean.TRUE);
		final ProductData testProductData = productDetailController.getProductWithAvailabilityOptions(mockProductData);
		Assert.assertEquals(testProductData.getAvailableForPickup(), Boolean.TRUE);
	}

	@Test
	public void getNearbyPointOfServicesTest()
	{
		widgetInstanceManager.getWidgetSettings().put(ProductDetailController.SETTING_NEARBY_POS_COUNT, Integer.valueOf(1));
		final ProductData mockProductData = Mockito.mock(ProductData.class);
		final StoreFinderStockSearchPageData<PointOfServiceStockData> mockStoreFinderStockSearchPageData = Mockito
				.mock(StoreFinderStockSearchPageData.class);
		final List<PointOfServiceStockData> posList = Arrays.asList(Mockito.mock(PointOfServiceStockData.class),
				Mockito.mock(PointOfServiceStockData.class));
		Mockito.when(mockStoreFinderStockSearchPageData.getResults()).thenReturn(posList);
		Mockito.when(
				storeFinderStockFacade.productPOSSearch(Mockito.eq("testPOS"), Mockito.any(ProductData.class),
						Mockito.any(PageableData.class))).thenReturn(mockStoreFinderStockSearchPageData);
		final PointOfServiceModel posMock = Mockito.mock(PointOfServiceModel.class);
		Mockito.when(posMock.getName()).thenReturn("testPOS");
		Mockito.when(inStoreService.getDefaultPointOfServiceForCurrentUser()).thenReturn(posMock);

		final List<PointOfServiceStockData> result = productDetailController.getNearbyPointOfServices(mockProductData, false);

		Assert.assertNotNull(result);
		Assert.assertEquals(posList, result);
	}

	@Test
	public void getLocalAts()
	{
		final StoreFinderStockSearchPageData<PointOfServiceStockData> mockStoreFinderStockSearchPageData = Mockito
				.mock(StoreFinderStockSearchPageData.class);
		final List<PointOfServiceStockData> posList = Arrays.asList(Mockito.mock(PointOfServiceStockData.class),
				Mockito.mock(PointOfServiceStockData.class));
		Mockito.when(mockStoreFinderStockSearchPageData.getResults()).thenReturn(posList);
		Mockito.when(
				storeFinderStockFacade.productPOSSearch(Mockito.eq("testPOS"), Mockito.any(ProductData.class),
						Mockito.any(PageableData.class))).thenReturn(mockStoreFinderStockSearchPageData);
		final PointOfServiceModel posMock = Mockito.mock(PointOfServiceModel.class);
		Mockito.when(posMock.getName()).thenReturn("testPOS");
		Mockito.when(inStoreService.getDefaultPointOfServiceForCurrentUser()).thenReturn(posMock);
		Mockito.when(productDetailController.getPOSName()).thenReturn("testPOS");


		final String localAts = productDetailController.getLocalAts(posList);

		Assert.assertNotNull(localAts);
	}

	@Test
	public void getPictureURLTest()
	{
		productDetailController.getWidgetSettings().put(ProductDetailController.SETTING_IMAGE_FORMAT, "product");
		final ImageData imageData1 = new ImageData();
		imageData1.setFormat("product");
		final ImageData imageData2 = new ImageData();
		imageData2.setFormat("product");
		final List<ImageData> mockImageDataCol = Arrays.asList(imageData1, imageData2);
		final ProductData mockProductData = new ProductData();

		mockProductData.setImages(mockImageDataCol);
		final String mockPictureURL = productDetailController.getPictureURL(mockProductData);
		Assert.assertNotNull(mockPictureURL);
	}

}
