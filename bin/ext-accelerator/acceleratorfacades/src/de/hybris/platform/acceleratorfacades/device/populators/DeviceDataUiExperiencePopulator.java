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
package de.hybris.platform.acceleratorfacades.device.populators;

import de.hybris.platform.acceleratorfacades.device.data.DeviceData;
import de.hybris.platform.acceleratorfacades.device.data.UiExperienceData;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang.BooleanUtils;

/**
 * Populator that maps from a DeviceData to a UiExperienceLevel enum value.
 */
public class DeviceDataUiExperiencePopulator implements Populator<DeviceData, UiExperienceData>
{
	@Override
	public void populate(final DeviceData deviceData, final UiExperienceData uiExperienceData) throws ConversionException
	{
		if (BooleanUtils.toBoolean(deviceData.getDesktopBrowser()))
		{
			uiExperienceData.setLevel(UiExperienceLevel.DESKTOP);
		}
		else if (BooleanUtils.toBoolean(deviceData.getMobileBrowser()))
		{
			uiExperienceData.setLevel(UiExperienceLevel.MOBILE);
		}
		else if (BooleanUtils.toBoolean(deviceData.getTabletBrowser()))
		{
			uiExperienceData.setLevel(UiExperienceLevel.DESKTOP);
		}
		else
		{
			// Default to the DESKTOP
			uiExperienceData.setLevel(UiExperienceLevel.DESKTOP);
		}
	}
}
