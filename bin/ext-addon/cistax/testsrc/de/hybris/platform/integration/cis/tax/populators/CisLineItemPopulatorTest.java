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
import de.hybris.platform.commerceservices.externaltax.TaxCodeStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.integration.commons.OndemandDiscountedOrderEntry;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.cis.api.model.CisLineItem;


/**
 *
 *
 */
@UnitTest
public class CisLineItemPopulatorTest
{
	private final static String PRODUCT_CODE = "test-product";
	private final static String PRODUCT_DESC = "This is a product";
	private final static String PRODUCT_TAX_CODE = "test-tax-code";
	private CisLineItemPopulator cisLineItemPopulator;

	@Mock
	private TaxCodeStrategy taxCodeStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		cisLineItemPopulator = new CisLineItemPopulator();
		cisLineItemPopulator.setTaxCodeStrategy(taxCodeStrategy);
	}

	@Test
	public void shouldPopulateLineItem()
	{
		final CisLineItem cisLineItem = new CisLineItem();
		final OndemandDiscountedOrderEntry ondemandDiscountedOrderEntry = Mockito.mock(OndemandDiscountedOrderEntry.class);
		final AbstractOrderEntryModel abstractOrderEntry = Mockito.mock(AbstractOrderEntryModel.class);
		final ProductModel productModel = Mockito.mock(ProductModel.class);
		final AbstractOrderModel abstractOrder = Mockito.mock(AbstractOrderModel.class);

		given(abstractOrderEntry.getEntryNumber()).willReturn(Integer.valueOf(1));
		given(productModel.getCode()).willReturn(PRODUCT_CODE);
		given(productModel.getName()).willReturn(PRODUCT_DESC);
		given(abstractOrderEntry.getProduct()).willReturn(productModel);
		given(abstractOrderEntry.getQuantity()).willReturn(Long.valueOf(1));
		given(abstractOrderEntry.getTotalPrice()).willReturn(Double.valueOf(15.95));
		given(abstractOrderEntry.getOrder()).willReturn(abstractOrder);
		given(taxCodeStrategy.getTaxCodeForCodeAndOrder(PRODUCT_CODE, abstractOrder)).willReturn(PRODUCT_TAX_CODE);
		given(ondemandDiscountedOrderEntry.getDiscountedUnitPrice()).willReturn(BigDecimal.valueOf(15.95));
		given(ondemandDiscountedOrderEntry.getOrderEntry()).willReturn(abstractOrderEntry);
		cisLineItemPopulator.populate(ondemandDiscountedOrderEntry, cisLineItem);

		Assert.assertEquals(abstractOrderEntry.getEntryNumber(), cisLineItem.getId());
		Assert.assertEquals(PRODUCT_CODE, cisLineItem.getItemCode());
		Assert.assertEquals(PRODUCT_DESC, cisLineItem.getProductDescription());
		Assert.assertEquals(Integer.valueOf(abstractOrderEntry.getQuantity().intValue()), cisLineItem.getQuantity());
		Assert.assertEquals(BigDecimal.valueOf(abstractOrderEntry.getTotalPrice().doubleValue()), cisLineItem.getUnitPrice());
		Assert.assertEquals(PRODUCT_TAX_CODE, cisLineItem.getTaxCode());
	}
}
