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
package de.hybris.platform.sap.sapcarintegration.constants;

/**
 * Global class for all Sapcarintegration constants. You can add global constants for your extension into this class.
 */
public final class SapcarintegrationConstants extends GeneratedSapcarintegrationConstants
{
	public static final String EXTENSIONNAME = "sapcarintegration";
	
	public static final String PROXY_HOST = "proxyhost";
	public static final String PROXY_PORT = "proxyport";
	public static final String SEPARATOR = "/";

	public static final String HTTP_METHOD_PUT = "PUT";
	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HTTP_HEADER_ACCEPT = "Accept";
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	
	public static final String SAPCLIENT_PARAM = "P_SAPClient";
	
	public static final String POSSALES_ENTITYSETNAME_QUERY = "POSSalesQuery";
	
	public static final String POSSALES_ENTITYSETNAME_RESULT = "POSSalesQueryResults";
		
	public static final String LOCATION_ENTITYSETNAME_QUERY = "RetailLocationQuery";
	
	public static final String LOCATION_ENTITYSETNAME_RESULT = "RetailLocationQueryResults";
	
	public static final String MCSALES_ENTITYSETNAME_QUERY = "MultiChannelSalesOrdersQuery";
	
	public static final String MCSALES_ENTITYSETNAME_RESULT = "MultiChannelSalesOrdersQueryResults";
	
	
	public static final String SELECT_POSSALES_HEADER = "TransactionNumber,RetailStoreID,OperatorID,OrderChannel,BeginTimestamp,OrderChannelName,Location,LocationName,RetailStoreID,BusinessDayDate,TransactionIndex,TransactionCurrency,CustomerNumber,SalesUnit,SalesAmount,TaxExcludedAmount,DistributedHeaderTaxAmount";
	
	public static final String SELECT_POSSALES_ITEM = "Article,ArticleName,SalesUnit,SalesQuantityInSalesUnit,TransactionNumber,RetailStoreID,OperatorID,OrderChannel,OrderChannelName,Location,LocationName,RetailStoreID,BusinessDayDate,TransactionIndex,TransactionCurrency,CustomerNumber,SalesUnit,SalesAmount,TaxExcludedAmount,DistributedHeaderTaxAmount";
		
	public static final String SELECT_MCSALES_HEADER = "SAPClient,CustomerNumber,TransactionNumber,SalesDocumentType,RetailStoreID,BusinessDayDate,TransactionIndex,CreationDate,CreationTime,OrderChannel,OrderChannelName,SalesOrganization,DistributionChannel,OrganizationDivision,OverallOrderProcessStatus,OverallOrderProcessStatusDesc,TransactionCurrency,TaxAmount,TotalNetAmount";
	
	public static final String SELECT_MCSALES_ITEM = "SAPClient,CustomerNumber,TransactionNumber,SalesDocumentType,CreationDate,CreationTime,Article,ArticleName,OrderChannel,OrderChannelName,SalesOrganization,DistributionChannel,OrganizationDivision,OrderQuantityUnit,UnitOfMeasureName,UnitOfMeasure_E,PaymentMethod,OverallOrderProcessStatus,OverallOrderProcessStatusDesc,TransactionCurrency,OrderQuantity,TaxAmount,TotalNetAmount";
	
	public static final String SELECT_LOCATION = "HouseNumber,Location,StreetName,POBox,PostalCode,CityName,Country";

	public static final String DEFAULT_DATE = "99991231";
	public static final String DEFAULT_TIME = "000000";

	public static final String DEFAULT_DATE_TIME = "99991231000000";
	
	
	private SapcarintegrationConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
