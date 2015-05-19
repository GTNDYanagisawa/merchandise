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
package de.hybris.platform.b2b.services.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTest;
import de.hybris.platform.b2b.WorkflowIntegrationTest;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class B2BOrder2POEApprovalProcessIntegrationTest extends WorkflowIntegrationTest
{
	@Resource
	public B2BWorkflowIntegrationService b2bWorkflowIntegrationService;
	@Resource
	public BaseDao baseDao;

	@Before
	public void before() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
		userService.setCurrentUser(userService.getAdminUser());
		i18nService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));
	}

	@Test
	public void shouldStart2POEApprovalProcessAndAssertAproval() throws Exception
	{
		final OrderModel order = createOrder("2POE S HH", 200, OrderStatus.CREATED);
		Assert.assertNotNull(order);
		final B2BApprovalProcessModel b2bApprovalProcessModel = baseDao.findFirstByAttribute(B2BApprovalProcessModel.ORDER, order,
				B2BApprovalProcessModel.class);
		Assert.assertNotNull(b2bApprovalProcessModel);
		Assert.assertTrue(b2bApprovalProcessModel.getProcessDefinitionName().equals("twoPairOfEyesApproval"));

		// approve workflow
		if (this.waitForProcessAction(b2bApprovalProcessModel.getCode(), "waitProcess", 60000))
		{
			// make sure order has an approval workflow
			this.modelService.refresh(order);
			Assert.assertNotNull(order.getWorkflow());

			modelService.refresh(order);
			final Collection<WorkflowActionModel> actions = b2bWorkflowIntegrationService.getStartWorkflowActions(order
					.getWorkflow());
			final Collection<B2BPermissionResultModel> permissions = order.getPermissionResults();

			// make sure we have 2 start actions to be executed.
			Assert.assertEquals(2, actions.size());

			// check if we have 2 permissions
			Assert.assertEquals(2, permissions.size());

			// check if both approvers have a task
			final Collection<B2BCustomerModel> actionApprovers = new ArrayList<B2BCustomerModel>();
			final Collection<B2BCustomerModel> permissionApprovers = new ArrayList<B2BCustomerModel>();

			for (final Iterator actionsIterator = actions.iterator(); actionsIterator.hasNext();)
			{
				final WorkflowActionModel action = (WorkflowActionModel) actionsIterator.next();
				actionApprovers.add((B2BCustomerModel) action.getPrincipalAssigned());
			}

			for (final Iterator permissionsIterator = permissions.iterator(); permissionsIterator.hasNext();)
			{
				final B2BPermissionResultModel permission = (B2BPermissionResultModel) permissionsIterator.next();
				permissionApprovers.add(permission.getApprover());
			}

			Assert.assertTrue(permissionApprovers.containsAll(actionApprovers));

			for (final Iterator actionsIterator = actions.iterator(); actionsIterator.hasNext();)
			{
				final WorkflowActionModel action = (WorkflowActionModel) actionsIterator.next();
				// approve order for both approvers
				b2bWorkflowIntegrationService.decideAction(action, B2BWorkflowIntegrationService.DECISIONCODES.APPROVE.name());
			}
		}

		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 60000);
		this.modelService.refresh(b2bApprovalProcessModel);
		this.modelService.refresh(order);
		// assure process finished properly
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel.getProcessState());
		// assure order got approved
		Assert.assertEquals(OrderStatus.APPROVED, order.getStatus());

	}

	@Test
	@Ignore("refactor into selenium driven tests")
	public void shouldStart2POEApprovalProcessAndAssertRejection() throws Exception
	{
		final OrderModel order = createOrder("2POE S HH", 250, OrderStatus.CREATED);
		Assert.assertNotNull(order);

		final B2BApprovalProcessModel b2bApprovalProcessModel = baseDao.findFirstByAttribute(B2BApprovalProcessModel.ORDER, order,
				B2BApprovalProcessModel.class);

		Assert.assertNotNull(b2bApprovalProcessModel);
		Assert.assertTrue(b2bApprovalProcessModel.getProcessDefinitionName().equals("twoPairOfEyesApproval"));

		if (this.waitForProcessAction(b2bApprovalProcessModel.getCode(), "waitProcess", 60000))
		{
			this.modelService.refresh(order);
			Assert.assertNotNull(order.getWorkflow());

			modelService.refresh(order);
			final Collection<WorkflowActionModel> actions = b2bWorkflowIntegrationService.getStartWorkflowActions(order
					.getWorkflow());
			Assert.assertEquals(2, actions.size());

			final Iterator<WorkflowActionModel> actionIterator = actions.iterator();
			WorkflowActionModel action = actionIterator.next();
			// reject in first action
			b2bWorkflowIntegrationService.decideAction(action, B2BWorkflowIntegrationService.DECISIONCODES.REJECT.name());
			Assert.assertTrue(actionIterator.hasNext());
			action = actionIterator.next();
			// approve in second action
			b2bWorkflowIntegrationService.decideAction(action, B2BWorkflowIntegrationService.DECISIONCODES.APPROVE.name());
		}

		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 60000);
		// assure process finished properly
		this.modelService.refresh(b2bApprovalProcessModel);
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel.getProcessState());
		// assure order got rejected
		this.modelService.refresh(order);
		Assert.assertEquals(OrderStatus.REJECTED, order.getStatus());
	}

	@Test
	public void shouldStart2POEApprovalProcessForCustomerWithoutApproversAndAssertAssignedToAdmin() throws Exception
	{

		final UserGroupModel groupA = userService.getUserGroupForUID("2POE_APPROVERS_A");
		final UserGroupModel groupB = userService.getUserGroupForUID("2POE_APPROVERS_B");

		final UserModel modelCEO = userService.getUserForUID("2POE CEO");
		final UserModel modelCFO = userService.getUserForUID("2POE CFO");

		final Set<PrincipalModel> filteredSet = new HashSet<PrincipalModel>();
		filteredSet.addAll(groupA.getMembers());
		filteredSet.remove(modelCEO);
		groupA.setMembers(filteredSet);
		modelService.save(groupA);

		filteredSet.clear();
		filteredSet.addAll(groupB.getMembers());
		filteredSet.remove(modelCFO);
		groupB.setMembers(filteredSet);
		modelService.save(groupB);

		Assert.assertFalse(userService.isMemberOfGroup(modelCEO, groupA));
		Assert.assertFalse(userService.isMemberOfGroup(modelCEO, groupB));
		Assert.assertFalse(userService.isMemberOfGroup(modelCFO, groupA));
		Assert.assertFalse(userService.isMemberOfGroup(modelCFO, groupB));

		final OrderModel order = createOrder("2POE CEO", 250, OrderStatus.CREATED);
		Assert.assertNotNull(order);

		final B2BApprovalProcessModel b2bApprovalProcessModel = baseDao.findFirstByAttribute(B2BApprovalProcessModel.ORDER, order,
				B2BApprovalProcessModel.class);

		Assert.assertNotNull(b2bApprovalProcessModel);
		Assert.assertTrue(b2bApprovalProcessModel.getProcessDefinitionName().equals("twoPairOfEyesApproval"));
		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 60000);
		// assure process finished properly
		this.modelService.refresh(b2bApprovalProcessModel);
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel.getProcessState());
		// assure order got assigned to admin
		this.modelService.refresh(order);
		Assert.assertEquals(OrderStatus.ASSIGNED_TO_ADMIN, order.getStatus());

		final Set<PrincipalGroupModel> filterSet = new HashSet<PrincipalGroupModel>();
		filterSet.addAll(modelCEO.getGroups());
		filterSet.add(groupA);
		modelCEO.setGroups(filterSet);
		modelService.save(modelCEO);

		filterSet.clear();
		filterSet.addAll(modelCFO.getGroups());
		filterSet.add(groupB);
		modelCFO.setGroups(filterSet);
		modelService.save(modelCFO);

		Assert.assertTrue(userService.isMemberOfGroup(modelCEO, groupA));
		Assert.assertTrue(userService.isMemberOfGroup(modelCFO, groupB));
	}
}
