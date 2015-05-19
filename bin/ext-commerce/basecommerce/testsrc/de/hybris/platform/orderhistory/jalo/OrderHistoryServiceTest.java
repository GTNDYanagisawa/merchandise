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
package de.hybris.platform.orderhistory.jalo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.order.Order;
import de.hybris.platform.jalo.order.OrderEntry;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.order.delivery.DefaultDeliveryCostsStrategy;
import de.hybris.platform.jalo.order.delivery.DeliveryCostsStrategy;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.order.TestPriceFactory;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.jalo.mockups.actions.TestAction;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.PriceValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class OrderHistoryServiceTest extends ServicelayerTransactionalTest
{


	@Resource
	private CartService cartService;
	@Resource
	private OrderService orderService;
	@Resource
	private UserService userService;
	@Resource
	private OrderHistoryService orderHistoryService;
	@Resource
	private ModelService modelService;

	private DeliveryCostsStrategy strategy;
	private OrderManager orderManager;

	private PriceFactory priceFactory;

	@Before
	public void setUp() throws Exception
	{
		priceFactory = jaloSession.getPriceFactory();
		createCoreData();
		orderManager = OrderManager.getInstance();
		strategy = orderManager.getDeliveryCostsStrategy();
		orderManager.setDeliveryCostsStrategy(new DefaultDeliveryCostsStrategy());
	}

	@After
	public void tearDown()
	{
		orderManager.setDeliveryCostsStrategy(strategy);
		jaloSession.setPriceFactory(priceFactory);
	}

	/**
	 * tests creation of OrderHistoryEntry and fetching them as well.
	 * 
	 * @throws Exception
	 */
	protected OrderModel placeOrder(final UserModel user) throws Exception //NOPMD
	{
		final AddressModel deliveryAddress = modelService.create(AddressModel.class);
		deliveryAddress.setFirstname("Krzysztof");
		deliveryAddress.setLastname("Kwiatosz");
		deliveryAddress.setTown("Katowice");
		deliveryAddress.setOwner(user);

		final DebitPaymentInfoModel paymentInfo = modelService.create(DebitPaymentInfoModel.class);
		//		paymentInfo.setOwner(cart);
		paymentInfo.setBank("Bank");
		paymentInfo.setUser(user);
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("I");
		return orderService.placeOrder(cartService.getSessionCart(), deliveryAddress, null, paymentInfo);
	}


	@Test
	public void testEntries() throws Exception
	{
		final OrderModel order = placeOrder(userService.getCurrentUser());

		final List<OrderHistoryEntryModel> entriesBefore = order.getHistoryEntries();

		//creation of orderhistoryEntry
		final OrderHistoryEntryModel historyEntry0 = createEntry(order, true);

		//check the orders history entries
		modelService.refresh(order);

		final List<OrderHistoryEntryModel> entriesAfter = order.getHistoryEntries();

		Assert.assertTrue("HistoryEntries collection not of expected size", entriesBefore.size() < entriesAfter.size());
		Assert.assertTrue("Order doesn't own created history entry", entriesAfter.contains(historyEntry0));
		Assert.assertNotNull("Order history entry doesn't have relation to order. OrderHistoryEntryModel.order is null",
				entriesAfter.get(entriesAfter.size() - 1).getOrder());
		Assert.assertTrue("Order history entry doesn't have relation to the proper order", entriesAfter
				.get(entriesAfter.size() - 1).getOrder().equals(order));

		//add additional two history entries
		createEntry(order, false);
		createEntry(order, false);

		modelService.saveAll();
		modelService.refresh(order);

		//Now the size should be 3
		Assert.assertTrue("Wrong size of the order history entries", order.getHistoryEntries().size() == 3);
		int previous = -1;
		//check proper assignment of order positions attributes
		for (final OrderHistoryEntryModel entryModel : order.getHistoryEntries())
		{
			final OrderHistoryEntry entry = modelService.getSource(entryModel);
			previous += 1;
			//Every next enrty should have incremented orderPOS attribute
			Assert.assertEquals(previous, entry.getOrderPOSAsPrimitive());
		}

		//consequently - if another entry is added - its orderPOS attribute should be equal to the amount of history entries before it's creation
		final int nextval = order.getHistoryEntries().size();
		final OrderHistoryEntryModel historyEntry3 = createEntry(order, true);
		Assert.assertEquals("Invalid orderPOS attrinute of OrderHistoryEntry", nextval,
				((OrderHistoryEntry) modelService.getSource(historyEntry3)).getOrderPOSAsPrimitive());

	}

	/**
	 * Tests fetching OrderHistoryEntries (or descriptions only) of an order for the given time range.
	 * 
	 * @throws Exception
	 */

	@Test
	public void testTimeRange() throws Exception
	{

		//place order
		final OrderModel order = placeOrder(userService.getCurrentUser());

		//create 2 history entries for this order and store time markers in between
		final Date date1 = new Date();
		Thread.sleep(200);
		final OrderHistoryEntryModel historyEntry1 = createEntry(order, true, "OK");
		final String description1 = historyEntry1.getDescription();
		Thread.sleep(200);
		final Date date2 = new Date();
		Thread.sleep(200);
		final OrderHistoryEntryModel historyEntry2 = createEntry(order, true, "NOK");
		final String description2 = historyEntry2.getDescription();
		Thread.sleep(200);
		final Date date3 = new Date();

		//refresh the order
		modelService.refresh(order);

		//verify if fetching of history entries with time constraints works properly

		final String msg = "Time constrained fetching of order history entries failed";
		//full set
		Collection<OrderHistoryEntryModel> entries = orderHistoryService.getHistoryEntries(order, null, null);
		Collection<String> descriptions = orderHistoryService.getHistoryEntriesDescriptions(order, null, null);

		assertTrue(msg, entries.contains(historyEntry1) && entries.contains(historyEntry2));
		assertTrue(msg, descriptions.contains(description1) && descriptions.contains(description2));

		entries = orderHistoryService.getHistoryEntries(order, date1, date3);
		descriptions = orderHistoryService.getHistoryEntriesDescriptions(order, date1, date3);

		assertTrue(msg, entries.contains(historyEntry1) && entries.contains(historyEntry2));
		assertTrue(msg, descriptions.contains(description1) && descriptions.contains(description2));

		//half sets
		entries = orderHistoryService.getHistoryEntries(order, date2, null);
		descriptions = orderHistoryService.getHistoryEntriesDescriptions(order, date2, null);

		assertTrue(msg, !entries.contains(historyEntry1) && entries.contains(historyEntry2));
		assertTrue(msg, !descriptions.contains(description1) && descriptions.contains(description2));

		entries = orderHistoryService.getHistoryEntries(order, null, date2);
		descriptions = orderHistoryService.getHistoryEntriesDescriptions(order, null, date2);

		assertTrue(msg, entries.contains(historyEntry1) && !entries.contains(historyEntry2));
		assertTrue(msg, descriptions.contains(description1) && !descriptions.contains(description2));

		//empty set
		entries = orderHistoryService.getHistoryEntries(order, date3, date1);
		descriptions = orderHistoryService.getHistoryEntriesDescriptions(order, date3, date1);

		assertTrue(msg, entries.isEmpty());
		assertTrue(msg, descriptions.isEmpty());

	}

	/**
	 * Tests fetching OrderHistoryEntries of all Orders of the same customer
	 * 
	 * @throws Exception
	 */

	@Test
	public void testOrderOwner() throws Exception
	{
		final String msg = "Wrong order history entry in the resulting collection";
		final UserModel user1 = modelService.create(UserModel.class);
		user1.setName("test1");
		user1.setUid("test1");
		modelService.save(user1);

		final UserModel user2 = modelService.create(UserModel.class);
		user2.setName("test2");
		user2.setUid("test2");
		modelService.save(user2);

		final OrderModel order1 = placeOrder(user1);
		//need to add user manually - default place order strategy always takes current user.
		order1.setUser(user1);
		modelService.save(order1);
		modelService.refresh(order1);

		final OrderHistoryEntryModel historyEntry1 = createEntry(order1, true);

		final OrderModel order2 = placeOrder(user2);
		order2.setUser(user2);
		modelService.save(order2);
		modelService.refresh(order2);

		final OrderHistoryEntryModel historyEntry2 = createEntry(order2, true);

		Collection<OrderHistoryEntryModel> entries = orderHistoryService.getHistoryEntries(user1, null, null);
		assertTrue(msg, entries.contains(historyEntry1) && !entries.contains(historyEntry2));
		Assert.assertEquals("Order history entry relates to a wrong order", order1, entries.iterator().next().getOrder());
		Assert.assertEquals("Order history entry relates to a wrong user", user1, entries.iterator().next().getOrder().getUser());
		entries = orderHistoryService.getHistoryEntries(user2, null, null);
		assertTrue(msg, !entries.contains(historyEntry1) && entries.contains(historyEntry2));
		Assert.assertEquals("Order history entry relates to a wrong order", order2, entries.iterator().next().getOrder());
		Assert.assertEquals("Order history entry relates to a wrong oser", user2, entries.iterator().next().getOrder().getUser());

	}

	@Test
	@SuppressWarnings("PMD")
	public void testOrderCloning()
	{
		final CatalogModel cat = new CatalogModel();
		cat.setId("cat");

		final CatalogVersionModel catalogVersion = new CatalogVersionModel();
		catalogVersion.setCatalog(cat);
		catalogVersion.setVersion("online");

		final UnitModel unit = new UnitModel();
		unit.setCode("u");
		unit.setUnitType("u");
		unit.setConversion(Double.valueOf(1));

		final CurrencyModel curr = modelService.create(CurrencyModel.class);
		curr.setIsocode("cur");
		curr.setSymbol("CUR");
		curr.setActive(Boolean.TRUE);
		curr.setConversion(Double.valueOf(1));
		curr.setDigits(Integer.valueOf(2));

		final CustomerModel cust = new CustomerModel();
		cust.setUid("cust");

		final AddressModel adr1 = new AddressModel();
		adr1.setOwner(cust);
		adr1.setStreetname("adr1 street");
		adr1.setStreetnumber("adr1 street number");
		adr1.setTown("adr1 town");

		final AddressModel adr2 = new AddressModel();
		adr2.setOwner(cust);
		adr2.setStreetname("adr2 street");
		adr2.setStreetnumber("adr2 street number");
		adr2.setTown("adr2 town");

		cust.setDefaultPaymentAddress(adr1);
		cust.setDefaultShipmentAddress(adr2);

		final ProductModel product = new ProductModel();
		product.setCode("p");
		product.setCatalogVersion(catalogVersion);

		final Date orderDate = new Date();

		final Boolean net = Boolean.FALSE;

		modelService.saveAll(cat, catalogVersion, unit, curr, adr1, adr2, cust, product);

		final Currency currItem = modelService.getSource(curr);

		OrderModel originalOrder = new OrderModel();
		originalOrder.setUser(cust);
		originalOrder.setDate(orderDate);
		originalOrder.setCurrency(curr);
		originalOrder.setNet(net);
		originalOrder.setPaymentAddress(cust.getDefaultPaymentAddress());
		originalOrder.setDeliveryAddress(cust.getDefaultShipmentAddress());

		OrderEntryModel entry1 = new OrderEntryModel();
		entry1.setOrder(originalOrder);
		entry1.setEntryNumber(Integer.valueOf(0));
		entry1.setProduct(product);
		entry1.setUnit(unit);
		entry1.setQuantity(Long.valueOf(5));

		OrderEntryModel entry2 = new OrderEntryModel();
		entry2.setOrder(originalOrder);
		entry2.setEntryNumber(Integer.valueOf(1));
		entry2.setProduct(product);
		entry2.setUnit(unit);
		entry2.setQuantity(Long.valueOf(10));

		// YTODO payment info

		modelService.saveAll(originalOrder, entry1, entry2);

		assertFalse("originalOrder should be saved", modelService.isNew(originalOrder));
		assertFalse("entry1 should be saved", modelService.isNew(entry1));
		assertFalse("entry2 should be saved", modelService.isNew(entry2));

		final Order originalOrderItem = modelService.getSource(originalOrder);
		final OrderEntry e1Item = modelService.getSource(entry1);
		final OrderEntry e2Item = modelService.getSource(entry2);

		modelService.detach(originalOrder);
		modelService.detach(entry1);
		modelService.detach(entry2);

		originalOrder = modelService.get(originalOrderItem);
		entry1 = modelService.get(e1Item);
		entry2 = modelService.get(e2Item);

		assertEquals(cust, originalOrder.getUser());
		assertEquals(curr, originalOrder.getCurrency());
		assertEquals(orderDate, originalOrder.getDate());
		assertEquals(orderDate, originalOrder.getCreationtime());
		assertEquals(net, originalOrder.getNet());

		assertNotNull(originalOrder.getPaymentAddress());
		assertNotNull(originalOrder.getDeliveryAddress());
		assertEquals(Boolean.TRUE, originalOrder.getDeliveryAddress().getDuplicate());
		assertEquals(Boolean.TRUE, originalOrder.getPaymentAddress().getDuplicate());
		assertFalse(cust.getDefaultPaymentAddress().equals(originalOrder.getPaymentAddress()));
		assertFalse(cust.getDefaultShipmentAddress().equals(originalOrder.getDeliveryAddress()));

		assertEquals("adr1 street", originalOrder.getPaymentAddress().getStreetname());
		assertEquals("adr1 street number", originalOrder.getPaymentAddress().getStreetnumber());
		assertEquals("adr1 town", originalOrder.getPaymentAddress().getTown());

		assertEquals("adr2 street", originalOrder.getDeliveryAddress().getStreetname());
		assertEquals("adr2 street number", originalOrder.getDeliveryAddress().getStreetnumber());
		assertEquals("adr2 town", originalOrder.getDeliveryAddress().getTown());

		assertEquals(Arrays.asList(entry1, entry2), originalOrder.getEntries());

		assertEquals(originalOrder, entry1.getOrder());
		assertEquals(product, entry1.getProduct());
		assertEquals(unit, entry1.getUnit());
		assertEquals(Integer.valueOf(0), entry1.getEntryNumber());
		assertEquals(Long.valueOf(5), entry1.getQuantity());

		assertEquals(originalOrder, entry2.getOrder());
		assertEquals(product, entry2.getProduct());
		assertEquals(unit, entry2.getUnit());
		assertEquals(Integer.valueOf(1), entry2.getEntryNumber());
		assertEquals(Long.valueOf(10), entry2.getQuantity());

		// YTODO taxes, discounts, global discounts

		final TestPriceFactory priceFactory = new TestPriceFactory();

		priceFactory.setBasePrice(e1Item, new PriceValue("cur", 2.55, false));
		priceFactory.setBasePrice(e2Item, new PriceValue("cur", 5.33, false));

		final PriceFactory before = jaloSession.getSessionContext().getPriceFactory();
		try
		{
			jaloSession.getSessionContext().setPriceFactory(priceFactory);
			orderService.calculateOrder(originalOrder);
		}
		finally
		{
			jaloSession.getSessionContext().setPriceFactory(before);
		}

		assertEquals(Boolean.TRUE, entry1.getCalculated());
		assertEquals(2.55, entry1.getBasePrice().doubleValue(), 0.00001);
		assertEquals(currItem.round(5 * 2.55), entry1.getTotalPrice().doubleValue(), 0.00001);

		assertEquals(Boolean.TRUE, entry2.getCalculated());
		assertEquals(5.33, entry2.getBasePrice().doubleValue(), 0.00001);
		assertEquals(currItem.round(10 * 5.33), entry2.getTotalPrice().doubleValue(), 0.00001);

		assertEquals(Boolean.TRUE, originalOrder.getCalculated());
		assertEquals(currItem.round(10 * 5.33) + currItem.round(5 * 2.55), originalOrder.getTotalPrice().doubleValue(), 0.00001);

		final OrderModel clone = orderHistoryService.createHistorySnapshot(originalOrder);

		assertNotNull(clone);

		assertNotSame(originalOrder, clone);
		assertFalse(originalOrder.equals(clone));
		assertTrue(modelService.isNew(clone));

		assertEquals(cust, clone.getUser());
		assertEquals(curr, clone.getCurrency());
		assertEquals(orderDate, clone.getDate());
		assertEquals(orderDate, clone.getCreationtime());
		assertEquals(net, clone.getNet());
		assertEquals(originalOrder, clone.getOriginalVersion());

		final String versionID = clone.getVersionID();
		assertNotNull(versionID);

		assertNotNull(clone.getPaymentAddress());
		assertNotNull(clone.getDeliveryAddress());
		assertEquals(Boolean.TRUE, clone.getDeliveryAddress().getDuplicate());
		assertEquals(Boolean.TRUE, clone.getPaymentAddress().getDuplicate());
		assertFalse(cust.getDefaultPaymentAddress().equals(clone.getPaymentAddress()));
		assertFalse(cust.getDefaultShipmentAddress().equals(originalOrder.getDeliveryAddress()));
		assertTrue(originalOrder.getPaymentAddress().equals(clone.getPaymentAddress()));
		assertTrue(originalOrder.getDeliveryAddress().equals(originalOrder.getDeliveryAddress()));

		assertEquals("adr1 street", clone.getPaymentAddress().getStreetname());
		assertEquals("adr1 street number", clone.getPaymentAddress().getStreetnumber());
		assertEquals("adr1 town", clone.getPaymentAddress().getTown());

		assertEquals("adr2 street", clone.getDeliveryAddress().getStreetname());
		assertEquals("adr2 street number", clone.getDeliveryAddress().getStreetnumber());
		assertEquals("adr2 town", clone.getDeliveryAddress().getTown());


		List<OrderEntryModel> entries = (List) clone.getEntries();
		assertEquals(2, entries.size());

		final OrderEntryModel e1Clone = entries.get(0);

		assertNotSame(entry1, e1Clone);
		assertFalse(entry1.equals(e1Clone));
		assertTrue(modelService.isNew(e1Clone));

		assertEquals(clone, e1Clone.getOrder());
		assertEquals(product, e1Clone.getProduct());
		assertEquals(unit, e1Clone.getUnit());
		assertEquals(Integer.valueOf(0), e1Clone.getEntryNumber());
		assertEquals(Long.valueOf(5), e1Clone.getQuantity());

		final OrderEntryModel e2Clone = entries.get(1);

		assertNotSame(entry2, e2Clone);
		assertFalse(entry2.equals(e2Clone));
		assertTrue(modelService.isNew(e2Clone));

		assertEquals(clone, e2Clone.getOrder());
		assertEquals(product, e2Clone.getProduct());
		assertEquals(unit, e2Clone.getUnit());
		assertEquals(Integer.valueOf(1), e2Clone.getEntryNumber());
		assertEquals(Long.valueOf(10), e2Clone.getQuantity());

		assertEquals(Boolean.TRUE, e1Clone.getCalculated());
		assertEquals(2.55, e1Clone.getBasePrice().doubleValue(), 0.00001);
		assertEquals(currItem.round(5 * 2.55), e1Clone.getTotalPrice().doubleValue(), 0.00001);

		assertEquals(Boolean.TRUE, e2Clone.getCalculated());
		assertEquals(5.33, e2Clone.getBasePrice().doubleValue(), 0.00001);
		assertEquals(currItem.round(10 * 5.33), e2Clone.getTotalPrice().doubleValue(), 0.00001);

		assertEquals(Boolean.TRUE, clone.getCalculated());
		assertEquals(currItem.round(10 * 5.33) + currItem.round(5 * 2.55), clone.getTotalPrice().doubleValue(), 0.00001);

		orderHistoryService.saveHistorySnapshot(clone);

		assertFalse(modelService.isNew(clone));
		assertFalse(modelService.isNew(e1Clone));
		assertFalse(modelService.isNew(e2Clone));

		assertEquals(cust, clone.getUser());
		assertEquals(curr, clone.getCurrency());
		assertEquals(orderDate, clone.getDate());
		assertEquals(orderDate, clone.getCreationtime());
		assertEquals(net, clone.getNet());
		assertEquals(versionID, clone.getVersionID());

		assertNotNull(clone.getPaymentAddress());
		assertNotNull(clone.getDeliveryAddress());
		assertEquals(Boolean.TRUE, clone.getDeliveryAddress().getDuplicate());
		assertEquals(Boolean.TRUE, clone.getPaymentAddress().getDuplicate());
		assertFalse(cust.getDefaultPaymentAddress().equals(clone.getPaymentAddress()));
		assertFalse(cust.getDefaultShipmentAddress().equals(originalOrder.getDeliveryAddress()));
		// now they should have been cloned by jalo layer !!!
		assertFalse(originalOrder.getPaymentAddress().equals(clone.getPaymentAddress()));
		if (originalOrder.getDeliveryAddress() != null)
		{
			assertFalse(originalOrder.getDeliveryAddress().equals(clone.getDeliveryAddress()));
		}

		assertEquals("adr1 street", clone.getPaymentAddress().getStreetname());
		assertEquals("adr1 street number", clone.getPaymentAddress().getStreetnumber());
		assertEquals("adr1 town", clone.getPaymentAddress().getTown());

		assertEquals("adr2 street", clone.getDeliveryAddress().getStreetname());
		assertEquals("adr2 street number", clone.getDeliveryAddress().getStreetnumber());
		assertEquals("adr2 town", clone.getDeliveryAddress().getTown());


		entries = (List) clone.getEntries();
		assertEquals(2, entries.size());
		assertEquals(Arrays.asList(e1Clone, e2Clone), entries);

		assertNotSame(entry1, e1Clone);
		assertFalse(entry1.equals(e1Clone));
		assertFalse(modelService.isNew(e1Clone));
		assertEquals(originalOrder, clone.getOriginalVersion());

		assertEquals(clone, e1Clone.getOrder());
		assertEquals(product, e1Clone.getProduct());
		assertEquals(unit, e1Clone.getUnit());
		assertEquals(Integer.valueOf(0), e1Clone.getEntryNumber());
		assertEquals(Long.valueOf(5), e1Clone.getQuantity());

		assertNotSame(entry2, e2Clone);
		assertFalse(entry2.equals(e2Clone));
		assertFalse(modelService.isNew(e2Clone));

		assertEquals(clone, e2Clone.getOrder());
		assertEquals(product, e2Clone.getProduct());
		assertEquals(unit, e2Clone.getUnit());
		assertEquals(Integer.valueOf(1), e2Clone.getEntryNumber());
		assertEquals(Long.valueOf(10), e2Clone.getQuantity());

		assertEquals(Boolean.TRUE, e1Clone.getCalculated());
		assertEquals(2.55, e1Clone.getBasePrice().doubleValue(), 0.00001);
		assertEquals(currItem.round(5 * 2.55), e1Clone.getTotalPrice().doubleValue(), 0.00001);

		assertEquals(Boolean.TRUE, e2Clone.getCalculated());
		assertEquals(5.33, e2Clone.getBasePrice().doubleValue(), 0.00001);
		assertEquals(currItem.round(10 * 5.33), e2Clone.getTotalPrice().doubleValue(), 0.00001);

		assertEquals(Boolean.TRUE, clone.getCalculated());
		assertEquals(currItem.round(10 * 5.33) + currItem.round(5 * 2.55), clone.getTotalPrice().doubleValue(), 0.00001);

	}

	//tools

	protected OrderHistoryEntryModel createEntry(final OrderModel order, final boolean save)
	{
		return createEntry(order, save, "OK");
	}

	protected OrderHistoryEntryModel createEntry(final OrderModel order, final boolean save, final String result)
	{
		final OrderHistoryEntryModel historyModel = modelService.create(OrderHistoryEntryModel.class);
		historyModel.setTimestamp(new Date());
		historyModel.setOrder(order);
		historyModel.setDescription(new TestAction(result).execute());

		if (save)
		{
			modelService.save(historyModel);
		}
		return historyModel;
	}




}
