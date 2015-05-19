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
package de.hybris.platform.stock;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.basecommerce.enums.StockLevelUpdateType;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.stock.impl.DefaultStockLevelDao;
import de.hybris.platform.stock.impl.StockLevelDao;
import de.hybris.platform.stock.model.StockLevelHistoryEntryModel;
import de.hybris.platform.stock.strategy.StockLevelProductStrategy;
import de.hybris.platform.stock.strategy.impl.DefaultProductAvailabilityStrategy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Test for {@link StockService}
 */
public class StockServiceTest extends ServicelayerTest
{

	private static final Logger LOG = Logger.getLogger(StockServiceTest.class.getName());

	private final String productCode1 = "testProduct1";
	private final String productCode2 = "testProduct2";
	private final String productCode3 = "testProduct3";

	private final String wareHouse1 = "warehouse1";
	private final String wareHouse2 = "warehouse2";
	private final String wareHouse3 = "warehouse3";

	@Resource
	private ModelService modelService;
	@Resource
	private StockService stockService;
	@Resource
	private ProductService productService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private I18NService i18nService;
	@Resource(name = "defaultStockLevelProductID")
	private StockLevelProductStrategy stockLevelProductStrategy;
	@Resource
	private StockLevelDao stockLevelDao;
	@Resource
	private WarehouseService warehouseService;

	private StockLevelModel stockLevel1;
	private StockLevelModel stockLevel2;

	private WarehouseModel warehouse1;
	private WarehouseModel warehouse2;
	private WarehouseModel warehouse3;

	private ProductModel product1;
	private ProductModel product2;
	private ProductModel product3;

	/**
	 * Setup of the test:
	 * <ul>
	 * <li>retrieves 3 products,</li>
	 * <li>retrieves 3 warehouses.</li>
	 * </ul>
	 */
	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createDefaultCatalog();

		importCsv("/testsrc/stockServiceTestData.csv", "windows-1252");

		product1 = productService.getProductForCode(productCode1);
		product2 = productService.getProductForCode(productCode2);
		product3 = productService.getProductForCode(productCode3);

		warehouse1 = warehouseService.getWarehouseForCode(wareHouse1);
		warehouse2 = warehouseService.getWarehouseForCode(wareHouse2);
		warehouse3 = warehouseService.getWarehouseForCode(wareHouse3);
	}

	/**
	 * Tests the creation of stock levels.
	 * <ul>
	 * <li>creates stockLlevel1,</li>
	 * <li>name, warehouse, availability, and history size check,</li>
	 * <li>creates stockLevel2,</li>
	 * <li>name, warehouse, availability, and history size check.</li>
	 * </ul>
	 */
	@Test
	public void testCreateStockLevel()
	{
		stockLevel1 = this.createStockLevel(product1, warehouse1, 10);
		assertNotNull(stockLevel1);
		assertEquals(productCode1, stockLevel1.getProductCode());
		assertEquals(warehouse1, stockLevel1.getWarehouse());
		assertEquals(10, stockLevel1.getAvailable());
		assertEquals(1, stockLevel1.getStockLevelHistoryEntries().size());
		stockLevel2 = this.createStockLevel(product2, warehouse2, 20);
		assertNotNull(stockLevel2);
		assertEquals(productCode2, stockLevel2.getProductCode());
		assertEquals(warehouse2, stockLevel2.getWarehouse());
		assertEquals(20, stockLevel2.getAvailable());
		assertEquals(1, stockLevel2.getStockLevelHistoryEntries().size());
	}

	/**
	 * Tests to create the same product in the same warehouse twice. The first time succeeds, and the second time throws
	 * a RuntimeException.
	 */
	@Test(expected = RuntimeException.class)
	public void testCreateSameStockLevel()
	{
		stockLevel1 = this.createStockLevel(product1, warehouse1, 10);
		assertNotNull(stockLevel1);
		stockLevel1 = this.createStockLevel(product1, warehouse1, 10);
	}

	/**
	 * Tests the search for all stock levels and the total actual amount.
	 * <ul>
	 * <li>creates 3 stock levels: productCode1 in both warehouse1 and warehouse2, and productCode2 in warehouse2 only,</li>
	 * <li>sets the first stock level as FORCE_IN_STOCK,</li>
	 * <li>checks the result of the search for productCode1,</li>
	 * <li>checks the result of the search for productCode1 in 3 specified warehouses,</li>
	 * <li>creates another 2 stock levels: productCode1 and productCode3 in warehouse3,</li>
	 * <li>checks the result of the search for productCode1 in 3 specified warehouses,</li>
	 * <li>reserves productCode1, and test the negative stock level.</li>
	 * </ul>
	 */
	@Test
	public void testTotalActualAmount() throws BusinessException
	{
		stockLevel1 = this.createStockLevel(product1, warehouse1, 10);
		this.createStockLevel(product1, warehouse2, 15);
		this.createStockLevel(product2, warehouse2, 20);
		int totalActualAmount = stockService.getTotalStockLevelAmount(product1);
		assertEquals(25, totalActualAmount);

		final List<WarehouseModel> warehouses = new ArrayList<WarehouseModel>();
		warehouses.add(warehouse1);
		stockService.setInStockStatus(product1, warehouses, InStockStatus.FORCEINSTOCK);
		warehouses.add(warehouse2);
		warehouses.add(warehouse3);
		totalActualAmount = stockService.getTotalStockLevelAmount(product1, warehouses);
		assertEquals(25, totalActualAmount);

		this.createStockLevel(product1, warehouse3, 30);
		this.createStockLevel(product3, warehouse3, 30);
		totalActualAmount = stockService.getTotalStockLevelAmount(product1, warehouses);
		assertEquals(55, totalActualAmount);

		try
		{
			stockService.reserve(product1, warehouse1, 20, "all of the products");
		}
		catch (final InsufficientStockLevelException e)
		{
			fail("FORCE_IN_STOCK, so the reservation must be successful");
		}
		this.modelService.refresh(stockLevel1);
		totalActualAmount = stockService.getTotalStockLevelAmount(product1, warehouses);
		assertEquals(45, totalActualAmount);
		stockLevel1.setTreatNegativeAsZero(false);
		totalActualAmount = stockService.getTotalStockLevelAmount(product1, warehouses);
		assertEquals(35, totalActualAmount);
	}

	/**
	 * Tests the enabling and disabling of in stock function.
	 * <ul>
	 * <li>sets the status for a non-existing stock level, and there should be no exceptions,</li>
	 * <li>creates the first stock level and enables in stock, and tests it,</li>
	 * <li>creates another stock level and enables in stock, and tests it,</li>
	 * <li>disables both stock levels, and test them.</li>
	 * </ul>
	 */
	@Test
	public void testInStockStatus()
	{
		final List<WarehouseModel> warehouses = new ArrayList<WarehouseModel>();
		warehouses.add(warehouse1);
		stockService.setInStockStatus(product1, warehouses, InStockStatus.FORCEINSTOCK);

		stockLevel1 = this.createStockLevel(product1, warehouse1, 10);
		assertEquals(InStockStatus.NOTSPECIFIED, stockService.getInStockStatus(product1, warehouse1));
		stockService.setInStockStatus(product1, warehouses, InStockStatus.FORCEINSTOCK);
		assertEquals(InStockStatus.FORCEINSTOCK, stockService.getInStockStatus(product1, warehouse1));

		stockLevel2 = this.createStockLevel(product1, warehouse2, 15);
		assertEquals(InStockStatus.NOTSPECIFIED, stockService.getInStockStatus(product1, warehouse2));
		warehouses.add(warehouse2);
		stockService.setInStockStatus(product1, warehouses, InStockStatus.FORCEINSTOCK);
		assertEquals(InStockStatus.FORCEINSTOCK, stockService.getInStockStatus(product1, warehouse1));

		stockService.setInStockStatus(product1, warehouses, InStockStatus.NOTSPECIFIED);
		assertEquals(InStockStatus.NOTSPECIFIED, stockService.getInStockStatus(product1, warehouse1));
		assertEquals(InStockStatus.NOTSPECIFIED, stockService.getInStockStatus(product1, warehouse2));
	}

	/**
	 * Tests the enabling and disabling of out of stock function.
	 * <ul>
	 * <li>sets the status for a non-existing stock level, and there should be no exceptions,</li>
	 * <li>creates the first stock level and enables out of stock, and tests it,</li>
	 * <li>creates another stock level and enables out of stock, and tests it,</li>
	 * <li>disables both stock levels, and tests them</li>
	 * </ul>
	 */
	@Test
	public void testOutOfStock()
	{
		final List<WarehouseModel> warehouses = new ArrayList<WarehouseModel>();
		warehouses.add(warehouse1);
		stockService.setInStockStatus(product1, warehouses, InStockStatus.FORCEOUTOFSTOCK);

		stockLevel1 = this.createStockLevel(product1, warehouse1, 10);
		assertEquals(InStockStatus.NOTSPECIFIED, stockService.getInStockStatus(product1, warehouse1));
		stockService.setInStockStatus(product1, warehouses, InStockStatus.FORCEOUTOFSTOCK);
		assertEquals(InStockStatus.FORCEOUTOFSTOCK, stockService.getInStockStatus(product1, warehouse1));

		stockLevel2 = this.createStockLevel(product1, warehouse2, 15);
		assertEquals(InStockStatus.NOTSPECIFIED, stockService.getInStockStatus(product1, warehouse2));
		warehouses.add(warehouse2);
		stockService.setInStockStatus(product1, warehouses, InStockStatus.FORCEOUTOFSTOCK);
		assertEquals(InStockStatus.FORCEOUTOFSTOCK, stockService.getInStockStatus(product1, warehouse2));

		stockService.setInStockStatus(product1, warehouses, InStockStatus.NOTSPECIFIED);
		assertEquals(InStockStatus.NOTSPECIFIED, stockService.getInStockStatus(product1, warehouse2));
		assertEquals(InStockStatus.NOTSPECIFIED, stockService.getInStockStatus(product1, warehouse2));
	}

	/**
	 * Tests stock level status.
	 * <ul>
	 * <li>creates a stock level, and tests the in stock status,</li>
	 * <li>reserves the product, and tests the in stock status.</li>
	 * </ul>
	 */
	@Test
	public void testProductStockStatus() throws BusinessException
	{
		stockLevel1 = this.createStockLevel(product1, warehouse1, 10);
		assertEquals(StockLevelStatus.INSTOCK, stockService.getProductStatus(product1, warehouse1));

		final List<WarehouseModel> warehouses = new ArrayList<WarehouseModel>();
		warehouses.add(warehouse2);
		assertEquals(StockLevelStatus.OUTOFSTOCK, stockService.getProductStatus(product1, warehouses));
		warehouses.add(warehouse3);
		assertEquals(StockLevelStatus.OUTOFSTOCK, stockService.getProductStatus(product1, warehouses));
		warehouses.add(warehouse1);
		assertEquals(StockLevelStatus.INSTOCK, stockService.getProductStatus(product1, warehouses));

		try
		{
			stockService.reserve(product1, warehouse1, 10, "all of the products");
		}
		catch (final InsufficientStockLevelException e)
		{
			fail("10 pieces must be able to be reserved");
		}
		this.modelService.refresh(stockLevel1);
		assertEquals(StockLevelStatus.OUTOFSTOCK, stockService.getProductStatus(product1, warehouses));
	}

	/**
	 * Tests actual stock level update.
	 * <ul>
	 * <li>creates a stock level, and updates the actual stock level with multi-threads,</li>
	 * <li>thread pool: 50, and rounds: 150,</li>
	 * <li>tests the stock level history entry size</li>
	 * </ul>
	 */
	@Test
	public void testUpdateActualStockLevel() throws Exception
	{
		stockLevel1 = this.createStockLevel(product1, warehouse1, 10);
		stockLevel1.setMaxStockLevelHistoryCount(-1);
		modelService.save(stockLevel1);
		System.out.println("###########################################   TEST ###########################################");
		System.out.println("###########################################   MAX HISTORY: "
				+ stockLevel1.getMaxStockLevelHistoryCount() + "  ###########################################");
		System.out.println("###########################################   TEST ###########################################");
		assertEquals(10, stockLevel1.getAvailable());

		final int maxThreads = 50;
		final int waitingMinutes = 10;
		final int rounds = maxThreads * 3;
		doMultiThreads(product1, warehouse1, maxThreads, rounds, waitingMinutes, MultiThreadsAction.UPDATE_ACTUAL_STOCKLEVEL);

		//make sure that all StockLevelHistoryEntries are created
		Thread.sleep(rounds * 3);
		modelService.refresh(stockLevel1);
		assertEquals(rounds + 1, stockLevel1.getStockLevelHistoryEntries().size());
	}

	private enum MultiThreadsAction
	{
		UPDATE_ACTUAL_STOCKLEVEL, RESERVE;
	}

	/**
	 * Helper class for multi-threads test.
	 */
	private class StockLevelMultiThreadsRunnable implements Runnable
	{
		final WarehouseModel house;
		final ProductModel product;
		final int amount;
		final MultiThreadsAction action;

		final Tenant threadTenant;

		StockLevelMultiThreadsRunnable(final ProductModel model, final WarehouseModel warehouse, final int amount,
				final MultiThreadsAction action)
		{
			this.product = model;
			this.house = warehouse;
			this.amount = amount;
			this.threadTenant = Registry.getCurrentTenant();
			this.action = action;
		}

		/**
		 * Multi-thread actions running...
		 */
		@Override
		public void run()
		{
			try
			{
				prepareThread();

				if (MultiThreadsAction.RESERVE == this.action)
				{
					stockService.reserve(product, house, amount, "");
				}
				else if (MultiThreadsAction.UPDATE_ACTUAL_STOCKLEVEL == this.action)
				{
					stockService.updateActualStockLevel(product, house, amount, "");
				}
			}
			catch (final Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
			finally
			{
				unprepareThread();
			}
		}

		protected void prepareThread()
		{
			Registry.setCurrentTenant(threadTenant);
		}

		protected void unprepareThread()
		{
			JaloSession.deactivate();
			Registry.unsetCurrentTenant();
		}

	}

	/**
	 * Test update actual stock level with negative amount. Should display warn in console.
	 */
	@Test
	public void testUpdateActualStockLevelNegativeAmount()
	{
		stockLevel1 = this.createStockLevel(product1, warehouse1, 10);
		stockService.updateActualStockLevel(product1, warehouse1, -1, "");
		assertEquals(0, stockLevel1.getAvailable());
	}

	/**
	 * Test update actual stock level with zero amount. Should not display any text in the console.
	 */
	@Test
	public void testUpdateActualStockLevelZeroAmount()
	{
		stockLevel1 = this.createStockLevel(product1, warehouse1, 10);
		stockService.updateActualStockLevel(product1, warehouse1, 0, "");
		assertEquals(0, stockLevel1.getAvailable());
	}

	/**
	 * Tests release of stock level.
	 */
	@Test
	public void testRelease() throws BusinessException
	{
		stockLevel1 = this.createStockLevel(product1, warehouse1, 10);
		stockService.release(product1, warehouse1, 5, "release");
		this.modelService.refresh(stockLevel1);
		assertEquals(15, stockService.getStockLevelAmount(product1, warehouse1));
	}

	/**
	 * Tests stock level update of multi-reservations.
	 * <ul>
	 * <li>creates a stock level, and reserves the corresponding product with multi-threads,</li>
	 * <li>thread pool: 200, and rounds: 600,</li>
	 * <li>tests the total stock level history entry size,</li>
	 * <li>tests reserved value of the last stock level history entry.</li>
	 * </ul>
	 */
	@Test
	public void testMultiReservations() throws Exception
	{
		final int totalAmount = 50000;
		stockLevel1 = this.createStockLevel(product1, warehouse1, totalAmount);
		stockLevel1.setMaxStockLevelHistoryCount(-1);
		modelService.save(stockLevel1);
		assertEquals(totalAmount, stockLevel1.getAvailable());

		final int maxThreads = 100;
		final int waitingMinutes = 15;
		final int rounds = maxThreads * 50;
		doMultiThreads(product1, warehouse1, maxThreads, rounds, waitingMinutes, MultiThreadsAction.RESERVE);

		modelService.refresh(stockLevel1);

		//half of reservations with one piece, and another half with 2 pieces
		final int totalReserved = (rounds / 2) + rounds;
		assertEquals(totalReserved, stockLevel1.getReserved());
		assertEquals(rounds + 1, stockLevel1.getStockLevelHistoryEntries().size());
	}

	/**
	 * Multi-threads test.
	 */
	private void doMultiThreads(final ProductModel product, final WarehouseModel warehouse, final int maxThreads,
			final int rounds, final int waitingMinutes, final MultiThreadsAction action) throws InterruptedException
	{
		final ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
		for (int i = 1; i <= rounds; i++)
		{
			if (MultiThreadsAction.RESERVE == action)
			{
				final Runnable reserveOperation = new StockLevelMultiThreadsRunnable(product, warehouse, (i % 2) + 1,
						MultiThreadsAction.RESERVE);
				executor.execute(reserveOperation);
			}
			else if (MultiThreadsAction.UPDATE_ACTUAL_STOCKLEVEL == action)
			{
				final Runnable updateOperation = new StockLevelMultiThreadsRunnable(product, warehouse, i * 10,
						MultiThreadsAction.UPDATE_ACTUAL_STOCKLEVEL);
				executor.execute(updateOperation);
			}
		}

		executor.shutdown();
		// wait several minutes, all task threads should be finished
		executor.awaitTermination(waitingMinutes, TimeUnit.MINUTES);
		assertTrue("it should not take more than " + waitingMinutes + " minutes to finish this test.", executor.isTerminated());

		LOG.info("all threads finished.");
	}

	/**
	 * Tests an unsuccessful reservation and a successful one in sequence.
	 * <ul>
	 * <li>creates a stock level with 100 pieces in stock,</li>
	 * <li>tries to reserve 200 pieces, must be unsuccessful,</li>
	 * <li>tries to reserve 10 pieces, must be successful this time.</li>
	 * </ul>
	 */
	@Test
	public void testUnsuccessfulReservations() throws BusinessException
	{
		stockLevel1 = this.createStockLevel(product1, warehouse1, 100);
		stockLevel1.setMaxStockLevelHistoryCount(-1);
		modelService.save(stockLevel1);
		try
		{
			stockService.reserve(product1, warehouse1, 200, "");
			fail("cannot allow reservation with 200 pieces.");
		}
		catch (final InsufficientStockLevelException e)
		{
			//expected
		}
		assertEquals(1, stockLevel1.getStockLevelHistoryEntries().size());
		try
		{
			stockService.reserve(product1, warehouse1, 10, "");
		}
		catch (final InsufficientStockLevelException e)
		{
			fail("must allow reservation with 10 pieces.");
		}
		modelService.refresh(stockLevel1);
		assertEquals(2, stockLevel1.getStockLevelHistoryEntries().size());
	}

	@Test
	public void testSuccessfulReservations() throws BusinessException
	{
		stockLevel1 = this.createStockLevel(product1, warehouse1, 100);

		stockService.reserve(product1, warehouse1, 50, "");
		assertEquals(50, stockLevel1.getReserved());

		stockService.reserve(product1, warehouse1, 10, "");
		assertEquals(60, stockLevel1.getReserved());

		stockService.reserve(product1, warehouse1, 11, "");
		assertEquals(71, stockLevel1.getReserved());

		stockService.reserve(product1, warehouse1, 29, "");
		assertEquals(100, stockLevel1.getReserved());
	}

	/**
	 * <ul>
	 * <li>Test of product availability by invoking default strategy for calculating product availability, passing
	 * product, quantity and warehouse/store as parameters,</li>
	 * <li>Test of availability date by invoking default strategy for calculating product availability, passing product,
	 * quantity and warehouse/store as parameters (TODO),</li>
	 * <li>Test of "bestOf" implementation</li>
	 * </ul>
	 */
	@Test
	public void testProductAvailability()
	{
		// Setup
		final int quantity1 = 10;
		final int quantity2 = 5;
		final StockLevelModel stockLevel1 = this.createStockLevel(product1, warehouse1, quantity1);
		final Date nextDeliveryDate = getDate(2010, 10, 10);
		stockLevel1.setNextDeliveryTime(nextDeliveryDate);
		stockLevel1.setMaxPreOrder(100);
		final StockLevelModel stockLevel2 = this.createStockLevel(product1, warehouse2, quantity2);
		stockLevel2.setMaxPreOrder(0);

		modelService.saveAll();

		final List<WarehouseModel> warehouses = new ArrayList<WarehouseModel>();
		warehouses.add(warehouse1);
		warehouses.add(warehouse2);

		final String availability = stockService.getAvailability(product1, warehouses, null, null);

		LOG.info("Template based availability report:");
		System.out.println(availability);

		// 1. Test of product availability by invoking default strategy for calculating product availability, passing product, quantity and warehouse/store as parameters
		assertTrue(availability.contains(warehouse1.getCode()) && //
				availability.contains(warehouse2.getCode()) && //
				availability.contains(productCode1) && //
				availability.contains(String.valueOf(quantity1)) && //
				availability.contains(String.valueOf(quantity2)));

		// 2. Test of availability date by invoking default strategy for calculating product availability, passing product, quantity and warehouse/store as parameters
		final DefaultProductAvailabilityStrategy pas = new DefaultProductAvailabilityStrategy();
		pas.setI18nService(i18nService);
		final DefaultStockLevelDao dao = new DefaultStockLevelDao();
		dao.setFlexibleSearchService(flexibleSearchService);
		pas.setStockLevelDao(dao);
		final Map<WarehouseModel, Date> availability1 = pas.getAvailability(productCode1, warehouses, 99);

		final Collection<StockLevelModel> stockLevels = dao.findAllStockLevels(productCode1);
		for (final StockLevelModel stockLevel : stockLevels)
		{
			LOG.info(">> StockLevel: " + stockLevel);
			LOG.info("getProductCode" + stockLevel.getProductCode());
			LOG.info("getWarehouse: " + stockLevel.getWarehouse());
			LOG.info("getNextDeliveryTime: " + stockLevel.getNextDeliveryTime());
			LOG.info("getMaxPreOrder" + stockLevel.getMaxPreOrder());
			LOG.info("getAvailable: " + stockLevel.getAvailable());
		}

		LOG.info("AVAILABILITY: " + availability1.size());
		for (final Iterator it = availability1.entrySet().iterator(); it.hasNext();)
		{
			final Map.Entry<WarehouseModel, Date> entry = (Entry<WarehouseModel, Date>) it.next();
			LOG.info("WarehouseModel: " + entry.getKey());
			LOG.info("Date: " + entry.getKey());
		}
		final Map.Entry<WarehouseModel, Date> entry = availability1.entrySet().iterator().next();

		assertEquals(warehouse1, entry.getKey());
		assertEquals(nextDeliveryDate, entry.getValue());

		// 3a. bestOf quantity
		final Map<WarehouseModel, Integer> availability2 = pas.getAvailability(productCode1, warehouses, new Date());

		assertTrue("Wrong warehouse selected!", warehouse1 == stockService.getBestMatchOfQuantity(availability2));

		// 3b. bestOf availability date
		assertTrue("Wrong warehouse selected!", warehouse1 == stockService.getBestMatchOfAvailability(availability1));
	}

	private Date getDate(final int year, final int month, final int day)
	{
		final Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		return cal.getTime();
	}

	private StockLevelModel createStockLevel(final ProductModel product, final WarehouseModel warehouse, final int available)
	{
		final StockLevelModel stockLevel = this.createStockLevel(product, warehouse, available, 0, 0, InStockStatus.NOTSPECIFIED,
				-1, true);
		return stockLevel;
	}

	private StockLevelModel createStockLevel(final ProductModel product, final WarehouseModel warehouse, final int available,
			final int overSelling, final int reserved, final InStockStatus status, final int maxStockLevelHistoryCount,
			final boolean treatNegativeAsZero)
	{
		if (available < 0)
		{
			throw new IllegalArgumentException("available amount cannot be negative.");
		}
		if (overSelling < 0)
		{
			throw new IllegalArgumentException("overSelling amount cannot be negative.");
		}

		StockLevelModel stockLevel = this.stockLevelDao.findStockLevel(stockLevelProductStrategy.convert(product), warehouse);
		if (stockLevel != null)
		{
			throw new JaloSystemException("product [" + product + "] in warehouse [" + warehouse.getName()
					+ "] already exists. the same product cannot be created in the same warehouse again.");
		}

		//create the new stock level
		stockLevel = modelService.create(StockLevelModel.class);
		stockLevel.setProductCode(stockLevelProductStrategy.convert(product));
		stockLevel.setProduct(product);
		stockLevel.setWarehouse(warehouse);
		stockLevel.setAvailable(available);
		stockLevel.setOverSelling(overSelling);
		stockLevel.setReserved(reserved);
		stockLevel.setInStockStatus(status);
		stockLevel.setMaxStockLevelHistoryCount(maxStockLevelHistoryCount);
		stockLevel.setTreatNegativeAsZero(treatNegativeAsZero);
		//create the first history entry
		if (maxStockLevelHistoryCount != 0)
		{
			final List<StockLevelHistoryEntryModel> historyEntries = new ArrayList<StockLevelHistoryEntryModel>();
			final StockLevelHistoryEntryModel entry = this.createStockLevelHistoryEntry(stockLevel, available, 0,
					StockLevelUpdateType.WAREHOUSE, "new in stock");
			historyEntries.add(entry);
			stockLevel.setStockLevelHistoryEntries(historyEntries);
		}
		modelService.save(stockLevel);
		return stockLevel;
	}

	private StockLevelHistoryEntryModel createStockLevelHistoryEntry(final StockLevelModel stockLevel, final int actual,
			final int reserved, final StockLevelUpdateType updateType, final String comment)
	{
		if (stockLevel == null)
		{
			throw new IllegalArgumentException("stock level cannot be null.");
		}
		if (actual < 0)
		{
			throw new IllegalArgumentException("actual amount cannot be negative.");
		}

		if (stockLevel.getMaxStockLevelHistoryCount() != 0
				&& (stockLevel.getMaxStockLevelHistoryCount() < 0 || stockLevel.getMaxStockLevelHistoryCount() > (stockLevel
						.getStockLevelHistoryEntries() == null ? 0 : stockLevel.getStockLevelHistoryEntries().size())))
		{
			final StockLevelHistoryEntryModel historyEntry = modelService.create(StockLevelHistoryEntryModel.class);
			historyEntry.setStockLevel(stockLevel);
			historyEntry.setActual(actual);
			historyEntry.setReserved(reserved);
			historyEntry.setUpdateType(updateType);
			if (comment != null)
			{
				historyEntry.setComment(comment);
			}
			historyEntry.setUpdateDate(new Date());
			modelService.save(historyEntry);
			return historyEntry;
		}
		return null;
	}

	/**
	 * Tests create stock level with no history created.
	 */
	@Test
	public void testCreateNoHistoryCreated() throws Exception
	{
		stockLevel1 = this.createStockLevel(product1, warehouse1, 10, 0, 0, InStockStatus.NOTSPECIFIED, 0, true);
		assertEquals(10, stockLevel1.getAvailable());

		this.modelService.refresh(stockLevel1);
		assertEquals(0, stockLevel1.getStockLevelHistoryEntries().size());
	}

	/**
	 * Tests release of stock level with no history created. Defaults to 0
	 */
	@Test
	public void testReleaseNoHistoryCreated() throws BusinessException
	{
		stockLevel1 = this.createStockLevel(product1, warehouse1, 10, 0, 0, InStockStatus.NOTSPECIFIED, 0, true);
		stockService.release(product1, warehouse1, 5, "release");
		this.modelService.refresh(stockLevel1);

		assertEquals(0, stockLevel1.getStockLevelHistoryEntries().size());
	}

	/**
	 * Tests reserve stock level with no history created. Defaults to 0
	 */
	@Test
	public void testReserveNoHistoryCreated() throws Exception
	{
		stockLevel1 = this.createStockLevel(product1, warehouse1, 10, 0, 0, InStockStatus.NOTSPECIFIED, 0, true);

		assertEquals(10, stockLevel1.getAvailable());

		stockService.reserve(product1, warehouse1, 1, "reserve");
		this.modelService.refresh(stockLevel1);

		assertEquals(0, stockLevel1.getStockLevelHistoryEntries().size());
	}

}
