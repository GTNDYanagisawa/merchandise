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

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.externaltax.ExternalTaxDocument;
import de.hybris.platform.integration.commons.model.OndemandBaseStorePreferenceModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.testframework.TestUtils;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.hybris.commons.tenant.TenantContextService;


@ManualTest
public class CisCalculateExternalTaxesStrategyIntegrationTest extends ServicelayerTest
{
	private static final String ATS_FORMULA = "ON_HAND";
	private static final String ADDRESS_LINE = "1700 Broadway";
	private static final String POSTAL_CODE = "10019";
	private static final String COUNTRY = "US";
	private static final String STATE = "NY";
	private static final String TOWN = "New York";
	private static final String ENTRY_PRICE = "21.00";

	@Resource
	private DefaultCisCalculateExternalTaxesStrategy calculateExternalTaxesStrategy;

	@Resource
	private ModelService modelService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private TenantContextService tenantContextService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private BaseStoreService baseStoreService;

	@Resource
	private DeliveryService deliveryService;

	@Resource
	private CommonI18NService commonI18NService;


	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createDefaultUsers();
		importCsv("/ondemandcommon/test/testAcceleratorData.csv", "UTF-8");
		importCsv("/cistax/test/testTaxOrder.csv", "UTF-8");
		final BaseSiteModel site = baseSiteService.getBaseSiteForUID("testSite");
		Assert.assertNotNull("no baseSite with uid 'testSite", site);
		site.setChannel(SiteChannel.B2C);
		baseSiteService.setCurrentBaseSite(site, false);
		createTenantPreference();
	}

	protected void createTenantPreference()
	{
		final BaseStoreModel baseStoreModel = baseStoreService.getCurrentBaseStore();
		final OndemandBaseStorePreferenceModel tenantBaseStorePreferenceModel = modelService
				.create(OndemandBaseStorePreferenceModel.class);
		tenantBaseStorePreferenceModel.setBaseStore(baseStoreModel);
		modelService.save(tenantBaseStorePreferenceModel);
		baseStoreModel.setOndemandBaseStorePreferences(Arrays.asList(tenantBaseStorePreferenceModel));
		modelService.save(baseStoreModel);
	}

	@Test
	public void shouldCalculateTaxes()
	{

		try
		{
			TestUtils.disableFileAnalyzer("expected some exceptions in the log");

			final String orderCode = "test_tax_order";
			final OrderModel orderModel = getOrderForCode(orderCode);

			final ExternalTaxDocument externalTaxDocument = calculateExternalTaxesStrategy.calculateExternalTaxes(orderModel);

			Assert.assertNotNull(externalTaxDocument);
			Assert.assertNotNull(externalTaxDocument.getAllTaxes());
			Assert.assertNotNull(externalTaxDocument.getShippingCostTaxes());

			// Check some value, can't be certain ast taxes are subject to change
			Assert.assertTrue(externalTaxDocument.getAllTaxes().size() > 0);
			Assert.assertTrue(TaxValue.sumAbsoluteTaxValues(externalTaxDocument.getShippingCostTaxes()) > 0);
			Assert.assertTrue(TaxValue.sumAbsoluteTaxValues(externalTaxDocument.getTaxesForOrderEntry(0)) > 0);

		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	protected OrderModel getOrderForCode(final String orderCode)
	{
		final DefaultGenericDao defaultGenericDao = new DefaultGenericDao(OrderModel._TYPECODE);
		defaultGenericDao.setFlexibleSearchService(flexibleSearchService);
		final List<OrderModel> orders = defaultGenericDao.find(Collections.singletonMap(OrderModel.CODE, orderCode));
		Assert.assertFalse(orders.isEmpty());
		final OrderModel orderModel = orders.get(0);
		Assert.assertNotNull("Order should have been loaded from database", orderModel);
		setEntryPrices(orderModel);
		orderModel.setDeliveryAddress(getUSDeliveryAddress());
		orderModel.setCreationtime(new Date());
		final PriceValue deliveryCost = deliveryService.getDeliveryCostForDeliveryModeAndAbstractOrder(
				orderModel.getDeliveryMode(), orderModel);
		orderModel.setDeliveryCost(Double.valueOf(deliveryCost.getValue()));
		return orderModel;
	}

	protected AddressModel getUSDeliveryAddress()
	{
		final AddressModel address = modelService.create(AddressModel.class);
		address.setLine1(ADDRESS_LINE);
		address.setCountry(commonI18NService.getCountry(COUNTRY));
		address.setRegion(commonI18NService.getRegion(commonI18NService.getCountry(COUNTRY), STATE));
		address.setPostalcode(POSTAL_CODE);
		address.setTown(TOWN);
		return address;
	}

	protected void setEntryPrices(final AbstractOrderModel orderModel)
	{
		for (final AbstractOrderEntryModel entry : orderModel.getEntries())
		{
			entry.setTotalPrice(new Double(ENTRY_PRICE));
		}
	}
}
