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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.CheckoutStepValidator;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@UnitTest
public class CheckoutStepTest
{
	private static final String PREVIOUS = "previous";
	private static final String CURRENT = "current";
	private static final String NEXT = "next";

	private CheckoutStep checkoutStep;

	private Map<String, String> transitions;

	private Map<String, String> checkoutGroupValidationMap;

	@Mock
	private CheckoutStepValidator checkoutStepValidator;

	@Mock
	private CheckoutGroup checkoutGroup;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		transitions = new HashMap<String, String>();
		checkoutGroupValidationMap = new HashMap<String, String>();
		checkoutStep = new CheckoutStep();
		buildTransitions();


		checkoutStep.setCheckoutGroup(checkoutGroup);
		checkoutStep.setCheckoutStepValidator(checkoutStepValidator);
		checkoutStep.setTransitions(transitions);
	}

	private void buildTransitions()
	{
		transitions.put(PREVIOUS, PREVIOUS);
		transitions.put(CURRENT, CURRENT);
		transitions.put(NEXT, NEXT);

		checkoutGroupValidationMap.put(ValidationResults.REDIRECT_TO_CART.name(), "someCheckoutStepBean");
		given(checkoutGroup.getValidationResultsMap()).willReturn(checkoutGroupValidationMap);
	}

	@Test
	public void shouldGetPreviousStep()
	{
		Assert.assertTrue(checkoutStep.previousStep().equals(PREVIOUS));

		transitions.remove(PREVIOUS);
		Assert.assertNull(checkoutStep.previousStep());
	}

	@Test
	public void shouldGetCurrentStep()
	{
		Assert.assertTrue(checkoutStep.currentStep().equals(CURRENT));

		transitions.remove(CURRENT);
		Assert.assertNull(checkoutStep.currentStep());
	}

	@Test
	public void shouldGetNextStep()
	{
		Assert.assertTrue(checkoutStep.nextStep().equals(NEXT));

		transitions.remove(NEXT);
		Assert.assertNull(checkoutStep.nextStep());

		given(checkoutStepValidator.validateOnExit()).willReturn(ValidationResults.REDIRECT_TO_CART);
		Assert.assertTrue(checkoutStep.nextStep().equals("someCheckoutStepBean"));
	}

	@Test
	public void shouldHaveValidationErrors()
	{
		Assert.assertTrue(checkoutStep.checkIfValidationErrors(ValidationResults.FAILED));
	}

	@Test
	public void shouldNotHaveValidationErrors()
	{
		Assert.assertFalse(checkoutStep.checkIfValidationErrors(ValidationResults.SUCCESS));
	}


	@Test
	public void shouldValidate()
	{
		final RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);
		checkoutStep.setCheckoutStepValidator(null);
		Assert.assertTrue(checkoutStep.validate(redirectAttributes).equals(ValidationResults.SUCCESS));
	}
}
