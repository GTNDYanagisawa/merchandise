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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.sap.productconfig.facades.ConfigConsistenceChecker;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;

import java.util.List;

import org.apache.log4j.Logger;


public class ConfigConsistenceCheckerImpl implements ConfigConsistenceChecker
{
	private static final Logger LOG = Logger.getLogger(ConfigConsistenceCheckerImpl.class);

	@Override
	public ConfigurationData checkConfiguration(final ConfigurationData configData)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("do consistence check for product '" + configData.getKbKey().getProductCode() + "' with configID '"
					+ configData.getConfigId() + "'");
		}
		final List<UiGroupData> csticGroups = configData.getGroups();

		for (final UiGroupData csticGroup : csticGroups)
		{
			checkGroup(csticGroup);
		}

		return configData;
	}

	private void checkGroup(final UiGroupData group)
	{
		final List<CsticData> cstics = group.getCstics();

		for (final CsticData cstic : cstics)
		{
			checkCstic(cstic);
		}

		final List<UiGroupData> subGroups = group.getSubGroups();
		if (subGroups == null || subGroups.isEmpty())
		{
			return;
		}
		for (final UiGroupData subGroup : subGroups)
		{
			checkGroup(subGroup);
		}

	}

	private void checkCstic(final CsticData cstic)
	{
		switch (cstic.getType())
		{
			case RADIO_BUTTON:
				checkRadioButtonConsistence(cstic);
				break;
			default:
				break;
		}

	}

	private void checkRadioButtonConsistence(final CsticData cstic)
	{
		final List<CsticValueData> domainValues = cstic.getDomainvalues();
		final int valueCount = domainValues.size();
		if (valueCount != 1)
		{
			return;
		}

		final CsticValueData singleValue = domainValues.get(0);
		if (!singleValue.isSelected())
		{
			return;
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("changed radio button type to readOnly for cstic '" + cstic.getName() + "'");
		}
		cstic.setType(UiType.READ_ONLY);
	}
}
