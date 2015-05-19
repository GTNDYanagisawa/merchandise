/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.sap.sapordermgmtbol.constants;



/**
 * Global class for all Sapordermgmtbol constants. You can add global constants for your extension into this class.
 */
public final class SapordermgmtbolConstants extends GeneratedSapordermgmtbolConstants
{
	/**
	 * Extension name
	 */
	public static final String EXTENSIONNAME = "sapordermgmtbol";
	/**
	 * Prefix for beans related to this extension
	 */
	public static final String PREFIX = "sapOrdermgmt";

	private SapordermgmtbolConstants()
	{
		//empty to avoid instantiating this constant class
	}

	//Aliases - Business Objects
	/**
	 * Alias: Cart BO
	 */
	public static final String ALIAS_BO_CART = PREFIX + "CartBO";
	/**
	 * Alias: Order BO
	 */
	public static final String ALIAS_BO_ORDER = PREFIX + "OrderBO";
	/**
	 * Alias: Order History BO (has a different connection compared to Order BO)
	 */
	public static final String ALIAS_BO_ORDER_HISTORY = PREFIX + "OrderHistoryBO";
	/**
	 * Alias: Configuration Settings BO
	 */
	public static final String ALIAS_BO_TRANSACTION_CONFIGURATION = PREFIX + "TransactionConfigurationBO";
	/**
	 * Alias: Search BO
	 */
	public static final String ALIAS_BO_SEARCH = PREFIX + "SearchBO";

	//Bean ID's - Backend Objects


	/**
	 * Cart BE (BE stands for '_B_ack_e_ndObject')
	 */
	public static final String BEAN_ID_BE_CART_ERP = PREFIX + "DefaultCartBeERP";
	/**
	 * Order BE
	 */
	public static final String BEAN_ID_BE_ORDER_ERP = PREFIX + "DefaultOrderBeERP";
	/**
	 * Order History BE
	 */
	public static final String BEAN_ID_BE_ORDER_HISTORY_ERP = PREFIX + "DefaultOrderHistoryBeERP";

	//Aliases - Interaction Objects
	/**
	 * Interaction object : Create order
	 */
	public static final String ALIAS_INT_CREATE_ORDER = PREFIX + "CreateOrderInteraction";
	/**
	 * Interaction object: Init cart
	 */
	public static final String ALIAS_INT_INITBASKET = PREFIX + "InitCartInteraction";
	/**
	 * Interaction object: Add to cart
	 */
	public static final String ALIAS_INT_ADD_TO_BASKET_FUNCTIONS = PREFIX + "AddToBasketFunctions";
	/**
	 * Interaction object: Check document validity
	 */
	public static final String ALIAS_INT_CHECK_DOCUMENT_VALID = PREFIX + "CheckDocumentValid";

	//Other aliases
	/**
	 * Prototype scope bean / Connected document
	 */
	public static final String ALIAS_BEAN_CONNECTED_DOCUMENT = PREFIX + "ConnectedDocument";
	/**
	 * Prototype scope bean / ERP BE layer frequent exits
	 */
	public static final String ALIAS_BEAN_BE_CUST_EXIT = PREFIX + "ERPCustExit";
	/**
	 * Prototype scope bean / Backend util
	 */
	public static final String ALIAS_BEAN_BACKEND_UTIL = PREFIX + "BackendUtil";
	/**
	 * Prototype scope bean / Transactions factory
	 */
	public static final String ALIAS_BEAN_TRANSACTIONS_FACTORY = PREFIX + "TransactionsFactory";
	/**
	 * Prototype scope bean / Item list
	 */
	public static final String ALIAS_BEAN_ITEM_LIST = PREFIX + "ItemList";
	/**
	 * Prototype scope bean / Billing header status
	 */
	public static final String ALIAS_BEAN_BILLING_HEADER_STATUS = PREFIX + "BillingHeaderStatus";
	/**
	 * Prototype scope bean / Billing item status
	 */
	public static final String ALIAS_BEAN_BILLING_ITEM_STATUS = PREFIX + "BillingItemStatus";
	/**
	 * Prototype scope bean / Shipping status
	 */
	public static final String ALIAS_BEAN_SHIPPING_STATUS = PREFIX + "ShippingStatus";
	/**
	 * Prototype scope bean / Processing status
	 */
	public static final String ALIAS_BEAN_PROCESSING_STATUS = PREFIX + "ProcessingStatus";
	/**
	 * Prototype scope bean / Connected document item
	 */
	public static final String ALIAS_BEAN_CONNECTED_DOCUMENT_ITEM = PREFIX + "ConnectedDocumentItem";
	/**
	 * Prototype scope bean / Partner list
	 */
	public static final String ALIAS_BEAN_PARTNER_LIST = PREFIX + "PartnerList";
	/**
	 * Prototype scope bean / Text
	 */
	public static final String ALIAS_BEAN_TEXT = PREFIX + "Text";
	/**
	 * Prototype scope bean / Alternative product
	 */
	public static final String ALIAS_BEAN_ALTERNATIVE_PRODUCT = PREFIX + "AlternativeProduct";
	/**
	 * Prototype scope bean / Alternative product list
	 */
	public static final String ALIAS_BEAN_ALTERNATIVE_PRODUCT_LIST = PREFIX + "AlternativeProductList";
	/**
	 * Prototype scope bean / Additional pricing
	 */
	public static final String ALIAS_BEAN_ADDITIONAL_PRICING = PREFIX + "AdditionalPricing";
	/**
	 * Prototype scope bean / Schedule line
	 */
	public static final String ALIAS_BEAN_SCHEDLINE = PREFIX + "Schedline";
	/**
	 * Prototype scope bean / Partner list entry
	 */
	public static final String ALIAS_BEAN_PARTNER_LIST_ENTRY = PREFIX + "PartnerListEntry";
	/**
	 * Prototype scope bean / Overall header status
	 */
	public static final String ALIAS_BEAN_OVERALL_STATUS_ORDER = PREFIX + "OverallStatusOrder";
	/**
	 * Prototype scope bean / Util bean
	 */
	public static final String ALIAS_BEAN_SALES_TRANSACTIONS_UTIL = PREFIX + "SalesTransactionsUtil";
	/**
	 * Prototype scope bean / Item
	 */
	public static final String ALIAS_BEAN_ITEM = PREFIX + "Item";
	/**
	 * Prototype scope bean / Ship-to party
	 */
	public static final String ALIAS_BEAN_SHIP_TO = PREFIX + "ShipTo";
	/**
	 * Prototype scope bean / Bill-to party
	 */
	public static final String ALIAS_BEAN_BILL_TO = PREFIX + "BillTo";
	/**
	 * Prototype scope bean / Transfer item utility
	 */
	public static final String ALIAS_BEAN_TRANSFERITEM_UTILITY = PREFIX + "TransferItemUtility";
	/**
	 * Prototype scope bean / Sales document header
	 */
	public static final String ALIAS_BEAN_HEADER = PREFIX + "SalesdocHeader";
	/**
	 * Prototype scope bean / Backend message mapper
	 */
	public static final String ALIAS_BEAN_BACKEND_MESSAGE_MAPPER = PREFIX + "BackendMessageMapper";
	/**
	 * Prototype scope bean / Message mapping rule container
	 */
	public static final String ALIAS_BEAN_MESSAGE_MAPPING_RULES_CONTAINER = PREFIX + "MessageMappingRulesContainer";
	/**
	 * Prototype scope bean / Message mapping rules loader
	 */
	public static final String ALIAS_BEAN_MESSAGE_MAPPING_RULES_LOADER = PREFIX + "MessageMappingRulesLoader";
	/**
	 * Prototype scope bean / Search result list
	 */
	public static final String ALIAS_BEAN_SEARCH_RESULT_LIST = PREFIX + "SearchResultList";
	/**
	 * Prototype scope bean / Search result
	 */
	public static final String ALIAS_BEAN_SEARCH_RESULT_ENTRY = PREFIX + "SearchResult";

	//Strategies
	/**
	 * Prototype scope bean / Read strategy
	 */
	public static final String ALIAS_BEAN_READ_STRATEGY = PREFIX + "ReadStrategy";
	/**
	 * Prototype scope bean / Write strategy
	 */
	public static final String ALIAS_BEAN_WRITE_STRATEGY = PREFIX + "WriteStrategy";
	/**
	 * Prototype scope bean / Actions strategy
	 */
	public static final String ALIAS_BEAN_ACTIONS_STRATEGY = PREFIX + "ActionsStrategy";
	/**
	 * Prototype scope bean / Product configuration exchange strategy
	 */
	public static final String ALIAS_BEAN_PCFG_STRATEGY = PREFIX + "ProductConfigurationStrategy";
	/**
	 * Prototype scope bean / Close strategy (release order session in ERP backend)
	 */
	public static final String ALIAS_BEAN_CLOSE_STRATEGY = PREFIX + "CloseStrategy";


	// Mapper
	/**
	 * Prototype scope bean / Partner mapper
	 */
	public static final String ALIAS_BEAN_PARTNER_MAPPER = PREFIX + "PartnerMapper";
	/**
	 * Prototype scope bean / Header mapper
	 */
	public static final String ALIAS_BEAN_HEADER_MAPPER = PREFIX + "HeaderMapper";
	/**
	 * Prototype scope bean / Item mapper
	 */
	public static final String ALIAS_BEAN_ITEM_MAPPER = PREFIX + "ItemMapper";
	/**
	 * Prototype scope bean / Header text mapper
	 */
	public static final String ALIAS_BEAN_HEADER_TEXT_MAPPER = PREFIX + "HeaderTextMapper";
	/**
	 * Prototype scope bean / Item text mapper
	 */
	public static final String ALIAS_BEAN_ITEM_TEXT_MAPPER = PREFIX + "ItemTextMapper";
	/**
	 * Prototype scope bean / Incompletion mapper
	 */
	public static final String ALIAS_BEAN_INCOMPLETION_MAPPER = PREFIX + "IncompletionMapper";


	//Bean Ids - Caches
	/**
	 * Cache region: Delivery types
	 */
	public static final String BEAN_ID_CACHE_DELIVERY_TYPES = PREFIX + "DeliveryTypeCacheRegion";
	/**
	 * Cache region: Message mapping
	 */
	public static final String BEAN_ID_CACHE_MESSAGE_MAPPING = PREFIX + "MessageMappingCacheRegion";
	/**
	 * Cache region: User status
	 */
	public static final String BEAN_ID_CACHE_USER_STATUS = PREFIX + "UserStatusCacheRegion";


	//Configuration properties
	/**
	 * Configuration property: Condition type for header freight
	 */
	public static final String CONFIGURATION_PROPERTY_HEADER_CONDITION_TYPE_FREIGHT = "sapordermgmt_headerCondTypeFreight";
	/**
	 * Configuration property: Item sub total freight
	 */
	public static final String CONFIGURATION_PROPERTY_SOURCE_FREIGHT = "sapordermgmt_sourceFreight";
	/**
	 * Configuration property: Item sub total net without freight
	 */
	public static final String CONFIGURATION_PROPERTY_SOURCE_NET_WO_FREIGHT = "sapordermgmt_sourceNetWOFreight";
	/**
	 * Configuration property: Max hits
	 */
	public static final String CONFIGURATION_PROPERTY_SEARCH_MAX_HITS = "sapordermgmt_maxHits";
	/**
	 * Configuration property: Date range for order search
	 */
	public static final String CONFIGURATION_PROPERTY_SEARCH_DATE_RANGE = "sapordermgmt_dateRange";








}
