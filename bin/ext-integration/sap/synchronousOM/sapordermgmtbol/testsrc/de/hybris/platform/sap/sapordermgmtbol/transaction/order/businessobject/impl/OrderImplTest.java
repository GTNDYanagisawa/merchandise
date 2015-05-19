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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.businessobject.impl.TransactionConfigurationImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.interf.OrderBackend;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class OrderImplTest extends SapordermanagmentBolSpringJunitTest
{

	private OrderImpl classUnderTest;
	private OrderBackend mockedOrderBackend;
	private TransactionConfiguration transConf;

	@Override
	@Before
	public void setUp()
	{
		transConf = new TransactionConfigurationImpl();
		classUnderTest = (OrderImpl) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_ORDER);
		mockedOrderBackend = EasyMock.createMock(OrderBackend.class);

		classUnderTest.setBackendService(mockedOrderBackend);
		classUnderTest.setTransactionConfiguration(transConf);

	}

	private void replay()
	{
		EasyMock.replay(mockedOrderBackend);
	}

	@Override
	public void tearDown()
	{
		EasyMock.verify(mockedOrderBackend);
	}

	@Test
	public void testUpdateRequired_False() throws CommunicationException, BackendException
	{
		EasyMock.expect(mockedOrderBackend.saveInBackend(classUnderTest, true)).andReturn(true);
		replay();

		classUnderTest.setUpdateMissing(false);
		classUnderTest.saveAndCommit();

		assertFalse(classUnderTest.isUpdateMissing());
	}

	@Test
	public void testUpdateRequired_True() throws CommunicationException, BackendException
	{
		mockedOrderBackend.updateInBackend(classUnderTest, transConf, new ArrayList<TechKey>());
		EasyMock.expect(mockedOrderBackend.saveInBackend(classUnderTest, true)).andReturn(true);
		replay();

		classUnderTest.setUpdateMissing(true);
		classUnderTest.saveAndCommit();

		assertFalse(classUnderTest.isUpdateMissing());
	}

	@Test
	public void testClone()
	{
		replay();
		boolean exception = false;

		try
		{
			classUnderTest.clone();
		}
		catch (final CloneNotSupportedException e)
		{
			exception = true;
		}

		assertTrue("An exception must be thrown", exception);
	}

}
