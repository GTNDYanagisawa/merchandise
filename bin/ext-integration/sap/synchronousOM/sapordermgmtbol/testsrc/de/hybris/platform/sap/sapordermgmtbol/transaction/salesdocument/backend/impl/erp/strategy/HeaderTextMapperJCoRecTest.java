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
import static org.junit.Assert.assertNull;

import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.JCORecTestBase;

import org.junit.Test;

import com.sap.conn.jco.JCoTable;


@SuppressWarnings("javadoc")
public class HeaderTextMapperJCoRecTest extends JCORecTestBase
{
	public HeadTextMapper classUnderTest;


	@Override
	public void setUp()
	{
		super.setUp();
		classUnderTest = (HeadTextMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER_TEXT_MAPPER);
		classUnderTest.setConfigLangIso("D");
		classUnderTest.setConfigTextId("0");
	}

	@Test
	public void testRead() throws JCoRecException
	{

		final JCoMockRepository testRepository = getJCORepository("jcoErpWecModel2DMapperTextTest");
		final JCoTable table = testRepository.getTable("ET_HEAD_TEXT_COMV");

		final Header header = (Header) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER);
		classUnderTest.read(table, header);

		final Text text = header.getText();
		assertNotNull(text);
		assertEquals("The Header Text", text.getText());
		assertEquals("H", text.getHandle());
	}

	@Test
	public void testReadWrongTextID() throws JCoRecException
	{

		final JCoMockRepository testRepository = getJCORepository("jcoErpWecModel2DMapperTextTest");
		final JCoTable table = testRepository.getTable("ET_HEAD_TEXT_COMV");

		final Header header = (Header) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER);
		classUnderTest.setConfigTextId("1000");
		header.setText(null);
		classUnderTest.read(table, header);

		assertNull(header.getText());
	}


}
