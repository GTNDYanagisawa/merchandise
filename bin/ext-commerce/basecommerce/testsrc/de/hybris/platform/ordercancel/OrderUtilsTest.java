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
package de.hybris.platform.ordercancel;

import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;

import java.util.Arrays;
import java.util.Collections;

import org.fest.assertions.Assertions;
import org.junit.Test;


/**
 * Test that check all use cases of {@link OrderUtils} class.
 */
public class OrderUtilsTest
{

	/**
	 * Test method for {@link OrderUtils#hasLivingEntries(de.hybris.platform.core.model.order.AbstractOrderModel)}.
	 */
	@Test
	public void testHasLivingEntries()
	{
		final OrderModel order = new OrderModel();
		final OrderEntryModel entryLiving = new OrderEntryModel();
		entryLiving.setQuantityStatus(null);

		final OrderEntryModel entryLiving2 = new OrderEntryModel();
		entryLiving2.setQuantityStatus(OrderEntryStatus.LIVING);

		final OrderEntryModel entryDead = new OrderEntryModel();
		entryDead.setQuantityStatus(OrderEntryStatus.DEAD);


		order.setEntries(Arrays.<AbstractOrderEntryModel> asList(entryDead, entryLiving));
		Assertions.assertThat(OrderUtils.hasLivingEntries(order)).isEqualTo(true);

		order.setEntries(Arrays.<AbstractOrderEntryModel> asList(entryDead, entryLiving2));
		Assertions.assertThat(OrderUtils.hasLivingEntries(order)).isEqualTo(true);

		order.setEntries(Collections.<AbstractOrderEntryModel> singletonList(entryDead));
		Assertions.assertThat(OrderUtils.hasLivingEntries(order)).isEqualTo(false);

		order.setEntries(Collections.EMPTY_LIST);
		Assertions.assertThat(OrderUtils.hasLivingEntries(order)).isEqualTo(false);
	}
}
