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
package de.hybris.platform.mobileservices.text;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.mobileservices.enums.MobileMessageError;
import de.hybris.platform.mobileservices.enums.MobileMessageStatus;
import de.hybris.platform.mobileservices.jalo.text.MobileMessageContext;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;
import de.hybris.platform.mobileservices.text.engine.IncomingSMSMessageDTO;
import de.hybris.platform.mobileservices.text.engine.IncomingSMSMessageGateway;
import de.hybris.platform.mobileservices.text.testimpl.TestStatusRecord;
import de.hybris.platform.persistence.GenericItemEJBImpl;
import de.hybris.platform.persistence.property.PersistenceManager;
import de.hybris.platform.persistence.property.TypeInfoMap;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskEngine;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.Utilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Ignore;


/**
 * 
 */
@Ignore
public abstract class StatusRecordTestBase extends ServicelayerTest
{
	//private static final Logger LOG = Logger.getLogger(StatusRecordTestBase.class.getName());

	@Resource
	protected ModelService modelService;
	@Resource
	private IncomingSMSMessageGateway incomingSMSMessageGateway;
	/** The short url service. */
	@Resource
	private TaskService taskService;

	protected final MobileMessageStatus assertSent = MobileMessageStatus.SENT;
	protected final MobileMessageError assertNoError = null;
	protected final Boolean assertIsNotDefaultAction = Boolean.FALSE;
	protected final Boolean assertIsDefaultAction = Boolean.TRUE;

	@Before
	public void removePreviousTestConfig() throws Exception
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		importCsv("/testdata/cleanup.csv", "UTF-8");
		TestStatusRecord.reset();

		assertTaskEngineRunning();
	}

	//TODO this is for finding out why BAM-390 is actually failing. Please remove as soon as we've
	// solved that!
	protected String dumpBAM390Info(final MobileMessageContextModel msg)
	{
		final MobileMessageContext jaloItem = JaloSession.getCurrentSession().getItem(msg.getPk());

		final MobileMessageStatus slStatus = msg.getStatus();
		final de.hybris.platform.jalo.enumeration.EnumerationValue jaloStatus = jaloItem.getStatus();
		final PK statusPKDB = readStatusFromDB(jaloItem.getPK());
		final de.hybris.platform.jalo.enumeration.EnumerationValue ejbJaloStatus = (EnumerationValue) ((GenericItemEJBImpl) jaloItem
				.getImplementation()).getProperty(null, MobileMessageContext.STATUS);
		final de.hybris.platform.jalo.enumeration.EnumerationValue dbJaloStatus = (EnumerationValue) (statusPKDB == null ? null
				: JaloSession.getCurrentSession().getItem(statusPKDB));
		final boolean isNew = modelService.isNew(msg);
		final boolean isDirty = modelService.isModified(msg);
		final boolean txRunning = Transaction.current().isRunning();
		final boolean txCacheIsolationEnabled = Config.itemCacheIsolationActivated();
		final boolean itemCacheBound = jaloItem.isCacheBound();

		return "Msg(pk:" + msg.getPk() + "/" + jaloItem.getPK() + ", new=" + isNew + ", modified=" + isDirty + ", txRunning="
				+ txRunning + ", txCacheIsolation=" + txCacheIsolationEnabled + ", cacheBound=" + itemCacheBound + ", isEqual="
				+ statusIsEqual(slStatus, jaloStatus, ejbJaloStatus, dbJaloStatus)
				+ ", status(sl)=" + slStatus
				+ ", status(jalo)="
				+ jaloStatus + ", status(ejb)" + ejbJaloStatus + ", status(jdbc)" + dbJaloStatus
				+ ")";
	}

	private boolean statusIsEqual(final MobileMessageStatus slStatus, final EnumerationValue jaloStatus,
			final EnumerationValue ejbStatus, final EnumerationValue dbStatus)
	{
		if (slStatus == null && jaloStatus == null && dbStatus == null)
		{
			return true;
		}
		else
		{
			final String slCode = slStatus != null ? slStatus.getCode() : "<null>";
			final String jaloCode = jaloStatus != null ? jaloStatus.getCode() : "<null>";
			final String ejbCode = ejbStatus != null ? ejbStatus.getCode() : "<null>";
			final String dbCode = dbStatus != null ? dbStatus.getCode() : "<null>";

			return slCode.equalsIgnoreCase(jaloCode) && jaloCode.equalsIgnoreCase(ejbCode) && ejbCode.equalsIgnoreCase(dbCode);
		}
	}

	private PK readStatusFromDB(final PK msgPK)
	{
		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;

		final PersistenceManager persistenceManager = Registry.getCurrentTenantNoFallback().getPersistenceManager();
		final TypeInfoMap persistenceInfo = persistenceManager.getPersistenceInfo(MobileMessageContextModel._TYPECODE);
		final String statusCol = persistenceInfo.getInfoForProperty(MobileMessageContextModel.STATUS, false).getColumnName();
		final String pkCol = persistenceInfo.getInfoForCoreProperty(MobileMessageContextModel.PK).getColumnName();
		final String tbl = persistenceInfo.getItemTableName();

		try
		{
			con = Registry.getCurrentTenantNoFallback().getDataSource().getConnection();
			stmt = con.createStatement();
			resultSet = stmt.executeQuery("SELECT " + statusCol + " FROM " + tbl + " WHERE " + pkCol + " = " + msgPK.toString());

			if (resultSet.next())
			{
				final long column = resultSet.getLong(1);
				return resultSet.wasNull() ? null : PK.fromLong(column);
			}
			else
			{
				throw new IllegalStateException("No message record found form pk " + msgPK);
			}
		}
		catch (final SQLException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			Utilities.tryToCloseJDBC(con, stmt, resultSet);
		}

	}



	protected MobileMessageContextModel getMessage(final String pk)
	{
		final MobileMessageContextModel ctx = modelService.get(PK.parse(pk));
		modelService.refresh(ctx);
		return ctx;
	}

	protected void startMonitor()
	{
		TestStatusRecord.reset();
	}

	protected MobileMessageStatus endMonitor()
	{
		return TestStatusRecord.waitForAnyMessage(30000);
	}


	protected MobileMessageStatus blockUsingRecord(final PK pk)
	{
		return blockUsingModel(pk.toString());
	}

	protected MobileMessageStatus blockUsingRecord(final String pk)
	{
		for (int i = 0; i < 200; i++)
		{
			final MobileMessageStatus status = TestStatusRecord.query(pk);
			if (status != null
					&& (status.equals(MobileMessageStatus.SENT) || status.equals(MobileMessageStatus.DISCARDED) || status
							.equals(MobileMessageStatus.ERROR)))
			{
				return status;
			}

			try
			{
				Thread.sleep(50);
			}
			catch (final InterruptedException x) // NOPMD by willy on 14/05/10 16:45
			{
				//NO SILENTLY SWALLOWED EXCEPTION ALLOWED (if this is too radical, at least LOG the exception)!
				throw new RuntimeException(x);
			}
		}

		//Nothing found, return a value that uniquely indicates this outcome
		return null;

	}

	protected MobileMessageStatus blockUsingModel(final String pk)
	{
		return blockUsingModel(pk, 50);
	}

	protected MobileMessageStatus blockUsingModel(final String pk, final int waitTimeSeconds)
	{
		final int waitTimePerTurnMs = 50;
		final int turns = (waitTimeSeconds * 1000) / waitTimePerTurnMs;

		for (int i = 0; i < turns; i++)
		{
			final MobileMessageContextModel msg = getMessage(pk);
			final MobileMessageStatus status = msg.getStatus();
			if (status != null
					&& (status.equals(MobileMessageStatus.SENT) || status.equals(MobileMessageStatus.DISCARDED) || status
							.equals(MobileMessageStatus.ERROR)))
			{
				return status;
			}
			try
			{
				Thread.sleep(waitTimePerTurnMs);
			}
			catch (final InterruptedException x) // NOPMD by willy on 14/05/10 16:45
			{
				//NO SILENTLY SWALLOWED EXCEPTION ALLOWED (if this is too radical, at least LOG the exception)!
				throw new RuntimeException(x);
			}
		}

		//Nothing found, return a value that uniquely indicates this outcome
		return null;

	}

	protected String messageReceived(final String shortcodeCountryIsocode, final String phoneCountryIsocode,
			final String shortcode, final String phone, final String text)
	{
		final IncomingSMSMessageDTO dto = new IncomingSMSMessageDTO();

		dto.setPhoneCountryIsoCode(phoneCountryIsocode);
		dto.setPhoneNumber(phone);

		dto.setShortCodeCountryIsoCode(shortcodeCountryIsocode);
		dto.setShortcode(shortcode);

		dto.setContent(text);

		final PK pk = incomingSMSMessageGateway.messageReceived("testEngine", dto);

		return pk != null ? pk.toString() : null;
	}

	protected boolean isTextServiceIllegalStateReplyFailure(final MobileMessageContextModel message)
	{
		modelService.refresh(message);

		return MessageTestingUtilities.isTextServiceIllegalStateReplyFailure(message);
	}

	protected boolean isTextServiceIllegalStateReplyFailure(final String pk)
	{

		return isTextServiceIllegalStateReplyFailure(getMessage(pk));
	}

	private void assertTaskEngineRunning()
	{
		final TaskEngine engine = taskService.getEngine();
		// check state
		assertTrue("Task engine is running", engine.isRunning());

	}

	protected MobileMessageContextModel waitForMessageToBeProcessed(final String pk)
	{
		for (int i = 0; i < 1000; i++)
		{
			final MobileMessageContextModel msg = getMessage(pk);
			final MobileMessageStatus status = msg.getStatus();
			if (status != null
					&& (status.equals(MobileMessageStatus.SENT) || status.equals(MobileMessageStatus.DISCARDED) || status
							.equals(MobileMessageStatus.ERROR)))
			{
				return msg;
			}


			try
			{
				Thread.sleep(50);
			}
			catch (final InterruptedException x) // NOPMD by willy on 14/05/10 16:45
			{
				//NO SILENTLY SWALLOWED EXCEPTION ALLOWED (if this is too radical, at least LOG the exception)!
				throw new RuntimeException(x);
			}
		}

		//Nothing found, return a value that uniquely indicates this outcome
		return null;
	}

	/**
	 * Checks that the message successfully completes with the required status and error parameters.
	 * @param message
	 * @param expectedOutcome, the desired status, cannot be null
	 * @param expectedError, the expected error code. Null means "no error"
	 */
	protected void assertMessageSuccessfulyProcessed(final MobileMessageContextModel message,
			final MobileMessageStatus expectedStatus, final MobileMessageError expectedError, final Boolean isDefaultAction)
	{
		final String actionName = (message.getMatchedAction() != null) ? message.getMatchedAction().getCode() : "unknown";
		// Check if the reply couldn't be send because the status was not in PROCESSING
		assertFalse("We are in BAM-390 scenario", isTextServiceIllegalStateReplyFailure(message));
		assertEquals("Expected action " + actionName + " status mismatch", expectedStatus, message.getStatus());
		assertEquals("Expected action " + actionName + " error indicator mismatch", expectedError, message.getMessageError());
		assertEquals("Expected action " + actionName + " isDefaultAction mismatch",
				isDefaultAction, message.getUsingDefaultAction());
	}

	protected void assertMessageSuccessfulyProcessed(final MobileMessageContextModel message,
			final MobileMessageStatus expectedStatus)
	{
		assertMessageSuccessfulyProcessed(message, expectedStatus, null, Boolean.FALSE);
	}

	protected void assertMessageSuccessfulyProcessed(final MobileMessageContextModel message)
	{
		assertMessageSuccessfulyProcessed(message, MobileMessageStatus.SENT, null, Boolean.FALSE);
	}

}
