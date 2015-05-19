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
package de.hybris.platform.sap.productconfig.runtime.interf;

import java.util.Date;


/**
 * Key to identify a knowledge base (KB) for product configuration. This Object is immutable.
 * 
 */
public interface KBKey
{

	/**
	 * @return the product code
	 */
	public String getProductCode();

	/**
	 * @return the knowledge base name
	 */
	public String getKbName();

	/**
	 * @return the knowledge base logical system
	 */
	public String getKbLogsys();

	/**
	 * @return the knowledge base version
	 */
	public String getKbVersion();

	/**
	 * @return the date
	 */
	public Date getDate();

}