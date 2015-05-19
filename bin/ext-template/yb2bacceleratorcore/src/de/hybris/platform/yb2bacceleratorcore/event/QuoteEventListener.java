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
package de.hybris.platform.yb2bacceleratorcore.event;

import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public abstract class QuoteEventListener<T extends AbstractEvent> extends AbstractOrderEventListener<T>
{
	protected B2BCommentModel getLatestCommentByUser(final OrderModel processedOrder, final UserModel user)
	{
		final List<B2BCommentModel> b2bComments = new ArrayList<B2BCommentModel>(processedOrder.getB2bcomments());

		// sort in Descending order.
		Collections.sort(b2bComments, new Comparator<B2BCommentModel>()
		{
			@Override
			public int compare(final B2BCommentModel b2bCommentModel1, final B2BCommentModel b2bCommentModel2)
			{
				return (b2bCommentModel1.getModifiedtime().compareTo(b2bCommentModel2.getModifiedtime()));
			}
		});

		B2BCommentModel comment = getModelService().create(B2BCommentModel.class);
		comment.setComment("");
		comment.setOwner(user);
		comment.setCode("SalesQuote");
		comment.setCreationtime(new Date());

		for (final B2BCommentModel b2bCommentModel : b2bComments)
		{
			if (b2bCommentModel.getOwner().equals(user))
			{
				comment = b2bCommentModel;
				break;
			}
		}
		return comment;
	}

	protected OrderHistoryEntryModel createSnapshot(final OrderModel order, final B2BCommentModel comment, final UserModel user,
			final OrderStatus orderStatus)
	{
		final OrderModel snapshot = getOrderHistoryService().createHistorySnapshot(order);
		final B2BCommentModel clone = getModelService().clone(comment);
		clone.setOrder(null);
		snapshot.setB2bcomments(Collections.singleton(clone));
		snapshot.setStatus(orderStatus);
		getOrderHistoryService().saveHistorySnapshot(snapshot);

		final OrderHistoryEntryModel historyEntry = getModelService().create(OrderHistoryEntryModel.class);
		historyEntry.setOrder(order);
		historyEntry.setPreviousOrderVersion(snapshot);
		//TODO: localize + timeservice not new Date()
		historyEntry.setDescription("The status has changed for the quote");
		historyEntry.setTimestamp(new Date());
		historyEntry.setOwner(user);
		getModelService().save(historyEntry);
		return historyEntry;
	}
}
