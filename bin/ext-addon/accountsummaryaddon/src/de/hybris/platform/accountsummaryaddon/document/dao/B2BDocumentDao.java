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
package de.hybris.platform.accountsummaryaddon.document.dao;

import java.util.List;

import de.hybris.platform.accountsummaryaddon.enums.DocumentStatus;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentTypeModel;
import de.hybris.platform.accountsummaryaddon.model.DocumentMediaModel;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.search.SearchResult;


public interface B2BDocumentDao
{

	/**
	 * Gets all open documents for a given B2B Unit.
	 * 
	 * @param unit
	 *           the B2B unit
	 * @return a SearchResult<B2BDocumentModel> containing open documents.
	 */
	SearchResult<B2BDocumentModel> getOpenDocuments(final B2BUnitModel unit);

	/**
	 * Gets all open documents for a given MediaModel.
	 * 
	 * @param mediaModel
	 *           the media model.
	 * @return a SearchResult<B2BDocumentModel> containing open documents.
	 */
	SearchResult<B2BDocumentModel> getOpenDocuments(final MediaModel mediaModel);

	/**
	 * @param numberOfDays
	 *           elapsed days since the document media's creation time
	 * @param documentTypes
	 *           a list of document types
	 * @param documentStatuses
	 *           a list of document statuses
     * @return a SearchResult<DocumentMediaModel> containing document media.
	 */
	SearchResult<DocumentMediaModel> findOldDocumentMedia(final int numberOfDays, final List<B2BDocumentTypeModel> documentTypes,
			final List<DocumentStatus> documentStatuses);
}
