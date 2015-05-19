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

import static org.mockito.Mockito.*;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


@UnitTest
public class DefaultB2BApproverServiceMockTest extends HybrisMokitoTest
{
	private final DefaultB2BApproverService defaultB2BApproverService = new DefaultB2BApproverService();

	@Mock
	public UserService userService;

	@Mock
	public B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;


	@Before
	public void setUp() throws Exception
	{
		defaultB2BApproverService.setB2bUnitService(b2bUnitService);
		defaultB2BApproverService.setUserService(userService);
	}

	@Test
	public void testGetAllApprovers() throws Exception
	{
		final B2BCustomerModel mockB2BCustomerModel = Mockito.mock(B2BCustomerModel.class);
		final B2BUnitModel mockB2BUnitModel = Mockito.mock(B2BUnitModel.class);
		final B2BUserGroupModel mockB2BUserGroupModel = Mockito.mock(B2BUserGroupModel.class);
		final PrincipalGroupModel mockPrincipalGroupModel = Mockito.mock(PrincipalGroupModel.class);

		when(mockB2BCustomerModel.getCustomerID()).thenReturn("customer123");
		when(mockB2BUserGroupModel.getUid()).thenReturn(B2BConstants.B2BAPPROVERGROUP);
		when(mockB2BCustomerModel.getGroups()).thenReturn(Collections.singleton(mockPrincipalGroupModel));
		when(b2bUnitService.getParent(mockB2BCustomerModel)).thenReturn(mockB2BUnitModel);
		when(mockB2BCustomerModel.getApprovers()).thenReturn(Collections.singleton(mockB2BCustomerModel));
		when(mockB2BCustomerModel.getApproverGroups()).thenReturn(Collections.singleton(mockB2BUserGroupModel));
		when(userService.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP)).thenReturn(mockB2BUserGroupModel);
		when(Boolean.valueOf(userService.isMemberOfGroup(mockB2BCustomerModel, mockB2BUserGroupModel))).thenReturn(Boolean.TRUE);

		//Only return active approvers
		when(mockB2BCustomerModel.getActive()).thenReturn(Boolean.TRUE);

		final List<B2BCustomerModel> allApprovers = defaultB2BApproverService.getAllApprovers(mockB2BCustomerModel);
		Assert.assertNotNull(allApprovers);
		Assert.assertTrue(CollectionUtils.isNotEmpty(allApprovers));
		Assert.assertEquals(allApprovers.get(0).getCustomerID(), mockB2BCustomerModel.getCustomerID());
	}

}
