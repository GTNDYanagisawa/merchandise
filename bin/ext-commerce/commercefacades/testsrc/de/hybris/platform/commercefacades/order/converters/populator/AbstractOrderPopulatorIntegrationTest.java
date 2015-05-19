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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * Class testing performance of cart converters. There are no separate methods for entry converters because they are
 * included into order converters tests.
 * 
 */
@IntegrationTest
public class AbstractOrderPopulatorIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource(name = "defaultCartConverter")
	private AbstractPopulatingConverter<CartModel, CartData> cartConverter;

	@Resource(name = "defaultOrderConverter")
	private AbstractPopulatingConverter<OrderModel, OrderData> orderConverter;

	@Resource
	private ModelService modelService;
	@Resource
	private FlexibleSearchService flexibleSearchService;

	private CartModel cartModel;
	private OrderModel orderModel;

	protected static final String CART_CODE = "AbstractOrderConverterIntegrationTestCart";
	protected static final String ORDER_CODE = "AbstractOrderConverterIntegrationTestOrder";
	protected static final String PRODUCT_CODE = "AbstractOrderConverterIntegrationTestProduct";
	protected static final String USER_CODE = "AbstractOrderConverterIntegrationTestUser";

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		final CatalogModel catalog = flexibleSearchService
				.<CatalogModel> search("SELECT {PK} FROM {Catalog} WHERE {id}='testCatalog'").getResult().get(0);

		final CurrencyModel currencyModel = (CurrencyModel) flexibleSearchService
				.search("SELECT {PK} FROM {Currency} WHERE {isocode}='EUR'").getResult().get(0);

		final CustomerModel customerModel = modelService.create(CustomerModel.class);
		customerModel.setUid(USER_CODE);

		final ProductModel productModel = modelService.create(ProductModel.class);
		productModel.setCode(PRODUCT_CODE);
		productModel.setCatalogVersion(flexibleSearchService
				.<CatalogVersionModel> search("SELECT {PK} FROM {CatalogVersion} WHERE {version}='Online' AND {catalog}=?catalog",
						Collections.singletonMap("catalog", catalog)).getResult().get(0));

		cartModel = modelService.create(CartModel.class);
		cartModel.setCode(CART_CODE);
		cartModel.setCurrency(currencyModel);
		cartModel.setTotalPrice(Double.valueOf(1.0));
		cartModel.setSubtotal(Double.valueOf(2.0));
		cartModel.setDeliveryCost(Double.valueOf(3.0));
		cartModel.setDate(new Date());
		cartModel.setNet(Boolean.FALSE);
		cartModel.setUser(customerModel);

		final UnitModel unitModel = modelService.create(UnitModel.class);
		unitModel.setUnitType("awsome");
		unitModel.setCode("goblins");

		final CartEntryModel entry = modelService.create(CartEntryModel.class);
		entry.setProduct(productModel);
		entry.setOrder(cartModel);
		entry.setBasePrice(Double.valueOf(2.2));
		entry.setTotalPrice(Double.valueOf(3.9));
		entry.setQuantity(Long.valueOf(1));
		entry.setUnit(unitModel);

		final List<AbstractOrderEntryModel> entryList = new ArrayList<AbstractOrderEntryModel>();
		entryList.add(entry);
		cartModel.setEntries(entryList);
		modelService.save(cartModel);

		orderModel = modelService.create(OrderModel.class);
		orderModel.setCode(ORDER_CODE);
		orderModel.setCurrency(currencyModel);
		orderModel.setTotalPrice(Double.valueOf(1.0));
		orderModel.setSubtotal(Double.valueOf(2.0));
		orderModel.setDeliveryCost(Double.valueOf(3.0));
		orderModel.setDate(new Date());
		orderModel.setNet(Boolean.TRUE);
		orderModel.setUser(customerModel);

		final OrderEntryModel orderEntry = modelService.create(OrderEntryModel.class);
		orderEntry.setProduct(productModel);
		orderEntry.setOrder(orderModel);
		orderEntry.setBasePrice(Double.valueOf(2.2));
		orderEntry.setTotalPrice(Double.valueOf(3.9));
		orderEntry.setQuantity(Long.valueOf(1));
		orderEntry.setUnit(unitModel);

		final List<AbstractOrderEntryModel> orderEntryList = new ArrayList<AbstractOrderEntryModel>();
		orderEntryList.add(orderEntry);
		orderModel.setEntries(orderEntryList);
		modelService.save(orderModel);
	}

	@Test
	public void testCartConverter()
	{
		final CartData cartData = cartConverter.convert(cartModel);
		Assert.assertNotNull("CartData was null", cartData);
		Assert.assertEquals("Cart Code did not match", CART_CODE, cartData.getCode());
		Assert.assertEquals("Cart Base Price did not match", BigDecimal.valueOf(2.2), cartData.getEntries().iterator().next()
				.getBasePrice().getValue());
	}

	@Test
	public void testOrderConverter()
	{
		final OrderData orderData = orderConverter.convert(orderModel);
		Assert.assertNotNull("OrderData was null", orderData);
		Assert.assertEquals("Order Code did not match", ORDER_CODE, orderData.getCode());
		Assert.assertEquals("Order Base Price did not match", BigDecimal.valueOf(2.2), orderData.getEntries().iterator().next()
				.getBasePrice().getValue());
	}

}
