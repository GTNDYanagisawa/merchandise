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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util;

/**
 * Stores the result of a call to the backend. Is evaluated in backend layer implementations e.g. SalesDocumentERP,
 * SalesDocumentCRM
 * 
 */
public class BackendCallResult
{

	/**
	 * The constants for the result of the back end call. <br
	 */
	public enum Result
	{

		/**
		 * Call executed successfully
		 */
		success,
		/**
		 * Call execution failed
		 */
		failure
	}

	private final Result result;

	/**
	 * Stores the result of the back end call. <br>
	 * 
	 * @param result
	 *           the enum value for the success or failure
	 */
	public BackendCallResult(final Result result)
	{
		this.result = result;
	}

	/**
	 * The standard constructor will initialize to 'success'!
	 */
	public BackendCallResult()
	{
		result = Result.success;
	}

	/**
	 * @return result the enum value for the success or failure
	 */
	public Result getResult()
	{
		return result;
	}

	/**
	 * @return true if the get back end call was failure
	 */
	public boolean isFailure()
	{
		return result.equals(Result.failure);
	}

}
