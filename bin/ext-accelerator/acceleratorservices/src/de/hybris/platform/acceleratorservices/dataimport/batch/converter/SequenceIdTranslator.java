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
package de.hybris.platform.acceleratorservices.dataimport.batch.converter;

import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.servicelayer.exceptions.SystemException;

import org.apache.commons.lang.StringUtils;


/**
 * Translator for the sequenceId attribute comparing the sequenceId (e.g. UTC) of the import with the stored value in
 * the database.
 */
public class SequenceIdTranslator extends AbstractValueTranslator
{

	@Override
	public Object importValue(final String valueExpr, final Item toItem) throws JaloInvalidParameterException
	{
		clearStatus();
		Long result = null;
		if (!StringUtils.isBlank(valueExpr))
		{
			try
			{
				result = Long.valueOf(valueExpr);
			}
			catch (final NumberFormatException exc)
			{
				throw new JaloInvalidParameterException(exc, 0);
			}
			if (toItem != null)
			{
				Long curSeqId = null;
				try
				{
					curSeqId = (Long) toItem.getAttribute("sequenceId");
				}
				catch (final JaloSecurityException e)
				{
					throw new SystemException("attribute sequenceId unreadable for item " + toItem.getPK(), e);
				}
				if (curSeqId != null && isInValidSequenceId(result, curSeqId))
				{
					setError();
				}
			}
		}
		return result;
	}

	@Override
	public String exportValue(final Object value) throws JaloInvalidParameterException
	{
		return value == null ? "" : value.toString();
	}

	/**
	 * Verify, if the sequence is valid
	 * 
	 * @param sequenceId
	 * @param curSeqId
	 * @return true, if the sequenceId is invalid
	 */
	protected boolean isInValidSequenceId(final Long sequenceId, final Long curSeqId)
	{
		return curSeqId.compareTo(sequenceId) >= 0;
	}
}
