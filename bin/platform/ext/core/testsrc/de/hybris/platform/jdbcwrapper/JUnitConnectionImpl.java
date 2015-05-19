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
package de.hybris.platform.jdbcwrapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransactionRollbackException;


/**
 * For testing error flag in connection itself.
 */
public class JUnitConnectionImpl extends ConnectionImpl
{
	public static enum CommitMode
	{
		NORMAL, COMMIT_AND_ERROR, NO_COMMIT_ERROR, ROLLBACK_ERROR;
	}

	private volatile boolean forceHasError = false;
	private volatile CommitMode commitMode = CommitMode.NORMAL;

	public JUnitConnectionImpl(final HybrisDataSource ds, final Connection conn)
	{
		super(ds, conn);
	}

	public void setError(final boolean hasError)
	{
		this.forceHasError = hasError;
	}

	public void setCommitMode(final CommitMode mode)
	{
		this.commitMode = mode;
	}

	@Override
	public void commit() throws SQLException
	{
		switch (commitMode)
		{
			case COMMIT_AND_ERROR:
				super.commit();
				throw new SQLTransactionRollbackException("Transaction rolled back as requested by test mode " + commitMode);
			case NO_COMMIT_ERROR:
				throw new SQLTransactionRollbackException("Transaction rolled back as requested by test mode " + commitMode);
			case ROLLBACK_ERROR:
				super.rollback();
				throw new SQLTransactionRollbackException("Transaction rolled back as requested by test mode " + commitMode);
			case NORMAL: // fall through
			default:
				super.commit();
		}
	}

	@Override
	protected void autoRollbackOnUnsetTxBOund()
	{
		throw new IllegalStateException("JUnitConnectionImpl doesnt automatically rollback open transactions!");
	}

	@Override
	protected boolean gotError()
	{
		return forceHasError || super.gotError();
	}

	public void resetTestMode()
	{
		forceHasError = false;
		commitMode = CommitMode.NORMAL;
	}

}
