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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Test;


@SuppressWarnings("javadoc")
public class TextImplTest extends SapordermanagmentBolSpringJunitTest
{

	private final TextImpl classUnderTest = new TextImpl();


	@Test
	public void testBeanInitialization()
	{
		final TextImpl cut = (TextImpl) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_TEXT);
		assertNotNull(cut);
	}


	@Test
	public void testEquals()
	{
		classUnderTest.setText("X");

		final TextImpl text1 = new TextImpl();
		text1.setText("X");
		final TextImpl text2 = new TextImpl();
		text2.setText("Y");

		assertTrue(classUnderTest.equals(text1));
		assertFalse(classUnderTest.equals(text2));

		assertTrue("HashCode must be the same", classUnderTest.hashCode() == text1.hashCode());
		assertFalse("HashCode must not be the same", classUnderTest.hashCode() == text2.hashCode());

	}

	@Test
	public void testTooLong()
	{

		final String string10kPlus = createLongString(10 * 1024 + 1);

		classUnderTest.setText(string10kPlus);
		final String newText = classUnderTest.getText();

		assertTrue("Should be truncated", string10kPlus.length() > newText.length());
	}

	@Test
	public void testLong()
	{

		final String string10k = createLongString(10 * 1024);

		classUnderTest.setText(string10k);
		final String newText = classUnderTest.getText();

		assertTrue("Should not be truncated", string10k.length() == newText.length());
	}

	private String createLongString(final int numberOfChars)
	{
		final StringBuilder builder = new StringBuilder();

		for (int ii = 0; ii < numberOfChars; ii++)
		{
			builder.append('X');
		}
		return builder.toString();
	}

}
