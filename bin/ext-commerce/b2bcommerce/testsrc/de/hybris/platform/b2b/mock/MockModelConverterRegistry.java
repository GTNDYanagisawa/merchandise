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
package de.hybris.platform.b2b.mock;

import de.hybris.platform.servicelayer.internal.converter.ConverterRegistry;
import de.hybris.platform.servicelayer.internal.converter.ModelConverter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;

import org.junit.Ignore;


@Ignore
public class MockModelConverterRegistry implements ConverterRegistry
{
	@Override
	public ModelService getModelService()
	{
		return null; // To change body of implemented methods use File |
						 // Settings | File Templates.
	}

	@Override
	public ModelConverter getModelConverterByModelType(final Class<?> modelClass)
	{
		return null; // To change body of implemented methods use File |
						 // Settings | File Templates.
	}

	@Override
	public boolean hasModelConverterForModelType(final Class<?> modelClass)
	{
		return false; // To change body of implemented methods use File |
						  // Settings | File Templates.
	}

	@Override
	public ModelConverter getModelConverterBySourceType(final String key)
	{
		return null; // To change body of implemented methods use File |
						 // Settings | File Templates.
	}

	@Override
	public boolean hasModelConverterForSourceType(final String key)
	{
		return false; // To change body of implemented methods use File |
						  // Settings | File Templates.
	}

	@Override
	public String getMappedType(final Class<?> modelClass)
	{
		return null; // To change body of implemented methods use File |
						 // Settings | File Templates.
	}

	@Override
	public ModelConverter removeModelConverterBySourceType(final String type)
	{
		return null; // To change body of implemented methods use File |
						 // Settings | File Templates.
	}

	@Override
	public Collection<ModelConverter> getModelConverters()
	{
		return null; // To change body of implemented methods use File |
						 // Settings | File Templates.
	}

	@Override
	public void clearModelConverters()
	{
		// NO OP	
	}

	@Override
	public ModelConverter getModelConverterByModel(final Object model)
	{
		// NO OP
		return null;
	}

}
