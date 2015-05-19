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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Represents a List of <code>Message</code> objects. This class can be used to maintain a collection of such objects. <br>
 * It contains some useful additional methods to retrieve and group messages. <br>
 * The internal storage is organized using a List, so duplicates of items are allowed.
 */
public class MessageList implements Iterable<Message>
{
	/**
	 * List of messages.
	 */
	private final List<Message> messageList;

	/**
	 * Creates a new <code>MessageList</code> object.
	 */
	public MessageList()
	{
		messageList = new ArrayList<Message>(1);
	}

	/**
	 * Removes all messages with the given keys from the list of messages.<br>
	 * 
	 * @param resourceKeys
	 *           resourceKeys of messages
	 */
	public void remove(final String[] resourceKeys)
	{

		final Iterator<Message> iter = messageList.iterator();
		while (iter.hasNext())
		{
			final Message message = iter.next();
			for (int i = 0; i < resourceKeys.length; i++)
			{
				if (message.getResourceKey().equals(resourceKeys[i]))
				{
					iter.remove();
					break;
				}
			}
		}
	}

	/**
	 * Removes all messages with the given key from the list of messages.<br>
	 * 
	 * @param resourceKey
	 *           resourceKey of messages
	 */
	public void remove(final String resourceKey)
	{

		remove(new String[]
		{ resourceKey });
	}

	/**
	 * Adds a new <code>Message</code> to the list. If you try to add a message that is already present in the list, the
	 * new item will not be added and the method returns silently.<br>
	 * <br>
	 * <b>Implementation note:</b> This method performs a linear search and calls the equal() method on each item. For
	 * small amounts (less then 15) of items this may be ok. For more you should implement a better technique.
	 * 
	 * @param item
	 *           Message to be stored in <code>MessageList</code>
	 */
	public void add(final Message item)
	{

		if (!messageList.contains(item))
		{
			messageList.add(item);
		}
	}

	/**
	 * Adds a new <code>MessageList</code> to the list. <br>
	 * <strong>If you try to add a message that is already present in the list, the item will be added again!</strong><br>
	 * 
	 * @param items
	 *           Messages to be stored in <code>MessageList</code>
	 */
	public void add(final MessageList items)
	{

		this.messageList.addAll(items.messageList);
	}

	/**
	 * Returns the element at the specified position in this list.
	 * 
	 * @param index
	 *           index of element to return
	 * @return the element at the specified position in this list
	 */
	public Message get(final int index)
	{
		return messageList.get(index);
	}

	/**
	 * <p>
	 * Retrieves the <code>Message</code> of the list for a given type and for the given property.
	 * </p>
	 * <p>
	 * The type constants are stored in <code>Message</code>.
	 * </p>
	 * <b>Note</b> If the List contains more then one <code>Message</code> for the given combination of type and property
	 * only the first is returned. To retrieve all entries use <code>subList</code>.
	 * 
	 * @param type
	 *           Type of message as defined in <code>Message</code>
	 * @param property
	 *           Name of the property the message should be associated with
	 * @return <code>Message</code> object for the given search criteria or <code>null</code> if no object was found.
	 */
	public Message get(final int type, final String property)
	{

		final Iterator<Message> i = messageList.iterator();
		while (i.hasNext())
		{
			final Message message = i.next();
			if ((message.getType() == type) && property.equals(message.getProperty()))
			{
				return message;
			}
		}

		return null;
	}

	/**
	 * Removes all mappings from this list.
	 */
	public void clear()
	{
		messageList.clear();
	}

	/**
	 * Returns the number of elemts in this list.
	 * 
	 * @return the number of number of elemts in this list.
	 */
	public int size()
	{
		return messageList.size();
	}

	/**
	 * Returns true if this list contains no data.
	 * 
	 * @return <code>true</code> if list contains no data.
	 */
	public boolean isEmpty()
	{
		return messageList.isEmpty();
	}

	/**
	 * Returns <code>true</code> if this list contains the specified element.
	 * 
	 * @param value
	 *           value whose presence in this list is to be tested.
	 * @return <code>true</code> if this list contains the specified value.
	 */
	public boolean contains(final Message value)
	{
		return messageList.contains(value);
	}

	/**
	 * Returns <code>true</code> if this list contains a message of the given type.<br>
	 * The type constants are stored in <code>Message</code>.
	 * 
	 * @param type
	 *           Type of message as defined in <code>Message</code>
	 * @return <code>true</code> if message of type is present in list; otherwise <code>false</code>.
	 */
	public boolean contains(final int type)
	{

		final Iterator<Message> i = messageList.iterator();
		while (i.hasNext())
		{
			final Message message = i.next();
			if (message.getType() == type)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns <code>true</code> if this list contains a message with the given key.<br>
	 * 
	 * @param resourceKey
	 *           resourceKey of message
	 * @return <code>true</code> if message of type is present in list; otherwise <code>false</code>.
	 */
	public boolean contains(final String resourceKey)
	{

		final Iterator<Message> i = messageList.iterator();
		while (i.hasNext())
		{
			final Message message = i.next();
			if (message.getResourceKey().equals(resourceKey))
			{
				return true;
			}
		}

		return false;

	}

	/**
	 * Returns <code>true</code> if this list contains a message of the given type and for the given property.<br>
	 * The type constants are stored in <code>Message</code>.
	 * 
	 * @param type
	 *           Type of message as defined in <code>Message</code>
	 * @param property
	 *           Name of the property the message should be associated with
	 * @return <code>true</code> if message of type is present in list; otherwise <code>false</code>.
	 */
	public boolean contains(final int type, final String property)
	{
		return (get(type, property) != null);
	}

	/**
	 * Returns an iterator over the elements contained in the <code>MessageList</code>.
	 * 
	 * @return Iterator for this object
	 */
	public Iterator<Message> iterator()
	{
		return messageList.iterator();
	}

	/**
	 * Returns the MessageList as array .<br>
	 * 
	 * @return message array
	 */
	public Message[] toArray()
	{
		final Message[] messages = new Message[messageList.size()];
		int i = 0;
		for (final Message message : messageList)
		{
			messages[i] = message;
			i++;
		}
		return messages;
	}

	/**
	 * <p>
	 * Returns a sub list of the elements of this list, for the given selection criteria. All <code>Message</code>
	 * elemenets of the list for the given type and the given property are extracted and returned in a new
	 * <codeMessageList</code> object. If there are no elements matching the criteria <code>null</code> is returned.
	 * </p>
	 * <p>
	 * <b>Note</b> Only a <em>shallow</em> copy is performed. Both lists are containig references to the same objects in
	 * memory.
	 * </p>
	 * 
	 * @param type
	 *           Type of message as defined in <code>Message</code>
	 * @param property
	 *           Name of the property the message should be associated with
	 * @return <code>MessageList</code> object for the given search criteria or <code>null</code> if no object was found.
	 */
	public MessageList subList(final int type, final String property)
	{
		final MessageList returnVal = new MessageList();

		final Iterator<Message> i = messageList.iterator();

		while (i.hasNext())
		{
			final Message message = i.next();
			if ((message.getType() == type) && property.equalsIgnoreCase(message.getProperty()))
			{
				returnVal.add(message);
			}
		}

		return (returnVal.size() == 0) ? null : returnVal;
	}

	/**
	 * <p>
	 * Returns a sub list of the elements of this list, for the given selection criteria. All <code>Message</code>
	 * elemenets of the list for the given type are extracted and returned in a new <codeMessageList</code> object. If
	 * there are no elements matching the criteria <code>null</code> is returned.
	 * </p>
	 * <p>
	 * <b>Note</b> Only a <em>shallow</em> copy is performed. Both lists are containig references to the same objects in
	 * memory.
	 * </p>
	 * 
	 * @param type
	 *           Type of message as defined in <code>Message</code>
	 * @return <code>MessageList</code> object for the given search criteria or <code>null</code> if no object was found.
	 */
	public MessageList subList(final int type)
	{
		final MessageList returnVal = new MessageList();

		final Iterator<Message> i = messageList.iterator();

		while (i.hasNext())
		{
			final Message message = i.next();
			if (message.getType() == type)
			{
				returnVal.add(message);
			}
		}

		return (returnVal.size() == 0) ? null : returnVal;
	}

	/**
	 * <p>
	 * Compares the specified object with this list for equality. Returns <code>true</code> if and only if the specified
	 * object is also a list, both lists have the same size, and all corresponding pairs of elements in the two lists are
	 * <em>equal</em>. (Two elements <code>e1</code> and <code>e2</code> are equal if
	 * <code>(e1==null ? e2==null : e1.equals(e2))</code>.) In other words, two lists are defined to be equal if they
	 * contain the same elements in the same order.
	 * </p>
	 * <p>
	 * This implementation first checks if the specified object is this list. If so, it returns <code>true</code>; if
	 * not, it checks if the specified object is a list. If not, it returns <code>false</code>; if so, it iterates over
	 * both lists, comparing corresponding pairs of elements. If any comparison returns <code>false</code>, this method
	 * returns <code>false</code>. If either iterator runs out of elements before the other it returns <code>false</code>
	 * (as the lists are of unequal length); otherwise it returns true when the iterations complete.
	 * </p>
	 * 
	 * @param o
	 *           the object to be compared for equality with this list.
	 * @return <code>true</code> if the specified object is equal to this list.
	 */
	@Override
	public boolean equals(final Object o)
	{
		if (o == null)
		{
			return false;
		}
		else if (o == this)
		{
			return true;
		}
		else if (!(o instanceof MessageList))
		{
			return false;
		}
		else
		{
			return messageList.equals(((MessageList) o).messageList);
		}
	}

	/**
	 * Returns the hash code value for this list. The hash code of a list is defined to be the result of the following
	 * calculation:
	 * 
	 * <pre>
	 * hashCode = 1;
	 * Iterator i = list.iterator();
	 * while (i.hasNext())
	 * {
	 * 	Object obj = i.next();
	 * 	hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
	 * }
	 * </pre>
	 * 
	 * This ensures that <code>list1.equals(list2)</code> implies that <code>list1.hashCode()==list2.hashCode()</code>
	 * for any two lists, <code>list1</code> and <code>list2</code>, as required by the general contract of
	 * <code>Object.hashCode</code>.
	 * 
	 * @return the hash code value for this list.
	 */
	@Override
	public int hashCode()
	{
		return messageList.hashCode();
	}

	/**
	 * <p>
	 * Returns a string representation of this collection. The string representation consists of a list of the
	 * collection's elements in the order they are returned by its iterator, enclosed in square brackets ("[]"). Adjacent
	 * elements are separated by the characters ", " (comma and space). Elements are converted to strings as by
	 * <code>String.valueOf(Object)</code>.
	 * </p>
	 * <p>
	 * This implementation creates an empty string buffer, appends a left square bracket, and iterates over the
	 * collection appending the string representation of each element in turn. After appending each element except the
	 * last, the string ", " is appended. Finally a right bracket is appended. A string is obtained from the string
	 * buffer, and returned.
	 * </p>
	 * 
	 * @return a string representation of this collection
	 */
	@Override
	public String toString()
	{
		return messageList.toString();
	}
}
