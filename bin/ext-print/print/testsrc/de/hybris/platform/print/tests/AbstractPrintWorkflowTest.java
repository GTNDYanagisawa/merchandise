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
package de.hybris.platform.print.tests;

import static org.junit.Assert.fail;

import de.hybris.platform.cockpit.model.CommentMetadataModel;
import de.hybris.platform.comments.model.CommentGroupModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.security.AccessManager;
import de.hybris.platform.jalo.security.Principal;
import de.hybris.platform.jalo.security.UserRight;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.print.collaboration.facades.PrintCollaborationFacade;
import de.hybris.platform.print.jalo.PrintManager;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.workflow.jalo.WorkflowAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;



public abstract class AbstractPrintWorkflowTest extends AbstractPrintServicelayerTest
{

	@Resource
	private PrintCollaborationFacade printCollaborationFacade;

	@Resource
	private SessionService sessionService;

	private UserModel layouter;
	private UserModel printmanager;

	private UserGroupModel layoutergroup;
	private UserGroupModel printadmingroup;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		createUserData();
		createWorkflowData();
	}

	public void createUserData() throws Exception
	{
		final UserGroupModel cockpitgroup = createUserGroup("cockpitgroup", null);
		final UserGroupModel printgroup = createUserGroup("printgroup", cockpitgroup);
		layoutergroup = createUserGroup("layoutergroup", printgroup);
		printadmingroup = createUserGroup("printadmingroup", printgroup);

		layouter = createEmployee("layouter", layoutergroup);
		printmanager = createEmployee("printmanager", printadmingroup);

		PrintManager.getInstance().importCSVFromResources("/print/import/printuserrights.csv");

		// Setting read permissions to the users explicitly as we don't need to check user/group rights later on
		final UserRight readRight = AccessManager.getInstance().getOrCreateUserRightByCode(AccessManager.READ);
		TypeManager.getInstance().getComposedType(WorkflowAction.class)
				.addPositivePermission((Principal) getModelService().getSource(layouter), readRight);
		TypeManager.getInstance().getComposedType(WorkflowAction.class)
				.addPositivePermission((Principal) getModelService().getSource(printmanager), readRight);
	}

	public void createWorkflowData() throws Exception
	{
		//import essential comment data
		PrintManager.getInstance().importCSVFromResources("/print/import/test/Print_8_jobworkflowtemplateTest.csv");
	}

	protected CommentModel createComment(final PageModel page, final String subject, final String text, final UserModel user)
	{
		//create comment
		final CommentModel comment = new CommentModel();
		comment.setSubject(subject);
		comment.setText(text);
		comment.setAuthor(user);

		//create type
		final CommentTypeModel commentType = getModelService().create(CommentTypeModel.class);
		comment.setCommentType(commentType);

		//create component
		final ComponentModel component = getModelService().create(ComponentModel.class);

		//create domain
		final DomainModel domain = getModelService().create(DomainModel.class);
		domain.setCode("TestDomain" + System.currentTimeMillis());
		commentType.setDomain(domain);
		component.setDomain(domain);
		comment.setComponent(component);
		getModelService().save(comment);

		//create list required for page
		final List<CommentModel> commentList = new ArrayList<CommentModel>(page.getComments());
		commentList.add(comment);

		//set page comment
		page.setComments(commentList);
		getModelService().save(page);

		return comment;
	}

	protected CommentGroupModel createCommentGroup(final ItemModel item)
	{
		final CommentGroupModel commentGroup = new CommentGroupModel();
		commentGroup.setCode("commentGroup");
		commentGroup.setItem(item);

		getModelService().save(commentGroup);

		return commentGroup;
	}

	protected CommentMetadataModel createCommentMetadata(final ItemModel item, final CommentModel comment)
	{
		final CommentMetadataModel commentMetadata = getModelService().create(CommentMetadataModel.class);
		commentMetadata.setX(Integer.valueOf(0));
		commentMetadata.setY(Integer.valueOf(0));
		commentMetadata.setPageIndex(Integer.valueOf(0));

		commentMetadata.setComment(comment);
		commentMetadata.setItem(item);
		getModelService().save(commentMetadata);
		return commentMetadata;
	}

	protected CommentTypeModel createCommentType()
	{
		final CommentTypeModel commentType = new CommentTypeModel();
		commentType.setCode("comment");
		commentType.setDomain(createDomain());
		return getOrCreateItem(commentType);
	}

	protected ComponentModel createComponent()
	{
		final ComponentModel component = new ComponentModel();
		component.setCode("cockpit");
		component.setDomain(createDomain());
		return getOrCreateItem(component);
	}

	protected DomainModel createDomain()
	{
		final DomainModel domain = new DomainModel();
		domain.setCode("cockpit");
		return getOrCreateItem(domain);
	}

	protected EmployeeModel createEmployee(final String uid, final UserGroupModel group)
	{
		final EmployeeModel example = new EmployeeModel();
		example.setUid(uid);

		final EmployeeModel user = getOrCreateItem(example);

		if (group != null)
		{
			user.setGroups(Collections.<PrincipalGroupModel> singleton(group));
			getModelService().save(user);
		}

		return user;
	}

	protected UserModel createUser(final String uid, final UserGroupModel group)
	{
		final UserModel example = new UserModel();
		example.setUid(uid);

		final UserModel user = getOrCreateItem(example);

		if (group != null)
		{
			user.setGroups(Collections.<PrincipalGroupModel> singleton(group));
			getModelService().save(user);
		}

		return user;
	}

	protected UserGroupModel createUserGroup(final String uid, final UserGroupModel parent)
	{
		final UserGroupModel example = new UserGroupModel();
		example.setUid(uid);

		final UserGroupModel group = getOrCreateItem(example);

		if (parent != null)
		{
			group.setGroups(Collections.<PrincipalGroupModel> singleton(parent));
			getModelService().save(group);
		}

		return group;
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

	protected CommentModel isCommentExists(final String subject)
	{
		//create comment to search for
		final CommentModel comment = new CommentModel();
		comment.setSubject(subject);
		final CommentModel foundComment = getFlexibleSearchService().getModelByExample(comment);

		//check whether comment was found
		if (foundComment == null)
		{
			fail("Required comment not found");
		}
		return foundComment;
	}

	protected PrintCollaborationFacade getPrintCollaborationFacade()
	{
		return printCollaborationFacade;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	protected UserModel getLayouter()
	{
		return layouter;
	}

	protected UserModel getPrintmanager()
	{
		return printmanager;
	}

	protected UserGroupModel getLayoutergroup()
	{
		return layoutergroup;
	}

	protected UserGroupModel getPrintadmingroup()
	{
		return printadmingroup;
	}

}
