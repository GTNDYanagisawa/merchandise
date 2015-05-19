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
package de.hybris.platform.payment.jalo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.jalo.CategoryManager;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.commands.factory.CommandFactoryRegistry;
import de.hybris.platform.payment.commands.impl.CommandFactoryRegistryMockImpl;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.NewSubscription;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.payment.methods.CardPaymentService;
import de.hybris.platform.payment.methods.impl.DefaultCardPaymentServiceImpl;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.payment.strategy.PaymentInfoCreatorStrategy;
import de.hybris.platform.payment.strategy.TransactionCodeGenerator;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Utilities;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;



public class PaymentTest extends ServicelayerTest
{
	/**
	 * some currency symbol other than EUR
	 */
	private static final String CURRENCY_SYMBOL = Currency.getInstance(Locale.JAPAN).getSymbol();
	private static final String TEST_CC_NUMBER = "4111111111111111";
	private static final int TEST_CC_EXPIRATION_MONTH = 12;
	private static final int TEST_CC_EXPIRATION_YEAR_VALID = (Calendar.getInstance().get(Calendar.YEAR) + 2);
	private static final int TEST_CC_EXPIRATION_YEAR_EXPIRED = (Calendar.getInstance().get(Calendar.YEAR) - 2);
	private static final BigDecimal AMOUNT = BigDecimal.valueOf(242); // Front?



	@Resource
	private PaymentService paymentService;
	@Resource
	private CardPaymentService cardPaymentService;
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
	@Resource
	private CommandFactoryRegistry mockupCommandFactoryRegistry;

	//@Resource
	//private I18NService i18nService;

	private CommandFactoryRegistry serverCommandFactoryRegistry;

	/** Edit the local|project.properties to change logging behaviour (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(PaymentTest.class.getName());

	//private OrderModel order;

	@BeforeClass
	public static void prepare() throws Exception //NOPMD
	{
		Registry.activateStandaloneMode();
		Utilities.setJUnitTenant();
		LOG.debug("Preparing...");

		final ApplicationContext appCtx = Registry.getApplicationContext();
		final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) appCtx;
		final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
		if (beanFactory.getRegisteredScope("tenant") == null)
		{
			beanFactory.registerScope("tenant", new de.hybris.platform.spring.TenantScope());
		}
		final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry) beanFactory);
		LOG.info("Loading payment-spring-test.xml...");
		xmlReader.loadBeanDefinitions(new ClassPathResource("payment-spring-test.xml"));
	}

	@Before
	public void setUp() throws Exception
	{
		LOG.debug("Set up in progress...");
		final TransactionCodeGenerator generator = new MockTransactionCodeGenerator();
		((DefaultPaymentServiceImpl) paymentService).setTransactionCodeGenerator(generator);

		createCoreData();
		createDefaultCatalog();
		createOrder();

		// For some reason a different commandFactoryRegistry is set by Spring here and in the cardPaymentService
		if (cardPaymentService instanceof DefaultCardPaymentServiceImpl)
		{
			LOG.info("Setting the commandFactoryRegistry: " + mockupCommandFactoryRegistry.getClass().getName());
			final DefaultCardPaymentServiceImpl dCardPayentService = (DefaultCardPaymentServiceImpl) cardPaymentService;

			serverCommandFactoryRegistry = dCardPayentService.getCommandFactoryRegistry();
			dCardPayentService.setCommandFactoryRegistry(mockupCommandFactoryRegistry);
		}
	}

	@After
	public void tearDown()
	{
		LOG.debug("Tear down in progress...");
		if (cardPaymentService instanceof DefaultCardPaymentServiceImpl)
		{
			final DefaultCardPaymentServiceImpl dCardPayentService = (DefaultCardPaymentServiceImpl) cardPaymentService;
			dCardPayentService.setCommandFactoryRegistry(serverCommandFactoryRegistry);
		}
	}

	public static void createCoreData() throws Exception
	{
		LOG.info("Creating essential data for core ...");
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		// importing test csv
		importCsv("/test/testBasics.csv", "windows-1252");
		LOG.info("Finished creating essential data for core in " + (System.currentTimeMillis() - startTime) + "ms");
	}

	public static void createDefaultCatalog() throws Exception
	{
		LOG.info("Creating test catalog ...");
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		final long startTime = System.currentTimeMillis();

		importCsv("/test/testCatalog.csv", "windows-1252");

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

	public void createOrder() throws InvalidCartException, Exception
	{
		LOG.info("Creating order ...");

		final CurrencyModel currency = modelService.create(CurrencyModel.class);
		currency.setIsocode(CURRENCY_SYMBOL);
		currency.setSymbol(CURRENCY_SYMBOL);
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
		//final UserModel user = userService.getCurrentUser();
		cartService.addToCart(cart, product0, 2, null);
		cartService.addToCart(cart, product1, 2, null);
		cartService.addToCart(cart, product2, 2, null);
	}

	////////////////////   T E S T I N G  S T A R T S   H E R E   /////////////////////////////

	@Test
	public void testAuthorization()
	{
		final BillingInfo billingInfo = createBillingInfo();
		final AddressModel deliveryAddress = createDeliveryAddress();
		final CardInfo card = getCardInfo(billingInfo, true);
		final Currency currency = getCurrency();

		final PaymentTransactionEntryModel ptem = paymentService.authorize("173412-TXN-CODE-43142356", AMOUNT,
				(Currency) modelService.toPersistenceLayer(currency), deliveryAddress, deliveryAddress, card);

		assertEquals("Amount doesn't match!", AMOUNT.longValue(), ptem.getAmount().longValue());
		assertNotNull("Payment Transaction shouldn't ne null!", ptem.getPaymentTransaction());
		assertEquals("Transaction Status doesn't match!", TransactionStatus.ACCEPTED.name(), ptem.getTransactionStatus());
		assertEquals("Transaction Status Details doesn't match!", TransactionStatusDetails.SUCCESFULL.name(),
				ptem.getTransactionStatusDetails());
		assertEquals("Type doesn't match!", PaymentTransactionType.AUTHORIZATION.name(), ptem.getType().getCode());
		final long difference = Calendar.getInstance().getTime().getTime() - ptem.getTime().getTime();
		final long hour = 1000l * 60l * 60l;
		assertTrue("The txn did not happen in the last hour! [diff=" + difference + " milis]", (difference < hour));
	}

	@Test
	public void testFailedAuthorization()
	{
		final BillingInfo billingInfo = createBillingInfo();
		final AddressModel deliveryAddress = createDeliveryAddress();
		final CardInfo card = getCardInfo(billingInfo, false);
		final Currency currency = getCurrency();

		final PaymentTransactionEntryModel ptem = paymentService.authorize("173412-TXN-CODE-43142356", AMOUNT,
				(Currency) modelService.toPersistenceLayer(currency), deliveryAddress, deliveryAddress, card);

		assertEquals("Transaction has not been rejected!", TransactionStatus.REJECTED.name(), ptem.getTransactionStatus());
		assertEquals("Transaction has invalid description!", TransactionStatusDetails.INVALID_CARD_EXPIRATION_DATE.name(),
				ptem.getTransactionStatusDetails());
		assertEquals("Transaction type doesn't match!", PaymentTransactionType.AUTHORIZATION.name(), ptem.getType().getCode());
	}

	@Test
	public void testAuthorizationReviewStatus()
	{
		final BillingInfo billingInfo = createBillingInfo();
		final AddressModel deliveryAddress = createDeliveryAddress();
		final CardInfo card = getCardInfo(billingInfo, true);
		final Currency currency = getCurrency();
		final BigDecimal amountForReview = BigDecimal.valueOf(5001);

		final PaymentTransactionEntryModel ptem = paymentService.authorize("173412-TXN-CODE-43142356", amountForReview,
				(Currency) modelService.toPersistenceLayer(currency), deliveryAddress, deliveryAddress, card);

		assertEquals("Transaction is not in REVIEW state!", TransactionStatus.REVIEW.name(), ptem.getTransactionStatus());
		assertEquals("Transaction has invalid description!", TransactionStatusDetails.REVIEW_NEEDED.name(),
				ptem.getTransactionStatusDetails());
		assertEquals("Transaction type doesn't match!", PaymentTransactionType.AUTHORIZATION.name(), ptem.getType().getCode());
	}

	@Test
	public void testPaymentInfoCreation()
	{
		final PaymentInfoCreatorStrategy piCreator = ((DefaultPaymentServiceImpl) paymentService).getPaymentInfoCreator();
		assertNotNull(piCreator);

		final UserModel user = userService.getCurrentUser();
		final CardInfo card = getCardInfo(createBillingInfo(), true);
		final CartModel cart = cartService.getSessionCart();
		final AddressModel deliveryAddress = getDeliveryAddress(user);
		//final DebitPaymentInfoModel paymentInfo = getPaymentInfo(cart, user);

		final PaymentTransactionEntryModel cartPtem = paymentService.authorize("1234", AMOUNT,
				(Currency) modelService.toPersistenceLayer(getCurrency()), deliveryAddress, deliveryAddress, card);

		final PaymentTransactionModel cartTX = cartPtem.getPaymentTransaction();
		cartTX.setOrder(cart);
		modelService.save(cartTX);

		((DefaultPaymentServiceImpl) paymentService).attachPaymentInfo(cartTX, user, card, AMOUNT);

		final CreditCardPaymentInfoModel info = (CreditCardPaymentInfoModel) cartTX.getInfo();
		assertEquals(info.getBillingAddress().getEmail(), "nobody@cybersource.com");
		assertEquals(info.getUser().getUid(), "admin");
		assertEquals(info.getNumber(), "4111111111111111");
		assertEquals(info.getValidToYear(), String.valueOf(TEST_CC_EXPIRATION_YEAR_VALID));
	}

	@Test
	public void testAuthorizeWithCode() throws InvalidCartException
	{
		final UserModel user = userService.getCurrentUser();
		final CardInfo card = getCardInfo(createBillingInfo(), true);
		final CartModel cart = getCart();
		final AddressModel deliveryAddress = getDeliveryAddress(user);
		final DebitPaymentInfoModel paymentInfo = getPaymentInfo(cart, user);

		final PaymentTransactionEntryModel cartPtem = paymentService.authorize("1234", AMOUNT,
				(Currency) modelService.toPersistenceLayer(getCurrency()), deliveryAddress, deliveryAddress, card);

		final PaymentTransactionModel cartTX = cartPtem.getPaymentTransaction();
		cartTX.setOrder(cart);
		modelService.save(cartTX);

		final String cartPtemCode = cartPtem.getCode(); // preserve code since placing the order will remove the cart payment tx

		final OrderModel order = orderService.placeOrder(cart, deliveryAddress, null, paymentInfo);
		final List<PaymentTransactionModel> transactions = order.getPaymentTransactions();

		assertEquals("Wrong PaymentTransaction count!", 1, transactions.size());

		final PaymentTransactionModel orderTX = order.getPaymentTransactions().iterator().next();

		assertEquals("Wrong PaymentTransactionEntry count!", 1, orderTX.getEntries().size());

		final PaymentTransactionEntryModel orderPtem = orderTX.getEntries().iterator().next();

		assertEquals("Wrong transactioncode!", cartPtemCode, orderPtem.getCode());
	}

	@Test
	public void testAuthorizeWithTransaction() throws InvalidCartException
	{
		final UserModel user = userService.getCurrentUser();
		final CardInfo card = getCardInfo(createBillingInfo(), true);
		final CartModel cart = getCart();
		final AddressModel deliveryAddress = getDeliveryAddress(user);
		final DebitPaymentInfoModel paymentInfo = getPaymentInfo(cart, user);

		final PaymentTransactionModel cartTX = new PaymentTransactionModel();
		cartTX.setCode("1234");
		modelService.save(cartTX);

		final PaymentTransactionEntryModel cartPtem = paymentService.authorize(cartTX, AMOUNT,
				(Currency) modelService.toPersistenceLayer(getCurrency()), deliveryAddress, deliveryAddress, card);

		assertEquals("Wrong PaymentTransaction instance!", cartTX, cartPtem.getPaymentTransaction());

		cartTX.setOrder(cart);
		modelService.save(cartTX);

		final String cartPtemCode = cartPtem.getCode();

		final OrderModel order = orderService.placeOrder(cart, deliveryAddress, null, paymentInfo);
		final List<PaymentTransactionModel> transactions = order.getPaymentTransactions();

		assertEquals("Wrong PaymentTransaction count!", 1, transactions.size());

		final PaymentTransactionModel orderTX = order.getPaymentTransactions().iterator().next();

		assertEquals("Wrong PaymentTransactionEntry count!", 1, orderTX.getEntries().size());

		final PaymentTransactionEntryModel orderPtem = orderTX.getEntries().iterator().next();

		assertEquals("Wrong transactioncode!", cartPtemCode, orderPtem.getCode());

		// there should be only one
		assertNotNull("PaymentTransaction not found!", paymentService.getPaymentTransaction("1234"));
	}

	@Test
	public void testCapture() throws InvalidCartException
	{
		final UserModel user = userService.getCurrentUser();
		final CardInfo card = getCardInfo(createBillingInfo(), true);
		final CartModel cart = getCart();
		final AddressModel deliveryAddress = getDeliveryAddress(user);
		final DebitPaymentInfoModel paymentInfo = getPaymentInfo(cart, user);

		// create TXN
		final PaymentTransactionModel cartTX = new PaymentTransactionModel();
		cartTX.setCode("777");
		modelService.save(cartTX);

		// authorize attempt, successfull
		final PaymentTransactionEntryModel cartPtem = paymentService.authorize(cartTX, AMOUNT,
				(Currency) modelService.toPersistenceLayer(getCurrency()), deliveryAddress, deliveryAddress, card);

		// assign the cart to the transaction
		cartTX.setOrder(cart);
		modelService.save(cartTX);

		final String cartPtemTxStatus = cartPtem.getTransactionStatus();

		// create an order from the cart
		final OrderModel order = orderService.placeOrder(cart, deliveryAddress, null, paymentInfo);

		// very surprising, the old cartTX is not valid for capture any more, get the txn from the order instead
		final PaymentTransactionModel orderTX = order.getPaymentTransactions().iterator().next();

		// capture attempt
		if (TransactionStatus.ACCEPTED.name().equals(cartPtemTxStatus))
		{
			final PaymentTransactionEntryModel cartPtemCapture = paymentService.capture(orderTX);
			assertEquals("Capture Transaction Status doesn't match!", TransactionStatus.ACCEPTED.name(),
					cartPtemCapture.getTransactionStatus());
			assertNotSame("Two different transactions must not be identical!", cartPtem, cartPtemCapture);
		}
	}

	@Test
	public void testSubscriptionOperations() throws InvalidCartException
	{
		final UserModel user = userService.getCurrentUser();
		final CardInfo card = getCardInfo(createBillingInfo(), true);
		final AddressModel deliveryAddress = getDeliveryAddress(user);
		final Currency currency = (Currency) modelService.toPersistenceLayer(getCurrency());

		// Plain vanilla authorization
		final PaymentTransactionEntryModel ptem = paymentService.authorize("1234567", AMOUNT, currency, deliveryAddress,
				deliveryAddress, card);
		modelService.refresh(ptem);
		modelService.refresh(ptem.getPaymentTransaction());

		assertEquals("Authorize (card): Status doesn't match!", TransactionStatus.ACCEPTED.name(), ptem.getTransactionStatus());
		assertEquals("Authorize (card): Status Details doesn't match!", TransactionStatusDetails.SUCCESFULL.name(),
				ptem.getTransactionStatusDetails());

		// Create subscription out of the authorization
		final NewSubscription newSubscription = paymentService.createSubscription(ptem.getPaymentTransaction(), deliveryAddress,
				card);
		final PaymentTransactionEntryModel ptem2 = newSubscription.getTransactionEntry();
		final String subscriptionID = newSubscription.getSubscriptionID();

		assertEquals("Create subscription: Status doesn't match!", TransactionStatus.ACCEPTED.name(), ptem2.getTransactionStatus());
		assertEquals("Create subscription: Status Details doesn't match!", TransactionStatusDetails.SUCCESFULL.name(),
				ptem2.getTransactionStatusDetails());
		assertNotNull("Subscription ID must not be null!", subscriptionID);

		// Let's use the subscription to make an authorization
		final PaymentTransactionEntryModel txn1 = paymentService.authorize("1234568", AMOUNT, currency, null, subscriptionID);

		assertEquals("Authorize (no card) Transaction Status doesn't match!", TransactionStatus.ACCEPTED.name(),
				txn1.getTransactionStatus());
		assertEquals("Authorize (no card) Transaction Status Details doesn't match!", TransactionStatusDetails.SUCCESFULL.name(),
				txn1.getTransactionStatusDetails());

		// get data
		final BillingInfo billInfo = new BillingInfo();
		final CardInfo cardInfo = new CardInfo();

		final PaymentTransactionEntryModel txn2 = paymentService.getSubscriptionData("1234569", subscriptionID,
				CommandFactoryRegistryMockImpl.MOCKUP_PAYMENT_PROVIDER, billInfo, cardInfo);

		assertEquals("GetData subscription: Status doesn't match!", TransactionStatus.ACCEPTED.name(), txn2.getTransactionStatus());
		assertEquals("GetData subscription: Status Details doesn't match!", TransactionStatusDetails.SUCCESFULL.name(),
				txn2.getTransactionStatusDetails());

		// and update the subscription
		final PaymentTransactionEntryModel txn3 = paymentService.updateSubscription("1234570", subscriptionID,
				CommandFactoryRegistryMockImpl.MOCKUP_PAYMENT_PROVIDER, null, cardInfo);

		assertEquals("Update subscription: Status doesn't match!", TransactionStatus.ACCEPTED.name(), txn3.getTransactionStatus());
		assertEquals("Update subscription: Status Details doesn't match!", TransactionStatusDetails.SUCCESFULL.name(),
				txn3.getTransactionStatusDetails());
	}

	@Test
	public void testCreateSubscriptionWithoutAuth() throws InvalidCartException
	{
		final UserModel user = userService.getCurrentUser();
		final CardInfo card = getCardInfo(createBillingInfo(), true);
		final AddressModel deliveryAddress = getDeliveryAddress(user);

		final NewSubscription newSubscription = paymentService.createSubscription("1234567",
				CommandFactoryRegistryMockImpl.MOCKUP_PAYMENT_PROVIDER, getCurrency(), deliveryAddress, card);
		final PaymentTransactionEntryModel ptem = newSubscription.getTransactionEntry();

		assertEquals("Transaction Status doesn't match!", TransactionStatus.ACCEPTED.name(), ptem.getTransactionStatus());
		assertEquals("Transaction Status Details doesn't match!", TransactionStatusDetails.SUCCESFULL.name(),
				ptem.getTransactionStatusDetails());
	}

	////////////////////   T E S T I N G    E N D S   H E R E   /////////////////////////////

	private DebitPaymentInfoModel getPaymentInfo(final CartModel cart, final UserModel user)
	{
		final DebitPaymentInfoModel paymentInfo = new DebitPaymentInfoModel();
		paymentInfo.setOwner(cart);
		paymentInfo.setBank("MyBank");
		paymentInfo.setUser(user);
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("Ich");
		return paymentInfo;
	}

	private AddressModel getDeliveryAddress(final UserModel user)
	{
		final AddressModel deliveryAddress = new AddressModel();
		deliveryAddress.setOwner(user);
		deliveryAddress.setFirstname("Albert");
		deliveryAddress.setLastname("Einstein");
		deliveryAddress.setTown("Munich");
		return deliveryAddress;
	}

	private CartModel getCart() throws InvalidCartException
	{
		final ProductModel product0 = productService.getProduct("testProduct0");
		final ProductModel product1 = productService.getProduct("testProduct1");
		final ProductModel product2 = productService.getProduct("testProduct2");
		final CartModel cart = cartService.getSessionCart();
		cartService.addToCart(cart, product0, 2, null);
		cartService.addToCart(cart, product1, 2, null);
		cartService.addToCart(cart, product2, 2, null);

		return cart;
	}

	private Currency getCurrency()
	{
		return Currency.getInstance("USD");
	}

	private CardInfo getCardInfo(final BillingInfo billingInfo, final boolean valid)
	{
		final CardInfo card = new CardInfo();
		card.setCardHolderFullName("John Doe");
		card.setCardNumber(TEST_CC_NUMBER);
		card.setExpirationMonth(Integer.valueOf(TEST_CC_EXPIRATION_MONTH));
		if (valid)
		{
			card.setExpirationYear(Integer.valueOf(TEST_CC_EXPIRATION_YEAR_VALID));
		}
		else
		{
			card.setExpirationYear(Integer.valueOf(TEST_CC_EXPIRATION_YEAR_EXPIRED));
		}
		card.setBillingInfo(billingInfo);
		card.setCardType(CreditCardType.VISA);
		return card;
	}

	private AddressModel createDeliveryAddress()
	{
		final AddressModel deliveryAddress = new AddressModel();
		deliveryAddress.setOwner(userService.getCurrentUser());
		deliveryAddress.setFirstname("Juergen");
		deliveryAddress.setLastname("Albertsen");
		deliveryAddress.setTown("Muenchen");
		modelService.saveAll(Arrays.asList(deliveryAddress));
		return deliveryAddress;
	}

	private BillingInfo createBillingInfo()
	{
		final BillingInfo billingInfo = new BillingInfo();
		billingInfo.setCity("Mountain View");
		billingInfo.setCountry("US");
		billingInfo.setEmail("nobody@cybersource.com");
		billingInfo.setFirstName("John");
		billingInfo.setIpAddress("10.7" + "." + "7.7");
		billingInfo.setLastName("Doe");
		billingInfo.setPhoneNumber("650-965-6000");
		billingInfo.setPostalCode("94043");
		billingInfo.setState("CA");
		billingInfo.setStreet1("1295 Charleston Road");
		return billingInfo;
	}

	public class MockTransactionCodeGenerator implements TransactionCodeGenerator
	{
		private int suffix = 4711;

		@Override
		public String generateCode(final String base)
		{
			suffix++;
			return base + "-" + suffix;
		}
	}
}
