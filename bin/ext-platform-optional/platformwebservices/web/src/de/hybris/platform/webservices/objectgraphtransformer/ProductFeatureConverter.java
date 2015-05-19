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
package de.hybris.platform.webservices.objectgraphtransformer;

import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyInterceptor;


/**
 * This converter is a "trick". It does NO conversion, it passes the value through unchanged. It exists only for it's
 * side-effects: it extracts some information from the model (indirect references) and then injects them into properties
 * of the DTO. Default mechanisms could not be used, because currently only something like this is supported:
 * dto.setA(model.getA()), and here we have something like: dto.setA(model.getB().getC().getD())
 */
public class ProductFeatureConverter implements PropertyInterceptor<Object, String>
{

	@Override
	public String intercept(final PropertyContext ctx, final Object source)
	{
		final ProductFeatureModel model = (ProductFeatureModel) ctx.getParentContext().getSourceNodeValue();

		// this converter acts as property-factory 
		// either for property 'code' or 'name'
		final String propName = ctx.getPropertyMapping().getId();

		// for untyped features we assign some default valeu here
		String result = model.getQualifier();

		// for typed features we can take the concrete value
		if (model.getClassificationAttributeAssignment() != null)
		{
			final ClassificationAttributeModel classAttr = model.getClassificationAttributeAssignment().getClassificationAttribute();
			result = "code".equals(propName) ? classAttr.getCode() : classAttr.getName();
		}
		return result;
	}

}
