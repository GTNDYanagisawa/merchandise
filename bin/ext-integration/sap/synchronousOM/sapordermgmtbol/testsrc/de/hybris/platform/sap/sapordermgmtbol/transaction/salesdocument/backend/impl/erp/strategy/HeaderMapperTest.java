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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl.HeaderSalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.util.Date;
import java.util.GregorianCalendar;

import org.easymock.EasyMock;
import org.junit.Test;

import com.sap.conn.jco.JCoStructure;


@SuppressWarnings("javadoc")
public class HeaderMapperTest extends SapordermanagmentBolSpringJunitTest
{

	public HeaderMapper cut = new HeaderMapper();

	@Test
	public void testBeanInitialization()
	{
		final HeaderMapper cut = (HeaderMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER_MAPPER);
		assertNotNull(cut);
	}

	@Test
	public void testBeanDependencyInjection()
	{
		final HeaderMapper cut = (HeaderMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER_MAPPER);
		assertNotNull(cut.converter);
	}



	@Test
	public void testSetValidToDateForQuotations()
	{
		final Header header = new HeaderSalesDocument();

		final Date testValidToDate = (new GregorianCalendar(2012, 5, 30)).getTime();
		final JCoStructure mockedEsHeadComV = EasyMock.createMock(JCoStructure.class);
		EasyMock.expect(mockedEsHeadComV.getDate("BNDDT")).andReturn(testValidToDate);
		EasyMock.replay(mockedEsHeadComV);

		cut.setValidToDateForQuotations(mockedEsHeadComV, header, DocumentType.QUOTATION);

		assertEquals(testValidToDate, header.getValidTo());
	}

}
