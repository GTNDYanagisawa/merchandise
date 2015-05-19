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
package de.hybris.platform.servicelayer.user.interceptors;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.InterceptorRegistry;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class ModifySystemUsersInterceptorTest extends ServicelayerTransactionalBaseTest
{
	@Resource
	private UserService userService;

	@Resource
	private ModelService modelService;


	@Test
	public void testInterceptorAsPreparerInstalled()
	{
		final InterceptorRegistry reg = ((DefaultModelService) modelService).getInterceptorRegistry();
		final Collection<ValidateInterceptor> validaters = reg.getValidateInterceptors("Principal");
		assertFalse(validaters.isEmpty());
		boolean found = false;
		for (final ValidateInterceptor inter : validaters)
		{
			if (inter instanceof ModifySystemUsersInterceptor)
			{
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	@Test
	public void testInterceptorAsRemoverInstalled()
	{
		final InterceptorRegistry reg = ((DefaultModelService) modelService).getInterceptorRegistry();
		final Collection<RemoveInterceptor> removers = reg.getRemoveInterceptors("Principal");
		assertFalse(removers.isEmpty());
		boolean found = false;
		for (final RemoveInterceptor inter : removers)
		{
			if (inter instanceof ModifySystemUsersInterceptor)
			{
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	@Test
	public void testDisableLoginForAdmin()
	{
		final EmployeeModel admin = userService.getAdminUser();
		admin.setLoginDisabled(false);
		modelService.save(admin);

		admin.setLoginDisabled(true);
		try
		{
			modelService.save(admin);
			fail("expected ModelSavingException");
		}
		catch (final ModelSavingException e)
		{
			assertTrue("", e.getCause() instanceof InterceptorException);
			final InterceptorException interceptorException = (InterceptorException) e.getCause();
			assertTrue(interceptorException.getInterceptor() instanceof ModifySystemUsersInterceptor);
		}
		catch (final Exception e)
		{
			fail("not expected exception: " + e);
		}
	}
}
