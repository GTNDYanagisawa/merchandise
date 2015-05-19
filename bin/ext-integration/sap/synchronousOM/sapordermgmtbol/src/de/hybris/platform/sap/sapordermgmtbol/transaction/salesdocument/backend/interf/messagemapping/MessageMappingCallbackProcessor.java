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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.BackendMessage;


/**
 * Defines a callback function that is called during message mapping
 */
public interface MessageMappingCallbackProcessor
{


	/**
	 * Method is called during message replacement. Implement this message if you want to change the message before it
	 * gets replaced. E.g.: Replace message arguments .<br>
	 * It's also possible to remove messages via returning false.
	 * 
	 * @param message
	 *           Back end message to evaluate
	 * @return Do we want to keep the message?
	 */
	public boolean process(BackendMessage message);

	/**
	 * Should return the id of the message mapping callback processor. Returned string must be equals to the callbackId
	 * specified in a message rule (messages.xml) in order to get called
	 * 
	 * @return ID of callback
	 */
	public String getId();

}
