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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BusinessStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.EStatus;

import junit.framework.TestCase;


@SuppressWarnings("javadoc")
public class ERPOrderStatusMappingTest extends TestCase
{
	public ERPOrderStatusMappingTest(String name)
	{
		super(name);
	}

	public void testShippingStatus()
	{
		BusinessStatus shipStatus = null;

		// NOT_RELEVANT
		shipStatus = new ShippingStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType(' '), EStatus.getStatusType(' '));

		assertEquals(shipStatus.getStatus(), EStatus.NOT_RELEVANT);

		shipStatus = new ShippingStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('A'), EStatus.getStatusType('C'));
		assertEquals(shipStatus.getStatus(), EStatus.NOT_RELEVANT);

		// NOT_SHIPPED
		shipStatus = new ShippingStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType(' '), EStatus.getStatusType(' '));
		assertEquals(shipStatus.getStatus(), EStatus.NOT_PROCESSED);

		shipStatus = new ShippingStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('A'), EStatus.getStatusType(' '));
		assertEquals(shipStatus.getStatus(), EStatus.NOT_PROCESSED);

		shipStatus = new ShippingStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('A'), EStatus.getStatusType(' '));
		assertEquals(shipStatus.getStatus(), EStatus.NOT_PROCESSED);

		// PARTIALLY SHIPPED
		shipStatus = new ShippingStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('B'), EStatus.getStatusType(' '));
		assertEquals(shipStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		shipStatus = new ShippingStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('C'), EStatus.getStatusType(' '));
		assertEquals(shipStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		shipStatus = new ShippingStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('B'), EStatus.getStatusType(' '));
		assertEquals(shipStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		// SHIPPED
		shipStatus = new ShippingStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('C'), EStatus.getStatusType(' '));
		assertEquals(shipStatus.getStatus(), EStatus.PROCESSED);

		// other values
		/*
		 * shipStatus = new ShippingStatus(EStatus.getStatusType('t'), EStatus .getStatusType('j'),
		 * EStatus.getStatusType('o')); assertEquals(shipStatus.getStatus(), null);
		 */

	}

	public void testBillingItemStatus()
	{
		BusinessStatus billStatus = null;

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType(' '), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_RELEVANT);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType(' '), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_RELEVANT);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType(' '), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_RELEVANT);

		// NOT INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType('A'), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType('A'), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('A'), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('A'), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		// PARTIALLY_INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType('B'), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType('B'), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('B'), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('B'), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		// INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType('C'), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType('C'), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('C'), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('C'), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		// NOT INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType(' '), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType(' '), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		// NOT INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType('A'), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType('A'), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('A'), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('A'), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		// PARTIALLY_INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType('B'), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType('B'), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('B'), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('B'), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		// INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType('C'), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType('C'), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('C'), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('C'), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		// PARTIALLY_INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType(' '), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType(' '), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		// NOT INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType('A'), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType('A'), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('A'), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('A'), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		// PARTIALLY_INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType('B'), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType('B'), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('B'), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('B'), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		// INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType('C'), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType('C'), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('C'), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('C'), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		// PARTIALLY_INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType(' '), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);
		// INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType(' '), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		// NOT INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType('A'), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType('A'), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('A'), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('A'), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		// PARTIALLY_INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType('B'), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType('B'), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('B'), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('B'), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		// INVOICED
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType('C'), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType('C'), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('C'), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('C'), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		// other values
		/*
		 * billStatus = new ShippingStatus(EStatus.getStatusType('t'), EStatus .getStatusType('j'),
		 * EStatus.getStatusType('o')); assertEquals(billStatus.getStatus(), null);
		 */

		// rejected item
		billStatus = new BillingItemStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('C'), EStatus.getStatusType('C'),
				EStatus.getStatusType('C'));
		assertEquals(billStatus.getStatus(), EStatus.NOT_RELEVANT);
	}

	public void testBillingHeaderStatus()
	{
		BusinessStatus billStatus = null;

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_RELEVANT);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_RELEVANT);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType(' '),
				EStatus.getStatusType(' '), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_RELEVANT);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType('A'),
				EStatus.getStatusType(' '), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType('B'),
				EStatus.getStatusType(' '), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType(' '), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('A'), EStatus.getStatusType('C'),
				EStatus.getStatusType(' '), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType(' '),
				EStatus.getStatusType('A'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType(' '),
				EStatus.getStatusType('A'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('A'),
				EStatus.getStatusType('A'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('A'),
				EStatus.getStatusType('A'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.NOT_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('B'),
				EStatus.getStatusType('A'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('B'),
				EStatus.getStatusType('A'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('C'),
				EStatus.getStatusType('A'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('C'),
				EStatus.getStatusType('A'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType(' '),
				EStatus.getStatusType('B'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('A'),
				EStatus.getStatusType('B'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('A'),
				EStatus.getStatusType('B'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('B'),
				EStatus.getStatusType('B'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('B'),
				EStatus.getStatusType('B'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('C'),
				EStatus.getStatusType('B'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('C'),
				EStatus.getStatusType('B'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType(' '),
				EStatus.getStatusType('C'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType(' '),
				EStatus.getStatusType('C'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('A'),
				EStatus.getStatusType('C'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('A'),
				EStatus.getStatusType('C'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('B'),
				EStatus.getStatusType('C'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('B'),
				EStatus.getStatusType('C'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('B'), EStatus.getStatusType('C'),
				EStatus.getStatusType('C'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		billStatus = new BillingHeaderStatusImpl(EStatus.getStatusType('C'), EStatus.getStatusType('C'),
				EStatus.getStatusType('C'), EStatus.getStatusType(' '));
		assertEquals(billStatus.getStatus(), EStatus.PROCESSED);
	}

	public void testOverallStatus()
	{
		BusinessStatus procStatus = null;
		procStatus = new OverallStatusOrderImpl(EStatus.getStatusType('A'), new ShippingStatusImpl(EStatus.getStatusType('B'),
				EStatus.getStatusType('A'), EStatus.getStatusType(' ')), new BillingHeaderStatusImpl(EStatus.getStatusType('B'),
				EStatus.getStatusType('B'), EStatus.getStatusType('C'), EStatus.getStatusType(' ')), EStatus.getStatusType(' '));

		assertEquals(procStatus.getStatus(), EStatus.NOT_PROCESSED);

		procStatus = new OverallStatusOrderImpl(EStatus.getStatusType('C'), new ShippingStatusImpl(EStatus.getStatusType(' '),
				EStatus.getStatusType(' '), EStatus.getStatusType('C')), new BillingHeaderStatusImpl(EStatus.getStatusType('C'),
				EStatus.getStatusType('C'), EStatus.getStatusType('C'), EStatus.getStatusType(' ')), EStatus.getStatusType(' '));
		assertEquals(procStatus.getStatus(), EStatus.PROCESSED);

		procStatus = new OverallStatusOrderImpl(EStatus.getStatusType('B'), new ShippingStatusImpl(EStatus.getStatusType(' '),
				EStatus.getStatusType(' '), EStatus.getStatusType('C')), new BillingHeaderStatusImpl(EStatus.getStatusType('C'),
				EStatus.getStatusType('C'), EStatus.getStatusType('C'), EStatus.getStatusType(' ')), EStatus.getStatusType(' '));
		assertEquals(procStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		procStatus = new OverallStatusOrderImpl(EStatus.getStatusType('C'), new ShippingStatusImpl(EStatus.getStatusType(' '),
				EStatus.getStatusType(' '), EStatus.getStatusType('C')), new BillingHeaderStatusImpl(EStatus.getStatusType('C'),
				EStatus.getStatusType('C'), EStatus.getStatusType('B'), EStatus.getStatusType(' ')), EStatus.getStatusType(' '));
		assertEquals(procStatus.getStatus(), EStatus.PARTIALLY_PROCESSED);

		procStatus = new OverallStatusOrderImpl(EStatus.getStatusType('C'), new ShippingStatusImpl(EStatus.getStatusType(' '),
				EStatus.getStatusType(' '), EStatus.getStatusType('C')), new BillingHeaderStatusImpl(EStatus.getStatusType('C'),
				EStatus.getStatusType(' '), EStatus.getStatusType(' '), EStatus.getStatusType('C')), EStatus.getStatusType('C'));
		assertEquals(procStatus.getStatus(), EStatus.CANCELLED);
	}

}
