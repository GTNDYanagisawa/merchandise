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
 */

package com.hybris.backoffice.cockpitng.dataaccess.facades.permissions;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.security.UserRightModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.jalo.security.Principal;
import de.hybris.platform.jalo.security.UserRight;
import de.hybris.platform.jalo.type.AttributeDescriptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.security.permissions.PermissionAssignment;
import de.hybris.platform.servicelayer.security.permissions.PermissionCheckResult;
import de.hybris.platform.servicelayer.security.permissions.PermissionCheckingService;
import de.hybris.platform.servicelayer.security.permissions.PermissionManagementService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;

import com.hybris.backoffice.cockpitng.dataaccess.facades.common.PlatformFacadeStrategyHandleCache;
import com.hybris.backoffice.daos.BackofficeUserRightsDao;
import com.hybris.cockpitng.dataaccess.facades.permissions.Permission;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionInfo;
import com.hybris.cockpitng.labels.LabelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPlatformPermissionManagementFacadeStrategyTest
{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultPlatformPermissionManagementFacadeStrategyTest.class);

	@Mock
	private transient TypeService typeService;
	@Mock
	private transient PlatformFacadeStrategyHandleCache platformFacadeStrategyHandleCache;
	@Mock
	private transient PermissionCheckingService permissionCheckingService;
	@Mock
	private transient PermissionManagementService permissionManagementService;
	@Mock
	private transient UserService userService;
	@Mock
	private transient ModelService modelService;
	@Mock
	private transient BackofficeUserRightsDao backofficeUserRightsDao;
	@Mock
	private transient FlexibleSearchService flexibleSearchService;

	private transient DefaultPlatformPermissionManagementFacadeStrategy platformPermissionManagementFacade = new DefaultPlatformPermissionManagementFacadeStrategy();

    private static final  String principal = "testUser1";
	private static final String typeCode = "Product";
	private static final String permissionNameRead = "read";
	private static final String permissionNameChange = "change";
	private static final String permissionNameRemove = "remove";
	private static final String permissionNameCreate = "create";
	private static final String field = "code";
	private transient ComposedTypeModel composedTypeModel;
	private transient PermissionCheckResult permissionCheckResult;
	private transient Permission permission;
	private transient PrincipalModel testUser;
	private transient Collection<PermissionAssignment> permissionAssignments;

	@Before
	public void setUp()
	{

		typeService = Mockito.mock(TypeService.class);
		platformPermissionManagementFacade.setTypeService(typeService);

		permissionManagementService = Mockito.mock(PermissionManagementService.class);
		platformPermissionManagementFacade.setPermissionManagementService(permissionManagementService);

		platformFacadeStrategyHandleCache = Mockito.mock(PlatformFacadeStrategyHandleCache.class);
		platformPermissionManagementFacade.setPlatformFacadeStrategyHandleCache(platformFacadeStrategyHandleCache);

		permissionCheckingService = Mockito.mock(PermissionCheckingService.class);
		platformPermissionManagementFacade.setPermissionCheckingService(permissionCheckingService);

		modelService = Mockito.mock(ModelService.class);
		platformPermissionManagementFacade.setModelService(modelService);

		backofficeUserRightsDao = Mockito.mock(BackofficeUserRightsDao.class);
		platformPermissionManagementFacade.setBackofficeUserRightsDao(backofficeUserRightsDao);

		flexibleSearchService = Mockito.mock(FlexibleSearchService.class);
		platformPermissionManagementFacade.setFlexibleSearchService(flexibleSearchService);

		userService = Mockito.mock(UserService.class);
		platformPermissionManagementFacade.setUserService(userService);

		testUser = new PrincipalModel();
		testUser.setUid(principal);
		Mockito.when(flexibleSearchService.getModelByExample(Mockito.any())).thenReturn(testUser);

		composedTypeModel = Mockito.mock(ComposedTypeModel.class);
		Mockito.when(typeService.getComposedTypeForCode(typeCode)).thenReturn(composedTypeModel);

        final PermissionAssignment  permissionAssignmentRead = new PermissionAssignment(permissionNameRead, testUser);
		final PermissionAssignment permissionAssignmentChange = new PermissionAssignment(permissionNameChange, testUser);
		final PermissionAssignment permissionAssignmentCreate = new PermissionAssignment(permissionNameCreate, testUser);
		final PermissionAssignment permissionAssingmentRemove = new PermissionAssignment(permissionNameRemove, testUser);
		permissionAssignments = new ArrayList<>();
		permissionAssignments.add(permissionAssignmentRead);
		permissionAssignments.add(permissionAssignmentChange);
		permissionAssignments.add(permissionAssignmentCreate);
		permissionAssignments.add(permissionAssingmentRemove);
		Mockito.when(permissionManagementService.getTypePermissions(composedTypeModel)).thenReturn(permissionAssignments);

		Mockito.when(permissionManagementService.getTypePermissionsForPrincipal(composedTypeModel, testUser)).thenReturn(
				permissionAssignments);

		permissionCheckResult = Mockito.mock(PermissionCheckResult.class);
		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				return permissionCheckResult;
			}

		}).when(permissionCheckingService).checkAttributeDescriptorPermission(typeCode, field, permissionNameCreate);

		permissionCheckResult = Mockito.mock(PermissionCheckResult.class);
		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				return permissionCheckResult;
			}

		}).when(permissionCheckingService).checkAttributeDescriptorPermission(typeCode, field, permissionNameChange);

		permissionCheckResult = Mockito.mock(PermissionCheckResult.class);
		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				return permissionCheckResult;
			}

		}).when(permissionCheckingService).checkAttributeDescriptorPermission(typeCode, field, permissionNameRemove);
		final PermissionAssignment permissionAssignment = Mockito.mock(PermissionAssignment.class);
		final Collection<PermissionAssignment> rightModel = new ArrayList<>();
		rightModel.add(Mockito.mock(PermissionAssignment.class));
		rightModel.add(permissionAssignment);

		final Collection<UserRightModel> col = new ArrayList<>();
		col.add(Mockito.mock(UserRightModel.class));
		Mockito.when(backofficeUserRightsDao.findUserRightsByCode(Mockito.anyString())).thenReturn(col);

		final UserModel user = new UserModel();
		user.setUid(testUser.getUid());
		Mockito.when(userService.getUserForUID(principal)).thenReturn(user);

		final Principal jaloPrincipal = Mockito.mock(Principal.class);
		Mockito.when(modelService.getSource(testUser)).thenReturn(jaloPrincipal);
		Mockito.when(Boolean.valueOf(userService.isUserExisting(principal))).thenReturn(Boolean.TRUE);

		Mockito.when(modelService.getAll(Mockito.anyCollection(), Mockito.anyCollection())).thenReturn(col);

		final LabelService labelService = Mockito.mock(LabelService.class);
		platformPermissionManagementFacade.setLabelService(labelService);
		Mockito.when(labelService.getObjectLabel(principal)).thenReturn("some label");
	}

	@Test
	public void getPrincipalsWithPermissionAssignmentTest()
	{
		final AttributeDescriptorModel attrDescModel = new AttributeDescriptorModel();
		final Set<AttributeDescriptorModel> attributeDescriptorModels = new HashSet<>();
		attributeDescriptorModels.add(attrDescModel);
		Mockito.when(typeService.getAttributeDescriptorsForType(composedTypeModel)).thenReturn(attributeDescriptorModels);

		Mockito.when(permissionManagementService.getAttributePermissions(attrDescModel)).thenReturn(permissionAssignments);

		Mockito.when(composedTypeModel.getCode()).thenReturn(typeCode);
		Assert.assertEquals(platformPermissionManagementFacade.getPrincipalsWithPermissionAssignment(typeCode).isEmpty(), false);
		Assert.assertNotNull(platformPermissionManagementFacade.getPrincipalsWithPermissionAssignment(typeCode));
	}

	@Test
	public void getTypePermissionTest()
	{
		final Permission permission = platformPermissionManagementFacade.getTypePermission(principal, typeCode, permissionNameRead);
		Assert.assertNotNull(permission);
	}

	@Test
	public void getFieldPermissionTest()
	{
		permissionCheckResult = Mockito.mock(PermissionCheckResult.class);
		Mockito.when(permissionCheckingService.checkAttributeDescriptorPermission(typeCode, field, permissionNameRead)).thenReturn(
				permissionCheckResult);
		final Permission permission = platformPermissionManagementFacade.getFieldPermission(principal, typeCode, field,
				permissionNameRead);
		Assert.assertNotNull(permission);
		Assert.assertNotNull(permission.getField());
	}

	@Test
	public void getFieldPermissionInfoTest()
	{
		final AttributeDescriptorModel attributeDescriptorModel = Mockito.mock(AttributeDescriptorModel.class);
		Mockito.when(attributeDescriptorModel.getQualifier()).thenReturn(field);

		final Set<AttributeDescriptorModel> set = new HashSet<>();
		set.add(attributeDescriptorModel);
		set.add(attributeDescriptorModel);
		Mockito.when(typeService.getAttributeDescriptorsForType(composedTypeModel)).thenReturn(set);

		final PermissionAssignment pa = Mockito.mock(PermissionAssignment.class);
		final Collection<PermissionAssignment> attributePermissions = new ArrayList<>();
		attributePermissions.add(Mockito.mock(PermissionAssignment.class));
		attributePermissions.add(Mockito.mock(PermissionAssignment.class));
		attributePermissions.add(pa);
		Mockito.when(permissionManagementService.getAttributePermissions(attributeDescriptorModel))
				.thenReturn(attributePermissions);
		Mockito.when(pa.getPermissionName()).thenReturn(permissionNameRead);

		Mockito.when(attributeDescriptorModel.getName()).thenReturn(permissionNameRead);
		Mockito.when(typeService.getAttributeDescriptor(composedTypeModel, field)).thenReturn(attributeDescriptorModel);

		final PermissionInfo permissionInfo = platformPermissionManagementFacade.getFieldPermissionInfo(principal, typeCode, field);
		Assert.assertNotNull(permissionInfo);
		Assert.assertNotNull(permissionInfo.getPermissions());
		Assert.assertNotNull(permissionInfo.getPermission(permissionNameRead));
	}

	@Test
	public void getTypePermissionInfosForPrincipalTest()
	{
		final PermissionAssignment permissionAssignment = Mockito.mock(PermissionAssignment.class);
		final Collection<PermissionAssignment> rightModel = new ArrayList<>();
		rightModel.add(Mockito.mock(PermissionAssignment.class));
		rightModel.add(permissionAssignment);

		final Collection<UserRightModel> col = new ArrayList<>();
		col.add(Mockito.mock(UserRightModel.class));
		Mockito.when(backofficeUserRightsDao.findUserRightsByCode(Mockito.anyString())).thenReturn(col);

		final UserModel user = new UserModel();
		user.setUid(testUser.getUid());
		Mockito.when(userService.getUserForUID(principal)).thenReturn(user);

		final Principal jaloPrincipal = Mockito.mock(Principal.class);
		Mockito.when(modelService.getSource(user)).thenReturn(jaloPrincipal);
		Mockito.when(Boolean.valueOf(userService.isUserExisting(principal))).thenReturn(Boolean.TRUE);

		final ArrayList list = new ArrayList();
		list.add(Mockito.mock(UserRightModel.class));
		Mockito.when(modelService.getAllSources(Mockito.anyCollection(), Mockito.anyCollection())).thenReturn(list);

		final Map map = new HashMap<>();
		final Tenant tenantMock = Mockito.mock(Tenant.class);
		final UserRight userRight = new UserRight()
		{
			@Override
			public PK getPK()
			{
				return de.hybris.platform.core.PK.fromLong(42);
			}

			@Override
			public Tenant getTenant()
			{
				return tenantMock;
			}
		};

		map.put(userRight, "");
		Mockito.when(jaloPrincipal.getItemPermissionsMap(list)).thenReturn(map);

		Mockito.when(modelService.getAll(Mockito.anyCollection(), Mockito.anyCollection())).thenReturn(col);

		final Collection<PermissionInfo> typePermissions = platformPermissionManagementFacade
				.getTypePermissionInfosForPrincipal(principal);
		Assert.assertNotNull(typePermissions);

	}

	@Test
	public void setAttributePermission()
	{
		final boolean referenceAccess = true;
		permission = new Permission(false, !referenceAccess, permissionNameRead, principal, typeCode, field);

		final AttributeDescriptorModel attributeDescriptorModel = Mockito.mock(AttributeDescriptorModel.class);
		Mockito.when(typeService.getAttributeDescriptor(composedTypeModel, field)).thenReturn(attributeDescriptorModel);

		platformPermissionManagementFacade.setPermission(permission);
		final PermissionInfo permissionInfo = platformPermissionManagementFacade.updatePermissionInfo(permission);
		final Permission tmpPermission = permissionInfo.getPermission(permissionNameRead);
		tmpPermission.setDenied(referenceAccess);
		platformPermissionManagementFacade.setPermission(tmpPermission);
		final PermissionInfo tmpPermissionInfo = platformPermissionManagementFacade.updatePermissionInfo(tmpPermission);
		final Permission tmpPermission2 = tmpPermissionInfo.getPermission(permissionNameRead);
		Assert.assertEquals(tmpPermission.isDenied(), tmpPermission2.isDenied());
		Assert.assertEquals(referenceAccess, tmpPermission2.isDenied());
	}

	@Test
	public void setTypePermission()
	{
		final boolean referenceAccess = false;
		final String blankField = StringUtils.EMPTY;
		permission = new Permission(false, !referenceAccess, permissionNameRead, principal, typeCode, blankField);

		final AtomicTypeModel atomicTypeModel = Mockito.mock(AtomicTypeModel.class);
		Mockito.when(typeService.getAtomicTypeForCode(typeCode)).thenReturn(atomicTypeModel);

		platformPermissionManagementFacade.setPermission(permission);

		final PermissionInfo referencePermissionInfo = platformPermissionManagementFacade.updatePermissionInfo(permission);
		final Permission referencePermission = referencePermissionInfo.getPermission(permissionNameRead);
		referencePermission.setDenied(referenceAccess);

		platformPermissionManagementFacade.setPermission(referencePermission);

		final PermissionInfo retrievedPermissionInfo = platformPermissionManagementFacade.updatePermissionInfo(referencePermission);
		final Permission retrievedPermission = retrievedPermissionInfo.getPermission(permissionNameRead);

		Assert.assertEquals(referencePermission.isDenied(), retrievedPermission.isDenied());
		Assert.assertEquals(referenceAccess, retrievedPermission.isDenied());
	}

	@Test
	public void deletePermissionTest()
	{
		permission = new Permission(true, false, permissionNameRead, principal, typeCode, null);

		final Principal jaloPrincipal = Mockito.mock(Principal.class);
		Mockito.when(modelService.getSource(testUser)).thenReturn(jaloPrincipal);
		Mockito.when(Boolean.valueOf(userService.isUserExisting(principal))).thenReturn(Boolean.TRUE);

		final ArrayList list = new ArrayList();
		list.add(Mockito.mock(UserRightModel.class));
		Mockito.when(modelService.getAllSources(Mockito.anyCollection(), Mockito.anyCollection())).thenReturn(list);

		final Map map = new HashMap<>();
		final Tenant tenantMock = Mockito.mock(Tenant.class);
		final UserRight userRight = new UserRight()
		{
			@Override
			public PK getPK()
			{
				return de.hybris.platform.core.PK.fromLong(42);
			}

			@Override
			public Tenant getTenant()
			{
				return tenantMock;
			}
		};

		Mockito.when(jaloPrincipal.getPK()).thenReturn(de.hybris.platform.core.PK.fromLong(666));
		Mockito.when(jaloPrincipal.getTenant()).thenReturn(tenantMock);

		map.put(userRight, "");
		Mockito.when(jaloPrincipal.getItemPermissionsMap(list)).thenReturn(map);

		platformPermissionManagementFacade.deletePermission(permission);

		Mockito.verify(jaloPrincipal).setItemPermissionsByMap(list, map);

		final AttributeDescriptorModel attrDescModel = new AttributeDescriptorModel();
		attrDescModel.setQualifier(field);

		final Set<AttributeDescriptorModel> set = new HashSet<>();
		set.add(attrDescModel);
		Mockito.when(typeService.getAttributeDescriptorsForType(composedTypeModel)).thenReturn(set);

		final AttributeDescriptor attributeDescriptorJalo = Mockito.mock(AttributeDescriptor.class);
		Mockito.when(modelService.getSource(attrDescModel)).thenReturn(attributeDescriptorJalo);

		Mockito.when(attributeDescriptorJalo.getPermissionMap(list)).thenReturn(map);

		Mockito.when(permissionManagementService.getAttributePermissions(attrDescModel)).thenReturn(permissionAssignments);

		permission = new Permission(true, false, permissionNameRead, principal, typeCode, field);
		platformPermissionManagementFacade.deletePermission(permission);

		try
		{
			Mockito.verify(attributeDescriptorJalo).setPermissionsByMap(list, map);
		}
		catch (final JaloSecurityException e)
		{
			LOG.error(e.getMessage(),e);
		}
	}

	@Test
	public void updatePermissionInfoTest()
	{
		permission = new Permission(true, true, permissionNameRead, principal, typeCode, null);

		final PermissionInfo permissionInfo = platformPermissionManagementFacade.updatePermissionInfo(permission);
		final Permission updatedPermission = permissionInfo.getPermission(permissionNameRead);
		Assert.assertNotNull(updatedPermission);
		Assert.assertEquals(updatedPermission.getName(), permission.getName());
	}

	@Test
	public void getPrincipalPermissionInfo() throws Exception
	{
		final PermissionInfo permissionInfo = platformPermissionManagementFacade.getPrincipalPermissionInfo(principal, typeCode);

		Assert.assertEquals(typeCode, permissionInfo.getTypeCode());
		Assert.assertEquals(principal, permissionInfo.getPrincipal());
		Assert.assertEquals(PermissionInfo.PermissionInfoType.PRINCIPAL, permissionInfo.getPermissionInfoType());

		final int expectedPermissionNumberSize = 4;
		Assert.assertEquals(expectedPermissionNumberSize, permissionInfo.getPermissions().size());
	}

	@Test
	public void getTypePermissionInfo() throws Exception
	{
		final PermissionInfo permissionInfo = platformPermissionManagementFacade.getTypePermissionInfo(principal, typeCode);

		Assert.assertEquals(typeCode, permissionInfo.getTypeCode());
		Assert.assertEquals(principal, permissionInfo.getPrincipal());
		Assert.assertEquals(PermissionInfo.PermissionInfoType.TYPE, permissionInfo.getPermissionInfoType());

		final int expectedPermissionNumberSize = 4;
		Assert.assertEquals(expectedPermissionNumberSize, permissionInfo.getPermissions().size());
	}
}
