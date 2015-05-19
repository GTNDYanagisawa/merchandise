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
package de.hybris.platform.sap.core.bol.backend.jco;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoRecordFieldIterator;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.util.conv.GuidConversionUtil;


/**
 * Convenience class for the management of JCo. The implementation of JCo is sometimes a little bit low level for the
 * purposes of the internet sales application. Because of this the <code>JCoHelper</code> class provides some helper
 * classes and convenience methods to simplify the handling of JCo and especially its data structures.
 * 
 */
public class JCoHelper
{

	static final Logger log = Logger.getLogger(JCoHelper.class.getName());

	private final static String EMPTY_STRING = "";

	/**
	 * Represents the data returned by a call to the function module.
	 * 
	 * @version 1.0
	 */
	public static class ReturnValue
	{
		private final JCoStructure message;
		private final JCoTable messages;
		private final String returnCode;

		/**
		 * Creates a new instance.
		 * 
		 * @param messages
		 *           Table containing the messages returned from the backend
		 * @param returnCode
		 *           The return code returned from the backend
		 */
		public ReturnValue(final JCoTable messages, final String returnCode)
		{
			this.message = null;
			this.returnCode = returnCode;
			this.messages = messages;
		}

		/**
		 * Creates a new instance.
		 * 
		 * @param message
		 *           Structure containing only one message (Structure can be different as for the messages table)
		 * @param messages
		 *           Table containing the messages returned from the backend
		 * @param returnCode
		 *           The return code returned from the backend
		 */
		public ReturnValue(final JCoTable messages, final JCoStructure message, final String returnCode)
		{
			this.message = message;
			this.returnCode = returnCode;
			this.messages = messages;
		}

		/**
		 * Creates a new instance.
		 * 
		 * @param returnValue
		 *           the return value
		 */
		public ReturnValue(final JCoHelper.ReturnValue returnValue)
		{
			this.returnCode = returnValue.getReturnCode();
			this.messages = returnValue.getMessages();
			this.message = returnValue.getMessage();
		}

		/**
		 * Get the code the CRM call has returned.
		 * 
		 * @return The backend return code
		 */
		public String getReturnCode()
		{
			return returnCode;
		}

		/**
		 * Get the table containing the messages returned by the call to the backend (e.g. ERP, CRM, ...)
		 * 
		 * @return table containing the messages
		 */
		public JCoTable getMessages()
		{
			return messages;
		}

		/**
		 * Get the Structure containing the messages returned by the call to the backend (e.g. ERP, CRM, ...)
		 * 
		 * @return table containing the messages
		 */
		public JCoStructure getMessage()
		{
			return message;
		}
	}

	/**
	 * Helper for String-formatting Returns a String filled up to the left with filler up to the length len.
	 * 
	 * @param src
	 *           the source
	 * @param filler
	 *           the filler
	 * @param len
	 *           the length
	 * @return newStr
	 */
	public static String lFillStr(final String src, final String filler, final int len)
	{
		final StringBuffer buf = new StringBuffer(len);

		for (int i = 0; i < len - src.length(); i++)
		{
			buf.append(filler);
		}
		buf.append(src);

		return buf.toString();
	}

	/**
	 * Wraps around a JCo record and decorates the record with convenient methods to set the record's data. <br>
	 * An important feature is that changelists (the X-structures) are maintained transparently and that you are able to
	 * directly pass the higher level internet sales data structures to the JCo struct. <br>
	 * Secondly the handling of boolean values is improved (no more "X").
	 * 
	 * @version 1.0
	 */
	public static final class RecordWrapper
	{

		private final JCoRecord record;
		private final JCoRecord changeList;

		/**
		 * Creates a new wrapper around the JCo record.
		 * 
		 * @param record
		 *           The record to store the data in
		 * @param changeList
		 *           Changelist used by the X-structures
		 */
		public RecordWrapper(final JCoRecord record, final JCoRecord changeList)
		{
			this.record = record;
			this.changeList = changeList;
		}

		/**
		 * Creates a new wrapper around the JCo structure.
		 * 
		 * @param record
		 *           The record to store the data in
		 */
		public RecordWrapper(final JCoRecord record)
		{
			this(record, null);
		}

		/* internal method to set the changed field */
		/**
		 * Set the changed field.
		 * 
		 * @param field
		 *           changed field to be set
		 */
		private void setChangeFlag(final String field)
		{
			if (changeList != null)
			{
				changeList.setValue(field, "X");
			}
		}

		/**
		 * Returns the JCO.Record that is wrapped by this class.
		 * 
		 * @return the wrapped <code>JCoRecord</code>
		 */
		public JCoRecord getRecord()
		{
			return record;
		}

		/**
		 * Returns the JCO.Record that is wrapped by this class and is used as a X-Structure.
		 * 
		 * @return the wrapped <code>JCoRecord</code>
		 */
		public JCoRecord getChangeList()
		{
			return changeList;
		}

		/**
		 * Sets the given field of the JCo structure to the provided value. The changelist, if present, is automatically
		 * maintained.
		 * 
		 * @param value
		 *           Value to be set
		 * @param field
		 *           JCo field to be filled with the value
		 */
		public void setValue(final String value, final String field)
		{
			setValue(value, field, true);
		}

		/**
		 * Sets the given field of the JCo structure to the provided value. The value is filled up with filler to the left
		 * The changelist, if present, is automatically maintained.
		 * 
		 * @param value
		 *           Value to be set
		 * @param field
		 *           JCo field to be filled with the value
		 * @param filler
		 *           The single-char-string to fill up value to the left
		 */
		public void setValueFilledLeft(final String value, final String field, final String filler)
		{

			final int len = record.getMetaData().getLength(field);
			final String fatNewValueBecauseItsFilledUpWithLotsOfZeros = lFillStr(value, filler, len);

			setValue(fatNewValueBecauseItsFilledUpWithLotsOfZeros, field, true);
		}

		/**
		 * Sets the given field of the JCo structure to the provided value. The changelist, if present, is only maintained
		 * if the <code>maintainChange</code> flag is set to <code>true</code>.
		 * 
		 * @param value
		 *           Value to be set
		 * @param field
		 *           JCo field to be filled with the value
		 * @param maintainChange
		 *           Indicates whether the changelist should be maintained or not
		 */
		public void setValue(final String value, final String field, final boolean maintainChange)
		{
			if (value == null)
			{
				return;
			}

			record.setValue(field, value);
			if (maintainChange)
			{
				setChangeFlag(field);
			}
		}

		/**
		 * Sets the given field of the JCo structure to the provided value. The changelist, if present, is automatically
		 * maintained.
		 * 
		 * @param value
		 *           Value to be set
		 * @param field
		 *           JCo field to be filled with the value
		 */
		public void setValue(final int value, final String field)
		{
			setValue(value, field, true);
		}

		/**
		 * Sets the given field of the JCo structure to the provided value. The changelist, if present, is automatically
		 * maintained.
		 * 
		 * @param value
		 *           Value to be set
		 * @param field
		 *           JCo field to be filled with the value
		 */
		public void setValue(final double value, final String field)
		{
			setValue(value, field, true);
		}

		/**
		 * Sets the given field of the JCo structure to the provided value. The changelist, if present, is only maintained
		 * if the <code>maintainChange</code> flag is set to <code>true</code>.
		 * 
		 * @param value
		 *           Value to be set
		 * @param field
		 *           JCo field to be filled with the value
		 * @param maintainChange
		 *           Indicates whether the changelist should be maintained or not
		 */
		public void setValue(final int value, final String field, final boolean maintainChange)
		{
			record.setValue(value, field);
			if (maintainChange)
			{
				setChangeFlag(field);
			}
		}

		/**
		 * Sets the given field of the JCo structure to the provided value. The changelist, if present, is only maintained
		 * if the <code>maintainChange</code> flag is set to <code>true</code>.
		 * 
		 * @param value
		 *           Value to be set
		 * @param field
		 *           JCo field to be filled with the value
		 * @param maintainChange
		 *           Indicates whether the changelist should be maintained or not
		 */
		public void setValue(final double value, final String field, final boolean maintainChange)
		{
			record.setValue(field, value);
			if (maintainChange)
			{
				setChangeFlag(field);
			}
		}

		/**
		 * Sets the given field of the JCo structure to the provided value. The changelist, if present, is automatically
		 * maintained.
		 * 
		 * @param value
		 *           Value to be set
		 * @param field
		 *           JCo field to be filled with the value
		 */
		public void setValue(final boolean value, final String field)
		{
			setValue(value, field, true);
		}

		/**
		 * Sets the given field of the JCo structure to the provided value. The changelist, if present, is only maintained
		 * if the <code>maintainChange</code> flag is set to <code>true</code>.
		 * 
		 * @param value
		 *           Value to be set
		 * @param field
		 *           JCo field to be filled with the value
		 * @param maintainChange
		 *           Indicates whether the changelist should be maintained or not
		 */
		public void setValue(final boolean value, final String field, final boolean maintainChange)
		{
			if (value)
			{
				record.setValue(field, "X");
			}
			else
			{
				record.setValue(field, "");
			}

			if (maintainChange)
			{
				setChangeFlag(field);
			}
		}


		/**
		 * Sets the given field of the JCo structure to the value of the provided technical key (the TechKey is an
		 * abstraction of the SAP GUI). <br>
		 * The changelist, if present, is automatically maintained.<br>
		 * If the provided key is <code>null</code> <b>nothing</b> is done and the method returns immediately.
		 * 
		 * @param techkey
		 *           Technical key to be set
		 * @param field
		 *           JCo field to be filled with the value
		 */
		public void setValue(final TechKey techkey, final String field)
		{
			if (techkey == null)
			{
				return;
			}

			setValue(techkey.getIdAsString(), field, true);
		}

		/**
		 * Sets the given field of the JCo record to the value of the provided technical key (the TechKey is an
		 * abstraction of the SAP GUI). <br>
		 * The changelist, if present, is only maintained if the <code>maintainChange</code> flag is set to
		 * <code>true</code>.<br>
		 * If the provided tech key is <code>null</code> <b>nothing</b> is done and the method returns immediately.
		 * 
		 * @param techkey
		 *           Technical key to be set
		 * @param field
		 *           JCo field to be filled with the value
		 * @param maintainChange
		 *           Indicates whether the changelist should be maintained or not
		 */
		public void setValue(final TechKey techkey, final String field, final boolean maintainChange)
		{
			if (techkey == null)
			{
				return;
			}

			record.setValue(field, techkey.getIdAsString());

			if (maintainChange)
			{
				setChangeFlag(field);
			}
		}

		/**
		 * Returns the value of the specified field as a <code>TechKey</code> object.
		 * 
		 * @param field
		 *           JCo field to be retrieved
		 * @return TechKey constructed from the value of the given field
		 */
		public TechKey getTechKey(final String field)
		{
			return new TechKey(getStringPerfOpt(record, field));
		}

		/**
		 * Returns the value of the specified field as a Java language <code>String</code> object.
		 * 
		 * @param field
		 *           JCo field to be retrieved
		 * @return String constructed from the value of the given field
		 */
		public String getString(final String field)
		{
			return getStringPerfOpt(record, field);
		}

		/**
		 * Returns the value of the specified field as a Java language <code>boolean</code> primitive type.
		 * 
		 * @param field
		 *           JCo field to be retrieved
		 * @return Boolean constructed from the value of the given field
		 */
		public boolean getBoolean(final String field)
		{
			final String val = getStringPerfOpt(record, field);

			if (val.length() == 0)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
	}

	/**
	 * Returns the group of the JCOException as a String.
	 * 
	 * @param jcoEx
	 *           the JCO exception
	 * @return string describing the group of the exception
	 */
	public static String getExceptionGroupAsString(final JCoException jcoEx)
	{

		String groupString = null;

		switch (jcoEx.getGroup())
		{

			case JCoException.JCO_ERROR_COMMUNICATION:
				groupString = "JCO_ERROR_COMMUNICATION";
				break;

			case JCoException.JCO_ERROR_LOGON_FAILURE:
				groupString = "JCO_ERROR_LOGON_FAILURE";
				break;

			case JCoException.JCO_ERROR_SYSTEM_FAILURE:
				groupString = "JCO_ERROR_SYSTEM_FAILURE";
				break;

			case JCoException.JCO_ERROR_SERVER_STARTUP:
				groupString = "JCO_ERROR_SERVER_STARTUP";
				break;

			case JCoException.JCO_ERROR_ABAP_EXCEPTION:
				groupString = "JCO_ERROR_ABAP_EXCEPTION";
				break;

			case JCoException.JCO_ERROR_APPLICATION_EXCEPTION:
				groupString = "JCO_ERROR_APPLICATION_EXCEPTION";
				break;

			case JCoException.JCO_ERROR_CANCELLED:
				groupString = "JCO_ERROR_CANCELLED";
				break;

			case JCoException.JCO_ERROR_ILLEGAL_TID:
				groupString = "JCO_ERROR_ILLEGAL_TID";
				break;

			case JCoException.JCO_ERROR_INTERNAL:
				groupString = "JCO_ERROR_INTERNAL";
				break;

			case JCoException.JCO_ERROR_NOT_SUPPORTED:
				groupString = "JCO_ERROR_NOT_SUPPORTED";
				break;

			case JCoException.JCO_ERROR_PROGRAM:
				groupString = "JCO_ERROR_PROGRAM";
				break;

			case JCoException.JCO_ERROR_PROTOCOL:
				groupString = "JCO_ERROR_PROTOCOL";
				break;

			case JCoException.JCO_ERROR_RESOURCE:
				groupString = "JCO_ERROR_RESOURCE";
				break;

			case JCoException.JCO_ERROR_STATE_BUSY:
				groupString = "JCO_ERROR_STATE_BUSY";
				break;

			case JCoException.JCO_ERROR_CONVERSION:
				groupString = "JCO_ERROR_CONVERSION";
				break;

			case JCoException.JCO_ERROR_FIELD_NOT_FOUND:
				groupString = "JCO_ERROR_FIELD_NOT_FOUND";
				break;

			case JCoException.JCO_ERROR_FUNCTION_NOT_FOUND:
				groupString = "JCO_ERROR_FUNCTION_NOT_FOUND";
				break;

			case JCoException.JCO_ERROR_NULL_HANDLE:
				groupString = "JCO_ERROR_NULL_HANDLE";
				break;

			case JCoException.JCO_ERROR_UNSUPPORTED_CODEPAGE:
				groupString = "JCO_ERROR_UNSUPPORTED_CODEPAGE";
				break;

			case JCoException.JCO_ERROR_XML_PARSER:
				groupString = "JCO_ERROR_XML_PARSER";
				break;

			default:
				groupString = "undefined (" + jcoEx.getGroup() + ")";
				break;
		}

		return groupString;
	}





	/**
	 * Converts the ABAP logic for booleans, <code>"" = false</code>, <code>"X" = true</code> into a Java boolean.
	 * Instead of using this method, you may want to use the <code>Wrapper</code> class. The use of this method is only
	 * helpful, if you want to convert a limited amount of fields, mostly booleans.
	 * 
	 * @param string
	 *           String to be converted into a boolean
	 * @return <code>false</code> if the string is empty, otherwise <code>true</code>.
	 */
	public static boolean getBoolean(final String string)
	{
		if (string == null)
		{
			return false;
		}
		else
		{
			return (string.length() != 0);
		}
	}

	/**
	 * Converts the ABAP logic for booleans, <code>"" = false</code>, <code>"X" = true</code> into a Java boolean.
	 * Instead of using this method, you may want to use the <code>Wrapper</code> class. The use of this method is only
	 * helpful, if you want to convert a limited amount of fields, mostly booleans.
	 * 
	 * @param field
	 *           JCO.Field to be converted into a boolean
	 * @return <code>false</code> if the string is empty, otherwise <code>true</code>.
	 */
	public static boolean getBoolean(final JCoField field)
	{
		if (field == null)
		{
			return false;
		}
		else
		{
			return (field.getString().length() != 0);
		}
	}

	/**
	 * Converts the ABAP logic for booleans, <code>"" = false</code>, <code>"X" = true</code> into a Java boolean.
	 * Instead of using this method, you may want to use the <code>Wrapper</code> class. The use of this method is only
	 * helpful, if you want to convert a limited amount of fields, mostly booleans.
	 * 
	 * @param record
	 *           JCO.Record containing the column to be converted into a boolean
	 * @param field
	 *           Name of the column to be converted
	 * @return <code>false</code> if the string is empty, otherwise <code>true</code>.
	 */
	public static boolean getBoolean(final JCoRecord record, final String field)
	{
		if (record == null)
		{
			return false;
		}
		else
		{
			return (getStringPerfOpt(record, field).length() != 0);
		}
	}

	/**
	 * Returns the specified field of a JCo record converted to a string.
	 * 
	 * @param record
	 *           JCO.Record containing the column to be converted into a String
	 * @param field
	 *           Name of the column to be converted
	 * @return The value as String
	 */
	public static String getString(final JCoRecord record, final String field)
	{
		if (record == null)
		{
			return null;
		}
		else
		{
			return getStringPerfOpt(record, field);
		}
	}

	/**
	 * Returns the specified raw field of a JCo record converted to a string.
	 * 
	 * @param record
	 *           JCO.Record containing the column to be converted into a String
	 * @param field
	 *           Name of the column to be converted
	 * @return The value as String
	 */
	public static String getStringFromRaw(final JCoRecord record, final String field)
	{
		if (record == null)
		{
			return null;
		}
		else
		{
			return GuidConversionUtil.convertToString(record.getByteArray(field));
		}
	}

	/**
	 * Returns the specified field of a JCo record converted to a date.
	 * 
	 * @param record
	 *           JCO.Record containing the column to be converted into a Date
	 * @param field
	 *           Name of the column to be converted
	 * @return The value as Date
	 */
	public static Date getDate(final JCoRecord record, final String field)
	{
		if (record == null)
		{
			return null;
		}
		else
		{
			return record.getDate(field);
		}
	}

	/**
	 * Converts a technical key stored in a JCo record into an object of type <code>TechKey</code>. The use of this
	 * method is only helpful, if you want to convert a limited amount of fields, mostly booleans.
	 * 
	 * @param record
	 *           JCO.Record containing the column to be converted into a technical key. If this parameter is
	 *           <code>null</code> the method returns <code>null</code>
	 * @param field
	 *           Name of the column to be converted
	 * @return a technical key for the specified column.
	 */
	public static TechKey getTechKey(final JCoRecord record, final String field)
	{
		if (record == null)
		{
			return null;
		}
		else
		{
			return new TechKey(getStringPerfOpt(record, field));
		}
	}

	/**
	 * Converts a technical key stored in a JCo record as raw value into an object of type <code>TechKey</code>. T
	 * 
	 * @param record
	 *           JCO.Record containing the column to be converted into a technical key. If this parameter is
	 *           <code>null</code> the method returns <code>null</code>
	 * @param field
	 *           Name of the column to be converted
	 * @return a technical key for the specified column.
	 */
	public static TechKey getTechKeyFromRaw(final JCoRecord record, final String field)
	{
		if (record == null)
		{
			return null;
		}
		else
		{
			return GuidConversionUtil.convertToTechKey(record.getByteArray(field));
		}
	}

	/**
	 * Sets the given field of the JCo structure to the provided value.
	 * 
	 * @param record
	 *           The JCo record to work with
	 * @param value
	 *           Value to be set
	 * @param field
	 *           JCo field to be filled with the value
	 */
	public static void setValue(final JCoRecord record, final int value, final String field)
	{
		record.setValue(field, value);
	}

	/**
	 * Sets the given field of the JCo structure to the provided value.
	 * 
	 * @param record
	 *           The JCo record to work with
	 * @param value
	 *           Value to be set
	 * @param field
	 *           JCo field to be filled with the value
	 */
	public static void setValue(final JCoRecord record, final String value, final String field)
	{
		if (value == null)
		{
			return;
		}

		record.setValue(field, value);
	}

	/**
	 * Sets the given string field of the JCo structure to the provided value as Raw, that means the value is converted
	 * into a byte Array.
	 * 
	 * @param record
	 *           The JCo record to work with
	 * @param value
	 *           Value to be set
	 * @param field
	 *           JCo field to be filled with the value
	 */
	public static void setValueAsRaw(final JCoRecord record, final String value, final String field)
	{
		if (value == null)
		{
			return;
		}

		record.setValue(field, GuidConversionUtil.convertToByteArray(value));
	}

	/**
	 * Sets the given field of the JCo structure to the provided value.
	 * 
	 * @param record
	 *           The JCo record to work with
	 * @param value
	 *           Value to be set
	 * @param field
	 *           JCo field to be filled with the value
	 */
	public static void setValue(final JCoRecord record, final double value, final String field)
	{
		record.setValue(field, value);
	}

	/**
	 * Sets the given field of the JCo structure to the provided value.
	 * 
	 * @param record
	 *           The JCo record to work with
	 * @param value
	 *           Value to be set
	 * @param field
	 *           JCo field to be filled with the value
	 */
	public static void setValue(final JCoRecord record, final boolean value, final String field)
	{
		if (value)
		{
			record.setValue(field, "X");
		}
		else
		{
			record.setValue(field, "");
		}
	}

	/**
	 * Sets the given field of the JCo structure to the provided value. If the provided tech key is <code>null</code>
	 * <b>nothing</b> is done and the method returns immediately.
	 * 
	 * @param record
	 *           The JCo record to work with
	 * @param value
	 *           Value to be set
	 * @param field
	 *           JCo field to be filled with the value
	 */
	public static void setValue(final JCoRecord record, final TechKey value, final String field)
	{
		if (value == null)
		{
			return;
		}

		record.setValue(field, value.getIdAsString());
	}

	/**
	 * Sets the given string field of the JCo structure to the provided value as Raw, that means the value is converted
	 * into a byte Array. If the provided tech key is <code>null</code> <b>nothing</b> is done and the method returns
	 * immediately.
	 * 
	 * @param record
	 *           The JCo record to work with
	 * @param value
	 *           Value to be set
	 * @param field
	 *           JCo field to be filled with the value
	 */
	public static void setValueAsRaw(final JCoRecord record, final TechKey value, final String field)
	{
		if (value == null)
		{
			return;
		}

		record.setValue(field, GuidConversionUtil.convertToByteArray(value));
	}

	/**
	 * Converts an ABAP NUMC data type to an String which means it's trims all leading zeros.
	 * 
	 * @param record
	 *           the JCo record to extract field from
	 * @param field
	 *           the name of the field that should be extracted from the record
	 * @return the trimed string
	 */
	public static String getStringFromNUMC(final JCoRecord record, final String field)
	{

		final String numc = getStringPerfOpt(record, field);

		final int length = numc.length();

		if (length > 0)
		{
			int start = length;

			for (int i = 0; i < length; i++)
			{
				if (numc.charAt(i) != '0')
				{
					start = i;
					i = length;
				}
			}
			if (start == length)
			{
				return "";
			}
			else
			{
				return numc.substring(start);
			}
		}
		return "";

	}

	/**
	 * Logs a RFC-call into the log file of the application.
	 * 
	 * @param functionName
	 *           the name of the JCo function that was executed
	 * @param input
	 *           input data for the function module. You may set this parameter to <code>null</code> if you don't have
	 *           any data to be logged.
	 * @param output
	 *           output data for the function module. You may set this parameter to <code>null</code> if you don't have
	 *           any data to be logged.
	 * @param log
	 *           the logging context to be used
	 */
	public static void logCall(final String functionName, final JCoRecord input, final JCoRecord output, final Logger log)
	{
		if (log.isDebugEnabled())
		{
			boolean recordOK = true;

			if (input instanceof JCoTable)
			{
				final JCoTable inputTable = (JCoTable) input;
				if (inputTable.getNumRows() <= 0)
				{
					recordOK = false;
				}
			}

			if ((input != null) && (recordOK == true))
			{

				final StringBuffer in = new StringBuffer();

				in.append("::").append(functionName).append("::").append(" - IN: ").append(input.getMetaData().getName())
						.append(" * ");

				final JCoFieldIterator iterator = input.getFieldIterator();

				while (iterator.hasNextField())
				{
					final JCoField field = iterator.nextField();

					logField(in, field);
				}

				log.debug(in.toString());
			}

			recordOK = true;

			if (output instanceof JCoTable)
			{
				final JCoTable outputTable = (JCoTable) output;
				if (outputTable.getNumRows() <= 0)
				{
					recordOK = false;
				}
			}

			if ((output != null) && (recordOK == true))
			{

				final StringBuffer out = new StringBuffer();

				out.append("::").append(functionName).append("::").append(" - OUT: ").append(output.getMetaData().getName())
						.append(" * ");

				final JCoFieldIterator iterator = output.getFieldIterator();

				while (iterator.hasNextField())
				{
					final JCoField field = iterator.nextField();

					logField(out, field);

				}

				log.debug(out.toString());
			}
		}
	}

	/**
	 * Logs a JCo field into the log file of the application.
	 * 
	 * @param out
	 *           the StringBuffer to be logged
	 * @param field
	 *           the field to be logged
	 */
	private static void logField(final StringBuffer out, final JCoField field)
	{

		if (field.isStructure())
		{
			out.append(field.getName()).append("=STRUCTURE[ ");
			final JCoStructure structure = field.getStructure();
			final JCoRecordFieldIterator recordFieldIterator = structure.getRecordFieldIterator();

			logRecordFieldIterator(out, recordFieldIterator);
			out.append("]");

		}
		else if (field.isTable())
		{
			out.append(field.getName()).append("=TABLE[ ");
			final JCoTable table = field.getTable();
			if (table.getNumRows() > 0)
			{
				final JCoRecordFieldIterator recordFieldIterator = table.getRecordFieldIterator();

				logRecordFieldIterator(out, recordFieldIterator);

			}

			out.append("]");

		}
		else
		{
			out.append(field.getName()).append("='").append(field.getString()).append("' ");
		}

	}

	/**
	 * Logs a JCo record field iterator into the log file of the application.
	 * 
	 * @param out
	 *           the StringBuffer to be logged
	 * @param recordFieldIterator
	 *           the record field iterator to be logged
	 */
	private static void logRecordFieldIterator(final StringBuffer out, final JCoRecordFieldIterator recordFieldIterator)
	{
		while (recordFieldIterator.hasNextField())
		{
			final JCoField fieldOfStructure = recordFieldIterator.nextField();

			logField(out, fieldOfStructure);

		}
	}

	/**
	 * Logs a RFC-call into the log file of the application.
	 * 
	 * @param functionName
	 *           the name of the JCo function that was executed
	 * @param input
	 *           input data for the function module. You may set this parameter to <code>null</code> if you don't have
	 *           any data to be logged.
	 * @param output
	 *           output data for the function module. You may set this parameter to <code>null</code> if you don't have
	 *           any data to be logged.
	 * @param log
	 *           the logging context to be used
	 */
	public static void logCall(final String functionName, final JCoHelper.RecordWrapper input,
			final JCoHelper.RecordWrapper output, final Logger log)
	{
		if (log.isDebugEnabled())
		{
			if ((input != null) && (output != null))
			{
				logCall(functionName, input.getRecord(), output.getRecord(), log);
			}
			else if (input != null)
			{
				logCall(functionName, input.getRecord(), null, log);
			}
			else if (output != null)
			{
				logCall(functionName, null, output.getRecord(), log);
			}
		}
	}

	/**
	 * Logs a RFC-call into the log file of the application.
	 * 
	 * @param functionName
	 *           the name of the JCo function that was executed
	 * @param input
	 *           input data for the function module. You may set this parameter to <code>null</code> if you don't have
	 *           any data to be logged.
	 * @param output
	 *           output data for the function module. You may set this parameter to <code>null</code> if you don't have
	 *           any data to be logged.
	 * @param log
	 *           the logging context to be used
	 */
	public static void logCall(final String functionName, final JCoTable input, final JCoTable output, final Log4JWrapper log)
	{

		if (log.isDebugEnabled())
		{
			if (input != null)
			{

				final int rows = input.getNumRows();
				final int cols = input.getNumColumns();
				final String inputName = input.getMetaData().getName();

				for (int i = 0; i < rows; i++)
				{
					final StringBuffer in = new StringBuffer();

					in.append("::").append(functionName).append("::").append(" - IN: ").append(inputName).append("[").append(i)
							.append("] ");

					// add an additional space, just to be fancy
					if (i < 10)
					{
						in.append("  ");
					}
					else if (i < 100)
					{
						in.append(' ');
					}

					in.append("* ");

					input.setRow(i);

					for (int k = 0; k < cols; k++)
					{
						in.append(input.getMetaData().getName(k)).append("='").append(input.getString(k)).append("' ");
					}
					log.debug(in.toString());
				}

				input.firstRow();
			}

			if (output != null)
			{

				final int rows = output.getNumRows();
				final int cols = output.getNumColumns();
				final String outputName = output.getMetaData().getName();

				for (int i = 0; i < rows; i++)
				{
					final StringBuffer out = new StringBuffer();

					out.append("::").append(functionName).append("::").append(" - OUT: ").append(outputName).append("[").append(i)
							.append("] ");

					// add an additional space, just to be fancy
					if (i < 10)
					{
						out.append("  ");
					}
					else if (i < 100)
					{
						out.append(' ');
					}

					out.append("* ");
					output.setRow(i);

					for (int k = 0; k < cols; k++)
					{
						out.append(output.getMetaData().getName(k)).append("='").append(output.getString(k)).append("' ");
					}
					log.debug(out.toString());
				}

				output.firstRow();
			}
		}
	}

	/**
	 * Logs a RFC-call into the log file of the application.
	 * 
	 * @param functionName
	 *           the function name to be logged
	 * @param input
	 *           input record
	 * @param output
	 *           output record
	 */
	protected static void logCall(final String functionName, final JCoRecord input, final JCoRecord output)
	{
		if (log.isDebugEnabled())
		{
			logCall(functionName, input, output, log);
		}
	}

	/**
	 * Logs a RFC-call into the log file of the application.
	 * 
	 * @param functionName
	 *           the function name to be logged
	 * @param input
	 *           input record wrapper
	 * @param output
	 *           output record wrapper
	 */
	protected static void logCall(final String functionName, final JCoHelper.RecordWrapper input,
			final JCoHelper.RecordWrapper output)
	{
		if (log.isDebugEnabled())
		{
			logCall(functionName, input, output, log);
		}
	}

	/**
	 * Logs a RFC-call into the log file of the application.
	 * 
	 * @param functionName
	 *           the function name to be logged
	 * @param input
	 *           input JCo table
	 * @param output
	 *           output JCo table
	 */
	protected static void logCall(final String functionName, final JCoTable input, final JCoTable output)
	{
		if (log.isDebugEnabled())
		{
			logCall(functionName, input, output, log);
		}
	}

	/**
	 * Log an exception with level ERROR.
	 * 
	 * @param functionName
	 *           the function name to be logged
	 * @param ex
	 *           the Exception to be logged
	 */
	protected static void logException(final String functionName, final JCoException ex)
	{

		final String message = functionName + " - EXCEPTION: GROUP='" + JCoHelper.getExceptionGroupAsString(ex) + "'" + ", KEY='"
				+ ex.getKey() + "'";
		log.log(Level.ERROR, message);

		if (log.isDebugEnabled())
		{
			log.debug(message);
		}
	}

	/**
	 * This methods avoids creation of independent copies of "" in order to save memory.
	 * 
	 * @param jcoRecord
	 *           the JCo record
	 * @param arg0
	 *           argument
	 * @return result
	 */
	public static String getStringPerfOpt(final JCoRecord jcoRecord, final String arg0)
	{
		final String result = jcoRecord.getString(arg0);
		if (EMPTY_STRING.equals(result))
		{
			return EMPTY_STRING;
		}
		else
		{
			return result;
		}
	}

	/**
	 * Obfuscate string value. String is replaced with a list of "*".
	 * 
	 * @param value
	 *           the value to be obfuscated
	 * @return obfuscated value
	 */
	static public String obfuscate(final String value)
	{
		final char OBFUSCATOR_CHAR = '*';
		if (value == null)
		{
			return null;
		}
		else
		{
			final StringBuffer strBuf = new StringBuffer(value);
			final int len = value.length();
			for (int i = len - 1; i >= 0; --i)
			{
				strBuf.setCharAt(i, OBFUSCATOR_CHAR);
			}
			return strBuf.toString();
		}
	}

	/**
	 * Logs a RFC-call into the log file of the application.
	 * 
	 * @param functionName
	 *           the name of the JCo function that was executed
	 * @param marker
	 *           textual maker to identify a particular logging
	 * @param table
	 *           input data for the function module. You may set this parameter to <code>null</code> if you don't have
	 *           any data to be logged.
	 * @param obfuscatedColumns
	 *           columns, values of this columns will be obfuscated
	 * @param log
	 *           the logging context to be used
	 */
	public static void logCall(final String functionName, final String marker, final JCoTable table,
			final String[] obfuscatedColumns, final Logger log)
	{

		if (log.isDebugEnabled() && table != null)
		{

			final Collection<String> obfuscatedColumnsSet = new HashSet<String>(Arrays.asList(obfuscatedColumns));

			final int rows = table.getNumRows();
			final int cols = table.getNumColumns();
			final String tableName = table.getMetaData().getName();

			for (int i = 0; i < rows; i++)
			{
				final StringBuffer in = new StringBuffer();

				in.append("::").append(functionName).append("::").append(" - ").append(marker).append(" ").append(tableName)
						.append("[").append(i).append("] ");

				// add an additional space, just to be fancy
				if (i < 10)
				{
					in.append("  ");
				}
				else if (i < 100)
				{
					in.append(' ');
				}

				in.append("* ");

				table.setRow(i);

				for (int k = 0; k < cols; k++)
				{
					final String columnName = table.getMetaData().getName(k);
					String value = table.getString(k);
					if (obfuscatedColumnsSet.contains(columnName))
					{
						value = obfuscate(value);
					}
					in.append(columnName).append("='").append(value).append("' ");
				}

				log.debug(in.toString());
			}

			table.firstRow();
		}
	}

}
