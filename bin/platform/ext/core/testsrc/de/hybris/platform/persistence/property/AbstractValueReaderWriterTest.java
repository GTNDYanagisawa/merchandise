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
package de.hybris.platform.persistence.property;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.persistence.property.JDBCValueMappings.AbstractValueReaderWriter;
import de.hybris.platform.persistence.property.JDBCValueMappings.DefaultDateTimestampValueReaderWriter;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.util.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.support.JdbcUtils;


/**
 * Test proofs fix for PLA-11109
 */
@IntegrationTest
public class AbstractValueReaderWriterTest extends HybrisJUnit4Test
{
	private static final String MODIFIED_TS_COLUMN = "modifiedTS";
	private static final int STATEMENT_INDEX_ANCHOR = 1;
	private final AbstractValueReaderWriter<java.util.Date, Timestamp> readWriter = new DefaultDateTimestampValueReaderWriter();
	private Media media;

	@Before
	public void prepareSingleTestMedia()
	{
		//create a product
		media = MediaManager.getInstance().createMedia("TestMediaForModification");
	}

	@Test
	public void testDateMillisWriteReadStability() throws SQLException
	{
		final long start = System.currentTimeMillis();
		for (int i = 0; i < 2000; i++)
		{

			testSingleReadWrite(start + i);
		}
	}

	private void testSingleReadWrite(final long timeInMs) throws SQLException
	{
		Connection connection = null;
		PreparedStatement updateStatement = null;
		PreparedStatement selectStatement = null;
		ResultSet rs = null;
		try
		{
			connection = jaloSession.getTenant().getDataSource().getConnection();

			final String tenantAwareMediaTableName = ((ComposedType) TypeManager.getInstance().getType("Media")).getTable();
			final String updateQuery = "UPDATE " + tenantAwareMediaTableName + " SET " + MODIFIED_TS_COLUMN + " = ? WHERE "
					+ Item.PK + " = " + media.getPK().toString();
			updateStatement = connection.prepareStatement(updateQuery);
			final String selectQuery = "SELECT " + MODIFIED_TS_COLUMN + " FROM " + tenantAwareMediaTableName + " WHERE " + Item.PK
					+ " = " + media.getPK().toString();
			selectStatement = connection.prepareStatement(selectQuery);

			final long notAdjustedNow = timeInMs;
			readWriter.setValue(updateStatement, STATEMENT_INDEX_ANCHOR, new Timestamp(notAdjustedNow));

			if (updateStatement.executeUpdate() == 1)
			{
				rs = selectStatement.executeQuery();
				Assert.assertNotNull(rs);
				Assert.assertTrue(rs.next());
				final long valueAfterUpdate = readWriter.getValue(rs, STATEMENT_INDEX_ANCHOR).getTime();
				Assert.assertEquals(adjustToDB(new Date(notAdjustedNow)).getTime(), valueAfterUpdate);
			}
			else
			{
				Assert.fail("No record have been update via query (" + updateQuery + ") , check criteria ");
			}
		}
		finally
		{
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(selectStatement);
			JdbcUtils.closeStatement(updateStatement);
			JdbcUtils.closeConnection(connection);
		}
	}

	private Date adjustToDB(final Date date)
	{
		if (Config.isMySQLUsed() && !Config.getBoolean("mysql.allow.fractional.seconds", false))
		{
			return new Date((date.getTime() / 1000) * 1000);
		}
		//PLA-11109
		else if (Config.isSQLServerUsed())
		{
			final long millis = date.getTime();
			long roundedmillis = 0;
			switch ((int) (millis % 10))
			{
				case 0:
				case 1:
					roundedmillis = (millis / 10 * 10);
					break;

				case 2:
				case 3:
				case 4:
					roundedmillis = (millis / 10 * 10) + 3;
					break;
				case 5:
				case 6:
				case 7:
				case 8:
					roundedmillis = (millis / 10 * 10) + 7;
					break;
				case 9:
					roundedmillis = (millis / 10 * 10) + 10;
					break;
			}
			return new java.util.Date(roundedmillis);
		}
		else
		{
			return date;
		}
	}
}
