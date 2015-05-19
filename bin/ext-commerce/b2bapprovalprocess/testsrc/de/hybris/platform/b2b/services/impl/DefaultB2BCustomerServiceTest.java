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
import de.hybris.platform.b2b.B2BIntegrationTransactionalTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import java.util.Locale;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


/**
 * Integration test for B2BCustomerService read before implementing
 * https://wiki.hybris.com/display/general/Coding+Practices+and+Principles
 */
@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class DefaultB2BCustomerServiceTest extends B2BIntegrationTransactionalTest
{
	@Before
	public void before() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
		sessionService.getCurrentSession().setAttribute("user",
				this.modelService.<Object> toPersistenceLayer(userService.getAdminUser()));
		i18nService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));
	}

	@Resource
	public B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;

	@Test
	public void testAddMember() throws Exception
	{
		final B2BCustomerModel user = login("GC CEO");
		final B2BUnitModel unitToBeAssigned = b2bUnitService.getUnitForUid("GC Sales UK");
		Assert.assertNotNull(unitToBeAssigned);
		b2bCustomerService.addMember(user, unitToBeAssigned);
		Assert.assertNotNull(CollectionUtils.find(user.getGroups(), new Predicate()
		{
			@Override
			public boolean evaluate(final Object result)
			{
				return ((PrincipalGroupModel) result).getUid().equals("GC Sales UK");
			}
		}));
	}

	@Test
	public void testSetParentB2BUnit() throws Exception
	{
		B2BCustomerModel user = login("GC CEO");
		final B2BUnitModel assignedUnit = b2bUnitService.getParent(user);
		Assert.assertNotNull(assignedUnit);
		final B2BUnitModel unitToBeAssigned = b2bUnitService.getUnitForUid("GC Sales UK");
		Assert.assertNotNull(unitToBeAssigned);
		b2bCustomerService.addMember(user, unitToBeAssigned);
		modelService.save(user);
		b2bUnitService.setCurrentUnit(user, unitToBeAssigned);
		user = (B2BCustomerModel) userService.getUserForUID("GC CEO");
		Assert.assertEquals(unitToBeAssigned, b2bUnitService.getParent(user));
	}


	@Test
	public void testGetCurrentB2BCustomer() throws Exception
	{
		login("GC CEO");
		Assert.assertEquals(userService.getCurrentUser(), b2bCustomerService.getCurrentB2BCustomer());
	}


	@Test
	@Deprecated
	public void testPrincipalExists() throws Exception
	{
		login("GC S HH");
		Assert.assertTrue(b2bCustomerService.principalExists("admin"));
	}
}
