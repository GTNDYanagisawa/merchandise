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
package de.hybris.platform.acceleratorservices.order.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.site.impl.DefaultBaseSiteService;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class DefaultAcceleratorConsignmentDaoTest extends BaseCommerceBaseTest
{

	@Resource
	private DefaultAcceleratorConsignmentDao defaultAcceleratorConsignmentDao;

	@Resource
	private DefaultBaseSiteService defaultBaseSiteService;


	/**
	 * see ACC-6734
	 * 
	 * @throws ImpExException
	 */
	@Test
	public void shouldGetConsignements() throws ImpExException
	{
		importCsv("/acceleratorservices/test/consignmentDaoTestData.csv", "windows-1252");
		final BaseSiteModel site = defaultBaseSiteService.getBaseSiteForUID("storetemplate");
		Assert.assertNotNull(site);
		final List<ConsignmentModel> consignments = defaultAcceleratorConsignmentDao.findConsignmentsForStatus(
				Arrays.asList(ConsignmentStatus.PICKPACK, ConsignmentStatus.READY), Arrays.asList(site));
		Assert.assertThat(Integer.valueOf(consignments.size()), CoreMatchers.equalTo(Integer.valueOf(1)));
	}
}
