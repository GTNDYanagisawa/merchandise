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
package de.hybris.platform.b2b.interceptor;


import static org.mockito.Mockito.*;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.workflow.interceptors.AutomatedWorkflowActionTemplateValidator;
import de.hybris.platform.workflow.model.AutomatedWorkflowActionTemplateModel;
import org.junit.Test;
import org.mockito.Mock;


@UnitTest
public class AutomatedWorkflowActionTemplateValidatorMockTest extends HybrisMokitoTest
{

	private final AutomatedWorkflowActionTemplateValidator actionTemplateValidator = new AutomatedWorkflowActionTemplateValidator();
	@Mock
	InterceptorContext interceptorContextMock;
	@Mock
	AutomatedWorkflowActionTemplateModel automatedWorkfow;

	@Test
	public void checkThatAutomatedWorkflowActionValidationIsCorrect() throws InterceptorException
	{

		when(automatedWorkfow.getJobHandler()).thenReturn("automatedWorkfow");
		actionTemplateValidator.onValidate(automatedWorkfow, interceptorContextMock);

	}

}
