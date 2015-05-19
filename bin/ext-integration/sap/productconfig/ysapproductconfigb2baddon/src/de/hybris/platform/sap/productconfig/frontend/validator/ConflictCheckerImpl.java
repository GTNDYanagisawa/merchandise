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
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;

import java.util.List;

import org.springframework.validation.BindingResult;


public class ConflictCheckerImpl implements ConflictChecker
{
	private static final String UNUSED_DUMMY_MESSAGE = "no message";
	private static final String DEFAULT_ERROR_CODE = "sapproductconfig.conflict.default";
	private static final String DEFAULT_MISSING_MANDATORY_TEXT_CODE = "sapproductconfig.missing.mandatory.field.default";
	private static final String DEFAULT_MISSING_MANDATORY_RADIO_CODE = "sapproductconfig.missing.mandatory.radio.default";
	private static final String DEFAULT_MISSING_MANDATORY_MULTI_CODE = "sapproductconfig.missing.mandatory.multi.default";
	private static final String DEFAULT_MISSING_MANDATORY_TEXT_CODE_WITH_FIELDNAME = "sapproductconfig.missing.mandatory.field.with.fieldname";
	private static final String DEFAULT_MISSING_MANDATORY_RADIO_CODE_WITH_FIELDNAME = "sapproductconfig.missing.mandatory.radio.with.fieldname";
	private static final String DEFAULT_MISSING_MANDATORY_MULTI_CODE_WITH_FIELDNAME = "sapproductconfig.missing.mandatory.multi.with.fieldname";
	private static final String NULL_ERROR_CODE = null;

	@Override
	public void checkConflicts(final ConfigurationData config, final BindingResult bindingResult)
	{
		final List<UiGroupData> groups = config.getGroups();

		for (int ii = 0; ii < groups.size(); ii++)
		{
			final UiGroupData group = groups.get(ii);
			final String prefix = "groups[" + ii + "].";
			checkConflitcsInGroups(group, prefix, bindingResult);
		}
	}

	private GroupStatusType checkConflitcsInGroups(final UiGroupData group, final String prefix, final BindingResult bindingResult)
	{
		final List<CsticData> cstics = group.getCstics();

		boolean groupWarning = false;
		if (cstics != null && !cstics.isEmpty())
		{
			for (int ii = 0; ii < cstics.size(); ii++)
			{
				final CsticData csticData = cstics.get(ii);
				final List<ConflictData> conflicts = csticData.getConflicts();

				final boolean nothingToValidate = isEmptyOrNull(conflicts);
				if (nothingToValidate)
				{
					continue;
				}

				final String path = prefix + "cstics[" + ii + "].value";
				validate(csticData, conflicts, path, bindingResult);

				groupWarning = true;
			}
		}

		final List<UiGroupData> subGroups = group.getSubGroups();
		if (subGroups != null && !subGroups.isEmpty())
		{
			for (int ii = 0; ii < subGroups.size(); ii++)
			{
				final String path = prefix + "subGroups[" + ii + "].";
				final GroupStatusType state = checkConflitcsInGroups(subGroups.get(ii), path, bindingResult);

				switch (state)
				{
					case ERROR:
						group.setGroupStatus(GroupStatusType.ERROR);
						break;
					case WARNING:
						groupWarning = true;
						break;
					default:
						break;
				}
			}
		}

		if (groupWarning && group.getGroupStatus() != GroupStatusType.ERROR)
		{
			group.setGroupStatus(GroupStatusType.WARNING);
		}

		return group.getGroupStatus();
	}

	@Override
	public void checkMandatoryFields(final ConfigurationData config, final BindingResult bindingResult)
	{
		final List<UiGroupData> groups = config.getGroups();

		for (int ii = 0; ii < groups.size(); ii++)
		{
			final UiGroupData group = groups.get(ii);
			final String prefix = "groups[" + ii + "].";
			checkMandatoryFieldsInGroups(group, prefix, bindingResult);
		}
	}

	public GroupStatusType checkMandatoryFieldsInGroups(final UiGroupData group, final String prefix,
			final BindingResult bindingResult)
	{
		final List<CsticData> cstics = group.getCstics();

		boolean groupWarning = false;
		for (int ii = 0; ii < cstics.size(); ii++)
		{
			final CsticData csticData = cstics.get(ii);
			if (!csticData.isRequired())
			{
				continue;
			}

			final String path = prefix + "cstics[" + ii + "].value";

			final boolean fieldWarning = validateMandatoryFields(csticData, path, bindingResult);
			if (!groupWarning && fieldWarning)
			{
				groupWarning = true;
			}

		}

		final List<UiGroupData> subGroups = group.getSubGroups();
		if (subGroups != null && !subGroups.isEmpty())
		{
			for (int ii = 0; ii < subGroups.size(); ii++)
			{
				final String path = prefix + "subGroups[" + ii + "].";
				final GroupStatusType state = checkMandatoryFieldsInGroups(subGroups.get(ii), path, bindingResult);

				switch (state)
				{
					case ERROR:
						group.setGroupStatus(GroupStatusType.ERROR);
						break;
					case WARNING:
						groupWarning = true;
						break;
					default:
						break;
				}
			}
		}


		if (groupWarning && group.getGroupStatus() != GroupStatusType.ERROR)
		{
			group.setGroupStatus(GroupStatusType.WARNING);
		}

		return group.getGroupStatus();
	}

	private boolean validateMandatoryFields(final CsticData csticData, final String path, final BindingResult bindingResult)
	{

		if (!checkEmpty(csticData))
		{
			return false;
		}

		csticData.setCsticStatus(CsticStatusType.WARNING);

		final String errorText = determineMandatoryFieldErrorMessage(csticData.getType());
		final String[] errorCodes = determineMandatoryFieldErrorCode(csticData.getType());
		final MandatoryFieldError error = new MandatoryFieldError(csticData, path, null, errorCodes, errorText);
		bindingResult.addError(error);

		return true;

	}

	private String determineMandatoryFieldErrorMessage(final UiType type)
	{
		switch (type)
		{
			case RADIO_BUTTON:
			case DROPDOWN:
				return "Please select one value";
			case CHECK_BOX_LIST:
				return "Please select one or more values";
			default:
				return "Please enter a value for the required field";
		}
	}

	private String[] determineMandatoryFieldErrorCode(final UiType type)
	{
		final String[] errorCodes = new String[2];

		switch (type)
		{
			case RADIO_BUTTON:
			case DROPDOWN:
				errorCodes[0] = DEFAULT_MISSING_MANDATORY_RADIO_CODE_WITH_FIELDNAME;
				errorCodes[1] = DEFAULT_MISSING_MANDATORY_RADIO_CODE;
				break;
			case CHECK_BOX_LIST:
				errorCodes[0] = DEFAULT_MISSING_MANDATORY_MULTI_CODE_WITH_FIELDNAME;
				errorCodes[1] = DEFAULT_MISSING_MANDATORY_MULTI_CODE;
				break;
			default:
				errorCodes[0] = DEFAULT_MISSING_MANDATORY_TEXT_CODE_WITH_FIELDNAME;
				errorCodes[1] = DEFAULT_MISSING_MANDATORY_TEXT_CODE;
				break;
		}
		return errorCodes;
	}

	private boolean checkEmpty(final CsticData csticData)
	{
		final String value = csticData.getValue();
		if (UiType.CHECK_BOX_LIST == csticData.getType())
		{
			for (final CsticValueData csticValue : csticData.getDomainvalues())
			{
				if (csticValue.isSelected())
				{
					return false;
				}
			}
			return true;
		}
		if (value == null)
		{
			return true;
		}
		if (value.equals("") || value.trim().length() == 0)
		{
			return true;
		}

		return false;
	}

	private void validate(final CsticData csticData, final List<ConflictData> conflicts, final String path,
			final BindingResult bindingResult)
	{
		for (final ConflictData conflict : conflicts)
		{
			final String conflictText = determineErrorMessage(conflict);
			final String[] errorCodes = determineErrorCode(conflict);
			final ConflictError error = new ConflictError(csticData, path, csticData.getValue(), errorCodes, conflictText);
			bindingResult.addError(error);
		}
	}


	protected String determineErrorMessage(final ConflictData conflict)
	{
		String conflictText = conflict.getText();

		final boolean textAvailable = (null != conflictText);
		if (!textAvailable)
		{
			conflictText = UNUSED_DUMMY_MESSAGE;
		}

		return conflictText;
	}


	protected String[] determineErrorCode(final ConflictData conflict)
	{
		final String conflictText = conflict.getText();
		final String[] errorCodes = new String[1];

		final boolean textAvailable = (null != conflictText);
		if (!textAvailable)
		{
			errorCodes[0] = DEFAULT_ERROR_CODE;
		}
		else
		{
			errorCodes[0] = NULL_ERROR_CODE;
		}

		return errorCodes;
	}

	private boolean isEmptyOrNull(final List<ConflictData> conflicts)
	{
		if (conflicts == null)
		{
			return true;
		}

		if (conflicts.isEmpty())
		{
			return true;
		}
		return false;
	}
}
