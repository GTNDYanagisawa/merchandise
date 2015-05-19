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
package de.hybris.platform.commercefacades.user.converters.populator;

import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalModel;

import org.springframework.util.Assert;


/**
 * Convert a PrincipalModel to a PrincipalData
 */
public class PrincipalPopulator implements Populator<PrincipalModel, PrincipalData>
{

	@Override
	public void populate(final PrincipalModel source, final PrincipalData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setUid(source.getUid());
		target.setName(source.getDisplayName());
	}
}
