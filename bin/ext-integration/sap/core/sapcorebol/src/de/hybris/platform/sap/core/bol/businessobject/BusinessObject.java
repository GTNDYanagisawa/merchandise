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

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.core.common.message.MessageListHolder;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Business Object interface.
 */
public interface BusinessObject extends MessageListHolder
{

	/**
	 * Retrieves the key for the object.
	 * 
	 * @return The object's key
	 */
	public TechKey getTechKey();

	/**
	 * Sets the key for the document.
	 * 
	 * @param techKey
	 *           Key to be set
	 */
	public void setTechKey(TechKey techKey);

	/**
	 * 
	 * This method creates a unique handle, as an alternative key for the business object, because at the creation point
	 * no techkey for the object exists. Therefore maybay the handle is needed to identify the object in backend
	 * 
	 */
	public void createUniqueHandle();


	/**
	 * This method sets the handle, as an alternative key for the business object, because at the creation point no
	 * techkey for the object exists. Therefore maybay the handle is needed to identify the object in backend
	 * 
	 * @param handle
	 *           the handle of business object which identifies the object in the backend, if the techkey still not
	 *           exists
	 * 
	 */
	public void setHandle(String handle);


	/**
	 * This method returns the handle, as an alternative key for the business object, because at the creation point no
	 * techKey for the object exists. Therefore maybe the handle is needed to identify the object in back end
	 * 
	 * return the handle of business object which is needed to identify the object in the back end, if the techKey still
	 * not exists
	 * 
	 * @return the BO handle
	 */
	public String getHandle();


	/**
	 * Returns the information, if a handle for the object exists.
	 * 
	 * @return true, if there is a handle false, if the handle is <code>null</code> or if the handle is an empty string.
	 */
	public boolean hasHandle();


	/**
	 * 
	 * The method returns an iterator over all sub objects of the BusinessObject.
	 * 
	 * @return Iterator to loop over sub objects
	 * 
	 */
	public Iterator getSubObjectIterator();


	/**
	 * Set the Business Object invalid, no property.
	 * 
	 */
	public void setInvalid();


	/**
	 * Set the Business Object valid, no property.
	 * 
	 */
	public void setValid();


	/**
	 * Returns if the business object is valid.
	 * 
	 * @return valid
	 * 
	 */
	public boolean isValid();


	/**
	 * copy the messages from an another BusinessObject and add this to the object.
	 * 
	 * @param bob
	 *           reference to a <code>BusinessObjectBase</code> object
	 */
	public void copyMessages(BusinessObjectBase bob);


	/**
	 * Copy the messages from an another Message list holder and add this to the object.
	 * 
	 * @param messageListHolder
	 *           reference to a <code>MessageListHolder</code> object
	 */
	public void copyMessages(MessageListHolder messageListHolder);

	/**
	 * Returns if the business object or one of it sub objects has a message.
	 * 
	 * @return <code>true</code>, if at least one message exists
	 */
	public boolean hasMessages();


	/**
	 * Removes all messages with the given key from the list of messages.<br>
	 * 
	 * @param resourceKey
	 *           resourceKey of messages
	 */
	public void clearMessages(String resourceKey);


	/**
	 * Clears all messages and set state of the Business Object to valid.
	 */
	public void clearOwnMessages();


	/**
	 * Returns if the business object itself has a message.
	 * 
	 * @return <code>true</code>, if at least one message exists
	 */
	public boolean hasOwnMessages();


	/**
	 * Returns the messages of the Business Object itself.
	 * 
	 * @return message list of Business Object without child objects
	 */
	public MessageList getOwnMessageList();


	/**
	 * Log an message to the IsaLocation of BusinessObject.
	 * 
	 * @param message
	 *           message to log
	 */
	public void logMessage(Message message);

	/**
	 * This method stores arbitrary data within this Business Object.
	 * 
	 * @param key
	 *           key with which the specified value is to be associated.
	 * @param value
	 *           value to be associated with the specified key.
	 */
	public void addExtensionData(String key, Object value);

	/**
	 * This method retrieves extension data associated with the Business Object.
	 * 
	 * @param key
	 *           key with which the specified value is to be associated.
	 * @return value which is associated with the specified key
	 */
	public Object getExtensionData(String key);

	/**
	 * This method removes extension data from the Business Object.
	 * 
	 * @param key
	 *           key of the extension data
	 */
	public void removeExtensionData(String key);


	/**
	 * This method retrieves all extension data associated with the Business Object.
	 * 
	 * @return all extension data as entry set
	 */
	public Set<Entry<String, Object>> getExtensionDataValues();


	/**
	 * This method removes all extensions data from the Business Object.
	 */
	public void removeExtensionDataValues();


	/**
	 * Sets the extension map to the given map.
	 * 
	 * @param extensionData
	 *           the new extension HashMap for the object
	 */
	public void setExtensionMap(Map<String, Object> extensionData);


	/**
	 * Returns the extension map.
	 * 
	 * @return extensionData, the extension Map of the object
	 */
	public Map<String, Object> getExtensionMap();

	/**
	 * This method is called after all properties have been set It needs to be defined in the Spring bean definition as
	 * init-method.
	 */
	public void init();

	/**
	 * This method is called before the bean is invalidated It needs to be defined in the Spring bean definition as
	 * destroy-method.
	 */
	public void destroy();

}
