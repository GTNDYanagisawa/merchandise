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
package de.hybris.platform.sap.productconfig.frontend.validator;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConflictData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiValidationType;

import java.util.ArrayList;
import java.util.List;


public class ValidatorTestData
{

	/**
	 * @return empty Configuration
	 */
	public static ConfigurationData createEmptyConfigurationWithDefaultGroup()
	{
		final ConfigurationData configuration = new ConfigurationData();
		final List<UiGroupData> csticGroups = new ArrayList<>();
		final List<CsticData> cstics = new ArrayList<CsticData>();
		final UiGroupData defaultGroup = new UiGroupData();
		defaultGroup.setId("DEFAULT");
		defaultGroup.setCstics(cstics);
		defaultGroup.setGroupStatus(GroupStatusType.DEFAULT);
		csticGroups.add(defaultGroup);
		configuration.setGroups(csticGroups);
		return configuration;
	}

	public static UiGroupData createGroupWithNumeric(final String field, final String value)
	{
		return createGroupWithNumeric("DEFAULT", field, value);
	}

	public static UiGroupData createGroupWithNumeric(final String groupId, final String field, final String value)
	{
		final UiGroupData defaultGroup = new UiGroupData();
		defaultGroup.setId(groupId);
		defaultGroup.setGroupStatus(GroupStatusType.DEFAULT);
		final CsticData cstic = new CsticData();

		cstic.setType(UiType.NUMERIC);
		cstic.setValidationType(UiValidationType.NUMERIC);
		cstic.setValue(value);
		cstic.setName(field);
		cstic.setEntryFieldMask("-_____._____");
		cstic.setTypeLength(10);
		cstic.setNumberScale(5);

		final List<CsticData> cstics = new ArrayList<CsticData>();
		cstics.add(cstic);
		defaultGroup.setCstics(cstics);

		return defaultGroup;
	}

	public static ConfigurationData createConfigurationWithNumeric(final String field, final String value)
	{
		final ConfigurationData configuration = createEmptyConfigurationWithDefaultGroup();

		final List<UiGroupData> csticGroups = new ArrayList<>();
		csticGroups.add(createGroupWithNumeric(field, value));
		configuration.setGroups(csticGroups);

		return configuration;
	}

	public static ConfigurationData createConfigurationWithNumericInSubGroup(final String field, final String value)
	{
		final ConfigurationData configuration = createEmptyConfigurationWithDefaultGroup();

		final List<UiGroupData> csticGroups = new ArrayList<>();
		final UiGroupData defaultGroup = new UiGroupData();
		defaultGroup.setId("GROUPWITHSUBGROUP");
		defaultGroup.setGroupStatus(GroupStatusType.DEFAULT);
		csticGroups.add(defaultGroup);
		configuration.setGroups(csticGroups);

		final ArrayList<UiGroupData> subGroups = new ArrayList<>();
		subGroups.add(createGroupWithNumeric(field, value));
		defaultGroup.setSubGroups(subGroups);

		return configuration;
	}

	public static ConfigurationData createConfigurationWithConflict(final String conflictText)
	{
		final ConfigurationData configuration = createEmptyConfigurationWithDefaultGroup();
		final CsticData cstic = new CsticData();

		final List<ConflictData> conflicts = new ArrayList<ConflictData>();
		final ConflictData conflict = new ConflictData();
		conflict.setText(conflictText);
		conflicts.add(conflict);
		cstic.setConflicts(conflicts);

		final List<CsticData> cstics = new ArrayList<>();
		cstics.add(cstic);

		configuration.getGroups().get(0).setCstics(cstics);
		return configuration;
	}

}
