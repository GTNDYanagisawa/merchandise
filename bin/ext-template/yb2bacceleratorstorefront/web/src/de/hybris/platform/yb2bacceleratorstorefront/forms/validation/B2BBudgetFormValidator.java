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

import de.hybris.platform.servicelayer.i18n.FormatFactory;
import de.hybris.platform.yb2bacceleratorstorefront.forms.B2BBudgetForm;

import java.text.ParseException;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component("b2BBudgetFormValidator")
@Scope("tenant")
public class B2BBudgetFormValidator implements Validator
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
		return B2BBudgetForm.class.equals(aClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(final Object object, final Errors errors)
	{
		final B2BBudgetForm form = (B2BBudgetForm) object;

		final String budget = form.getBudget();
		if (StringUtils.isBlank(budget))
		{
			errors.rejectValue("budget", "general.required");
		}
		else
		{
			final Number budgetNumber;
			try
			{
				budgetNumber = getFormatFactory().createNumberFormat().parse(budget);
				if (budgetNumber.doubleValue() < 0D)
				{
					errors.rejectValue("budget", "text.company.manageBudgets.budget.invalid");
				}
			}
			catch (final ParseException e)
			{
				errors.rejectValue("budget", "text.company.manageBudgets.budget.invalid");
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
