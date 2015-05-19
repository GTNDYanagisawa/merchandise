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

import de.hybris.platform.commercewebservicescommons.mapping.DataMapper;
import de.hybris.platform.commercewebservicescommons.mapping.FieldSetBuilder;
import de.hybris.platform.commercewebservicescommons.mapping.config.FieldMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.Filter;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.MappingContextFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Provides an implementation of {@link de.hybris.platform.commercewebservicescommons.mapping.DataMapper} which can be
 * injected. It automatically autodiscover and register any implementations of {@link ma.glasnost.orika.Mapper},
 * {@link ma.glasnost.orika.Converter} or {@link ma.glasnost.orika.Filter}.
 */
public class DefaultDataMapper extends ConfigurableMapper implements DataMapper, ApplicationContextAware
{
	private static final boolean DEFAULT_MAP_NULLS = false;
	private final MappingContextFactory mappingContextFactory = new MappingContext.Factory();
	private MapperFactory factory;
	private FieldSetBuilder fieldSetBuilder;
	private ApplicationContext applicationContext;

	public DefaultDataMapper()
	{
		super(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureFactoryBuilder(final DefaultMapperFactory.Builder factoryBuilder)
	{
		factoryBuilder.captureFieldContext(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure(final MapperFactory factory)
	{
		this.factory = factory;
		addAllSpringBeans();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;
		init();
	}

	/**
	 * Adds all managed beans of type {@link ma.glasnost.orika.Mapper}, {@link ma.glasnost.orika.Converter} or
	 * {@link ma.glasnost.orika.Filter} to the parent {@link MapperFactory}.
	 */
	protected void addAllSpringBeans()
	{
		final Map<String, Converter> converters = applicationContext.getBeansOfType(Converter.class);
		for (final Converter converter : converters.values())
		{
			addConverter(converter);
		}

		final Map<String, Mapper> mappers = applicationContext.getBeansOfType(Mapper.class);
		for (final Mapper mapper : mappers.values())
		{
			addMapper(mapper);
		}

		final Map<String, Filter> filters = applicationContext.getBeansOfType(Filter.class);
		for (final Filter filter : filters.values())
		{
			addFilter(filter);
		}

		final Map<String, FieldMapper> fieldMappers = applicationContext.getBeansOfType(FieldMapper.class);
		for (final FieldMapper mapper : fieldMappers.values())
		{
			addFieldMapper(mapper);
		}

	}

	/**
	 * Adds a {@link Converter}.
	 * 
	 * @param converter
	 *           The converter.
	 */
	public void addConverter(final Converter<?, ?> converter)
	{
		factory.getConverterFactory().registerConverter(converter);
	}

	/**
	 * Adds a {@link Mapper}.
	 * 
	 * @param mapper
	 *           The mapper.
	 */
	public void addMapper(final Mapper<?, ?> mapper)
	{
		factory.classMap(mapper.getAType(), mapper.getBType()).byDefault().customize((Mapper) mapper).register();
	}

	/**
	 * Adds a {@link Mapper} with field mapping given by fieldMapper object
	 * 
	 * @param fieldMapper
	 *           Object storing field mapping information.
	 */
	public void addFieldMapper(final FieldMapper fieldMapper)
	{
		ClassMapBuilder mapBuilder = null;
		if ((fieldMapper.getSourceClassArguments() != null && !fieldMapper.getSourceClassArguments().isEmpty())
				|| (fieldMapper.getDestClassArguments() != null && !fieldMapper.getDestClassArguments().isEmpty()))
		{
			final Type sourceType = TypeFactory.valueOf(fieldMapper.getSourceClass(), fieldMapper.getSourceActualTypeArguments());
			final Type destType = TypeFactory.valueOf(fieldMapper.getDestClass(), fieldMapper.getDestActualTypeArguments());
			mapBuilder = factory.classMap(sourceType, destType);
		}
		else
		{
			mapBuilder = factory.classMap(fieldMapper.getSourceClass(), fieldMapper.getDestClass());
		}

		for (final Map.Entry<String, String> entry : fieldMapper.getFieldMapping().entrySet())
		{
			mapBuilder.field(entry.getKey(), entry.getValue());
		}
		factory.registerClassMap(mapBuilder.byDefault().toClassMap());
	}

	/**
	 * Adds a {@link Filter}.
	 * 
	 * @param filter
	 *           The filter.
	 */
	public void addFilter(final Filter<?, ?> filter)
	{
		factory.registerFilter(filter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <S, D> D map(S sourceObject, Class<D> destinationClass)
	{
		return map(sourceObject, destinationClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <S, D> D map(final S sourceObject, final Class<D> destinationClass, final String fields)
	{
		return map(sourceObject, destinationClass, createMappingContext(destinationClass, fields));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <S, D> D map(final S sourceObject, final Class<D> destinationClass, final Set<String> fields)
	{
		return map(sourceObject, destinationClass, createMappingContext(fields));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <S, D> void map(S sourceObject, D destinationObject, String fields)
	{
		map(sourceObject, destinationObject, createMappingContext(destinationObject.getClass(), fields));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <S, D> void map(final S sourceObject, final D destinationObject, final String fields, final boolean mapNulls)
	{
		map(sourceObject, destinationObject, createMappingContext(destinationObject.getClass(), fields, mapNulls));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <S, D> void map(S sourceObject, D destinationObject)
	{
		map(sourceObject, destinationObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <S, D> void map(final S sourceObject, final D destinationObject, final boolean mapNulls)
	{
		map(sourceObject, destinationObject, createMappingContext(destinationObject.getClass(), null, mapNulls));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <S, D> List<D> mapAsList(final Iterable<S> source, final Class<D> destinationClass, final String fields)
	{
		return mapAsList(source, destinationClass, createMappingContext(destinationClass, fields));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <S, D> Set<D> mapAsSet(final Iterable<S> source, final Class<D> destinationClass, final String fields)
	{
		return mapAsSet(source, destinationClass, createMappingContext(destinationClass, fields));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <S, D> void mapAsCollection(final Iterable<S> source, final Collection<D> destination, final Class<D> destinationClass,
			final String fields)
	{
		mapAsCollection(source, destination, destinationClass, createMappingContext(destinationClass, fields));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <S, D> void mapGeneric(final S sourceObject, final D destObject,
			final java.lang.reflect.Type[] sourceActualTypeArguments, final java.lang.reflect.Type[] destActualTypeArguments,
			final String fields, final Map<String, Class> destTypeVariableMap)
	{
		final Type sourceType = TypeFactory.valueOf(sourceObject.getClass(), sourceActualTypeArguments);
		final Type destType = TypeFactory.valueOf(destObject.getClass(), destActualTypeArguments);
		map(sourceObject, destObject, sourceType, destType,
				createMappingContextForGeneric(destObject.getClass(), fields, destTypeVariableMap));
	}

	protected MappingContext createMappingContext(final Set<String> fields)
	{
		final MappingContext context = mappingContextFactory.getContext();
		if (fields != null)
			context.setProperty(FIELD_SET_NAME, fields);
		return context;
	}

	protected MappingContext createMappingContext(final Class destinationClass, final String fields)
	{
		return createMappingContext(destinationClass, fields, DEFAULT_MAP_NULLS);
	}

	protected MappingContext createMappingContext(final Class destinationClass, final String fields, final boolean mapNulls)
	{
		final MappingContext context = mappingContextFactory.getContext();
		if (fields != null)
		{
			final Set<String> propertySet = fieldSetBuilder.createFieldSet(destinationClass, FIELD_PREFIX, fields);
			context.setProperty(FIELD_SET_NAME, propertySet);
		}
		context.setProperty(MAP_NULLS, mapNulls);
		return context;
	}

	protected MappingContext createMappingContextForGeneric(final Class destinationClass, final String fields,
			final Map<String, Class> typeVariableMap)
	{
		final MappingContext context = mappingContextFactory.getContext();
		if (fields != null)
		{
			final FieldSetBuilderContext fieldSetBuilderContext = new FieldSetBuilderContext();
			fieldSetBuilderContext.setTypeVariableMap(typeVariableMap);
			final Set<String> propertySet = fieldSetBuilder.createFieldSet(destinationClass, FIELD_PREFIX, fields,
					fieldSetBuilderContext);
			context.setProperty(FIELD_SET_NAME, propertySet);
		}
		return context;
	}

	@Required
	public void setFieldSetBuilder(final FieldSetBuilder fieldSetBuilder)
	{
		this.fieldSetBuilder = fieldSetBuilder;
	}
}
