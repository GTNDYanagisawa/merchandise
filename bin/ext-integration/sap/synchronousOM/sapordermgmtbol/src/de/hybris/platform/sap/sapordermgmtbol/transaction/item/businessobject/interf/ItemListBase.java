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
package de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf;

import java.util.List;

import de.hybris.platform.sap.core.common.TechKey;



/**
 * This Interface derives from the standard List interface. It Represents a List of anything extending
 * <code>ItemBase</code> Interface. The concrete type can be specified via generics. In addition to the standard List
 * interface it provides additional get,remove and contains methods using the techKey or the item handle as identifier.
 * 
 * @stereotype collection
 * @param <T>
 *           Actual Item Type
 */
public interface ItemListBase<T extends SimpleItem> extends List<T>
{

	/**
	 * Returns <tt>true</tt> if this list contains an element with the given handle<br>
	 * <br>
	 * <b>Note -</b> This implementation assumes that the list is small and performs a simple search running in the list
	 * from the first element to the last.
	 * 
	 * @param handle
	 *           handle to search for
	 * @return <tt>true</tt> if this list contains an element with the given handle
	 */
	boolean contains(String handle);

	/**
	 * Returns <tt>true</tt> if this list contains an element with the given technical key<br>
	 * <br>
	 * <b>Note -</b> This implementation assumes that the list is small and performs a simple search running in the list
	 * from the first element to the last.
	 * 
	 * @param techKey
	 *           techKey to search for
	 * @return <tt>true</tt> if this list contains an element with the given technical key
	 */
	boolean contains(TechKey techKey);

	/**
	 * Returns the item specified by the given handle.<br>
	 * <br>
	 * <b>Note -</b> This implementation assumes that the list is small and performs a simple search running in the list
	 * from the first element to the last.
	 * 
	 * @param handle
	 *           the handle of the item that should be retrieved
	 * @return the item with the given handle or <code>null</code> if no item for that key was found.
	 */
	public T get(String handle);

	/**
	 * Returns the item specified by the given technical key.<br>
	 * <br>
	 * <b>Note -</b> This implementation assumes that the list is small and performs a simple search running in the list
	 * from the first element to the last.
	 * 
	 * @param techKey
	 *           the technical key of the item that should be retrieved
	 * @return the item with the given techical key or <code>null</code> if no item for that key was found.
	 */
	public T get(TechKey techKey);

	/**
	 * Returns the index for the item with the given handle.<br>
	 * <bt> <b>Note -</b> This implementation assumes that the list is small and performs a simple search running in the
	 * list from the first element to the last.
	 * 
	 * @param handle
	 *           item handle
	 * @return index in the item list, or -1 if such an element does not exists.
	 */
	public int indexOf(String handle);

	/**
	 * Returns the index for the item with the given key.<br>
	 * <br>
	 * <b>Note -</b> This implementation assumes that the list is small and performs a simple search running in the list
	 * from the first element to the last.
	 * 
	 * @param techKey
	 *           item key
	 * @return index in the item list, or -1 if such an element does not exists.
	 */
	public int indexOf(TechKey techKey);

	/**
	 * Remove the given element from this list, if it exists.<br>
	 * <br>
	 * <b>Note -</b> This implementation assumes that the list is small and performs a simple search running in the list
	 * from the first element to the last.
	 * 
	 * @param handle
	 *           of element to remove from the list
	 * @return <tt>true</tt> if this list contained the specified element
	 */
	public boolean remove(String handle);

	/**
	 * Remove the given element from this list, if it exists.<br>
	 * <br>
	 * <b>Note -</b> This implementation assumes that the list is small and performs a simple search running in the list
	 * from the first element to the last.
	 * 
	 * @param techKey
	 *           index of element to remove from the list
	 * @return <tt>true</tt> if this list contained the specified element
	 */
	public boolean remove(TechKey techKey);

}