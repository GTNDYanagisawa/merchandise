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
package de.hybris.platform.commercefacades.order.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.converters.populator.ProductBasicPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPrimaryImagePopulator;
import de.hybris.platform.commercefacades.product.converters.populator.VariantSelectedPopulator;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.commerceservices.order.impl.OrderEntryModifiableChecker;
import de.hybris.platform.commerceservices.strategies.ModifiableChecker;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


@UnitTest
public class OrderEntryPopulatorTest
{
	@Mock
	private ProductBasicPopulator<ProductModel, ProductData> productBasicPopulator; //NOPMD
	@Mock
	private VariantSelectedPopulator<ProductModel, ProductData> variantSelectedPopulator; //NOPMD
	@Mock
	private ProductPrimaryImagePopulator<ProductModel, ProductData> productPrimaryImagePopulator; //NOPMD
	@Mock
	private UrlResolver<ProductModel> productModelUrlResolver; //NOPMD
	@InjectMocks
	private final ProductPopulator productPopulator = new ProductPopulator(); //NOPMD
	@Mock
	private AbstractPopulatingConverter<ProductModel, ProductData> productConverter;
	@Mock
	private Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;
	@Mock
	private PriceDataFactory priceDataFactory;
	@InjectMocks
	private final OrderEntryPopulator orderEntryPopulator = new OrderEntryPopulator();

	private final AbstractPopulatingConverter<AbstractOrderEntryModel, OrderEntryData> entryConverter = new ConverterFactory<AbstractOrderEntryModel, OrderEntryData, OrderEntryPopulator>()
			.create(OrderEntryData.class, orderEntryPopulator);

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private AbstractOrderEntryModel entry;
	@Spy
	private final ModifiableChecker<AbstractOrderEntryModel> entryOrderChecker = new OrderEntryModifiableChecker();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		BDDMockito.given(entry.getOrder().getCurrency().getIsocode()).willReturn("no code");
	}

	@Test
	public void testConvert()
	{
		final DeliveryModeModel deliveryModeModel = mock(DeliveryModeModel.class);
		final DeliveryModeData deliveryModeData = mock(DeliveryModeData.class);
		final AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);
		final ProductModel productModel = mock(ProductModel.class);
		final ProductData productData = mock(ProductData.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final AbstractOrderModel abstractOrderModel = mock(AbstractOrderModel.class);
		final PriceData priceDataBase = mock(PriceData.class);
		final PriceData priceDataTotal = mock(PriceData.class);

		given(abstractOrderEntryModel.getEntryNumber()).willReturn(Integer.valueOf(2));
		given(abstractOrderEntryModel.getQuantity()).willReturn(Long.valueOf(33));
		given(abstractOrderEntryModel.getProduct()).willReturn(productModel);
		given(abstractOrderEntryModel.getDeliveryMode()).willReturn(deliveryModeModel);
		given(productConverter.convert(productModel)).willReturn(productData);
		given(deliveryModeConverter.convert(deliveryModeModel)).willReturn(deliveryModeData);
		given(abstractOrderEntryModel.getBasePrice()).willReturn(Double.valueOf(2.1));
		given(abstractOrderEntryModel.getTotalPrice()).willReturn(Double.valueOf(4.1));
		given(abstractOrderEntryModel.getOrder()).willReturn(abstractOrderModel);
		given(abstractOrderModel.getCurrency()).willReturn(currencyModel);
		given(currencyModel.getIsocode()).willReturn("isoCode");
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(2.1), currencyModel)).willReturn(priceDataBase);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(4.1), currencyModel)).willReturn(priceDataTotal);

		final OrderEntryData entryData = entryConverter.convert(abstractOrderEntryModel);

		Assert.assertEquals(Integer.valueOf(2), entryData.getEntryNumber());
		Assert.assertEquals(Long.valueOf(33), entryData.getQuantity());
		Assert.assertEquals(productData, entryData.getProduct());
		Assert.assertEquals(priceDataBase, entryData.getBasePrice());
		Assert.assertEquals(priceDataTotal, entryData.getTotalPrice());
		Assert.assertEquals(deliveryModeData, entryData.getDeliveryMode());
	}

	@Test
	public void testPopulateCartEntryWhenCanModify()
	{
		final OrderEntryData result = Mockito.spy(new OrderEntryData());
		Mockito.doReturn(Boolean.TRUE).when(entryOrderChecker).canModify(entry);
		expectSetUpdatableTrueCalled(result);

		try
		{
			entryConverter.populate(entry, result);
			Assert.fail("should set updatatable true when  entryOrderChecker.canModify(entry) return true");
		}

		catch (final ExpectedException e)
		{
			//ok here 
		}
	}

	@Test
	public void testPopulateCartEntryWhenCanNotModify()
	{
		final OrderEntryData result = Mockito.spy(new OrderEntryData());
		Mockito.doReturn(Boolean.FALSE).when(entryOrderChecker).canModify(entry);
		expectSetUpdatableFalseCalled(result);
		try
		{
			entryConverter.populate(entry, result);
			Assert.fail("should set updatatable false when  entryOrderChecker.canModify(entry) return false");
		}

		catch (final ExpectedException e)
		{
			//ok here 
		}
	}


	private void expectSetUpdatableTrueCalled(final OrderEntryData result)
	{
		BDDMockito.doThrow(new ExpectedException()).when(result).setUpdateable(true);
	}

	private void expectSetUpdatableFalseCalled(final OrderEntryData result)
	{
		BDDMockito.doThrow(new ExpectedException()).when(result).setUpdateable(false);
	}

	class ExpectedException extends RuntimeException
	{
		//
	}
}
