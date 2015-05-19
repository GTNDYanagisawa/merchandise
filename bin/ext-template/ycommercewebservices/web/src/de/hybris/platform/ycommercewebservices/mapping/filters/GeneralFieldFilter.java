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
package de.hybris.platform.ycommercewebservices.mapping.filters;

import de.hybris.platform.commercewebservicescommons.mapping.FieldSelectionStrategy;

import ma.glasnost.orika.Filter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Property;
import ma.glasnost.orika.metadata.Type;

import org.springframework.beans.factory.annotation.Required;


/**
 * Filter that applies to any pair of types and attributes. It prevents from mapping unwanted fields.
 */
public class GeneralFieldFilter implements Filter<Object, Object>
{
	private FieldSelectionStrategy fieldSelectionStrategy;

	@Override
	public boolean appliesTo(final Property property, final Property property2)
	{
		return true;
	}

	@Override
	public <S extends Object, D extends Object> boolean shouldMap(final Type<S> sType, final String sourceName, final S source,
			final Type<D> dType, final String destName, final D dest, final MappingContext mappingContext)
	{
		return fieldSelectionStrategy.shouldMap(source, dest, mappingContext);
	}

	@Override
	public boolean filtersSource()
	{
		return false;
	}

	@Override
	public boolean filtersDestination()
	{
		return false;
	}

	@Override
	public <S extends Object> S filterSource(final S s, final Type<S> sType, final String s2, final Type<?> type, final String s3,
			final MappingContext mappingContext)
	{
		return null;
	}

	@Override
	public <D extends Object> D filterDestination(final D d, final Type<?> type, final String s, final Type<D> dType,
			final String s2, final MappingContext mappingContext)
	{
		return null;
	}

	@Override
	public Type<Object> getAType()
	{
		return null;
	}

	@Override
	public Type<Object> getBType()
	{
		return null;
	}

	@Required
	public void setFieldSelectionStrategy(final FieldSelectionStrategy fieldSelectionStrategy)
	{
		this.fieldSelectionStrategy = fieldSelectionStrategy;
	}
}
