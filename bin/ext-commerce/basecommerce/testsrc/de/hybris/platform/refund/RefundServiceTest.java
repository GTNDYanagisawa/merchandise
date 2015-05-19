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
package de.hybris.platform.refund;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.refund.impl.DefaultRefundService;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * RefundService test
 * 
 */
public class RefundServiceTest extends ServicelayerTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(RefundServiceTest.class.getName());
	private static final String CSV_RESOURCE_DIR = "/testsrc/";
	private static final String CSV_EXTENSION = ".csv";

	@Resource
	private I18NService i18nService;
	@Resource
	private ProductService productService;
	@Resource
	private CartService cartService;
	@Resource
	private UserService userService;
	@Resource
	private OrderService orderService;
	@Resource
	private ReturnService returnService;
	@Resource
	private RefundService refundService;
	@Resource
	private ModelService modelService;
	@Resource
	private CatalogService catalogService;
	@Resource
	private OrderHistoryService orderHistoryService;

	@Before
	public void setUp() throws Exception
	{
		importCSVFromResources("refundOrderTestData");
	}

	public void importCSVFromResources(final String fileName) throws ImpExException
	{
		final StringBuilder resource = new StringBuilder(fileName);
		if (!hasExtension(resource.toString()))
		{
			resource.append(CSV_EXTENSION);
		}
		if (!hasResourceFolder(resource.toString()))
		{
			resource.insert(0, CSV_RESOURCE_DIR);
		}

		final InputStream inStr = RefundServiceTest.class.getResourceAsStream(resource.toString());
		ImpExManager.getInstance().importDataLight(inStr, "UTF-8", true);
	}

	private boolean hasExtension(final String fileName)
	{
		return FilenameUtils.getExtension(fileName).equals("") ? false : true;
	}

	private boolean hasResourceFolder(final String fileName)
	{
		return FilenameUtils.getPath(fileName).equals("") ? false : true;
	}

	@Test
	public void testRefundCalculation() throws Exception
	{
		final ProductModel product1 = new ProductModel();
		product1.setCode("test");
		product1.setUnit(productService.getUnit("kg"));
		product1.setCatalogVersion(catalogService.getCatalogVersion("testCatalog", "Online"));
		//product1.setPrice(Double.valueOf(100));

		final PriceRowModel prmodel = modelService.create(PriceRowModel.class);
		prmodel.setCurrency(i18nService.getCurrency("EUR"));
		prmodel.setMinqtd(Long.valueOf(1));
		prmodel.setNet(Boolean.TRUE);
		prmodel.setPrice(Double.valueOf(5.00));
		prmodel.setUnit(productService.getUnit("kg"));
		prmodel.setProduct(product1);
		prmodel.setCatalogVersion(catalogService.getCatalogVersion("testCatalog", "Online"));
		modelService.saveAll(Arrays.asList(prmodel, product1));

		final CartModel cart = cartService.getSessionCart();
		final UserModel user = userService.getCurrentUser();
		cartService.addToCart(cart, product1, 2, null);

		final AddressModel deliveryAddress = new AddressModel();
		deliveryAddress.setOwner(user);
		deliveryAddress.setFirstname("Juergen");
		deliveryAddress.setLastname("Albertsen");
		deliveryAddress.setTown("Muenchen");
		modelService.saveAll(Arrays.asList(deliveryAddress));

		final DebitPaymentInfoModel paymentInfo = new DebitPaymentInfoModel();
		paymentInfo.setOwner(cart);
		paymentInfo.setCode("debit");
		paymentInfo.setBank("MeineBank");
		paymentInfo.setUser(user);
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("Ich");
		modelService.saveAll(Arrays.asList(paymentInfo));

		// the original order the customer wants to have a refund for
		final OrderModel order = orderService.placeOrder(cart, deliveryAddress, null, paymentInfo);

		// lets create a RMA for it
		final ReturnRequestModel request = returnService.createReturnRequest(order);
		returnService.createRMA(request);

		// based on the original order the call center agent creates a refund order kind of preview (**)
		final OrderModel refundOrderPreview = refundService.createRefundOrderPreview(order);

		// all following "refund processing", will be based on the refund order instance (copy of the original order)
		final AbstractOrderEntryModel productToRefund1 = refundOrderPreview.getEntries().iterator().next(); // has quantity of 2

		// create the preview "refund"
		final RefundEntryModel refundEntry1 = returnService.createRefund(request, productToRefund1, "no.1", Long.valueOf(1),
				ReturnAction.IMMEDIATE, RefundReason.LATEDELIVERY);

		// calculate the preview refund ...
		assertEquals("Unexpected order price!", BigDecimal.valueOf(10.0),
				BigDecimal.valueOf(refundOrderPreview.getTotalPrice().doubleValue()));

		refundService.apply(Arrays.asList(refundEntry1), refundOrderPreview);

		assertEquals("Wrong refund (preview)!", BigDecimal.valueOf(5.0),
				BigDecimal.valueOf(refundOrderPreview.getTotalPrice().doubleValue()));

		assertNull("There shouldn't exists any record entry yet!", ((DefaultRefundService) refundService).getModificationHandler()
				.getReturnRecord(order));
		// based on presented "preview" (see **) the customer decides if he wants to accept the offered refund
		// ... and in the case the customer agrees, the call center agent will now recalculate the "original" order
		refundService.apply(refundOrderPreview, request);

		assertEquals("Wrong refund (apply)!", BigDecimal.valueOf(5.0), BigDecimal.valueOf(order.getTotalPrice().doubleValue()));

		final Collection<OrderHistoryEntryModel> histories = orderHistoryService.getHistoryEntries(order, null, null);

		assertEquals("Wrong count of history entries!", 1, histories.size());

		final OrderHistoryEntryModel history = histories.iterator().next();

		assertEquals("Unexpected orderhistory price!", BigDecimal.valueOf(10.0),
				BigDecimal.valueOf(history.getPreviousOrderVersion().getTotalPrice().doubleValue()));
	}


	/**
	 * Test for BCOM-149
	 */
	@Test
	public void order_calc_when_quantity_equal_0() throws Exception
	{
		final ProductModel product1 = new ProductModel();
		product1.setCode("test");
		product1.setUnit(productService.getUnit("kg"));
		product1.setCatalogVersion(catalogService.getCatalogVersion("testCatalog", "Online"));
		//product1.setPrice(Double.valueOf(100));

		final PriceRowModel prmodel = modelService.create(PriceRowModel.class);
		prmodel.setCurrency(i18nService.getCurrency("EUR"));
		prmodel.setMinqtd(Long.valueOf(1));
		prmodel.setNet(Boolean.TRUE);
		prmodel.setPrice(Double.valueOf(5.00));
		prmodel.setUnit(productService.getUnit("kg"));
		prmodel.setProduct(product1);
		prmodel.setCatalogVersion(catalogService.getCatalogVersion("testCatalog", "Online"));
		modelService.saveAll(Arrays.asList(prmodel, product1));

		final CartModel cart = cartService.getSessionCart();
		final UserModel user = userService.getCurrentUser();
		cartService.addToCart(cart, product1, 2, null);

		final AddressModel deliveryAddress = new AddressModel();
		deliveryAddress.setOwner(user);
		deliveryAddress.setFirstname("Juergen");
		deliveryAddress.setLastname("Albertsen");
		deliveryAddress.setTown("Muenchen");
		modelService.saveAll(Arrays.asList(deliveryAddress));

		final DebitPaymentInfoModel paymentInfo = new DebitPaymentInfoModel();
		paymentInfo.setOwner(cart);
		paymentInfo.setCode("debit");
		paymentInfo.setBank("MeineBank");
		paymentInfo.setUser(user);
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("Ich");
		modelService.saveAll(Arrays.asList(paymentInfo));

		// the original order the customer wants to have a refund for
		final OrderModel order = orderService.placeOrder(cart, deliveryAddress, null, paymentInfo);

		// lets create a RMA for it
		final ReturnRequestModel request = returnService.createReturnRequest(order);
		returnService.createRMA(request);

		// based on the original order the call center agent creates a refund order kind of preview (**)
		final OrderModel refundOrderPreview = refundService.createRefundOrderPreview(order);

		// all following "refund processing", will be based on the refund order instance (copy of the original order)
		final AbstractOrderEntryModel productToRefund1 = refundOrderPreview.getEntries().iterator().next(); // has quantity of 2

		// create the preview "refund"
		final RefundEntryModel refundEntry1 = returnService.createRefund(request, productToRefund1, "no.1", Long.valueOf(2),
				ReturnAction.IMMEDIATE, RefundReason.LATEDELIVERY);

		// calculate the preview refund ...
		assertEquals("Unexpected order price!", BigDecimal.valueOf(10.0),
				BigDecimal.valueOf(refundOrderPreview.getTotalPrice().doubleValue()));

		refundService.apply(Arrays.asList(refundEntry1), refundOrderPreview);

		assertEquals("Wrong refund (preview)!", BigDecimal.valueOf(0.0),
				BigDecimal.valueOf(refundOrderPreview.getTotalPrice().doubleValue()));

		assertNull("There shouldn't exists any record entry yet!", ((DefaultRefundService) refundService).getModificationHandler()
				.getReturnRecord(order));
		// based on presented "preview" (see **) the customer decides if he wants to accept the offered refund
		// ... and in the case the customer agrees, the call center agent will now recalculate the "original" order
		refundService.apply(refundOrderPreview, request);

		assertEquals("Wrong refund (apply)!", BigDecimal.valueOf(0.0), BigDecimal.valueOf(order.getTotalPrice().doubleValue()));

		final Collection<OrderHistoryEntryModel> histories = orderHistoryService.getHistoryEntries(order, null, null);

		assertEquals("Wrong count of history entries!", 1, histories.size());

		final OrderHistoryEntryModel history = histories.iterator().next();

		assertEquals("Unexpected orderhistory price!", BigDecimal.valueOf(10.0),
				BigDecimal.valueOf(history.getPreviousOrderVersion().getTotalPrice().doubleValue()));
	}


}
