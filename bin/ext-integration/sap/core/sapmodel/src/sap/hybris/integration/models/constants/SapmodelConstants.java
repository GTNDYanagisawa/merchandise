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
package sap.hybris.integration.models.constants;

/**
 * Global class for all Sapmodel constants. You can add global constants for your extension into this class.
 */
public final class SapmodelConstants extends GeneratedSapmodelConstants
{
	public static final String EXTENSIONNAME = "sapmodel";

	public static final String CONFIGURATION_PROPERTY_CURRENCY = "defaultCurrency";
	
	
	public static final String CONFIGURATION_PROPERTY_SALES_ORG = "sapcommon_salesOrganization";
	public static final String CONFIGURATION_PROPERTY_DISTRIBUTION_CHANNEL = "sapcommon_distributionChannel";
	public static final String CONFIGURATION_PROPERTY_DIVISION = "sapcommon_division";

	public static final String CONFIGURATION_PROPERTY_TRANSACTION_TYPE = "sapcommon_transactionType";

	/** SAP PRICING CONSTANT ATTRIBURES **/

	public static final String CONFIGURATION_PROPERTY_REFERENCE_CUSTOMER = "sapcommon_referenceCustomer";

	private SapmodelConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
