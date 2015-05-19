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
package de.hybris.platform.print.comet.strategies.comment.impl;

import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.print.comet.exceptions.CometValidationException;
import de.hybris.platform.print.comet.notes.definition.xml.Notes;
import de.hybris.platform.print.comet.validation.impl.UniquePKValidationStrategy;
import de.hybris.platform.print.jalo.PrintManager;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.tests.AbstractPrintWorkflowTest;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;


@IntegrationTest
public class UniquePKValidationStrategyTest extends AbstractPrintWorkflowTest
{
	private final UniquePKValidationStrategy uniquePKValidationStrategy = (UniquePKValidationStrategy) Registry
			.getApplicationContext().getBean("uniquePKValidationStrategy");

	@Test(expected = CometValidationException.class)
	public void testDuplicateNote() throws Exception
	{
		final PageModel page = preparePage();

		final File file = new File(PrintManager.class.getResource(
				"/print/test/comet/comment/04_duplicate_note_within_same_page.xml").toURI());
		validateFile(file, page);

		fail("Should have thrown a comet validation exception");
	}

	private void validateFile(final File file, final PageModel page) throws Exception
	{
		final JAXBContext ctx = JAXBContext.newInstance(Notes.class);
		final Unmarshaller unm = ctx.createUnmarshaller();
		final Notes notes = (Notes) unm.unmarshal(file);
		uniquePKValidationStrategy.validate(notes, page);
	}
}
