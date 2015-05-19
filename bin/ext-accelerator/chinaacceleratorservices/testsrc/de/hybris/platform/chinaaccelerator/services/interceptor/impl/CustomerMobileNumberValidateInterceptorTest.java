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
package de.hybris.platform.chinaaccelerator.services.interceptor.impl;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.chinaaccelerator.services.customer.impl.CustomerMobileNumberValidateInterceptor;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.InterceptorRegistry;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


/**
 * Integration test suite for {@link CustomerMobileNumberValidateInterceptor}
 *
 */
@IntegrationTest
public class CustomerMobileNumberValidateInterceptorTest extends ServicelayerTransactionalBaseTest
{
	final static String TEST_UID = "test_uid";
	final static String TEST_MOBILE = "test_mobile";
	final static String TEST_DUMMY = "test_dummy";

	@Resource
	private ModelService modelService;

	@Test
	public void testInterceptorInstalled()
	{
		final InterceptorRegistry reg = ((DefaultModelService) modelService).getInterceptorRegistry();

		final Collection<ValidateInterceptor> validators = reg.getValidateInterceptors("customer");

		assertFalse(validators.isEmpty());

		boolean found = false;

		for (final ValidateInterceptor inter : validators)
		{
			if (inter instanceof CustomerMobileNumberValidateInterceptor)
			{
				found = true;
				break;
			}
		}

		assertTrue(found);
	}


	@Test
	public void testSaveMobileNumber()
	{
		final CustomerModel customer1 = new CustomerModel();

		customer1.setUid(TEST_UID);
		customer1.setMobileNumber(TEST_MOBILE);
		modelService.save(customer1);

		//case1: save customer with mobile number not modified
		modelService.save(customer1);

		//case2: save customer without mobile number
		customer1.setMobileNumber(null);
		modelService.save(customer1);

		//case3: save customer with empty mobile number
		customer1.setMobileNumber("");
		modelService.save(customer1);
	}


	@Test
	public void testSaveDuplicateMobileNumber()
	{
		final CustomerModel customer1 = new CustomerModel();
		customer1.setUid(TEST_UID);
		customer1.setMobileNumber(TEST_MOBILE);

		modelService.save(customer1);

		try
		{
			final CustomerModel customer2 = new CustomerModel();
			customer2.setUid(TEST_DUMMY);
			customer2.setMobileNumber(TEST_MOBILE);

			//try to save customer with existed mobile number
			modelService.save(customer2);
			Assert.fail("InterceptorException expected.");
		}
		catch (final Exception e)
		{
			//ok
			Assert.assertTrue(e.getCause() instanceof InterceptorException
					&& ((InterceptorException) e.getCause()).getInterceptor().getClass()
							.equals(CustomerMobileNumberValidateInterceptor.class));
		}
	}




}
