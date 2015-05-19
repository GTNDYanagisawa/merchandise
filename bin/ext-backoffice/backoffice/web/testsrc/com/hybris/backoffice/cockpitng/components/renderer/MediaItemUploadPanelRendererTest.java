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
 */
package com.hybris.backoffice.cockpitng.components.renderer;

import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.core.util.CockpitProperties;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacadeStrategy;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.services.media.ObjectPreview;
import com.hybris.cockpitng.services.media.ObjectPreviewService;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.widgets.baseeditorarea.DefaultEditorAreaController;
import com.hybris.cockpitng.widgets.editorarea.renderer.EditorAreaRendererUtils;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Fileupload;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;


public class MediaItemUploadPanelRendererTest
{
	public static final Logger LOG = Logger.getLogger(MediaItemUploadPanelRendererTest.class);

	@InjectMocks
	private final MediaItemUploadPanelRenderer renderer = new MediaItemUploadPanelRenderer();
	@Mock
	private MediaModel media;
	@Mock
	private WidgetInstanceManager widgetInstanceManager;
    @Mock
    private ModelService modelService;
	@Mock
	private WidgetModel widgetModel;
	@Mock
	private CockpitProperties cockpitProperties;
	@Mock
	private MediaService mediaService;
	@Mock
	private PermissionFacadeStrategy permissionFacadeStrategy;
	@Mock
	private ObjectPreviewService objectPreviewService;
	@Mock
	private ObjectFacade objectFacade;

	private Map<String, EventListener<Event>> value;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		CockpitTestUtil.mockZkEnvironment();
		Mockito.when(media.getPk()).thenReturn(PK.fromLong(123));
		Mockito.when(media.getCreationtime()).thenReturn(new Date());
		Mockito.when(media.getModifiedtime()).thenReturn(new Date());

		final ObjectPreview objectPreview = new ObjectPreview("aaa","bbb",false);
		Mockito.when(objectPreviewService.getPreview(Mockito.anyString())).thenReturn(objectPreview);

		Mockito.when(widgetInstanceManager.getModel()).thenReturn(widgetModel);
		Mockito.when(widgetModel.getValue(DefaultEditorAreaController.MODEL_CURRENT_OBJECT, Object.class)).thenReturn(media);
		Mockito.when(cockpitProperties.getProperty("fileUpload.maxSize")).thenReturn("10");
		Mockito.when(widgetModel.getValue(EditorAreaRendererUtils.MODEL_EDITOR_AREA_AFTER_SAVE_LISTENERS_MAP, Map.class))
				.thenReturn(value = new HashMap<>());
		Mockito.when(permissionFacadeStrategy.canReadType(MediaItemUploadPanelRenderer.MEDIA_TYPE_CODE)).thenReturn(Boolean
				.TRUE);
		Mockito.when(permissionFacadeStrategy.canReadInstance(media)).thenReturn(Boolean.TRUE);
		Mockito.doAnswer(new Answer()
		{
			@Override
			public Object answer(final InvocationOnMock invocationOnMock) throws Throwable
			{
				Mockito.when(media.getMime()).thenReturn((String) invocationOnMock.getArguments()[0]);
				return null;
			}
		}).when(media).setMime(Matchers.anyString());
		Mockito.doAnswer(new Answer()
		{
			@Override
			public Object answer(final InvocationOnMock invocationOnMock) throws Throwable
			{
				Mockito.when(media.getRealFileName()).thenReturn((String) invocationOnMock.getArguments()[0]);
				return null;
			}
		}).when(media).setRealFileName(Matchers.anyString());
	}

	@Test
	public void testFileupload()
	{
		final Media zkMedia = Mockito.mock(Media.class);
		Mockito.when(zkMedia.getContentType()).thenReturn("image/jpeg");
		Mockito.when(zkMedia.getName()).thenReturn("Swinka");
		Mockito.when(zkMedia.getByteData()).thenReturn(new byte[128]);
		Mockito.when(zkMedia.isBinary()).thenReturn(Boolean.TRUE);

		// render
		final Div parent = new Div();
		renderer.render(parent, null, null, null, widgetInstanceManager);

		// upload
		final Fileupload fileupload = (Fileupload)parent.getChildren().get(0).getChildren().get(0).getChildren().get(6);
		try
		{
			// trigger upload event
			triggerUploadEvent(fileupload, zkMedia);

			// trigger AfterSaveListener
			final EventListener<Event> eventListener = value.get(MediaItemUploadPanelRenderer.MEDIA_UPLOAD);
			eventListener.onEvent(new Event(""));
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			fail(e.getMessage());
		}

		// verify
		Mockito.verify(mediaService).setStreamForMedia(media, zkMedia.getStreamData());
		Assert.assertEquals(zkMedia.getContentType(), media.getMime());
		Assert.assertEquals(zkMedia.getName(), media.getRealFileName());
		try
        {
			Mockito.verify(objectFacade).reload(media);
		}
        catch (final ObjectNotFoundException ex)
        {
			LOG.error("exception",ex);
		}
	}

	@Test
	public void testFileDownloadDisabled()
	{
		// render
		final Div parent = new Div();
		media.setURL(null);
		renderer.render(parent, null, null, null, widgetInstanceManager);
		final Button filedownload = (Button)parent.getChildren().get(0).getChildren().get(0).getChildren().get(7);
		// verify
		Assert.assertTrue(filedownload.isDisabled());
	}

	@Test
	public void testFileDownloadEnabled()
	{
		// render
		final Div parent = new Div();
		Mockito.when(media.getURL()).thenReturn("file://blackhole");
		renderer.render(parent, null, null, null, widgetInstanceManager);
		final Button filedownload = (Button)parent.getChildren().get(0).getChildren().get(0).getChildren().get(7);
		// verify
		Assert.assertFalse(filedownload.isDisabled());
	}

	protected void triggerUploadEvent(final Fileupload fileupload, final Media zkMedia) throws Exception
	{
		final Iterable<EventListener<? extends Event>> listeners = fileupload.getEventListeners(Events.ON_UPLOAD);
		for (final EventListener ev : listeners)
		{
			ev.onEvent(new UploadEvent(Events.ON_UPLOAD, fileupload, new Media[] { zkMedia }));
		}
	}
}