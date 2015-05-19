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
package de.hybris.platform.commerceservices.customer.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;


@IntegrationTest
public class CustomerOriginalUidPrepareInterceptorTest extends ServicelayerBaseTest
{
	private static final String UPPER_ID = "ZZZZZZ";
	private static final String OTHER_UPPER_ID = "YYYYY";

	private static final String LOWER_ID = UPPER_ID.toLowerCase();
	private static final String OTHER_LOWER_ID = OTHER_UPPER_ID.toLowerCase();

	private CustomerModel customer;

	@Resource
	private ModelService modelService;

	@After
	public void dispose()
	{
		modelService.remove(customer);
	}

	@Test
	public void testCreateNoUid()
	{

		customer = modelService.create(CustomerModel.class);
		customer.setOriginalUid(UPPER_ID);

		modelService.save(customer);

		Assert.assertEquals(UPPER_ID, customer.getOriginalUid());
		Assert.assertEquals(LOWER_ID, customer.getUid());


	}


	@Test
	public void testModifyNoUid()
	{

		customer = modelService.create(CustomerModel.class);
		customer.setOriginalUid(UPPER_ID);

		modelService.save(customer);

		Assert.assertEquals(UPPER_ID, customer.getOriginalUid());
		Assert.assertEquals(LOWER_ID, customer.getUid());


	}

	@Test
	public void testCreateWithUid()
	{

		customer = modelService.create(CustomerModel.class);
		customer.setOriginalUid(UPPER_ID);
		customer.setUid(UPPER_ID);

		modelService.save(customer);

		Assert.assertEquals(UPPER_ID, customer.getOriginalUid());
		Assert.assertEquals(LOWER_ID, customer.getUid());

	}

	@Test
	public void testModifyUidToLowerCase()
	{

		customer = modelService.create(CustomerModel.class);
		customer.setOriginalUid(UPPER_ID);
		customer.setUid(UPPER_ID);

		modelService.save(customer);

		customer.setUid(OTHER_UPPER_ID);
		customer.setOriginalUid(OTHER_UPPER_ID);
		modelService.save(customer);

		Assert.assertEquals(OTHER_UPPER_ID, customer.getOriginalUid());
		Assert.assertEquals(OTHER_LOWER_ID, customer.getUid());

	}

	@Test
	public void testCreateNoOrignalUidToLower()
	{

		customer = modelService.create(CustomerModel.class);
		customer.setUid(UPPER_ID);

		modelService.save(customer);

		Assert.assertEquals(UPPER_ID, customer.getOriginalUid());
		Assert.assertEquals(LOWER_ID, customer.getUid());

	}

	@Test
	public void testModifyUpperUIdNoOrignalUid()
	{

		customer = modelService.create(CustomerModel.class);
		customer.setUid(UPPER_ID);

		modelService.save(customer);

		customer.setUid(OTHER_UPPER_ID);
		modelService.save(customer);

		Assert.assertEquals(OTHER_UPPER_ID, customer.getOriginalUid());
		Assert.assertEquals(OTHER_LOWER_ID, customer.getUid());

	}

	@Test
	public void testCreateNoOrignalUid()
	{

		customer = modelService.create(CustomerModel.class);
		customer.setUid(LOWER_ID);

		modelService.save(customer);

		Assert.assertEquals(LOWER_ID, customer.getOriginalUid());
		Assert.assertEquals(LOWER_ID, customer.getUid());

	}

	@Test
	public void testModifyNoOrignalUid()
	{

		customer = modelService.create(CustomerModel.class);
		customer.setUid(LOWER_ID);

		modelService.save(customer);

		customer.setUid(OTHER_LOWER_ID);
		modelService.save(customer);

		Assert.assertEquals(OTHER_LOWER_ID, customer.getOriginalUid());
		Assert.assertEquals(OTHER_LOWER_ID, customer.getUid());

	}
}
