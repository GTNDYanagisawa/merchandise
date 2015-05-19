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
package de.hybris.platform.test;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.MediaUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class MediaUtilIntegrationTest extends HybrisJUnit4Test
{

	private String mediaWebrootBackup;


	@Before
	public void setUp() throws Exception
	{
		mediaWebrootBackup = Config.getParameter("mediaweb.webroot");
	}

	@After
	public void tearDown()
	{
		Config.setParameter("mediaweb.webroot", mediaWebrootBackup);
	}

	@Test
	public void shouldReturnLocalMediaWebRootUrl()
	{
		// given
		Config.setParameter("mediaweb.webroot", null);

		// when
		final String rootURL = MediaUtil.getLocalMediaWebRootUrl();

		// then
		assertThat(rootURL).isNotEmpty().isEqualTo("/medias");
	}
}
