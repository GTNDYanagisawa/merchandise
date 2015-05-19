package com.hybris.datahub.core.rest.controllers;

import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import com.hybris.datahub.core.dto.ItemImportTaskData;
import com.hybris.datahub.core.facades.ItemImportTaskRunningFacade;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@SuppressWarnings("javadoc")
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ItemImportControllerUnitTest
{
	private static final String POOL_NAME = "Test pool";
	private static final Long PUBLICATION_ID = 1L;
	private static final String RESULT_CALLBACK_URL = "http://localhost";

	private InputStream input;

	@Mock
	private ItemImportTaskRunningFacade facade;

	@Mock
	private SessionService sessionService;

	@Mock
	private Session session;

	@InjectMocks
	private final ItemImportController resource = new ItemImportController();

	@Before
	public void setUp()
	{
		input = new ByteArrayInputStream(new byte[0]);
		Mockito.doReturn(session).when(sessionService).getCurrentSession();
	}

	@Test
	public void testScheduleImportTaskIsSuccessful() throws IOException
	{
		resource.importFromStream(POOL_NAME, PUBLICATION_ID, RESULT_CALLBACK_URL, input);

		Mockito.verify(facade).scheduleImportTask(any(ItemImportTaskData.class));
	}

	@Test
	public void testResourceClosesTheInputStream() throws IOException
	{
		final InputStream in = Mockito.mock(InputStream.class);
		Mockito.when(in.read(any(byte[].class))).thenReturn(-1);

		resource.importFromStream(POOL_NAME, PUBLICATION_ID, RESULT_CALLBACK_URL, in);

		Mockito.verify(in).close();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPoolName_NotProvided() throws IOException
	{
		resource.importFromStream(null, PUBLICATION_ID, RESULT_CALLBACK_URL, input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPublicationId_NotProvided() throws IOException
	{
		resource.importFromStream(POOL_NAME, null, RESULT_CALLBACK_URL, input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCallbackUrl_NotProvided() throws IOException
	{
		resource.importFromStream(POOL_NAME, PUBLICATION_ID, null, input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInputStream_NotProvided() throws IOException
	{
		resource.importFromStream(POOL_NAME, PUBLICATION_ID, RESULT_CALLBACK_URL, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResultFromImportFailure() throws IOException
	{
		Mockito.doThrow(new IllegalArgumentException()).when(facade).scheduleImportTask(any(ItemImportTaskData.class));
		resource.importFromStream(POOL_NAME, PUBLICATION_ID, RESULT_CALLBACK_URL, input);
	}
}
