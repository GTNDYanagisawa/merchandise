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
package de.hybris.platform.sap.core.common.message;

/**
 * The MessageListHolder interface should be implemented by objects which holds a message list to handles messages.
 */
public interface MessageListHolder
{


	/**
	 * Constant to define the state valid.
	 */
	public final static int VALID = 0;


	/**
	 * Constant to define the state invalid.
	 */
	public final static int INVALID = 1;


	/**
	 * Add a message to the message list.
	 * 
	 * @param message
	 *           message to add
	 */
	public void addMessage(Message message);



	/**
	 * Clear all messages in the message list.
	 * 
	 */
	public void clearMessages();


	/**
	 * Returns the messages list object itself. Always the reference of the original object should be provided.
	 * 
	 * @return reference to message list
	 */
	public MessageList getMessageList();


}
