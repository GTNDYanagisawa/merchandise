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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.platform.comments.model.CommentGroupModel;
import de.hybris.platform.comments.services.CommentGroupDao;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.tests.AbstractPrintServicelayerTest;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


public class DefaultCommentGroupDaoIntegrationTest extends AbstractPrintServicelayerTest
{

	@Resource
	private CommentGroupDao defaultCommentGroupDao;

	@Resource
	private SessionService sessionService;

	/** Name of the test CommentGroup */
	private static final String COMMENTGROUP_NAME = "TestCommentGroup;";

	private UserModel currentSessionUser;

	private Date roundedSessionCreationTime;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		roundedSessionCreationTime = getRoundedSessionCreationTime();
		currentSessionUser = (UserModel) sessionService.getCurrentSession().getAttribute("user");
	}


	/**
	 * creates a CommentGroup for a page, user and date and checks if the DAO finds it
	 */
	@Test
	public void commentGroupForPageExistsTest()
	{
		// create a page
		final PageModel page = preparePage();
		getModelService().save(page);

		final CommentGroupModel commentGroupModel = createCommentGroup(page, COMMENTGROUP_NAME);
		getModelService().save(commentGroupModel);

		final CommentGroupModel commentGroup = defaultCommentGroupDao.findCommentGroup(page, currentSessionUser,
				roundedSessionCreationTime);
		assertNotNull("Found no CommentGroup for this page and the current user session!", commentGroup);
		assertEquals("Couldn't find my created test CommentGroup", COMMENTGROUP_NAME, commentGroup.getCode());
	}

	/**
	 * creates two CommentGroups for a page, user and date and checks if the DAO return the first one. When there accidentally have
	 * been created more than one CommentGroup for a page in one session we consider the first one the one to go with.
	 */
	@Test
	public void ambiguousCommentGroupsForPageExistTest()
	{
		// create a page
		final PageModel page = preparePage();
		getModelService().save(page);

		final CommentGroupModel commentGroupModel1 = createCommentGroup(page, COMMENTGROUP_NAME + "1");
		getModelService().save(commentGroupModel1);

		final CommentGroupModel commentGroupModel2 = createCommentGroup(page, COMMENTGROUP_NAME + "2");
		getModelService().save(commentGroupModel2);

		final CommentGroupModel commentGroup = defaultCommentGroupDao.findCommentGroup(page, currentSessionUser,
				roundedSessionCreationTime);
		assertNotNull("Found no CommentGroup for this page and the current user session!", commentGroup);
		assertEquals("Couldn't find the first of my two created test CommentGroups", COMMENTGROUP_NAME + "1",
				commentGroup.getCode());
	}

	/**
	 * tries to find a CommentGroup for a page where no CommentGroup has been created for this page in this user session
	 */
	@Test
	public void commentGroupForPageDoesntExistTest()
	{
		// create a page
		final PageModel page = preparePage();
		getModelService().save(page);

		final Date date = getRoundedSessionCreationTime();
		final UserModel user = (UserModel) sessionService.getCurrentSession().getAttribute("user");

		final CommentGroupModel commentGroup = defaultCommentGroupDao.findCommentGroup(page, user, date);
		assertNull("Found a CommentGroup where no one has been created", commentGroup);
	}

	private Date getRoundedSessionCreationTime()
	{
		long sessionTime = JaloSession.getCurrentSession().getCreationTime();
		sessionTime = (long) (Math.round(sessionTime / 1000d) * 1000d);
		return new Date(sessionTime);
	}

	private CommentGroupModel createCommentGroup(final PageModel page, final String code)
	{
		final CommentGroupModel commentGroup = getModelService().create(CommentGroupModel.class);
		commentGroup.setCode(code);
		commentGroup.setDate(roundedSessionCreationTime);
		commentGroup.setItem(page);
		commentGroup.setUser(currentSessionUser);
		return commentGroup;
	}

}
