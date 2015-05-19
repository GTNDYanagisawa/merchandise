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
package de.hybris.platform.commerceservices.strategies.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.dao.CommerceCartDao;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultStaleCartRemovalStrategyTest extends ServicelayerBaseTest
{

	@Resource
	private ModelService modelService;

	@Resource
	private CommerceCartDao commerceCartDao;

	@Resource
	private DefaultStaleCartRemovalStrategy staleCartRemovalStrategy;

	private BaseSiteModel baseSite;

	private UserModel user;

	private CartModel newCart;

	private CartModel oldCart;

	private CurrencyModel currency;

	private final static String TEST_USER = "TestUser";
	private final static String TEST_SITE = "TestSite";

	@Before
	public void setUp()
	{
		user = getModelService().create(UserModel.class);
		user.setUid(TEST_USER);
		getModelService().save(user);

		baseSite = getModelService().create(BaseSiteModel.class);
		baseSite.setUid(TEST_SITE);
		getModelService().save(baseSite);

		currency = getModelService().create(CurrencyModel.class);
		currency.setIsocode("USD");
		getModelService().save(currency);

		oldCart = getModelService().create(CartModel.class);
		oldCart.setSite(baseSite);
		oldCart.setModifiedtime(new Date());
		oldCart.setUser(user);
		oldCart.setDate(new Date());
		oldCart.setCurrency(currency);
		oldCart.setGuid("oldCart");

		getModelService().save(oldCart);
	}

	@Test
	public void shouldNotRemoveCart()
	{
		Assert.assertTrue(getCommerceCartDao().getCartsForRemovalForSiteAndUser(oldCart.getModifiedtime(), baseSite, user).size() == 1);

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(oldCart);
		parameter.setBaseSite(baseSite);
		parameter.setUser(user);
		staleCartRemovalStrategy.removeStaleCarts(parameter);

		Assert.assertTrue(getCommerceCartDao().getCartsForRemovalForSiteAndUser(oldCart.getModifiedtime(), baseSite, user).size() == 1);
	}

	@Test
	public void shouldRemoveCart()
	{
		newCart = getModelService().create(CartModel.class);
		newCart.setSite(baseSite);
		newCart.setModifiedtime(new Date());
		newCart.setUser(user);
		newCart.setDate(new Date());
		newCart.setCurrency(currency);
		newCart.setGuid("newCart");
		getModelService().save(newCart);

		Assert.assertTrue(getCommerceCartDao().getCartsForRemovalForSiteAndUser(newCart.getModifiedtime(), baseSite, user).size() == 2);

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(newCart);
		parameter.setBaseSite(baseSite);
		parameter.setUser(user);

		staleCartRemovalStrategy.removeStaleCarts(parameter);

		Assert.assertTrue(getCommerceCartDao().getCartsForRemovalForSiteAndUser(newCart.getModifiedtime(), baseSite, user).size() == 1);

	}

	private ModelService getModelService()
	{
		return modelService;
	}

	private CommerceCartDao getCommerceCartDao()
	{
		return commerceCartDao;
	}

}
