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
package de.hybris.platform.acceleratorcms.model;

import de.hybris.platform.acceleratorcms.model.restrictions.CMSUiExperienceRestrictionModel;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.util.localization.Localization;

import org.springframework.beans.factory.annotation.Required;


/**
 */
public class UiExperienceRestrictionDynamicDescription implements
		DynamicAttributeHandler<String, CMSUiExperienceRestrictionModel>
{
	private TypeService typeService;

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	@Override
	public String get(final CMSUiExperienceRestrictionModel model)
	{
		final UiExperienceLevel uiExperience = model.getUiExperience();

		final StringBuilder result = new StringBuilder();
		if (uiExperience != null)
		{
			final EnumerationValueModel enumerationValue = getTypeService().getEnumerationValue(uiExperience);
			if (enumerationValue != null)
			{
				final String localizedString = Localization.getLocalizedString("type.CMSUiExperienceRestriction.description.text");
				result.append(localizedString == null ? "Page only applies on experience level:" : localizedString);
				result.append(' ').append(enumerationValue.getName());
			}
		}

		return result.toString();
	}

	@Override
	public void set(final CMSUiExperienceRestrictionModel model, final String value)
	{
		throw new UnsupportedOperationException();
	}
}
