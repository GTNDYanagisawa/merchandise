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
package de.hybris.platform.print.services.impl;

import de.hybris.platform.comments.model.CommentGroupModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.print.collaboration.facades.PrintCollaborationFacade;
import de.hybris.platform.print.jalo.PrintManager;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.tests.AbstractPrintServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;


/**
 * Preload all informations for preparation to run the TestDefaultPrintCollaborationFacade
 */
public abstract class AbstractPrintJobServicelayerTest extends AbstractPrintServicelayerTest
{

	private CommentModel commentModel;
	private CommentModel commentModelWithJob;
	private CommentModel commentModelWithPage;
	private CommentModel commentModelWithPageJob;

	private UserModel userModel;

	private List<CommentModel> commentModelList;

	private PrintCollaborationFacade printCollaborationFacade;

	private PageModel dummyAttachment;

	private SessionService sessionService;

	private CommentGroupModel commentGroup;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		printCollaborationFacade = (PrintCollaborationFacade) Registry.getApplicationContext().getBean("printCollaborationFacade");
		sessionService = (SessionService) Registry.getApplicationContext().getBean("sessionService");

		initUserModel();

		//import essential comment data
		PrintManager.getInstance().importCSVFromResources("/print/import/test/Print_8_jobworkflowtemplateTest.csv");

		initAttachement();
		initCommentModels();
		createCommentGroup();
	}

	private Date getRoundedSessionCreationTime()
	{
		long sessionTime = JaloSession.getCurrentSession().getCreationTime();
		sessionTime = (long) (Math.round(sessionTime / 1000d) * 1000d);
		return new Date(sessionTime);
	}

	private void createCommentGroup()
	{
		final UserModel user = (UserModel) sessionService.getCurrentSession().getAttribute("user");

		final CommentGroupModel commentGroup = getModelService().create(CommentGroupModel.class);
		commentGroup.setCode("commentGroup");
		commentGroup.setDate(getRoundedSessionCreationTime());
		commentGroup.setItem(dummyAttachment);
		commentGroup.setUser(user);

		this.commentGroup = commentGroup;
	}

	protected <T extends ItemModel> T getOrCreateItem(T example)
	{
		try
		{
			example = getFlexibleSearchService().getModelByExample(example);
		}
		catch (final ModelNotFoundException e)
		{
			getModelService().attach(example);
			getModelService().save(example);
		}
		return example;
	}

	protected DomainModel getDomain()
	{
		final DomainModel domain = new DomainModel();
		domain.setCode("cockpit");
		return getOrCreateItem(domain);
	}

	protected ComponentModel getComponent()
	{
		final ComponentModel component = new ComponentModel();
		component.setCode("cockpit");
		component.setDomain(getDomain());
		return getOrCreateItem(component);
	}

	protected CommentTypeModel getCommentType()
	{
		final CommentTypeModel commentType = new CommentTypeModel();
		commentType.setCode("comment");
		commentType.setDomain(getDomain());
		return getOrCreateItem(commentType);
	}

	/**
	 * Init attatchement for comments
	 */
	private void initAttachement()
	{
		dummyAttachment = preparePage();
		getModelService().save(dummyAttachment);
	}

	/**
	 * Init testUser for comments
	 */
	private void initUserModel()
	{
		userModel = createUser("layouter");
	}

	/**
	 * Init all commentModels with proper parameters
	 */
	protected void initCommentModels()
	{
		//CommentModel
		commentModel = prepareCommentWithMetadata("commentModel", "no subject", 0, userModel);

		//CommentModel with job
		//pageIndex need to be set to 1 to differ from other comments
		commentModelWithJob = prepareCommentWithMetadata("commentModelWithJob", "no subject", 1, userModel);
		printCollaborationFacade.createJob(commentModelWithJob, dummyAttachment, userModel);

		//CommentModel with page
		commentModelWithPage = prepareComment("commentModelWithPage", "no subject", 0, userModel);

		//CommentModel with page and job
		commentModelWithPageJob = prepareComment("commentModelWithPageJob", "no subject", 0, userModel);
		printCollaborationFacade.createJob(commentModelWithPageJob, dummyAttachment, userModel);

		commentModelList = new ArrayList<CommentModel>();
		commentModelList.add(commentModel);
		commentModelList.add(commentModelWithJob);
		commentModelList.add(commentModelWithPage);
		commentModelList.add(commentModelWithPageJob);
	}

	/**
	 * @return the userModel
	 */
	public UserModel getUserModel()
	{
		return userModel;
	}

	/**
	 * @return the commentModel
	 */
	public CommentModel getCommentModel()
	{
		return commentModel;
	}

	/**
	 * @param commentModel1 the commentModel to set
	 */
	public void setCommentModel(final CommentModel commentModel1)
	{
		this.commentModel = commentModel1;
	}

	/**
	 * @return the commentModelWithJob
	 */
	public CommentModel getCommentModelWithJob()
	{
		return commentModelWithJob;
	}

	/**
	 * @param commentModelWithJob the commentModelWithJob to set
	 */
	public void setCommentModelWithJob(final CommentModel commentModelWithJob)
	{
		this.commentModelWithJob = commentModelWithJob;
	}

	/**
	 * @return the commentModelWithPage
	 */
	public CommentModel getCommentModelWithPage()
	{
		return commentModelWithPage;
	}

	/**
	 * @param commentModelWithPage the commentModelWithPage to set
	 */
	public void setCommentModelWithPage(final CommentModel commentModelWithPage)
	{
		this.commentModelWithPage = commentModelWithPage;
	}

	/**
	 * @return the commentModelWithPageJob
	 */
	public CommentModel getCommentModelWithPageJob()
	{
		return commentModelWithPageJob;
	}

	/**
	 * @param commentModelWithPageJob the commentModelWithPageJob to set
	 */
	public void setCommentModelWithPageJob(final CommentModel commentModelWithPageJob)
	{
		this.commentModelWithPageJob = commentModelWithPageJob;
	}

	/**
	 * @return the printCollaborationFacade
	 */
	public PrintCollaborationFacade getPrintCollaborationFacade()
	{
		return printCollaborationFacade;
	}

	/**
	 * @return the commentModelList
	 */
	public List<CommentModel> getCommentModelList()
	{
		return commentModelList;
	}

	/**
	 * @return the dummyAttachment
	 */
	public PageModel getDummyAttachment()
	{
		return dummyAttachment;
	}

	/**
	 * @return the commentGroup
	 */
	public CommentGroupModel getCommentGroup()
	{
		return commentGroup;
	}
}
