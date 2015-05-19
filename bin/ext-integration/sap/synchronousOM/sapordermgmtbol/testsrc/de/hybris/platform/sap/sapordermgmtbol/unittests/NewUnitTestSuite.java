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
package de.hybris.platform.sap.sapordermgmtbol.unittests;

import de.hybris.platform.sap.sapcommonbol.transaction.util.ConversionHelperTest;
import de.hybris.platform.sap.sapordermgmtbol.module.transaction.item.businessobject.impl.AlternativProductListImplTest;
import de.hybris.platform.sap.sapordermgmtbol.module.transaction.item.businessobject.impl.AlternativeProductImplTest;
import de.hybris.platform.sap.sapordermgmtbol.module.transaction.item.businessobject.impl.ItemBaseImplTest;
import de.hybris.platform.sap.sapordermgmtbol.module.transaction.item.businessobject.impl.ItemSalesDocTest;
import de.hybris.platform.sap.sapordermgmtbol.module.transaction.item.businessobject.impl.SimpleItemImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.impl.erp.BasketERPTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.businessobject.impl.BasketImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.BillToImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.BillingHeaderStatusImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.BillingItemStatusImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.ConnectedDocumentImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.ConnectedDocumentItemImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.ERPOrderStatusMappingTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.SalesDocumentBaseImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.SchedlineImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.ShipToImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.SimpleDocumentImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl.HeaderBaseImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl.HeaderSalesDocumentTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl.SimpleHeaderImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.interactionobjects.impl.CheckDocumentValidImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.interactionobjects.impl.CreateOrderImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.interactionobjects.impl.InitBasketImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl.TransactionConfigurationBaseTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.businessobject.impl.PricingInfoImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.businessobject.impl.SalesTransactionsFactoryImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.businessobject.impl.TransactionConfigurationImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.util.MessageUtilTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.impl.erp.OrderERPTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.OrderImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.PartnerListEntryImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.PartnerListImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.TextImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.AdditionalPricingImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.SalesDocumentERPTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.DocumentTypeMappingTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.ERPLO_APICustomerExitsImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingCallbackLoaderTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.businessobject.impl.SalesDocumentImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.backend.impl.SearchBackendERPTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.impl.SearchFilterImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.impl.SearchImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.impl.SearchResultImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.impl.SearchResultListImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.impl.ItemHierarchyTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.impl.PrettyPrinterTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.impl.SalesTransactionsUtilImplTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * 
 */
@RunWith(Suite.class)
@SuiteClasses(
{ HeaderSalesDocumentTest.class, SimpleItemImplTest.class, BasketImplTest.class, SimpleHeaderImplTest.class,
		HeaderBaseImplTest.class, PartnerListEntryImplTest.class, PartnerListEntryImplTest.class, PartnerListImplTest.class,
		TextImplTest.class, AlternativProductListImplTest.class, ItemSalesDocTest.class, BillingItemStatusImplTest.class,
		ConnectedDocumentImplTest.class, ConnectedDocumentItemImplTest.class, ERPOrderStatusMappingTest.class,
		SalesDocumentBaseImplTest.class, SimpleDocumentImplTest.class, ConversionHelperTest.class, ItemHierarchyTest.class,
		SalesTransactionsUtilImplTest.class, CheckDocumentValidImplTest.class, CreateOrderImplTest.class, InitBasketImplTest.class,
		PricingInfoImplTest.class, SalesTransactionsFactoryImplTest.class, OrderImplTest.class, SalesDocumentImplTest.class,
		PrettyPrinterTest.class, MessageUtilTest.class, SearchResultImplTest.class, SearchResultListImplTest.class,
		SearchImplTest.class, SearchBackendERPTest.class, SearchFilterImplTest.class, BillToImplTest.class,
		SchedlineImplTest.class, BillingHeaderStatusImplTest.class, DocumentTypeMappingTest.class, //
		MessageMappingUnitTestSuite.class, //
		StrategyUnitTestSuite.class, //
		ShipToImplTest.class, //
		TransactionConfigurationImplTest.class, //
		AlternativeProductImplTest.class, //
		SalesDocumentERPTest.class, //
		AdditionalPricingImplTest.class, //
		ERPLO_APICustomerExitsImplTest.class, //		
		ItemBaseImplTest.class, //
		BasketERPTest.class, //
		OrderERPTest.class, //
		TransactionConfigurationBaseTest.class, //
		MessageMappingCallbackLoaderTest.class

})
public class NewUnitTestSuite
{
	//not needed
}
