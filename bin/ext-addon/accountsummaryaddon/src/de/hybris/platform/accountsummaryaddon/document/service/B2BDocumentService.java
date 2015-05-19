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
package de.hybris.platform.accountsummaryaddon.document.service;

import java.util.List;

import de.hybris.platform.accountsummaryaddon.document.AccountSummaryDocumentQuery;
import de.hybris.platform.accountsummaryaddon.enums.DocumentStatus;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentTypeModel;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.search.SearchResult;


/**
 * Provides services for B2BDocument business logic/domain
 * 
 */
public interface B2BDocumentService
{
	/**
	 * Finds all B2BDocumentModel filtered by the query. The resulting list only contains document associated to the
	 * current user's B2BUnit.
	 * 
	 * @param query
	 *           paged document query
	 * @return result : a SeachPageData< B2BDocumentModel > containing documents.
	 */
	SearchPageData<B2BDocumentModel> findDocuments(final AccountSummaryDocumentQuery query);

	/**
	 * Return all open documents for a given B2B Unit.
	 * 
	 * @param unit
	 *           the B2B unit.
	 * @return result : a SeachPageData< B2BDocumentModel > containing open documents.
	 */
	SearchResult<B2BDocumentModel> getOpenDocuments(final B2BUnitModel unit);

	/**
	 * Returns all open documents for a given documentMediaModel
	 * 
	 * @param mediaModel
	 *           the media model
	 * @return result : a SeachPageData< B2BDocumentModel > containing open documents.
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
	void deleteB2BDocumentFiles(final int numberOfDays, final List<B2BDocumentTypeModel> documentTypes,
			final List<DocumentStatus> documentStatuses);
}
