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
package de.hybris.platform.b2b.punchout.actions;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.enums.PunchOutOrderOperationAllowed;
import de.hybris.platform.b2b.punchout.services.CXMLBuilder;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.cxml.CXML;
import org.cxml.Message;
import org.cxml.PunchOutOrderMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutOrderMessageProcessingActionTest
{

	private static final String BUYER_COOKIE = "This is the buyer cookie: 1CX3L4843PPZO";

	@InjectMocks
	private final PunchOutOrderMessageProcessingAction action = new PunchOutOrderMessageProcessingAction();
	private final PunchOutOrderMessage punchOutOrder = new PunchOutOrderMessage();
	private final PunchOutSession punchOutSession = new PunchOutSession();
	@Mock
	private Converter<CartModel, PunchOutOrderMessage> cartConverter;
	@Mock
	private PunchOutSessionService punchOutSessionService;
    @Mock
	private CartModel cartModel;

	@Before
	public void prepare()
	{
        final CurrencyModel currencyModel = mock(CurrencyModel.class);
        when(currencyModel.getIsocode()).thenReturn("ISO");

        when(cartModel.getTotalPrice()).thenReturn(999.99);
        when(cartModel.getCurrency()).thenReturn(currencyModel);

		punchOutSession.setBuyerCookie(BUYER_COOKIE);
		when(cartConverter.convert(cartModel)).thenReturn(punchOutOrder);
		when(punchOutSessionService.getCurrentPunchOutSession()).thenReturn(punchOutSession);
	}

	@Test
	public void shouldCreateThePunchOutTransaction()
	{
		final CXML transaction = CXMLBuilder.newInstance().create();
		action.process(cartModel, transaction);

		//Default value is edit
		assertEquals("This implementation supports edit operation", PunchOutOrderOperationAllowed.EDIT.getCode(), punchOutOrder
				.getPunchOutOrderMessageHeader().getOperationAllowed());
		assertEquals(BUYER_COOKIE, punchOutOrder.getBuyerCookie().getContent().get(0));
		assertTrue(((Message) transaction.getHeaderOrMessageOrRequestOrResponse().get(0))
				.getPunchOutOrderMessageOrProviderDoneMessageOrSubscriptionChangeMessageOrDataAvailableMessageOrSupplierChangeMessageOrOrganizationChangeMessage()
				.contains(punchOutOrder));

        assertThat(punchOutOrder
                .getPunchOutOrderMessageHeader().getTotal().getMoney().getvalue(), is(String.valueOf(cartModel.getTotalPrice())));
        assertThat(punchOutOrder
                .getPunchOutOrderMessageHeader().getTotal().getMoney().getCurrency(), is(String.valueOf(cartModel.getCurrency().getIsocode())));
	}
}
