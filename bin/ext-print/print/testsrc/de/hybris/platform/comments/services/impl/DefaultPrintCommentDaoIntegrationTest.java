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
package de.hybris.platform.comments.services.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.comments.model.CommentGroupModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.print.services.impl.AbstractPrintJobServicelayerTest;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;


public class DefaultPrintCommentDaoIntegrationTest extends AbstractPrintJobServicelayerTest
{
	@Resource
	private PrintCommentDao printCommentDao;


	/**
	 * 1. create job (included Workflow) 2. call PrintCommentDao.getCommentForWorkflow() 3. compare got CommentModel with created
	 * Job
	 */
	@Test
	public void testCommentForWorkflow()
	{
		final CommentModel job = getCommentModelWithJob();

		final SearchResult<CommentModel> resultList = printCommentDao.getCommentForWorkflow(job.getWorkflow());
		assertThat(resultList).isNotNull();
		final List<CommentModel> commentList = resultList.getResult();

		assertThat(commentList).isNotEmpty();
		assertThat(commentList).hasSize(1);
		assertThat(commentList.get(0)).isEqualTo(job);
	}

	/**
	 * 1. create comment 2. create commentGroup 3. assign commentGroup to comment 4. call
	 * PrintCommentDao.getCommentsForCommentGroup() 5. compoare got CommentModel with created
	 */
	@Test
	public void testCommentsForCommentGroup()
	{
		final CommentGroupModel commentGroup = getCommentGroup();
		getModelService().save(commentGroup);

		final CommentModel comment = getCommentModel();

		comment.setCommentGroup(commentGroup);
		getModelService().save(comment);

		assertThat(printCommentDao.getCommentsForCommentGroup(commentGroup)).isNotNull();

		final List<CommentModel> commentReturnList = printCommentDao.getCommentsForCommentGroup(commentGroup).getResult();

		assertThat(commentReturnList).hasSize(1);
		assertThat(commentReturnList.get(0)).isEqualTo(comment);
	}
}
