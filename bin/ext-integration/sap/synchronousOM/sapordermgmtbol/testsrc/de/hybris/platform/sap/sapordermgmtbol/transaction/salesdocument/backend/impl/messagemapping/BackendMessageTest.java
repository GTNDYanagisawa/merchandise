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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Test;

import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoRecord;


@SuppressWarnings("javadoc")
public class BackendMessageTest extends TestCase
{

	public static JCoRecord mockRecord(final IMocksControl mc, //
			final String beSeverity, //
			final String beClass, //
			final String beNumber, //
			final String[] args, //
			final String metaTextFieldName, final String messageText, //
			final String metaRefTechKeyName, final TechKey refTechKey)
	{

		final JCoRecord mObj = mc.createMock(JCoRecord.class);

		final JCoMetaData mMeta = mc.createMock(JCoMetaData.class);
		EasyMock.expect(mObj.getMetaData()).andReturn(mMeta).once();

		EasyMock.expect(mObj.getString(BackendMessage.FIELDS.TYPE)).andReturn(beSeverity).anyTimes();
		EasyMock.expect(mObj.getString(BackendMessage.FIELDS.CLASS)).andReturn(beClass).anyTimes();
		EasyMock.expect(mObj.getString(BackendMessage.FIELDS.NUMBER)).andReturn(beNumber).anyTimes();

		EasyMock.expect(mObj.getString(BackendMessage.FIELDS.V1)).andReturn(args[0]).anyTimes();
		EasyMock.expect(mObj.getString(BackendMessage.FIELDS.V2)).andReturn(args[1]).anyTimes();
		EasyMock.expect(mObj.getString(BackendMessage.FIELDS.V3)).andReturn(args[2]).anyTimes();
		EasyMock.expect(mObj.getString(BackendMessage.FIELDS.V4)).andReturn(args[3]).anyTimes();

		mockMovingStructField(mMeta, mObj, BackendMessage.FIELDS.TEXT, metaTextFieldName, messageText);

		mockMovingStructField(mMeta, mObj, BackendMessage.FIELDS.REF_TECH_KEY, metaRefTechKeyName, refTechKey.getIdAsString());

		return mObj;
	}

	// Works for TechKey, ect too - string is converted to the type by helper
	public static void mockMovingStructField(final JCoMetaData mMeta, final JCoRecord mObj, final String[] metaTextFieldNames,
			final String metaTextFieldName, final String value)
	{
		for (final String fName : metaTextFieldNames)
		{
			final boolean isIt = fName.equals(metaTextFieldName);
			EasyMock.expect(mMeta.hasField(fName)).andReturn(isIt).anyTimes();
			EasyMock.expect(mObj.getString(fName)).andReturn((isIt ? value : "")).anyTimes();
		}
	}

	@Test
	public void testConstructorRecord_NotEmpty() throws BackendException
	{

		final IMocksControl mc = EasyMock.createNiceControl();
		final TechKey key = TechKey.generateKey();
		final JCoRecord mockRecord = mockRecord(mc, "E", "V1", "001", new String[]
		{ "a1", "a2", "a3", "a4" }, BackendMessage.FIELDS.TEXT[0], "text", BackendMessage.FIELDS.REF_TECH_KEY[0], key);
		mc.replay();

		final BackendMessage msg = new BackendMessage(mockRecord);

		assertEquals("E", msg.getBeSeverity());
		assertEquals("V1", msg.getBeClass());
		assertEquals("001", msg.getBeNumber());
		assertEquals("a1", msg.getVars()[0]);
		assertEquals("a2", msg.getVars()[1]);
		assertEquals("a3", msg.getVars()[2]);
		assertEquals("a4", msg.getVars()[3]);
		assertEquals("text", msg.getMessageText());
		assertEquals(key, msg.getRefTechKey());

		mc.verify();
	}

	@Test
	public void testConstructorRecord_Empty() throws BackendException
	{

		final IMocksControl mc = EasyMock.createNiceControl();
		final TechKey key = TechKey.generateKey();
		final JCoRecord mockRecord = mockRecord(mc, "", "", "000", new String[]
		{ "", "", "", "" }, BackendMessage.FIELDS.TEXT[0], "", BackendMessage.FIELDS.REF_TECH_KEY[0], key);
		mc.replay();

		final BackendMessage msg = new BackendMessage(mockRecord);

		assertTrue(msg.isEmpty());

		assertEquals("", msg.getBeSeverity());
		assertEquals("", msg.getBeClass());
		assertEquals("000", msg.getBeNumber());
		assertEquals("", msg.getVars()[0]);
		assertEquals("", msg.getVars()[1]);
		assertEquals("", msg.getVars()[2]);
		assertEquals("", msg.getVars()[3]);
		assertEquals("", msg.getMessageText());
		assertEquals(key, msg.getRefTechKey());

		mc.verify();
	}


	@Test
	public void testConstructorWithParametersUsingEmptyMessage() throws BackendException
	{


		final BackendMessage msg = new BackendMessage("", "", "000", "", "", "", "");

		assertTrue(msg.isEmpty());

		assertEquals("", msg.getBeSeverity());
		assertEquals("", msg.getBeClass());
		assertEquals("000", msg.getBeNumber());
		assertEquals("", msg.getVars()[0]);
		assertEquals("", msg.getVars()[1]);
		assertEquals("", msg.getVars()[2]);
		assertEquals("", msg.getVars()[3]);


	}

	@Test
	public void testEquals() throws BackendException
	{


		BackendMessage msg1 = new BackendMessage("", "", "000", "", "", "", "");
		BackendMessage msg2 = msg1;

		assertTrue(msg1.equals(msg1));

		msg1 = new BackendMessage("", "", "000", "", "", "", "");
		final String differentObject = new String();

		assertFalse(msg1.equals(differentObject));


		msg1 = new BackendMessage("E", "ID1", "089", "", "", "", "");
		msg2 = null;

		assertFalse(msg1.equals(msg2));


		msg1 = new BackendMessage("E", "ID1", "089", "", "", "", "");
		msg2 = new BackendMessage("E", "ID1", "089", "", "", "", "");

		assertTrue(msg1.equals(msg2));

		msg1 = new BackendMessage("W", "ID1", "089", "", "", "", "");
		msg2 = new BackendMessage("E", "ID2", "090", "", "", "", "");

		assertFalse(msg1.equals(msg2));


		msg1 = new BackendMessage("E", "ID1", "089", "A1", "A2", "A3", "A4");
		msg2 = new BackendMessage("E", "ID1", "089", "A1", "A2", "A3", "A4");

		assertTrue(msg1.equals(msg2));


		msg1 = new BackendMessage("E", "ID1", "089", "A1", "A2", "A3", "A4");
		msg2 = new BackendMessage("W", "ID2", "090", "A5", "A6", "A7", "A8");

		assertFalse(msg1.equals(msg2));


	}


	@Test
	public void testHashCode() throws BackendException
	{

		BackendMessage msg1 = null;
		BackendMessage msg2 = null;

		msg1 = new BackendMessage("E", "ID1", "089", "A1", "A2", "A3", "A4");
		msg2 = new BackendMessage("E", "ID1", "089", "A1", "A2", "A3", "A4");

		assertTrue(msg1.hashCode() == msg2.hashCode());

		msg1 = new BackendMessage("E", "ID1", "089", "A1", "A2", "A3", "A4");
		msg2 = new BackendMessage("W", "ID2", "090", "A5", "A6", "A7", "A8");

		assertFalse(msg1.hashCode() == msg2.hashCode());


	}

}
