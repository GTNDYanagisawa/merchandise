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
package de.hybris.platform.sap.core.bol.businessobject;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * Allows implementing object interaction with the backend using the backend manager. <br>
 * Implementing this interface tells the BusinessObjectManager to give the object a reference to the
 * BackendObjectManager. You need this reference to call methods of the back end logic (BE).
 */
public interface BackendAware
{

	/**
	 * Returns the backend business object.
	 * 
	 * @return backend business object
	 * @throws BackendException
	 *            {@link BackendException}
	 */
	public BackendBusinessObject getBackendBusinessObject() throws BackendException;

	/**
	 * Returns the backend business object.
	 * 
	 * @param initialize
	 *           if true, backend object is re-created
	 * @return backend business object
	 * @throws BackendException
	 *            {@link BackendException}
	 */
	public BackendBusinessObject getBackendBusinessObject(boolean initialize) throws BackendException;

}
