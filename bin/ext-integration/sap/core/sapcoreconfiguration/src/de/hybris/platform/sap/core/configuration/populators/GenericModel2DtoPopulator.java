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
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Generic populator which automatically populates model properties to DTO.
 * <p>
 * The following logic is executed
 * <ul>
 * <li>values of properties with same names are copied</li>
 * <li>related model PKs are copied to the according DTO properties with PK suffix</li>
 * </ul>
 * </p>
 */
public class GenericModel2DtoPopulator implements Populator<ItemModel, Object>
{

	private static final Logger LOG = Logger.getLogger(GenericModel2DtoPopulator.class.getName());

	@Override
	public void populate(final ItemModel model, final Object dto) throws ConversionException
	{
		final Map<String, PropertyDescriptor> dtoPropertyDescriptors = getPropertyDescriptors(dto);
		final Map<String, PropertyDescriptor> modelPropertyDescriptors = getPropertyDescriptors(model);
		for (final PropertyDescriptor dtoPropertyDescriptor : dtoPropertyDescriptors.values())
		{
			// Get property name
			String propertyName = dtoPropertyDescriptor.getName();
			boolean pkHandling = false;
			// Check if DTO has a setter for the current property
			if (dtoPropertyDescriptor.getWriteMethod() == null)
			{
				continue;
			}

			// Check if standard property or related PK property
			if (propertyName.endsWith("PK"))
			{
				final String pkPropertyName = propertyName.substring(0, propertyName.length() - 2);
				propertyName = pkPropertyName;
				pkHandling = true;
			}

			// Check if corresponding getter exists in model
			final PropertyDescriptor modelPropertyDescriptor = modelPropertyDescriptors.get(propertyName);
			if (modelPropertyDescriptor != null)
			{
				// Get model value
				final Object modelValue = getModelValue(model, modelPropertyDescriptor, pkHandling);
				// Set DTO value
				setDtoValue(dto, dtoPropertyDescriptor, modelValue);
			}
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
				modelValue = ((ItemModel) modelValue).getPk();
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
	 * Sets the value into the corresponding DTO property.
	 * 
	 * @param dto
	 *           data transfer object (bean)
	 * @param dtoPropertyDescriptor
	 *           property descriptor of the corresponding property
	 * @param value
	 *           property value
	 */
	private void setDtoValue(final Object dto, final PropertyDescriptor dtoPropertyDescriptor, final Object value)
	{
		try
		{
			dtoPropertyDescriptor.getWriteMethod().invoke(dto, new Object[]
			{ value });
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			LOG.fatal("Property '" + dtoPropertyDescriptor.getName() + "' of dto '" + dto.getClass() + "' could not be written: "
					+ e.toString());
		}
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
