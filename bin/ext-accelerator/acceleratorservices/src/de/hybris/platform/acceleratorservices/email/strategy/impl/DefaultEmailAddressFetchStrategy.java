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
package de.hybris.platform.acceleratorservices.email.strategy.impl;

import de.hybris.platform.acceleratorservices.email.dao.EmailAddressDao;
import de.hybris.platform.acceleratorservices.email.strategy.EmailAddressFetchStrategy;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.tx.DefaultTransaction;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.tx.TransactionBody;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.Utilities;

import java.sql.Connection;
import java.sql.SQLIntegrityConstraintViolationException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default strategy for fetching EmailAddressModel for given address. The implementation executes in a transaction which
 * uses fetch-create if needed-fetch approach which is important in multi threaded environment.
 * 
 * @see de.hybris.platform.acceleratorservices.model.email.EmailAddressModel
 */
public class DefaultEmailAddressFetchStrategy implements EmailAddressFetchStrategy
{
	private EmailAddressDao emailAddressDao;
	private ModelService modelService;

	/**
	 * In case of SQL Server and unique index constraint violation an exception of the type will be thrown.
	 */
	private static final String COM_MICROSOFT_SQLSERVER_JDBC_SQL_SERVER_EXCEPTION = "com.microsoft.sqlserver.jdbc.SQLServerException";

	private static final String HANA_JDBCDRIVEREXCEPTION = "com.sap.db.jdbc.exceptions.JDBCDriverException";

	private static final Logger LOG = Logger.getLogger(DefaultEmailAddressFetchStrategy.class);

	@Override
	public EmailAddressModel fetch(final String emailAddress, final String displayName)
	{
		ServicesUtil.validateParameterNotNull(emailAddress, "emailAddress must not be null");
		try
		{
			final Transaction tx = Transaction.current();
			tx.setTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
			final boolean txRollbackOnlyBefore = tx.isRollbackOnly();

			return (EmailAddressModel) tx.execute(new TransactionBody()
			{
				@Override
				public Object execute()
				{
					final JaloSession session = JaloSession.getCurrentSession();
					try
					{
						disableNestedTransactions(session.createLocalSessionContext());

						EmailAddressModel emailAddressModel = loadEmailAddressFromDatabase(emailAddress, displayName);
						if (emailAddressModel == null)
						{
							try
							{
								emailAddressModel = getModelService().create(EmailAddressModel.class);
								emailAddressModel.setEmailAddress(emailAddress);
								emailAddressModel.setDisplayName(displayName);
								getModelService().save(emailAddressModel);
							}
							catch (final ModelSavingException e)
							{
								if (isIgnorableConstraintViolationException(e))
								{
									emailAddressModel = loadEmailAddressFromDatabase(emailAddress, displayName);
									if (emailAddressModel == null)
									{
										emailAddressModel = handleMissingEmailAfterInsertConflict(e, emailAddress, displayName);
									}
									// PLA-11093
									// reset rollback-only here in case it had not been set before ( otherwise our successful retry would be lost! ) 
									if (!txRollbackOnlyBefore && tx.isRollbackOnly())
									{
										((DefaultTransaction) tx).clearRollbackOnly();
									}
								}
								else
								{
									throw new IllegalStateException(
											ModelSavingException.class.getName() + " recognized as unrecoverable.", e);
								}
							}
						}
						return emailAddressModel;
					}
					finally
					{
						session.removeLocalSessionContext();
					}
				}

			});
		}
		catch (final Exception e)
		{
			throw new SystemException("Could not find email address for email: " + emailAddress + " and name: " + displayName, e);
		}
	}

	protected EmailAddressModel loadEmailAddressFromDatabase(final String emailAddress, final String displayName)
	{
		return getEmailAddressDao().findEmailAddressByEmailAndDisplayName(emailAddress, displayName);
	}

	protected boolean isIgnorableConstraintViolationException(final Exception exception)
	{
		if (isInsertConditionException(exception))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Could not schedule task! The " + exception.getClass().getSimpleName() + " has occurred with message: '"
						+ exception.getMessage() + "'. Now attempting to run the transaction again!");
			}
			return true;
		}
		else if (exception instanceof RuntimeException)
		{
			throw (RuntimeException) exception;
		}
		else
		{
			throw new SystemException(exception);
		}
	}

	protected boolean isInsertConditionException(final Exception exception)
	{
		return Utilities.getRootCauseOfType(exception, InterceptorException.class) != null
				|| Utilities.getRootCauseOfType(exception, SQLIntegrityConstraintViolationException.class) != null
				|| isHanaConstraintViolation(exception) || isSQLServerConstraintViolation(exception);
	}

	protected boolean isSQLServerConstraintViolation(final Exception exception)
	{
		final Throwable root = Utilities.getRootCauseOfName(exception, COM_MICROSOFT_SQLSERVER_JDBC_SQL_SERVER_EXCEPTION);
		final boolean isSQLServerUniqueConstrainViolation = root != null && root.getMessage().toLowerCase().contains("unique");
		if (!isSQLServerUniqueConstrainViolation && root != null)
		{
			LOG.warn("Unexpected SQLServerException: " + exception.getMessage() + ". Unable to find 'unique' in message.");
		}
		return isSQLServerUniqueConstrainViolation;
	}

	protected boolean isHanaConstraintViolation(final Exception exception)
	{
		final Throwable root = Utilities.getRootCauseOfName(exception, HANA_JDBCDRIVEREXCEPTION);
		final boolean isHanaUniqueConstrainViolation = root != null && root.getMessage().toLowerCase().contains("check_unique");
		if (!isHanaUniqueConstrainViolation && root != null)
		{
			LOG.warn("Unexpected Hana JDBCDriverException: " + exception.getMessage()
					+ ". Unable to find 'check_unique' in message. ");
		}
		return isHanaUniqueConstrainViolation;
	}


	protected void disableNestedTransactions(final SessionContext loclCtx)
	{
		loclCtx.setAttribute(SessionContext.TRANSACTION_IN_CREATE_DISABLED, Boolean.TRUE);
		loclCtx.setAttribute(DefaultModelService.ENABLE_TRANSACTIONAL_SAVES, Boolean.FALSE);
	}

	protected EmailAddressDao getEmailAddressDao()
	{
		return emailAddressDao;
	}

	@Required
	public void setEmailAddressDao(final EmailAddressDao emailAddressDao)
	{
		this.emailAddressDao = emailAddressDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Tries to recover even if we don't find a specific email after getting a insert conflict. For now this only
	 * concerns Hana!
	 * 
	 * @param e
	 */
	protected EmailAddressModel handleMissingEmailAfterInsertConflict(final ModelSavingException e, final String emailAddress,
			final String displayName)
	{
		// XXX be aware that this is a Hana hack since getting a constrain exception duing INSERT
		// does NOT guarantee that subsequent lookups will actually find any existing row !!!
		if (Config.isHanaUsed())
		{
			for (int retries = 0; retries < 10; retries++)
			{
				try
				{
					Thread.sleep(100);
				}
				catch (final InterruptedException e1)
				{
					Thread.currentThread().interrupt(); // restore flag
					break;
				}
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Hana hack: retrying to lookup " + emailAddress + "/" + displayName + " - count = " + retries);
				}
				final EmailAddressModel addr = loadEmailAddressFromDatabase(emailAddress, displayName);
				if (addr != null)
				{
					return addr;
				}
			}
			if (LOG.isDebugEnabled())
			{
				LOG.debug(
						"Could neither create nor load email address. Special handling for Hana was re-trying for 1 second (10 times). Ignorable exception was:",
						e);
			}
			throw new IllegalStateException(
					"Could neither create nor load email address, even with special Hana handling (retrying lookup for 1 second).");
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Could neither create nor load email address. Ignorable exception was:", e);
			}
			throw new IllegalStateException("Could neither create nor load email address");
		}
	}
}
