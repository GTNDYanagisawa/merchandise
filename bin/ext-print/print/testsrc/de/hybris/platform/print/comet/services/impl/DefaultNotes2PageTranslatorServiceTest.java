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
package de.hybris.platform.print.comet.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.model.CommentMetadataModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.print.comet.notes.definition.xml.Notes;
import de.hybris.platform.print.comet.services.Notes2PageTranslatorService;
import de.hybris.platform.print.enums.PageMode;
import de.hybris.platform.print.jalo.PrintManager;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.tests.AbstractPrintWorkflowTest;
import de.hybris.platform.servicelayer.security.permissions.PermissionAssignment;
import de.hybris.platform.servicelayer.security.permissions.PermissionManagementService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.workflow.model.WorkflowActionModel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;


@IntegrationTest
public class DefaultNotes2PageTranslatorServiceTest extends AbstractPrintWorkflowTest
{
	private final TypeService typeService = (TypeService) Registry.getApplicationContext().getBean("typeService");

	private final Notes2PageTranslatorService notes2PageTranslatorService = (Notes2PageTranslatorService) Registry
			.getApplicationContext().getBean("notes2PageTranslatorService");

	private final PermissionManagementService permissionManagementService = (PermissionManagementService) Registry
			.getApplicationContext().getBean("permissionManagementService");

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		createDomain();
		createCommentType();
		createComponent();
	}

	@Test
	public void testCreateJob() throws Exception
	{
		// log in as layouter
		final UserModel layouter = getLayouter();
		getUserService().setCurrentUser(layouter);

		// allow layouter to read Page objects
		permissionManagementService.addTypePermission(typeService.getComposedTypeForClass(PageModel.class),
				new PermissionAssignment(PermissionsConstants.READ, layouter));

		// create a page
		final PageModel page = preparePage();
		getModelService().save(page);

		final List<CommentModel> previousComments = page.getComments();

		final File file = new File(PrintManager.class.getResource("/print/test/comet/comment/02_new_job.xml").toURI());
		translateFile(file, page);

		// disable errors from PagePlanningValidator
		page.setPageMode(PageMode.SEQUENCE);
		getModelService().saveAll();

		// check if amount of comments within list have increased
		getModelService().refresh(page);
		assertTrue("Comment count has not increased", previousComments.size() < page.getComments().size());

		final List<CommentModel> newComments = new ArrayList<CommentModel>(page.getComments());
		newComments.removeAll(previousComments);

		assertEquals("More than one comment have been created", 1, newComments.size());

		final CommentModel newJob = newComments.get(0);

		assertEquals("Subject does not match", "new subject", newJob.getSubject());
		assertEquals("Description does not match", "<p>new description</p>", newJob.getText());
		assertNotNull("Created comment is not a job", newJob.getWorkflow());
		assertEquals("Assigne is not the layoutergroup", "layoutergroup", newJob.getWorkflow().getPrincipalAssigned().getUid());
		assertNotNull("Created comment has no metadata", newJob.getCommentMetadata());

		final CommentMetadataModel metadata = Iterables.get(newJob.getCommentMetadata(), 0);
		assertEquals("Comment has wrong X position", 116, metadata.getX().intValue());
		assertEquals("Comment has wrong Y position", 256, metadata.getY().intValue());
	}

	@Test
	public void testUpdateDeletedNote() throws Exception
	{
		// log in as print manager
		final UserModel printmanager = getPrintmanager();
		getUserService().setCurrentUser(printmanager);

		// create a page
		final PageModel page = preparePage();
		getModelService().save(page);

		// create a job, assign metadata
		createComment(page, "comment", "comment description", printmanager);
		final CommentModel comment = isCommentExists("comment");
		createCommentMetadata(page, comment);
		getModelService().refresh(comment);

		final ByteArrayInputStream file = prepareNotesXML("/print/test/comet/comment/05_update_deleted_comment.xml",
				Collections.singletonMap("WOMEN_INTRO_COMMENT_PK", comment.getPk().getLongValueAsString()));

		// remove comment
		getModelService().remove(comment);

		if (translateFile(file, page))
		{
			fail("Should have been unsuccessful");
		}
	}

	@Test
	public void testUpdateJob() throws Exception
	{
		// log in as print manager
		final UserModel printmanager = getPrintmanager();
		getUserService().setCurrentUser(printmanager);

		// create a page
		final PageModel page = preparePage();
		getModelService().save(page);

		// create a job, assign metadata
		createComment(page, "Please enlarge this picture", "<p>It should be at least double the current size.</p>", printmanager);
		final CommentModel job = isCommentExists("Please enlarge this picture");
		createCommentMetadata(page, job);
		getModelService().refresh(job);

		getPrintCollaborationFacade().createJob(job, page, printmanager);

		final ByteArrayInputStream file = prepareNotesXML("/print/test/comet/comment/06_update_job.xml",
				Collections.singletonMap("WOMEN_INTRO_COMMENT_PK", job.getPk().getLongValueAsString()));

		// log in as layouter
		final UserModel layouter = getLayouter();
		getUserService().setCurrentUser(layouter);

		// allow printmanager to read Page objects
		permissionManagementService.addTypePermission(typeService.getComposedTypeForClass(PageModel.class),
				new PermissionAssignment(PermissionsConstants.READ, getLayoutergroup()));

		translateFile(file, page);
		getModelService().saveAll();

		assertEquals("Subject does not match", "Please enlarge this picture", job.getSubject());
		assertEquals("Description does not match", "<p>It should be at least double the current size.</p>", job.getText());
		assertNotNull("Created comment is not a job", job.getWorkflow());
		assertNotNull("Created comment has no metadata", job.getCommentMetadata());

		final CommentMetadataModel metadata = Iterables.get(job.getCommentMetadata(), 0);
		assertEquals("Comment has wrong X position", 256, metadata.getX().intValue());
		assertEquals("Comment has wrong Y position", 192, metadata.getY().intValue());

		getModelService().refresh(job.getWorkflow());
		final WorkflowActionModel currentAction = getPrintCollaborationFacade().getCurrentAction(job);
		assertEquals("Job has wrong state", "Okay", currentAction.getTemplate().getCode());
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

	private boolean translateFile(final File file, final PageModel page) throws Exception
	{
		final JAXBContext ctx = JAXBContext.newInstance(Notes.class);
		final Unmarshaller unm = ctx.createUnmarshaller();
		final Notes notes = (Notes) unm.unmarshal(file);

		boolean syncSuccess = notes2PageTranslatorService.validate(notes, page);

		if (!notes2PageTranslatorService.translate(notes, page))
		{
			syncSuccess = false;
		}

		return syncSuccess;
	}

	private boolean translateFile(final InputStream file, final PageModel page) throws Exception
	{
		final JAXBContext ctx = JAXBContext.newInstance(Notes.class);
		final Unmarshaller unm = ctx.createUnmarshaller();
		final Notes notes = (Notes) unm.unmarshal(file);

		boolean syncSuccess = notes2PageTranslatorService.validate(notes, page);

		if (!notes2PageTranslatorService.translate(notes, page))
		{
			syncSuccess = false;
		}

		return syncSuccess;
	}
}
