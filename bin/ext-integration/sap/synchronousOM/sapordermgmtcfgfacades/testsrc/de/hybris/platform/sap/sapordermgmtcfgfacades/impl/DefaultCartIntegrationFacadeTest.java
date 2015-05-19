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
package de.hybris.platform.sap.sapordermgmtcfgfacades.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;

import org.easymock.EasyMock;
import org.junit.Test;


@UnitTest
public class DefaultCartIntegrationFacadeTest
{
	ProductConfigurationService productConfigurationServiceMock;
	CartService cartServiceMock;
	ProductService productServiceMock;
	BackendAvailabilityService backendAvailabilityServiceMock;
	ConfigurationCartIntegrationFacade productConfigDefaultCartIntegrationFacadeMock;

	DefaultCartIntegrationFacade classUnderTest = new DefaultCartIntegrationFacade();

	ConfigurationData conf;
	private final String itemKey = "123";


	private void prepareMocksForAddToCartBackendDown() throws CommerceCartModificationException
	{
		conf = new ConfigurationData();

		backendAvailabilityServiceMock = EasyMock.createMock(BackendAvailabilityService.class);
		classUnderTest.setBackendAvailabilityService(backendAvailabilityServiceMock);
		EasyMock.expect(Boolean.valueOf(backendAvailabilityServiceMock.isBackendDown())).andReturn(Boolean.TRUE);

		productConfigDefaultCartIntegrationFacadeMock = EasyMock.createMock(ConfigurationCartIntegrationFacade.class);
		classUnderTest.setProductConfigDefaultCartIntegrationFacade(productConfigDefaultCartIntegrationFacadeMock);
		EasyMock.expect(productConfigDefaultCartIntegrationFacadeMock.addConfigurationToCart(conf)).andReturn(itemKey).once();
		EasyMock.replay(backendAvailabilityServiceMock, productConfigDefaultCartIntegrationFacadeMock);
	}


	private void prepareMocksForAddToCartBackendAvailable() throws CommerceCartModificationException
	{

		conf = EasyMock.createMock(ConfigurationData.class);
		EasyMock.expect(conf.getConfigId()).andReturn("confId");
		EasyMock.expect(conf.getCartItemPK()).andReturn("PK");

		productConfigurationServiceMock = EasyMock.createMock(ProductConfigurationService.class);
		classUnderTest.setProductConfigurationService(productConfigurationServiceMock);
		EasyMock.expect(productConfigurationServiceMock.retrieveConfigurationModel("confId")).andReturn(null);

		cartServiceMock = EasyMock.createMock(CartService.class);
		classUnderTest.setCartService(cartServiceMock);
		EasyMock.expect(cartServiceMock.addConfigurationToCart(null)).andReturn(itemKey).once();
		EasyMock.expect(Boolean.valueOf(cartServiceMock.isItemAvailable("PK"))).andReturn(Boolean.FALSE);


		backendAvailabilityServiceMock = EasyMock.createMock(BackendAvailabilityService.class);
		classUnderTest.setBackendAvailabilityService(backendAvailabilityServiceMock);
		EasyMock.expect(Boolean.valueOf(backendAvailabilityServiceMock.isBackendDown())).andReturn(Boolean.FALSE).anyTimes();

		productConfigDefaultCartIntegrationFacadeMock = EasyMock.createMock(ConfigurationCartIntegrationFacade.class);
		classUnderTest.setProductConfigDefaultCartIntegrationFacade(productConfigDefaultCartIntegrationFacadeMock);

		EasyMock.expect(productConfigDefaultCartIntegrationFacadeMock.addConfigurationToCart(conf)).andReturn(itemKey).once();

		EasyMock.replay(conf, productConfigurationServiceMock, cartServiceMock, backendAvailabilityServiceMock,
				productConfigDefaultCartIntegrationFacadeMock);

	}


	private void prepareMocksForAddToCartUpdateBackendAvailable() throws CommerceCartModificationException
	{

		conf = EasyMock.createMock(ConfigurationData.class);
		EasyMock.expect(conf.getConfigId()).andReturn("confId");
		EasyMock.expect(conf.getCartItemPK()).andReturn("PK");

		productConfigurationServiceMock = EasyMock.createMock(ProductConfigurationService.class);
		classUnderTest.setProductConfigurationService(productConfigurationServiceMock);
		EasyMock.expect(productConfigurationServiceMock.retrieveConfigurationModel("confId")).andReturn(null);

		cartServiceMock = EasyMock.createMock(CartService.class);
		classUnderTest.setCartService(cartServiceMock);
		cartServiceMock.updateConfigurationInCart("PK", null);
		EasyMock.expectLastCall().once();
		EasyMock.expect(Boolean.valueOf(cartServiceMock.isItemAvailable("PK"))).andReturn(Boolean.TRUE);

		backendAvailabilityServiceMock = EasyMock.createMock(BackendAvailabilityService.class);
		classUnderTest.setBackendAvailabilityService(backendAvailabilityServiceMock);
		EasyMock.expect(Boolean.valueOf(backendAvailabilityServiceMock.isBackendDown())).andReturn(Boolean.FALSE).anyTimes();

		productConfigDefaultCartIntegrationFacadeMock = EasyMock.createMock(ConfigurationCartIntegrationFacade.class);
		classUnderTest.setProductConfigDefaultCartIntegrationFacade(productConfigDefaultCartIntegrationFacadeMock);

		EasyMock.expect(productConfigDefaultCartIntegrationFacadeMock.addConfigurationToCart(conf)).andReturn(itemKey).once();

		EasyMock.replay(conf, productConfigurationServiceMock, cartServiceMock, backendAvailabilityServiceMock,
				productConfigDefaultCartIntegrationFacadeMock);

	}


	@Test
	public void testAddToCartNoBackend() throws CommerceCartModificationException
	{
		//expect that the async facade is called
		prepareMocksForAddToCartBackendDown();
		final String key = classUnderTest.addConfigurationToCart(conf);
		EasyMock.verify(productConfigDefaultCartIntegrationFacadeMock);
		assertEquals(key, itemKey);
	}

	@Test
	public void testAddToCartBackendAvailable() throws CommerceCartModificationException
	{
		//expect that both config integration facades are called
		prepareMocksForAddToCartBackendAvailable();
		final String key = classUnderTest.addConfigurationToCart(conf);
		EasyMock.verify(productConfigDefaultCartIntegrationFacadeMock);
		assertEquals(key, itemKey);
	}

	@Test
	public void testAddToCartUpdateConfigurationAndBackendAvailable() throws CommerceCartModificationException
	{
		//expect that an update and and add is done
		prepareMocksForAddToCartUpdateBackendAvailable();
		final String key = classUnderTest.addConfigurationToCart(conf);
		EasyMock.verify(productConfigDefaultCartIntegrationFacadeMock);
		assertEquals(key, "PK");
	}




}
