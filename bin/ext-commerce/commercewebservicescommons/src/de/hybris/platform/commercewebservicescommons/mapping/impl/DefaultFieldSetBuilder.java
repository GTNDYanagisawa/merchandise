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

import de.hybris.platform.commercewebservicescommons.mapping.FieldSetBuilder;
import de.hybris.platform.commercewebservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.sql.Time;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.cache.annotation.Cacheable;


/**
 * Default implementation of {@link de.hybris.platform.commercewebservicescommons.mapping.FieldSetBuilder}
 * 
 */
public class DefaultFieldSetBuilder implements FieldSetBuilder
{
	private final static Logger LOG = Logger.getLogger(DefaultFieldSetBuilder.class);
	/**
	 * Define how many recurrency level builder should support by default (it is case when object have it's own type
	 * field e.g. VariantMatrixElementData have elements which are also VariantMatrixElementData type)
	 */
	private int defaultRecurrencyLevel = 4;

	/**
	 * Max size of created field set. When it is exceeded exception is thrown.
	 */
	private int defaultMaxFieldSetSize = 50000;

	/**
	 * Set of classes, which we consider simple - for such
	 */
	private Set<Class> simpleClassSet;

	/**
	 * Helper for field set levels
	 */
	private FieldSetLevelHelper fieldSetLevelHelper;

	public DefaultFieldSetBuilder()
	{
		simpleClassSet = new HashSet<Class>(Arrays.<Class> asList(Byte.class, Short.class, Integer.class, Long.class,
				Boolean.class, Character.class, Float.class, String.class, Date.class, Time.class, Object.class));
	}

	@Override
	@Cacheable(value = "fieldSetCache", key = "{#clazz,#fieldPrefix,#configuration}")
	public Set<String> createFieldSet(final Class clazz, final String fieldPrefix, final String configuration)
	{
		return createFieldSet(clazz, fieldPrefix, configuration, null);
	}

	/**
	 * Method converts configuration string to set of fully qualified field names eg. address.country.name,
	 * address.country.isocode.<br/>
	 * For example : <br/>
	 * - createFieldSet(AddressData.class,"address","firstName, lastName") will return set
	 * {address.firstName,address.lastName}<br/>
	 * - createFieldSet(AddressData.class,"address","BASIC,town") when definition for BASIC level ="firstName, lastName"
	 * will return {address.firstName,address.lastName,address.town}
	 * 
	 * @param clazz
	 *           - class of object for which field set is created
	 * @param fieldPrefix
	 *           - prefix which should be added to field name
	 * @param configuration
	 *           - string describing properties which should be added to the set
	 * @param context
	 *           - object storing additional information like :<br/>
	 *           <b>typeVariableMap</b> - map containing information about types used in generic class <br/>
	 *           e.g. if we have type class like ProductSearchPageData<STATE, RESULT> we should give map like
	 *           {STATE=SearchStateData.class, RESULT=ProductData.class}<br/>
	 *           <b>recurrencyLevel</b> - define how many recurrency level builder should support (it is case when object
	 *           have it's own type field e.g. VariantMatrixElementData have elements which are also
	 *           VariantMatrixElementData type) <br/>
	 *           <b>recurrencyMap</b> - map for controlling recurrency level
	 * @return set of fully qualified field names
	 */
	@Override
	@Cacheable(value = "fieldSetCache", key = "{#clazz,#fieldPrefix,#configuration,#context}")
	public Set<String> createFieldSet(final Class clazz, final String fieldPrefix, final String configuration,
			FieldSetBuilderContext context)
	{
		if (context == null)
		{
			context = new FieldSetBuilderContext();
			context.setRecurrencyLevel(getDefaultRecurrencyLevel());
			context.setMaxFieldSetSize(getDefaultMaxFieldSetSize());
		}
		context.resetFieldCounter();
		context.resetRecurrencyMap();
		return createFieldSetInternal(clazz, fieldPrefix, configuration, context);
	}

	/**
	 * Method converts configuration string to set of fully qualified field names eg. address.country.name,
	 * address.country.isocode.<br/>
	 * For example : <br/>
	 * - createFieldSetInternal(AddressData.class,"address","firstName, lastName") will return set
	 * {address.firstName,address.lastName}<br/>
	 * - createFieldSetInternal(AddressData.class,"address","BASIC,town") when definition for BASIC level
	 * ="firstName, lastName" will return {address.firstName,address.lastName,address.town}
	 * 
	 * @param clazz
	 *           - class of object for which field set is created
	 * @param fieldPrefix
	 *           - prefix which should be added to field name
	 * @param configuration
	 *           - string describing properties which should be added to the set
	 * @param context
	 *           - object storing additional information like :<br/>
	 *           <b>typeVariableMap</b> - map containing information about types used in generic class <br/>
	 *           e.g. if we have type class like ProductSearchPageData<STATE, RESULT> we should give map like
	 *           {STATE=SearchStateData.class, RESULT=ProductData.class}<br/>
	 *           <b>recurrencyLevel</b> - define how many recurrency level builder should support (it is case when object
	 *           have it's own type field e.g. VariantMatrixElementData have elements which are also
	 *           VariantMatrixElementData type) <br/>
	 *           <b>recurrencyMap</b> - map for controlling recurrency level
	 * @return set of fully qualified field names
	 */
	private Set<String> createFieldSetInternal(final Class clazz, final String fieldPrefix, String configuration,
			final FieldSetBuilderContext context)
	{
		final Set<String> fieldSet = new HashSet();

		if (configuration == null || configuration.isEmpty())
		{
			return fieldSet;
		}
		configuration = configuration.trim();
		if (configuration.charAt(configuration.length() - 1) == ',' || configuration.charAt(configuration.length() - 1) == '(')
		{
			throw new ConversionException("Incorrect configuration");
		}
		int currentPos = 0;
		String elementName = "";
		while (currentPos < configuration.length())
		{
			elementName = getElementName(currentPos, configuration);
			currentPos = currentPos + elementName.length();
			elementName = elementName.trim();
			if (getFieldSetLevelHelper().isLevelName(elementName, clazz))
			{
				fieldSet.addAll(createFieldSetForLevel(clazz, fieldPrefix, elementName, context));
			}
			else
			{
				final Type fieldType = getFieldType(elementName, clazz);
				final String fullFieldName = createFullFieldName(fieldPrefix, elementName);
				currentPos = parseComplexField(configuration, currentPos, fullFieldName, fieldType, fieldSet, context);
			}
			currentPos = omitComma(currentPos, configuration);
		}
		return fieldSet;
	}

	private String getElementName(final int startIndex, final String configuration)
	{
		final int elementEnd = findElementEnd(startIndex, configuration);
		return configuration.substring(startIndex, elementEnd);
	}

	private int findElementEnd(final int startIndex, final String configuration)
	{
		int index = startIndex;
		while (index < configuration.length())
		{
			final char c = configuration.charAt(index);
			if (c == '(' || c == ',')
			{
				return index;
			}
			index++;
		}

		return configuration.length();
	}

	/**
	 * Method returns set of fully qualified field names defined in level. For example if BASIC='firstName,lastName' and
	 * prefix='address' we will get set with values : address.firstName and address.lastName
	 * 
	 * @param clazz
	 *           - class of object. It is needed to find level definition
	 * @param prefix
	 *           - prefix, which should be added to parameter name
	 * @param levelName
	 *           - level name e.g. BASIC
	 * @param context
	 *           - object storing additional information like :<br/>
	 *           <b>typeVariableMap</b> - map containing information about types used in generic class <br/>
	 *           e.g. if we have type class like ProductSearchPageData<STATE, RESULT> we should give map like
	 *           {STATE=SearchStateData.class, RESULT=ProductData.class}<br/>
	 *           <b>recurrencyLevel</b> - define how many recurrency level builder should support (it is case when object
	 *           have it's own type field e.g. VariantMatrixElementData have elements which are also
	 *           VariantMatrixElementData type) <br/>
	 *           <b>recurrencyMap</b> - map for controlling recurrency level
	 * @return set of fully qualified field names
	 */
	private Set<String> createFieldSetForLevel(final Class clazz, final String prefix, final String levelName,
			final FieldSetBuilderContext context)
	{
		String levelDef = fieldSetLevelHelper.getLevelDefinitionForClass(clazz, levelName);
		if (levelDef == null)
		{
			if (FieldSetLevelHelper.BASIC_LEVEL.equals(levelName))
			{
				levelDef = fieldSetLevelHelper.createBasicLevelDefinition(clazz);
			}
			else if (FieldSetLevelHelper.DEFAULT_LEVEL.equals(levelName))
			{
				levelDef = fieldSetLevelHelper.createDefaultLevelDefinition(clazz);
			}
			else if (FieldSetLevelHelper.FULL_LEVEL.equals(levelName))
			{
				levelDef = fieldSetLevelHelper.createFullLevelDefinition(clazz);
			}
		}
		return createFieldSetInternal(clazz, prefix, levelDef, context);
	}

	/**
	 * Methods add fully qualified field name to fieldSet. If field is complex class, it also add fully qualified names
	 * for it's fields. Method also handle Collections (like Map, List), WildcardType, TypeVariable
	 * 
	 * @param configuration
	 *           - string describing properties which should be added to the set
	 * @param currentPos
	 *           - position in configuration string
	 * @param fullFieldName
	 *           - fully qualified field name
	 * @param fieldType
	 *           - field type
	 * @param fieldSet
	 *           - set where fully qualified field name should be added
	 * @param context
	 *           - object storing additional information like :<br/>
	 *           <b>typeVariableMap</b> - map containing information about types used in generic class <br/>
	 *           e.g. if we have type class like ProductSearchPageData<STATE, RESULT> we should give map like
	 *           {STATE=SearchStateData.class, RESULT=ProductData.class}<br/>
	 *           <b>recurrencyLevel</b> - define how many recurrency level builder should support (it is case when object
	 *           have it's own type field e.g. VariantMatrixElementData have elements which are also
	 *           VariantMatrixElementData type) <br/>
	 *           <b>recurrencyMap</b> - map for controlling recurrency level
	 * @return current position in configuration string
	 */
	private int parseComplexField(final String configuration, final int currentPos, final String fullFieldName,
			final Type fieldType, final Set<String> fieldSet, final FieldSetBuilderContext context)
	{
		if (fieldType instanceof ParameterizedType)
		{
			final ParameterizedType parametrizedType = (ParameterizedType) fieldType;
			final Type rawType = parametrizedType.getRawType();

			if (Map.class.isAssignableFrom((Class<?>) rawType))
			{
				return parseMapField(configuration, currentPos, fullFieldName, (ParameterizedType) fieldType, fieldSet, context);
			}
			else if (Collection.class.isAssignableFrom((Class<?>) rawType))
			{
				return parseComplexField(configuration, currentPos, fullFieldName, parametrizedType.getActualTypeArguments()[0],
						fieldSet, context);
			}
			else
			{
				return parseField(configuration, currentPos, fullFieldName, rawType, fieldSet, context);
			}
		}
		else if (fieldType instanceof WildcardType)
		{
			final WildcardType wildcartType = (WildcardType) fieldType;
			final Type[] lowerBounds = wildcartType.getLowerBounds();
			if (lowerBounds != null && lowerBounds.length > 0 && lowerBounds[0] != null)
			{
				return parseComplexField(configuration, currentPos, fullFieldName, lowerBounds[0], fieldSet, context);
			}
			else
			{
				return parseComplexField(configuration, currentPos, fullFieldName, wildcartType.getUpperBounds()[0], fieldSet,
						context);
			}
		}
		else if (fieldType instanceof TypeVariable)
		{
			final TypeVariable typeVariable = (TypeVariable) fieldType;
			if (context.getTypeVariableMap() != null && context.getTypeVariableMap().containsKey(typeVariable.getName()))
			{
				return parseComplexField(configuration, currentPos, fullFieldName,
						context.getTypeVariableMap().get(typeVariable.getName()), fieldSet, context);
			}
			else
			{
				return parseComplexField(configuration, currentPos, fullFieldName, Object.class, fieldSet, context);
			}
		}
		else
		{
			return parseField(configuration, currentPos, fullFieldName, fieldType, fieldSet, context);
		}
	}

	/**
	 * Methods add fully qualified field name to fieldSet. If field is complex class, it also add fully qualified names
	 * for it's fields.
	 * 
	 * @param configuration
	 *           - string describing properties which should be added to the set
	 * @param currentPos
	 *           - position in configuration string
	 * @param fullFieldName
	 *           - fully qualified field name
	 * @param fieldType
	 *           - field type
	 * @param fieldSet
	 *           - set where fully qualified field name should be added
	 * @param context
	 *           - object storing additional information like :<br/>
	 *           <b>typeVariableMap</b> - map containing information about types used in generic class <br/>
	 *           e.g. if we have type class like ProductSearchPageData<STATE, RESULT> we should give map like
	 *           {STATE=SearchStateData.class, RESULT=ProductData.class}<br/>
	 *           <b>recurrencyLevel</b> - define how many recurrency level builder should support (it is case when object
	 *           have it's own type field e.g. VariantMatrixElementData have elements which are also
	 *           VariantMatrixElementData type) <br/>
	 *           <b>recurrencyMap</b> - map for controlling recurrency level
	 * @return current position in configuration string
	 */
	private int parseField(final String configuration, int currentPos, final String fullFieldName, final Type fieldType,
			final Set<String> fieldSet, final FieldSetBuilderContext context)
	{
		final Class fieldClass = getClassForType(fieldType);
		currentPos = omitSpace(currentPos, configuration);
		if (currentPos < configuration.length() && configuration.charAt(currentPos) == '(')
		{
			final int confEnd = findMatchingCloseBracket(configuration, currentPos);
			if (confEnd != -1)
			{
				final String fieldConf = configuration.substring(currentPos + 1, confEnd);
				if (!fieldConf.isEmpty() && isSimpleClass(fieldClass))
				{
					throw new ConversionException("Incorrect configuration : field '" + fullFieldName + "' don't need configuration");
				}
				if (!context.isRecurencyLevelExceeded(fieldClass))
				{
					context.addToRecurrencyMap(fieldClass);
					fieldSet.addAll(createFieldSetInternal(fieldClass, fullFieldName, fieldConf, context));
					context.removeFromRecurrencyMap(fieldClass);
				}
			}
			else
			{
				throw new ConversionException("Incorrect configuration : Missing ')'");
			}
			currentPos = omitBracket(confEnd, configuration);
		}
		else if (!isSimpleClass(fieldClass))
		{
			if (!context.isRecurencyLevelExceeded(fieldClass))
			{
				context.addToRecurrencyMap(fieldClass);
				fieldSet.addAll(createFieldSetInternal(fieldClass, fullFieldName, FieldSetLevelHelper.BASIC_LEVEL, context));
				context.removeFromRecurrencyMap(fieldClass);
			}
		}

		addToFieldSet(fieldSet, fullFieldName, context);

		return currentPos;
	}

	private void addToFieldSet(final Set<String> fieldSet, final String fullFieldName, final FieldSetBuilderContext context)
	{
		if (!context.isMaxFieldSetSizeExceeded())
		{
			fieldSet.add(fullFieldName);
			context.incrementFieldCounter();
		}
		else
		{
			throw new ConversionException(
					"Max field set size exceeded. Reason of that can be : too generic configuration, lack of properly defined BASIC field set level for data class, reccurency in data structure");
		}
	}

	private Class getClassForType(final Type fieldType)
	{
		Class fieldClass = Object.class;
		if (fieldType instanceof Class)
		{
			fieldClass = (Class) fieldType;
			if (fieldClass.isArray())
			{
				while (fieldClass.isArray())
				{
					fieldClass = fieldClass.getComponentType();
				}
			}
		}
		return fieldClass;
	}

	/**
	 * Methods add fully qualified field name for map and it's key and value.
	 * 
	 * @param configuration
	 *           - string describing properties which should be added to the set
	 * @param currentPos
	 *           - position in configuration string
	 * @param fieldName
	 *           - map field name
	 * @param fieldType
	 *           - field type
	 * @param fieldSet
	 *           - set where fully qualified field name should be added
	 * @param context
	 *           - object storing additional information like :<br/>
	 *           <b>typeVariableMap</b> - map containing information about types used in generic class <br/>
	 *           e.g. if we have type class like ProductSearchPageData<STATE, RESULT> we should give map like
	 *           {STATE=SearchStateData.class, RESULT=ProductData.class}<br/>
	 *           <b>recurrencyLevel</b> - define how many recurrency level builder should support (it is case when object
	 *           have it's own type field e.g. VariantMatrixElementData have elements which are also
	 *           VariantMatrixElementData type) <br/>
	 *           <b>recurrencyMap</b> - map for controlling recurrency level
	 * @return current position in configuration string
	 */
	private int parseMapField(final String configuration, int currentPos, final String fieldName,
			final ParameterizedType fieldType, final Set<String> fieldSet, final FieldSetBuilderContext context)
	{
		String mapConf = "";
		currentPos = omitSpace(currentPos, configuration);
		if (currentPos < configuration.length() && configuration.charAt(currentPos) == '(')
		{
			final int confEnd = findMatchingCloseBracket(configuration, currentPos);
			if (confEnd != -1)
			{
				mapConf = configuration.substring(currentPos + 1, confEnd).trim();
			}
			else
			{
				throw new ConversionException("Incorrect map configuration : Missing ')'");
			}
			currentPos = omitBracket(confEnd, configuration);
		}

		final Type keyType = fieldType.getActualTypeArguments()[0];
		final String keyFieldName = createFullFieldName(fieldName, "key");
		int pos = parseComplexField(mapConf, 0, keyFieldName, keyType, fieldSet, context);

		final Type valueType = fieldType.getActualTypeArguments()[1];
		final String valueFieldName = createFullFieldName(fieldName, "value");
		pos = parseComplexField(mapConf, pos, valueFieldName, valueType, fieldSet, context);

		if (pos < mapConf.length())
		{
			throw new ConversionException("Incorrect map configuration : '" + mapConf + "'");
		}

		addToFieldSet(fieldSet, fieldName, context);

		return currentPos;
	}

	/**
	 * Method returns field type
	 * 
	 * @param fieldName
	 *           - field name
	 * @param objectClass
	 *           - object class
	 * @return field type
	 */
	protected Type getFieldType(final String fieldName, final Class objectClass)
	{
		if (fieldName == null || fieldName.isEmpty())
		{
			throw new ConversionException("Incorrect field: field name is empty string");
		}

		Class clazz = objectClass;
		while (clazz != null)
		{
			try
			{
				final Field fieldField = clazz.getDeclaredField(fieldName);
				return fieldField.getGenericType();
			}
			catch (final NoSuchFieldException e)
			{
				clazz = clazz.getSuperclass();
			}
		}

		throw new ConversionException("Incorrect field:'" + fieldName + "'");
	}

	/**
	 * Method create fully qualified field name
	 * 
	 * @param basePrefix
	 *           - prefix
	 * @param fieldName
	 *           - field name
	 * @return fully qualified field name
	 */
	protected String createFullFieldName(final String basePrefix, final String fieldName)
	{
		final String prefix;
		if (basePrefix == null || basePrefix.isEmpty())
		{
			prefix = fieldName;
		}
		else
		{
			prefix = basePrefix + "." + fieldName;
		}
		return prefix;
	}

	/**
	 * Method check if class should be considered as simple. Simple class cannot have configuration. So if class of
	 * field1 is simple we cannot use : field1(field11,field12)
	 * 
	 * @param clazz
	 *           - field class
	 * @return true - if field type is simple<br/>
	 *         false - if field type is complex
	 */
	protected boolean isSimpleClass(final Class clazz)
	{
		if (clazz.isPrimitive() || clazz.isEnum() || getSimpleClassSet().contains(clazz) || Number.class.isAssignableFrom(clazz)
				|| HybrisEnumValue.class.isAssignableFrom(clazz))
		{
			return true;
		}
		return false;
	}

	private int findMatchingCloseBracket(final String configuration, final int openPos)
	{
		int closePos = openPos + 1;
		int counter = 1;
		while (counter > 0 && closePos < configuration.length())
		{
			final char c = configuration.charAt(closePos++);
			if (c == '(')
			{
				counter++;
			}
			else if (c == ')')
			{
				counter--;
			}
		}
		if (counter == 0)
		{
			return --closePos;
		}
		return -1;
	}

	private int omitSpace(final int startIndex, final String configuration)
	{
		int index = startIndex;
		while (index < configuration.length() && configuration.charAt(index) <= ' ')
		{
			index++;
		}
		return index;
	}

	private int omitComma(final int startIndex, final String configuration)
	{
		int index = omitSpace(startIndex, configuration);
		if (index < configuration.length() && configuration.charAt(index) == ',')
		{
			index++;
		}
		return index;
	}

	private int omitBracket(final int startIndex, final String configuration)
	{
		int index = omitSpace(startIndex, configuration);
		if (index < configuration.length() && configuration.charAt(index) == ')')
		{
			index++;
		}
		return index;
	}

	public Set<Class> getSimpleClassSet()
	{
		return simpleClassSet;
	}

	public void setSimpleClassSet(final Set<Class> simpleTypeSet)
	{
		this.simpleClassSet = simpleTypeSet;
	}

	public int getDefaultRecurrencyLevel()
	{
		return defaultRecurrencyLevel;
	}

	public void setDefaultRecurrencyLevel(final int defaultRecurrencyLevel)
	{
		this.defaultRecurrencyLevel = defaultRecurrencyLevel;
	}

	public FieldSetLevelHelper getFieldSetLevelHelper()
	{
		return fieldSetLevelHelper;
	}

	@Required
	public void setFieldSetLevelHelper(final FieldSetLevelHelper fieldSetLevelHelper)
	{
		this.fieldSetLevelHelper = fieldSetLevelHelper;
	}

	public int getDefaultMaxFieldSetSize()
	{
		return defaultMaxFieldSetSize;
	}

	public void setDefaultMaxFieldSetSize(final int defaultMaxFieldSetSize)
	{
		this.defaultMaxFieldSetSize = defaultMaxFieldSetSize;
	}

}
