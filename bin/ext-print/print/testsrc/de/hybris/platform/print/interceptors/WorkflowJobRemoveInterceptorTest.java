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
package de.hybris.platform.print.interceptors;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.jalo.JaloObjectNoLongerValidException;
import de.hybris.platform.print.collaboration.facades.PrintCollaborationFacade;
import de.hybris.platform.print.services.impl.AbstractPrintJobServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.workflow.model.WorkflowModel;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class WorkflowJobRemoveInterceptorTest extends AbstractPrintJobServicelayerTest
{
	/**
	 * service to test
	 */
	private PrintCollaborationFacade printCollaborationFacade;

	@Resource
	private ModelService modelService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		//get required beans and services
		modelService = getModelService();
		printCollaborationFacade = getPrintCollaborationFacade();
	}

	/**
	 * Test removal of job (comment with workflow). When comment is removed, workflow should be also removed.
	 */
	@Test(expected = JaloObjectNoLongerValidException.class)
	public void testRemoveWorkflow()
	{
		final CommentModel comment = getCommentModel();
		printCollaborationFacade.createJob(comment, getDummyAttachment(), getUserModel());
		modelService.save(comment);

		assertThat(comment).isNotNull();
		assertThat(comment.getWorkflow()).isNotNull();

		final WorkflowModel workflow = comment.getWorkflow();
		modelService.remove(comment);
		modelService.refresh(workflow);
	}
}
