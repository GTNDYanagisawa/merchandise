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
package de.hybris.platform.mediaconversion.web.facades;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author pohl
 */
@IntegrationTest
public class DefaultDefaultOnDemandConversionFacadeTest extends HybrisJUnit4Test
{

	private static DefaultOnDemandConversionFacade imgFacade;

	@BeforeClass
	public static void setup()
	{
		DefaultDefaultOnDemandConversionFacadeTest.imgFacade = Registry.getApplicationContext().getBean(
				DefaultOnDemandConversionFacade.class);
		Assert.assertEquals("ConversionServletPath", "/convert",
				DefaultDefaultOnDemandConversionFacadeTest.imgFacade.getConversionServletPath());
		Assert.assertEquals("mediaconversion webcontext", "/mediaconversion", DefaultDefaultOnDemandConversionFacadeTest.imgFacade
				.getConfigurationService().getConfiguration().getString("mediaconversion.webroot", "/mediaconversion"));
	}

	@Test
	public void testBuildConvertUrl() throws Exception
	{
		final ConversionMediaFormatModel format = new ConversionMediaFormatModel();
		format.setQualifier("thumbnail");

		this.testBuildConvertUrl(format);

		format.setMimeType("image/jpeg");
		this.testBuildConvertUrl(format);
	}

	private void testBuildConvertUrl(final ConversionMediaFormatModel format)
	{
		// pre-checks
		Assert.assertEquals("ConversionServletPath set.", "/convert",
				DefaultDefaultOnDemandConversionFacadeTest.imgFacade.getConversionServletPath());

		final PK pk = PK.parse("1234939953093");
		final MediaContainerModel container = new MockMediaContainerModel(pk);

		final String extension = this.getExtensionByMime(format.getMimeType());

		Assert.assertEquals("standard (conversionServletpath = '/convert')", "/mediaconversion/convert/1234939953093/thumbnail"
				+ extension, DefaultDefaultOnDemandConversionFacadeTest.imgFacade.buildConvertUrl(container, format));

		final String assServletPath = DefaultDefaultOnDemandConversionFacadeTest.imgFacade.getConversionServletPath();
		try
		{
			DefaultDefaultOnDemandConversionFacadeTest.imgFacade.setConversionServletPath("/");
			Assert.assertEquals("conversionServletpath = '/'", "/mediaconversion/1234939953093/thumbnail" + extension,
					DefaultDefaultOnDemandConversionFacadeTest.imgFacade.buildConvertUrl(container, format));
			DefaultDefaultOnDemandConversionFacadeTest.imgFacade.setConversionServletPath("");
			Assert.assertEquals("conversionServletpath = ''", "/mediaconversion/1234939953093/thumbnail" + extension,
					DefaultDefaultOnDemandConversionFacadeTest.imgFacade.buildConvertUrl(container, format));
			DefaultDefaultOnDemandConversionFacadeTest.imgFacade.setConversionServletPath(null);
			Assert.assertEquals("conversionServletpath = null", "/mediaconversion/1234939953093/thumbnail" + extension,
					DefaultDefaultOnDemandConversionFacadeTest.imgFacade.buildConvertUrl(container, format));

			DefaultDefaultOnDemandConversionFacadeTest.imgFacade.setConversionServletPath("convert");
			Assert.assertEquals("conversionServletpath = 'convert'", "/mediaconversion/convert/1234939953093/thumbnail" + extension,
					DefaultDefaultOnDemandConversionFacadeTest.imgFacade.buildConvertUrl(container, format));
			DefaultDefaultOnDemandConversionFacadeTest.imgFacade.setConversionServletPath("convert/");
			Assert.assertEquals("conversionServletpath = 'convert/'",
					"/mediaconversion/convert/1234939953093/thumbnail" + extension,
					DefaultDefaultOnDemandConversionFacadeTest.imgFacade.buildConvertUrl(container, format));
		}
		finally
		{
			DefaultDefaultOnDemandConversionFacadeTest.imgFacade.setConversionServletPath(assServletPath);
		}
	}

	private String getExtensionByMime(final String mimeType)
	{
		if ("image/jpeg".equals(mimeType))
		{
			return ".jpg";
		}

		return "";
	}

	private final static class MockMediaContainerModel extends MediaContainerModel
	{
		private final PK pk;

		private MockMediaContainerModel(final PK pk)
		{
			this.pk = pk;
		}

		@Override
		public PK getPk()
		{
			return pk;
		}
	}
}
