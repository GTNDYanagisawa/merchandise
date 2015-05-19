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
package de.hybris.platform.sap.core.bol.businessobject;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.jco.exceptions.BackendCommunicationException;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.exceptions.BackendLogonException;
import de.hybris.platform.sap.core.jco.exceptions.BackendServerStartupException;
import de.hybris.platform.sap.core.jco.exceptions.BackendSystemFailureException;

import com.sap.tc.logging.Severity;


/**
 * The <code>BusinessObjectHelper</code> splits the {@link BackendException} used by the back end into some more
 * meaningful and back end independent exceptions.
 */
public abstract class BusinessObjectHelper
{

	private static final Log4JWrapper LOG = Log4JWrapper.getInstance(BusinessObjectHelper.class.getName());

	/**
	 * Splits the {@link BackendException} used by the back end into some more meaningful and back end independent
	 * exceptions.
	 * 
	 * @param ex
	 *           The backend exception to be split
	 * @throws CommunicationException
	 *            {@link CommunicationException}
	 * @throws LogonException
	 *            {@link LogonException}
	 * @throws SystemFailureException
	 *            {@link SystemFailureException}
	 * @throws ServerStartupException
	 *            {@link ServerStartupException}
	 * @throws BORuntimeException
	 *            {@link BORuntimeException}
	 */
	public static void splitException(final BackendException ex) throws CommunicationException, LogonException,
			SystemFailureException, ServerStartupException, BORuntimeException
	{

		LOG.debug(ex);

		if (ex instanceof BackendLogonException)
		{
			final String messageKey = "exception.communication";
			LOG.log(Severity.DEBUG, LogCategories.APPS_COMMON_CORE, "An exception while communicate with the backend: {0}",
					new String[]
					{ ex.getMessage() });
			final LogonException wrapperEx = new LogonException(ex.getMessage(), new Message(Message.ERROR, messageKey, null, null),
					ex);
			throw wrapperEx;
		}
		else if (ex instanceof BackendSystemFailureException)
		{
			final String messageKey = "exception.communication";
			LOG.log(Severity.DEBUG, LogCategories.APPS_COMMON_CORE, "An exception while communicate with the backend: {0}",
					new String[]
					{ ex.getMessage() });
			final SystemFailureException wrapperEx = new SystemFailureException(ex.getMessage(), new Message(Message.ERROR,
					messageKey, null, null), ex);
			throw wrapperEx;
		}
		else if (ex instanceof BackendServerStartupException)
		{
			final String messageKey = "exception.communication";
			LOG.log(Severity.DEBUG, LogCategories.APPS_COMMON_CORE, "An exception while communicate with the backend: {0}",
					new String[]
					{ ex.getMessage() });
			final ServerStartupException wrapperEx = new ServerStartupException(ex.getMessage(), new Message(Message.ERROR,
					messageKey, null, null), ex);
			throw wrapperEx;
		}
		else if (ex instanceof BackendCommunicationException)
		{
			final String messageKey = "exception.communication";
			LOG.log(Severity.DEBUG, LogCategories.APPS_COMMON_CORE, "An exception while communicate with the backend: {0}",
					new String[]
					{ ex.getMessage() });
			final CommunicationException wrapperEx = new CommunicationException(ex.getMessage(), new Message(Message.ERROR,
					messageKey, null, null), ex);
			throw wrapperEx;
		}
		else
		{
			LOG.log(Severity.DEBUG, LogCategories.APPS_COMMON_CORE, "An exception while communicate with the backend: {0}",
					new String[]
					{ ex.getMessage() });
			final BORuntimeException wrapperEx = new BORuntimeException(ex.getMessage(), ex);
			throw wrapperEx;
		}

	}

}
