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
package de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.PartnerFunctionData;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerListEntry;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class PartnerListImplTest extends SapordermanagmentBolSpringJunitTest
{

	@Resource(name = SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST)
	private final PartnerListImpl classUnderTest = new PartnerListImpl();
	@Resource(name = SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST_ENTRY)
	private final PartnerListEntryImpl entry1 = new PartnerListEntryImpl();
	@Resource(name = SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST_ENTRY)
	private final PartnerListEntryImpl entry2 = new PartnerListEntryImpl();
	private final TechKey techKey1 = new TechKey("X");

	@Override
	@Before
	public void setUp()
	{
		try
		{
			super.setUp();
		}
		catch (final Exception e)
		{
			// $JL-EXC$
		}
		entry1.setPartnerId("1");
		entry1.setPartnerTechKey(techKey1);
		entry2.setPartnerId("2");
	}

	@Test
	public void testCreatePartnerLIstEntry()
	{
		assertNotNull(classUnderTest.createPartnerListEntry());
	}

	@Test
	public void testContact()
	{

		classUnderTest.setContact(entry1);
		assertEquals("1", classUnderTest.getContact().getPartnerId());
		assertEquals("1", classUnderTest.getContactData().getPartnerId());

		classUnderTest.setContactData(entry2);
		assertEquals("2", classUnderTest.getContact().getPartnerId());
		assertEquals("2", classUnderTest.getContactData().getPartnerId());
	}

	@Test
	public void testPayer()
	{

		classUnderTest.setPayer(entry1);
		assertEquals("1", classUnderTest.getPayer().getPartnerId());
		assertEquals("1", classUnderTest.getPayerData().getPartnerId());

		classUnderTest.setPayerData(entry2);
		assertEquals("2", classUnderTest.getPayer().getPartnerId());
		assertEquals("2", classUnderTest.getPayerData().getPartnerId());
	}

	@Test
	public void testReseller()
	{

		classUnderTest.setReseller(entry1);
		assertEquals("1", classUnderTest.getReseller().getPartnerId());
		assertEquals("1", classUnderTest.getResellerData().getPartnerId());

		classUnderTest.setResellerData(entry2);
		assertEquals("2", classUnderTest.getReseller().getPartnerId());
		assertEquals("2", classUnderTest.getResellerData().getPartnerId());
	}

	@Test
	public void testSoldTo()
	{

		classUnderTest.setSoldTo(entry1);
		assertEquals("1", classUnderTest.getSoldTo().getPartnerId());
		assertEquals("1", classUnderTest.getSoldToData().getPartnerId());

		classUnderTest.setSoldToData(entry2);
		assertEquals("2", classUnderTest.getSoldTo().getPartnerId());
		assertEquals("2", classUnderTest.getSoldToData().getPartnerId());
	}

	@Test
	public void testSoldFrom()
	{

		classUnderTest.setSoldFrom(entry1);
		assertEquals("1", classUnderTest.getSoldFrom().getPartnerId());
		assertEquals("1", classUnderTest.getSoldFromData().getPartnerId());

		classUnderTest.setSoldFromData(entry2);
		assertEquals("2", classUnderTest.getSoldFrom().getPartnerId());
		assertEquals("2", classUnderTest.getSoldFromData().getPartnerId());
	}

	@Test
	public void testRemovePartner()
	{
		String partID;
		add2Partners();
		assertEquals("1", classUnderTest.getSoldTo().getPartnerId());
		assertEquals("2", classUnderTest.getContact().getPartnerId());
		classUnderTest.removePartner(PartnerFunctionData.SOLDTO);
		assertEquals("2", classUnderTest.getContact().getPartnerId());
		partID = classUnderTest.getContact().getPartnerId();
		assertEquals("2", partID);
		assertNull(classUnderTest.getSoldTo());
	}

	@Test
	public void testRemovePartnerData()
	{
		add2Partners();
		assertEquals("1", classUnderTest.getSoldToData().getPartnerId());
		assertEquals("2", classUnderTest.getContactData().getPartnerId());
		classUnderTest.removePartnerData(PartnerFunctionData.SOLDTO);
		assertEquals("2", classUnderTest.getContactData().getPartnerId());
		assertNull(classUnderTest.getSoldTo());
	}

	private void add2Partners()
	{
		classUnderTest.setSoldTo(entry1);
		classUnderTest.setContact(entry2);
	}

	@Test
	public void testRemovePartner_techKey()
	{
		add2Partners();
		assertEquals(2, classUnderTest.getList().size());

		classUnderTest.removePartner(techKey1);

		assertEquals(1, classUnderTest.getList().size());
	}

	@Test
	public void testRemovePartnerData_techKey()
	{
		add2Partners();
		assertEquals(2, classUnderTest.getList().size());

		classUnderTest.removePartnerData(techKey1);

		assertEquals(1, classUnderTest.getList().size());
	}

	@Test
	public void testSetList()
	{
		final HashMap<String, PartnerListEntry> partnerList = new HashMap<String, PartnerListEntry>();
		partnerList.put("11", entry1);
		classUnderTest.setList(partnerList);
		assertEquals(1, classUnderTest.getList().size());

	}

	@Test
	public void testIterator()
	{
		final HashMap<String, PartnerListEntry> partnerList = new HashMap<String, PartnerListEntry>();
		partnerList.put("11", entry1);
		classUnderTest.setList(partnerList);
		final Iterator<Entry<String, PartnerListEntry>> iterator = classUnderTest.iterator();
		int counter = 0;
		while (iterator.hasNext())
		{
			final PartnerListEntry partnerListEntry = iterator.next().getValue();
			assertEquals(entry1, partnerListEntry);
			counter++;
		}
		assertEquals(1, counter);
	}

	@Test
	public void testGetAllToString_empty()
	{
		assertNotNull(classUnderTest.getAllToString());
	}

	@Test
	public void testGetAllToString()
	{
		classUnderTest.setPartnerData("XXX", entry1);
		final String key1 = classUnderTest.getAllToString();

		classUnderTest.setPartnerData("YYY", entry2);
		final String key2 = classUnderTest.getAllToString();

		assertFalse(key1.equals(key2));
	}

}
