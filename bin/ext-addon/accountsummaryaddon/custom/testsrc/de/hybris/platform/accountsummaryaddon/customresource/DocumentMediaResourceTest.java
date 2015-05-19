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
package de.hybris.platform.accountsummaryaddon.customresource;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservices.ServiceLocator;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.platform.accountsummaryaddon.model.DocumentMediaModel;

@UnitTest
public class DocumentMediaResourceTest
{
	@Mock
	private HttpServletRequest servletRequest;
	@Mock
	private ServiceLocator serviceLocator;
	@Mock
	private ModelService modelService;
	@Mock
	private MediaService mediaService;
	private DocumentMediaModel documentMedia;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		when(serviceLocator.getModelService()).thenReturn(modelService);
		when(serviceLocator.getMediaService()).thenReturn(mediaService);
		when(servletRequest.getContentType()).thenReturn("image/jpeg");
	}

	@Test
	public void shouldGetErrorAttachFileIsEmpty()
	{
		documentMedia = new DocumentMediaModel();
		final DocumentMediaResource resource = new DocumentMediaResource()
		{
			@Override
			protected ItemModel readResourceInternal(final ItemModel modelToSet)
			{
				return documentMedia;
			}
		};
		resource.setServiceLocator(serviceLocator);
		final Response response = resource.putDocumentMedia(null, servletRequest);

		TestCase.assertEquals(400, response.getStatus());
	}

	@Test
	public void shouldSuccessAttachFileIsNotEmptyAndDocumentMediaNotExist()
	{
		final DocumentMediaResource resource = new DocumentMediaResource()
		{
			@Override
			protected ItemModel readResourceInternal(final ItemModel modelToSet)
			{
				return null;
			}
		};
		resource.setServiceLocator(serviceLocator);
		final Response response = resource.putDocumentMedia(null, servletRequest);

		TestCase.assertEquals(400, response.getStatus());
	}

	@Test
	public void shouldSuccessAttachFileIsNotEmpty()
	{
		documentMedia = new DocumentMediaModel();
		final String str = "Attach file";
		final InputStream file = new ByteArrayInputStream(str.getBytes());

		final DocumentMediaResource resource = new DocumentMediaResource()
		{
			@Override
			protected ItemModel readResourceInternal(final ItemModel modelToSet)
			{
				return documentMedia;
			}
		};
		resource.setServiceLocator(serviceLocator);

		final Response response = resource.putDocumentMedia(file, servletRequest);

		TestCase.assertEquals(200, response.getStatus());
		TestCase.assertEquals("image/jpeg", documentMedia.getMime());
		verify(mediaService, times(1)).setStreamForMedia(Mockito.isA(DocumentMediaModel.class), Mockito.isA(DataInputStream.class));
	}
}
