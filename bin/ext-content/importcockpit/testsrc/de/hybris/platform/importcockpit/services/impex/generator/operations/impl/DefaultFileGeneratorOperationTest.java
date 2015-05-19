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
package de.hybris.platform.importcockpit.services.impex.generator.operations.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.importcockpit.model.ImportCockpitCronJobModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultFileGeneratorOperationTest
{

	@Mock
	private ImportCockpitCronJobModel cronJob;
	private DefaultFileGeneratorOperation fgOperation;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		fgOperation = new DefaultFileGeneratorOperation();
	}

	@Test
	public void testAssembleOutputFileName()
	{
		when(cronJob.getCode()).thenReturn("theCode");
		final String fileName = fgOperation.assembleOutputFileName(cronJob);
		final Pattern pattern = Pattern.compile("import_package_theCode_\\d{2}-\\d{2}-\\d{4}-\\d{2}-\\d{2}-\\d{3}");
		final Matcher matcher = pattern.matcher(fileName);
		assertTrue("File name must match the regex", matcher.matches());
	}
}
