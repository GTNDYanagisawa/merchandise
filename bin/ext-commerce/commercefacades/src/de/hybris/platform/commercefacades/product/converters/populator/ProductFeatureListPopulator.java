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
package de.hybris.platform.commercefacades.product.converters.populator;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.commercefacades.product.data.ClassificationData;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populate the classifications of the product data from a feature list
 */
public class ProductFeatureListPopulator<SOURCE extends FeatureList, TARGET extends ProductData> implements
		Populator<SOURCE, TARGET>
{
	private Converter<ClassificationClassModel, ClassificationData> classificationConverter;
	private Converter<Feature, FeatureData> featureConverter;

	protected Converter<ClassificationClassModel, ClassificationData> getClassificationConverter()
	{
		return classificationConverter;
	}

	@Required
	public void setClassificationConverter(final Converter<ClassificationClassModel, ClassificationData> classificationConverter)
	{
		this.classificationConverter = classificationConverter;
	}

	protected Converter<Feature, FeatureData> getFeatureConverter()
	{
		return featureConverter;
	}

	@Required
	public void setFeatureConverter(final Converter<Feature, FeatureData> featureConverter)
	{
		this.featureConverter = featureConverter;
	}

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		target.setClassifications(buildClassificationDataList(source));
	}

	protected List<ClassificationData> buildClassificationDataList(final FeatureList source)
	{
		final List<ClassificationData> result = new ArrayList<ClassificationData>();
		final Map<String, ClassificationData> map = new HashMap<String, ClassificationData>();

		for (final Feature feature : source.getFeatures())
		{
			if (feature.getValues() != null && !feature.getValues().isEmpty())
			{
				final ClassificationData classificationData;
				final ClassificationClassModel classificationClass = feature.getClassAttributeAssignment().getClassificationClass();
				final String classificationClassCode = classificationClass.getCode();
				if (map.containsKey(classificationClassCode))
				{
					classificationData = map.get(classificationClassCode);
				}
				else
				{
					classificationData = classificationConverter.convert(classificationClass);

					map.put(classificationClassCode, classificationData);
					result.add(classificationData);
				}

				// Create the feature
				final FeatureData newFeature = getFeatureConverter().convert(feature);

				// Add the feature to the classification
				if (classificationData.getFeatures() == null)
				{
					classificationData.setFeatures(new ArrayList<FeatureData>(1));
				}
				classificationData.getFeatures().add(newFeature);
			}
		}

		return result.isEmpty() ? null : result;
	}
}
