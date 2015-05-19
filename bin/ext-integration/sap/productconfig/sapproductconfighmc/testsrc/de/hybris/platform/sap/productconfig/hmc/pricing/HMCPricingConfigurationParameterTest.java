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

package de.hybris.platform.sap.productconfig.hmc.pricing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.productconfig.hmc.constants.SapproductconfighmcConstants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import sap.hybris.integration.models.services.SalesAreaService;


public class HMCPricingConfigurationParameterTest
{

	private static final String PRICING_PROCEDURE_EXAMPLE = "PRICING_PROCEDURE";
	private static final String BASE_PRICE_EXAMPLE = "BASE_PRICE";
	private static final String SELECTED_OPTIONS_EXAMPLE = "SELECTED_OPTION";
	private static final String SALES_ORGANIZATION_EXAMPLE = "SALES_ORGANIZATION";
	private static final String DISTRIBUTION_CHANNEL_EXAMPLE = "DISTRIBUTION_CHANNEL";
	private static final String DIVISION_EXAMPLE = "DIVISION";
	private static final String CURRENCY_SAP_CODE = "USD";
	private static final String UNIT_SAP_CODE = "ST";

	@Mock
	protected ModuleConfigurationAccess moduleConfigurationAccess;

	@Mock
	protected SalesAreaService commonSalesAreaService;

	@Mock
	protected CurrencyModel currencyModel;

	@Mock
	protected UnitModel unitModel;


	private HMCPricingConfigurationParameter pricingParameter;



	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		pricingParameter = new HMCPricingConfigurationParameter();
		pricingParameter.setModuleConfigurationAccess(moduleConfigurationAccess);
		pricingParameter.setCommonSalesAreaService(commonSalesAreaService);
	}

	@Test
	public void testGetPricingProcedure()
	{
		when(moduleConfigurationAccess.getProperty(SapproductconfighmcConstants.CONFIGURATION_PRICING_PROCEDURE)).thenReturn(
				PRICING_PROCEDURE_EXAMPLE);

		final String priceProcedure = pricingParameter.getPricingProcedure();
		assertEquals(PRICING_PROCEDURE_EXAMPLE, priceProcedure);
	}

	@Test
	public void testGetCondFuncForBasePrice()
	{
		when(moduleConfigurationAccess.getProperty(SapproductconfighmcConstants.CONFIGURATION_CONDITION_FUNCTION_BASE_PRICE))
				.thenReturn(BASE_PRICE_EXAMPLE);

		final String condFuncBasePrice = pricingParameter.getTargetForBasePrice();
		assertEquals(BASE_PRICE_EXAMPLE, condFuncBasePrice);
	}

	@Test
	public void testGetCondFuncForSelectedOptions()
	{
		when(moduleConfigurationAccess.getProperty(SapproductconfighmcConstants.CONFIGURATION_CONDITION_FUNCTION_SECLECTED_OPTIONS))
				.thenReturn(SELECTED_OPTIONS_EXAMPLE);

		final String condFuncSelectedOptions = pricingParameter.getTargetForSelectedOptions();
		assertEquals(SELECTED_OPTIONS_EXAMPLE, condFuncSelectedOptions);
	}

	@Test
	public void testGetPricingProcedureValueNotDefined()
	{
		final String priceProcedure = pricingParameter.getPricingProcedure();
		assertNull(priceProcedure);
	}

	@Test
	public void testGetCondFuncForBasePriceValueNotDefined()
	{
		final String condFuncBasePrice = pricingParameter.getTargetForBasePrice();
		assertNull(condFuncBasePrice);
	}

	@Test
	public void testGetCondFuncForSelectedOptionsValueNotDefined()
	{
		final String condFuncSelectedOptions = pricingParameter.getTargetForSelectedOptions();
		assertNull(condFuncSelectedOptions);
	}

	@Test
	public void testGetSalesOrganization()
	{
		when(commonSalesAreaService.getSalesOrganization()).thenReturn(SALES_ORGANIZATION_EXAMPLE);

		final String salesOrganization = pricingParameter.getSalesOrganization();
		assertEquals(SALES_ORGANIZATION_EXAMPLE, salesOrganization);
	}

	@Test
	public void testGetSalesOrganizationValueNotDefined()
	{
		final String salesOrganization = pricingParameter.getSalesOrganization();
		assertNull(salesOrganization);
	}

	@Test
	public void testGetDistributionChannel()
	{
		when(commonSalesAreaService.getDistributionChannelForConditions()).thenReturn(DISTRIBUTION_CHANNEL_EXAMPLE);

		final String distributionChannel = pricingParameter.getDistributionChannelForConditions();
		assertEquals(DISTRIBUTION_CHANNEL_EXAMPLE, distributionChannel);
	}

	@Test
	public void testGetDistributionChannelValueNotDefined()
	{
		final String distributionChannel = pricingParameter.getDistributionChannelForConditions();
		assertNull(distributionChannel);
	}

	@Test
	public void testGetDivision()
	{
		when(commonSalesAreaService.getDivisionForConditions()).thenReturn(DIVISION_EXAMPLE);

		final String division = pricingParameter.getDivisionForConditions();
		assertEquals(DIVISION_EXAMPLE, division);
	}

	@Test
	public void testGetDivisionValueNotDefined()
	{
		final String division = pricingParameter.getDivisionForConditions();
		assertNull(division);
	}

	@Test
	public void testRetrieveCurrencySapCode()
	{
		when(currencyModel.getSapCode()).thenReturn(CURRENCY_SAP_CODE);

		final String currencySapCode = pricingParameter.retrieveCurrencySapCode(currencyModel);
		assertEquals(CURRENCY_SAP_CODE, currencySapCode);
	}

	@Test
	public void testRetrieveUnitSapCode()
	{
		when(unitModel.getSapCode()).thenReturn(UNIT_SAP_CODE);

		final String unitSapCode = pricingParameter.retrieveUnitSapCode(unitModel);
		assertEquals(UNIT_SAP_CODE, unitSapCode);
	}

}
