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
 */

package de.hybris.platform.ycommercewebservices.errors.converters;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commercewebservicescommons.dto.error.ErrorWsDTO;

import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;


@UnitTest
public class CartModificationDataErrorConverterTest
{
	private static long OUT_OF_STOCK_VALUE = 0;
	private static long LOW_STOCK_VALUE = 10;
	private static long PRODUCT_QUANTITY = 20;
	private static String PRODUCT_CODE = "123456";
	private static Integer ENTRY_NUMBER = Integer.valueOf(1);
	public static final String NO_STOCK_MESSAGE = "cart.noStock";
	public static final String LOW_STOCK_MESSAGE = "cart.noStock";

	private CartModificationDataErrorConverter cartModificationDataErrorConverter = new CartModificationDataErrorConverter();

	private CartModificationData cartModificationData;
	@Mock
	private OrderEntryData entry;
	@Mock
	private ProductData product;

	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	@Mock
	private MessageSource messageSource;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		cartModificationDataErrorConverter = new CartModificationDataErrorConverter();
		cartModificationDataErrorConverter.setCommerceCommonI18NService(commerceCommonI18NService);
		cartModificationDataErrorConverter.setMessageSource(messageSource);

		cartModificationData = new CartModificationData();
		cartModificationData.setEntry(entry);
		cartModificationData.setQuantity(PRODUCT_QUANTITY);

		given(entry.getProduct()).willReturn(product);
		given(entry.getEntryNumber()).willReturn(ENTRY_NUMBER);
		given(product.getCode()).willReturn(PRODUCT_CODE);
		given(messageSource.getMessage(eq(NO_STOCK_MESSAGE), any(Object[].class), any(Locale.class))).willReturn(NO_STOCK_MESSAGE);
		given(messageSource.getMessage(eq(LOW_STOCK_MESSAGE), any(Object[].class), any(Locale.class)))
				.willReturn(LOW_STOCK_MESSAGE);
	}

	@Test
	public void testConvertWhenOutOfStock()
	{
		cartModificationData.setStatusCode(CommerceCartModificationStatus.NO_STOCK);
		cartModificationData.setQuantityAdded(OUT_OF_STOCK_VALUE);

		final List<ErrorWsDTO> result = cartModificationDataErrorConverter.convert(cartModificationData);

		Assert.assertEquals(1, result.size());
		final ErrorWsDTO error = result.get(0);
		Assert.assertEquals(CartModificationDataErrorConverter.TYPE, error.getType());
		Assert.assertEquals(CartModificationDataErrorConverter.SUBJECT_TYPE, error.getSubjectType());
		Assert.assertEquals(ENTRY_NUMBER.toString(), error.getSubject());
		Assert.assertEquals(CartModificationDataErrorConverter.NO_STOCK, error.getReason());
		Assert.assertEquals(NO_STOCK_MESSAGE, error.getMessage());
	}

	@Test
	public void testConvertWhenLowStock()
	{
		cartModificationData.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
		cartModificationData.setQuantityAdded(LOW_STOCK_VALUE);

		final List<ErrorWsDTO> result = cartModificationDataErrorConverter.convert(cartModificationData);

		Assert.assertEquals(1, result.size());
		final ErrorWsDTO error = result.get(0);
		Assert.assertEquals(CartModificationDataErrorConverter.TYPE, error.getType());
		Assert.assertEquals(CartModificationDataErrorConverter.SUBJECT_TYPE, error.getSubjectType());
		Assert.assertEquals(ENTRY_NUMBER.toString(), error.getSubject());
		Assert.assertEquals(CartModificationDataErrorConverter.LOW_STOCK, error.getReason());
		Assert.assertEquals(LOW_STOCK_MESSAGE, error.getMessage());
	}

}
