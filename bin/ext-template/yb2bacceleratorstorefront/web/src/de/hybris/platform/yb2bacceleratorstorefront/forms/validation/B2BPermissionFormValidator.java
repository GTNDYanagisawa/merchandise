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
package de.hybris.platform.yb2bacceleratorstorefront.forms.validation;

import de.hybris.platform.b2bacceleratorservices.enums.B2BPermissionTypeEnum;
import de.hybris.platform.servicelayer.i18n.FormatFactory;
import de.hybris.platform.yb2bacceleratorstorefront.forms.B2BPermissionForm;

import java.text.ParseException;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Validator for B2B permission form.
 */
@Component("b2BPermissionFormValidator")
@Scope("tenant")
public class B2BPermissionFormValidator implements Validator
{
	@Resource(name = "formatFactory")
	private FormatFactory formatFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> aClass)
	{
		return B2BPermissionForm.class.equals(aClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(final Object object, final Errors errors)
	{
		final B2BPermissionForm b2BPermissionForm = (B2BPermissionForm) object;
		final String permissionType = b2BPermissionForm.getB2BPermissionTypeData().getCode();

		if (!B2BPermissionTypeEnum.B2BBUDGETEXCEEDEDPERMISSION.equals(B2BPermissionTypeEnum.valueOf(permissionType)))
		{
			if (StringUtils.isEmpty(b2BPermissionForm.getCurrency()))
			{
				errors.rejectValue("currency", "general.required");
			}

			final String thresholdValue = b2BPermissionForm.getValue();
			if (StringUtils.isBlank(thresholdValue))
			{
				errors.rejectValue("value", "general.required");
			}
			else
			{
				final Number thresholdNumber;
				try
				{
					thresholdNumber = getFormatFactory().createNumberFormat().parse(thresholdValue);
					if (thresholdNumber.doubleValue() < 0D)
					{
						errors.rejectValue("value", "text.company.managePermissions.threshold.value.error");
					}
				}
				catch (final ParseException e)
				{
					errors.rejectValue("value", "text.company.managePermissions.threshold.value.invalid");
				}
			}

			if (B2BPermissionTypeEnum.B2BORDERTHRESHOLDTIMESPANPERMISSION.equals(B2BPermissionTypeEnum.valueOf(permissionType)))
			{
				if (StringUtils.isEmpty(b2BPermissionForm.getTimeSpan()))
				{
					errors.rejectValue("timeSpan", "general.required");
				}
			}
		}
	}

	protected FormatFactory getFormatFactory()
	{
		return formatFactory;
	}

	public void setFormatFactory(final FormatFactory formatFactory)
	{
		this.formatFactory = formatFactory;
	}
}
