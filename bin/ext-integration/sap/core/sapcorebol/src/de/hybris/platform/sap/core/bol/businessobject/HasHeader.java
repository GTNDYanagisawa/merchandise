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



/**
 * Allows generic access to objects, which has an header object.
 */
public interface HasHeader
{

	/**
	 * Returns the header business object.
	 * 
	 * @return header business object
	 */
	public BusinessObjectBase getHeader();

}
