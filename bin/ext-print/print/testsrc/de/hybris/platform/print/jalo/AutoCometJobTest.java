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
package de.hybris.platform.print.jalo;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.print.model.AutoCometJobModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * 
 */
public class AutoCometJobTest extends ServicelayerTest
{


	private static final Logger LOG = Logger.getLogger(AutoCometJobTest.class.getName());

	@Resource
	private ModelService modelService;

	@Test
	public void testCreateAutoCometJob()
	{

		try
		{
			final ItemModel modelItem = modelService.create("AutoCometJob");

			Assert.assertTrue(modelItem instanceof AutoCometJobModel);

			modelService.save(modelItem);
			LOG.info(modelService.getSource(modelItem));

			Assert.assertTrue(((AutoCometJobModel) modelItem).getId() != null);


		}
		finally
		{
			//nothing here
		}

	}


}
