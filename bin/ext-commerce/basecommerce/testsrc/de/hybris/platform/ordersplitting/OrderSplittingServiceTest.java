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
package de.hybris.platform.ordersplitting;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;



public class OrderSplittingServiceTest extends ServicelayerTransactionalTest
{

	private final static Logger LOG = Logger.getLogger(OrderSplittingServiceTest.class);

	//BCOM-156 - test the default set up of the service, not one with customized splitting strategies
	@Resource(name = "defaultOrderSplittingService")
	private OrderSplittingService orderSplittingService;
	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;
	@Resource
	private ProductService productService;

	private OrderModel order1;

	private DeliveryModeModel deliveryMode1;
	private DeliveryModeModel deliveryMode2;

	private Date namedDeliveryDate1;
	private Date namedDeliveryDate2;


	private VendorModel vendor;

	private ProductModel product;

	@Override
	@Before
	public void init()
	{
		super.init();
		try
		{
			createCoreData();
			createDefaultCatalog();
			createHardwareCatalog();
			createDefaultUsers();

			final WarehouseModel warehouse = modelService.create(WarehouseModel.class);
			warehouse.setDefault(Boolean.TRUE);
			warehouse.setCode("w1");
			vendor = modelService.create(VendorModel.class);
			vendor.setCode("v1");
			warehouse.setVendor(vendor);

			modelService.save(warehouse);

			order1 = modelService.create(OrderModel.class);
			final CurrencyModel currency = new CurrencyModel();
			currency.setIsocode("EUR1");
			currency.setSymbol("EUR1");
			currency.setBase(Boolean.TRUE);
			currency.setActive(Boolean.TRUE);
			currency.setConversion(Double.valueOf(1));
			modelService.save(currency);
			order1.setCurrency(currency);
			order1.setNet(Boolean.TRUE);
			order1.setDate(new Date());
			order1.setUser(userService.getUser("ariel"));

			deliveryMode1 = modelService.create(DeliveryModeModel.class);
			deliveryMode1.setCode("deliveryMode1");

			deliveryMode2 = modelService.create(DeliveryModeModel.class);
			deliveryMode2.setCode("deliveryMode2");

			namedDeliveryDate1 = new Date();
			namedDeliveryDate2 = new Date(new Date().getTime() + 1000);//shift in time

			final CatalogModel catalog = new CatalogModel();
			catalog.setId("catalog");

			final CatalogVersionModel catalogVersion = new CatalogVersionModel();
			catalogVersion.setCatalog(catalog);
			catalogVersion.setVersion("version");

			product = new ProductModel();
			product.setCode("Product1");
			product.setCatalogVersion(catalogVersion);

			modelService.save(product);

		}
		catch (final Exception e)
		{
			throw new RuntimeException(e); //NOPMD
		}
	}

	@Test
	public void testOrderDeliveryModeSplitting() throws Exception
	{
		LOG.debug("Class under test : " + orderSplittingService.getClass().getName());
		final List<AbstractOrderEntryModel> orderEntryList = new ArrayList<AbstractOrderEntryModel>();

		orderEntryList.add(getNewOrderEntryModel(deliveryMode1, order1, null));
		orderEntryList.add(getNewOrderEntryModel(deliveryMode1, order1, null));

		orderEntryList.add(getNewOrderEntryModel(deliveryMode2, order1, null));
		orderEntryList.add(getNewOrderEntryModel(deliveryMode2, order1, null));

		logTestEntries(orderEntryList);

		final List<ConsignmentModel> consignmentModelList = orderSplittingService.splitOrderForConsignmentNotPersist(order1,
				orderEntryList);

		assertEquals("Split should return 2 consignment", 2, consignmentModelList.size());
		for (final ConsignmentModel consignment : consignmentModelList)
		{
			assertEquals("Split should return 2 consignmentEntries for consignment " + consignmentModelList.indexOf(consignment), 2,
					consignment.getConsignmentEntries().size());
		}
	}



	@Test
	public void testOrderAndOrderDeliveryModeAndNamedDeliveryDateSplitting() throws Exception
	{
		LOG.debug("Class under test : " + orderSplittingService.getClass().getName());
		final List<AbstractOrderEntryModel> orderEntryList = new ArrayList<AbstractOrderEntryModel>();

		orderEntryList.add(getNewOrderEntryModel(deliveryMode1, order1, namedDeliveryDate1));
		orderEntryList.add(getNewOrderEntryModel(deliveryMode1, order1, namedDeliveryDate1));

		orderEntryList.add(getNewOrderEntryModel(deliveryMode2, order1, namedDeliveryDate1));
		orderEntryList.add(getNewOrderEntryModel(deliveryMode2, order1, namedDeliveryDate1));

		orderEntryList.add(getNewOrderEntryModel(deliveryMode1, order1, namedDeliveryDate2));
		orderEntryList.add(getNewOrderEntryModel(deliveryMode1, order1, namedDeliveryDate2));

		orderEntryList.add(getNewOrderEntryModel(deliveryMode2, order1, namedDeliveryDate2));
		orderEntryList.add(getNewOrderEntryModel(deliveryMode2, order1, namedDeliveryDate2));

		logTestEntries(orderEntryList);

		final List<ConsignmentModel> consignmentModelList = orderSplittingService.splitOrderForConsignmentNotPersist(order1,
				orderEntryList);

		assertEquals("Split should return 4 consignment", 4, consignmentModelList.size());

		for (final ConsignmentModel consignment : consignmentModelList)
		{
			assertEquals("Split should return 2 consignmentEntries for consignment " + consignmentModelList.indexOf(consignment), 2,
					consignment.getConsignmentEntries().size());
		}
	}



	private OrderEntryModel getNewOrderEntryModel(final DeliveryModeModel deliveryMode, final OrderModel order,
			final Date NamedDeliveryDate)
	{
		final OrderEntryModel orderEntryModel = modelService.create(OrderEntryModel.class);
		orderEntryModel.setDeliveryMode(deliveryMode);
		orderEntryModel.setOrder(order);
		orderEntryModel.setNamedDeliveryDate(NamedDeliveryDate);

		orderEntryModel.setChosenVendor(vendor);
		orderEntryModel.setProduct(product);
		orderEntryModel.setUnit(productService.getUnit("pieces"));
		orderEntryModel.setQuantity(Long.valueOf(10));
		modelService.save(orderEntryModel);
		return orderEntryModel;
	}


	private void logTestEntries(final List<AbstractOrderEntryModel> orderEntryList)
	{
		if (LOG.isDebugEnabled())
		{
			for (final AbstractOrderEntryModel currentOrderEntry : orderEntryList)
			{
				LOG.debug("OrderEntry [" + currentOrderEntry.getPk() + "] with delivery mode (" + currentOrderEntry.getDeliveryMode()
						+ ") and delivery date (" + currentOrderEntry.getNamedDeliveryDate() + ")");
			}
		}
	}

}
