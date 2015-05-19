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
package de.hybris.platform.sap.core.common;

import java.io.Serializable;
import java.util.Locale;
import java.util.UUID;


/**
 * Class representing the concept of an unique identifier for an object. The key may be a database primary key, a GUID
 * or some other identifier.
 */
public final class TechKey implements Serializable
{

	private static final long serialVersionUID = 7548386685047439842L;

	/**
	 * The value used for initial technical keys, i.e. keys that are not defined and created with a constructor call
	 * giving <code>null</code> as a parameter.
	 */
	private static final String initialValue = "";

	/**
	 * An empty TechKey. Use this if you need an empty technical key for your purposes. Do not create them with new
	 * TechKey(""). If you ask this key if its initial it says "yes I am".
	 */
	public static final TechKey EMPTY_KEY = new TechKey(null);

	private String id;
	private final int hashCode; // Store the hash code for performance's sake
	private boolean isInitial;

	/**
	 * Creates a new instance by providing a string representation of the key. Do not create new technical keys using new
	 * <code>TechKey(null)</code> but use the defined static final <code>EMPTY_KEY</code>.
	 * 
	 * @param id
	 *           key to be used for the construction
	 */
	public TechKey(final String id)
	{
		if (id != null)
		{
			this.id = id;
			isInitial = false;
		}
		else
		{
			this.id = initialValue;
			isInitial = true;
		}
		// Cache the hash code because of the heavy use that is made of this
		// method in the hashMap
		hashCode = this.id.hashCode();
	}

	/**
	 * Tells you, whether the actual key is initial or not.
	 * 
	 * @return <code>true</code> if the key is initial or <code>false</code> if its not.
	 */
	public boolean isInitial()
	{
		return isInitial;
	}

	/**
	 * Retrieves the id as a <code>String</code>.
	 * 
	 * @return String representation of the key
	 */
	public String getIdAsString()
	{
		return id;
	}

	/**
	 * Checks if the passed TechKey instance is empty.
	 * <p>
	 * It is empty, if the id is <code>null</code> or if the content as <code>String</code> contains whitespaces only.
	 * </p>
	 * 
	 * @param techKey
	 *           TechKey instance
	 * @return <code>true</code> if the provided <code>techKey</code> is empty
	 */
	static public boolean isEmpty(final TechKey techKey)
	{

		if (techKey == null)
		{
			return true;
		}

		if (techKey.getIdAsString().trim().length() == 0)
		{
			return true;
		}

		return false;
	}

	/**
	 * Returns the string representation of the TechKey.
	 * 
	 * @return String string representation
	 */
	@Override
	public String toString()
	{
		return id.toString();
	}

	/**
	 * Returns the UUID string representation of the given object.
	 * <p>
	 * Example: <code>12345678-9012-3456-7890-1234567890AB</code>
	 * </p>
	 * 
	 * @return UUID String representation
	 */
	public String toUUIDString()
	{
		final StringBuilder stringBuilder = new StringBuilder(36);
		stringBuilder.append(id, 0, 8).append("-").append(id, 8, 12).append("-").append(id, 12, 16).append("-").append(id, 16, 20)
				.append("-").append(id, 20, 32);
		return stringBuilder.toString();
	}

	/**
	 * Returns the hash code of the key.
	 * 
	 * @return hash code
	 */
	@Override
	public int hashCode()
	{
		return hashCode;
	}

	/**
	 * Compares this TechKey to the specified object. The result is <code>true</code> if and only if the argument is not
	 * <code>null</code> and is a <code>TechKey</code> object that represents the same key value as this object.
	 * 
	 * @param o
	 *           object to compare with
	 * @return <code>true</code> if the keys are identical; otherwise <code>false</code>.
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
		else if (!(o instanceof TechKey))
		{
			return false;
		}
		else if (((TechKey) o).hashCode != hashCode)
		{
			return false;
		}
		else
		{
			return id.equals(((TechKey) o).id);
		}
	}

	/**
	 * Creates a new unique technical key using as GUID with length 32 using the class UUID.
	 * 
	 * @return new unique technical key
	 */
	public static TechKey generateKey()
	{
		// Generate random TechKey (UUID class is synchronized)
		return fromUUIDString(UUID.randomUUID().toString());
	}

	/**
	 * Creates a new unique technical key using as GUID with length 32 using the UUID string format.
	 * 
	 * @param uuidString
	 *           UUID formatted String
	 * 
	 * @return new unique technical key
	 */
	public static TechKey fromUUIDString(final String uuidString)
	{
		// Conversion of UUID string: removing of the '-' characters
		final StringBuilder buf = new StringBuilder(32);
		buf.append(uuidString, 0, 8);
		buf.append(uuidString, 9, 13);
		buf.append(uuidString, 14, 18);
		buf.append(uuidString, 19, 23);
		buf.append(uuidString, 24, 36);
		return new TechKey(buf.toString().toUpperCase(Locale.ENGLISH));
	}

}
