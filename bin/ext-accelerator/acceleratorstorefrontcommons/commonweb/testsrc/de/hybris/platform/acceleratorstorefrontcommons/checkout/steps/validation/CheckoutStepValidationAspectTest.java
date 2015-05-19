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
package de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutGroup;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;


@UnitTest
public class CheckoutStepValidationAspectTest
{
	private static final String PROCEED = "Proceed";
	private static final String FAILURE = "Failure";

	private CheckoutStepValidationAspect checkoutStepValidationAspect;

	private Map<String, CheckoutGroup> checkoutFlowGroupMap;

	private Map<String, CheckoutStep> checkoutStepMap;

	@Mock
	private AcceleratorCheckoutFacade acceleratorCheckoutFacade;

	@Mock
	private CheckoutGroup checkoutGroup;

	@Mock
	private CheckoutStep checkoutStep;

	@Mock
	private ProceedingJoinPoint pjp;

	@Before
	public void setUp() throws Throwable
	{
		MockitoAnnotations.initMocks(this);
		setupGroup();
		setupPjp();

		checkoutStepValidationAspect = new CheckoutStepValidationAspect();
		checkoutStepValidationAspect.setCheckoutFacade(acceleratorCheckoutFacade);
		checkoutStepValidationAspect.setCheckoutFlowGroupMap(checkoutFlowGroupMap);
	}

	private void setupGroup()
	{
		given(acceleratorCheckoutFacade.getCheckoutFlowGroupForCheckout()).willReturn("myGroup");
		checkoutFlowGroupMap = new HashMap<String, CheckoutGroup>();
		checkoutFlowGroupMap.put("myGroup", checkoutGroup);

		given(checkoutStep.onValidation(ValidationResults.FAILED)).willReturn(FAILURE);
		checkoutStepMap = new HashMap<String, CheckoutStep>();
		checkoutStepMap.put("myStep", checkoutStep);
		given(checkoutGroup.getCheckoutStepMap()).willReturn(checkoutStepMap);
	}

	private void setupPjp() throws Throwable
	{
		final MethodSignature signature = Mockito.mock(MethodSignature.class);
		final Method testMethod = this.getClass().getMethod("annotatedMethod");
		given(signature.getMethod()).willReturn(testMethod);

		given(pjp.getSignature()).willReturn(signature);
		given(pjp.proceed()).willReturn(PROCEED);

		final Object[] args = new Object[2];
		args[1] = Mockito.mock(RedirectAttributesModelMap.class);
		given(pjp.getArgs()).willReturn(args);
	}

	@Test
	public void shouldProceedValidateCheckoutStep() throws Throwable
	{
		given(checkoutStep.validate((RedirectAttributesModelMap) pjp.getArgs()[1])).willReturn(ValidationResults.SUCCESS);
		Assert.assertTrue(checkoutStepValidationAspect.validateCheckoutStep(pjp).equals(PROCEED));
	}

	@Test
	public void shouldNoProceedWithValidationErrors() throws Throwable
	{
		given(Boolean.valueOf(checkoutStep.checkIfValidationErrors(ValidationResults.FAILED))).willReturn(Boolean.TRUE);
		given(checkoutStep.validate((RedirectAttributesModelMap) pjp.getArgs()[1])).willReturn(ValidationResults.FAILED);
		Assert.assertTrue(checkoutStepValidationAspect.validateCheckoutStep(pjp).equals(FAILURE));
	}

	@PreValidateCheckoutStep(checkoutStep = "myStep")
	public void annotatedMethod()
	{
		// NO PMD
	}
}
