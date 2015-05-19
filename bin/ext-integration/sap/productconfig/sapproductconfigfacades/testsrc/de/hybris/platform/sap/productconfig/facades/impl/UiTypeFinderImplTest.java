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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiValidationType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class UiTypeFinderImplTest
{
	private final UiTypeFinderImpl uiTypeFinder = new UiTypeFinderImpl();


	private CsticModel csticModel;





	@Before
	public void setup()
	{
		csticModel = new CsticModelImpl();
	}

	protected CsticModel createSimpleInput()
	{
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setAllowsAdditionalValues(false);
		csticModel.setAssignableValues(createAssignableValueList(0));
		csticModel.setEntryFieldMask(null);
		csticModel.setMultivalued(false);
		csticModel.setReadonly(false);
		csticModel.setValueType(CsticModel.TYPE_STRING);
		return csticModel;
	}

	protected CsticModel createNumericInput(final int valueType)
	{
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setAllowsAdditionalValues(false);
		csticModel.setAssignableValues(createAssignableValueList(0));
		csticModel.setEntryFieldMask(null);
		csticModel.setMultivalued(false);
		csticModel.setReadonly(false);
		csticModel.setValueType(valueType);
		return csticModel;
	}

	protected CsticModel createSelection(final int valueType, final int numOptions, final boolean isMultivalued)
	{
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setAllowsAdditionalValues(false);
		csticModel.setAssignableValues(createAssignableValueList(numOptions));
		csticModel.setEntryFieldMask(null);
		csticModel.setMultivalued(isMultivalued);
		csticModel.setReadonly(false);
		csticModel.setValueType(valueType);
		csticModel.setStaticDomainLength(csticModel.getAssignableValues().size());
		return csticModel;
	}

	private List<CsticValueModel> createAssignableValueList(final int size)
	{
		final List<CsticValueModel> values = new ArrayList<>(size);
		for (int ii = 0; ii < size; ii++)
		{
			final CsticValueModel value = new CsticValueModelImpl();
			values.add(value);
		}
		return values;
	}

	@Test
	public void givenFloatThenUiTypeNumeric() throws Exception
	{
		csticModel = createNumericInput(CsticModel.TYPE_FLOAT);
		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = uiTypeFinder.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NUMERIC, actual);
		assertEquals("Wrong UI  type", UiValidationType.NUMERIC, actualValidationType);
	}

	@Test
	public void givenFloatReadOnlyThenUiTypeReadOnly() throws Exception
	{
		csticModel = createNumericInput(CsticModel.TYPE_FLOAT);
		csticModel.setReadonly(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = uiTypeFinder.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.READ_ONLY, actual);
		assertEquals("Wrong UI  type", UiValidationType.NONE, actualValidationType);
	}

	@Test
	public void givenIntegerThenUiTypeNumeric() throws Exception
	{
		csticModel = createNumericInput(CsticModel.TYPE_INTEGER);
		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NUMERIC, actual);
	}

	@Test
	public void givenIntegerReadOnlyThenUiTypeReadOnly() throws Exception
	{
		csticModel = createNumericInput(CsticModel.TYPE_INTEGER);
		csticModel.setReadonly(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.READ_ONLY, actual);
	}


	@Test
	public void givenStringThenUiTypeString() throws Exception
	{
		csticModel = createSimpleInput();

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = uiTypeFinder.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.STRING, actual);
		assertEquals("Wrong UI  type", UiValidationType.NONE, actualValidationType);

	}

	@Test
	public void givenStringReadOnlyThenUiTypeReadOnly() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setReadonly(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI type", UiType.READ_ONLY, actual);
	}

	@Test
	public void givenStringAndMultiValueThenUiTypeCheckbox() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 1, true);
		csticModel.setConstrained(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.CHECK_BOX, actual);
	}

	@Test
	public void givenStringAndMultiValueReadOnlyThenUiTypeReadOnly() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 1, true);
		csticModel.setReadonly(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.READ_ONLY, actual);
	}

	@Test
	public void givenStringAnd4ValuesThenUiTypeRadio() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 4, false);
		csticModel.setConstrained(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.RADIO_BUTTON, actual);
	}

	@Test
	public void givenFloatAnd4ValuesThenUiTypeRadio() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_FLOAT, 4, false);
		csticModel.setConstrained(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.RADIO_BUTTON, actual);
	}

	@Test
	public void givenFloatAnd4ValuesReadOnlyThenUiTypeReadOnly() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_FLOAT, 4, false);
		csticModel.setReadonly(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.READ_ONLY, actual);
	}

	@Test
	public void givenStringAnd5ValuesThenUiTypeDDLB() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 5, false);
		csticModel.setConstrained(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.DROPDOWN, actual);
	}

	@Test
	public void givenStringAnd5ValuesReadOnlyThenUiTypeReadOnly() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 5, false);
		csticModel.setReadonly(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.READ_ONLY, actual);
	}

	@Test
	public void givenIntAnd5ValuesThenUiTypeDDLB() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_INTEGER, 5, false);
		csticModel.setConstrained(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.DROPDOWN, actual);
	}


	@Test
	public void givenStringAndMultivaluedDomainThenUiCheckboxList() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 2, true);
		csticModel.setConstrained(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.CHECK_BOX_LIST, actual);
	}

	@Test
	public void givenFloatAndMultivaluedDomainThenUiCheckboxList() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_FLOAT, 2, true);
		csticModel.setConstrained(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.CHECK_BOX_LIST, actual);
	}

	@Test
	public void givenUndefinedThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setValueType(CsticModel.TYPE_UNDEFINED);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}

	@Test
	public void givenDateThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setValueType(CsticModel.TYPE_DATE);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}

	@Test
	public void givenCurrencyThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setValueType(CsticModel.TYPE_CURRENCY);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}

	@Test
	public void givenClassThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setValueType(CsticModel.TYPE_CLASS);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}

	@Test
	public void givenBooleanThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setValueType(CsticModel.TYPE_BOOLEAN);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = uiTypeFinder.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
		assertEquals("Wrong UI  type", UiValidationType.NONE, actualValidationType);
	}

	@Test
	public void givenSingleValueAllowsAdditionalValueStringThenDropDownAdditionalInputString() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 6, false);
		csticModel.setAllowsAdditionalValues(true);


		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = uiTypeFinder.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.DROPDOWN_ADDITIONAL_INPUT, actual);
		assertEquals("Wrong UI  type", UiValidationType.NONE, actualValidationType);
	}

	@Test
	public void givenSingleValueAllowsAdditionalValueNumericThenDropDownAdditionalInputNumeric() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_FLOAT, 6, false);
		csticModel.setAllowsAdditionalValues(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = uiTypeFinder.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.DROPDOWN_ADDITIONAL_INPUT, actual);
		assertEquals("Wrong UI  type", UiValidationType.NUMERIC, actualValidationType);
	}

	@Test
	public void givenSingleValueAllowsAdditionalValueStringThenRadioAdditionalInputString() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_STRING, 3, false);
		csticModel.setAllowsAdditionalValues(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = uiTypeFinder.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.RADIO_BUTTON_ADDITIONAL_INPUT, actual);
		assertEquals("Wrong UI  type", UiValidationType.NONE, actualValidationType);
	}

	@Test
	public void givenSingleValueAllowsAdditionalValueNumericThenRadioAdditionalInputNumeric() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_FLOAT, 3, false);
		csticModel.setAllowsAdditionalValues(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		final UiValidationType actualValidationType = uiTypeFinder.findUiValidationTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.RADIO_BUTTON_ADDITIONAL_INPUT, actual);
		assertEquals("Wrong UI  type", UiValidationType.NUMERIC, actualValidationType);
	}

	@Test
	public void givenSingleSelectionIntervalThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_INTEGER, 4, true);
		csticModel.setIntervalInDomain(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}

	@Test
	public void givenMultiValuedIntervalThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSelection(CsticModel.TYPE_INTEGER, 4, false);
		csticModel.setIntervalInDomain(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}

	@Test
	public void givenScientificThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createNumericInput(CsticModel.TYPE_FLOAT);
		csticModel.setEntryFieldMask("_,____.__EE");

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}

	@Test
	public void givenMultivaluedStringThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setMultivalued(true);

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}

	@Test
	public void givenStringWithTemplateThenUiTypeNotImplemented() throws Exception
	{
		csticModel = createSimpleInput();
		csticModel.setEntryFieldMask("abcd-efg");

		final UiType actual = uiTypeFinder.findUiTypeForCstic(csticModel);
		assertEquals("Wrong UI  type", UiType.NOT_IMPLEMENTED, actual);
	}
}
