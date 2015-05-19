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
package de.hybris.platform.commercewebservicescommons.mapping.mappers;

import de.hybris.platform.commercewebservicescommons.mapping.FieldSelectionStrategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class AbstractCustomMapper<A, B> extends CustomMapper<A, B>
{
	private FieldSelectionStrategy fieldSelectionStrategy;

	protected boolean shouldMap(final Object source, final Object dest, final MappingContext mappingContext)
	{
		return fieldSelectionStrategy.shouldMap(source, dest, mappingContext);
	}

	@Required
	public void setFieldSelectionStrategy(final FieldSelectionStrategy fieldSelectionStrategy)
	{
		this.fieldSelectionStrategy = fieldSelectionStrategy;
	}
}
