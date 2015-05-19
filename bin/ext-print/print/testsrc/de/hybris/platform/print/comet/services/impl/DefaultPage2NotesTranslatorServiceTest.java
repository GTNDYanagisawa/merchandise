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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commons.translator.RenderersFactory;
import de.hybris.platform.commons.translator.RenderersFactoryFromFile;
import de.hybris.platform.commons.translator.Translator;
import de.hybris.platform.commons.translator.TranslatorConfiguration;
import de.hybris.platform.core.Registry;
import de.hybris.platform.print.comet.converter.comment.impl.AbstractConverterTest;
import de.hybris.platform.print.comet.converter.impl.NoteCommentsPopulator;
import de.hybris.platform.print.comet.notes.definition.xml.Notes;
import de.hybris.platform.print.comet.services.Page2NotesTranslatorService;
import de.hybris.platform.print.comet.services.PrintTranslatorService;
import de.hybris.platform.print.jalo.PrintManager;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.util.translator.EscapingIndesignPrerenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultPage2NotesTranslatorServiceTest extends AbstractConverterTest
{
	// name of configuration file that should be in the same source folder as this class
	private static final String HTML_PARSERS_CONFIGURATION_FILE_NAME = "/print/translator/translator_parsers_html.xml";
	// name of configuration file that should be in the same source folder as this class
	private static final String INDESIGN_TRANSLATOR_CONFIGURATION_FILE_NAME = "/print/translator/translator_renderers_indesign.xml";
	// name of file with properties
	public final static String INDESIGN_PROPERTIES_FILENAME = "/print/translator/indesign.properties";

	private Translator translator;

	private final Page2NotesTranslatorService page2NotesTranslatorService = (Page2NotesTranslatorService) Registry
			.getApplicationContext().getBean("page2NotesTranslatorService");

	@Mock
	private PrintTranslatorService printTranslatorService;

	@Before
	public void setUp() throws IOException
	{
		MockitoAnnotations.initMocks(this);

		InputStream translatorRenderersIndesign_IS = null;
		InputStream indesignProperties_IS = null;
		InputStream translatorParsersHtml_IS = null;
		// InputStream indesignReplace_IS = null;
		try
		{
			//load configuration and initialize translator
			translatorRenderersIndesign_IS = Translator.class.getResourceAsStream(INDESIGN_TRANSLATOR_CONFIGURATION_FILE_NAME);
			indesignProperties_IS = Translator.class.getResourceAsStream(INDESIGN_PROPERTIES_FILENAME);
			final RenderersFactory renderersFactory = new RenderersFactoryFromFile(translatorRenderersIndesign_IS,
					indesignProperties_IS);
			translatorParsersHtml_IS = Translator.class.getResourceAsStream(HTML_PARSERS_CONFIGURATION_FILE_NAME);
			final TranslatorConfiguration config = new TranslatorConfiguration(translatorParsersHtml_IS, renderersFactory);
			config.addPrerenderer(new EscapingIndesignPrerenderer());
			translator = new Translator(config);
		}
		catch (final RuntimeException e)
		{
			throw e;
		}
		finally
		{
			translatorRenderersIndesign_IS.close();
			indesignProperties_IS.close();
			translatorParsersHtml_IS.close();
		}
	}

	@Test
	public void testTranslate() throws IOException, JAXBException, URISyntaxException, ParseException
	{
		// Prepare the mocked service
		when(printTranslatorService.getDefaultTranslator()).thenReturn(translator);
		when(printTranslatorService.localize2ClientLanguage("comet.indesign.addreply", new Object[0])).thenReturn("Add a reply:");

		final NoteCommentsPopulator commentsPopulator = (NoteCommentsPopulator) Registry.getApplicationContext().getBean(
				"noteCommentsPopulator");
		commentsPopulator.setPrintTranslatorService(printTranslatorService);
		// Finished mocking

		final PageModel pageModel = createPage();

		final List<CommentModel> commentModels = new ArrayList<CommentModel>();
		commentModels.add(createCommentModelForPage(pageModel));
		pageModel.setComments(commentModels);

		final File testCommentFile = new File(PrintManager.class.getResource("/print/test/comet/comment/01_comment.xml").toURI());
		final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testCommentFile), "UTF-8"));
		final StringWriter writer = new StringWriter();

		try
		{
			final JAXBContext ctx = JAXBContext.newInstance(Notes.class);
			final Marshaller marshaller = ctx.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			final Notes notes = page2NotesTranslatorService.translate(pageModel);
			marshaller.marshal(notes, writer);

			// "Add reply" tag always generates current time. We need that information
			// We don't check if indexes are set. If something is wrong, test will fail anyway
			final String addReplyDate = notes.getNote().get(0).getComments().getComment().get(1).getOn();

			// Fetch contents of xml file we are going to test
			final StringBuffer testCommentContent = new StringBuffer();

			String line;
			while ((line = reader.readLine()) != null)
			{
				testCommentContent.append(line.replace("CURRENT_TIME", addReplyDate));
				testCommentContent.append(System.lineSeparator());
			}

			assertEquals("output of marshaller and test comment differs", testCommentContent.toString(), writer.getBuffer()
					.toString());
		}
		finally
		{
			reader.close();
			writer.close();
		}
	}
}
