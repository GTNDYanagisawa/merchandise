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
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.print.comet.services.NotesArchivingService;
import de.hybris.platform.print.comet.soap.CometBinary;
import de.hybris.platform.print.jalo.PrintManager;
import de.hybris.platform.print.jalo.PrintPublication;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.tests.AbstractPrintWorkflowTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@IntegrationTest
public class DefaultNotesArchivingServiceTest extends AbstractPrintWorkflowTest
{
	private final NotesArchivingService notesArchivingService = (NotesArchivingService) Registry.getApplicationContext().getBean(
			"notesArchivingService");

	private final static String PAGE_STRING_ID = "CustomPageStringId";

	@Mock
	private CometBinary initialNotes;

	@Mock
	private CometBinary sameNotes;

	@Mock
	private CometBinary differentNotes;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPushSameNotes() throws IOException, URISyntaxException
	{
		//mocking
		final byte[] initialContent = Files.readAllBytes(Paths.get(PrintManager.class.getResource(
				"/print/test/comet/comment/07_archive_initial.xml").toURI()));

		when(initialNotes.getContentBytes()).thenReturn(initialContent);
		when(initialNotes.getFilename()).thenReturn("push_pagecomments_xml_20140305152708_" + PAGE_STRING_ID + ".xml");

		final byte[] sameContent = Files.readAllBytes(Paths.get(PrintManager.class.getResource(
				"/print/test/comet/comment/08_archive_same.xml").toURI()));

		when(sameNotes.getContentBytes()).thenReturn(sameContent);
		when(sameNotes.getFilename()).thenReturn("push_pagecomments_xml_20140305153456_" + PAGE_STRING_ID + ".xml");

		//preparations
		final PageModel page = preparePage();
		page.setId(PAGE_STRING_ID);

		getModelService().save(page);

		PrintManager.getInstance().getSessionManager()
				.setPublication((PrintPublication) getModelService().toPersistenceLayer(page.getPublication()));

		notesArchivingService.archiveNotes(initialNotes);

		getModelService().refresh(page);
		assertEquals("Initial DtpComment has not been appended", 1, page.getDtpComments().size());

		notesArchivingService.archiveNotes(initialNotes);

		getModelService().refresh(page);
		assertEquals("Initial DtpComment has been appended again?", 1, page.getDtpComments().size());

		notesArchivingService.archiveNotes(sameNotes);

		getModelService().refresh(page);
		assertEquals("Same notes have been appended?", 1, page.getDtpComments().size());

	}

	@Test
	public void testPushDifferentNotes() throws IOException, URISyntaxException
	{
		//mocking
		final byte[] initialContent = Files.readAllBytes(Paths.get(PrintManager.class.getResource(
				"/print/test/comet/comment/07_archive_initial.xml").toURI()));

		when(initialNotes.getContentBytes()).thenReturn(initialContent);
		when(initialNotes.getFilename()).thenReturn("push_pagecomments_xml_20140305152708_" + PAGE_STRING_ID + ".xml");

		final byte[] differentContent = Files.readAllBytes(Paths.get(PrintManager.class.getResource(
				"/print/test/comet/comment/09_archive_different.xml").toURI()));

		when(differentNotes.getContentBytes()).thenReturn(differentContent);
		when(differentNotes.getFilename()).thenReturn("push_pagecomments_xml_20140305153456_" + PAGE_STRING_ID + ".xml");

		//preparations
		final PageModel page = preparePage();
		page.setId(PAGE_STRING_ID);

		getModelService().save(page);

		PrintManager.getInstance().getSessionManager()
				.setPublication((PrintPublication) getModelService().toPersistenceLayer(page.getPublication()));

		notesArchivingService.archiveNotes(initialNotes);

		getModelService().refresh(page);
		assertEquals("Initial DtpComment has not been appended", 1, page.getDtpComments().size());

		notesArchivingService.archiveNotes(differentNotes);

		getModelService().refresh(page);
		assertEquals("Different DtpComment has not been appended", 2, page.getDtpComments().size());
	}
}
