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
package de.hybris.platform.cockpit.services.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.SystemService;
import de.hybris.platform.cockpit.services.security.impl.DefaultUIAccessRightService;
import de.hybris.platform.cockpit.zk.mock.DummyExecution;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;


@UnitTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/cockpit/cockpit-test.xml")
@SuppressWarnings("boxing")
public class DefaultUIAccessRightServiceTest
{
	@Autowired
	private ApplicationContext applicationContext;
	@Mock
	private SystemService systemService;
	@Mock
	private ModelService modelService;
	@Mock
	private TypedObject typedObject;

	private DefaultUIAccessRightService accessRightService;
	private DefaultUIAccessRightService dummyDefaultUIAccessRightService;

	@Before
	public void setUp()
	{
		ExecutionsCtrl.setCurrent(new DummyExecution(applicationContext));
		MockitoAnnotations.initMocks(this);
		accessRightService = new DefaultUIAccessRightService();
		accessRightService.setSystemService(systemService);
		accessRightService.setModelService(modelService);

		dummyDefaultUIAccessRightService = new DefaultUIAccessRightService()
		{
			@Override
			public boolean canWrite(final UserModel user, final CatalogVersionModel catVersion)
			{
				return true;
			}
		};
		dummyDefaultUIAccessRightService.setSystemService(systemService);
		dummyDefaultUIAccessRightService.setModelService(modelService);
	}

	@Test
	public void shouldRefuseWhenTypeIsNull()
	{
		given(Boolean.valueOf(systemService.checkPermissionOn(anyString(), anyString()))).willReturn(Boolean.TRUE);
		assertFalse("", accessRightService.isWritable(null));
	}

	@Test
	public void shouldRefuseWhenTypeAndItemAreNull()
	{
		given(Boolean.valueOf(systemService.checkPermissionOn(anyString(), anyString()))).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(modelService.isNew(any()))).willReturn(Boolean.FALSE);
		assertFalse("", accessRightService.isWritable(null, null));
	}

	@Test
	public void shouldAllowWhenTypeIsNullButItemIsWritable()
	{
		given(Boolean.valueOf(systemService.checkPermissionOn(anyString(), anyString()))).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(modelService.isNew(any()))).willReturn(Boolean.FALSE);
		given(typedObject.getObject()).willReturn(new CatalogVersionModel());
		assertTrue("", dummyDefaultUIAccessRightService.isWritable(null, typedObject));
	}

	@Test
	public void shouldRefuseWhenDesciptorAndTypeAreNull()
	{
		given(Boolean.valueOf(systemService.checkPermissionOn(anyString(), anyString()))).willReturn(Boolean.TRUE);
		assertFalse("", accessRightService.isWritable(null, null, null, false));
	}

	@Test
	public void shoudAllowWhenDescriptorIsNullButItemIsWritable()
	{
		given(typedObject.getObject()).willReturn(new CatalogVersionModel());
		given(Boolean.valueOf(systemService.checkPermissionOn(anyString(), anyString()))).willReturn(Boolean.TRUE);
		assertTrue("", dummyDefaultUIAccessRightService.isWritable(null, typedObject, null, false));
	}
}
