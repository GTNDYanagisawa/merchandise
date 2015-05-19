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
package de.hybris.platform.cockpit.session.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.Assert;


@UnitTest
public class CelumEventHandlerTest
{
	private CelumEventHandler celumEventHandler;

	@Mock
	private ModelService modelService;
	@Mock
	private TypeService typeService;
	@Mock
	private MediaService mediaService;
	@Mock
	private MediaModel mediaModel;
	@Mock
	private MediaFormatModel mediaFormatModel;
	@Mock
	private MediaContainerModel mediaContainerModel;
	@Mock
	private TypedObject typedObject;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		celumEventHandler = new CelumEventHandler();
		celumEventHandler.setMediaService(mediaService);
		celumEventHandler.setModelService(modelService);
		celumEventHandler.setCockpitTypeService(typeService);
	}

	@Test
	public void testGetMedia()
	{
		// Given
		final Map<String, String[]> params = new HashMap();
		final String[] values =
		{ "12345" };
		params.put("cel-cont", values);
		params.put("cel-mf", values);
		given(modelService.get(any(PK.class))).willReturn(mediaContainerModel);
		given(mediaService.getFormat(anyString())).willReturn(mediaFormatModel);
		given(mediaService.getMediaByFormat(mediaContainerModel, mediaFormatModel)).willReturn(mediaModel);
		given(typeService.wrapItem(any())).willReturn(typedObject);

		// When
		final TypedObject obj = celumEventHandler.getMedia(params);

		// Then
		Assert.notNull(obj);
	}

	@Test
	public void testIllegalStateExceptionWhenNoValidMediaFormatFound()
	{
		// Given
		final Map<String, String[]> params = new HashMap();

		// When
		try
		{
			celumEventHandler.getMedia(params);
		}
		catch (final IllegalStateException e)
		{
			// OK
		}
	}

}
