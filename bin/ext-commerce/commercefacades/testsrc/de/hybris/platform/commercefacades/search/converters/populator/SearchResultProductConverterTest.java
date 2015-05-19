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
package de.hybris.platform.commercefacades.search.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SearchResultProductConverterTest
{
	protected static final String URL = "url";

	private final SearchResultProductPopulator searchResultProductPopulator = new SearchResultProductPopulator();
	private AbstractPopulatingConverter<SearchResultValueData, ProductData> searchResultProductConverter;

	@Mock
	private ImageFormatMapping imageFormatMapping;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private UrlResolver<ProductData> productDataUrlResolver;
	@Mock
	private Populator<FeatureList, ProductData> productFeatureListPopulator;
	@Mock
	private Converter<ProductModel, StockData> stockConverter;
	@Mock
	private Converter<StockLevelStatus, StockData> stockLevelStatusConverter;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private ProductService productService;

	@Mock
	private CurrencyModel gbpCurrency;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		searchResultProductPopulator.setImageFormatMapping(imageFormatMapping);
		searchResultProductPopulator.setPriceDataFactory(priceDataFactory);
		searchResultProductPopulator.setProductDataUrlResolver(productDataUrlResolver);
		searchResultProductPopulator.setProductFeatureListPopulator(productFeatureListPopulator);
		searchResultProductPopulator.setProductService(productService);
		searchResultProductPopulator.setCommonI18NService(commonI18NService);
		searchResultProductPopulator.setStockConverter(stockConverter);
		searchResultProductPopulator.setStockLevelStatusConverter(stockLevelStatusConverter);

		searchResultProductConverter = new ConverterFactory<SearchResultValueData, ProductData, SearchResultProductPopulator>()
				.create(ProductData.class, searchResultProductPopulator);

		given(commonI18NService.getCurrentCurrency()).willReturn(gbpCurrency);
		given(gbpCurrency.getIsocode()).willReturn("GBP");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertNull()
	{
		searchResultProductConverter.convert(null);
	}

	@Test
	public void testConvertEmpty()
	{
		final SearchResultValueData searchResultValueData = new SearchResultValueData();
		final ProductData result = searchResultProductConverter.convert(searchResultValueData);

		Assert.assertNull(result.getCode());
		Assert.assertNull(result.getName());
		Assert.assertNull(result.getAverageRating());
		Assert.assertNull(result.getDescription());
		Assert.assertNull(result.getPrice());
		Assert.assertNull(result.getUrl());
	}

	@Test
	public void testConvertBasics()
	{
		//Registry.activateMasterTenant();
		final SearchResultValueData searchResultValueData = mock(SearchResultValueData.class);

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", "code value");
		map.put("name", "name value");
		map.put("description", "description value");
		map.put("summary", "summary value");
		map.put("reviewAvgRating", Double.valueOf(5));
		map.put("priceValue", Double.valueOf(9.99));

		given(productDataUrlResolver.resolve(Matchers.<ProductData> any())).willReturn(URL);
		given(searchResultValueData.getValues()).willReturn(map);

		final PriceData priceData = mock(PriceData.class);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(9.99), gbpCurrency)).willReturn(priceData);

		final ProductData productData = searchResultProductConverter.convert(searchResultValueData);

		Assert.assertEquals("code value", productData.getCode());
		Assert.assertEquals("name value", productData.getName());
		Assert.assertEquals("description value", productData.getDescription());
		Assert.assertEquals("summary value", productData.getSummary());
		Assert.assertEquals(Double.valueOf(5), productData.getAverageRating());
		Assert.assertNotNull(productData.getPrice());
		Assert.assertEquals(priceData, productData.getPrice());
		Assert.assertEquals(URL, productData.getUrl());
	}

	@Test
	public void testConvertBasicsAndImage()
	{
		//Registry.activateMasterTenant();
		final SearchResultValueData searchResultValueData = mock(SearchResultValueData.class);

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", "code value");
		map.put("name", "name value");
		map.put("description", "description value");
		map.put("summary", "summary value");
		map.put("reviewAvgRating", Double.valueOf(5));
		map.put("priceValue", Double.valueOf(9.99));

		given(productDataUrlResolver.resolve(Matchers.<ProductData> any())).willReturn(URL);
		given(searchResultValueData.getValues()).willReturn(map);

		final PriceData priceData = mock(PriceData.class);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(9.99), gbpCurrency)).willReturn(priceData);

		given(imageFormatMapping.getMediaFormatQualifierForImageFormat("thumbnail")).willReturn("imageFormat");
		map.put("img-imageFormat", "url to image");

		final ProductData productData = searchResultProductConverter.convert(searchResultValueData);

		Assert.assertEquals("code value", productData.getCode());
		Assert.assertEquals("name value", productData.getName());
		Assert.assertEquals("description value", productData.getDescription());
		Assert.assertEquals("summary value", productData.getSummary());
		Assert.assertEquals(Double.valueOf(5), productData.getAverageRating());
		Assert.assertNotNull(productData.getPrice());
		Assert.assertEquals(priceData, productData.getPrice());
		Assert.assertEquals(URL, productData.getUrl());
		Assert.assertNotNull(productData.getImages());
		Assert.assertEquals(1, productData.getImages().size());
		Assert.assertEquals("url to image", productData.getImages().iterator().next().getUrl());
		Assert.assertEquals("thumbnail", productData.getImages().iterator().next().getFormat());
	}
}
