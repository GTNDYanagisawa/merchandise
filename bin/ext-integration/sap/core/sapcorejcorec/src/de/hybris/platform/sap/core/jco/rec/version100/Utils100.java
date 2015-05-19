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
package de.hybris.platform.sap.core.jco.rec.version100;

import de.hybris.platform.sap.core.jco.rec.RepositoryPlayback;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.FieldType;

import com.sap.conn.jco.JCoMetaData;


/**
 * Constants and methods used for parsing and saving of the JCoRecorder repository files.
 */
public class Utils100
{

	/** Key-word used for {@link RepositoryPlayback#getRecord(String)} if an import-parameter is requested. */
	public static final String PARAMETERLIST_IMPORT_NAME = "INPUT";
	/** Key-word used for {@link RepositoryPlayback#getRecord(String)} if an export-parameter is requested. */
	public static final String PARAMETERLIST_EXPORT_NAME = "OUPUT";
	/** Key-word used for {@link RepositoryPlayback#getRecord(String)} if an changing-parameter is requested. */
	public static final String PARAMETERLIST_CHANGING_NAME = "CHANGING";
	/** Key-word used for {@link RepositoryPlayback#getRecord(String)} if an table-parameter is requested. */
	public static final String PARAMETERLIST_TABLES_NAME = "TABLES";

	/** This byte length is used by the recorder as a default value for BCD elements. */
	public static final int STANDARD_LENGTH_BCD = 16;
	/** This byte length is used by the recorder as a default value for elements representing character strings. */
	public static final int STANDARD_LENGTH_SYMBOLCHAIN = 16;//64;

	/**
	 * Converts a {@link FieldType} value to an integer value used by JCo.
	 * 
	 * @param type
	 *           the {@link FieldType} value that shall be converted.
	 * @return Returns the corresponding integer value.
	 */
	public static int fieldTypeToInt(final FieldType type)
	{
		switch (type)
		{
			case BCD:
				return JCoMetaData.TYPE_BCD; //2
			case BYTE:
				return JCoMetaData.TYPE_BYTE; //4
			case CHAR:
				return JCoMetaData.TYPE_CHAR; //0
			case DATE:
				return JCoMetaData.TYPE_DATE; //1
			case DECF_16:
				return JCoMetaData.TYPE_DECF16; //23
			case DECF_34:
				return JCoMetaData.TYPE_DECF34; //24
			case FLOAT:
				return JCoMetaData.TYPE_FLOAT; //7
			case INT:
				return JCoMetaData.TYPE_INT; //8
			case INT_1:
				return JCoMetaData.TYPE_INT1; //10
			case INT_2:
				return JCoMetaData.TYPE_INT2; //9
			case NUM:
				return JCoMetaData.TYPE_NUM; //6
			case STRING:
				return JCoMetaData.TYPE_STRING; //29
			case TIME:
				return JCoMetaData.TYPE_TIME; //3
			case XSTRING:
				return JCoMetaData.TYPE_XSTRING; //30
				//STRUCTURE 17
				//TABLE 99
			default:
				throw new UnsupportedOperationException("The Fieldtype value " + type + " does not exist!");
		}
	}

	/**
	 * Converts a integer type value (used by JCo) to the corresponding {@link FieldType} value.
	 * 
	 * @param type
	 *           the integer value that shall be converted.
	 * @return Returns the corresponding {@link FieldType} value.
	 */
	public static FieldType fieldTypeFromInt(final int type)
	{
		switch (type)
		{
			case JCoMetaData.TYPE_BCD:
				return FieldType.BCD; //2
			case JCoMetaData.TYPE_BYTE:
				return FieldType.BYTE; //4
			case JCoMetaData.TYPE_CHAR:
				return FieldType.CHAR; //0
			case JCoMetaData.TYPE_DATE:
				return FieldType.DATE; //1
			case JCoMetaData.TYPE_DECF16:
				return FieldType.DECF_16; //23
			case JCoMetaData.TYPE_DECF34:
				return FieldType.DECF_34; //24
			case JCoMetaData.TYPE_FLOAT:
				return FieldType.FLOAT; //7
			case JCoMetaData.TYPE_INT:
				return FieldType.INT; //8
			case JCoMetaData.TYPE_INT1:
				return FieldType.INT_1; //10
			case JCoMetaData.TYPE_INT2:
				return FieldType.INT_2; //9
			case JCoMetaData.TYPE_NUM:
				return FieldType.NUM; //6
			case JCoMetaData.TYPE_STRING:
				return FieldType.STRING; //29
			case JCoMetaData.TYPE_TIME:
				return FieldType.TIME; //3
			case JCoMetaData.TYPE_XSTRING:
				return FieldType.XSTRING; //30
				//STRUCTURE 17
				//TABLE 99
			default:
				throw new UnsupportedOperationException("The Fieldtype value " + type + " does not exist!");
		}
	}
}
