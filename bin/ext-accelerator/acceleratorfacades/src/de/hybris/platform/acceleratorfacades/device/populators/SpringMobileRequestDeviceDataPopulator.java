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
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceResolver;


public class SpringMobileRequestDeviceDataPopulator implements Populator<HttpServletRequest, DeviceData>
{

	private DeviceResolver deviceResolver;

	@Override
	public void populate(final HttpServletRequest source, final DeviceData target) throws ConversionException
	{
		final Device device = deviceResolver.resolveDevice(source);

		target.setUserAgent(source.getHeader("User-Agent"));
		target.setDesktopBrowser(Boolean.valueOf(device.isNormal()));
		target.setTabletBrowser(Boolean.valueOf(device.isTablet()));
		target.setMobileBrowser(Boolean.valueOf(device.isMobile()));
	}

	public String toStringDeviceData(final DeviceData device)
	{
		final StringBuilder builder = new StringBuilder(73);
		builder.append("[DeviceData ");
		builder.append("id").append('=').append(device.getId()).append(", ");
		builder.append("userAgent").append('=').append(device.getUserAgent()).append(", ");
		builder.append("capabilities").append('=').append(device.getCapabilities()).append(", ");
		builder.append("desktop").append('=').append(device.getDesktopBrowser()).append(", ");
		builder.append("mobile").append('=').append(device.getMobileBrowser()).append(", ");
		builder.append("tablet").append('=').append(device.getTabletBrowser()).append(", ");
		builder.append(']');
		return builder.toString();
	}

	public DeviceResolver getDeviceResolver()
	{
		return deviceResolver;
	}

	@Required
	public void setDeviceResolver(final DeviceResolver deviceResolver)
	{
		this.deviceResolver = deviceResolver;
	}
}
