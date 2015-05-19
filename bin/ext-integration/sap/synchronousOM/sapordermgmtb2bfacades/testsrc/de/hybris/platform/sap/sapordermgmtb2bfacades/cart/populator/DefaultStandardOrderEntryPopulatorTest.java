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
package de.hybris.platform.sap.sapordermgmtb2bfacades.cart.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.constants.SapproductconfigruntimeinterfaceConstants;
import de.hybris.platform.sap.sapordermgmtservices.prodconf.impl.DefaultProductConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


/** 
 *
 */
public class DefaultStandardOrderEntryPopulatorTest
{
	DefaultStandardOrderEntryPopulator classUnderTest = new DefaultStandardOrderEntryPopulator();
	private AbstractOrderEntryModel source;
	private String pk;
	private DefaultProductConfigurationService defaultConfigurationService;


	@Test
	public void testPopulate()
	{


		final OrderEntryData target = new OrderEntryData();
		classUnderTest.populateCFGAttributes(source, target);
		assertTrue(target.isConfigurable());
		assertEquals(pk, target.getHandle());
	}

	@Before
	public void init()
	{
		source = EasyMock.createMock(AbstractOrderEntryModel.class);
		pk = "1";
		final String cfg = "<XML>";
		final PK key = PK.parse(pk);
		EasyMock.expect(source.getPk()).andReturn(key).anyTimes();
		EasyMock.expect(source.getExternalConfiguration()).andReturn(cfg);
		defaultConfigurationService = EasyMock.createMock(DefaultProductConfigurationService.class);
		EasyMock.expect(defaultConfigurationService.isInSession(pk)).andReturn(true);
		final SessionService sessionService = EasyMock.createMock(SessionService.class);
		sessionService.setAttribute(SapproductconfigruntimeinterfaceConstants.PRODUCT_CONFIG_SESSION_PREFIX + pk, null);
		EasyMock.expect(sessionService.getAttribute(SapproductconfigruntimeinterfaceConstants.PRODUCT_CONFIG_SESSION_PREFIX + pk))
				.andReturn(null);
		defaultConfigurationService.setSessionService(sessionService);
		EasyMock.replay(source, sessionService, defaultConfigurationService);
		classUnderTest.setProductConfigurationService(defaultConfigurationService);

		//final ConfigModel configModel = new ConfigModelImpl();
		//defaultConfigurationService.setConfigModel(configModel);

	}

	@Test
	public void testProductConfigurationService()
	{

		assertEquals(defaultConfigurationService, classUnderTest.getProductConfigurationService());
	}

	@Test
	public void testIsConfigurationSessionAvailable()
	{
		assertTrue(classUnderTest.isConfigurationSessionAvailable(pk));
	}
}
