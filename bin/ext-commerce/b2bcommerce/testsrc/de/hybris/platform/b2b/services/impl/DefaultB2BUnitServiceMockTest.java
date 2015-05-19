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

import de.hybris.platform.b2b.dao.PrincipalGroupMembersDao;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.spring.TenantScope;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.transaction.PlatformTransactionManager;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class DefaultB2BUnitServiceMockTest extends HybrisMokitoTest
{

	DefaultB2BUnitService defaultB2BUnitService = new DefaultB2BUnitService();
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
	@Mock
	public PrincipalGroupMembersDao mockPrincipalGroupMemberDao;

	@Before
	public void setUp()
	{
		defaultB2BUnitService.setPrincipalGroupMembersDao(mockPrincipalGroupMemberDao);
		defaultB2BUnitService.setModelService(mockModelService);
		defaultB2BUnitService.setSessionService(mockSessionService);
		defaultB2BUnitService.setUserService(mockUserService);
	}

	@Test
	public void testGetBranch()
	{
		final B2BUnitModel unit = mock(B2BUnitModel.class);
		final Set<B2BUnitModel> branch = defaultB2BUnitService.getBranch(unit);
		Assert.assertTrue(branch.contains(unit));
	}

	@Test
	public void testSetCurrentUnit()
	{
		final B2BUnitModel unit = mock(B2BUnitModel.class);
		final B2BCustomerModel customer = mock(B2BCustomerModel.class);

		final Object[] objectArray = new Object[4];
		objectArray[0] = unit;
		objectArray[1] = unit;
		objectArray[2] = unit;
		objectArray[3] = unit;
		doReturn(objectArray).when(mockSessionService).executeInLocalView(any(SessionExecutionBody.class));

		defaultB2BUnitService.setCurrentUnit(customer, unit);

		verify(customer, times(1)).setDefaultB2BUnit(unit);
		verify(mockModelService, times(1)).save(customer);
		verify(mockSessionService, times(1)).getCurrentSession();
	}

	@Test
	public void testGetParent()
	{
		//should return null when B2BCustomerModel argument is null. (tests 1st if statement)
		final B2BCustomerModel nullEmployee = null;
		Assert.assertTrue(defaultB2BUnitService.getParent(nullEmployee) == null);

		//should return defaultB2BUnit. (tests 2nd if statement)
		final B2BUnitModel defaultB2BUnit = mock(B2BUnitModel.class);

		final B2BCustomerModel employee = mock(B2BCustomerModel.class);
		when(employee.getDefaultB2BUnit()).thenReturn(defaultB2BUnit);

		final B2BUnitModel parent = defaultB2BUnitService.getParent(employee);
		Assert.assertTrue(parent == defaultB2BUnit);

		//should return unitA instead of unitB. (tests 3rd if statement)
		final B2BUnitModel unitB = mock(B2BUnitModel.class);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();
		groups.add(unitB);

		final B2BCustomerModel employeeB = mock(B2BCustomerModel.class);
		when(employeeB.getGroups()).thenReturn(groups);

		final B2BUnitModel returnedUnit = defaultB2BUnitService.getParent(employeeB);
		Assert.assertTrue(returnedUnit == unitB);
	}

	@Test
	public void testGetParentByUnit()
	{
		final B2BUnitModel unit = mock(B2BUnitModel.class);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();
		groups.add(unit);
		when(unit.getGroups()).thenReturn(groups);

		Assert.assertTrue(defaultB2BUnitService.getParent(unit) == unit);
	}

	@Test
	public void testGetRootUnit()
	{
		final B2BUnitModel unitA = mock(B2BUnitModel.class);
		final B2BUnitModel unitB = mock(B2BUnitModel.class);
		final Set<PrincipalGroupModel> groups = mock(Set.class);

		final Iterator<PrincipalGroupModel> iterator = mock(Iterator.class);
		when(Boolean.valueOf(iterator.hasNext())).thenReturn(Boolean.TRUE, Boolean.FALSE);
		when(groups.iterator()).thenReturn(iterator);
		when(iterator.next()).thenReturn(unitB);
		when(unitA.getGroups()).thenReturn(groups);

		final B2BUnitModel returnedUnit = defaultB2BUnitService.getRootUnit(unitA);
		Assert.assertTrue(returnedUnit == unitB);

	}

	@Test
	public void testGetAllParents()
	{
		final B2BUnitModel unitA = mock(B2BUnitModel.class);
		final B2BUnitModel unitB = mock(B2BUnitModel.class);
		final Set<PrincipalGroupModel> groups = mock(Set.class);

		final Iterator<PrincipalGroupModel> iterator = mock(Iterator.class);
		when(groups.iterator()).thenReturn(iterator);
		when(Boolean.valueOf(iterator.hasNext())).thenReturn(Boolean.TRUE, Boolean.FALSE);
		when(iterator.next()).thenReturn(unitB);
		when(unitA.getGroups()).thenReturn(groups);

		final List<B2BUnitModel> parents = defaultB2BUnitService.getAllParents(unitA);
		Assert.assertTrue(parents.size() == 2);
		Assert.assertTrue(parents.contains(unitB) && parents.contains(unitA));
	}

	@Test
	public void testGetUnitForUid()
	{
		final String uid = "0001";
		final B2BUnitModel unit = mock(B2BUnitModel.class);
		when(mockUserService.getUserGroupForUID(uid, B2BUnitModel.class)).thenReturn(unit);

		final B2BUnitModel returnedUnit = defaultB2BUnitService.getUnitForUid(uid);
		Assert.assertTrue(returnedUnit == unit);
	}

	@Test
	public void testAddMember()
	{
		//test when member does not have groups
		final B2BUnitModel group = mock(B2BUnitModel.class);
		final PrincipalModel member = mock(PrincipalModel.class);
		when(member.getGroups()).thenReturn(null);

		defaultB2BUnitService.addMember(group, member);

		verify(member, times(1)).getGroups();
		verify(member, times(1)).setGroups(any(Set.class));

		//test when member does have groups
		final PrincipalModel memberWithGroups = mock(PrincipalModel.class);
		final PrincipalGroupModel principalGroup = mock(PrincipalGroupModel.class);
		when(memberWithGroups.getGroups()).thenReturn(Collections.singleton(principalGroup));

		defaultB2BUnitService.addMember(group, memberWithGroups);

		verify(memberWithGroups, times(2)).getGroups();
		verify(memberWithGroups, times(1)).setGroups(any(Set.class));
	}
}
