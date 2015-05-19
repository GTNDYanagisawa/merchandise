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
package de.hybris.platform.acceleratorservices.dataexport.generic.output.csv;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.util.ReflectionUtils;


/**
 * Class that converts a list of objects to a list of Strings using the Object's annotations.
 */
public class ConvertObjectsToCSV implements Converter<List<Object>, String>
{
	private static final Logger LOG = Logger.getLogger(ConvertObjectsToCSV.class);
	public static final String GETTER = "get";
	public static final String SETTER = "set";
	public static final String IS = "is"; //NOPMD
	public static final int INDEX_THREE = 3;
	public static final int INDEX_TWO = 2;

	@Override
	public String convert(final List<Object> dataList, final String prototype) throws ConversionException
	{
		return convert(dataList);
	}

	@Override
	public String convert(final List<Object> dataList)
	{
		final List<String> result = new ArrayList<String>();
		if (!dataList.isEmpty())
		{
			final ClassAnnotationProperties classProperties = buildClassProperties(dataList.get(0));
			final String header = generateHeader(classProperties);
			result.add(header);
			LOG.debug("Generated header: " + header);

			result.addAll(generateData(dataList, classProperties));
		}
		final StringBuilder builder = new StringBuilder();
		for (final String s : result)
		{
			builder.append(s).append("\r\n");
		}
		return builder.toString();
	}

	protected List<String> generateData(final List<Object> dataList, final ClassAnnotationProperties classProperties)
	{
		final List<String> result = new ArrayList<String>();
		for (final Object data : dataList)
		{
			final Map<Integer, Method> integerMethodMap = classProperties.getGetters();
			final Iterator<Entry<Integer, Method>> entriesIterator = integerMethodMap.entrySet().iterator();
			final StringBuilder builder = new StringBuilder();
			while (entriesIterator.hasNext())
			{
				final Entry<Integer, Method> currentEntry = entriesIterator.next();
				final Integer integer = currentEntry.getKey();
				final Method method = currentEntry.getValue();
				Object returnObject = ReflectionUtils.invokeMethod(method, data);
				if (returnObject == null)
				{
					returnObject = classProperties.getNullValue().get(integer);
				}
				builder.append(returnObject);
				builder.append(entriesIterator.hasNext() ? classProperties.delimiter : "");
			}
			LOG.debug(builder);
			result.add(builder.toString());
		}
		return result;
	}


	/**
	 * Method that gathers information about the data class and places the information into a ClassAnnotationProperties
	 * object.
	 * 
	 * @param data
	 * @return classProperties
	 */
	protected ClassAnnotationProperties buildClassProperties(final Object data)
	{
		final Map<Integer, String> fields = new HashMap<Integer, String>();
		final Map<Integer, Method> getters = new HashMap<Integer, Method>();
		final Map<Integer, Method> setters = new HashMap<Integer, Method>();
		final Map<Integer, String> nullValues = new HashMap<Integer, String>();

		final Class<?> aClass = data.getClass();
		final Method[] methods = aClass.getMethods();

		for (final Method method : methods)
		{
			final DelimitedFileMethod annotation = method.getAnnotation(DelimitedFileMethod.class);
			if (annotation != null)
			{
				final int position = annotation.position();
				final String methodName = method.getName();

				//work out the property name for the method
				String name = annotation.name();
				if ("".equals(name))
				{
					name = workoutPropertyName(methodName);
				}
				fields.put(Integer.valueOf(position), name);

				if (methodName.startsWith(GETTER) || methodName.startsWith(IS))
				{
					getters.put(Integer.valueOf(position), method);
					setters.put(
							Integer.valueOf(position),
							ReflectionUtils.findMethod(
									data.getClass(),
									SETTER
											+ (methodName.startsWith(GETTER) ? methodName.substring(INDEX_THREE) : methodName
													.substring(INDEX_TWO))));
					nullValues.put(Integer.valueOf(position), annotation.nullValue());
				}
				else if (methodName.startsWith(SETTER))
				{
					setters.put(Integer.valueOf(position), method);
					getters.put(Integer.valueOf(position),
							ReflectionUtils.findMethod(data.getClass(), GETTER + methodName.substring(INDEX_THREE)));
					nullValues.put(Integer.valueOf(position), annotation.nullValue());
				}

			}
		}

		final DelimitedFile delimitedFile = aClass.getAnnotation(DelimitedFile.class);
		final String delimiter = delimitedFile.delimiter();

		return new ClassAnnotationProperties(delimiter, fields, getters, setters, nullValues);
	}

	//use the getter/setter to generate the field name
	protected String workoutPropertyName(String methodName)
	{
		final StringBuilder builder = new StringBuilder();
		if (methodName.startsWith(GETTER) || methodName.startsWith(SETTER) || methodName.startsWith(IS))
		{
			methodName = methodName.startsWith(IS) ? methodName.substring(INDEX_TWO) : methodName.substring(INDEX_THREE);
			for (int j = 0; j < methodName.length(); j++)
			{
				if ((Character.isUpperCase(methodName.charAt(j)) || Character.isDigit(methodName.charAt(j))) && j != 0)
				{
					builder.append(' ');
				}
				builder.append(Character.toLowerCase(methodName.charAt(j)));
			}
		}

		return builder.toString();
	}


	protected String generateHeader(final ClassAnnotationProperties dataProperties)
	{
		final StringBuilder builder = new StringBuilder();
		final Map<Integer, String> map = dataProperties.getPropertyNames();
		final Iterator<Entry<Integer, String>> entriesIterator = map.entrySet().iterator();

		while (entriesIterator.hasNext())
		{
			final Entry<Integer, String> currentEntry = entriesIterator.next();
			final String dataProperty = currentEntry.getValue();
			builder.append(dataProperty);
			if (entriesIterator.hasNext())
			{
				builder.append(dataProperties.getDelimiter());
			}
		}

		return builder.toString();
	}

	public static class ClassAnnotationProperties
	{
		private String delimiter;
		private Map<Integer, String> propertyNames;
		private Map<Integer, Method> getters;
		private Map<Integer, Method> setters;
		private Map<Integer, String> nullValue;

		public ClassAnnotationProperties(final String delimiter, final Map<Integer, String> propertyNames,
				final Map<Integer, Method> getters, final Map<Integer, Method> setters, final Map<Integer, String> nullValue)
		{
			this.delimiter = delimiter;
			this.propertyNames = propertyNames;
			this.getters = getters;
			this.setters = setters;
			this.nullValue = nullValue;
		}

		public String getDelimiter()
		{
			return delimiter;
		}

		public void setDelimiter(final String delimiter)
		{
			this.delimiter = delimiter;
		}

		public Map<Integer, String> getPropertyNames()
		{
			return propertyNames;
		}

		public void setPropertyNames(final Map<Integer, String> propertyNames)
		{
			this.propertyNames = propertyNames;
		}

		public Map<Integer, Method> getGetters()
		{
			return getters;
		}

		public void setGetters(final Map<Integer, Method> getters)
		{
			this.getters = getters;
		}

		public Map<Integer, Method> getSetters()
		{
			return setters;
		}

		public void setSetters(final Map<Integer, Method> setters)
		{
			this.setters = setters;
		}

		public Map<Integer, String> getNullValue()
		{
			return nullValue;
		}

		public void setNullValue(final Map<Integer, String> nullValue)
		{
			this.nullValue = nullValue;
		}
	}
}
