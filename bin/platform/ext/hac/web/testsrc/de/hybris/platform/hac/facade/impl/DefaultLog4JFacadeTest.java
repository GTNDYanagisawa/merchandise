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
package de.hybris.platform.hac.facade.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.hac.facade.Log4JFacade;

import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultLog4JFacadeTest
{
	private Log4JFacade log4JFacade;
	@Mock
	private Enumeration currentLoggers;
	@Mock
	private Logger rootLogger, otherLogger; // NOPMD

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		log4JFacade = new DefaultLog4JFacade()
		{
			@Override
			protected Enumeration getCurrentLoggers()
			{
				return currentLoggers;
			}

			@Override
			protected Logger getRootLogger()
			{
				return rootLogger;
			}
		};
	}


	/**
	 * Test method for {@link de.hybris.platform.hac.facade.impl.DefaultLog4JFacade#getLoggers()}.
	 */
	@Test
	public void shouldReturnListOfLoggersContainingRootLogger()
	{
		// given
		given(Boolean.valueOf(currentLoggers.hasMoreElements())).willReturn(Boolean.TRUE, Boolean.FALSE);
		given(currentLoggers.nextElement()).willReturn(otherLogger);

		// when
		final List<Logger> loggers = log4JFacade.getLoggers();

		// then
		assertThat(loggers).isNotEmpty().hasSize(2).containsOnly(rootLogger, otherLogger);
	}

}
