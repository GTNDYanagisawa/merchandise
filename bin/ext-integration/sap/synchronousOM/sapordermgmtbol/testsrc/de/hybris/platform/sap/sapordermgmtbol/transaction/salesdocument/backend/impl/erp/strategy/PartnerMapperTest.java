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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.impl.AddressImpl;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Test;


@SuppressWarnings("javadoc")
public class PartnerMapperTest extends SapordermanagmentBolSpringJunitTest
{

	public PartnerMapper cut = new PartnerMapper();

	@Test
	public void testBeanInitialization()
	{
		final PartnerMapper cut = (PartnerMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_MAPPER);
		assertNotNull(cut);
	}


	@Test
	public void testAddressChanged()
	{
		final Address address = new AddressImpl();
		assertFalse(cut.isAddressChanged(address));
		address.setTel1Numbr("1");
		assertTrue(cut.isAddressChanged(address));
	}

	@Test
	public void testAddressChangedNullAddress()
	{
		assertFalse(cut.isAddressChanged(null));
	}

}
