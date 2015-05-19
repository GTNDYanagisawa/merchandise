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
package de.hybris.platform.sap.core.configuration.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Generic populator which automatically populates model properties to map.
 * <p>
 * The following logic is executed
 * <ul>
 * <li>values of properties with same names are copied</li>
 * <li>related model PKs are copied to the according properties with PK suffix</li>
 * </ul>
 * </p>
 */
public class GenericModel2MapPopulator implements Populator<ItemModel, Map<String, Object>>
{

	private static final Logger LOG = Logger.getLogger(GenericModel2MapPopulator.class.getName());

	@Override
	public void populate(final ItemModel model, final Map<String, Object> propertyMap) throws ConversionException
	{
		final Map<String, PropertyDescriptor> modelPropertyDescriptors = getPropertyDescriptors(model);
		for (final PropertyDescriptor modelPropertyDescriptor : modelPropertyDescriptors.values())
		{
			// Get property name
			String propertyName = modelPropertyDescriptor.getName();

			// Ignore generic properties
			switch (propertyName)
			{
				case "itemModelContext":
				case "comments":
				case "class":
					continue;
			}

			// Define map property name
			final Class<?> returnType = modelPropertyDescriptor.getReadMethod().getReturnType();
			// Foreign key handling with 1:1 relation
			boolean pkHandling = false;
			if (ItemModel.class.isAssignableFrom(returnType))
			{
				propertyName += "PK";
				pkHandling = true;
			}
			// Foreign key handling with 1:n relation
			else if (Collection.class.isAssignableFrom(returnType))
			{
				// not supported
				continue;
			}

			// Get model value
			final Object modelValue = getModelValue(model, modelPropertyDescriptor, pkHandling);
			// Add value to map
			propertyMap.put(propertyName, modelValue);

		}
	}

	/**
	 * Gets the value from the corresponding model property.
	 * 
	 * @param model
	 *           model
	 * @param pkHandling
	 *           foreign primary key handling
	 * @param modelPropertyDescriptor
	 *           property descriptor
	 * @return value of the model property
	 */
	private Object getModelValue(final ItemModel model, final PropertyDescriptor modelPropertyDescriptor, final boolean pkHandling)
	{
		Object modelValue = null;
		try
		{
			modelValue = modelPropertyDescriptor.getReadMethod().invoke(model, (Object[]) null);
			if (pkHandling)
			{
				if (modelValue != null)
				{
					modelValue = ((ItemModel) modelValue).getPk();
				}
				else
				{
					modelValue = "";
				}
			}
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			LOG.fatal("Property '" + modelPropertyDescriptor.getName() + "' of model '" + model.getClass() + "' could not be read: "
					+ e.toString());
		}
		return modelValue;
	}

	/**
	 * Determines all property descriptors for the given bean.
	 * 
	 * @param bean
	 *           bean to be instrospected
	 * 
	 * @return map of property descriptors
	 */
	private Map<String, PropertyDescriptor> getPropertyDescriptors(final Object bean)
	{
		BeanInfo info;
		final Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<String, PropertyDescriptor>();
		try
		{
			info = Introspector.getBeanInfo(bean.getClass());
			final PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
			for (final PropertyDescriptor propertyDescriptor : propertyDescriptors)
			{
				propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
			}
		}
		catch (final IntrospectionException e)
		{
			LOG.fatal("Bean '" + bean.getClass() + "' could not be introspected.");
		}
		return propertyDescriptorMap;
	}

}
