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
package de.hybris.platform.btg.servicelayer.services.evaluator.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.platform.btg.services.impl.BTGEvaluationContext;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class BTGEvaluationContextProviderTest
{
	private SessionBTGEvaluationContextProvider provider;
	private SessionService sessionService;
	private Session currentSession;

	@Before
	public void prepare()
	{
		provider = new SessionBTGEvaluationContextProvider();
	}

	@Test
	public void shouldReturnNotNullContext()
	{
		//given
		final BTGEvaluationContext context = new BTGEvaluationContext();
		createMocks(context);

		//when
		final BTGEvaluationContext result = provider.getCurrentContext();

		//then
		verifyMocks();
		assertEquals(context, result);
	}

	@Test
	public void shouldReturnNullContext()
	{
		//given
		createMocks(null);

		//when
		final BTGEvaluationContext result = provider.getCurrentContext();

		//then
		verifyMocks();
		assertNull(result);
	}

	private void verifyMocks()
	{
		verify(sessionService);
		verify(currentSession);
	}

	private void createMocks(final BTGEvaluationContext context)
	{
		currentSession = createMock(Session.class);
		expect(currentSession.getAttribute(SessionBTGEvaluationContextProvider.BTG_CURRENT_EVALUATION_CONTEXT)).andReturn(
				context);
		sessionService = createMock(SessionService.class);
		expect(sessionService.getCurrentSession()).andReturn(currentSession);

		replay(sessionService);
		replay(currentSession);
		provider.setSessionService(sessionService);
	}
}
