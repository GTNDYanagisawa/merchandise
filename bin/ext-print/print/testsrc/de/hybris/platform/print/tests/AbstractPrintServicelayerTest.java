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
package de.hybris.platform.print.tests;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.cockpit.model.CommentMetadataModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.link.LinkModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.link.Link;
import de.hybris.platform.jalo.security.AccessManager;
import de.hybris.platform.jalo.security.UserRight;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.print.enums.GridMode;
import de.hybris.platform.print.enums.PageMode;
import de.hybris.platform.print.model.ChapterModel;
import de.hybris.platform.print.model.CometConfigurationModel;
import de.hybris.platform.print.model.GridElementModel;
import de.hybris.platform.print.model.GridModel;
import de.hybris.platform.print.model.LayoutTemplateModel;
import de.hybris.platform.print.model.PageFormatModel;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.print.model.PlacementModel;
import de.hybris.platform.print.model.PublicationModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.constants.WorkflowConstants;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.jalo.WorkflowAction;
import de.hybris.platform.workflow.model.AbstractWorkflowActionModel;
import de.hybris.platform.workflow.model.AbstractWorkflowDecisionModel;
import de.hybris.platform.workflow.model.AutomatedWorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;


@Ignore
public abstract class AbstractPrintServicelayerTest extends ServicelayerTransactionalTest
{

	final private static Logger LOG = Logger.getLogger(AbstractPrintServicelayerTest.class);

	private LayoutTemplateModel defaultTemplate = null;

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	@Resource
	private ConfigurationService configurationService;

	@Resource
	private CatalogService catalogService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	private CometConfigurationModel cometConfig;
	private ChapterModel chapter;
	private PublicationModel publication;
	private PageFormatModel pageFormat;

	private String printJobWorkflowName;


	@Before
	public void setUp() throws Exception
	{
		createCometConfiguration();
		createLayoutTemplate();
		createPageFormat();
		createPublication();
		createChapter();

		printJobWorkflowName = configurationService.getConfiguration().getString("print.comment.workflow.name",
				"Auto-Created PrintJob Workflow");

	}

	/**
	 * Creates a user instance using given uid and assign him read access to WorkflowActions
	 * @param userName user id used for user creation
	 * @return created user
	 */
	protected UserModel createUser(final String userName)
	{
		User user = null;
		try
		{
			user = UserManager.getInstance().createUser(userName);
			final UserRight readRight = AccessManager.getInstance().getOrCreateUserRightByCode(AccessManager.READ);
			assertNotNull("UserRight should not be null", readRight);
			TypeManager.getInstance().getComposedType(WorkflowAction.class).addPositivePermission(user, readRight);
			assertNotNull("User should not be null", user);
		}
		catch (final ConsistencyCheckException e)
		{
			fail("Can not create user caused by: " + e.getMessage());
		}
		return modelService.get(user);
	}

	protected GridModel prepareGrid(final int gridElementsCount, final int id)
	{
		final GridModel grid = modelService.create(GridModel.class);

		grid.setCometConfig(cometConfig);
		grid.setId(Integer.valueOf(id));

		final List<GridElementModel> gridElements = new ArrayList<GridElementModel>(gridElementsCount);

		for (int i = 0; i < gridElementsCount; i++)
		{
			final GridElementModel gridElement = modelService.create(GridElementModel.class);
			gridElement.setDefaultLayoutTemplate(defaultTemplate);
			gridElement.setId(Integer.valueOf(i));
			gridElement.setGrid(grid);
			modelService.save(gridElement);
			gridElements.add(gridElement);
		}

		grid.setElements(gridElements);
		modelService.save(grid);
		return grid;
	}

	protected PageModel preparePage()
	{
		final PageModel page = modelService.create(PageModel.class);
		page.setPageMode(PageMode.SINGLE);
		page.setGrid(prepareGrid(1, 1));
		page.setGridMode(GridMode.FIXED);
		page.setPlacements(new ArrayList<PlacementModel>());

		page.setCode("TestPage" + System.currentTimeMillis());
		page.setChapter(chapter);
		page.setPublication(publication);
		page.setId("TestPageID" + System.currentTimeMillis());

		modelService.save(page);
		return page;
	}

	protected void createChapter()
	{
		chapter = modelService.create(ChapterModel.class);
		chapter.setCode("TestChapter");
		chapter.setPublication(publication);
		modelService.save(chapter);
	}

	protected void createPublication()
	{
		publication = modelService.create(PublicationModel.class);
		publication.setCode("TestPublication");
		publication.setPageFormat(pageFormat);
		publication.setConfiguration(cometConfig);
		modelService.save(publication);
	}

	protected void createPageFormat()
	{
		pageFormat = modelService.create(PageFormatModel.class);
		pageFormat.setName("TestPageFormat");
		pageFormat.setWidth(Double.valueOf(10d));
		pageFormat.setHeight(Double.valueOf(10d));
		pageFormat.setQualifier("testQualifier");
		modelService.save(pageFormat);
	}

	protected void createLayoutTemplate()
	{
		defaultTemplate = modelService.create(LayoutTemplateModel.class);
		defaultTemplate.setCode("TestTemplate");
		defaultTemplate.setCometConfig(cometConfig);
		defaultTemplate.setId(Integer.valueOf(1));
	}

	protected void createCometConfiguration()
	{
		cometConfig = modelService.create(CometConfigurationModel.class);
		cometConfig.setCode("TestCometConfiguration");
		modelService.save(cometConfig);
	}

	protected CommentModel prepareComment(final String code, final String subject, final int pageIndex, final UserModel assignee)
	{
		final CommentModel comment = modelService.create(CommentModel.class);
		comment.setCode(code);
		comment.setText(subject + "_text");

		final DomainModel domain = modelService.<DomainModel> create(DomainModel.class);
		domain.setCode(code + "_domain");

		final ComponentModel component = modelService.<ComponentModel> create(ComponentModel.class);
		component.setCode(code + "_component");
		component.setDomain(domain);

		final CommentTypeModel type = modelService.<CommentTypeModel> create(CommentTypeModel.class);
		type.setCode(code + "_commentType");
		type.setComments(Collections.singletonList(comment));
		type.setDomain(domain);

		comment.setCommentType(type);
		comment.setSubject(subject);
		comment.setOwner(assignee);
		comment.setAuthor(assignee);
		comment.setComponent(component);


		modelService.save(comment);

		return comment;
	}

	/**
	 * Create metadata to place comment on a page
	 */
	protected CommentModel prepareCommentWithMetadata(final String code, final String subject, final int pageIndex,
			final UserModel assignee)
	{
		final CommentModel comment = prepareComment(code, subject, pageIndex, assignee);

		assignCommentMetadata(comment, pageIndex);

		return comment;
	}

	/**
	 * Assign metadata to comment
	 */
	private void assignCommentMetadata(final CommentModel comment, final int pageIndex)
	{
		final CommentMetadataModel meta = modelService.create(CommentMetadataModel.class);
		meta.setPageIndex(Integer.valueOf(pageIndex));
		meta.setX(Integer.valueOf(10));
		meta.setY(Integer.valueOf(10));
		meta.setComment(comment);
		comment.setCommentMetadata(Collections.singleton(meta));
	}

	/**
	 * Creates new basic PrintJob workflow template with 3 WorkflowActions and the transition 1-->2, 2-->1 and 2-->3
	 * @param owner user which will be the owner of the workflow template assignee for WorkflowAction 1 and 3
	 * @param assignee user which will be assignee for WorkflowAction 2
	 * @return created WorkflowTemplate with 3 WorkflowActions and transitions 1-->2, 2-->1 and 2-->3
	 */
	protected WorkflowTemplateModel createPrintJobWorkflowTemplate(final UserModel owner, final UserModel assignee)
	{
		final String templateName = configurationService.getConfiguration().getString("print.comment.workflow.template",
				"PrintJob.Workflow");

		final WorkflowTemplateModel template = createWorkflowTemplate(owner, templateName, templateName);
		final WorkflowActionTemplateModel templateAction1 = createWorkflowActionTemplateModel(owner, "start",
				WorkflowActionType.START, template);
		final WorkflowActionTemplateModel templateAction2 = createWorkflowActionTemplateModel(assignee, "middle",
				WorkflowActionType.NORMAL, template);
		final WorkflowActionTemplateModel templateAction3 = createWorkflowActionTemplateModel(owner, "end", WorkflowActionType.END,
				template);

		final WorkflowDecisionTemplateModel templateDecision12 = createWorkflowDecisionTemplate("decision12", templateAction1);
		final WorkflowDecisionTemplateModel templateDecision21 = createWorkflowDecisionTemplate("decision21", templateAction2);
		final WorkflowDecisionTemplateModel templateDecision23 = createWorkflowDecisionTemplate("decision23", templateAction2);

		createWorkflowActionTemplateModelLinkTemplateRelation(templateDecision12, templateAction2, Boolean.FALSE);
		createWorkflowActionTemplateModelLinkTemplateRelation(templateDecision21, templateAction1, Boolean.FALSE);
		createWorkflowActionTemplateModelLinkTemplateRelation(templateDecision23, templateAction3, Boolean.FALSE);

		modelService.saveAll();
		return template;
	}

	/**
	 * Creates new workflow template using given user, code, and description.
	 * @param owner user assigned to new template
	 * @param code code of new template
	 * @param desc description assigned to template
	 * @return created template
	 */
	protected WorkflowTemplateModel createWorkflowTemplate(final UserModel owner, final String code, final String desc)
	{
		return createWorkflowTemplate(owner, code, desc, null);
	}

	/**
	 * Creates new workflow template using given user, code, description and activation script.
	 * @param owner user assigned to new template
	 * @param code code of new template
	 * @param desc description assigned to template
	 * @param activationScript code of the activation script
	 * @return created template
	 */
	protected WorkflowTemplateModel createWorkflowTemplate(final UserModel owner, final String code, final String desc,
			final String activationScript)
	{
		final WorkflowTemplateModel template = modelService.create(WorkflowTemplateModel.class);
		template.setOwner(owner);
		template.setCode(code);
		template.setDescription(desc);
		template.setActivationScript(activationScript);

		modelService.save(template);

		assertNotNull("Template should not be null", template);
		assertThat(modelService.isUpToDate(template)).isTrue();
		return template;
	}

	/**
	 * Creates new action template.
	 * @param user user assigned to template
	 * @param code code of template
	 * @param workflow workflow assigned to action template
	 * @return created action template
	 */
	protected WorkflowActionTemplateModel createWorkflowActionTemplateModel(final UserModel user, final String code,
			final WorkflowActionType actionType, final WorkflowTemplateModel workflow)
	{
		final WorkflowActionTemplateModel action = modelService.create(WorkflowActionTemplateModel.class);
		action.setPrincipalAssigned(user);
		action.setWorkflow(workflow);
		action.setCode(code);
		action.setSendEmail(Boolean.FALSE);
		action.setActionType(actionType);
		modelService.save(action);

		final List<WorkflowActionTemplateModel> coll = new ArrayList(workflow.getActions());
		coll.add(action);
		workflow.setActions(coll);
		modelService.save(workflow);

		assertNotNull("Action Template should not be null", action);
		return action;
	}

	/**
	 * Creates new automated action template.
	 * @param user user assigned to template
	 * @param code code of template
	 * @param workflow workflow assigned to action template
	 * @return created action template
	 */
	protected AutomatedWorkflowActionTemplateModel createAutomatedWorkflowActionTemplateModel(final UserModel user,
			final String code, final WorkflowActionType actionType, final WorkflowTemplateModel workflow, final Class jobClass)
	{
		final AutomatedWorkflowActionTemplateModel action = modelService.create(AutomatedWorkflowActionTemplateModel.class);
		action.setPrincipalAssigned(user);
		action.setWorkflow(workflow);
		action.setCode(code);
		action.setSendEmail(Boolean.FALSE);
		action.setActionType(actionType);
		action.setJobClass(jobClass);
		modelService.save(action);

		final List<WorkflowActionTemplateModel> coll = new ArrayList(workflow.getActions());
		coll.add(action);
		workflow.setActions(coll);
		modelService.save(workflow);

		assertNotNull("Automated Action Template should not be null", action);
		return action;
	}

	/**
	 * Creates new decision template.
	 * @param code code of template
	 * @param actionTemplate action template assigned to decision template
	 * @return created decision template
	 */
	protected WorkflowDecisionTemplateModel createWorkflowDecisionTemplate(final String code,
			final WorkflowActionTemplateModel actionTemplate)
	{
		final WorkflowDecisionTemplateModel decision = modelService.create(WorkflowDecisionTemplateModel.class);
		decision.setCode(code);
		decision.setActionTemplate(actionTemplate);
		modelService.save(decision);


		final Collection<WorkflowDecisionTemplateModel> coll = new ArrayList(actionTemplate.getDecisionTemplates());
		coll.add(decision);
		actionTemplate.setDecisionTemplates(coll);
		modelService.save(actionTemplate);

		assertNotNull("The decision should not be null", decision);
		assertThat(modelService.isUpToDate(decision)).isTrue();
		return decision;
	}

	/**
	 * @param decisionTemplate
	 * @param toAction
	 * @param hasAndConnection
	 */
	protected void createWorkflowActionTemplateModelLinkTemplateRelation(final WorkflowDecisionTemplateModel decisionTemplate,
			final WorkflowActionTemplateModel toAction, final Boolean hasAndConnection)
	{
		final List<WorkflowActionTemplateModel> toTemplateActions = new ArrayList<WorkflowActionTemplateModel>(
				decisionTemplate.getToTemplateActions());
		toTemplateActions.add(toAction);
		decisionTemplate.setToTemplateActions(toTemplateActions);
		modelService.save(decisionTemplate);

		assertThat(modelService.isUpToDate(decisionTemplate)).isTrue();

		final Collection<LinkModel> incomingLinkList = getLinkTemplates(decisionTemplate, toAction);

		for (final LinkModel link : incomingLinkList)
		{
			setAndConnectionTemplate(link, hasAndConnection);
		}

	}

	private Collection<LinkModel> getLinkTemplates(final AbstractWorkflowDecisionModel decision,
			final AbstractWorkflowActionModel action)
	{
		SearchResult<LinkModel> results;
		final Map params = new HashMap();
		params.put("desc", decision);
		params.put("act", action);

		if (decision == null && action == null)
		{
			fail("Decision and action cannot both be null");
			return null;
		}
		else if (action == null)
		{
			results = flexibleSearchService.search("SELECT {" + ItemModel.PK + "} from {"
					+ "WorkflowActionTemplateLinkTemplateRelation" + "} where {" + LinkModel.SOURCE + "}=?desc", params);
		}
		else if (decision == null)
		{
			results = flexibleSearchService.search("SELECT {" + ItemModel.PK + "} from {"
					+ "WorkflowActionTemplateLinkTemplateRelation" + "} where {" + LinkModel.TARGET + "}=?act", params);
		}
		else
		{
			results = flexibleSearchService.search("SELECT {" + ItemModel.PK + "} from {"
					+ "WorkflowActionTemplateLinkTemplateRelation" + "} where {" + LinkModel.SOURCE + "}=?desc AND {"
					+ LinkModel.TARGET + "}=?act", params);
			if (results.getTotalCount() != 1)
			{
				// if source and target are specified only an Item can be returned.
				LOG.error("There is more than one WorkflowActionTemplateLinkTemplateRelation for source '" + decision.getCode()
						+ "' and target '" + action.getCode() + "'");
			}
		}
		return results.getResult();
	}

	private void setAndConnectionTemplate(final LinkModel item, final Boolean value)
	{
		final Link itemSource = modelService.getSource(item);
		itemSource
				.setProperty(WorkflowConstants.Attributes.WorkflowActionTemplateLinkTemplateRelation.ANDCONNECTIONTEMPLATE, value);
	}

	/**
	 * Gets the decision with given code from test workflow instance.
	 * @param code code of needed decision
	 * @return decision of test workflow with given code
	 */
	protected WorkflowDecisionModel getDecision(final String code, final WorkflowActionModel action)
	{
		final Collection<WorkflowDecisionModel> decisions = action.getDecisions();
		for (final WorkflowDecisionModel decision : decisions)
		{
			if (decision.getCode().equals(code))
			{
				return decision;
			}
		}
		fail("Decision " + code + "can not be found");
		return null;
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @return the userService
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @return the catalogService
	 */
	protected CatalogService getCatalogService()
	{
		return catalogService;
	}

	/**
	 * @return the flexibleSearchService
	 */
	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @return the printJobWorkflowName
	 */
	protected String getPrintJobWorkflowName()
	{
		return printJobWorkflowName;
	}

}
