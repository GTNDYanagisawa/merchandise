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
package de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider;

import de.hybris.platform.b2b.model.GenericVariantProductModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Utility class for the Product Value Providers.
 */
public abstract class AbstractMultidimensionalProductFieldValueProvider extends AbstractPropertyFieldValueProvider implements
		FieldValueProvider
{
	private FieldNameProvider fieldNameProvider;

	protected abstract Object getFieldValue(final ProductModel product);

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		if (model instanceof ProductModel)
		{
			final Object field = getFieldValue((ProductModel) model);
			addFieldValues(fieldValues, indexedProperty, field);
		}
		else
		{
			throw new FieldValueProviderException("Cannot get field for non-product item");
		}

		return fieldValues;
	}

	/**
	 * Add a value in the list of field values. It will add one entry for each {@link FieldValue} related to the given
	 * Solr property.
	 * 
	 * @param fieldValues
	 *           The list of field values.
	 * @param indexedProperty
	 *           The property used on Solr.
	 * @param value
	 *           The value to be added.
	 */
	protected void addFieldValues(final List<FieldValue> fieldValues, final IndexedProperty indexedProperty, final Object value)
	{
		if (value != null)
		{
			final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, null);
			for (final String fieldName : fieldNames)
			{
				fieldValues.add(new FieldValue(fieldName, value));
			}
		}
	}

	/**
	 * For a given product, retrieve its base product.
	 * 
	 * @param model
	 *           The product.
	 * @return The base product, if it is a variant product, or the model, if it is already the base product or is not a
	 *         variant product.
	 */
	public ProductModel getBaseProduct(final ProductModel model)
	{
		final ProductModel baseProduct;
		if (model instanceof VariantProductModel)
		{
			final VariantProductModel variant = (VariantProductModel) model;
			baseProduct = variant.getBaseProduct();
		}
		else
		{
			baseProduct = model;
		}
		return baseProduct;
	}

	/**
	 * For a given product, finds out if it is a base product of {@link GenericVariantProductModel} products.
	 * 
	 * @param model
	 *           The product.
	 * @return True if it is a base product that has generic variants.
	 */
	public boolean isVariantBaseProduct(final ProductModel model)
	{
		boolean isBase = false;
		final Collection<VariantProductModel> variants = model.getVariants();
		if (CollectionUtils.isNotEmpty(variants))
		{
			for (final VariantProductModel variant : variants)
			{
				if (variant instanceof GenericVariantProductModel)
				{
					isBase = true;
					break;
				}
			}
		}
		return isBase;
	}

	protected FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	@Required
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}
}
