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
package de.hybris.basecommerce.jalo;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.basecommerce.jalo.BasecommerceManager;
import de.hybris.platform.basecommerce.jalo.DefaultMultiAddressDeliveryCostsStrategy;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.jalo.CategoryManager;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.deliveryzone.jalo.Zone;
import de.hybris.platform.deliveryzone.jalo.ZoneDeliveryMode;
import de.hybris.platform.deliveryzone.jalo.ZoneDeliveryModeManager;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.c2l.Country;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.order.delivery.DefaultDeliveryCostsStrategy;
import de.hybris.platform.jalo.order.delivery.DeliveryCostsStrategy;
import de.hybris.platform.jalo.order.delivery.JaloDeliveryModeException;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * JUnit Tests for the Basecommerce extension
 */
public class BasecommerceTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(BasecommerceTest.class.getName());

	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;
	@Resource
	private CartService cartService;
	@Resource
	private OrderService orderService;
	@Resource
	private ProductService productService;
	@Resource
	private CatalogService catalogService;

	//private MultiAddressDeliveryMode multiDeliveryMode = null;
	private OrderModel order;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createOrder();
	}


	/**
	 * Scenario: 1 <br>
	 * testProduct0 -> deliveryModeCode1( delivery cost:= 4.2 )<br>
	 * testProduct1 -> deliveryModeCode1( delivery cost:= 4.2 )<br>
	 * ==> package no.1 ( delivery cost:= 4.2 )<br>
	 * testProduct2 -> deliveryModeCode3( delivery cost:= 3.8 )<br>
	 * ==> package no.2 ( delivery cost:= 3.8 )<br>
	 * usage of DefaultMultiAddressDeliveryCostsStrategy -> total sum of delivery costs: 8.0<br>
	 * <br>
	 * Scenario: 2 <br>
	 * usage of DefaultDeliveryCostsStrategy -> total sum of delivery costs: 0.0<br>
	 * <br>
	 * Scenario: 3<br>
	 * testProduct0 -> deliveryModeCode1( delivery cost:= 4.2 )<br>
	 * testProduct1 -> deliveryModeCode1( delivery cost:= 4.2 )<br>
	 * ==> package no.1 ( delivery cost:= 4.2 )<br>
	 * testProduct2 -> deliveryMode is null -> fallback to delivery mode of order( delivery cost:= 0.0 )<br>
	 * ==> package no.2 ( delivery cost:= 0.0 )<br>
	 * usage of DefaultMultiAddressDeliveryCostsStrategy -> total sum of delivery costs: 4.2<br>
	 * <br>
	 * Scenario: 4<br>
	 * testProduct0 -> deliveryAddress is null -> fallback to delivery address of order -> additional package<br>
	 * ==> package no.1 ( delivery cost:= 4.2 )<br>
	 * ==> package no.2 ( delivery cost:= 4.2 )<br>
	 * ==> package no.3 ( delivery cost:= 0.0 )<br>
	 * usage of DefaultMultiAddressDeliveryCostsStrategy -> total sum of delivery costs: 8.4<br>
	 * 
	 * @throws JaloPriceFactoryException
	 */
	@Test
	public void testMultiAddressCalculation() throws JaloDeliveryModeException, JaloPriceFactoryException
	{
		final AbstractOrder abstractOrder = ((AbstractOrder) modelService.toPersistenceLayer(order));
		final DeliveryCostsStrategy strategy = OrderManager.getInstance().getDeliveryCostsStrategy();

		// Scenario: 1
		// testProduct0 -> deliveryModeCode1( delivery cost:= 4.2 )
		// testProduct1 -> deliveryModeCode1( delivery cost:= 4.2 )
		// ==> package no.1 ( delivery cost:= 4.2 )
		// testProduct2 -> deliveryModeCode3( delivery cost:= 3.8 )
		// ==> package no.2 ( delivery cost:= 3.8 )
		// usage of DefaultMultiAddressDeliveryCostsStrategy -> total sum of delivery costs: 8.0
		OrderManager.getInstance().setDeliveryCostsStrategy(new DefaultMultiAddressDeliveryCostsStrategy());
		abstractOrder.recalculate();
		LOG.info("Scenario 1 -> " + abstractOrder.getDeliveryCosts());

		assertEquals("DeliveryCost calculation failed!", Double.valueOf(8.0), Double.valueOf(abstractOrder.getDeliveryCosts()));

		// Scenario: 2
		// usage of DefaultDeliveryCostsStrategy -> total sum of delivery costs: 0.0
		OrderManager.getInstance().setDeliveryCostsStrategy(new DefaultDeliveryCostsStrategy());
		abstractOrder.recalculate();
		LOG.info("Scenario 2 -> " + abstractOrder.getDeliveryCosts());

		assertEquals("DeliveryCost calculation failed!", Double.valueOf(0.0), Double.valueOf(abstractOrder.getDeliveryCosts()));

		// Scenario: 3
		// testProduct0 -> deliveryModeCode1( delivery cost:= 4.2 )
		// testProduct1 -> deliveryModeCode1( delivery cost:= 4.2 )
		// ==> package no.1 ( delivery cost:= 4.2 )
		// testProduct2 -> deliveryMode is null -> fallback to delivery mode of order( delivery cost:= 0.0 )
		// ==> package no.2 ( delivery cost:= 0.0 )
		// usage of DefaultMultiAddressDeliveryCostsStrategy -> total sum of delivery costs: 4.2
		final List<AbstractOrderEntryModel> entries = order.getEntries();
		for (final AbstractOrderEntryModel entry : entries)
		{
			if (entry.getProduct().getCode().equals("testProduct2"))
			{
				BasecommerceManager.getInstance().setDeliveryMode((AbstractOrderEntry) modelService.toPersistenceLayer(entry), null);
			}
		}
		OrderManager.getInstance().setDeliveryCostsStrategy(new DefaultMultiAddressDeliveryCostsStrategy());
		abstractOrder.recalculate();
		LOG.info("Scenario 3 -> " + abstractOrder.getDeliveryCosts());

		assertEquals("DeliveryCost calculation failed!", Double.valueOf(4.2), Double.valueOf(abstractOrder.getDeliveryCosts()));

		//Scenario: 4
		// testProduct0 -> deliveryAddress is null -> fallback to delivery address of order -> additional package
		// ==> package no.1 ( delivery cost:= 4.2 )
		// ==> package no.2 ( delivery cost:= 4.2 )
		// ==> package no.3 ( delivery cost:= 0.0 )
		// usage of DefaultMultiAddressDeliveryCostsStrategy -> total sum of delivery costs: 8.4
		for (final AbstractOrderEntryModel entry : entries)
		{
			if (entry.getProduct().getCode().equals("testProduct0"))
			{
				BasecommerceManager.getInstance().setDeliveryAddress((AbstractOrderEntry) modelService.toPersistenceLayer(entry),
						null); // fallback to delivery address of order -> additional package

			}
		}
		OrderManager.getInstance().setDeliveryCostsStrategy(new DefaultMultiAddressDeliveryCostsStrategy());
		abstractOrder.recalculate();
		LOG.info("Scenario 4 -> " + abstractOrder.getDeliveryCosts());

		assertEquals("DeliveryCost calculation failed!", Double.valueOf(8.4), Double.valueOf(abstractOrder.getDeliveryCosts()));


		OrderManager.getInstance().setDeliveryCostsStrategy(strategy);
	}

	public static void createCoreData() throws Exception
	{
		LOG.info("Creating essential data for core ...");
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		// importing test csv
		importCsv("/servicelayer/test/testBasics.csv", "windows-1252");
		LOG.info("Finished creating essential data for core in " + (System.currentTimeMillis() - startTime) + "ms");
	}

	public static void createDefaultCatalog() throws Exception
	{
		LOG.info("Creating test catalog ...");
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		final long startTime = System.currentTimeMillis();

		importCsv("/servicelayer/test/testCatalog.csv", "windows-1252");

		// checking imported stuff
		final CatalogVersion version = CatalogManager.getInstance().getCatalog("testCatalog").getCatalogVersion("Online");
		Assert.assertNotNull(version);
		JaloSession.getCurrentSession().getSessionContext()
				.setAttribute(CatalogConstants.SESSION_CATALOG_VERSIONS, Collections.singletonList(version));
		//setting catalog to session and admin user
		final Category category = CategoryManager.getInstance().getCategoriesByCode("testCategory0").iterator().next();
		Assert.assertNotNull(category);
		final Product product = (Product) ProductManager.getInstance().getProductsByCode("testProduct0").iterator().next();
		Assert.assertNotNull(product);
		Assert.assertEquals(category, CategoryManager.getInstance().getCategoriesByProduct(product).iterator().next());

		LOG.info("Finished creating test catalog in " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@SuppressWarnings("deprecation")
	public void createOrder() throws InvalidCartException, Exception
	{
		LOG.info("Creating order ...");

		final CurrencyModel currency = modelService.create(CurrencyModel.class);
		currency.setIsocode("EUR1");
		currency.setSymbol("EUR1");
		currency.setBase(Boolean.TRUE);
		currency.setActive(Boolean.TRUE);
		currency.setConversion(Double.valueOf(1));
		modelService.save(currency);

		final ProductModel product0 = productService.getProduct("testProduct0");
		final PriceRowModel prmodel0 = modelService.create(PriceRowModel.class);
		prmodel0.setCurrency(currency);
		prmodel0.setMinqtd(Long.valueOf(1));
		prmodel0.setNet(Boolean.TRUE);
		prmodel0.setPrice(Double.valueOf(5.00));
		prmodel0.setUnit(productService.getUnit("kg"));
		prmodel0.setProduct(product0);
		prmodel0.setCatalogVersion(catalogService.getCatalogVersion("testCatalog", "Online"));
		modelService.saveAll(Arrays.asList(prmodel0, product0));


		final ProductModel product1 = productService.getProduct("testProduct1");
		final PriceRowModel prmodel1 = modelService.create(PriceRowModel.class);
		prmodel1.setCurrency(currency);
		prmodel1.setMinqtd(Long.valueOf(1));
		prmodel1.setNet(Boolean.TRUE);
		prmodel1.setPrice(Double.valueOf(7.00));
		prmodel1.setUnit(productService.getUnit("kg"));
		prmodel1.setProduct(product1);
		prmodel1.setCatalogVersion(catalogService.getCatalogVersion("testCatalog", "Online"));
		modelService.saveAll(Arrays.asList(prmodel1, product1));

		final ProductModel product2 = productService.getProduct("testProduct2");
		final PriceRowModel prmodel2 = modelService.create(PriceRowModel.class);
		prmodel2.setCurrency(currency);
		prmodel2.setMinqtd(Long.valueOf(1));
		prmodel2.setNet(Boolean.TRUE);
		prmodel2.setPrice(Double.valueOf(7.00));
		prmodel2.setUnit(productService.getUnit("kg"));
		prmodel2.setProduct(product2);
		prmodel2.setCatalogVersion(catalogService.getCatalogVersion("testCatalog", "Online"));
		modelService.saveAll(Arrays.asList(prmodel2, product2));

		final CartModel cart = cartService.getSessionCart();
		final UserModel user = userService.getCurrentUser();
		cartService.addToCart(cart, product0, 2, null);
		cartService.addToCart(cart, product1, 2, null);
		cartService.addToCart(cart, product2, 2, null);

		final CountryModel country = new CountryModel();
		country.setActive(Boolean.TRUE);
		country.setIsocode("xyz");
		modelService.save(country);

		final Zone zone = ZoneDeliveryModeManager.getInstance().createZone("myZone");
		zone.addCountry((Country) modelService.toPersistenceLayer(country));

		final AddressModel defaultDeliveryAddress = new AddressModel();
		defaultDeliveryAddress.setOwner(user);
		defaultDeliveryAddress.setFirstname("Albert");
		defaultDeliveryAddress.setLastname("Einstein");
		defaultDeliveryAddress.setTown("Munich");
		defaultDeliveryAddress.setCountry(country);

		final DebitPaymentInfoModel paymentInfo = new DebitPaymentInfoModel();
		paymentInfo.setOwner(cart);
		paymentInfo.setBank("MyBank");
		paymentInfo.setUser(user);
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("Ich");

		order = orderService.placeOrder(cart, defaultDeliveryAddress, null, paymentInfo);

		final AddressModel deliveryAddress1 = new AddressModel();
		deliveryAddress1.setOwner(user);
		deliveryAddress1.setFirstname("Albert1");
		deliveryAddress1.setLastname("Einstein1");
		deliveryAddress1.setTown("Munich1");
		deliveryAddress1.setCountry(country);

		final AddressModel deliveryAddress2 = new AddressModel();
		deliveryAddress2.setOwner(user);
		deliveryAddress2.setFirstname("Albert2");
		deliveryAddress2.setLastname("Einstein2");
		deliveryAddress2.setTown("Munich2");
		deliveryAddress2.setCountry(country);

		final AddressModel deliveryAddress3 = new AddressModel();
		deliveryAddress3.setOwner(user);
		deliveryAddress3.setFirstname("Albert3");
		deliveryAddress3.setLastname("Einstein3");
		deliveryAddress3.setTown("Munich3");
		deliveryAddress3.setCountry(country);

		modelService.saveAll(deliveryAddress1, deliveryAddress2, deliveryAddress3);

		final List<AbstractOrderEntryModel> entries = order.getEntries();

		final AbstractOrderEntryModel entry1 = entries.get(0);
		entry1.setDeliveryAddress(deliveryAddress1); // 2
		final ComposedType dmType = TypeManager.getInstance().getComposedType(ZoneDeliveryMode.class);
		final ZoneDeliveryMode dm1 = (ZoneDeliveryMode) OrderManager.getInstance().createDeliveryMode(dmType, "deliveryModeCode1");
		dm1.setCost((de.hybris.platform.jalo.c2l.Currency) modelService.toPersistenceLayer(currency), 1, 4.2, zone);

		BasecommerceManager.getInstance().setDeliveryMode((AbstractOrderEntry) modelService.toPersistenceLayer(entry1), dm1);

		final AbstractOrderEntryModel entry2 = entries.get(1);
		entry2.setDeliveryAddress(deliveryAddress1);
		BasecommerceManager.getInstance().setDeliveryMode((AbstractOrderEntry) modelService.toPersistenceLayer(entry2), dm1); // 2

		final AbstractOrderEntryModel entry3 = entries.get(2);
		entry3.setDeliveryAddress(deliveryAddress3);
		final ZoneDeliveryMode dm3 = (ZoneDeliveryMode) OrderManager.getInstance().createDeliveryMode(dmType, "deliveryModeCode3");
		dm3.setCost((de.hybris.platform.jalo.c2l.Currency) modelService.toPersistenceLayer(currency), 1, 3.8, zone);

		BasecommerceManager.getInstance().setDeliveryMode((AbstractOrderEntry) modelService.toPersistenceLayer(entry3), dm3);

		modelService.saveAll(entry1, entry2, entry3);
	}
}
