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
package de.hybris.platform.commerceservices.price;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.price.impl.DefaultCommercePriceService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.PriceValue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * JUnit test suite for {@link DefaultCommercePriceService}
 */
@IntegrationTest
public class DefaultCommercePriceServiceIntTest extends ServicelayerTest
{
	private static final double DELTA = 0.001;

	private static final double PRICE_PROD_1 = 1d;

	private static final double PRICE_PROD_2 = 2d;

	private static final double PRICE_PROD_4 = 4d;

	private static final boolean GROSS = false;

	private static final boolean NET = true;


	@Resource
	private UserService userService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private ModelService modelService;

	@Resource
	private PriceService priceService;

	@Resource
	private CommercePriceService commercePriceService;

	@Resource
	private ProductService productService;

	@Resource
	private CartService cartService;

	@Resource
	private SearchRestrictionService searchRestrictionService;

	@Resource
	private SessionService sessionService;

	private ProductModel product;

	private ProductModel baseProduct;

	private ProductModel baseProduct2;

	private ProductModel baseProduct3;

	@Before
	public void setUp() throws Exception
	{
		userService.setCurrentUser(userService.getAdminUser());
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		importCsv("/commerceservices/test/testCommercePrice.csv", "utf-8");

		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				searchRestrictionService.disableSearchRestrictions();

				product = productService.getProductForCode("HW1210-3422");
				baseProduct = productService.getProductForCode("HW1210-3423");
				baseProduct2 = productService.getProductForCode("HW1210-3424");
				baseProduct3 = productService.getProductForCode("HW1210-3425");
			}
		});
	}

	@Test
	public void testBaseStore()
	{
		final BaseSiteModel baseSiteModel = createSite(GROSS);
		verifyProduct(product, PRICE_PROD_1, PRICE_PROD_1, GROSS);
		verifyProduct(baseProduct, PRICE_PROD_4, PRICE_PROD_4, GROSS);
		updateBaseSite(baseSiteModel, NET);
		final double netPrice = calcNet(PRICE_PROD_1, true);
		verifyProduct(product, netPrice, netPrice, NET);
		final double netPrice2 = calcNet(PRICE_PROD_4, false);
		verifyProduct(baseProduct, netPrice2, netPrice2, NET);
	}

	@Test
	public void testCart()
	{
		final CartModel cart = cartService.getSessionCart();
		Assert.assertEquals(GROSS, cart.getNet().booleanValue());
		verifyProduct(product, PRICE_PROD_1, PRICE_PROD_1, GROSS);
		verifyProduct(baseProduct, PRICE_PROD_4, PRICE_PROD_4, GROSS);
		final CartModel curCart = cartService.getSessionCart();
		curCart.setNet(Boolean.TRUE);
		final double netPrice = calcNet(PRICE_PROD_1, true);
		verifyProduct(product, netPrice, netPrice, NET);
		final double netPrice2 = calcNet(PRICE_PROD_4, false);
		verifyProduct(baseProduct, netPrice2, netPrice2, NET);
	}

	@Test
	public void testDefault()
	{
		verifyProduct(product, PRICE_PROD_1, PRICE_PROD_1, GROSS);
		verifyProduct(baseProduct, PRICE_PROD_4, PRICE_PROD_4, GROSS);
		verifyProduct(baseProduct2, PRICE_PROD_1, PRICE_PROD_1, GROSS);
		verifyProduct(baseProduct3, PRICE_PROD_2, PRICE_PROD_2, GROSS);
	}

	@Test
	public void testCartFactory()
	{
		Assert.assertEquals(GROSS, cartService.getSessionCart().getNet().booleanValue());
		cartService.removeSessionCart();
		final BaseSiteModel baseSiteModel = createSite(GROSS);
		Assert.assertEquals(GROSS, cartService.getSessionCart().getNet().booleanValue());
		cartService.removeSessionCart();
		updateBaseSite(baseSiteModel, NET);
		Assert.assertEquals(NET, cartService.getSessionCart().getNet().booleanValue());
	}

	protected double calcNet(final double price, final boolean full)
	{
		return price / (full ? 1.2d : 1.1d);
	}

	protected void verifyProduct(final ProductModel product, final double webPrice, final double fromPrice, final boolean net)
	{
		verifyProduct(product, webPrice, fromPrice, net, true);
	}

	protected void verifyProduct(final ProductModel product, final double webPrice, final double fromPrice, final boolean net,
			final boolean purchasable)
	{
		final List<PriceInformation> list = priceService.getPriceInformationsForProduct(product);
		if (purchasable)
		{
			Assert.assertEquals(1, list.size());
			verifyPrice(list.get(0), webPrice, net);
			verifyPrice(commercePriceService.getWebPriceForProduct(product), webPrice, net);
		}
		else
		{
			Assert.assertEquals(0, list.size());
			Assert.assertNull(commercePriceService.getWebPriceForProduct(product));
		}
		verifyPrice(commercePriceService.getFromPriceForProduct(product), fromPrice, net);
	}

	protected void verifyPrice(final PriceInformation price, final double expected, final boolean net)
	{
		final PriceValue value = price.getPriceValue();
		Assert.assertEquals(expected, value.getValue(), DELTA);
		Assert.assertEquals("EUR", value.getCurrencyIso());
		Assert.assertEquals(net, value.isNet());
	}

	protected BaseSiteModel createSite(final boolean net)
	{
		final BaseSiteModel baseSiteModel = modelService.create(BaseSiteModel.class);
		baseSiteModel.setName("site");
		baseSiteModel.setUid("site");
		final BaseStoreModel store1 = modelService.create(BaseStoreModel.class);
		store1.setUid("store");
		store1.setNet(net);
		final List<BaseStoreModel> stores = new LinkedList<BaseStoreModel>();
		stores.add(store1);
		baseSiteModel.setStores(stores);
		modelService.saveAll();
		baseSiteService.setCurrentBaseSite(baseSiteModel, false);
		return baseSiteModel;
	}

	protected void updateBaseSite(final BaseSiteModel siteModel, final boolean net)
	{
		siteModel.getStores().get(0).setNet(net);
		modelService.save(siteModel);
	}
}
