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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;


/**
 * Abstract class which provides generic functionality to exchange texts between ERP (via LO-API) and the BOL layer
 */
abstract public class TextMapper extends BaseMapper
{

	/**
	 * ID of LO-API segment dealing with texts
	 */
	public static final String OBJECT_ID_TEXT = "TEXT";

	protected String configTextId = null;
	protected String configLangIso = null;

	/**
	 * Injected (eventually) text ID
	 * 
	 * @param configTextId
	 */
	public void setConfigTextId(final String configTextId)
	{
		this.configTextId = configTextId;
	}

	/**
	 * Injected (eventually) Language Iso code.
	 * 
	 * @param configLangIso
	 */
	public void setConfigLangIso(final String configLangIso)
	{
		this.configLangIso = configLangIso;
	}


	protected void traceReadTexts(final Text text, final String description, final Log4JWrapper sapLogger)
	{
		final StringBuilder debugOutput = new StringBuilder("Text read from backend: ");
		debugOutput.append(ConstantsR3Lrd.SEPARATOR + "Text found  " + description);
		debugOutput.append(ConstantsR3Lrd.SEPARATOR + "ID:         " + text.getId());
		debugOutput.append(ConstantsR3Lrd.SEPARATOR + "Text:       " + text.getText());
		debugOutput.append(ConstantsR3Lrd.SEPARATOR + "Handle:     " + text.getHandle());
		sapLogger.debug(debugOutput);
	}

	protected void traceText(final String heading, final String id, final String langu, final Text text, final String handle,
			final String parentHandle, final Log4JWrapper sapLogger)
	{

		final StringBuilder debugOutput = new StringBuilder(heading);
		debugOutput.append(ConstantsR3Lrd.LF + "Handle         : " + handle);
		debugOutput.append(ConstantsR3Lrd.LF + "Parent handle  : " + parentHandle);
		debugOutput.append(ConstantsR3Lrd.LF + "ID             : " + id);
		debugOutput.append(ConstantsR3Lrd.LF + "Langu          : " + langu);

		if (text != null)
		{
			debugOutput.append(ConstantsR3Lrd.LF + "Text            : " + text.getText());
		}
		else
		{
			debugOutput.append(ConstantsR3Lrd.LF + "No text");
		}
		sapLogger.debug(debugOutput);
	}

}
