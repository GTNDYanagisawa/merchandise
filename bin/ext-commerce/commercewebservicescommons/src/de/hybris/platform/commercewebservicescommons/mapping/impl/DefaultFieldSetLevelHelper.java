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
package de.hybris.platform.commercewebservicescommons.mapping.impl;

import de.hybris.platform.commercewebservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.commercewebservicescommons.mapping.config.FieldSetLevelMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Default implementation of {@link de.hybris.platform.commercewebservicescommons.mapping.FieldSetLevelHelper}
 */
public class DefaultFieldSetLevelHelper implements FieldSetLevelHelper, ApplicationContextAware
{
	/**
	 * Definitions of field levels for each class
	 */
	private Map<Class, Map<String, String>> levelMap;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
	{
		buildLevelMap(applicationContext);
	}

	/**
	 * Builds the level map based on all the managed beans of type
	 * {@link de.hybris.platform.commercewebservicescommons.mapping.config.FieldSetLevelMapping}.
	 * 
	 * @param applicationContext
	 *           The application context to look for managed beans in.
	 */
	protected void buildLevelMap(final ApplicationContext applicationContext)
	{
		levelMap = new HashMap<>();

		final Map<String, FieldSetLevelMapping> mappings = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext,
				FieldSetLevelMapping.class);
		for (final FieldSetLevelMapping mapping : mappings.values())
		{
			addToLevelMap(mapping);
		}
	}

	/**
	 * Method add level definition to level map. If level definition for class already exist - it is merged.
	 * 
	 * @param mapping
	 *           - object defining level definition
	 */
	protected void addToLevelMap(final FieldSetLevelMapping mapping)
	{
		if (levelMap.containsKey(mapping.getDtoClass()))
		{
			final Map<String, String> existingMap = levelMap.get(mapping.getDtoClass());
			String levelDefinition;
			for (final Entry<String, String> entry : mapping.getLevelMapping().entrySet())
			{
				if (existingMap.containsKey(entry.getKey()))
				{
					levelDefinition = existingMap.get(entry.getKey()) + "," + entry.getValue();
					existingMap.put(entry.getKey(), levelDefinition);
				}
				else
				{
					existingMap.put(entry.getKey(), entry.getValue());
				}
			}
		}
		else
		{
			levelMap.put(mapping.getDtoClass(), mapping.getLevelMapping());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLevelName(final String levelName, final Class objectClass)
	{
		if (BASIC_LEVEL.equals(levelName) || DEFAULT_LEVEL.equals(levelName) || FULL_LEVEL.equals(levelName))
		{
			return true;
		}
		final Map<String, String> map = getLevelMapForClass(objectClass);
		return map == null ? false : map.containsKey(levelName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createBasicLevelDefinition(final Class objectClass)
	{
		Map<String, String> map = getLevelMapForClass(objectClass);
		if (map == null)
		{
			map = new HashMap<String, String>();
			getLevelMap().put(objectClass, map);
		}

		String levelDefinition = "";
		final Field[] fieldList = objectClass.getDeclaredFields();
		for (final Field field : fieldList)
		{
			if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()))
			{
				levelDefinition = levelDefinition + field.getName() + ",";
			}
		}

		if (!levelDefinition.isEmpty())
		{
			levelDefinition = levelDefinition.substring(0, levelDefinition.length() - 1);
		}
		map.put(BASIC_LEVEL, levelDefinition);
		return levelDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createDefaultLevelDefinition(final Class objectClass)
	{
		Map<String, String> map = getLevelMapForClass(objectClass);
		if (map == null)
		{
			map = new HashMap<String, String>();
			getLevelMap().put(objectClass, map);
		}

		String levelDefinition = "";
		final Field[] fieldList = objectClass.getDeclaredFields();
		for (final Field field : fieldList)
		{
			if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()))
			{
				levelDefinition = levelDefinition + field.getName() + ",";
			}
		}

		if (!levelDefinition.isEmpty())
		{
			levelDefinition = levelDefinition.substring(0, levelDefinition.length() - 1);
		}
		map.put(DEFAULT_LEVEL, levelDefinition);
		return levelDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createFullLevelDefinition(Class objectClass)
	{
		Map<String, String> map = getLevelMapForClass(objectClass);
		if (map == null)
		{
			map = new HashMap<String, String>();
			getLevelMap().put(objectClass, map);
		}

		String levelDefinition = "";
		while (objectClass != null && objectClass != Object.class)
		{
			final Field[] fieldList = objectClass.getDeclaredFields();
			for (final Field field : fieldList)
			{
				if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()))
				{
					levelDefinition = levelDefinition + field.getName() + ",";
				}
			}
			objectClass = objectClass.getSuperclass();
		}

		if (!levelDefinition.isEmpty())
		{
			levelDefinition = levelDefinition.substring(0, levelDefinition.length() - 1);
		}
		map.put(FULL_LEVEL, levelDefinition);
		return levelDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLevelDefinitionForClass(final Class objectClass, final String levelName)
	{
		final Map<String, String> map = getLevelMapForClass(objectClass);
		if (map != null)
		{
			return map.get(levelName);
		}
		return null;
	}

	public Map<Class, Map<String, String>> getLevelMap()
	{
		return levelMap;
	}

	public void setLevelMap(final Map<Class, Map<String, String>> levelMap)
	{
		this.levelMap = levelMap;
	}

	public Map<String, String> getLevelMapForClass(final Class clazz)
	{
		return getLevelMap().get(clazz);
	}
}
