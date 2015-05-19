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
package de.hybris.platform.sap.core.jco.rec.version000;


/**
 * Constants used for parsing of the JCoRecorder repository files.
 */
public final class Utils000
{
	/** file name of the validation xsd file. */
	public static final String VALIDATION_FILE_NAME = "validation000.xsd";
	/** file path of the validation xsd file inclusive file name. */
	public static final String VALIDATION_FILE_SOURCE = "testdata/jcorecorder/" + VALIDATION_FILE_NAME;


	/** XML Tag-Name for the root node. */
	public static final String TAGNAME_REPOSITORY_ROOT = "FunctionRepository";
	/** XML Tag-Name for the metadata root node. */
	public static final String TAGNAME_METADATA_ROOT = "MetaData";
	/** XML Tag-Name for the metadata template root node. */
	public static final String TAGNAME_METADATA_TEMPLATE_ROOT = "FunctionTemplate";
	/** XML Tag-Name for the metadata function. */
	public static final String TAGNAME_METADATA_FUNCTION = "MFunc";
	/** XML Tag-Name for the metadata input parameterlist. */
	public static final String TAGNAME_METADATA_INPUT = "MIn";
	/** XML Tag-Name for the metadata output parameterlist. */
	public static final String TAGNAME_METADATA_OUTPUT = "MOut";
	/** XML Tag-Name for the metadata changing parameterlist. */
	public static final String TAGNAME_METADATA_CHANGING = "MChan";
	/** XML Tag-Name for the metadata JCo table. */
	public static final String TAGNAME_METADATA_TABLE = "MTab";
	/** XML Tag-Name for the metadata field. */
	public static final String TAGNAME_METADATA_FIELD = "MField";

	/** XML Tag-Name for the metadata record root node. */
	public static final String TAGNAME_METADATA_RECORD_ROOT = "RecordMetaData";
	/** XML Tag-Name for the metadata record. */
	public static final String TAGNAME_METADATA_RECORD = "MRec";

	/** XML Tag-Name for function root node. */
	public static final String TAGNAME_FUNCTION_ROOT = "Functions";
	/** XML Tag-Name for a single RFC function. */
	public static final String TAGNAME_SINGLE_RFC = "RFC";
	/** XML Tag-Name for the import parameterlist. */
	public static final String TAGNAME_IMPORT_PARAM = "INPUT";
	/** XML Tag-Name for the export parameterlist. */
	public static final String TAGNAME_EXPORT_PARAM = "OUTPUT";
	/** XML Tag-Name for the table parameterlist. */
	public static final String TAGNAME_TABLE_PARAM = "TABLES";
	/** XML Tag-Name for the changing parameterlist. */
	public static final String TAGNAME_CHANGING_PARAM = "CHANGING";
	/** XML Tag-Name for the exceptionlist. */
	public static final String TAGNAME_EXCEPTION_PARAM = "EXCEPTION";
	/** XML Tag-Name for a child of the Tag {@link #TAGNAME_TABLE_PARAM}. */
	public static final String TAGNAME_TABLE_ITEM = "item";

	/** XML Tag-Name for record root node. */
	public static final String TAGNAME_RECORDS_ROOT = "Records";



	/** XML Attribute-Name for the name of the function. */
	public static final String ATTNAME_FUNCTION_NAME = "functionName";
	/** XML Attribute-Name for the metadata key. */
	public static final String ATTNAME_FUNCTION_KEY = "key";
	/** XML Attribute-Name for the timestamp of the function call. */
	public static final String ATTNAME_RECORD_TIME = "recordTime";
	/** XML Attribute-Name for the key of the repository. */
	public static final String ATTNAME_REPO_KEY = "repoKey";

	/** XML Attribute-Name for the metadata decimals. */
	public static final String ATTNAME_METADATA_DECIMALS = "mDEC";
	/** XML Attribute-Name for the metadata description. */
	public static final String ATTNAME_METADATA_DESCRIPTION = "mDESC";
	/** XML Attribute-Name for the metadata name of a field. */
	public static final String ATTNAME_METADATA_FIELDNAME = "mFN";
	/** XML Attribute-Name for the metadata fieldtype. */
	public static final String ATTNAME_METADATA_FIELDTYPE = "mFT";
	/** XML Attribute-Name for the metadata recordtype. */
	public static final String ATTNAME_METADATA_RECORDTYPE = "mRT";

	/** XML Attribute-Name for the metadata uc bytelength. */
	public static final String ATTNAME_METADATA_UC_BYTELENGTH = "ucBL";
	/** XML Attribute-Name for the metadata nuc bytelength. */
	public static final String ATTNAME_METADATA_NUC_BYTELENGTH = "nucBL";
	/** XML Attribute-Name for the metadata uc byteoffset. */
	public static final String ATTNAME_METADATA_UC_BYTELOFFSET = "ucBO";
	/** XML Attribute-Name for the metadata nuc byteoffset. */
	public static final String ATTNAME_METADATA_NUC_BYTEOFFSET = "nucBO";


	/* ------- unused ------- */

	/** XML Tag-Name for the JCo structures. */
	public static final String TAGNAME_STRUCTURE = "Structure";
	/** XML Tag-Name for a single JCo table row. */
	public static final String TAGNAME_TABLE_ROW = "Row";

	/** XML Attribute-Name for the metadata default values. */
	public static final String ATTNAME_METADATA_DEFAULTS = "mDEF";
	/** XML Attribute-Name for the metadata line type. */
	public static final String ATTNAME_METADATA_LINE_TYPE = "mLT";
	/** XML Attribute-Name for the name of a JCo structure. */
	public static final String ATTNAME_STRUCTURE_NAME = "structureName";
	/** XML Attribute-Name for the name of a JCo table. */
	public static final String ATTNAME_TABLE_NAME = "tableName";
	/** XML Attribute-Name for the name of a JCo table row. */
	public static final String ATTNAME_TABLE_ROW = "count";
	/** XML Attribute-Name for the history aware flag. */
	public static final String ATTNAME_IS_HISTORY_AWARE = "hAware";

}
