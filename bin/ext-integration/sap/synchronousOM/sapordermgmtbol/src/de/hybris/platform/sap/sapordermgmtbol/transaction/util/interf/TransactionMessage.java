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
package de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf;

import java.util.Locale;


/**
 * This interface is used for transaction specific messages. <br>
 * 
 */
public interface TransactionMessage
{

	/**
	 * Get the message class defined in the back end.
	 * 
	 * @return the beMessageClass
	 */
	public String getBEMessageClass();

	/**
	 * get the message number defined in the back end.
	 * 
	 * @return the beMessageNumber
	 */
	public String getBEMessageNumber();

	/**
	 * @return the checkoutStepElement
	 */
	public String getCheckoutStepElement();

	/**
	 * Determine the message long text.
	 * 
	 * @return message text
	 */
	public String getMessageLongText();

	/**
	 * Determine the message text for the given locale.
	 * 
	 * @param locale
	 *           locale to get the correct translation.
	 * @return message text
	 */
	public String getMessageLongText(Locale locale);

	/**
	 * @return the msgId
	 */
	public String getMsgId();

	/**
	 * @return the msgNo
	 */
	public String getMsgNo();

	/**
	 * @return the msgType
	 */
	public String getMsgType();

	/**
	 * @return the msgV1
	 */
	public String getMsgV1();

	/**
	 * @return the msgV2
	 */
	public String getMsgV2();

	/**
	 * @return the msgV3
	 */
	public String getMsgV3();

	/**
	 * @return the msgV4
	 */
	public String getMsgV4();

	/**
	 * @return the resourceKeyLongtext
	 */
	public String getResourceKeyLongtext();

	/**
	 * Checks if this message is an error.<br>
	 * 
	 * @return true if this message is an error
	 */
	public boolean isError();

	/**
	 * @return the msgHide
	 */
	public boolean isMsgHide();

	/**
	 * @param beMessageClass
	 *           the beMessageClass to set
	 */
	public void setBeMessageClass(String beMessageClass);

	/**
	 * @param beMessageNumber
	 *           the beMessageNumber to set
	 */
	public void setBeMessageNumber(String beMessageNumber);

	/**
	 * @param checkoutStepElement
	 *           the checkoutStepElement to set
	 */
	public void setCheckoutStepElement(String checkoutStepElement);

	/**
	 * @param msgHide
	 *           the msgHide to set
	 */
	public void setMsgHide(boolean msgHide);

	/**
	 * @param msgId
	 *           the msgId to set
	 */
	public void setMsgId(String msgId);

	/**
	 * @param msgNo
	 *           the msgNo to set
	 */
	public void setMsgNo(String msgNo);

	/**
	 * @param msgType
	 *           the msgType to set
	 */
	public void setMsgType(String msgType);

	/**
	 * @param msgV1
	 *           the msgV1 to set
	 */
	public void setMsgV1(String msgV1);

	/**
	 * @param msgV2
	 *           the msgV2 to set
	 */
	public void setMsgV2(String msgV2);

	/**
	 * @param msgV3
	 *           the msgV3 to set
	 */
	public void setMsgV3(String msgV3);

	/**
	 * @param msgV4
	 *           the msgV4 to set
	 */
	public void setMsgV4(String msgV4);

	/**
	 * @param resourceKeyLongtext
	 *           the resourceKeyLongtext to set
	 */
	public void setResourceKeyLongtext(String resourceKeyLongtext);

	/**
	 * Retrieves message type (like error, warning)
	 * 
	 * @return message type
	 */
	public int getType();

	/**
	 * Retrieves message long text
	 * 
	 * @return message long text
	 */
	public String getMessageText();

}