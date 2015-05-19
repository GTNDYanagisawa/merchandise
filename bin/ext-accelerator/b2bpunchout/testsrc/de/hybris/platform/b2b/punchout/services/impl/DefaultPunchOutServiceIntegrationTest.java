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
package de.hybris.platform.b2b.punchout.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;
import de.hybris.platform.b2b.punchout.services.PunchOutService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.io.FileNotFoundException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.cxml.CXML;
import org.cxml.OrderRequest;
import org.cxml.ProfileRequest;
import org.cxml.ProfileResponse;
import org.cxml.PunchOutSetupRequest;
import org.cxml.PunchOutSetupResponse;
import org.cxml.Response;
import org.cxml.Transaction;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Integration test for {@link PunchOutService}.
 */
@Ignore("This test is unstable and will be fixed on the next release")
@IntegrationTest
public class DefaultPunchOutServiceIntegrationTest extends ServicelayerTest
{
	@Resource
	private PunchOutService punchOutService;

	@Resource
	private CartService cartService;

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	private CartModel cartModel;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/b2bpunchout/test/testOrganizations.csv", "utf-8");
		importCsv("/b2bpunchout/test/testB2BCatalog.csv", "utf-8");
		importCsv("/b2bpunchout/test/testB2BPunchOut.csv", "utf-8");
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID("b2bstoretemplate"), false);

		userService.setCurrentUser(userService.getAnonymousUser());

		cartModel = cartService.getSessionCart();
		assertNotNull("Session cart must be created", cartModel);
		assertNotNull("Session cart must have been persisted", cartModel.getPk());
		assertNotNull("Session cart must have been persisted", cartModel.getPk().getLong());
		assertTrue("Session cart must have been persisted with PK > 0", cartModel.getPk().getLongValue() > 0);

		final List<OrderModel> allOrders = findAllOrders();
		modelService.removeAll(allOrders);

		assertEquals("Sanity check that there are no existing orders in the database", 0, findAllOrders().size());
	}

	/**
	 * Tests the full scenario of passing an {@link OrderRequest}, populating a shopping cart and placing an order.
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void testPurchaseOrderRequestHappyPath() throws FileNotFoundException
	{
		final CXML requestBody = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/sampleOrderRequest.xml");

		final CXML response = punchOutService.processPurchaseOrderRequest(requestBody, cartModel);

		assertNotNull("Response CXML cannot be null", response);

		final List<Object> headerOrMessageOrRequestOrResponse = response.getHeaderOrMessageOrRequestOrResponse();
		assertEquals("Expecting the result CXML to only contain a response instance", 1, headerOrMessageOrRequestOrResponse.size());

		final Response responseData = (Response) headerOrMessageOrRequestOrResponse.get(0);
		assertEquals("200", responseData.getStatus().getCode());

		final List<OrderModel> allOrders = findAllOrders();

		assertEquals("Exactly one new order is expected to have been created", 1, allOrders.size());

		final OrderModel order = allOrders.iterator().next();

		final List<AbstractOrderEntryModel> entries = order.getEntries();
		assertEquals(3, entries.size());

		assertFalse("The order should not be calculated as it has been processed by the procurement system", order.getCalculated()
				.booleanValue());
		assertNotNull("Delivery address should be populated on the order", order.getDeliveryAddress());

		assertEquals("Order status should be ...?", OrderStatus.CREATED, order.getStatus());

		assertEquals("Total should match the request CXML value", new Double(187.60D), order.getTotalPrice());
		assertEquals("Total tax for this request is $0.00", new Double(0D), order.getTotalTax());
		assertEquals("No discounts should have been set", new Double(0D), order.getTotalDiscounts());

		assertTrue("No promotions should have been applied", CollectionUtils.isEmpty(order.getAllPromotionResults()));
	}

	private List<OrderModel> findAllOrders()
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery("select {" + OrderModel.PK + "} from {" + OrderModel._TYPECODE
				+ "}");
		final SearchResult<OrderModel> result = flexibleSearchService.search(query);
		return result.getResult();
	}

	/**
	 * Tests that sending a proper {@link PunchOutSetupRequest} in the form of a parsed from a file cXML object will
	 * yield a proper cXML {@link PunchOutSetupResponse}.
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void testPunchOutSetUpRequestHappyPath() throws FileNotFoundException
	{
		final CXML requestBody = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequest.xml");

		final CXML responseCXml = punchOutService.processPunchOutSetUpRequest(requestBody);

		final CXMLElementBrowser cxmlElementBrowser = new CXMLElementBrowser(responseCXml);

		final Response response = cxmlElementBrowser.findResponse();

		assertNotNull("Response must always exist in the response", response);

		assertEquals("Response code is expected to be success (200)", "200", response.getStatus().getCode());

		final PunchOutSetupResponse punchOutResponse = cxmlElementBrowser.findResponseByType(PunchOutSetupResponse.class);
		assertNotNull(punchOutResponse);

		final String url = punchOutResponse.getStartPage().getURL().getvalue();

		assertNotNull("StartPage URL must be part of the response", url);

		assertTrue("Must be a well formed URL", new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS).isValid(url));
	}

	/**
	 * Tests that sending a proper {@link PunchOutSetupRequest} in the form of a parsed from a file cXML object will
	 * yield a proper cXML {@link PunchOutSetupResponse}. The operation is 'edit'.
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void testPunchOutSetUpRequestEditOperation() throws FileNotFoundException
	{
		// sanity check
		CartModel cart = cartService.getSessionCart();
		assertEquals("Before the punchout setup request no items are expected in the cart", 0, cart.getEntries().size());

		final CXML requestBody = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequestEditOperation.xml");

		final CXML responseCXml = punchOutService.processPunchOutSetUpRequest(requestBody);

		final CXMLElementBrowser cxmlElementBrowser = new CXMLElementBrowser(responseCXml);
		final Response response = cxmlElementBrowser.findResponse();

		assertNotNull("Response must always exist in the response", response);

		assertEquals("Response code is expected to be success (200)", "200", response.getStatus().getCode());
		final PunchOutSetupResponse punchOutResponse = cxmlElementBrowser.findResponseByType(PunchOutSetupResponse.class);
		assertNotNull(punchOutResponse);

		final String url = punchOutResponse.getStartPage().getURL().getvalue();

		assertNotNull("StartPage URL must be part of the response", url);

		assertTrue("Must be a well formed URL", new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS).isValid(url));

		cart = cartService.getSessionCart();
		assertEquals("Expected 3 cart items as the items should match the itemOut elements in the CXML", 3, cart.getEntries()
				.size());
	}

	/**
	 * Tests that getting a {@link ProfileRequest} CXML will yield a proper {@link ProfileResponse} back.
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void testProfileTransactionHappyPath() throws FileNotFoundException
	{
		final CXML request = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/sampleProfileRequest.xml");
		final CXML result = punchOutService.processProfileRequest(request);

		final CXMLElementBrowser cxmlElementBrowser = new CXMLElementBrowser(result);
		final ProfileResponse profileResponse = cxmlElementBrowser.findResponseByType(ProfileResponse.class);

		final List<Transaction> transactions = profileResponse.getTransaction();
		assertTrue("At least one supported transaction is expected", CollectionUtils.isNotEmpty(transactions));

		for (final Transaction transaction : transactions)
		{
			assertNotNull("Request name should never be null", transaction.getRequestName());
			assertTrue("Request name should never be empty", StringUtils.isNotBlank(transaction.getRequestName()));
			assertTrue("Must be a well formed URL",
					new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS).isValid(transaction.getURL().getvalue()));
		}
	}
}
