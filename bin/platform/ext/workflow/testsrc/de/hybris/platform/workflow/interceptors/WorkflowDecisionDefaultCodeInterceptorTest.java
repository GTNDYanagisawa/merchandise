/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.workflow.interceptors;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class WorkflowDecisionDefaultCodeInterceptorTest extends ServicelayerTest
{
	@Resource
	ModelService modelService;

	@Test
	public void testIfCodeIsNotNull()
	{
		final WorkflowDecisionModel decision = modelService.create(WorkflowDecisionModel.class);

		modelService.save(decision);

		assertThat(decision.getCode()).isNotNull();
		assertThat(decision.getCode()).hasSize(8);
	}
}
