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
import de.hybris.platform.b2b.model.B2BReportingSetModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BReportingService;
import de.hybris.platform.core.model.ItemModel;
import java.util.HashSet;
import java.util.Locale;
import javax.annotation.Resource;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class B2BReportingServiceTest extends B2BIntegrationTransactionalTest
{
	@Resource
	protected B2BReportingService b2bReportingService;

	@Before
	public void before() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
		//importCsv("/b2bapprovalprocess/test/organizationdata.csv", "UTF-8");

		sessionService.getCurrentSession().setAttribute("user",
				this.modelService.<Object> toPersistenceLayer(userService.getAdminUser()));
		i18nService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));
	}

	@Test
	public void testReportingService()
	{
		final B2BUnitModel unit = b2bUnitService.getUnitForUid("IC");
		Assert.assertNotSame(Integer.valueOf(0), Integer.valueOf(b2bUnitService.getBranch(unit).size()));
		B2BReportingSetModel set = b2bReportingService.getReportingSetForB2BUnit(unit);
		if (set == null)
		{

			set = modelService.create(B2BReportingSetModel.class);
		}

		set.setReportingEntries(new HashSet<ItemModel>(b2bUnitService.getBranch(unit)));
		set.setCode(unit.getUid());
		modelService.save(set);
		set = b2bReportingService.getReportingSetForB2BUnit(unit);
		Assert.assertEquals(b2bUnitService.getBranch(unit).size(), set.getReportingEntries().size());

	}

	@Test
	public void testsetReportingSetForUnit()
	{
		final B2BUnitModel unit = b2bUnitService.getUnitForUid("IC");
		Assert.assertNotSame(Integer.valueOf(0), Integer.valueOf(b2bUnitService.getBranch(unit).size()));
		B2BReportingSetModel set = b2bReportingService.setReportSetForUnit(unit);
		set = b2bReportingService.getReportingSetForB2BUnit(unit);
		Assert.assertEquals(b2bUnitService.getBranch(unit).size(), set.getReportingEntries().size());

	}
}
