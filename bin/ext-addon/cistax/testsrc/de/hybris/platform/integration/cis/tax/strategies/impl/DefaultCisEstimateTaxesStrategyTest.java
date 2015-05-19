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


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.externaltax.DeliveryFromAddressStrategy;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.externaltax.ExternalTaxDocument;
import de.hybris.platform.integration.cis.tax.service.CisTaxCalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultCisEstimateTaxesStrategyTest
{
	private final DefaultCisEstimateTaxesStrategy defaultCisEstimateTaxesStrategy = new DefaultCisEstimateTaxesStrategy();
	@Mock
	private CisTaxCalculationService cisTaxCalculationService;
	@Mock
	private ModelService modelService;
	@Mock
	private CartService cartService;
	@Mock
	private DeliveryFromAddressStrategy estimatedDeliveryFromAddressStrategy;
	@Mock
	private TypeService typeService;
	@Mock
	private CommonI18NService commonI18NService;

	@Before
	public void initTest()
	{
		MockitoAnnotations.initMocks(this.getClass());
		defaultCisEstimateTaxesStrategy.setCartService(cartService);
		defaultCisEstimateTaxesStrategy.setCisTaxCalculationService(cisTaxCalculationService);
		defaultCisEstimateTaxesStrategy.setModelService(modelService);
		defaultCisEstimateTaxesStrategy.setTypeService(typeService);
		defaultCisEstimateTaxesStrategy.setEstimatedDeliveryFromAddressStrategy(estimatedDeliveryFromAddressStrategy);
		defaultCisEstimateTaxesStrategy.setCommonI18NService(commonI18NService);
		BDDMockito.given(commonI18NService.getCountry(Mockito.anyString())).willReturn(new CountryModel());
	}

	@Test
	public void shouldEstimateTaxes()
	{
		final CartModel cartModel = new CartModel();
		final TaxValue taxValue1 = new TaxValue("1", 10, true, "USD");
		final TaxValue taxValue2 = new TaxValue("2", 15, true, "USD");
		final ExternalTaxDocument taxDoc = new ExternalTaxDocument();
		taxDoc.setShippingCostTaxes(new ArrayList<TaxValue>());
		taxDoc.setTaxesForOrderEntry(0, Arrays.asList(taxValue1, taxValue2));

		BDDMockito.given(
				cartService.clone(Mockito.any(ComposedTypeModel.class), Mockito.any(ComposedTypeModel.class),
						Mockito.any(AbstractOrderModel.class), Mockito.any(String.class))).willReturn(cartModel);
		BDDMockito.given(modelService.create(AddressModel.class)).willReturn(new AddressModel());
		BDDMockito.given(cisTaxCalculationService.calculateExternalTaxes(cartModel)).willReturn(taxDoc);

		final BigDecimal taxTotal = defaultCisEstimateTaxesStrategy.estimateTaxes(cartModel, "11211", "US");
		Assert.assertThat(taxTotal.setScale(1), CoreMatchers.equalTo(BigDecimal.valueOf(25).setScale(1)));

	}
}
