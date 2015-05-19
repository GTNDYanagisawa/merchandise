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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * This exception is meant to handle critical errors which must lead to an abortion of the program flow. It should not
 * occur in productive environments (i.e. does not indicate communication or resource issues) but reflects problems with
 * the call of the ECommerce ERP backend layer.
 * 
 */
public class BackendExceptionECOERP extends BackendException
{

	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 5173890011152490823L;

	/**
	 * Meant to handle critical errors which must lead to an abortion of the program flow. It should not occur in
	 * productive environments (i.e. does not indicate communication or resource issues) but reflects problems with the
	 * call of the ECommerce ERP backend layer
	 * 
	 * @param msg
	 *           Message for the Exception
	 */
	public BackendExceptionECOERP(final String msg)
	{
		super(msg);
	}

}
