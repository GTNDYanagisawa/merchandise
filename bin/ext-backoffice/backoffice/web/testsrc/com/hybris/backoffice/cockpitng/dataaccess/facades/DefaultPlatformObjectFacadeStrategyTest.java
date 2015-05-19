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
package com.hybris.backoffice.cockpitng.dataaccess.facades;


import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.model.ModelService;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.hybris.backoffice.cockpitng.dataaccess.facades.object.DefaultPlatformObjectFacadeStrategy;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectDeletionException;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;
import com.hybris.cockpitng.labels.LabelService;



public class DefaultPlatformObjectFacadeStrategyTest
{
	@Test
	public void testLoad() throws ObjectNotFoundException
	{
		final ModelService modelService = Mockito.mock(ModelService.class);

		final UserModel user = new UserModel();
		user.setName("Test User");

		Mockito.when(modelService.get(PK.parse("1234"))).thenReturn(user);

		final LabelService labelService = Mockito.mock(LabelService.class);
		Mockito.when(labelService.getObjectLabel(Mockito.any())).thenReturn(StringUtils.EMPTY);

		final DefaultPlatformObjectFacadeStrategy strategy = new DefaultPlatformObjectFacadeStrategy();

		strategy.setModelService(modelService);
		strategy.setLabelService(labelService);

		// Test we get the same user
		Assert.assertEquals(user, strategy.load("1234", null));

		// Test that an unknown pk will return null
		Assert.assertNull(strategy.load("9999", null));
		Assert.assertNull(strategy.load(null, null));

		try
		{
			strategy.load("", null);
			Assert.fail("load method should have thrown an exception");
		}
		catch (final ObjectNotFoundException ex) // NOPMD
		{
			// expected behavior
		}

	}

	@Test
	public void testDeleteSuccess() throws ObjectNotFoundException
	{
		final ModelService modelService = Mockito.mock(ModelService.class);
		final LabelService labelService = Mockito.mock(LabelService.class);

		final UserModel user = new UserModel();
		user.setName("Test User");

		Mockito.doNothing().when(modelService).remove(user);
		Mockito.when(labelService.getObjectLabel(Mockito.any())).thenReturn(StringUtils.EMPTY);

		final DefaultPlatformObjectFacadeStrategy strategy = new DefaultPlatformObjectFacadeStrategy();
		strategy.setModelService(modelService);
		strategy.setLabelService(labelService);
		try
		{
			strategy.delete(user, null);
		}
		catch (final ObjectDeletionException ex)
		{
			Assert.fail();
		}

		Mockito.verify(modelService).remove(user);
		// Assert.assertEquals(user, strategy.delete(user, null));

	}

	@Test(expected = ObjectDeletionException.class)
	public void testDeleteException() throws ObjectDeletionException
	{
		final ModelService modelService = Mockito.mock(ModelService.class);
		final UserModel user = new UserModel();
		user.setName("Test User");
		Mockito.doThrow(new ModelRemovalException("Cannot delete object: ", null)).when(modelService).remove(user);
		final DefaultPlatformObjectFacadeStrategy strategy = new DefaultPlatformObjectFacadeStrategy();
		strategy.setModelService(modelService);
		strategy.delete(user, null);
		Mockito.verify(modelService).remove(user);
		// Assert.assertEquals(user, strategy.delete(user, null));
	}
}
