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

/**
 * Class to control the Load operation provided to ERP LORD API (FM ERP_LORD_LOAD)
 */
public class LoadOperation
{

	/** Create document */
	final static public String create = "H";
	/** Load document in display mode */
	final static public String display = "A";
	/** Load document in edit mode */
	final static public String edit = "V";

	private String op = null;
	private boolean changeable = false;

	/**
	 * @return the current Load operation
	 */
	public String getLoadOperation()
	{
		return this.op;
	}

	/**
	 * @param operation
	 *           The operation to use
	 */
	public void setLoadOperation(final String operation)
	{
		this.op = operation;
		if (operation.equals(LoadOperation.create) || operation.equals(LoadOperation.edit))
		{
			this.changeable = true;
		}
		else
		{
			this.changeable = false;
		}
	}

	/**
	 * @return Depending on the set operation, a document is changeable (create or edit mode) or not (display mode)
	 */
	public boolean isChangeable()
	{
		return changeable;
	}

}
