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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;


/**
 * The SalesDocumentConfiguration interface handles all kind of application settings relevant for the Sales Document.
 * 
 */
public interface SalesDocumentConfiguration extends BusinessObject
{

	/**
	 * Returns the property processType, which is provided by the sales module
	 * 
	 * @return processType
	 */
	@Deprecated
	public String getProcessType();

}
