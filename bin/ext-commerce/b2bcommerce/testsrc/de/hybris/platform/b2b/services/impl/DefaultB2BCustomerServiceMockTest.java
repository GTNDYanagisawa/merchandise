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

import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.spring.TenantScope;

import java.util.Collections;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.transaction.PlatformTransactionManager;


public class DefaultB2BCustomerServiceMockTest extends HybrisMokitoTest
{

	public static final Logger LOG = Logger.getLogger(DefaultB2BCustomerServiceMockTest.class);
	DefaultB2BCustomerService defaultB2BCustomerService = new DefaultB2BCustomerService();
	@Mock
	public UserService mockUserService;
	@Mock
	public BaseDao mockBaseDao;
	@Mock
	public ModelService mockModelService;
	@Mock
	public B2BUnitService<B2BUnitModel, B2BCustomerModel> mockB2bUnitService;
	@Mock
	public SessionService mockSessionService;
	@Mock
	public TenantScope mockTenantScope;
	@Mock
	public PlatformTransactionManager mockTxManager;
	@Mock
	public RendererService rendererService;
	@Test
	public void testGetB2BCustomer() throws Exception
	{
		final B2BCustomerModel b2bCustomerModel = mock(B2BCustomerModel.class);
		b2bCustomerModel.setUid("customerID");
		when(mockUserService.getUserForUID(Mockito.anyString(), Mockito.eq(B2BCustomerModel.class))).thenReturn(b2bCustomerModel);
		defaultB2BCustomerService.setUserService(mockUserService);

		final B2BCustomerModel foundCustomer = defaultB2BCustomerService.getUserForUID("Sam");
		Assert.assertEquals(b2bCustomerModel.getUid(), foundCustomer.getUid());


	}

	@Test
	public void testRuntimeExceptionB2BCustomer() throws Exception
	{
		final UnknownIdentifierException uie = new UnknownIdentifierException("Who's is Sam?");
		when(mockUserService.getUserForUID(Mockito.anyString(), Mockito.eq(B2BCustomerModel.class))).thenThrow(uie);
		defaultB2BCustomerService.setUserService(mockUserService);
		final B2BCustomerModel foundCustomer = defaultB2BCustomerService.getUserForUID("Sam");
		Assert.assertNull(foundCustomer);
	}

	@Test
	public void testAddMember()
	{
		final PrincipalGroupModel existingGroup = mock(PrincipalGroupModel.class);
		final PrincipalModel member = mock(PrincipalModel.class);
		when(member.getGroups()).thenReturn(Collections.singleton(existingGroup));

		final PrincipalGroupModel groupToAdd = mock(PrincipalGroupModel.class);

		defaultB2BCustomerService.addMember(member, groupToAdd);
		verify(member, times(1)).setGroups(anySet());
	}

	@Test
	public void testSettingAParentUnitWhenUserHasNoParentUnitAssigned()
	{
		final B2BCustomerModel member = mock(B2BCustomerModel.class);
		final B2BUnitModel parentUnit = mock(B2BUnitModel.class);
		when(member.getGroups()).thenReturn(null);

		defaultB2BCustomerService.addMember(member, parentUnit);
		verify(member, times(1)).setGroups(anySet());
	}

	@Test
	public void testSettingAParentUnitWhenUserHasAParentUnitAssigned()
	{
		final B2BCustomerModel member = mock(B2BCustomerModel.class);
		final B2BUnitModel newParentUnit = mock(B2BUnitModel.class);
		final B2BUnitModel oldParentUnit = mock(B2BUnitModel.class);

		when(member.getGroups()).thenReturn(Collections.singleton((PrincipalGroupModel) oldParentUnit));
		defaultB2BCustomerService.addMember(member, newParentUnit);
		verify(member, times(1)).setGroups(anySet());
	}
}
