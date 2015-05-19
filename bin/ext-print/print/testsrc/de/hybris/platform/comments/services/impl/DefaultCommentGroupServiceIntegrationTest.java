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
package de.hybris.platform.comments.services.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.comments.model.CommentGroupModel;
import de.hybris.platform.comments.services.CommentGroupService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.services.impl.AbstractPrintJobServicelayerTest;
import de.hybris.platform.servicelayer.session.SessionService;

import javax.annotation.Resource;

import org.junit.Test;


public class DefaultCommentGroupServiceIntegrationTest extends AbstractPrintJobServicelayerTest
{

	@Resource
	private CommentGroupService defaultCommentGroupService;

	@Resource
	private SessionService sessionService;


	/**
	 * creates a CommentGroup for a page and user, retrieves it from the persistence layer and checks if it is the same
	 */
	@Test
	public void testCreateCommentGroup()
	{
		// create a page
		final PageModel page = preparePage();
		getModelService().save(page);

		final UserModel sessionUser = (UserModel) sessionService.getCurrentSession().getAttribute("user");

		final CommentGroupModel commentGroupCreated = defaultCommentGroupService.createCommentGroup(page, sessionUser);
		getModelService().save(commentGroupCreated);

		final CommentGroupModel commenGroupRetrieved = defaultCommentGroupService.getCommentGroup(page, sessionUser);
		assertEquals("Another CommentGroup was retrieved than has been created before!", commentGroupCreated, commenGroupRetrieved);

		/**
		 * creating another CommentGroup for the same page in one user session is prohibited by the service class. When we try to
		 * create a new CommentGroup we should be presented with the first one we created.
		 */
		final CommentGroupModel additionalCommentGroupCreated = defaultCommentGroupService.createCommentGroup(page, sessionUser);
		getModelService().save(additionalCommentGroupCreated);

		assertEquals("The additional CommentGroup created is not the same we created in the first round!", commentGroupCreated,
				additionalCommentGroupCreated);

	}
}
