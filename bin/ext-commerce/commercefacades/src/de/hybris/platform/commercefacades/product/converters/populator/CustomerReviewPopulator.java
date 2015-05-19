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

import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.customerreview.model.CustomerReviewModel} as source and
 * {@link de.hybris.platform.commercefacades.product.data.ReviewData} as target type.
 */
public class CustomerReviewPopulator implements Populator<CustomerReviewModel, ReviewData>
{
	private Converter<PrincipalModel, PrincipalData> principalConverter;

	protected Converter<PrincipalModel, PrincipalData> getPrincipalConverter()
	{
		return principalConverter;
	}

	@Required
	public void setPrincipalConverter(final Converter<PrincipalModel, PrincipalData> principalConverter)
	{
		this.principalConverter = principalConverter;
	}

	@Override
	public void populate(final CustomerReviewModel source, final ReviewData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setId(source.getPk().getLongValueAsString());
		target.setComment(source.getComment());
		target.setDate(source.getCreationtime());
		target.setHeadline(source.getHeadline());
		target.setRating(source.getRating());
		target.setAlias(source.getAlias());

		target.setPrincipal(getPrincipalConverter().convert(source.getUser()));
	}
}
