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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp;

import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionTools;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;


/**
 * Converts SAP process types to their language dependent representations
 */
public class ProcessTypeConverter
{

	/**
	 * RFC export parameter EV_AUART_SPR
	 */
	public static final String EXPORT_PARAMETER_PROCESSTYPE = "EV_AUART_SPR";
	/**
	 * RFC import parameter IV_LANGUAGE
	 */
	public static final String INPUT_PARAMETER_LANGUAGE = "IV_LANGUAGE";
	/**
	 * RFC import parameter IV_AUART
	 */
	public static final String INPUT_PARAMETER_PROCESSTYPE = "IV_AUART";
	/**
	 * Conversion FM
	 */
	public static final String FM_FOR_CONVERSION = "ERP_WEC_CUST_AUART_TO_SPR";

	/**
	 * Converts a standard process type to its language dependent value
	 * 
	 * @param processType
	 *           the language independent process type from shop customising
	 * @param conn
	 *           the JCO connection to be used for access to ERP back end
	 * @return language dependent process type
	 * @throws BackendException
	 *            in case of a back-end error
	 */
	public String convertProcessTypeToLanguageDependent(final String processType, final JCoConnection conn)
			throws BackendException
	{

		final String language = getSAPLanguage();
		final String translProcType = getLanguageDependentProcessTypeFromBackend(processType, language, conn);

		return translProcType;
	}

	private String getLanguageDependentProcessTypeFromBackend(final String processType, final String language,
			final JCoConnection conn) throws BackendException
	{

		final JCoFunction function = conn.getFunction(FM_FOR_CONVERSION);
		final JCoParameterList importParameters = function.getImportParameterList();
		importParameters.setValue(INPUT_PARAMETER_PROCESSTYPE, processType);
		importParameters.setValue(INPUT_PARAMETER_LANGUAGE, language);

		conn.execute(function);

		final JCoParameterList exportParameters = function.getExportParameterList();
		String translProcType = exportParameters.getString(EXPORT_PARAMETER_PROCESSTYPE);

		if (translProcType == null || translProcType.isEmpty())
		{
			translProcType = processType;
		}

		return translProcType;
	}

	private String getSAPLanguage()
	{
		final String isoLanguage = LocaleUtil.getLocale().getLanguage();
		final String sapLanguage = ConversionTools.getR3LanguageCode(isoLanguage);
		return sapLanguage;
	}

}
