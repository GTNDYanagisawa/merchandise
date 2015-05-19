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
package de.hybris.platform.promotions.jalo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


/**
 * PromotionsTest.
 */
public class PromotionsTest extends HybrisJUnit4TransactionalTest
{
	private Cart cart;

	@Before
	public void setUp()
	{
		cart = jaloSession.getCart();
	}

	@Test
	public void testCart()
	{
		assertNotNull(cart);
		//cart.addNewEntry()
	}

	@Test
	public void testMaximum()
	{

		final User u = jaloSession.getUser();
		final List items = new ArrayList(3005);
		for (int i = 0; i < 3005; i++)
		{
			items.add(u);
		}
		final Map params = new HashMap();
		String query = "SELECT {PK} FROM {User} WHERE ";


		if (!Config.isOracleUsed())
		{
			query += " {PK} IN ( ?coll )";
			params.put("coll", items);
		}
		else
		{
			int pages = 0;
			for (int i = 0; i < items.size(); i += 1000)
			{
				params.put("coll_" + pages, items.subList(i, Math.min(i + 1000, items.size())));
				pages++;
			}

			for (int i = 0; i < pages; i++)
			{
				if (i > 0)
				{
					query += " OR ";
				}
				query += ("{PK} IN ( ?coll_" + i + " )");
			}
		}

		final List<User> res = FlexibleSearch.getInstance().search(//
				query, //
				params,//
				User.class).getResult();
		assertEquals(Collections.singletonList(u), res);

	}
}