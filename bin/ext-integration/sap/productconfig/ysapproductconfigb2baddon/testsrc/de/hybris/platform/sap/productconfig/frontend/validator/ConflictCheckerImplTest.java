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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.UiType;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;


public class ConflictCheckerImplTest
{

	@Mock
	private BindingResult bindingResult;

	private ConflictChecker checker;

	@Before
	public void setup()
	{
		checker = new ConflictCheckerImpl();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testNoConflicts()
	{
		final ConfigurationData config = ValidatorTestData.createEmptyConfigurationWithDefaultGroup();

		checker.checkConflicts(config, bindingResult);

		Mockito.verifyZeroInteractions(bindingResult);
		assertEquals(GroupStatusType.DEFAULT, config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testWithConflictWithText()
	{
		final ConfigurationData config = ValidatorTestData.createConfigurationWithConflict("a conflict");

		checker.checkConflicts(config, bindingResult);


		Mockito.verify(bindingResult, times(1)).addError(Mockito.any(ConflictError.class));
		assertEquals(GroupStatusType.WARNING, config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testDoNotOverwriteErrorStatusOnGroup()
	{
		final ConfigurationData config = ValidatorTestData.createConfigurationWithConflict("a conflict");
		config.getGroups().get(0).setGroupStatus(GroupStatusType.ERROR);

		checker.checkConflicts(config, bindingResult);

		assertEquals(GroupStatusType.ERROR, config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testWithConflictWithoutText()
	{
		final ConfigurationData config = ValidatorTestData.createConfigurationWithConflict(null);

		checker.checkConflicts(config, bindingResult);


		Mockito.verify(bindingResult, times(1)).addError(Mockito.any(ConflictError.class));
	}

	@Test
	public void testEquals()
	{
		final ConflictError error1 = new ConflictError(null, "Field", null, null, null);
		assertFalse(error1.equals(null));
		assertFalse(error1.equals("TEST"));
		assertTrue(error1.equals(error1));

		ConflictError error2 = new ConflictError(null, "Field", null, null, null);

		assertTrue(error1.equals(error2));
		error2 = new ConflictError(new CsticData(), "Field", null, null, null);
		assertFalse(error1.equals(error2));
		assertFalse(error2.equals(error1));
	}

	@Test
	public void testMandatoryFields()
	{
		final ConfigurationData config = createMandatoryFieldConfiguration();

		checker.checkMandatoryFields(config, bindingResult);

		Mockito.verify(bindingResult, times(3)).addError(Mockito.any(MandatoryFieldError.class));
		assertEquals(GroupStatusType.WARNING, config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testMandatoryFieldsMultipleGroups()
	{
		final ConfigurationData config = createMandatoryFieldConfiguration();
		config.getGroups().add(ValidatorTestData.createGroupWithNumeric("2", "abc", "123"));

		checker.checkMandatoryFields(config, bindingResult);

		Mockito.verify(bindingResult, times(3)).addError(Mockito.any(MandatoryFieldError.class));
		assertEquals(GroupStatusType.WARNING, config.getGroups().get(0).getGroupStatus());
		assertEquals(GroupStatusType.DEFAULT, config.getGroups().get(1).getGroupStatus());
	}

	@Test
	public void testMandatoryFieldsMultipleGroupsDoNotOverwriteError()
	{
		final ConfigurationData config = createMandatoryFieldConfiguration();
		config.getGroups().add(ValidatorTestData.createGroupWithNumeric("2", "abc", "123"));
		config.getGroups().get(0).setGroupStatus(GroupStatusType.ERROR);

		checker.checkMandatoryFields(config, bindingResult);

		Mockito.verify(bindingResult, times(3)).addError(Mockito.any(MandatoryFieldError.class));
		assertEquals(GroupStatusType.ERROR, config.getGroups().get(0).getGroupStatus());
		assertEquals(GroupStatusType.DEFAULT, config.getGroups().get(1).getGroupStatus());
	}

	@Test
	public void testNoMandatoryFields()
	{
		final ConfigurationData config = ValidatorTestData.createConfigurationWithConflict(null);

		checker.checkMandatoryFields(config, bindingResult);

		Mockito.verify(bindingResult, times(0)).addError(Mockito.any(MandatoryFieldError.class));
		assertEquals(GroupStatusType.DEFAULT, config.getGroups().get(0).getGroupStatus());
	}

	private ConfigurationData createMandatoryFieldConfiguration()
	{
		final ConfigurationData config = ValidatorTestData.createEmptyConfigurationWithDefaultGroup();
		final UiGroupData group = config.getGroups().get(0);

		final CsticData checkBoxListCstic = new CsticData();
		checkBoxListCstic.setRequired(true);
		checkBoxListCstic.setType(UiType.CHECK_BOX_LIST);
		final List<CsticValueData> domainvalues = new ArrayList<>();
		checkBoxListCstic.setDomainvalues(domainvalues);
		group.getCstics().add(checkBoxListCstic);

		final CsticData stringCstic = new CsticData();
		stringCstic.setType(UiType.STRING);
		stringCstic.setRequired(true);
		group.getCstics().add(stringCstic);

		final CsticData dropDownCstic = new CsticData();
		dropDownCstic.setRequired(true);
		dropDownCstic.setType(UiType.DROPDOWN);
		dropDownCstic.setDomainvalues(domainvalues);
		group.getCstics().add(dropDownCstic);
		return config;
	}
}
