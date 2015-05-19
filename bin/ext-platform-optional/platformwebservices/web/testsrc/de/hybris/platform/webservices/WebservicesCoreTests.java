/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.webservices;

import de.hybris.platform.core.dto.order.CartDTO;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphTransformer;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.BidiGraphTransformer;

import junit.framework.Assert;

import org.junit.Ignore;


@Ignore("PLA-11441")
public class WebservicesCoreTests extends AbstractWebServicesTest
{

	//@Test
	public void testUpdateModelGraph() throws Exception
	{
		super.createTestCustomers();
		final UserModel user = super.userService.getUserForUID("testCustomer1");
		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("new");
		currency.setSymbol("E");

		// save currency
		modelService.save(currency);

		// assign currency to user, save user
		user.setSessionCurrency(currency);
		modelService.save(user);

		// change currency symbol
		currency.setSymbol("X");

		// save user again
		modelService.save(user);

		// empty cache
		modelService.detachAll();

		// request user and assert currency symbol
		final UserModel user2 = userService.getUserForUID("testCustomer1");
		// and check whether symbol is correct
		Assert.assertEquals("X", user2.getSessionCurrency().getSymbol());
	}

	public void testTest()
	{
		final GraphTransformer graph = new BidiGraphTransformer(CartDTO.class);
		graph.transform(new CartDTO());
	}

	public static void main(final String[] argc)
	{
		final WebservicesCoreTests test = new WebservicesCoreTests();
		test.testTest();

	}

}
