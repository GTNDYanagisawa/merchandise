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
package de.hybris.platform.acceleratorfacades.device.impl;

import de.hybris.platform.acceleratorfacades.device.DeviceDetectionFacade;
import de.hybris.platform.acceleratorfacades.device.data.DeviceData;
import de.hybris.platform.acceleratorfacades.device.data.UiExperienceData;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserConstants;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the DeviceDetectionFacade
 */
public class DefaultDeviceDetectionFacade implements DeviceDetectionFacade
{
	protected static final String DETECTED_DEVICE = "DeviceDetectionFacade-Detected-Device";
	public static final String DEVICE_DETECTION_UIEXPERIENCE_LEVEL_SUPPORTED = "uiexperience.level.supported";

	private static final Logger LOG = Logger.getLogger(DefaultDeviceDetectionFacade.class.getName());

	private Converter<HttpServletRequest, DeviceData> requestDeviceDataConverter;
	private Converter<DeviceData, UiExperienceData> deviceDataUiExperienceDataConverter;
	private SessionService sessionService;
	private UiExperienceService uiExperienceService;
	private SiteConfigService siteConfigService;

	@Override
	public void initializeRequest(final HttpServletRequest request)
	{
		// Only initialise the detected device once per session
		if (getCurrentDetectedDevice() == null || "true".equals(request.getParameter("clear")))
		{
			// Detect the device in the current request
			final DeviceData deviceData = getRequestDeviceDataConverter().convert(request);
			setCurrentDetectedDevice(deviceData);

			// Map the detected device to a UiExperienceLevel
			final UiExperienceData uiExperienceData = getDeviceDataUiExperienceDataConverter().convert(deviceData);
			final List<String> supportedUiExperienceLevels = Arrays.asList(StringUtils.split(
					getSiteConfigService().getString(DEVICE_DETECTION_UIEXPERIENCE_LEVEL_SUPPORTED, StringUtils.EMPTY), ","));

			if (uiExperienceData != null
					&& uiExperienceData.getLevel() != null
					&& (supportedUiExperienceLevels.isEmpty() || supportedUiExperienceLevels.contains(uiExperienceData.getLevel()
							.getCode())))
			{
				getUiExperienceService().setDetectedUiExperienceLevel(uiExperienceData.getLevel());
			}
			else
			{
				// Default to DESKTOP experience or the first supportUi
				UiExperienceLevel defaultExperience = UiExperienceLevel.DESKTOP;
				try
				{
					if (!supportedUiExperienceLevels.isEmpty())
					{
						defaultExperience = UiExperienceLevel.valueOf(supportedUiExperienceLevels.get(0));
					}
				}
				catch (IllegalArgumentException e)
				{
					LOG.warn(String.format("Invalid UiExperienceLevel enum %s will default to 'Desktop'",
							supportedUiExperienceLevels.isEmpty() ? "" : supportedUiExperienceLevels.get(0)));
				}
				getUiExperienceService().setDetectedUiExperienceLevel(defaultExperience);
			}

			if (LOG.isDebugEnabled())
			{
				final UserModel userModel = (UserModel) getSessionService().getAttribute(UserConstants.USER_SESSION_ATTR_KEY);
				final String userUid = (userModel != null) ? userModel.getUid() : "<null>";

				LOG.debug("Detected device [" + deviceData.getId() + "] User Agent [" + deviceData.getUserAgent() + "] Mobile ["
						+ deviceData.getMobileBrowser() + "] Session user [" + userUid + "]");
			}
		}
	}

	@Override
	public DeviceData getCurrentDetectedDevice()
	{
		return getSessionService().getAttribute(DETECTED_DEVICE);
	}

	protected void setCurrentDetectedDevice(final DeviceData deviceData)
	{
		getSessionService().setAttribute(DETECTED_DEVICE, deviceData);
	}

	protected Converter<HttpServletRequest, DeviceData> getRequestDeviceDataConverter()
	{
		return requestDeviceDataConverter;
	}

	@Required
	public void setRequestDeviceDataConverter(final Converter<HttpServletRequest, DeviceData> requestDeviceDataConverter)
	{
		this.requestDeviceDataConverter = requestDeviceDataConverter;
	}

	protected Converter<DeviceData, UiExperienceData> getDeviceDataUiExperienceDataConverter()
	{
		return deviceDataUiExperienceDataConverter;
	}

	@Required
	public void setDeviceDataUiExperienceDataConverter(
			final Converter<DeviceData, UiExperienceData> deviceDataUiExperienceDataConverter)
	{
		this.deviceDataUiExperienceDataConverter = deviceDataUiExperienceDataConverter;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected UiExperienceService getUiExperienceService()
	{
		return uiExperienceService;
	}

	@Required
	public void setUiExperienceService(final UiExperienceService uiExperienceService)
	{
		this.uiExperienceService = uiExperienceService;
	}

	protected SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	@Required
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}
}
