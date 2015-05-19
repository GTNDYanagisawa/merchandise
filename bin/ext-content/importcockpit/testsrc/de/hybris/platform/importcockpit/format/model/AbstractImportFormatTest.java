/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.importcockpit.format.model;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.importcockpit.format.model.impl.ImportDateFormat;
import de.hybris.platform.importcockpit.format.model.impl.ImportNumberFormat;

import java.util.Locale;

import org.junit.Test;


@UnitTest
public class AbstractImportFormatTest
{

	private static final String SAMPLE_DATE_FORMAT = "dd/MM/yyyy hh:mm:ss";
	private static final String SAMPLE_NUMBER_FORMAT = "###,###.##";

	/**
	 * Test method for {@link java.lang.Object#toString()}.
	 */
	@Test
	public void testToString()
	{
		AbstractImportFormat format = new ImportDateFormat(SAMPLE_DATE_FORMAT, Locale.US);
		assertEquals("", SAMPLE_DATE_FORMAT, format.getPattern());
		assertEquals("", SAMPLE_DATE_FORMAT, format.toString());

		format = new ImportNumberFormat(SAMPLE_NUMBER_FORMAT, SAMPLE_NUMBER_FORMAT, ',', '.', 0, 8, Locale.US);
		assertEquals("", SAMPLE_NUMBER_FORMAT, format.getPattern());
		assertEquals("", SAMPLE_NUMBER_FORMAT, format.toString());
	}

}
