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
package de.hybris.platform.ycommercewebservices.validator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ReviewData;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component("productValidator")
public class ProductDataValidator implements Validator
{
	private Validator reviewValidator;

	@Override
	public boolean supports(final Class clazz)
	{
		return ProductData.class.equals(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final ProductData product = (ProductData) target;

		ValidationUtils.rejectIfEmpty(errors, "code", "field.required");

		try
		{
			errors.pushNestedPath("reviews");
			if (product.getReviews() != null)
			{
				for (final ReviewData review : product.getReviews())
				{
					ValidationUtils.invokeValidator(this.reviewValidator, review, errors);
				}
			}
		}
		finally
		{
			errors.popNestedPath();
		}
	}

	@Resource(name = "reviewValidator")
	public void setReviewValidator(final ReviewDataValidator validator)
	{
		if (validator == null)
		{
			throw new IllegalArgumentException("The supplied ReviewValidator is required and must not be null.");
		}

		if (!validator.supports(ReviewData.class))
		{
			throw new IllegalArgumentException("The supplied ReviewValidator must support the validation of BrandData instances.");
		}

		this.reviewValidator = validator;
	}
}
