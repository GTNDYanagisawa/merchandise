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

import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiTypeFinder;
import de.hybris.platform.sap.productconfig.facades.UiValidationType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class UiTypeFinderImpl implements UiTypeFinder
{
	private int dropDownListThreshold = 4;

	private final static Logger LOG = Logger.getLogger(UiTypeFinderImpl.class);

	@Override
	public UiType findUiTypeForCstic(final CsticModel model)
	{
		final List<UiType> posibleTypes = collectPossibleTypes(model);
		final UiType uiType = chooseUiType(posibleTypes, model);

		if (LOG.isTraceEnabled())
		{
			LOG.trace("UI type found for cstic model [CSTIC_NAME='" + model.getName() + "';CSTIC_TYPE='" + model.getValueType()
					+ "';CSTIC_UI_TYPE='" + uiType + "']");
		}


		return uiType;
	}

	protected List<UiType> collectPossibleTypes(final CsticModel model)
	{
		final List<UiType> possibleTypes = new ArrayList<>(1);

		if (isReadonly(model))
		{
			possibleTypes.add(UiType.READ_ONLY);
			return possibleTypes;
		}
		possibleTypes.addAll(checkForSingelValueTypes(model));

		possibleTypes.addAll(checkForMultiSelectionTypes(model));

		possibleTypes.addAll(checkForSingleSelectionTypes(model));
		return possibleTypes;
	}

	private List<UiType> checkForSingleSelectionTypes(final CsticModel model)
	{
		final List<UiType> possibleTypes = new ArrayList<>(1);
		if (isRadioButton(model))
		{
			possibleTypes.add(UiType.RADIO_BUTTON);
		}
		if (isRadioButtonAdditionalValue(model))
		{
			possibleTypes.add(UiType.RADIO_BUTTON_ADDITIONAL_INPUT);
		}
		if (isDDLB(model))
		{
			possibleTypes.add(UiType.DROPDOWN);
		}
		if (isDDLBAdditionalValue(model))
		{
			possibleTypes.add(UiType.DROPDOWN_ADDITIONAL_INPUT);
		}

		return possibleTypes;
	}

	private List<UiType> checkForMultiSelectionTypes(final CsticModel model)
	{
		final List<UiType> possibleTypes = new ArrayList<>(1);
		if (isCheckbox(model))
		{
			possibleTypes.add(UiType.CHECK_BOX);
		}
		if (isCheckboxList(model))
		{
			possibleTypes.add(UiType.CHECK_BOX_LIST);
		}

		return possibleTypes;
	}

	private List<UiType> checkForSingelValueTypes(final CsticModel model)
	{
		final List<UiType> possibleTypes = new ArrayList<>(1);
		if (isStringInput(model))
		{
			possibleTypes.add(UiType.STRING);
		}
		if (isNumericInput(model))
		{
			possibleTypes.add(UiType.NUMERIC);
		}

		return possibleTypes;
	}

	protected UiType chooseUiType(final List<UiType> posibleTypes, final CsticModel model)
	{
		UiType uiType;
		if (posibleTypes.isEmpty())
		{
			uiType = UiType.NOT_IMPLEMENTED;
		}
		else if (posibleTypes.size() == 1)
		{
			uiType = posibleTypes.get(0);
		}
		else
		{
			throw new IllegalArgumentException("Cstic: [" + model + "] has an ambigious uiType: [" + posibleTypes + "]");
		}
		return uiType;
	}

	protected List<UiValidationType> collectPossibleValidationTypes(final CsticModel model)
	{
		final List<UiValidationType> possibleTypes = new ArrayList<>(1);

		if (isReadonly(model))
		{
			possibleTypes.add(UiValidationType.NONE);
			return possibleTypes;
		}
		if (isSimpleNumber(model) && (isInput(model) || (isSingleSelection(model) && editableWithAdditionalValue(model))))
		{
			possibleTypes.add(UiValidationType.NUMERIC);
		}
		return possibleTypes;
	}

	protected UiValidationType chooseUiValidationType(final List<UiValidationType> posibleTypes, final CsticModel model)
	{
		UiValidationType uiType;
		if (posibleTypes.isEmpty())
		{
			uiType = UiValidationType.NONE;
		}
		else if (posibleTypes.size() == 1)
		{
			uiType = posibleTypes.get(0);
		}
		else
		{
			throw new IllegalArgumentException("Cstic: [" + model + "] has an ambigious uiValidationType: [" + posibleTypes + "]");
		}
		return uiType;
	}

	private boolean isReadonly(final CsticModel model)
	{
		boolean isReadOnly;
		isReadOnly = model.isReadonly();
		if (model.isConstrained() && (model.getAssignableValues() == null || model.getAssignableValues().size() == 0))
		{
			isReadOnly = true;
		}
		return isReadOnly;
	}

	protected boolean isDDLB(final CsticModel model)
	{
		boolean isDDLB;
		isDDLB = isSingleSelection(model) && model.getAssignableValues().size() > dropDownListThreshold
				&& editableWithoutAdditionalValue(model);
		return isDDLB;
	}

	protected boolean isDDLBAdditionalValue(final CsticModel model)
	{
		boolean isDDLB;
		isDDLB = isSingleSelection(model) && model.getAssignableValues().size() > dropDownListThreshold
				&& editableWithAdditionalValue(model);
		return isDDLB;
	}

	protected boolean isRadioButton(final CsticModel model)
	{
		boolean isRadioButton;
		isRadioButton = isSingleSelection(model) && model.getAssignableValues().size() <= dropDownListThreshold
				&& editableWithoutAdditionalValue(model);
		return isRadioButton;
	}

	protected boolean isRadioButtonAdditionalValue(final CsticModel model)
	{
		boolean isRadioButton;
		isRadioButton = isSingleSelection(model) && model.getAssignableValues().size() <= dropDownListThreshold
				&& editableWithAdditionalValue(model);
		return isRadioButton;
	}

	protected boolean isCheckbox(final CsticModel model)
	{
		boolean isCheckbox;
		isCheckbox = isMultiSelection(model) && model.getStaticDomainLength() == 1;
		return isCheckbox;
	}

	protected boolean isCheckboxList(final CsticModel model)
	{
		boolean isCheckboxList;
		isCheckboxList = isMultiSelection(model) && (model.isConstrained() || model.getAssignableValues().size() > 0)
				&& model.getStaticDomainLength() > 1;
		return isCheckboxList;
	}

	protected boolean isStringInput(final CsticModel model)
	{
		final boolean isStringInput = isInput(model) && CsticModel.TYPE_STRING == model.getValueType();
		return isStringInput;
	}

	protected boolean isNumericInput(final CsticModel model)
	{
		final boolean isNumeric = isInput(model)
				&& (CsticModel.TYPE_INTEGER == model.getValueType() || CsticModel.TYPE_FLOAT == model.getValueType());
		return isNumeric;
	}

	protected boolean isSelection(final CsticModel model)
	{
		final boolean isSelection = isValueTypeSupported(model)
				&& (model.isConstrained() || model.getAssignableValues().size() > 0);
		return isSelection;
	}

	protected boolean isMultiSelection(final CsticModel model)
	{
		final boolean isMultiSelection = isSelection(model) && model.isMultivalued();
		return isMultiSelection;
	}


	protected boolean isSingleSelection(final CsticModel model)
	{
		final boolean isSingleSelection = isSelection(model) && !model.isMultivalued();
		return isSingleSelection;
	}

	protected boolean isInput(final CsticModel model)
	{
		final boolean isInput = isValueTypeSupported(model) && editableWithoutAdditionalValue(model) && !model.isMultivalued()
				&& model.getAssignableValues().size() == 0 && !model.isConstrained();
		return isInput;
	}


	protected boolean editableWithoutAdditionalValue(final CsticModel model)
	{
		final boolean isSupported = !model.isAllowsAdditionalValues() && !model.isReadonly();
		return isSupported;
	}

	protected boolean editableWithAdditionalValue(final CsticModel model)
	{
		final boolean isSupported = model.isAllowsAdditionalValues() && !model.isReadonly();
		return isSupported;
	}

	protected boolean isValueTypeSupported(final CsticModel model)
	{
		final boolean isValueTypeSupported = (isSimpleString(model) || isSimpleNumber(model));
		return isValueTypeSupported;
	}

	protected boolean isSimpleString(final CsticModel model)
	{
		boolean isSimpleString = CsticModel.TYPE_STRING == model.getValueType();
		if (isSimpleString)
		{
			isSimpleString = model.getEntryFieldMask() == null || model.getEntryFieldMask().isEmpty();
		}
		return isSimpleString;
	}

	protected boolean isSimpleNumber(final CsticModel model)
	{
		boolean isNumber = CsticModel.TYPE_INTEGER == model.getValueType() || CsticModel.TYPE_FLOAT == model.getValueType();
		if (isNumber)
		{
			// Scientific format not supported
			final boolean isScientific = model.getEntryFieldMask() != null && model.getEntryFieldMask().contains("E");
			// Interval not supported
			final boolean isInterval = model.isIntervalInDomain();
			isNumber = !isScientific && !isInterval;
		}
		return isNumber;
	}

	/**
	 * @param dropDownListThreshold
	 *           the dropDownListThreshold to set
	 */
	public void setDropDownListThreshold(final int dropDownListThreshold)
	{
		this.dropDownListThreshold = dropDownListThreshold;
	}


	@Override
	public UiValidationType findUiValidationTypeForCstic(final CsticModel model)
	{
		final List<UiValidationType> possibleTypes = collectPossibleValidationTypes(model);
		return chooseUiValidationType(possibleTypes, model);
	}
}
