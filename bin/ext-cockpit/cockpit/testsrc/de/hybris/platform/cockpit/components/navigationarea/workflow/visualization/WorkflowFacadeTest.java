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
package de.hybris.platform.cockpit.components.navigationarea.workflow.visualization;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.components.navigationarea.workflow.visualization.WorkflowViewOptions.Options;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.spring.ctx.ScopeTenantIgnoreDocReader;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.daos.WorkflowDao;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;


/**
 * Test check {@link WorkflowFacade#getAllWorkflows(WorkflowViewOptions,WorkflowViewOptions,int,int)}
 */
@IntegrationTest
public class WorkflowFacadeTest extends ServicelayerTransactionalTest
{
	protected ApplicationContext applicationContext;

	@Before
	public void initApplicationContext() throws Exception
	{
		final GenericApplicationContext context = new GenericApplicationContext();
		context.setResourceLoader(new DefaultResourceLoader(Registry.class.getClassLoader()));
		context.setClassLoader(Registry.class.getClassLoader());
		context.getBeanFactory().setBeanClassLoader(Registry.class.getClassLoader());
		context.setParent(Registry.getGlobalApplicationContext());
		final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(context);
		xmlReader.setBeanClassLoader(Registry.class.getClassLoader());
		xmlReader.setDocumentReaderClass(ScopeTenantIgnoreDocReader.class);
		xmlReader.loadBeanDefinitions(getSpringConfigurationLocations());
		context.refresh();
		final AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
		beanFactory.autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
		this.applicationContext = context;
	}

	protected String[] getSpringConfigurationLocations()
	{
		return new String[]
		{ "classpath:/cockpit/cockpit-spring-wrappers.xml",//
				"classpath:/cockpit/cockpit-spring-services.xml", //
		};
	}

	private final static String WORKFLOW_USER_ID = "workflowTestUser";
	private final static String WORKFLOW_TEMPLATE_CODE = "SampleWorkflowPredecessor";
	// DAOs and services
	@Resource
	private WorkflowDao defaultWorkflowDao;
	//

	@Resource
	private UserService userService;

	@Resource
	private ModelService modelService;

	@Resource
	private WorkflowTemplateService workflowTemplateService;

	private WorkflowFacade getWorkflowFacade()
	{
		return applicationContext.getBean("workflowFacade", WorkflowFacade.class);
	}

	private static final WorkflowViewOptions WFL_OPTIONS;
	private static final WorkflowViewOptions ADHOC_WFL_OPTIONS;

	static
	{
		WFL_OPTIONS = new WorkflowViewOptions();
		WFL_OPTIONS.unselect(Options.FINISHED);
		WFL_OPTIONS.unselect(Options.FROM_TO);
		WFL_OPTIONS.unselect(Options.PLANNED);
		WFL_OPTIONS.unselect(Options.RUNNING);
		WFL_OPTIONS.unselect(Options.TERMINATED);

		ADHOC_WFL_OPTIONS = new WorkflowViewOptions();
		ADHOC_WFL_OPTIONS.unselect(Options.FINISHED);
		ADHOC_WFL_OPTIONS.unselect(Options.FROM_TO);
		ADHOC_WFL_OPTIONS.unselect(Options.PLANNED);
		ADHOC_WFL_OPTIONS.unselect(Options.RUNNING);
		ADHOC_WFL_OPTIONS.unselect(Options.TERMINATED);
	}

	@Before
	public void setUp() throws Exception
	{
		//given
		createCoreData();
		createDefaultCatalog();
		importCsv("/workflow/testdata/workflow_test_data.csv", "windows-1252");
		removeWorkflowsFromImpex();

	}

	private Set<String> addNewWorkflows(final int count)
	{
		final Set<String> ids = new HashSet<String>();

		final WorkflowTemplateModel normalTemplate = workflowTemplateService.getWorkflowTemplateForCode(WORKFLOW_TEMPLATE_CODE);
		final UserModel owner = userService.getUserForUID(WORKFLOW_USER_ID);

		for (int i = 0; i < count; i++)
		{
			final WorkflowModel workflow = modelService.create(WorkflowModel.class);
			final String code = "normal_" + (i + 1);
			workflow.setCode(code);
			ids.add(code);
			final JobModel jobModel = normalTemplate;
			workflow.setJob(jobModel);
			workflow.setOwner(owner);
			modelService.save(workflow);
		}

		return ids;
	}

	private Set<String> addNewAdhocWorkflows(final int count)
	{
		final Set<String> ids = new HashSet<String>();
		final WorkflowTemplateModel normalTemplate = workflowTemplateService.getAdhocWorkflowTemplate();
		final UserModel owner = userService.getUserForUID(WORKFLOW_USER_ID);

		for (int i = 0; i < count; i++)
		{
			final WorkflowModel workflow = modelService.create(WorkflowModel.class);
			final String code = "adhoc_" + (i + 1);
			workflow.setCode(code);
			ids.add(code);
			final JobModel jobModel = normalTemplate;
			workflow.setJob(jobModel);
			workflow.setOwner(owner);
			modelService.save(workflow);
		}
		return ids;
	}

	private void removeWorkflowsFromImpex()
	{
		setSessionUser(WORKFLOW_USER_ID);
		List<WorkflowModel> workflows = defaultWorkflowDao.findAllWorkflows(null, null);
		List<WorkflowModel> adhocWorkflows = defaultWorkflowDao.findAllAdhocWorkflows(null, null);
		for (final WorkflowModel w : workflows)
		{
			modelService.remove(w);
		}

		for (final WorkflowModel w : adhocWorkflows)
		{
			modelService.remove(w);
		}

		workflows = defaultWorkflowDao.findAllWorkflows(null, null);
		adhocWorkflows = defaultWorkflowDao.findAllAdhocWorkflows(null, null);
		assertThat(workflows).hasSize(0);
		assertThat(adhocWorkflows).hasSize(0);
	}

	private UserModel setSessionUser(final String uid)
	{
		final UserModel sessionUser = userService.getUserForUID(uid);
		userService.setCurrentUser(sessionUser);
		return sessionUser;
	}

	@Test
	public void testGetPageOfWorkflowsWhenOnlyNormalWorkflowsExist()
	{
		final int numberOfWorkflows = 10;
		int startIndex = 0;
		final int pageSize = 3;

		setSessionUser(WORKFLOW_USER_ID);
		final Set<String> expectedWorkflowIds = addNewWorkflows(numberOfWorkflows);
		final Set<String> fetchedWorkflowIds = new HashSet<String>();
		int totalFetchedWorkflows = 0;

		while (totalFetchedWorkflows < numberOfWorkflows)
		{
			final SearchResult<WorkflowModel> allWorkflows = getWorkflowFacade().getAllWorkflows(WFL_OPTIONS, ADHOC_WFL_OPTIONS,
					startIndex, pageSize);
			final int expectedNumberFetchedWorkflows = Math.min(pageSize, numberOfWorkflows - totalFetchedWorkflows);
			assertThat(allWorkflows.getResult()).hasSize(expectedNumberFetchedWorkflows);
			assertThat(allWorkflows.getCount()).isEqualTo(expectedNumberFetchedWorkflows);
			assertThat(allWorkflows.getTotalCount()).isEqualTo(numberOfWorkflows);
			totalFetchedWorkflows += allWorkflows.getResult().size();
			startIndex += pageSize;

			for (final WorkflowModel workflow : allWorkflows.getResult())
			{
				fetchedWorkflowIds.add(workflow.getCode());
			}
		}

		assertThat(totalFetchedWorkflows).isEqualTo(numberOfWorkflows);
		assertThat(expectedWorkflowIds).isEqualTo(fetchedWorkflowIds);
	}

	@Test
	public void testGetPageOfWorkflowsWhenOnlyAdhocWorkflowsExist()
	{
		final int numberOfWorkflows = 10;
		int startIndex = 0;
		final int pageSize = 3;

		setSessionUser(WORKFLOW_USER_ID);
		final Set<String> expectedAdhocWorkflowIds = addNewAdhocWorkflows(numberOfWorkflows);
		final Set<String> fetchedAdhocWorkflowIds = new HashSet<String>();
		int totalFetchedWorkflows = 0;

		while (totalFetchedWorkflows < numberOfWorkflows)
		{
			final SearchResult<WorkflowModel> allWorkflows = getWorkflowFacade().getAllWorkflows(WFL_OPTIONS, ADHOC_WFL_OPTIONS,
					startIndex, pageSize);
			final int expectedNumberFetchedWorkflows = Math.min(pageSize, numberOfWorkflows - totalFetchedWorkflows);
			assertThat(allWorkflows.getResult()).hasSize(expectedNumberFetchedWorkflows);
			assertThat(allWorkflows.getCount()).isEqualTo(expectedNumberFetchedWorkflows);
			assertThat(allWorkflows.getTotalCount()).isEqualTo(numberOfWorkflows);
			totalFetchedWorkflows += allWorkflows.getResult().size();
			startIndex += pageSize;

			for (final WorkflowModel workflow : allWorkflows.getResult())
			{
				fetchedAdhocWorkflowIds.add(workflow.getCode());
			}
		}

		assertThat(totalFetchedWorkflows).isEqualTo(numberOfWorkflows);
		assertThat(expectedAdhocWorkflowIds).isEqualTo(fetchedAdhocWorkflowIds);
	}

	@Test
	public void testGetPageOfWorkflowsWhenAdhocAndNormalWorkflowsExist()
	{
		final int numberOfAdhocWorkflows = 10;
		final int numberOfNormalWorkflows = 10;

		final int numberOfAllWorkflows = numberOfAdhocWorkflows + numberOfNormalWorkflows;
		int startIndex = 0;
		final int pageSize = 3;

		setSessionUser(WORKFLOW_USER_ID);
		final Set<String> expectedAdhocWorkflowIds = addNewAdhocWorkflows(numberOfAdhocWorkflows);
		final Set<String> expectedNormalWorkflowIds = addNewWorkflows(numberOfNormalWorkflows);
		final Set<String> expectedAllWorkflowIds = new HashSet<String>();
		expectedAllWorkflowIds.addAll(expectedAdhocWorkflowIds);
		expectedAllWorkflowIds.addAll(expectedNormalWorkflowIds);
		final Set<String> fetchedAdhocWorkflowIds = new HashSet<String>();
		int totalFetchedWorkflows = 0;

		while (totalFetchedWorkflows < numberOfAllWorkflows)
		{
			final SearchResult<WorkflowModel> allWorkflows = getWorkflowFacade().getAllWorkflows(WFL_OPTIONS, ADHOC_WFL_OPTIONS,
					startIndex, pageSize);
			final int expectedNumberFetchedWorkflows = Math.min(pageSize, numberOfAllWorkflows - totalFetchedWorkflows);
			assertThat(allWorkflows.getResult()).hasSize(expectedNumberFetchedWorkflows);
			assertThat(allWorkflows.getCount()).isEqualTo(expectedNumberFetchedWorkflows);
			assertThat(allWorkflows.getTotalCount()).isEqualTo(numberOfAllWorkflows);
			totalFetchedWorkflows += allWorkflows.getResult().size();
			startIndex += pageSize;

			for (final WorkflowModel workflow : allWorkflows.getResult())
			{
				fetchedAdhocWorkflowIds.add(workflow.getCode());
			}
		}

		assertThat(totalFetchedWorkflows).isEqualTo(numberOfAllWorkflows);
		assertThat(expectedAllWorkflowIds).isEqualTo(fetchedAdhocWorkflowIds);
	}
}
