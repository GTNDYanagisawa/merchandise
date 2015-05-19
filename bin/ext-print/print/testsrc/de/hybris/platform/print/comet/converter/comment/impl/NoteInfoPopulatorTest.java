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
package de.hybris.platform.print.comet.converter.comment.impl;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.print.comet.converter.impl.CommentInfo;
import de.hybris.platform.print.comet.converter.impl.NoteInfoPopulator;
import de.hybris.platform.print.comet.notes.definition.xml.Note;
import de.hybris.platform.print.comet.notes.definition.xml.NoteType;
import de.hybris.platform.print.model.PageModel;

import java.text.ParseException;

import org.junit.Test;


@UnitTest
public class NoteInfoPopulatorTest extends AbstractConverterTest
{
	private final NoteInfoPopulator noteInfoPopulator = (NoteInfoPopulator) Registry.getApplicationContext().getBean(
			"noteInfoPopulator");

	@Test
	public void testPopulate() throws ParseException
	{
		final PageModel page = createPage();
		final CommentModel commentModel = createCommentModelForPage(page);

		final CommentInfo commentInfo = new CommentInfo();
		commentInfo.setComment(commentModel);
		commentInfo.setCommentPosition(0);
		commentInfo.setPage(page);

		final Note note = new Note();
		noteInfoPopulator.populate(commentInfo, note);

		assertEquals("comet note is not of type hint", NoteType.HINT, note.getType());
		assertEquals("page value of parentUID is not 'pageStringUniqueID'", "pageStringUniqueID", note.getParentUIDs().getPage());
		assertEquals("author of comet note is not 'Don product manager'", "Don product manager", note.getCreated().getUser());
		assertNotNull("node 'changed' is null", note.getChanged());
		assertEquals("state of comment should be 'todo'", "todo", note.getChanged().getState());
		assertEquals("responsible of comet note should be empty", "", note.getResponsible().getUser());
	}
}
