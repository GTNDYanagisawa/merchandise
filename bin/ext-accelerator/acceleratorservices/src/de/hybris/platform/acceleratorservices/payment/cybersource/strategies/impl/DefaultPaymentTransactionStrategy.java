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
package de.hybris.platform.acceleratorservices.payment.cybersource.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.*;

import de.hybris.platform.acceleratorservices.payment.data.OrderInfoData;
import de.hybris.platform.acceleratorservices.payment.strategies.PaymentTransactionStrategy;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class DefaultPaymentTransactionStrategy implements PaymentTransactionStrategy
{

	private static final String REVIEW_DECISION_EVENT_NAME = "_ReviewDecision";
	private CommerceCheckoutService commerceCheckoutService;
	private ModelService modelService;
	private FlexibleSearchService flexibleSearchService;
	private BusinessProcessService businessProcessService;
	private PaymentService paymentService;

	@Override
	public PaymentTransactionEntryModel savePaymentTransactionEntry(final CustomerModel customerModel, final String requestId,
			final OrderInfoData orderInfoData)
	{
		validateParameterNotNull(orderInfoData, "orderInfoData cannot be null");

		final PaymentTransactionModel transaction = getModelService().create(PaymentTransactionModel.class);
		final PaymentTransactionType paymentTransactionType = PaymentTransactionType.CREATE_SUBSCRIPTION;
		transaction.setCode(customerModel.getUid() + "_" + UUID.randomUUID());
		transaction.setRequestId(requestId);
		transaction.setRequestToken(orderInfoData.getOrderPageRequestToken());
		transaction.setPaymentProvider(getCommerceCheckoutService().getPaymentProvider());
		getModelService().save(transaction);

		final PaymentTransactionEntryModel entry = getModelService().create(PaymentTransactionEntryModel.class);
		entry.setType(paymentTransactionType);
		entry.setRequestId(requestId);
		entry.setRequestToken(orderInfoData.getOrderPageRequestToken());
		entry.setTime(new Date());
		entry.setPaymentTransaction(transaction);
		entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
		entry.setCode(getPaymentService().getNewPaymentTransactionEntryCode(transaction, paymentTransactionType));
		getModelService().save(entry);
		return entry;
	}

	@Override
	public void setPaymentTransactionReviewResult(final PaymentTransactionEntryModel reviewDecisionEntry, final String guid)
	{
		if (reviewDecisionEntry == null || guid == null)
		{
			return;
		}

		OrderModel order = new OrderModel();
		// the guid is the merchant id composed of order.guid + -<number> for example: 599b141c2dc0a5d2ecf6735d2c9fb7a7416d83fa-1
		// we want to search for an order using the original guid
		order.setGuid(StringUtils.substringBefore(guid, "-"));
		order = getFlexibleSearchService().getModelByExample(order);
		final PaymentTransactionModel transaction = (order.getPaymentTransactions().iterator().hasNext() ? order
				.getPaymentTransactions().iterator().next() : null);

		if (checkIfAuthorizationWaitForReview(transaction))
		{
			reviewDecisionEntry.setPaymentTransaction(transaction);
			reviewDecisionEntry.setCode(getPaymentService().getNewPaymentTransactionEntryCode(transaction, PaymentTransactionType.REVIEW_DECISION));
			getModelService().save(reviewDecisionEntry);

			//send an event
			getBusinessProcessService().triggerEvent(transaction.getOrder().getCode() + REVIEW_DECISION_EVENT_NAME);
		}
	}

	protected boolean checkIfAuthorizationWaitForReview(final PaymentTransactionModel transaction)
	{
		if (transaction != null)
		{
			final List<PaymentTransactionEntryModel> transactionEntries = transaction.getEntries();
			for (int index = transactionEntries.size() - 1; index >= 0; index--)
			{
				final PaymentTransactionEntryModel entry = transactionEntries.get(index);

				if (isReviewDecision(entry))
				{
					return false;
				}
				else if (isAuthorizationInReview(entry))
				{
					return true;
				}
			}
		}
		return false;
	}

	protected boolean isReviewDecision(final PaymentTransactionEntryModel entry)
	{
		return PaymentTransactionType.REVIEW_DECISION.equals(entry.getType());
	}

	protected boolean isAuthorizationInReview(final PaymentTransactionEntryModel entry)
	{
		return PaymentTransactionType.AUTHORIZATION.equals(entry.getType())
				&& TransactionStatus.REVIEW.name().equals(entry.getTransactionStatus());
	}


	protected CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}

	@Required
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}


	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	protected PaymentService getPaymentService()
	{
		return paymentService;
	}

	@Required
	public void setPaymentService(final PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}
}
