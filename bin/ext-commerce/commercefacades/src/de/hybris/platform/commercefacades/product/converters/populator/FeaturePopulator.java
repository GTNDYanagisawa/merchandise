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

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.FeatureUnitData;
import de.hybris.platform.commercefacades.product.data.FeatureValueData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;


/**
 */
public class FeaturePopulator extends AbstractPopulatingConverter<Feature, FeatureData>
{
	@Override
	protected FeatureData createTarget()
	{
		return new FeatureData();
	}

	@Override
	public void populate(final Feature source, final FeatureData target)
	{
		final ClassAttributeAssignmentModel classAttributeAssignment = source.getClassAttributeAssignment();

		// Create the feature
		target.setCode(source.getCode());
		target.setComparable(Boolean.TRUE.equals(classAttributeAssignment.getComparable()));
		target.setDescription(classAttributeAssignment.getDescription());
		target.setName(source.getName());
		target.setRange(Boolean.TRUE.equals(classAttributeAssignment.getRange()));

		final ClassificationAttributeUnitModel unit = classAttributeAssignment.getUnit();
		if (unit != null)
		{
			final FeatureUnitData featureUnitData = new FeatureUnitData();
			featureUnitData.setName(unit.getName());
			featureUnitData.setSymbol(unit.getSymbol());
			featureUnitData.setUnitType(unit.getUnitType());
			target.setFeatureUnit(featureUnitData);
		}

		// Create the feature data items
		final List<FeatureValueData> featureValueDataList = new ArrayList<FeatureValueData>();
		for (final FeatureValue featureValue : source.getValues())
		{
			final FeatureValueData featureValueData = new FeatureValueData();
			final Object value = featureValue.getValue();
			if (value instanceof ClassificationAttributeValueModel)
			{
				featureValueData.setValue(((ClassificationAttributeValueModel) value).getName());
			}
			else if (NumberUtils.isNumber(String.valueOf(value)))
			{
				featureValueData.setValue(String.valueOf(value).replaceAll("\\.0*$", ""));
			}
			else
			{
				featureValueData.setValue(String.valueOf(value));
			}

			featureValueDataList.add(featureValueData);
		}
		target.setFeatureValues(featureValueDataList);

		super.populate(source, target);
	}
}
