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
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl.erp;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * Configuration related Backend layer exception
 */
public class BackendConfigurationException extends BackendException
{

	private static final long serialVersionUID = 5775612108542388947L;

	/**
	 * @param msg
	 *           Error message
	 */
	public BackendConfigurationException(final String msg)
	{
		super(msg);
	}

}
