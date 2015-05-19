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

import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.order.strategies.ordercloning.impl.DefaultCloneAbstractOrderStrategy;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.impl.DefaultTypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.spring.TenantScope;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.transaction.PlatformTransactionManager;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class DefaultB2BCartServiceMockTest extends HybrisMokitoTest
{

	public static final Logger LOG = Logger.getLogger(DefaultB2BCartServiceMockTest.class);

	private static final DefaultB2BCartService b2bCartService = new DefaultB2BCartService();
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
	public DefaultTypeService mockTypeService;

	@Test
	public void testCreateCartFromAbstractOrder()
	{
		final ComposedTypeModel cartCTM = mock(ComposedTypeModel.class);
		when(mockTypeService.getComposedTypeForClass(CartModel.class)).thenReturn(cartCTM);
		final ComposedTypeModel cartEntryCTM = mock(ComposedTypeModel.class);
		when(mockTypeService.getComposedTypeForClass(CartEntryModel.class)).thenReturn(cartEntryCTM);
		b2bCartService.setTypeService(mockTypeService);

		final KeyGenerator keyGen = mock(KeyGenerator.class);
		when(keyGen.generate()).thenReturn("aGeneratedKey");
		b2bCartService.setKeyGenerator(keyGen);

		final CartModel cartModel = mock(CartModel.class);
		final DefaultCloneAbstractOrderStrategy cloneOrderStrategy = mock(DefaultCloneAbstractOrderStrategy.class);
		when(cloneOrderStrategy
			.clone(any(ComposedTypeModel.class), any(ComposedTypeModel.class), any(AbstractOrderModel.class),
				any(String.class), eq(CartModel.class), eq(CartEntryModel.class))).thenReturn(cartModel);
		b2bCartService.setCloneAbstractOrderStrategy(cloneOrderStrategy);

		final AbstractOrderModel order = mock(AbstractOrderModel.class);
		final CartModel cart = b2bCartService.createCartFromAbstractOrder(order);
		Assert.assertTrue(cart == cartModel);
	}
}
