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
package de.hybris.platform.acceleratorservices.addonsupport;

import de.hybris.bootstrap.config.ExtensionInfo;
import de.hybris.platform.util.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;


public class RequiredAddOnsNameProvider
{

	private final Map<String, List<String>> extensionAddOns;

	public RequiredAddOnsNameProvider()
	{
		extensionAddOns = new HashMap<String, List<String>>();
	}

	public List<String> getAddOns(final String extensionName)
	{
		if (extensionAddOns.containsKey(extensionName))
		{
			return extensionAddOns.get(extensionName);
		}

		final List<String> dependentAddOns = getDependantAddOns(extensionName);
		extensionAddOns.put(extensionName, dependentAddOns);
		return dependentAddOns;
	}

	protected List<String> getDependantAddOns(final String extensionName)
	{
		if (StringUtils.isEmpty(extensionName))
		{
			return Collections.EMPTY_LIST;
		}
		final ExtensionInfo extensionInfo = Utilities.getExtensionInfo(extensionName);
		final Set<ExtensionInfo> allRequiredExtensionInfos = extensionInfo.getAllRequiredExtensionInfos();

		// Check if each required extension is an addon
		final Set<String> allAddOns = new HashSet<String>();
		for (final ExtensionInfo extension : allRequiredExtensionInfos)
		{
			if (isAddOnExtension(extension))
			{
				allAddOns.add(extension.getName());
			}
		}

		// Get the addon names in the correct order
		final List<String> addOnsInOrder = new ArrayList<String>();
		for (final String extName : Utilities.getExtensionNames())
		{
			if (allAddOns.contains(extName))
			{
				addOnsInOrder.add(extName);
			}
		}
		return addOnsInOrder;
	}

	protected boolean isAddOnExtension(final ExtensionInfo extensionInfo)
	{
		return new File(extensionInfo.getExtensionDirectory(), "acceleratoraddon").exists();
	}
}
