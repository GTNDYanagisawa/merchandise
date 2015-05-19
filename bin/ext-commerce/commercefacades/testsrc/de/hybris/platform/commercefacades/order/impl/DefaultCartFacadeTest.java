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
package de.hybris.platform.commercefacades.order.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCartFacadeTest
{
	private DefaultCartFacade defaultCardFacade;

	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	@Mock
	private AbstractPopulatingConverter<CommerceCartModification, CartModificationData> cartModificationConverter;
	@Mock
	private CartService cartService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private AbstractPopulatingConverter<CartModel, CartData> cartConverter;
	@Mock
	private AbstractPopulatingConverter<CartModel, CartData> miniCartConverter;
	@Mock
	private ProductService productService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private DeliveryService deliveryService;
	@Mock
	private Converter<CountryModel, CountryData> countryConverter;


	private CartModel cartModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		defaultCardFacade = new DefaultCartFacade();
		defaultCardFacade.setCartService(cartService);
		defaultCardFacade.setCartConverter(cartConverter);
		defaultCardFacade.setMiniCartConverter(miniCartConverter);
		defaultCardFacade.setProductService(productService);
		defaultCardFacade.setCommerceCartService(commerceCartService);
		defaultCardFacade.setCartModificationConverter(cartModificationConverter);
		defaultCardFacade.setCountryConverter(countryConverter);
		defaultCardFacade.setDeliveryService(deliveryService);
		cartModel = new CartModel();
		cartModel.setCode("cart");
		final CartData cartData = new CartData();
		cartData.setCode("cart");

		given(cartConverter.convert(cartModel)).willReturn(cartData);
		given(miniCartConverter.convert(cartModel)).willReturn(cartData);
	}

	@Test
	public void testGetSessionCart()
	{
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cartService.getSessionCart()).willReturn(cartModel);

		final CurrencyModel curr = new CurrencyModel();
		curr.setIsocode("EUR");
		curr.setSymbol("$");
		curr.setDigits(Integer.valueOf(2));
		final LanguageModel languageModel = new LanguageModel();
		languageModel.setIsocode("en");

		given(commonI18NService.getCurrency(anyString())).willReturn(curr);
		given(commonI18NService.getCurrentCurrency()).willReturn(curr);
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);
		given(commerceCommonI18NService.getLocaleForLanguage(languageModel)).willReturn(Locale.UK);

		final CartData cart = defaultCardFacade.getSessionCart();
		Assert.assertEquals("cart", cart.getCode());
	}

	@Test
	public void testGetSessionCartNull()
	{
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.FALSE);

		final CurrencyModel curr = new CurrencyModel();
		curr.setIsocode("EUR");
		curr.setSymbol("$");
		curr.setDigits(Integer.valueOf(2));
		final LanguageModel languageModel = new LanguageModel();
		languageModel.setIsocode("en");
		final CartData emptyCart = new CartData();

		given(miniCartConverter.convert(null)).willReturn(emptyCart);
		given(commonI18NService.getCurrency(anyString())).willReturn(curr);
		given(commonI18NService.getCurrentCurrency()).willReturn(curr);
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);
		given(commerceCommonI18NService.getLocaleForLanguage(languageModel)).willReturn(Locale.UK);


		final CartData cart = defaultCardFacade.getSessionCart();
		Assert.assertNotNull(cart);
		Assert.assertEquals(emptyCart, cart);
	}

	@Test
	public void testHasSessionCartFalse()
	{
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.FALSE);
		final boolean hasCart = defaultCardFacade.hasSessionCart();
		Assert.assertEquals(Boolean.FALSE, Boolean.valueOf(hasCart));
	}

	@Test
	public void testHasSessionCartTrue()
	{
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		final boolean hasCart = defaultCardFacade.hasSessionCart();
		Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(hasCart));
	}

	@Test
	public void testGetMiniCart()
	{
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cartService.getSessionCart()).willReturn(cartModel);
		final CartData cart = defaultCardFacade.getMiniCart();
		Assert.assertEquals("cart", cart.getCode());
	}

	@Test
	public void testGetMiniCartEmpty()
	{
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.FALSE);

		final CurrencyModel curr = new CurrencyModel();
		curr.setIsocode("EUR");
		curr.setSymbol("$");
		curr.setDigits(Integer.valueOf(2));
		final LanguageModel languageModel = new LanguageModel();
		languageModel.setIsocode("en");
		final CartData emptyCart = new CartData();

		given(miniCartConverter.convert(null)).willReturn(emptyCart);
		given(commonI18NService.getCurrency(anyString())).willReturn(curr);
		given(commonI18NService.getCurrentCurrency()).willReturn(curr);
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);
		given(commerceCommonI18NService.getLocaleForLanguage(languageModel)).willReturn(Locale.UK);

		final CartData cart = defaultCardFacade.getMiniCart();
		Assert.assertNotNull(cart);
		Assert.assertEquals(emptyCart, cart);
	}

	@Test
	public void testAddToCart() throws CommerceCartModificationException
	{
		final UnitModel unit = new UnitModel();
		unit.setCode("unit");
		final ProductModel product = new ProductModel();
		product.setCode("prodCode");
		product.setUnit(unit);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(productService.getProductForCode(anyString())).willReturn(product);
		defaultCardFacade.addToCart("prodCode", 1);
	}

	@Test
	public void testUpdateCartEntry() throws CommerceCartModificationException
	{
		final UnitModel unit = new UnitModel();
		unit.setCode("unit");
		final ProductModel product = new ProductModel();
		product.setCode("prodCode");
		product.setUnit(unit);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(productService.getProductForCode(anyString())).willReturn(product);
		defaultCardFacade.updateCartEntry(0, 1);
	}

	@Test
	public void testGetDeliveryCountries()
	{
		final CountryModel country = Mockito.mock(CountryModel.class);
		final List<CountryModel> deliveryCountries = new ArrayList<>();
		deliveryCountries.add(country);
		deliveryCountries.add(country);
		given(deliveryService.getDeliveryCountriesForOrder(null)).willReturn(deliveryCountries);
		given(country.getName()).willReturn("PL");

		given(countryConverter.convert(country)).willReturn(new CountryData());
		final List<CountryData> results = defaultCardFacade.getDeliveryCountries();
		verify(deliveryService).getDeliveryCountriesForOrder(null);
		verify(countryConverter, Mockito.times(2)).convert(country);
		Assert.assertEquals(results.size(), 2);
	}
}
