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
package de.hybris.platform.integration.cis.tax.strategies.impl;

import de.hybris.platform.commerceservices.service.data.CommerceOrderParameter;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class DefaultCisCartHashCalculationStrategyTest
{
	private final DefaultCisCartHashCalculationStrategy defaultCisCartHashCalcStrategy = new DefaultCisCartHashCalculationStrategy();
	private CartModel cartModel;

	@Before
	public void initCart()
	{
		cartModel = new CartModel();

		final AddressModel deliveryAddress = new AddressModel();
		deliveryAddress.setLine1("line1");
		deliveryAddress.setLine2("line2");
		deliveryAddress.setTown("town");
		deliveryAddress.setRegion(new RegionModel());
		deliveryAddress.getRegion().setIsocode("regionIso");
		deliveryAddress.setCountry(new CountryModel());
		deliveryAddress.getCountry().setIsocode("countryIso");
		deliveryAddress.setDistrict("district");
		deliveryAddress.setPostalcode("postalCode");
		cartModel.setDeliveryAddress(deliveryAddress);

		cartModel.setDeliveryFromAddress(deliveryAddress);

		final DeliveryModeModel deliveryMode = new DeliveryModeModel();
		deliveryMode.setCode("deliveryMode");
		cartModel.setDeliveryMode(deliveryMode);

		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("currency");
		cartModel.setCurrency(currencyModel);

		cartModel.setNet(Boolean.TRUE);
		cartModel.setDate(new Date());

		final CartEntryModel cartEntry = new CartEntryModel();
		cartEntry.setTotalPrice(Double.valueOf(10));
		cartEntry.setProduct(new ProductModel());
		cartEntry.getProduct().setCode("productCode");
		cartEntry.setQuantity(Long.valueOf(1));

		cartEntry.setDeliveryMode(new DeliveryModeModel());
		cartEntry.getDeliveryMode().setCode("cartEntryDeliveryMode");

		final AddressModel entryAddress = new AddressModel();
		entryAddress.setLine1("line1");
		entryAddress.setLine2("line2");
		entryAddress.setTown("town");
		entryAddress.setRegion(new RegionModel());
		entryAddress.getRegion().setIsocode("regionIso");
		entryAddress.setCountry(new CountryModel());
		entryAddress.getCountry().setIsocode("countryIso");
		entryAddress.setDistrict("district");
		entryAddress.setPostalcode("postalCode");
		cartEntry.setDeliveryAddress(entryAddress);
		cartModel.setEntries(Arrays.asList((AbstractOrderEntryModel) cartEntry));

		final PointOfServiceModel pos = new PointOfServiceModel();
		final AddressModel posAddress = new AddressModel();
		posAddress.setLine1("line1");
		posAddress.setLine2("line2");
		posAddress.setTown("town");
		posAddress.setRegion(new RegionModel());
		posAddress.getRegion().setIsocode("regionIso");
		posAddress.setCountry(new CountryModel());
		posAddress.getCountry().setIsocode("countryIso");
		posAddress.setDistrict("district");
		posAddress.setPostalcode("postalCode");
		pos.setAddress(entryAddress);
		cartEntry.setDeliveryPointOfService(pos);


	}

    private CommerceOrderParameter buildCommerceOrderParameter(final AbstractOrderModel abstractOrderModel, final List<String> additionalValues) {
        final CommerceOrderParameter parameter = new CommerceOrderParameter();
        parameter.setOrder(abstractOrderModel);
        parameter.setAdditionalValues(additionalValues);
        return parameter;
    }

	@Test
	public void shouldCalculateSameHash()
	{
		final String hash1 = defaultCisCartHashCalcStrategy.buildHashForAbstractOrder(buildCommerceOrderParameter(cartModel, null));
		final String hash2 = defaultCisCartHashCalcStrategy.buildHashForAbstractOrder(buildCommerceOrderParameter(cartModel, null));
		Assert.assertThat(hash1, CoreMatchers.equalTo(hash2));
	}

	@Test
	public void shouldCalculateDifferenttHashWithAddedValues()
	{
		final String hash1 = defaultCisCartHashCalcStrategy.buildHashForAbstractOrder(buildCommerceOrderParameter(cartModel, null));
		final String hash2 = defaultCisCartHashCalcStrategy.buildHashForAbstractOrder(buildCommerceOrderParameter(cartModel, Arrays.asList("2")));
		final String hash3 = defaultCisCartHashCalcStrategy.buildHashForAbstractOrder(buildCommerceOrderParameter(cartModel, Arrays.asList("3")));
		Assert.assertThat(hash1, CoreMatchers.not(CoreMatchers.equalTo(hash2)));
		Assert.assertThat(hash2, CoreMatchers.not(CoreMatchers.equalTo(hash3)));
		Assert.assertThat(hash1, CoreMatchers.not(CoreMatchers.equalTo(hash3)));

	}


	@Test
	public void shouldCalculateDifferentHashesWithCartDeliveryAddress()
	{
		final String hash1 = defaultCisCartHashCalcStrategy.buildHashForAbstractOrder(buildCommerceOrderParameter(cartModel, null));
		cartModel.getDeliveryAddress().setPostalcode("changed");
		final String hash2 = defaultCisCartHashCalcStrategy.buildHashForAbstractOrder(buildCommerceOrderParameter(cartModel, null));
		Assert.assertThat(hash1, CoreMatchers.not(CoreMatchers.equalTo(hash2)));
	}

	@Test
	public void shouldCalculateDifferentHashesWithCartDeliveryFromAddress()
	{
		final String hash1 = defaultCisCartHashCalcStrategy.buildHashForAbstractOrder(buildCommerceOrderParameter(cartModel, null));
		cartModel.getDeliveryFromAddress().setPostalcode("changed");
		final String hash2 = defaultCisCartHashCalcStrategy.buildHashForAbstractOrder(buildCommerceOrderParameter(cartModel, null));
		Assert.assertThat(hash1, CoreMatchers.not(CoreMatchers.equalTo(hash2)));
	}

	@Test
	public void shouldCalculateDifferentHashesWithCartEntry()
	{
		final String hash1 = defaultCisCartHashCalcStrategy.buildHashForAbstractOrder(buildCommerceOrderParameter(cartModel, null));
		cartModel.getEntries().get(0).setTotalPrice(Double.valueOf(15));
		final String hash2 = defaultCisCartHashCalcStrategy.buildHashForAbstractOrder(buildCommerceOrderParameter(cartModel, null));
		Assert.assertThat(hash1, CoreMatchers.not(CoreMatchers.equalTo(hash2)));
	}


}
