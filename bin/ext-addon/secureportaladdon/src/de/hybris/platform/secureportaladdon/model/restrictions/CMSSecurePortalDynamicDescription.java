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
package de.hybris.platform.secureportaladdon.model.restrictions;

import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import de.hybris.platform.util.localization.Localization;


public class CMSSecurePortalDynamicDescription implements DynamicAttributeHandler<String, CMSSecurePortalRestrictionModel>
{

	@Override
	public String get(final CMSSecurePortalRestrictionModel model)
	{

		final StringBuilder result = new StringBuilder();
		{
			final String localizedString = Localization.getLocalizedString("type.CMSSecurePortalRestriction.description.text");
			result.append(localizedString == null ? "CMS Action only applies to the applicable cms components" : localizedString);

		}

		return result.toString();
	}

	@Override
	public void set(final CMSSecurePortalRestrictionModel model, final String value)
	{
		throw new UnsupportedOperationException();
	}
}
