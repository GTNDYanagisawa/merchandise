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
package de.hybris.platform.acceleratorservices.payment.dao.impl;


import de.hybris.platform.acceleratorservices.model.payment.CCPaySubValidationModel;
import de.hybris.platform.acceleratorservices.payment.dao.CreditCardPaymentSubscriptionDao;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


public class DefaultCreditCardPaymentSubscriptionDao extends AbstractItemDao implements CreditCardPaymentSubscriptionDao
{
	private final String PAYMENT_QUERY = "SELECT {p.pk} FROM {CreditCardPaymentInfo as p} "
			+ "WHERE {p.subscriptionId} = ?subscriptionId ORDER BY {" + CreditCardPaymentInfoModel.MODIFIEDTIME + "} DESC";

	private final String SUBSCRIPTION_QUERY = "SELECT {p.pk} FROM {CCPaySubValidation as p} "
			+ "WHERE {p.subscriptionId} = ?subscriptionId";

	@Override
	public CCPaySubValidationModel findSubscriptionValidationBySubscription(final String subscriptionId)
	{
		validateParameterNotNull(subscriptionId, "subscriptionId must not be null!");

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(SUBSCRIPTION_QUERY);
		fQuery.addQueryParameter("subscriptionId", subscriptionId);

		final SearchResult<CCPaySubValidationModel> searchResult = getFlexibleSearchService().search(fQuery);
		final List<CCPaySubValidationModel> results = searchResult.getResult();

		if (results != null && results.iterator().hasNext())
		{
			return results.iterator().next();
		}

		return null;
	}

	@Override
	public CreditCardPaymentInfoModel findCreditCartPaymentBySubscription(final String subscriptionId)
	{
		validateParameterNotNull(subscriptionId, "subscriptionId must not be null!");

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(PAYMENT_QUERY);
		fQuery.addQueryParameter("subscriptionId", subscriptionId);

		final SearchResult<CreditCardPaymentInfoModel> searchResult = getFlexibleSearchService().search(fQuery);
		final List<CreditCardPaymentInfoModel> results = searchResult.getResult();

		if (results != null && results.iterator().hasNext())
		{
			return results.iterator().next();
		}

		return null;
	}
}
