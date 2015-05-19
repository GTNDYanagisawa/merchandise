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
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.print.comet.exceptions.CometValidationException;
import de.hybris.platform.print.comet.notes.definition.xml.Notes;
import de.hybris.platform.print.comet.validation.impl.CommentOnPageValidationStrategy;
import de.hybris.platform.print.jalo.PrintManager;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.tests.AbstractPrintWorkflowTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class CommentOnPageValidationStrategyTest extends AbstractPrintWorkflowTest
{
	private final CommentOnPageValidationStrategy commentOnPageValidationStrategy = (CommentOnPageValidationStrategy) Registry
			.getApplicationContext().getBean("commentOnPageValidationStrategy");

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		createDomain();
		createCommentType();
		createComponent();
	}

	@Test(expected = CometValidationException.class)
	public void testCopiedNoteFromOtherPage() throws Exception
	{
		// log in as print manager
		final UserModel printmanager = getPrintmanager();
		getUserService().setCurrentUser(printmanager);

		// create a page
		final PageModel shirts = preparePage();
		shirts.setCode("Women Shirts");
		getModelService().refresh(shirts);

		// create a job, assign metadata
		createComment(shirts, "comment", "comment description", printmanager);
		final CommentModel comment = isCommentExists("comment");
		createCommentMetadata(shirts, comment);
		getModelService().refresh(comment);

		// create a page
		final PageModel page = preparePage();
		getModelService().save(page);

		// create a job, assign metadata
		createComment(page, "job", "job description", printmanager);
		final CommentModel job = isCommentExists("job");
		createCommentMetadata(page, job);
		getModelService().refresh(job);

		final Map<String, String> replace = new HashMap<String, String>();
		replace.put("WOMEN_INTRO_COMMENT_PK", job.getPk().getLongValueAsString());
		replace.put("WOMEN_SHIRTS_COMMENT_PK", comment.getPk().getLongValueAsString());

		final ByteArrayInputStream file = prepareNotesXML("/print/test/comet/comment/03_copied_note_from_other_page.xml", replace);
		validateFile(file, page);

		fail("Should have thrown a comet exception");
	}

	private ByteArrayInputStream prepareNotesXML(final String file, final Map<String, String> replace) throws IOException,
			URISyntaxException
	{
		final byte[] encoded = Files.readAllBytes(Paths.get(PrintManager.class.getResource(file).toURI()));
		final StringBuffer buffer = new StringBuffer(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString());

		for (final Map.Entry<String, String> entry : replace.entrySet())
		{
			final int length = entry.getKey().length();
			for (int start = buffer.indexOf(entry.getKey()); start > -1; start = buffer.indexOf(entry.getKey(), start + length))
			{
				buffer.replace(start, start + length, entry.getValue());
			}
		}

		return new ByteArrayInputStream(buffer.toString().getBytes());
	}

	private void validateFile(final InputStream file, final PageModel page) throws Exception
	{
		final JAXBContext ctx = JAXBContext.newInstance(Notes.class);
		final Unmarshaller unm = ctx.createUnmarshaller();
		final Notes notes = (Notes) unm.unmarshal(file);
		commentOnPageValidationStrategy.validate(notes, page);
	}
}
