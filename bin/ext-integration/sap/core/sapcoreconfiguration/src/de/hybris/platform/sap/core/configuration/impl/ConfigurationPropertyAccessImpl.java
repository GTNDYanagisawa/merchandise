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
package de.hybris.platform.sap.core.configuration.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.sap.core.configuration.ConfigurationPropertyAccess;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Default implementation of {@link ConfigurationPropertyAccess} interface.
 */
public class ConfigurationPropertyAccessImpl implements ConfigurationPropertyAccess
{

	private static final Logger LOG = Logger.getLogger(ConfigurationPropertyAccessImpl.class.getName());

	private final ItemModel configurationModel;
	private final Map<String, Method> propertyMethods = new HashMap<String, Method>();
	private final Map<String, Method> propertyAccessMethods = new HashMap<String, Method>();
	private final Map<String, Method> propertyAccessCollectionMethods = new HashMap<String, Method>();
	private final Map<String, Object> properties = new HashMap<String, Object>();
	private final Map<String, ConfigurationPropertyAccess> propertyAccesses = new HashMap<String, ConfigurationPropertyAccess>();
	private final Map<String, Collection<ConfigurationPropertyAccess>> propertyAccessCollections = new HashMap<String, Collection<ConfigurationPropertyAccess>>();

	/**
	 * Standard constructor passing the configuration model.
	 * 
	 * @param configurationModel
	 *           configuration model
	 */
	public ConfigurationPropertyAccessImpl(final ItemModel configurationModel)
	{
		this.configurationModel = configurationModel;
		if (this.configurationModel != null)
		{
			createPropertyMaps();
		}
	}

	/**
	 * Returns the current configuration model.
	 * 
	 * @return sub class instance of {@link ItemModel}
	 */
	public ItemModel getConfigurationModel()
	{
		return configurationModel;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(final String propertyName)
	{
		Object value = null;
		if (!properties.containsKey(propertyName))
		{
			final Method readMethod = propertyMethods.get(propertyName);
			if (readMethod == null)
			{
				LOG.warn("Property '" + propertyName + "' not found in model " + configurationModel + ".");
			}
			else
			{
				try
				{
					value = readMethod.invoke(configurationModel, (Object[]) null);
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					LOG.warn("Property '" + propertyName + "' not found in model " + configurationModel + ".");
				}
			}
			propertyMethods.remove(propertyName);
			properties.put(propertyName, value);
		}
		return (T) properties.get(propertyName);
	}

	@Override
	public Map<String, Object> getAllProperties()
	{
		// Ensure all values are loaded
		final String[] propertyNames = propertyMethods.keySet().toArray(new String[propertyMethods.keySet().size()]);
		for (final String propertyName : propertyNames)
		{
			getProperty(propertyName);
		}
		return properties;
	}

	@Override
	public ConfigurationPropertyAccess getPropertyAccess(final String propertyAccessName)
	{
		ConfigurationPropertyAccess value = null;
		if (!propertyAccesses.containsKey(propertyAccessName))
		{
			final Method readMethod = propertyAccessMethods.get(propertyAccessName);
			if (readMethod == null)
			{
				LOG.warn("Property '" + propertyAccessName + "' not found in model " + configurationModel + ".");
			}
			else
			{
				try
				{
					final ItemModel itemModel = (ItemModel) readMethod.invoke(configurationModel, (Object[]) null);
					if (itemModel != null)
					{
						value = new ConfigurationPropertyAccessImpl(itemModel);
					}
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					LOG.warn("Property '" + propertyAccessName + "' not found in model " + configurationModel + ".");
				}
			}
			propertyAccessMethods.remove(propertyAccessName);
			propertyAccesses.put(propertyAccessName, value);
		}
		return propertyAccesses.get(propertyAccessName);
	}

	@Override
	public Map<String, ConfigurationPropertyAccess> getAllPropertyAccesses()
	{
		// Ensure all values are loaded
		final String[] propertyAccessNames = propertyAccessMethods.keySet().toArray(
				new String[propertyAccessMethods.keySet().size()]);
		for (final String propertyAccessName : propertyAccessNames)
		{
			getPropertyAccess(propertyAccessName);
		}
		return propertyAccesses;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<ConfigurationPropertyAccess> getPropertyAccessCollection(final String propertyAccessCollectionName)
	{
		Collection<ConfigurationPropertyAccess> value = null;
		if (!propertyAccessCollections.containsKey(propertyAccessCollectionName))
		{
			final Method readMethod = propertyAccessCollectionMethods.get(propertyAccessCollectionName);
			if (readMethod == null)
			{
				LOG.warn("Property '" + propertyAccessCollectionName + "' not found in model " + configurationModel + ".");
			}
			else
			{
				try
				{
					value = new ArrayList<ConfigurationPropertyAccess>();
					final Collection<ItemModel> collection = (Collection<ItemModel>) readMethod.invoke(configurationModel,
							(Object[]) null);
					if (collection != null)
					{
						for (final ItemModel itemModel : collection)
						{
							value.add(new ConfigurationPropertyAccessImpl(itemModel));
						}
					}
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					LOG.warn("Property '" + propertyAccessCollectionName + "' not found in model " + configurationModel + ".");
				}
			}
			propertyAccessCollectionMethods.remove(propertyAccessCollectionName);
			propertyAccessCollections.put(propertyAccessCollectionName, value);
		}
		return propertyAccessCollections.get(propertyAccessCollectionName);
	}

	@Override
	public Map<String, Collection<ConfigurationPropertyAccess>> getAllPropertyAccessCollections()
	{
		// Ensure all values are loaded
		final String[] propertyAccessCollectionNames = propertyAccessCollectionMethods.keySet().toArray(
				new String[propertyAccessCollectionMethods.keySet().size()]);
		for (final String propertyAccessCollectionName : propertyAccessCollectionNames)
		{
			getPropertyAccessCollection(propertyAccessCollectionName);
		}
		return propertyAccessCollections;
	}

	/**
	 * Determines all getter methods of the configuration model.
	 */
	private void createPropertyMaps()
	{
		BeanInfo info;
		try
		{
			info = Introspector.getBeanInfo(configurationModel.getClass());
			final PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
			for (final PropertyDescriptor propertyDescriptor : propertyDescriptors)
			{
				final Method readMethod = propertyDescriptor.getReadMethod();
				final Class<?> returnType = readMethod.getReturnType();
				final String name = propertyDescriptor.getName();
				if (ItemModel.class.isAssignableFrom(returnType))
				{
					propertyAccessMethods.put(name, readMethod);
				}
				else if (Collection.class.isAssignableFrom(returnType))
				{
					final Type genericReturnType = readMethod.getGenericReturnType();
					final Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
					if (actualTypeArguments.length == 1)
					{
						final Type argType = actualTypeArguments[0];
						final Class<?> argClass = (Class<?>) argType;
						if (ItemModel.class.isAssignableFrom(argClass))
						{
							propertyAccessCollectionMethods.put(name, readMethod);
						}
					}
				}
				else
				{
					propertyMethods.put(name, readMethod);
				}
			}
		}
		catch (final IntrospectionException e)
		{
			LOG.fatal("Model '" + configurationModel.getClass() + "' could not be introspected.");
		}
	}

}
