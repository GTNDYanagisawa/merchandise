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
package de.hybris.platform.voucher.jalo;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloImplementationManager;
import de.hybris.platform.jalo.order.delivery.DeliveryMode;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.order.TestPriceFactory;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.voucher.model.PromotionVoucherModel;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;


/**
 *
 */
@Ignore
public abstract class AbstractVoucherTest extends ServicelayerTest
{
	protected TestPriceFactory priceFactory;

	protected CatalogModel catalog;
	protected CatalogVersionModel catalogVersion;
	protected ProductModel product;
	protected CurrencyModel curr;
	protected UnitModel unit;
	protected PromotionVoucherModel promotionVoucher;
	protected DeliveryModeModel deliveryMode;
	private PriceFactory prevPriceFactory;
	@Resource
	protected ModelService modelService;


	protected double discountAmount;
	protected final double deliveryCost = TestDeliveryMode.deliveryCost;

	@Before
	public void setUp() throws ConsistencyCheckException
	{
		// mess with delivery mode - must be first
		JaloImplementationManager.replaceCoreJaloClass(DeliveryMode.class, TestDeliveryMode.class);

		deliveryMode = new DeliveryModeModel();
		deliveryMode.setCode("testDeliveryModel");
		deliveryMode.setActive(Boolean.TRUE);
		modelService.save(deliveryMode);

		discountAmount = 10d;

		unit = modelService.create(UnitModel.class);
		unit.setCode("myUnit");
		unit.setName("myUnit");
		unit.setUnitType("test");

		catalog = modelService.create(CatalogModel.class);
		catalog.setId("myCatalog");
		catalog.setDefaultCatalog(Boolean.TRUE);

		catalogVersion = modelService.create(CatalogVersionModel.class);
		catalogVersion.setCatalog(catalog);
		catalogVersion.setVersion("Online");
		catalogVersion.setActive(Boolean.TRUE);

		product = modelService.create(ProductModel.class);
		product.setApprovalStatus(ArticleApprovalStatus.APPROVED);
		product.setCatalogVersion(catalogVersion);
		product.setCode("myProduct");
		product.setUnit(unit);

		curr = modelService.create(CurrencyModel.class);
		curr.setIsocode("EUR");
		curr.setBase(Boolean.TRUE);
		curr.setSymbol("E");

		promotionVoucher = modelService.create(PromotionVoucherModel.class);

		promotionVoucher.setCode("testFreeSHippingVoucher");
		promotionVoucher.setFreeShipping(Boolean.TRUE);
		promotionVoucher.setVoucherCode("123");
		promotionVoucher.setValue(Double.valueOf(discountAmount));
		promotionVoucher.setCurrency(curr);
		promotionVoucher.setRedemptionQuantityLimitPerUser(Integer.valueOf(1));
		promotionVoucher.setRedemptionQuantityLimit(Integer.valueOf(10));

		modelService.saveAll();

		// TODO setup items
		prevPriceFactory = jaloSession.getSessionContext().getPriceFactory();
		priceFactory = new TestPriceFactory();
		jaloSession.getSessionContext().setPriceFactory(priceFactory);

		//make the test price factory return 15EUR for the test product
		priceFactory.setBasePrice((Product) modelService.getSource(product), new PriceValue(curr.getIsocode(), 15, false));

	}

	@After
	public void tearDown()
	{
		jaloSession.getSessionContext().setPriceFactory(prevPriceFactory);
		// switch back jalo class for delivery mode
		JaloImplementationManager.replaceCoreJaloClass(DeliveryMode.class, DeliveryMode.class);
	}
}
