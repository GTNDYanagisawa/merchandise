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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.variants.model.VariantProductModel;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import junit.framework.Assert;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


/**
 * Test suite for {@link ProductPricePopulator}
 */
@UnitTest
public class ProductPricePopulatorTest
{
	private static final String CURRENCY_ISO = "eur";
	private static final Double PRICE_VALUE = Double.valueOf(332.45D);

	@Mock
	private CommercePriceService commercePriceService;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private ModelService modelService;

	private ProductPricePopulator productPricePopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		productPricePopulator = new ProductPricePopulator();
		productPricePopulator.setModelService(modelService);
		productPricePopulator.setCommercePriceService(commercePriceService);
		productPricePopulator.setPriceDataFactory(priceDataFactory);
	}


	@Test
	public void testPopulateBuyPrice()
	{
		final ProductModel source = mock(ProductModel.class);
		final PriceInformation priceInformation = mock(PriceInformation.class);
		final PriceValue priceValue = mock(PriceValue.class);
		final PriceData priceData = mock(PriceData.class);

		given(priceValue.getCurrencyIso()).willReturn(CURRENCY_ISO);
		given(Double.valueOf(priceValue.getValue())).willReturn(PRICE_VALUE);
		given(priceInformation.getPriceValue()).willReturn(priceValue);
		given(commercePriceService.getWebPriceForProduct(source)).willReturn(priceInformation);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(PRICE_VALUE.doubleValue()), CURRENCY_ISO))
				.willReturn(priceData);

		final ProductData result = new ProductData();
		productPricePopulator.populate(source, result);

		Assert.assertEquals(priceData, result.getPrice());
	}


	@Test
	public void testPopulateFromPrice()
	{
		final ProductModel source = mock(ProductModel.class);
		final PriceInformation priceInformation = mock(PriceInformation.class);
		final PriceValue priceValue = mock(PriceValue.class);
		final PriceData priceData = mock(PriceData.class);
		final VariantProductModel variantProductModel = mock(VariantProductModel.class);

		given(source.getVariants()).willReturn(Collections.singleton(variantProductModel));
		given(priceValue.getCurrencyIso()).willReturn(CURRENCY_ISO);
		given(Double.valueOf(priceValue.getValue())).willReturn(PRICE_VALUE);
		given(priceInformation.getPriceValue()).willReturn(priceValue);
		given(commercePriceService.getFromPriceForProduct(source)).willReturn(priceInformation);
		given(priceDataFactory.create(PriceDataType.FROM, BigDecimal.valueOf(PRICE_VALUE.doubleValue()), CURRENCY_ISO))
				.willReturn(priceData);

		final ProductData result = new ProductData();
		productPricePopulator.populate(source, result);

		Assert.assertEquals(priceData, result.getPrice());
	}
}
