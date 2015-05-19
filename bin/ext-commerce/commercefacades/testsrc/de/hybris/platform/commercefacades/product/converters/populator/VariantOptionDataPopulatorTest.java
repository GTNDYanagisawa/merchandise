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
package de.hybris.platform.commercefacades.product.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.variants.model.VariantAttributeDescriptorModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.math.BigDecimal;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link VariantOptionDataPopulator}
 */
@UnitTest
public class VariantOptionDataPopulatorTest
{
	private static final String DESCRIPTOR_MODEL_QUALIFIER = "descQual";
	private static final String VARIANT_ATTR_VALUE = "attrValue";
	private static final String VARIANT_DATA_URL = "url/proCode";
	private static final Long STOCK_LEVEL = Long.valueOf(10);
	private static final String CURRENCY_ISO = "eur";
	private static final Double PRICE_VALUE = Double.valueOf(12.5D);

	@Mock
	private VariantsService variantsService;
	@Mock
	private UrlResolver<ProductModel> productModelUrlResolver;
	@Mock
	private Converter<ProductModel, StockData> stockConverter;
	@Mock
	private CommercePriceService commercePriceService;
	@Mock
	private PriceDataFactory priceDataFactory;

	private final AbstractPopulatingConverter<MediaModel, ImageData> imageConverter = new ConverterFactory<MediaModel, ImageData, ImagePopulator>()
			.create(ImageData.class, new ImagePopulator());

	private VariantOptionDataPopulator variantOptionDataPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		variantOptionDataPopulator = new VariantOptionDataPopulator();
		variantOptionDataPopulator.setCommercePriceService(commercePriceService);
		variantOptionDataPopulator.setImageConverter(imageConverter);
		variantOptionDataPopulator.setPriceDataFactory(priceDataFactory);
		variantOptionDataPopulator.setProductModelUrlResolver(productModelUrlResolver);
		variantOptionDataPopulator.setStockConverter(stockConverter);
		variantOptionDataPopulator.setVariantsService(variantsService);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConvertWhenSourceIsNull()
	{
		variantOptionDataPopulator.populate(null, mock(VariantOptionData.class));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConvertWhenPrototypeIsNull()
	{
		variantOptionDataPopulator.populate(mock(VariantProductModel.class), null);
	}


	@Test
	public void testConvert()
	{
		final VariantProductModel source = mock(VariantProductModel.class);
		final VariantAttributeDescriptorModel variantAttributeDescriptorModel = mock(VariantAttributeDescriptorModel.class);
		final ProductModel baseProduct = mock(ProductModel.class);
		final VariantTypeModel variantTypeModel = mock(VariantTypeModel.class);
		final PriceInformation priceInformation = mock(PriceInformation.class);
		final PriceValue priceValue = mock(PriceValue.class);
		final PriceData priceData = mock(PriceData.class);
		final StockData stockData = mock(StockData.class);

		given(priceValue.getCurrencyIso()).willReturn(CURRENCY_ISO);
		given(Double.valueOf(priceValue.getValue())).willReturn(PRICE_VALUE);
		given(priceInformation.getPriceValue()).willReturn(priceValue);
		given(commercePriceService.getWebPriceForProduct(source)).willReturn(priceInformation);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(priceValue.getValue()), CURRENCY_ISO)).willReturn(
				priceData);
		given(stockConverter.convert(Mockito.any(VariantProductModel.class))).willReturn(stockData);
		given(stockData.getStockLevelStatus()).willReturn(StockLevelStatus.INSTOCK);
		given(stockData.getStockLevel()).willReturn(STOCK_LEVEL);
		given(baseProduct.getVariantType()).willReturn(variantTypeModel);
		given(source.getBaseProduct()).willReturn(baseProduct);
		given(variantAttributeDescriptorModel.getQualifier()).willReturn(DESCRIPTOR_MODEL_QUALIFIER);
		given(variantsService.getVariantAttributesForVariantType(variantTypeModel)).willReturn(
				Collections.singletonList(variantAttributeDescriptorModel));
		given(variantsService.getVariantAttributeValue(source, DESCRIPTOR_MODEL_QUALIFIER)).willReturn(VARIANT_ATTR_VALUE);
		given(productModelUrlResolver.resolve(any(ProductModel.class))).willReturn(VARIANT_DATA_URL);

		final VariantOptionData result = new VariantOptionData();
		variantOptionDataPopulator.populate(source, result);

		Assert.assertEquals(1, result.getVariantOptionQualifiers().size());
		Assert.assertEquals(VARIANT_ATTR_VALUE, result.getVariantOptionQualifiers().iterator().next().getValue());
		Assert.assertEquals(VARIANT_DATA_URL, result.getUrl());
		Assert.assertEquals(STOCK_LEVEL, result.getStock().getStockLevel());
		Assert.assertEquals(priceData, result.getPriceData());
		Assert.assertEquals(StockLevelStatus.INSTOCK, result.getStock().getStockLevelStatus());
	}


	@Test
	public void testConvertNoStockSystem()
	{
		final VariantProductModel source = mock(VariantProductModel.class);
		final VariantAttributeDescriptorModel variantAttributeDescriptorModel = mock(VariantAttributeDescriptorModel.class);
		final ProductModel baseProduct = mock(ProductModel.class);
		final VariantTypeModel variantTypeModel = mock(VariantTypeModel.class);
		final PriceInformation priceInformation = mock(PriceInformation.class);
		final PriceValue priceValue = mock(PriceValue.class);
		final PriceData priceData = mock(PriceData.class);
		final StockData stockData = mock(StockData.class);

		given(priceValue.getCurrencyIso()).willReturn(CURRENCY_ISO);
		given(Double.valueOf(priceValue.getValue())).willReturn(PRICE_VALUE);
		given(priceInformation.getPriceValue()).willReturn(priceValue);
		given(commercePriceService.getWebPriceForProduct(source)).willReturn(priceInformation);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(priceValue.getValue()), CURRENCY_ISO)).willReturn(
				priceData);
		given(stockConverter.convert(Mockito.any(VariantProductModel.class))).willReturn(stockData);
		given(stockData.getStockLevelStatus()).willReturn(StockLevelStatus.INSTOCK);
		given(stockData.getStockLevel()).willReturn(Long.valueOf(0l));
		given(baseProduct.getVariantType()).willReturn(variantTypeModel);
		given(source.getBaseProduct()).willReturn(baseProduct);
		given(variantAttributeDescriptorModel.getQualifier()).willReturn(DESCRIPTOR_MODEL_QUALIFIER);
		given(variantsService.getVariantAttributesForVariantType(variantTypeModel)).willReturn(
				Collections.singletonList(variantAttributeDescriptorModel));
		given(variantsService.getVariantAttributeValue(source, DESCRIPTOR_MODEL_QUALIFIER)).willReturn(VARIANT_ATTR_VALUE);
		given(productModelUrlResolver.resolve(any(ProductModel.class))).willReturn(VARIANT_DATA_URL);

		final VariantOptionData result = new VariantOptionData();
		variantOptionDataPopulator.populate(source, result);

		Assert.assertEquals(1, result.getVariantOptionQualifiers().size());
		Assert.assertEquals(VARIANT_ATTR_VALUE, result.getVariantOptionQualifiers().iterator().next().getValue());
		Assert.assertEquals(VARIANT_DATA_URL, result.getUrl());
		Assert.assertEquals(Long.valueOf(0), result.getStock().getStockLevel());
		Assert.assertEquals(StockLevelStatus.INSTOCK, result.getStock().getStockLevelStatus());
		Assert.assertEquals(priceData, result.getPriceData());
	}


	@Test
	public void testConvertFromPrice()
	{
		final VariantProductModel source = mock(VariantProductModel.class);
		final VariantAttributeDescriptorModel variantAttributeDescriptorModel = mock(VariantAttributeDescriptorModel.class);
		final ProductModel baseProduct = mock(ProductModel.class);
		final VariantTypeModel variantTypeModel = mock(VariantTypeModel.class);
		final PriceInformation priceInformation = mock(PriceInformation.class);
		final PriceValue priceValue = mock(PriceValue.class);
		final PriceData priceData = mock(PriceData.class);
		final VariantProductModel subVariantProduct = mock(VariantProductModel.class);
		final StockData stockData = mock(StockData.class);

		given(source.getVariants()).willReturn(Collections.singleton(subVariantProduct));
		given(priceValue.getCurrencyIso()).willReturn(CURRENCY_ISO);
		given(Double.valueOf(priceValue.getValue())).willReturn(PRICE_VALUE);
		given(priceInformation.getPriceValue()).willReturn(priceValue);
		given(commercePriceService.getFromPriceForProduct(source)).willReturn(priceInformation);
		given(priceDataFactory.create(PriceDataType.FROM, BigDecimal.valueOf(priceValue.getValue()), CURRENCY_ISO)).willReturn(
				priceData);
		given(stockConverter.convert(Mockito.any(VariantProductModel.class))).willReturn(stockData);
		given(stockData.getStockLevelStatus()).willReturn(StockLevelStatus.INSTOCK);
		given(stockData.getStockLevel()).willReturn(STOCK_LEVEL);
		given(baseProduct.getVariantType()).willReturn(variantTypeModel);
		given(source.getBaseProduct()).willReturn(baseProduct);
		given(variantAttributeDescriptorModel.getQualifier()).willReturn(DESCRIPTOR_MODEL_QUALIFIER);
		given(variantsService.getVariantAttributesForVariantType(variantTypeModel)).willReturn(
				Collections.singletonList(variantAttributeDescriptorModel));
		given(variantsService.getVariantAttributeValue(source, DESCRIPTOR_MODEL_QUALIFIER)).willReturn(VARIANT_ATTR_VALUE);
		given(productModelUrlResolver.resolve(any(ProductModel.class))).willReturn(VARIANT_DATA_URL);

		final VariantOptionData result = new VariantOptionData();
		variantOptionDataPopulator.populate(source, result);

		Assert.assertEquals(1, result.getVariantOptionQualifiers().size());
		Assert.assertEquals(VARIANT_ATTR_VALUE, result.getVariantOptionQualifiers().iterator().next().getValue());
		Assert.assertEquals(VARIANT_DATA_URL, result.getUrl());
		Assert.assertEquals(STOCK_LEVEL, result.getStock().getStockLevel());
		Assert.assertEquals(priceData, result.getPriceData());
		Assert.assertEquals(StockLevelStatus.INSTOCK, result.getStock().getStockLevelStatus());
	}
}
