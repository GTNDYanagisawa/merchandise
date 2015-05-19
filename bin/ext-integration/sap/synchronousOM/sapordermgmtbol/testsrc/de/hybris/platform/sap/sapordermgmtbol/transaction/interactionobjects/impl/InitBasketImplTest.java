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
package de.hybris.platform.sap.sapordermgmtbol.transaction.interactionobjects.impl;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerListEntry;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Before;
import org.junit.Test;



@UnitTest
@SuppressWarnings("javadoc")
public class InitBasketImplTest extends SapordermanagmentBolSpringJunitTest
{


	private InitBasketImpl classUnderTest;
	private PartnerList partnerList;

	@Override
	@Before
	public void setUp()
	{
		classUnderTest = (InitBasketImpl) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_INT_INITBASKET);
		partnerList = (PartnerList) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST);
	}

	@Test
	public void testInitPartnerlist_contact() throws Exception
	{

		classUnderTest.initPartnerList(partnerList, null, "4711");

		final PartnerListEntry contactData = partnerList.getContactData();
		assertNotNull("Contact must be set", contactData);

	}



}
