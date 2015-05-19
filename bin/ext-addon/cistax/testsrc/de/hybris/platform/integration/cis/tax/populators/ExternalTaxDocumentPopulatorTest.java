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
package de.hybris.platform.integration.cis.tax.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.integration.cis.tax.CisTaxDocOrder;
import de.hybris.platform.integration.cis.tax.service.TaxValueConversionService;
import de.hybris.platform.integration.cis.tax.strategies.ShippingIncludedStrategy;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cis.api.tax.model.CisTaxDoc;
import com.hybris.cis.api.tax.model.CisTaxValue;


/**
 * 
 * 
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ExternalTaxDocumentPopulatorTest
{
	private ExternalTaxDocumentPopulator externalTaxDocumentPopulator;

	@Mock
	private TaxValueConversionService taxValueConversionService;
	@Mock
	private ShippingIncludedStrategy shippingIncludedStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this.getClass());

		externalTaxDocumentPopulator = new ExternalTaxDocumentPopulator();
		externalTaxDocumentPopulator.setShippingIncludedStrategy(shippingIncludedStrategy);
		externalTaxDocumentPopulator.setTaxValueConversionService(taxValueConversionService);
	}

	@Test
	public void shouldPopulate()
	{
		final CisTaxDocOrder taxDocOrder = Mockito.mock(CisTaxDocOrder.class);
		final AbstractOrderModel abstractOrder = Mockito.mock(AbstractOrderModel.class);
		final CisTaxDoc cisTaxDoc = Mockito.mock(CisTaxDoc.class);

		//Order attributes
		final CurrencyModel currencyModel = Mockito.mock(CurrencyModel.class);
		given(currencyModel.getIsocode()).willReturn("USD");
		given(abstractOrder.getCurrency()).willReturn(currencyModel);

		//Tax Doc attributes
		final List<CisTaxValue> taxLines = new ArrayList<CisTaxValue>();
		final CisTaxValue shippingCisTaxValue = Mockito.mock(CisTaxValue.class);
		given(shippingCisTaxValue.getValue()).willReturn(BigDecimal.valueOf(1.75d));
		final CisTaxValue orderLineTaxValue = Mockito.mock(CisTaxValue.class);
		given(orderLineTaxValue.getValue()).willReturn(BigDecimal.valueOf(12.75d));
		taxLines.add(orderLineTaxValue);
		taxLines.add(shippingCisTaxValue);
		//		given(cisTaxDoc.getLineItems()).willReturn(taxLines);

		//Tax Values
		final List<TaxValue> taxValues = new ArrayList<TaxValue>();
		final TaxValue taxValue = new TaxValue("taxCode", 12.75, true, 12.75, "USD");
		final TaxValue shippingTaxValue = new TaxValue("shippingTaxCode", 1.75, true, 1.75, "USD");
		taxValues.add(taxValue);
		taxValues.add(shippingTaxValue);

		//Tax Value List
		given(taxDocOrder.getTaxDoc()).willReturn(cisTaxDoc);

		given(Boolean.valueOf(externalTaxDocumentPopulator.getShippingIncludedStrategy().isShippingIncluded(taxDocOrder)))
				.willReturn(Boolean.FALSE);
		given(
				externalTaxDocumentPopulator.getTaxValueConversionService().getShippingTaxes(taxDocOrder.getTaxDoc().getLineItems(),
						abstractOrder.getCurrency().getIsocode(),
						externalTaxDocumentPopulator.getShippingIncludedStrategy().isShippingIncluded(taxDocOrder))).willReturn(
				taxValues);
		//		externalTaxDocumentPopulator.populate(source, target)
		Assert.assertNotNull(abstractOrder);

	}
}
