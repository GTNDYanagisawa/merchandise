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
package de.hybris.platform.webservices;



/**
 *
 */
public interface CrudActionNotifable<RESOURCE>
{
	void notifyBeforeGet();

	void notifyAfterGet();

	void notifyBeforePut();

	void notifyAfterPut();

	void notifyBeforeDelete();

	void notifyAfterDelete();

	void notifyBeforePost();

	void notifyAfterPost();

	//void notifyBeforePut(RESOURCE resource);

	//void notifyAfterPut(RESOURCE resource);
}
