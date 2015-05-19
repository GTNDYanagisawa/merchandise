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
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import javax.annotation.Resource;

import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class PartnerListEntryImplTest extends SapordermanagmentBolSpringJunitTest
{

	@Resource(name = SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST_ENTRY)
	private PartnerListEntryImpl classUnderTest;

	@Test
	public void testHandle()
	{
		final String handle = "handle";
		classUnderTest.setHandle(handle);
		assertEquals(handle, classUnderTest.getHandle());
	}

	@Test
	public void testClone()
	{
		final String id = "X";
		final TechKey partnerGUID = new TechKey("X");

		classUnderTest.setPartnerId(id);
		classUnderTest.setPartnerTechKey(partnerGUID);

		final PartnerListEntryImpl clone = (PartnerListEntryImpl) classUnderTest.clone();

		assertEquals(id, clone.getPartnerId());
		assertEquals(partnerGUID, clone.getPartnerTechKey());

		assertTrue(clone != classUnderTest);

	}

}
