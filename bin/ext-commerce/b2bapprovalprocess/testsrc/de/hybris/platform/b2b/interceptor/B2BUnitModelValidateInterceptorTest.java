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
package de.hybris.platform.b2b.interceptor;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class B2BUnitModelValidateInterceptorTest extends B2BIntegrationTest
{

	@Before
	public void beforeTest() throws Exception
	{
		de.hybris.platform.servicelayer.ServicelayerTest.createCoreData();
		de.hybris.platform.servicelayer.ServicelayerTest.createDefaultCatalog();
		de.hybris.platform.catalog.jalo.CatalogManager.getInstance().createEssentialData(java.util.Collections.EMPTY_MAP, null);
		importCsv("/impex/essentialdata_1_usergroups.impex", "UTF-8");
	}

	@Test
	public void shouldNotAllowUserToBecomeApprover() throws Exception
	{
		final B2BUnitModel unit = modelService.create(B2BUnitModel.class);
		unit.setUid("aUnit");

		final B2BCustomerModel customer = modelService.create(B2BCustomerModel.class);
		customer.setUid("test");
		customer.setName("test");
		customer.setEmail("test@test.com");

		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(customer.getGroups());
		groups.add(unit);
		customer.setGroups(groups);

		final Set<B2BCustomerModel> approvers = new HashSet<B2BCustomerModel>();
		approvers.add(customer);
		unit.setApprovers(approvers);

		try
		{
			modelService.save(unit);
		}
		catch (final ModelSavingException e)
		{
			Assert.assertTrue(e.getMessage().contains("error.b2bunit.approverNotMemberOfB2BApproverGroup"));
		}
	}

	@Test
	public void shouldAllowUserToBecomeApprover() throws Exception
	{
		final B2BUnitModel unit = modelService.create(B2BUnitModel.class);
		unit.setUid("aUnit");

		final B2BCustomerModel customer = modelService.create(B2BCustomerModel.class);
		customer.setUid("test");
		customer.setName("test");
		customer.setEmail("test@test.com");

		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(customer.getGroups());
		final UserGroupModel b2bApproverGroup = userService.getUserGroupForUID("b2bapprovergroup");
		groups.add(unit);
		groups.add(b2bApproverGroup);
		customer.setGroups(groups);

		final Set<B2BCustomerModel> approvers = new HashSet<B2BCustomerModel>();
		approvers.add(customer);
		unit.setApprovers(approvers);

		modelService.save(unit);
		Assert.assertFalse(modelService.isNew(unit));
		modelService.remove(unit);
	}
}
