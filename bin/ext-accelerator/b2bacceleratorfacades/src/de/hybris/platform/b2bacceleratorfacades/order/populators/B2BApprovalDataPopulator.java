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
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderHistoryEntryData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * Populates {@link de.hybris.platform.workflow.model.WorkflowActionModel} to {@link de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData}.
 */
public class B2BApprovalDataPopulator implements Populator<WorkflowActionModel, B2BOrderApprovalData>
{
	private B2BWorkflowIntegrationService b2bWorkflowIntegrationService;
	private Converter<OrderModel, OrderData> orderConverter;
	private Converter<OrderHistoryEntryModel, B2BOrderHistoryEntryData> b2bOrderHistoryEntryConverter;

	@Override
	public void populate(final WorkflowActionModel source, final B2BOrderApprovalData target)
	{
		final OrderModel orderModel = getB2bWorkflowIntegrationService().getOrderFromAction(source);
		target.setWorkflowActionModelCode(source.getCode());
		target.setB2bOrderData(getOrderConverter().convert(orderModel));

		target.setAllDecisions(new ArrayList<String>(CollectionUtils.collect(source.getDecisions(),
				new BeanToPropertyValueTransformer(WorkflowDecisionModel.QUALIFIER)
				{
					@Override
					public Object transform(final Object object)
					{
						final Object original = super.transform(object);
						if (original instanceof String)
						{
							return ((String) super.transform(object)).toUpperCase();
						}
						else
						{
							return original;
						}
					}
				})));

		if (source.getSelectedDecision() != null)
		{
			target.setSelectedDecision(source.getSelectedDecision().getName());
		}
		target.setApprovalComments(source.getComment());
		if (WorkflowActionStatus.IN_PROGRESS.equals(source.getStatus()))
		{
			target.setApprovalDecisionRequired(true);
		}

		final List<B2BOrderHistoryEntryData> orderHistoryEntriesData = Converters.convertAll(orderModel.getHistoryEntries(),
				getB2bOrderHistoryEntryConverter());
		//TODO:Add the QUOTE and MERCHANT keywords in enum as a dictionary for filtering
		target.setQuotesApprovalHistoryEntriesData(filterOrderHistoryEntriesForApprovalStage(orderHistoryEntriesData, "QUOTE"));
		target.setMerchantApprovalHistoryEntriesData(filterOrderHistoryEntriesForApprovalStage(orderHistoryEntriesData, "MERCHANT"));
		target.setOrderHistoryEntriesData(orderHistoryEntriesData);
	}

	public B2BWorkflowIntegrationService getB2bWorkflowIntegrationService()
	{
		return b2bWorkflowIntegrationService;
	}

	@Required
	public void setB2bWorkflowIntegrationService(final B2BWorkflowIntegrationService b2bWorkflowIntegrationService)
	{
		this.b2bWorkflowIntegrationService = b2bWorkflowIntegrationService;
	}

	public Converter<OrderModel, OrderData> getOrderConverter()
	{
		return orderConverter;
	}

	@Required
	public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter)
	{
		this.orderConverter = orderConverter;
	}

	public Converter<OrderHistoryEntryModel, B2BOrderHistoryEntryData> getB2bOrderHistoryEntryConverter()
	{
		return b2bOrderHistoryEntryConverter;
	}

	@Required
	public void setB2bOrderHistoryEntryConverter(
			final Converter<OrderHistoryEntryModel, B2BOrderHistoryEntryData> b2bOrderHistoryEntryConverter)
	{
		this.b2bOrderHistoryEntryConverter = b2bOrderHistoryEntryConverter;
	}


	protected List<B2BOrderHistoryEntryData> filterOrderHistoryEntriesForApprovalStage(
			final List<B2BOrderHistoryEntryData> orderHistoryEntries, final String orderApprovalStage)
	{

		final Collection<B2BOrderHistoryEntryData> outputList = CollectionUtils.select(orderHistoryEntries,
				applyApprovalStagePredicate(orderApprovalStage));
		if (outputList != null && !outputList.isEmpty())
		{
			final List<B2BOrderHistoryEntryData> selectedList = new ArrayList<B2BOrderHistoryEntryData>(outputList.size());
			selectedList.addAll(outputList);

			CollectionUtils
					.filter(orderHistoryEntries, PredicateUtils.notPredicate(applyApprovalStagePredicate(orderApprovalStage)));
			return selectedList;
		}
		return null;
	}

	protected Predicate applyApprovalStagePredicate(final String orderApprovalStage)
	{
		return new Predicate()
		{
			@Override
			public boolean evaluate(final Object object)
			{
				final B2BOrderHistoryEntryData orderHistoryEntryData = (B2BOrderHistoryEntryData) object;
				if (orderHistoryEntryData.getPreviousOrderVersionData() != null)
				{
					final String previousOrderStatus = orderHistoryEntryData.getPreviousOrderVersionData().getStatus().toString();
					return (previousOrderStatus.contains(orderApprovalStage));
				}
				return false;
			}
		};
	}
}
