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
package de.hybris.platform.yb2bacceleratorcore.actions;

import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.process.approval.actions.AbstractSimpleB2BApproveOrderDecisionAction;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.task.RetryLaterException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;


public class CreateQuoteOrderHistory extends AbstractSimpleB2BApproveOrderDecisionAction
{
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(CreateQuoteOrderHistory.class);

	private OrderHistoryService orderHistoryService;
	private UserService userService;


	@Override
	public Transition executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		OrderModel processedOrder = null;
		final Transition transition = Transition.NOK;
		try
		{
			processedOrder = process.getOrder();
			LOG.debug("Creating order history for " + processedOrder.getCode() + " and status " + processedOrder.getStatus());

			final B2BCommentModel b2bCommentModel = getLatestCommentByUser(processedOrder);

			final B2BCommentModel comment = modelService.create(B2BCommentModel.class);
			comment.setComment(b2bCommentModel.getComment());
			comment.setOwner(b2bCommentModel.getOwner());

			final OrderHistoryEntryModel entryModel = createSnapshot(processedOrder, comment);
			LOG.debug("Done creating history model with " + entryModel.toString() + "for order " + processedOrder.getCode());

			return Transition.OK;
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			this.handleError(processedOrder, e);
			return transition;
		}
	}

	protected B2BCommentModel getLatestCommentByUser(final OrderModel processedOrder)
	{

		final UserModel user = processedOrder.getUser();
		final List<B2BCommentModel> b2bComments = new ArrayList<B2BCommentModel>(processedOrder.getB2bcomments());

		Collections.sort(b2bComments, new Comparator<B2BCommentModel>()
		{
			@Override
			public int compare(final B2BCommentModel comment2, final B2BCommentModel comment1)
			{
				return (comment1.getModifiedtime().compareTo(comment2.getModifiedtime()));
			}
		});

		for (final B2BCommentModel b2bCommentModel : b2bComments)
		{
			if (b2bCommentModel.getOwner().getUid().equals(user.getUid()))
			{
				return b2bCommentModel;
			}
		}
		return null;
	}



	protected OrderHistoryEntryModel createSnapshot(final OrderModel order, final B2BCommentModel comment)
	{
		final OrderModel version = getOrderHistoryService().createHistorySnapshot(order);
		if (comment != null)
		{
			version.setB2bcomments(Collections.singleton(comment));
		}
		else
		{
			final B2BCommentModel usrComment = modelService.create(B2BCommentModel.class);
			usrComment.setOwner(order.getUser());
			usrComment.setComment("");
			version.setB2bcomments(Collections.singleton(usrComment));
		}

		getOrderHistoryService().saveHistorySnapshot(version);

		final OrderHistoryEntryModel historyEntry = modelService.create(OrderHistoryEntryModel.class);
		historyEntry.setOrder(order);
		historyEntry.setPreviousOrderVersion(version);
		historyEntry.setDescription("The status has changed for the quote");
		historyEntry.setTimestamp(new Date());
		historyEntry.setOwner(comment == null ? null : comment.getOwner());
		modelService.save(historyEntry);
		return historyEntry;
	}

	protected void handleError(final OrderModel order, final Exception exception)
	{
		if (order != null)
		{
			this.setOrderStatus(order, OrderStatus.B2B_PROCESSING_ERROR);
		}
		LOG.error(exception.getMessage(), exception);
	}

	/**
	 * @return the orderHistoryService
	 */
	public OrderHistoryService getOrderHistoryService()
	{
		return orderHistoryService;
	}

	/**
	 * @param orderHistoryService
	 *           the orderHistoryService to set
	 */
	public void setOrderHistoryService(final OrderHistoryService orderHistoryService)
	{
		this.orderHistoryService = orderHistoryService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
