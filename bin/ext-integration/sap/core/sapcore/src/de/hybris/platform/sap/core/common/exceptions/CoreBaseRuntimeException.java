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
package de.hybris.platform.sap.core.common.exceptions;

import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.core.common.message.MessageListHolder;


/**
 * Core base runtime exception.
 */
public class CoreBaseRuntimeException extends RuntimeException implements MessageListHolder
{

	private static final long serialVersionUID = 9213379737622022954L;

	/**
	 * Container for messages.
	 */
	protected MessageList messageList = new MessageList(); // NOPMD

	/**
	 * Standard constructor for CoreBaseRuntimeException with no detail message.
	 */
	public CoreBaseRuntimeException()
	{
		super();
	}

	/**
	 * Standard constructor for CoreBaseRuntimeException with the specified detail message.
	 * 
	 * @param message
	 *           the detail message.
	 */
	public CoreBaseRuntimeException(final String message)
	{
		super(message);
	}

	/**
	 * Standard constructor for CoreBaseRuntimeException with the specified detail message and root cause.
	 * 
	 * @param message
	 *           message text.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public CoreBaseRuntimeException(final String message, final Throwable rootCause)
	{
		super(message, rootCause);
	}

	/**
	 * Standard constructor for CoreBaseRuntimeException using a message object see
	 * {@link de.hybris.platform.sap.core.common.message.Message} for details. <br>
	 * 
	 * @param message
	 *           message which identifies the error message.
	 */
	public CoreBaseRuntimeException(final Message message)
	{
		super(message != null ? message.getMessageText() : "");
		addMessage(message);
	}

	/**
	 * Standard constructor for CoreBaseRuntimeException using a message object see
	 * {@link de.hybris.platform.sap.core.common.message.Message} for details.
	 * 
	 * @param message
	 *           message object which identifies the error message.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public CoreBaseRuntimeException(final Message message, final Throwable rootCause)
	{
		super(message != null ? message.getDescription() : "", rootCause);
		addMessage(message);
	}

	/**
	 * Standard constructor for CoreBaseRuntimeException using message objects see
	 * {@link de.hybris.platform.sap.core.common.message.MessageList} for details.
	 * 
	 * @param messageList
	 *           list of message object which identifies the error message.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public CoreBaseRuntimeException(final MessageList messageList, final Throwable rootCause)
	{
		super(rootCause);
		this.messageList.add(messageList);
	}

	/**
	 * Standard constructor for CoreBaseRuntimeException using message objects see
	 * {@link de.hybris.platform.sap.core.common.message.MessageList} for details.
	 * 
	 * @param messageList
	 *           list of message object which identifies the error message.
	 */
	public CoreBaseRuntimeException(final MessageList messageList)
	{
		super();
		this.messageList.add(messageList);
	}

	/**
	 * Add a additional message to the exception. <br>
	 * 
	 * @param message
	 *           message object which identifies the error message.
	 */
	public void addMessage(final Message message)
	{
		messageList.add(message);
	}

	/**
	 * Clears all messages in the message list.<br>
	 */
	public void clearMessages()
	{
		messageList.clear();
	}

	/**
	 * Returns the messages of the Business Object.
	 * 
	 * @return message list of the
	 */
	public MessageList getMessageList()
	{
		return messageList;
	}

}
