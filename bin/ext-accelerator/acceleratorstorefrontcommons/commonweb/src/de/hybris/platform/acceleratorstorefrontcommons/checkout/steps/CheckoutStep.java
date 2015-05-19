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

package de.hybris.platform.acceleratorstorefrontcommons.checkout.steps;


import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.CheckoutStepValidator;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


public class CheckoutStep implements StepTransition
{
	protected static final Logger LOG = Logger.getLogger(CheckoutStep.class);

	public static final String PREVIOUS = "previous";
	public static final String CURRENT = "current";
	public static final String NEXT = "next";
	private Map<String, String> transitions;
	private CheckoutStepValidator checkoutStepValidator;
	private CheckoutGroup checkoutGroup;
	private String progressBarId;

	@Override
	public String go(final String transition)
	{
		if (getTransitions().containsKey(transition))
		{
			return getTransitions().get(transition);
		}
		return null;
	}

	@Override
	public String onValidation(final ValidationResults validationResult)
	{
		if (getCheckoutGroup().getValidationResultsMap().containsKey(validationResult.name()))
		{
			return getCheckoutGroup().getValidationResultsMap().get(validationResult.name());
		}
		return null;
	}

	public ValidationResults validate(final RedirectAttributes redirectAttributes)
	{
		return (getCheckoutStepValidator()!= null) ? getCheckoutStepValidator().validateOnEnter(redirectAttributes):ValidationResults.SUCCESS;
	}

	public boolean checkIfValidationErrors(final ValidationResults validationResult)
	{
		if (!validationResult.equals(ValidationResults.SUCCESS))
		{
			return true;
		}
		return false;
	}

	public String previousStep()
	{
		return go(PREVIOUS);
	}

	public String currentStep()
	{
		return go(CURRENT);
	}

	public String nextStep()
	{
		if(getCheckoutStepValidator()!=null)
		{
			final ValidationResults validationResult = getCheckoutStepValidator().validateOnExit();
			if (validationResult != null && !validationResult.equals(ValidationResults.SUCCESS))
			{
				return onValidation(validationResult);
			}
		}
		return go(NEXT);
	}

	public Map<String, String> getTransitions()
	{
		return transitions;
	}

	@Required
	public void setTransitions(final Map<String, String> transitions)
	{
		this.transitions = transitions;
	}

	public CheckoutStepValidator getCheckoutStepValidator()
	{
		return checkoutStepValidator;
	}

	public void setCheckoutStepValidator(final CheckoutStepValidator checkoutStepValidator)
	{
		this.checkoutStepValidator = checkoutStepValidator;
	}

	public CheckoutGroup getCheckoutGroup()
	{
		return checkoutGroup;
	}

	@Required
	public void setCheckoutGroup(final CheckoutGroup checkoutGroup)
	{
		this.checkoutGroup = checkoutGroup;
	}

	public String getProgressBarId()
	{
		return progressBarId;
	}

	@Required
	public void setProgressBarId(final String progressBarId)
	{
		this.progressBarId = progressBarId;
	}
}
