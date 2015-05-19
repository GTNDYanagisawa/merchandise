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
package de.hybris.platform.accountsummaryaddon.document;

import java.util.Date;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.time.DateUtils;

import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.accountsummaryaddon.utils.XDate;

public class B2BDocumentPastDuePredicate implements Predicate
{
	public B2BDocumentPastDuePredicate()
	{
	}

	@Override
	public boolean evaluate( final Object doc )
	{
		if( !(doc instanceof B2BDocumentModel) )
		{
			return false;
		}
		
		final B2BDocumentModel document = (B2BDocumentModel) doc;
		
		final Date now = XDate.setToEndOfDay(DateUtils.addDays(new Date(), -1));

		return document.getDueDate() != null && document.getDueDate().getTime() <= now.getTime();
	}
}
