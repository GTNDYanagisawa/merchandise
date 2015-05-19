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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.UiType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Errors;


@UnitTest
public class ProductConfigurationValidatorTest
{
	private final ProductConfigurationValidator validator = new ProductConfigurationValidator();

	@Mock
	private Errors errorObj;

	@Mock
	private NumericChecker numericChecker;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		validator.setNumericChecker(numericChecker);
	}

	@Test
	public void notSupportingString() throws Exception
	{
		final boolean supports = validator.supports(String.class);
		assertFalse("Should not support everything", supports);
	}

	@Test
	public void supportingConfigurationData() throws Exception
	{
		final boolean supports = validator.supports(ConfigurationData.class);
		assertTrue("Must support ConfigurationData", supports);
	}

	@Test
	public void testEmptyConfiguration() throws Exception
	{
		final ConfigurationData configuration = ValidatorTestData.createEmptyConfigurationWithDefaultGroup();
		validator.validate(configuration, errorObj);
	}


	@Test
	public void tesValidateNumeric() throws Exception
	{
		final String fieldName = "numeric";
		final ConfigurationData configuration = ValidatorTestData.createConfigurationWithNumeric(fieldName, "123");

		validator.validate(configuration, errorObj);

		final CsticData cstic = configuration.getGroups().get(0).getCstics().get(0);
		Mockito.verify(numericChecker, Mockito.times(1)).validate(cstic, errorObj);
		Mockito.verify(numericChecker, Mockito.times(0)).validateAdditionalValue(cstic, errorObj);
	}

	@Test
	public void tesValidateNumeric_additionalEmpty() throws Exception
	{
		final String fieldName = "numeric";
		final ConfigurationData configuration = ValidatorTestData.createConfigurationWithNumeric(fieldName, "123");
		final CsticData csticData = configuration.getGroups().get(0).getCstics().get(0);
		csticData.setType(UiType.RADIO_BUTTON_ADDITIONAL_INPUT);
		csticData.setAdditionalValue("");

		validator.validate(configuration, errorObj);

		final CsticData cstic = csticData;
		Mockito.verify(numericChecker, Mockito.times(0)).validate(cstic, errorObj);
		Mockito.verify(numericChecker, Mockito.times(0)).validateAdditionalValue(cstic, errorObj);
	}

	@Test
	public void tesValidateNumeric_additional() throws Exception
	{
		final String fieldName = "numeric";
		final ConfigurationData configuration = ValidatorTestData.createConfigurationWithNumeric(fieldName, "123");
		final CsticData csticData = configuration.getGroups().get(0).getCstics().get(0);
		csticData.setType(UiType.RADIO_BUTTON_ADDITIONAL_INPUT);
		csticData.setAdditionalValue("123456");

		validator.validate(configuration, errorObj);

		final CsticData cstic = csticData;
		Mockito.verify(numericChecker, Mockito.times(0)).validate(cstic, errorObj);
		Mockito.verify(numericChecker, Mockito.times(1)).validateAdditionalValue(cstic, errorObj);
	}


	@Test
	public void tesValidateNumericInSubGroup() throws Exception
	{
		final String fieldName = "numeric";
		final ConfigurationData configuration = ValidatorTestData.createConfigurationWithNumericInSubGroup(fieldName, "123");

		validator.validate(configuration, errorObj);

		final CsticData cstic = configuration.getGroups().get(0).getSubGroups().get(0).getCstics().get(0);
		Mockito.verify(numericChecker, Mockito.times(1)).validate(cstic, errorObj);
		Mockito.verify(numericChecker, Mockito.times(0)).validateAdditionalValue(cstic, errorObj);

	}

}
