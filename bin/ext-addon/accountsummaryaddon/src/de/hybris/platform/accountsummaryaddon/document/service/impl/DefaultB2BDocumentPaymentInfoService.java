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
package de.hybris.platform.accountsummaryaddon.document.service.impl;

import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import de.hybris.platform.accountsummaryaddon.document.dao.B2BDocumentPaymentInfoDao;
import de.hybris.platform.accountsummaryaddon.document.data.B2BDragAndDropData;
import de.hybris.platform.accountsummaryaddon.document.service.B2BDocumentPaymentInfoService;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentPaymentInfoModel;


public class DefaultB2BDocumentPaymentInfoService extends AbstractBusinessService implements B2BDocumentPaymentInfoService
{

	@Resource
	private B2BDocumentPaymentInfoDao b2bDocumentPaymentInfoDao;

	@Resource
	private ModelService modelService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Override
	public SearchResult<B2BDocumentPaymentInfoModel> getDocumentPaymentInfo(final String documentNumber)
	{
		return b2bDocumentPaymentInfoDao.getDocumentPaymentInfo(documentNumber);

	}

	@Override
	public void applyPayment(final List<B2BDragAndDropData> lstActions)
	{


		for (final B2BDragAndDropData action : lstActions)
		{

			final B2BDocumentModel doc = new B2BDocumentModel();

			// get pay document
			doc.setDocumentNumber(action.getPayNumber());
			final B2BDocumentModel payDocument = flexibleSearchService.getModelByExample(doc);

			// get use document
			doc.setDocumentNumber(action.getUseNumber());
			final B2BDocumentModel useDocument = flexibleSearchService.getModelByExample(doc);

			// create new document payment info
			final B2BDocumentPaymentInfoModel paymentInfo = modelService.create(B2BDocumentPaymentInfoModel.class);
			paymentInfo.setAmount(action.getAmount());
			paymentInfo.setPayDocument(payDocument);
			paymentInfo.setUseDocument(useDocument);
			paymentInfo.setDate(new Date());
			paymentInfo.setExternal(UUID.randomUUID().toString());

			payDocument.setOpenAmount(payDocument.getOpenAmount().subtract(action.getAmount()));
			useDocument.setOpenAmount(useDocument.getOpenAmount().subtract(action.getAmount()));


		}

		modelService.saveAll();

	}
}
