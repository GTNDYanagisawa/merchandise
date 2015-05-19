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
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.util;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.BackendMessage.FIELDS;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoTable;


@SuppressWarnings("javadoc")
public class MessageUtilTest extends TestCase
{

	public void testHasMessage()
	{

		//Test has message should return false a jco table is null
		assertFalse(MessageUtil.hasMessage("W", "ID1", "089", "A1", "A2", "A3", "A4", null));

		//Test has message should return false a jco table is empty
		JCoTable jcoTableMock = EasyMock.createNiceMock(JCoTable.class);
		EasyMock.expect(jcoTableMock.getNumRows()).andReturn(0).anyTimes();
		EasyMock.replay(jcoTableMock);

		assertFalse(MessageUtil.hasMessage("W", "ID1", "089", "A1", "A2", "A3", "A4", jcoTableMock));

		//Test message is found in Jco Table
		jcoTableMock = EasyMock.createNiceMock(JCoTable.class);
		JCoMetaData jcoMetaMock = EasyMock.createNiceMock(JCoMetaData.class);

		EasyMock.expect(jcoTableMock.getNumRows()).andReturn(1).anyTimes();
		jcoTableMock.firstRow();
		EasyMock.expectLastCall();

		EasyMock.expect(jcoTableMock.getMetaData()).andReturn(jcoMetaMock).once();
		EasyMock.expect(jcoTableMock.getString(FIELDS.TYPE)).andReturn("W").once();
		EasyMock.expect(jcoTableMock.getString(FIELDS.CLASS)).andReturn("ID1").once();
		EasyMock.expect(jcoTableMock.getString(FIELDS.NUMBER)).andReturn("089").once();
		EasyMock.expect(jcoTableMock.getString(FIELDS.V1)).andReturn("A1").once();
		EasyMock.expect(jcoTableMock.getString(FIELDS.V2)).andReturn("A2").once();
		EasyMock.expect(jcoTableMock.getString(FIELDS.V3)).andReturn("A3").once();
		EasyMock.expect(jcoTableMock.getString(FIELDS.V4)).andReturn("A4").once();


		EasyMock.expect(jcoMetaMock.hasField((String) EasyMock.anyObject())).andReturn(false);

		EasyMock.replay(jcoTableMock);
		EasyMock.replay(jcoMetaMock);

		assertTrue(MessageUtil.hasMessage("W", "ID1", "089", "A1", "A2", "A3", "A4", jcoTableMock));


		//Test message is not found in Jco Table
		jcoTableMock = EasyMock.createNiceMock(JCoTable.class);
		jcoMetaMock = EasyMock.createNiceMock(JCoMetaData.class);

		EasyMock.expect(jcoTableMock.getNumRows()).andReturn(1).anyTimes();
		jcoTableMock.firstRow();
		EasyMock.expectLastCall();

		EasyMock.expect(jcoTableMock.getMetaData()).andReturn(jcoMetaMock).once();
		EasyMock.expect(jcoTableMock.getString(FIELDS.TYPE)).andReturn("E").once();
		EasyMock.expect(jcoTableMock.getString(FIELDS.CLASS)).andReturn("ID2").once();
		EasyMock.expect(jcoTableMock.getString(FIELDS.NUMBER)).andReturn("090").once();
		EasyMock.expect(jcoTableMock.getString(FIELDS.V1)).andReturn("A5").once();
		EasyMock.expect(jcoTableMock.getString(FIELDS.V2)).andReturn("A6").once();
		EasyMock.expect(jcoTableMock.getString(FIELDS.V3)).andReturn("A7").once();
		EasyMock.expect(jcoTableMock.getString(FIELDS.V4)).andReturn("A8").once();


		EasyMock.expect(jcoMetaMock.hasField((String) EasyMock.anyObject())).andReturn(false);

		EasyMock.replay(jcoTableMock);
		EasyMock.replay(jcoMetaMock);

		assertFalse(MessageUtil.hasMessage("W", "ID1", "089", "A1", "A2", "A3", "A4", jcoTableMock));


	}

}
