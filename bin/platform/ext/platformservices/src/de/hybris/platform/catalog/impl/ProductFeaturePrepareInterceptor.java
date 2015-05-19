/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.catalog.impl;

import de.hybris.platform.catalog.ClassificationUtils;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;


/**
 * PrepareInterceptor for the {@link ProductFeatureModel}. Sets the {@link ProductFeatureModel#QUALIFIER} if a
 * {@link ClassAttributeAssignmentModel} on the ProductFeatureModel exists.
 */
public class ProductFeaturePrepareInterceptor implements PrepareInterceptor
{
	@Override
	public void onPrepare(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof ProductFeatureModel)
		{
			final ProductFeatureModel pfm = (ProductFeatureModel) model;
			if (pfm.getQualifier() == null && pfm.getClassificationAttributeAssignment() != null)
			{
				pfm.setQualifier(ClassificationUtils.createFeatureQualifier(pfm.getClassificationAttributeAssignment()));
			}
		}
	}
}
