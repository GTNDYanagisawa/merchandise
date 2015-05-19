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
package de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl;

import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.bol.logging.LogSeverity;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;


/**
 * Representation of user configurable texts. A text consists of an id identifying the type of the text and the textual
 * information itself.
 * 
 */
public class TextImpl implements Text
{

	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(TextImpl.class.getName());

	private static final int MAX_TEXT_LENGTH = 10 * 1024;
	private String id;
	private String text;
	private String handle;

	private boolean textChanged;

	/**
	 * Creates a new object without initialization of the id and text information. After calling this constructor both
	 * fields are initialized with empty Strings
	 */
	public TextImpl()
	{
		this.id = "";
		this.text = "";
		this.handle = "";
		this.textChanged = false;
	}

	/**
	 * Creates a new object and sets the id and text information.
	 * 
	 * @param id
	 *           Type of the text
	 * @param text
	 *           The text itself
	 */
	public TextImpl(final String id, final String text)
	{
		this.id = id;
		this.text = text;
		this.handle = "";
		this.textChanged = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf .Text#getId()
	 */
	@Override
	public String getId()
	{
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf .Text#setId(java.lang.String)
	 */
	@Override
	public void setId(String id)
	{
		if (id == null)
		{
			id = "";
		}
		if (!id.equals(this.id))
		{
			textChanged = true;
		}
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf .Text#getText()
	 */
	@Override
	public String getText()
	{
		return text;
	}

	@Override
	public void setText(String text)
	{
		if (text == null)
		{
			text = "";
		}

		text = assureMaxLength(text);

		if (!text.equals(this.text))
		{
			textChanged = true;
		}
		this.text = text;
	}

	protected String assureMaxLength(String text2verify)
	{
		final long size = text2verify.length();

		if (size > MAX_TEXT_LENGTH)
		{
			writeLogMessageTooLong(size);
			text2verify = text2verify.substring(0, MAX_TEXT_LENGTH);
		}

		return text2verify;
	}

	private void writeLogMessageTooLong(final long actualSize)
	{
		final String[] args = new String[2];
		args[0] = String.valueOf(actualSize);
		args[1] = String.valueOf(MAX_TEXT_LENGTH);
		sapLogger.log(LogSeverity.INFO, LogCategories.APPLICATIONS,
				"Text is too long ({0} characters) and will be truncated ({1} characters)", args);
	}

	/**
	 * Returns a string Representation
	 * 
	 * @return String giving information about the object
	 */
	@Override
	public String toString()
	{
		return "Text [id=\"" + id + "\", text=\"" + text + "\"]";
	}

	/**
	 * Determines whether the given object is equal to this object
	 * 
	 * @param o
	 *           Object to compare with
	 * @return <code>true</code> if the object is equal, <code>false</code> if not
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
		else if (o instanceof TextImpl)
		{
			return text.equals(((TextImpl) o).text) && id.equals(((TextImpl) o).id);
		}
		else
		{
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return text.hashCode() ^ id.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf .Text#getHandle()
	 */
	@Override
	public String getHandle()
	{
		return handle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf .Text#setHandle(java.lang.String)
	 */
	@Override
	public void setHandle(final String handle)
	{
		this.handle = handle;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf .Text#clear()
	 */
	@Override
	public void clear()
	{
		handle = "";
		id = "";
		text = "";
		textChanged = true;
	}

	@Override
	public TextImpl clone()
	{
		try
		{
			// we only contain immutable fields so super clone is fine.
			final TextImpl clone = (TextImpl) super.clone();
			final boolean textExists = clone.getText() != null && !clone.getText().isEmpty();
			clone.setTextChanged(textExists);
			return clone;

		}
		catch (final CloneNotSupportedException ex)
		{
			// should not happen, because we are clone able
			throw new ApplicationBaseRuntimeException(
					"Failed to clone Object, check whether Cloneable Interface is still implemented", ex);
		}
	}

	@Override
	public boolean hasTextChanged()
	{
		return textChanged;
	}

	@Override
	public void resetTextChanged()
	{
		textChanged = false;
	}

	private void setTextChanged(final boolean textChanged)
	{
		this.textChanged = textChanged;
	}
}
