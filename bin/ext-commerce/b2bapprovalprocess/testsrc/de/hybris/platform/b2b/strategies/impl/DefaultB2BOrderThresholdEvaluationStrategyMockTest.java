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
package de.hybris.platform.b2b.strategies.impl;

import static org.mockito.Mockito.*;
import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.model.B2BOrderThresholdPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.spring.TenantScope;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.transaction.PlatformTransactionManager;


public class DefaultB2BOrderThresholdEvaluationStrategyMockTest extends HybrisMokitoTest
{

	DefaultB2BOrderThresholdEvaluationStrategy defaultB2BOrderThresholdEvaluationStrategy;

	@Mock
	public ModelService mockModelService;
	@Mock
	public SessionService mockSessionService;
	@Mock
	public TenantScope mockTenantScope;
	@Mock
	public PlatformTransactionManager mockTxManager;


	@Before
	public void setUp() throws Exception
	{
		defaultB2BOrderThresholdEvaluationStrategy = new DefaultB2BOrderThresholdEvaluationStrategy();
		defaultB2BOrderThresholdEvaluationStrategy.setModelService(mockModelService);
		defaultB2BOrderThresholdEvaluationStrategy.setSessionService(mockSessionService);
		defaultB2BOrderThresholdEvaluationStrategy.setTenantScope(mockTenantScope);
		defaultB2BOrderThresholdEvaluationStrategy.setTxManager(mockTxManager);
	}

	@Test
	public void testGetPermissionsToEvaluate()
	{
		final CurrencyModel usdCurrency = mock(CurrencyModel.class);
		when(usdCurrency.getIsocode()).thenReturn("USD");

		// Stubbing does not seem to work have to ceate concrete objects.
		final B2BOrderThresholdPermissionModel lowThreshold = new B2BOrderThresholdPermissionModel();
		lowThreshold.setCurrency(usdCurrency);
		lowThreshold.setThreshold(Double.valueOf(100D));

		final B2BOrderThresholdPermissionModel highThreshold = new B2BOrderThresholdPermissionModel();
		highThreshold.setCurrency(usdCurrency);
		highThreshold.setThreshold(Double.valueOf(1000D));

		final Set<B2BPermissionModel> permissionModels = new HashSet<B2BPermissionModel>(2);
		permissionModels.add(highThreshold);
		permissionModels.add(lowThreshold);
		final AbstractOrderModel mockAbstractOrderModel = mock(AbstractOrderModel.class);
		when(mockAbstractOrderModel.getCurrency()).thenReturn(usdCurrency);

		// this should return a permission with higher threshold
		Assert.assertEquals(highThreshold,
				defaultB2BOrderThresholdEvaluationStrategy.getPermissionToEvaluate(permissionModels, mockAbstractOrderModel));

	}
}
