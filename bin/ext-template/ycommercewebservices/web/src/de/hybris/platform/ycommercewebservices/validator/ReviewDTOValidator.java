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

import static de.hybris.platform.customerreview.model.CustomerReviewModel.COMMENT;
import static de.hybris.platform.customerreview.model.CustomerReviewModel.HEADLINE;
import static de.hybris.platform.customerreview.model.CustomerReviewModel.RATING;

import de.hybris.platform.commercewebservicescommons.dto.product.ReviewWsDTO;

import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


public class ReviewDTOValidator implements Validator
{
	private final static double RATING_MIN = 1.0d;
	private final static double RATING_MAX = 5.0d;

	@Override
	public boolean supports(final Class clazz)
	{
		return ReviewWsDTO.class.equals(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		ValidationUtils.rejectIfEmpty(errors, HEADLINE, "field.required");
		ValidationUtils.rejectIfEmpty(errors, COMMENT, "field.required");
		validateRating(errors);
	}

	protected void validateRating(final Errors errors)
	{
		Assert.notNull(errors, "Errors object must not be null");
		final Double rating = (Double) errors.getFieldValue(RATING);

		if (rating == null)
		{
			errors.rejectValue(RATING, "field.required");
		}
		else
		{
			if (rating.doubleValue() < RATING_MIN || rating.doubleValue() > RATING_MAX)
			{
				errors.rejectValue(RATING, "review.rating.invalid");
			}
		}
	}

}
