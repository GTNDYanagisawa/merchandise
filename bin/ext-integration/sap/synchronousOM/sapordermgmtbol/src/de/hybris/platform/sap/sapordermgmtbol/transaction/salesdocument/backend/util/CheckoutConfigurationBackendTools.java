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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util;

import java.util.HashMap;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.tc.logging.Severity;

import de.hybris.platform.sap.core.bol.businessobject.BORuntimeException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;

/**
 * Task of this class is to read SAP customizing used for checkout
 */
public class CheckoutConfigurationBackendTools {

	/**
	 * Logging instance
	 */
	protected static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(CheckoutConfigurationBackendTools.class.getName());

	/**
	 * generic method to call a searchvaluehelp in the backend and format result
	 * can be used for customizing tables, but not the complex valuehelps e.g.
	 * BP search <br>
	 * background: <br>
	 * called from the backendimplementations for CRM and ERP
	 * 
	 * @param jConn
	 *            JC0-Connection to the backend
	 * @param searchhelp
	 *            name of the searchhelp
	 * @param valueField
	 *            = Name of the Field, which contains the value
	 * @param descriptionField
	 *            = Name of the field, which contains the description background
	 * @param withFallbackDesc
	 *            = true means, if no description is found, the value is shown
	 *            as description. This is done in order to avoid lines with
	 *            space in the DropDownListboxes, which actually refer to values
	 * @return a HashMap with pairs ( content of the valuefield, content of the
	 *         descriptionfield)
	 * @throws BackendException
	 *             in case of issues with the JCO-Connection or ABAP-errors in
	 *             the backend
	 */
	public HashMap<String, String> executeSearchhelp(final JCoConnection jConn,
			final String searchhelp, final String valueField,
			final String descriptionField, final Boolean withFallbackDesc)
			throws BackendException {
		sapLogger.entering("executeSearchhelp()");
		JCoParameterList jParamList = null;
		HashMap<String, String> searchResult = null;

		// do the search

		// raises BackendException and writes log
		jParamList = executeBackendAccess(jConn, searchhelp);

		// convert the result from the generic functionmodule
		searchResult = convertSearchResult(jParamList, valueField,
				descriptionField, withFallbackDesc.booleanValue());

		sapLogger.exiting();
		return searchResult;

	} // execute Searchhelp

	/**
	 * @param jConn
	 *            JC0-Connection to the backend, normally type
	 *            JCoConnectionStateless
	 * @param searchhelp
	 *            name of the searchhelp in the backen
	 * @return the exportParameterlist with the tables containing the resultl
	 * @throws BackendException
	 */
	protected JCoParameterList executeBackendAccess(final JCoConnection jConn,
			final String searchhelp) throws BackendException {
		// jConn comes as input parameter as it is dependend on the backend ;
		sapLogger.entering("executeBackendAccess()");

		// step 1 execute the searchhelp
		JCoParameterList jImportParams = null;
		JCoParameterList jTableParams = null;
		JCoFunction jFunc = null;

		// Obtain the RFC-Function,
		// getFfunction raises BackendException, writes log
		jFunc = jConn.getFunction("BAPI_HELPVALUES_GET");

		// Fill the import parameters
		jImportParams = jFunc.getImportParameterList();

		jImportParams.setValue("OBJTYPE", "HELPVALUES");
		jImportParams.setValue("METHOD", "GETLIST");
		jImportParams.setValue("PARAMETER", "PARAMETER");
		jImportParams.getStructure("EXPLICIT_SHLP").setValue("SHLPNAME",
				searchhelp);
		jImportParams.getStructure("EXPLICIT_SHLP").setValue("SHLPTYPE", "SH");

		jConn.execute(jFunc);

		// in searchhelp are the result in tableParameters
		jTableParams = jFunc.getTableParameterList();

		// step 2 evaluate messages
		// errormessages from the backend are in the exportParams.odata[]
		final JCoParameterList jExportParams = jFunc.getExportParameterList();
		final JCoStructure returnStructure = jExportParams
				.getStructure("RETURN");

		if ("S&013".equals(returnStructure.getString("TYPE"))) {
			// if the checktable is empty, the searchhelp does not exist,...
			// the Bapi_Helpvalues_get returns in the parameter RETURN the
			// bapireturnstructure
			// with Type = E, messageid = S&, messageno = 013 and the text
			// "no helpvalues found"

			final String logMessage = "searchhelp" + searchhelp
					+ returnStructure.getString("MESSAGE")
					+ "check the situation in the Backend";
			sapLogger.log(Severity.WARNING,
					LogCategories.APPS_COMMON_CONFIGURATION,
					"Searchhelp doesn't exist", new Object[] { logMessage });
		}

		// log.exiting is done in the finally-clause
		sapLogger.exiting();
		return jTableParams;

	} // executeBackendAccess

	/**
	 * converts the result of the call to the BAPI_HELPVALUES_GET to an HashMap
	 * of pairs (value, description)
	 * 
	 * @param jTableParams
	 *            = contains all table parameters from the call of
	 *            BAPI_HELPVALUES_GET
	 * @param valueField
	 *            = Name of the Field, which contains the value
	 * @param descriptionField
	 *            = Name of the field, which contains the description background
	 * @param withFallbackDesc
	 *            = true means, if no description is found, the value is shown
	 *            as description. This is done in order to avoid lines with
	 *            space in the DropDownListboxes, which actually refer to values
	 * @return a HashMap with pairs ( content of the valuefield, content of the
	 *         descriptionfield)
	 */
	protected HashMap<String, String> convertSearchResult(
			final JCoParameterList jTableParams, final String valueField,
			final String descriptionField, final boolean withFallbackDesc) {
		sapLogger.entering("convertSearchResult()");

		// step 1 determine in the table "description_for_helpvalues" the
		// position of the required fields

		// background
		// tableparams contains four tables:
		// - Selection_for_helpvalues structure bapif4b contains the
		// selectoption parameters with incl/excl low and high in our
		// case always empty
		// - Helpvalues structure bapif4c contains the searchresult. It has
		// per result a row, which has to be splitted according to the
		// information in bapif4e -
		// - values_for_field structure bapif4d -
		// description_for_helpvalues structure bapif4e contains per
		// field of the resultstructure an entry with tablename,
		// fieldname,position,offset,length and descriptions
		//
		// for debugging the path to the data is >tableparams>metaData
		// >name[1],[2],[3],[4] contain the name of the parameters
		// eg. "Helpvalues"
		// >tabMeta[1],[2],[3],[4] > recname contain the name of the structure
		// e.g.
		// bapif4b
		// > name[j] contain the names of the fields in the structure e.g.
		// "TABNAME"
		// >odata[1],[2],[3],[4] > numRows contain the number of lines
		// > tableDataRows[j] contains the entry of table in string format. has
		// to be
		// split into the real fields

		final HashMap<String, String> searchResult = new HashMap<String, String>();

		JCoTable jFieldList;
		jFieldList = jTableParams.getTable("DESCRIPTION_FOR_HELPVALUES");

		int intValueFieldOffset = 0;
		int intValueFieldEnd = 0;
		int intDescriptionFieldOffset = 0;
		int intDescriptionFieldEnd = 0;

		for (int i = 0; i < jFieldList.getNumRows(); i++) {
			jFieldList.setRow(i);
			try {
				if (jFieldList.getString("FIELDNAME").equals(valueField)) {
					intValueFieldOffset = Integer.parseInt(jFieldList
							.getString("OFFSET"));
					intValueFieldEnd = intValueFieldOffset
							+ Integer.parseInt(jFieldList.getString("LENG"));
				}

				if (jFieldList.getString("FIELDNAME").equals(descriptionField)) {
					intDescriptionFieldOffset = Integer.parseInt(jFieldList
							.getString("OFFSET"));
					intDescriptionFieldEnd = intDescriptionFieldOffset
							+ Integer.parseInt(jFieldList.getString("LENG"));
				}
			} catch (final NumberFormatException ex) {
				sapLogger.log(Severity.ERROR,
						LogCategories.APPS_BUSINESS_LOGIC,
						"Exception during access to Customizing",
						new Object[] { ex.toString() });
				sapLogger.traceThrowable(Severity.DEBUG,
						"Exception during access to Customizing", ex);
				sapLogger.exiting();
				throw new BORuntimeException(ex.getMessage(), ex);
			}
		} // endfor

		// ---------------------------------------------------
		// step 2: build up the result from the helpvalues
		// checks, if the both fields are found
		if (intValueFieldEnd == 0) {
			final BORuntimeException ex = new BORuntimeException(
					"Exception during access to customizing in backend");
			sapLogger.log(Severity.ERROR, LogCategories.APPS_BUSINESS_LOGIC,
					"Exception during access to Customizing",
					new Object[] { ex.toString() });
			sapLogger.traceThrowable(Severity.DEBUG,
					"Exception during access to Customizing", ex);
			sapLogger.exiting();
			throw new BORuntimeException("Error in searchhelp results", ex);

		}
		if (intDescriptionFieldEnd == 0) {
			final BORuntimeException ex = new BORuntimeException(
					"Exception during access to customizing in backend");
			sapLogger.log(Severity.ERROR, LogCategories.APPS_BUSINESS_LOGIC,
					"Exception during access to Customizing",
					new Object[] { ex.toString() });
			sapLogger.traceThrowable(Severity.DEBUG,
					"Exception during access to Customizing", ex);
			sapLogger.exiting();
			throw new BORuntimeException("Error in searchhelp results", ex);
		}

		jFieldList = jTableParams.getTable("HELPVALUES");
		for (int i = 0; i < jFieldList.getNumRows(); i++) {

			jFieldList.setRow(i);
			final String stringHelpvalues = jFieldList.getString("HELPVALUES");
			String stringValue = "";
			String stringDescription = "";
			// the evaluation with the length is necessary to avoid exceptions
			// in the substring command
			// as the stringHelpvalues has only the length till the last
			// character not equal " " !
			final int strlenHelpvalues = stringHelpvalues.length();
			int strlenFieldStart = 0;
			int strlenFieldEnd = 0;
			// if the valueField is found, evalute it
			if (intValueFieldEnd > 0) {

				strlenFieldStart = Math.min(strlenHelpvalues,
						intValueFieldOffset);
				strlenFieldEnd = Math.min(strlenHelpvalues, intValueFieldEnd);
				final String work = stringHelpvalues.substring(
						strlenFieldStart, strlenFieldEnd);
				stringValue = deleteTrailingSpaces(work);

			} // if valueField found

			// if the descriptionField is found, evaluate it
			if (intDescriptionFieldEnd > 0) {

				strlenFieldEnd = Math.min(strlenHelpvalues,
						intDescriptionFieldEnd);
				String workstr = null;
				if (strlenHelpvalues > intDescriptionFieldOffset) {
					stringDescription = stringHelpvalues.substring(
							intDescriptionFieldOffset, strlenFieldEnd);
				}
				if (withFallbackDesc) {
					// if no description is maintain, the value is given
					// back as description
					// remark: in case of reading non-descriptional fields
					// the real empty value is required. It is not allowed
					// to give the stringvalue back. Examples: field
					// LocationObligatory for incoterms
					workstr = stringDescription.trim();
					if (workstr == null || workstr.length() == 0) {
						stringDescription = stringValue;
					}
				}

			} // if descriptionField found
			searchResult.put(stringValue, stringDescription);
		} // for
		sapLogger.exiting();
		return searchResult;

	} // convertSearchResult

	/**
	 * removes character space ( = " ") at the end of the inString examples:
	 * <ul>
	 * <li>"a b c " returns " a b c"</li>
	 * <li>"          " returns ""</li>
	 * <li>"" returns ""</li>
	 * <li>null returns null</li>
	 * </ul>
	 * 
	 * @param inString
	 *            input string
	 * @return a copy of the inString, with all space-characters removed from
	 *         the end
	 */
	public static String deleteTrailingSpaces(final String inString) {
		// without entering/exiting as it is a simple toolmethod
		String work = inString;

		if (work != null) {
			// length() > 0 is necessary to prevent a "out of range" !
			// if " " is not found , then lastIndexOf = -1,
			while (work.length() > 0
					&& work.lastIndexOf(' ') == work.length() - 1) {
				work = work.substring(0, work.length() - 1);

			} // while
		} // if
		return work;
	} // deleteTrailingSpaces
}