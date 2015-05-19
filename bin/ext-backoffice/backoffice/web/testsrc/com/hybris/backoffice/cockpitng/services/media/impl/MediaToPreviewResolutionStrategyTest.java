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

package com.hybris.backoffice.cockpitng.services.media.impl;

import de.hybris.platform.core.model.media.MediaModel;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

public class MediaToPreviewResolutionStrategyTest
{

	@InjectMocks
	private MediaToPreviewResolutionStrategy strategy = new MediaToPreviewResolutionStrategy();

	@Test
	public void testCanResolve()
	{
		Assertions.assertThat(strategy.canResolve("String")).isFalse();
		Assertions.assertThat(strategy.canResolve(new Object())).isFalse();
		Assertions.assertThat(strategy.canResolve(new MediaModel())).isTrue();
		Assertions.assertThat(strategy.canResolve(new MediaModel()
		{
			// class that extends Media should also be accepted
		})).isTrue();
	}

	@Test
	public void testResolvePreviewUrl()
	{
		final MediaModel media = Mockito.mock(MediaModel.class);
		Mockito.when(media.getURL2()).thenReturn("/abc");
		Assertions.assertThat(strategy.resolvePreviewUrl(media)).isNull();
	}

	@Test
	public void testResolveMimeType()
	{
		final MediaModel media = Mockito.mock(MediaModel.class);
		final String mime = String.format("application-x/%s", Long.toString(System.nanoTime(), 24));
		Mockito.when(media.getMime()).thenReturn(mime);
		Assertions.assertThat(strategy.resolveMimeType(media)).isEqualTo(mime);
	}
}