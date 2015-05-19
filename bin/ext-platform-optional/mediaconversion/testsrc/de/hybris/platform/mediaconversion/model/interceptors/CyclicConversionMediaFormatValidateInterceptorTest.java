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
package de.hybris.platform.mediaconversion.model.interceptors;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;


/**
 * @author pohl
 * 
 */
@IntegrationTest
public class CyclicConversionMediaFormatValidateInterceptorTest extends ServicelayerBaseTest
{

	@Resource
	private ModelService modelService;

	private void failOnSave(final ConversionMediaFormatModel format1)
	{
		try
		{
			this.modelService.save(format1);
			Assert.fail("Conversion format cycle was accepted.");
		}
		catch (final ModelSavingException e)
		{
			// correctly caught exception
			Assert.assertNotNull("Cause set.", e.getCause());
			Assert.assertEquals("Cause is a MediaConversionModelValidationException.",
					MediaConversionModelValidationException.class, e.getCause().getClass());
		}
	}

	@Test
	public void testDirectCycle()
	{
		final ConversionMediaFormatModel format1 = this.modelService.create(ConversionMediaFormatModel.class);
		format1.setQualifier("1.format1");
		format1.setConversionStrategy("imageMagickMediaConversionStrategy");
		format1.setInputFormat(format1);

		this.failOnSave(format1);
	}

	@Test
	public void testIndirectCycle()
	{
		final ConversionMediaFormatModel format1 = this.modelService.create(ConversionMediaFormatModel.class);
		format1.setQualifier("2.format1");
		format1.setConversionStrategy("imageMagickMediaConversionStrategy");
		format1.setInputFormat(null);
		this.modelService.save(format1);

		final ConversionMediaFormatModel format2 = this.modelService.create(ConversionMediaFormatModel.class);
		format2.setQualifier("2.format2");
		format2.setConversionStrategy("imageMagickMediaConversionStrategy");
		format2.setInputFormat(format1);
		this.modelService.save(format2);

		format1.setInputFormat(format2);
		this.failOnSave(format1);
	}

	@Test
	public void testLongCycle()
	{
		final ConversionMediaFormatModel format1 = this.modelService.create(ConversionMediaFormatModel.class);
		format1.setQualifier("3.format1");
		format1.setConversionStrategy("imageMagickMediaConversionStrategy");
		format1.setInputFormat(null);
		this.modelService.save(format1);

		final ConversionMediaFormatModel format2 = this.modelService.create(ConversionMediaFormatModel.class);
		format2.setQualifier("3.format2");
		format2.setConversionStrategy("imageMagickMediaConversionStrategy");
		format2.setInputFormat(format1);
		this.modelService.save(format2);

		final ConversionMediaFormatModel format3 = this.modelService.create(ConversionMediaFormatModel.class);
		format3.setQualifier("3.format3");
		format3.setConversionStrategy("imageMagickMediaConversionStrategy");
		format3.setInputFormat(format2);
		this.modelService.save(format3);

		final ConversionMediaFormatModel format4 = this.modelService.create(ConversionMediaFormatModel.class);
		format4.setQualifier("3.format4");
		format4.setConversionStrategy("imageMagickMediaConversionStrategy");
		format4.setInputFormat(format3);
		this.modelService.save(format4);

		format1.setInputFormat(format4);
		this.failOnSave(format1);
	}
}
