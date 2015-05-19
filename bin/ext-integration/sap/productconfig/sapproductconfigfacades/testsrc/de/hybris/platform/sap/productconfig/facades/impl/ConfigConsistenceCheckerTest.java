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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigConsistenceChecker;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ConfigConsistenceCheckerTest
{
	private ConfigConsistenceChecker configChecker;

	@Before
	public void setup()
	{
		configChecker = new ConfigConsistenceCheckerImpl();
	}

	@Test
	public void testSingleRadioButtonConsistence()
	{
		final CsticData cstic = createRadioButtinCstic();


		final ConfigurationData configData = new ConfigurationData();
		configData.setKbKey(new KBKeyData());
		final List<CsticData> cstics = new ArrayList<>();
		cstics.add(cstic);

		final List<UiGroupData> csticGroups = new ArrayList<>();
		final UiGroupData defaultGroup = new UiGroupData();
		defaultGroup.setId("DEFAULT");
		csticGroups.add(defaultGroup);
		configData.setGroups(csticGroups);
		defaultGroup.setCstics(cstics);

		final ConfigurationData checkedConfigData = configChecker.checkConfiguration(configData);


		assertNotNull("No ConfigurationData found", checkedConfigData);
		assertNotNull("No CsticData found", checkedConfigData.getGroups().get(0).getCstics());

		final CsticData checkedCstic = checkedConfigData.getGroups().get(0).getCstics().get(0);
		assertEquals(UiType.READ_ONLY, checkedCstic.getType());
	}

	@Test
	public void testSingleRadioButtonConsistenceOnSubInstance()
	{
		final ConfigurationData configData = new ConfigurationData();
		configData.setKbKey(new KBKeyData());

		final CsticData cstic = createRadioButtinCstic();
		final List<CsticData> cstics = new ArrayList<>();
		cstics.add(cstic);

		final List<UiGroupData> subGroups = new ArrayList<>();
		final UiGroupData subGroup = new UiGroupData();
		subGroup.setId("XYZ");
		subGroup.setCstics(cstics);
		subGroups.add(subGroup);

		final List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData defaultGroup = new UiGroupData();
		defaultGroup.setId("DEFAULT");
		defaultGroup.setCstics(new ArrayList<CsticData>());
		defaultGroup.setSubGroups(subGroups);
		groups.add(defaultGroup);
		configData.setGroups(groups);



		final ConfigurationData checkedConfigData = configChecker.checkConfiguration(configData);


		assertNotNull("No ConfigurationData found", checkedConfigData);
		assertNotNull("No CsticData found", checkedConfigData.getGroups().get(0).getCstics());

		final CsticData checkedCstic = checkedConfigData.getGroups().get(0).getSubGroups().get(0).getCstics().get(0);
		assertEquals(UiType.READ_ONLY, checkedCstic.getType());
	}

	/**
	 * @return
	 */
	protected CsticData createRadioButtinCstic()
	{
		final CsticData cstic = new CsticData();
		cstic.setType(UiType.RADIO_BUTTON);

		final List<CsticValueData> domainvalues = new ArrayList<>();
		final CsticValueData csticValue1 = createCsticValue("Value 1", true);
		domainvalues.add(csticValue1);
		cstic.setDomainvalues(domainvalues);
		return cstic;
	}

	@Test
	public void testTwoRadioButtonConsistence()
	{
		final CsticData cstic = new CsticData();
		cstic.setType(UiType.RADIO_BUTTON);

		final List<CsticValueData> domainvalues = new ArrayList<>();
		final CsticValueData csticValue1 = createCsticValue("Value 1", true);
		final CsticValueData csticValue2 = createCsticValue("Value 2", false);
		domainvalues.add(csticValue1);
		domainvalues.add(csticValue2);
		cstic.setDomainvalues(domainvalues);

		final ConfigurationData configData = new ConfigurationData();
		configData.setKbKey(new KBKeyData());
		final List<CsticData> cstics = new ArrayList<>();
		cstics.add(cstic);

		final List<UiGroupData> csticGroups = new ArrayList<>();
		final UiGroupData defaultGroup = new UiGroupData();
		defaultGroup.setId("DEFAULT");
		csticGroups.add(defaultGroup);
		configData.setGroups(csticGroups);
		defaultGroup.setCstics(cstics);


		final ConfigurationData checkedConfigData = configChecker.checkConfiguration(configData);


		assertNotNull("No ConfigurationData found", checkedConfigData);
		assertNotNull("No CsticData found", checkedConfigData.getGroups().get(0).getCstics());

		final CsticData checkedCstic = checkedConfigData.getGroups().get(0).getCstics().get(0);
		assertEquals(UiType.RADIO_BUTTON, checkedCstic.getType());
	}

	private CsticValueData createCsticValue(final String name, final boolean selected)
	{
		final CsticValueData csticValue1 = new CsticValueData();
		csticValue1.setName(name);
		csticValue1.setSelected(selected);
		return csticValue1;
	}
}
