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
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;

import de.hybris.platform.commercefacades.product.data.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorfacades.order.converters.populator.GroupOrderEntryPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;


@UnitTest
public class GroupOrderEntryPopulatorTest
{


    public static final long MIN_PRICE_RANGE_TEST = 4L;
    public static final long MAX_PRICE_RANGE_TEST = 6L;
    public static final String BASE_PRODUCT_2_PRICE_RANGE_TEST = "baseProduct2";
    public static final long TOTAL_PRICE_RANGE_TEST = 15L;
    @Mock
	ProductService productService;

	PriceDataFactory priceDataFactory = new PriceDataFactory()
	{

		@Override
		public PriceData create(PriceDataType priceType, BigDecimal value, String currencyIso)
		{
			final PriceData priceData = new PriceData();

			priceData.setPriceType(priceType);
			priceData.setValue(value);
			priceData.setCurrencyIso(currencyIso);

			return priceData;
		}

		@Override
		public PriceData create(PriceDataType priceType, BigDecimal value, CurrencyModel currency)
		{
			return null;
		}
	};

	@InjectMocks
	protected GroupOrderEntryPopulator populator = new GroupOrderEntryPopulator();


	@Before
	public void setUp()
	{

		MockitoAnnotations.initMocks(this);

		when(productService.getProductForCode(anyString())).thenAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{

				final String code = (String) invocationOnMock.getArguments()[0];
				final ProductModel productModel = mock(ProductModel.class);
				when(productModel.getCode()).thenReturn(code);
				when(productModel.getName()).thenReturn("Name " + code);
				when(productModel.getDescription()).thenReturn("Description " + code);

				return productModel;
			}
		});


		populator.setPriceDataFactory(priceDataFactory);

	}

	@Test
	public void shouldGroupEntries()
	{

		AbstractOrderData order = new AbstractOrderData();
		final ArrayList<OrderEntryData> originalEntries = new ArrayList<>();
		order.setEntries(originalEntries);

		originalEntries.add(createOrderEntry("productCode1", "baseProduct1", 1L, 1L));
		originalEntries.add(createOrderEntry("productCode2", "baseProduct1", 2L, 5L));
		originalEntries.add(createOrderEntry("productCode3", null, 3L, 5L));
		originalEntries.add(createOrderEntry("productCode4", BASE_PRODUCT_2_PRICE_RANGE_TEST, MIN_PRICE_RANGE_TEST, TOTAL_PRICE_RANGE_TEST));
		originalEntries.add(createOrderEntry("productCode5", BASE_PRODUCT_2_PRICE_RANGE_TEST, MIN_PRICE_RANGE_TEST + 1, TOTAL_PRICE_RANGE_TEST));
		originalEntries.add(createOrderEntry("productCode6", BASE_PRODUCT_2_PRICE_RANGE_TEST, MAX_PRICE_RANGE_TEST, TOTAL_PRICE_RANGE_TEST));
		originalEntries.add(createOrderEntry("productCode7", "baseProduct3", 7L, 7L));
        OrderEntryData notGroupedEntry = createOrderEntry("productCode8", "nonMultid", MAX_PRICE_RANGE_TEST, TOTAL_PRICE_RANGE_TEST);
        notGroupedEntry.getProduct().getBaseOptions().get(0).setVariantType("AnotherTypeOfVariant");
        originalEntries.add(notGroupedEntry);

		populator.populate(mock(AbstractOrderModel.class), order);

		assertThat(order.getEntries().size(), is(5));

		for (OrderEntryData parentEntry : order.getEntries())
		{
            validatePriceRange(parentEntry);

			if (parentEntry.getEntries() != null)
			{
				long totalQuantities = 0;
				int totalPrice = 0;
				for (OrderEntryData childEntry : parentEntry.getEntries())
				{
					final OrderEntryData firstEntry = parentEntry.getEntries().get(0);
					assertThat(childEntry.getProduct().getBaseProduct(), is(firstEntry.getProduct().getBaseProduct()));

					totalQuantities += childEntry.getQuantity();
					totalPrice += childEntry.getTotalPrice().getValue().intValue();
				}

				assertTrue(totalQuantities > 0);
				assertTrue(totalPrice > 0);

				assertThat(parentEntry.getQuantity(), is(totalQuantities));
                assertThat(parentEntry.getTotalPrice().getValue().intValue(), is(totalPrice));

				final OrderEntryData firstEntry = parentEntry.getEntries().get(0);
                validateProductInfo(parentEntry, firstEntry);
			}
		}



	}

    protected void validateProductInfo(OrderEntryData parentEntry, OrderEntryData firstEntry) {
        assertThat(parentEntry.getBasePrice(), is(firstEntry.getBasePrice()));
        assertThat(parentEntry.getProduct().getCode(), is(firstEntry.getProduct().getBaseProduct()));
        assertThat(parentEntry.getProduct().getImages(), is(firstEntry.getProduct().getImages()));
        assertThat(parentEntry.getProduct().getUrl(), is(firstEntry.getProduct().getUrl()));
    }

    protected void validatePriceRange(OrderEntryData parentEntry) {

        if (BASE_PRODUCT_2_PRICE_RANGE_TEST.equals(parentEntry.getProduct().getBaseProduct())){
            final long actualMinValue = parentEntry.getProduct().getPriceRange().getMinPrice().getValue().longValue();
            final long actualMaxValue = parentEntry.getProduct().getPriceRange().getMaxPrice().getValue().longValue();

            assertThat(actualMinValue, is(MIN_PRICE_RANGE_TEST));
            assertThat(actualMaxValue, is(MAX_PRICE_RANGE_TEST));
        }

    }


    public OrderEntryData createOrderEntry(String productCode, String baseProductCode, Long price, Long totalPrice)
	{
		final OrderEntryData entry = new OrderEntryData();

		final ProductData product = new ProductData();
		product.setCode(productCode);
		product.setBaseProduct(baseProductCode);
		product.setBaseOptions(new ArrayList<BaseOptionData>());
        if (baseProductCode != null)
        {
            BaseOptionData baseOption = new BaseOptionData();
            baseOption.setVariantType(GroupOrderEntryPopulator.VARIANT_TYPE);
            product.getBaseOptions().add(baseOption);
        }
        product.setImages(new ArrayList<ImageData>());

		product.setMultidimensional(baseProductCode != null);

		entry.setProduct(product);

		entry.setBasePrice(new PriceData());
		entry.getBasePrice().setValue(BigDecimal.valueOf(price));
		entry.getBasePrice().setCurrencyIso("USD");
		entry.getBasePrice().setPriceType(PriceDataType.BUY);

		entry.setTotalPrice(new PriceData());
		entry.getTotalPrice().setCurrencyIso("USD");
        entry.getTotalPrice().setValue(BigDecimal.valueOf(totalPrice));
		entry.getTotalPrice().setPriceType(PriceDataType.BUY);

		entry.setQuantity(1L);

		return entry;
	}

}
