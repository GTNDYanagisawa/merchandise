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
package de.hybris.platform.print.comet.utils.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.util.WebSessionFunctions;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.lf5.LogLevel;
import org.junit.Test;
import org.mockito.Mockito;



@UnitTest
public class DefaultCometLogToolsTest
{

	private final DefaultCometLogTools defaultCometLogTools = (DefaultCometLogTools) Registry.getApplicationContext().getBean(
			"defaultCometLogTools");

	private static final String REQUEST_IP = "127.1.2.3";
	private static final String REQUEST_METHOD = "myPOST";
	private static final String REQUEST_USER = "myTestUserId";
	private static final String REQUEST_PAGE = "myTestPage";
	private static final String REQUEST_MESSAGE_PROPERTY = "comet.useraction.pushnotes.start";

	/*
	 * Test if IP, user id, and request method are logged with log4j
	 */
	@Test
	public void testLogUserAction()
	{

		final UserModel userModel = new UserModel();
		userModel.setUid(REQUEST_USER);

		final PageModel pageModel = new PageModel();
		pageModel.setCode(REQUEST_PAGE);

		//preparing fake request
		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getRemoteAddr()).thenReturn(REQUEST_IP);
		when(request.getMethod()).thenReturn(REQUEST_METHOD);
		WebSessionFunctions.setCurrentHttpServletRequest(request);


		//Setting default locale to make sure this tests runs on every system 
		Locale.setDefault(Locale.ENGLISH);

		final Logger logger = Logger.getLogger(DefaultCometLogTools.class.getName());

		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final WriterAppender writeappender = new WriterAppender(new SimpleLayout(), os);
		logger.addAppender(writeappender);

		defaultCometLogTools.logUserAction(userModel, pageModel.getCode(), REQUEST_MESSAGE_PROPERTY, LogLevel.INFO);

		final String logMessage = os.toString();

		assertThat(logMessage).contains(REQUEST_IP);
		assertThat(logMessage).contains(REQUEST_METHOD);
		assertThat(logMessage).contains(REQUEST_USER);
	}
}
