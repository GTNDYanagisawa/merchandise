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
import de.hybris.platform.core.Registry;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author pohl
 */
@IntegrationTest
public class ConversionStrategyValidateInterceptorTest extends HybrisJUnit4TransactionalTest
{

	private static final Logger LOG = Logger.getLogger(ConversionStrategyValidateInterceptorTest.class);
	private final ModelService modelService = Registry.getApplicationContext().getBean("modelService", ModelService.class);

	@Test
	public void testInvalidConversionStrategy()
	{
		try
		{
			this.create("blablabla", "-resize 30x30");
			Assert.fail("Unknown conversion strategy was accepted.");
		}
		catch (final ModelSavingException e)
		{
			this.checkException(e);
		}
	}

	private void checkException(final ModelSavingException modelSavingException)
	{
		LOG.debug("Correctly threw exception: " + modelSavingException);
		Assert.assertTrue("checking cause: " + modelSavingException.getCause(),
				modelSavingException.getCause() instanceof MediaConversionModelValidationException);
		Assert.assertNotNull("Interceptor set.", ((InterceptorException) modelSavingException.getCause()).getInterceptor());
		Assert.assertEquals("Check interceptor.", ConversionStrategyValidateInterceptor.class,
				((InterceptorException) modelSavingException.getCause()).getInterceptor().getClass());
	}

	@Test
	public void testChangeToInvalidConversionStrategy()
	{
		final ConversionMediaFormatModel format = this.create("imageMagickMediaConversionStrategy", "-resize 30x30");
		Assert.assertNotNull("Object there.", format);
		Assert.assertEquals("imageMagickMediaConversionStrategy", format.getConversionStrategy());
		this.modelService.refresh(format);
		Assert.assertEquals("imageMagickMediaConversionStrategy", format.getConversionStrategy());

		try
		{
			format.setConversionStrategy("blablabla");
			this.modelService.save(format);
			Assert.fail("Unknown conversion strategy was accepted (change).");
		}
		catch (final ModelSavingException e)
		{
			this.checkException(e);
		}
	}

	private ConversionMediaFormatModel create(final String strategy, final String conversion)
	{
		final ConversionMediaFormatModel format = this.modelService.create(ConversionMediaFormatModel.class);
		format.setConversion(conversion);
		format.setConversionStrategy(strategy);
		format.setName("test");
		format.setQualifier("test" + System.currentTimeMillis());
		this.modelService.save(format);
		return format;
	}
}
