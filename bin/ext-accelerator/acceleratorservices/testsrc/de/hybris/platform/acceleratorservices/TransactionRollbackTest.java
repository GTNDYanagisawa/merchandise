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
package de.hybris.platform.acceleratorservices;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.Date;
import javax.annotation.Resource;
import junit.framework.Assert;
import org.junit.Test;


@IntegrationTest
public class TransactionRollbackTest extends ServicelayerTransactionalTest
{
	@Resource
	private UserService userService;
	@Resource
	private ModelService modelService;

	private final String userId = "TransactionRollbackTestUser" + new Date().getTime();

	@Test (expected = UnknownIdentifierException.class)
	public void runFirsthouldBeNotFoundBeforeTheTest()
	{
		userService.getUserForUID(userId);
	}
	@Test
	public void runSecondShouldInsertUser()
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid(userId);
		modelService.save(user);
		Assert.assertNotNull(userService.getUserForUID(userId));
	}

	@Test (expected = UnknownIdentifierException.class)
	public void runLasThouldBeNotFoundAfterTheTest()
	{
		userService.getUserForUID(userId);
	}
}
