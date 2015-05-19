/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.btg.integration;

import de.hybris.platform.basecommerce.enums.IntervalResolution;
import de.hybris.platform.btg.condition.ConditionEvaluatorRegistry;
import de.hybris.platform.btg.condition.impl.NumericExpressionEvaluator;
import de.hybris.platform.btg.condition.impl.PriceExpressionEvaluator;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.btg.enums.BTGRuleType;
import de.hybris.platform.btg.model.BTGAbstractLiteralOperandModel;
import de.hybris.platform.btg.model.BTGAssignToGroupDefinitionModel;
import de.hybris.platform.btg.model.BTGBooleanLiteralOperandModel;
import de.hybris.platform.btg.model.BTGCategoriesInOrdersOperandModel;
import de.hybris.platform.btg.model.BTGConditionModel;
import de.hybris.platform.btg.model.BTGDoubleLiteralOperandModel;
import de.hybris.platform.btg.model.BTGEachOrderTotalSumOperandModel;
import de.hybris.platform.btg.model.BTGGenderEnumLiteralOperandModel;
import de.hybris.platform.btg.model.BTGIntegerLiteralOperandModel;
import de.hybris.platform.btg.model.BTGLastOrderDateOperandModel;
import de.hybris.platform.btg.model.BTGLastOrdersOperandModel;
import de.hybris.platform.btg.model.BTGNumberOfOrdersOperandModel;
import de.hybris.platform.btg.model.BTGOperandModel;
import de.hybris.platform.btg.model.BTGOperatorModel;
import de.hybris.platform.btg.model.BTGOrderTotalSumOperandModel;
import de.hybris.platform.btg.model.BTGProductsInCartOperandModel;
import de.hybris.platform.btg.model.BTGProductsInOrdersOperandModel;
import de.hybris.platform.btg.model.BTGQuantityOfProductInCartOperandModel;
import de.hybris.platform.btg.model.BTGReferenceCategoriesOperandModel;
import de.hybris.platform.btg.model.BTGReferenceCountriesOperandModel;
import de.hybris.platform.btg.model.BTGReferenceDateOperandModel;
import de.hybris.platform.btg.model.BTGReferenceExactDateOperandModel;
import de.hybris.platform.btg.model.BTGReferencePriceOperandModel;
import de.hybris.platform.btg.model.BTGReferenceProductsOperandModel;
import de.hybris.platform.btg.model.BTGRuleModel;
import de.hybris.platform.btg.model.BTGSegmentModel;
import de.hybris.platform.btg.model.BTGStringLiteralOperandModel;
import de.hybris.platform.btg.model.BTGUserAddressPostalCodeOperandModel;
import de.hybris.platform.btg.model.BTGUserCountryOperandModel;
import de.hybris.platform.btg.model.BTGUserGenderOperandModel;
import de.hybris.platform.btg.rule.impl.DefaultRuleEvaluator;
import de.hybris.platform.btg.services.BTGEvaluationService;
import de.hybris.platform.btg.services.BTGResultService;
import de.hybris.platform.btg.services.ExpressionService;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSSiteDao;
import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.model.action.AbstractActionModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;


@Ignore
public abstract class BTGIntegrationTest extends ServicelayerTest
{

	private static final Logger LOG = Logger.getLogger(BTGIntegrationTest.class);

	//--customers
	protected CustomerModel customerA;
	protected CustomerModel customerB;
	protected CustomerModel customerC;
	protected UserModel anonymous;
	protected final static String USER_A = "user_a";
	protected final static String USER_B = "user_b";
	protected final static String USER_C = "user_c";

	//--user groups
	protected final static String GOODTASTE = "good_taste";
	protected final static String BADTASTE = "bad_taste";
	protected UserGroupModel goodTaste;
	protected UserGroupModel badTaste;

	//--web sites
	protected CMSSiteModel websiteA;
	protected CMSSiteModel websiteB;
	protected CMSSiteModel websiteC;
	protected final static String WEBSITE_A = "webSiteA";
	protected final static String WEBSITE_B = "webSiteB";
	protected final static String WEBSITE_C = "webSiteC";

	//--products templates
	protected final static String NONFULFILLING_PRODUCT_TEMPLATE = "otherProduct_";
	protected final static String AUGISTINER = "augustinerhelles";
	protected final static String HACKERPSCHORR = "hackerpschorr";
	protected final static String TYSKIE = "tyskie";
	protected final static String KROSTITZER = "krostitzer";
	protected final static String EXPENSIVE_150 = "expensiveProduct_150EUR";
	protected final static String EXPENSIVE_60 = "expensiveProduct_60EUR";
	protected final static String EXPENSIVE_100 = "expensiveProduct_100EUR";
	protected final static String EXPENSIVE_40 = "expensiveProduct_40EUR";
	//--Catalog Versions
	protected final static String ONLINE = "Online";
	protected final static String STAGED = "Staged";
	protected final static String DRINKS = "drinksCatalog";
	protected CatalogVersionModel online;
	protected CatalogVersionModel staged;

	//--categories
	protected final static String ALCOHOLS_ONLINE = "AlcoholsOnline";
	protected final static String ALCOHOLS_STAGED = "AlcoholsStaged";
	protected final static String BEERS_ONLINE = "BeersOnline";
	protected final static String BEERS_STAGED = "BeersStaged";
	protected final static String POLISHBEERS_ONLINE = "PolishBeersOnline";
	protected final static String POLISHBEERS_STAGED = "PolishBeersStaged";
	protected final static String TRADITIONALBEERS = "TraditionalBeers";
	protected final static String GERMANBEERS_ONLINE = "GermanBeersOnline";
	protected final static String GERMANBEERS_STAGED = "GermanBeersStaged";
	protected final static String SOFTDRINKS_ONLINE = "SoftDrinksOnline";
	protected final static String SOFTDRINKS_STAGED = "SoftDrinksStaged";

	//--currencies

	protected final static String EUR = "EUR";
	protected final static String USD = "USD";
	protected CurrencyModel eur;
	protected CurrencyModel usd;

	//--segments
	private final static String SEGMENTA = "segmentA";
	private final static String SEGMENTB = "segmentB";
	private final static String SEGMENTC = "segmentC";
	protected BTGSegmentModel segmentA;
	protected BTGSegmentModel segmentB;
	protected BTGSegmentModel segmentC;

	//--rules
	protected BTGRuleModel segmentAOrderRule;
	protected BTGRuleModel segmentACartRule;

	@Resource
	protected ModelService modelService;

	@Resource
	protected TypeService typeService;

	@Resource
	protected UserService userService;
	@Resource
	private CartService cartService;
	@Resource
	protected ProductService productService;
	@Resource
	protected CategoryService categoryService;
	@Resource
	private OrderService orderService;
	@Resource
	private CMSSiteDao cmsSiteDao;
	@Resource
	protected I18NService i18nService;
	@Resource
	protected CatalogService catalogService;
	@Resource
	protected FlexibleSearchService flexibleSearchService;
	@Resource
	protected DefaultRuleEvaluator ruleEvaluator;

	@Resource
	protected BTGEvaluationService btgEvaluationService;

	@Resource
	protected BTGResultService btgResultService;

	@Resource
	protected ExpressionService expressionService;

	@Resource
	protected ConditionEvaluatorRegistry conditionEvaluatorRegistry;

	@Before
	public void setUp() throws Exception
	{
		createBTGData();

	}

	protected CatalogVersionModel getDrinksOnlineCatalogVersion()
	{
		return getDrinksCatalogVersion("Online");
	}

	protected CatalogVersionModel getDrinksCatalogVersion(final String catalogVersionName)
	{
		return catalogService.getCatalogVersion("drinksCatalog", catalogVersionName);
	}

	protected BTGRuleModel createRule(final String name)
	{
		final BTGRuleModel rule = modelService.create(BTGRuleModel.class);
		rule.setCatalogVersion(getDrinksOnlineCatalogVersion());
		rule.setUid(UUID.randomUUID().toString());
		rule.setCode(name);
		rule.setRuleType(BTGRuleType.CART);
		return rule;
	}

	protected BTGSegmentModel createSegment()
	{
		final BTGSegmentModel segment = modelService.create(BTGSegmentModel.class);

		segment.setScope(BTGConditionEvaluationScope.ONLINE);
		final Calendar cal = Utilities.getDefaultCalendar();
		cal.add(Calendar.YEAR, -1);
		segment.setActiveFrom(cal.getTime());
		cal.add(Calendar.YEAR, 2);
		segment.setActiveTo(cal.getTime());
		segment.setName("testSegment");
		segment.setActive(Boolean.TRUE);
		segment.setCatalogVersion(getDrinksOnlineCatalogVersion());
		segment.setUid(UUID.randomUUID().toString());
		return segment;

	}

	protected BTGSegmentModel createSegment(final BTGConditionEvaluationScope scope, final String name,
			final CatalogVersionModel catVersion)
	{
		final BTGSegmentModel segment = modelService.create(BTGSegmentModel.class);

		segment.setScope(scope);
		final Calendar cal = Utilities.getDefaultCalendar();
		cal.add(Calendar.YEAR, -1);
		segment.setActiveFrom(cal.getTime());
		cal.add(Calendar.YEAR, 2);
		segment.setActiveTo(cal.getTime());
		segment.setName(name);
		segment.setCatalogVersion(catVersion);
		segment.setUid(UUID.randomUUID().toString());
		return segment;

	}

	protected void addToCart(final String... products) throws Exception
	{
		addToCartWithCatalog(getDrinksOnlineCatalogVersion(), products);
	}

	protected void addToCartWithCatalog(final String catalogVersionName, final String... products) throws Exception
	{
		addToCartWithCatalog(getDrinksCatalogVersion(catalogVersionName), products);
	}

	private void addToCartWithCatalog(final CatalogVersionModel catalogVersionModel, final String... products) throws Exception
	{
		final CartModel cart = cartService.getSessionCart();
		for (final String productName : products)
		{
			cartService.addToCart(cart, productService.getProduct(catalogVersionModel, productName), 1, null);
		}
	}

	protected OrderModel placeTestOrder(final String... products) throws Exception
	{
		try
		{
			final CartModel cart = cartService.getSessionCart();
			final UserModel user = userService.getCurrentUser();
			for (final String productName : products)
			{
				cartService.addToCart(cart, productService.getProduct(productName), 1, null);
			}

			final AddressModel deliveryAddress = new AddressModel();
			deliveryAddress.setOwner(user);
			deliveryAddress.setFirstname("Der");
			deliveryAddress.setLastname("Buck");
			deliveryAddress.setTown("Muenchen");

			final DebitPaymentInfoModel paymentInfo = new DebitPaymentInfoModel();
			paymentInfo.setOwner(cart);
			paymentInfo.setBank("MeineBank");
			paymentInfo.setUser(user);
			paymentInfo.setAccountNumber("34434");
			paymentInfo.setBankIDNumber("1111112");
			paymentInfo.setBaOwner("Ich");

			final OrderModel order = orderService.placeOrder(cart, deliveryAddress, null, paymentInfo);
			if (Config.isMySQLUsed())
			{
				//MySQL datetime has a resolution of 1 sec!!!
				//we want to assure that creationTime is different for each testorder.
				Thread.sleep(1100);
			}

			modelService.save(order);
			LOG.debug("test order placed : " + order.getCreationtime() + " total:" + order.getTotalPrice());


			return order;
		}
		catch (final InvalidCartException e)
		{
			LOG.error("Error placing test order: " + e.getMessage(), e);
			throw e;
		}
	}

	protected void createBTGData() throws Exception
	{
		LOG.info("Creating btg test data ..");
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		importCsv("/test/btgTestData.csv", "windows-1252");
		LOG.info("Finished creating btg test data " + (System.currentTimeMillis() - startTime) + "ms");

		setCustomersAndUserGroups();
		setWebSites();
		setCurrencies();
		setCatalogVersions();

	}

	/**
	 * Segment A for WebSiteA and Online catalog </br> Rule A1: <li>Order Rule Sum of values of last 10 orders >= 1000
	 * <li>Each order value of last 10 orders >= 50 <li>
	 * Last 5 orders contains product with code = 'augustinerhelles'</br> Rule A2: <li>Cart Rule Cart contains product
	 * with code ='augustinerhelles'</br> OutputAction: <li>Add to group "Good Taste"
	 */
	protected void createSegmentA()
	{
		final Collection<BTGConditionModel> conditionsA1 = new ArrayList<BTGConditionModel>();
		conditionsA1.add(createExpression(createOrderOperand(BTGOrderTotalSumOperandModel.class, 10, "segmentAOrderCondition1"),
				createOperator(PriceExpressionEvaluator.GREATER_OR_EQUALS), createPriceReferenceOperand(1000, EUR)));
		conditionsA1.add(createExpression(
				createOrderOperand(BTGEachOrderTotalSumOperandModel.class, 10, "segmentAOrderCondition2"),
				createOperator(PriceExpressionEvaluator.GREATER_OR_EQUALS), createPriceReferenceOperand(50, EUR)));
		conditionsA1.add(createExpression(createProductsInOrdersOperand(5, "orderTestOperand"), createOperator("contains"),
				createProductsOperand(AUGISTINER)));
		conditionsA1.add(createExpression(createOperandModel("A1C3L", BTGNumberOfOrdersOperandModel.class),
				createOperator(NumericExpressionEvaluator.GREATER_OR_EQUALS), createIntLiteralOperand(Integer.valueOf(15))));
		segmentAOrderRule = createRule(online, conditionsA1, "segmentAOrderRule", BTGRuleType.ORDER);


		segmentACartRule = createRule(online, Collections.singletonList(createExpression(
				createOperandModel("products in cart", BTGProductsInCartOperandModel.class), createOperator("contains"),
				createProductsOperand(AUGISTINER))), "segmentACartRule", BTGRuleType.ORDER);

		segmentA = createSegment(BTGConditionEvaluationScope.ONLINE, SEGMENTA, online);
		segmentA.setSites(Collections.singletonList(websiteA));
		segmentA.setRules(Arrays.asList(segmentAOrderRule, segmentACartRule));
		segmentA.setActive(Boolean.TRUE);
		segmentA.setOutputActions(Collections.singletonList(createAssign2GroupAction(online, goodTaste, "segmentAOutputAction")));
		modelService.save(segmentA);
	}

	/**
	 * Segment B for WebSite B and Online catalog Rule B1: User Rule User lives in Munich Rule B2: Order Rule Last 5
	 * orders contains product with code = 'HACKERPSCHORR' OutputAction: Add to group "Bad Taste"
	 */
	protected void createSegmentB()
	{
		final BTGRuleModel ruleB1 = createRule(online, Collections.singletonList(createExpression(
				createCustomerPostalCodeOperand("customerPostalCodeOperand"), createOperator("startsWith"),
				createStringLiteralOperand("D"))), "testRuleB1", BTGRuleType.ORDER);
		final BTGRuleModel ruleB2 = createRule(online, Collections.singletonList(createExpression(
				createProductsInOrdersOperand(5, "productsOpernand"), createOperator("contains"),
				createProductsOperand(HACKERPSCHORR))), "testRuleB2", BTGRuleType.ORDER);
		segmentB = createSegment(BTGConditionEvaluationScope.ONLINE, SEGMENTB, online);
		segmentB.setSites(Collections.singletonList(websiteB));
		segmentB.setRules(Arrays.asList(ruleB1, ruleB2));
		segmentB.setActive(Boolean.TRUE);
		segmentB.setOutputActions(Collections.singletonList(createAssign2GroupAction(online, badTaste, "segmentBOutputAction")));
		modelService.save(segmentB);
	}

	/**
	 * Segment C for WebSiteA and Offline catalog Rule A1: Order Rule Sum of values of last 10 orders >= 2000 Each order
	 * value of last 10 orders >= 50 Last 5 orders contains product with code = 'augustinerhelles' Rule A2: Cart Rule
	 * Cart contains product with code = 'augustinerhelles' OutputAction: Add to group "Good Taste"
	 */
	protected void createSegmentC()
	{
		final Collection<BTGConditionModel> conditionsC1 = new ArrayList<BTGConditionModel>();
		conditionsC1.add(createExpression(createOrderOperand(BTGOrderTotalSumOperandModel.class, 10, "C1C1L"),
				createOperator(PriceExpressionEvaluator.GREATER_OR_EQUALS), createPriceReferenceOperand(2000, EUR)));
		conditionsC1.add(createExpression(createOrderOperand(BTGEachOrderTotalSumOperandModel.class, 10, "C1C2L"),
				createOperator(PriceExpressionEvaluator.GREATER_OR_EQUALS), createPriceReferenceOperand(50, EUR)));
		conditionsC1.add(createExpression(createProductsInOrdersOperand(5, "C1C3L"), createOperator("contains"),
				createProductsOperand(AUGISTINER)));

		final BTGRuleModel ruleC1 = createRule(staged, conditionsC1, "testRuleC", BTGRuleType.ORDER);
		final BTGRuleModel ruleC2 = createRule(staged, Collections.singletonList(createExpression(
				createOperandModel("products in cart C", BTGProductsInCartOperandModel.class), createOperator("contains"),
				createProductsOperand(AUGISTINER))), "testRule", BTGRuleType.ORDER);
		segmentC = createSegment(BTGConditionEvaluationScope.ONLINE, SEGMENTC, staged);
		segmentC.setSites(Collections.singletonList(websiteA));
		segmentC.setRules(Arrays.asList(ruleC1, ruleC2));
		segmentC.setActive(Boolean.TRUE);
		segmentC.setOutputActions(Collections.singletonList(createAssign2GroupAction(staged, goodTaste, "segmentCOutputAction")));
		modelService.save(segmentC);
	}

	/**
	 * @param catVersion
	 * @param userGrp
	 * @param code
	 *           YTODO
	 * 
	 */
	private AbstractActionModel createAssign2GroupAction(final CatalogVersionModel catVersion, final UserGroupModel userGrp,
			final String code)
	{
		final BTGAssignToGroupDefinitionModel assignToGroupAction = modelService.create(BTGAssignToGroupDefinitionModel.class);
		assignToGroupAction.setUid(UUID.randomUUID().toString());
		assignToGroupAction.setCatalogVersion(catVersion);

		final List<UserGroupModel> userGroups = new ArrayList();
		userGroups.add(userGrp);
		assignToGroupAction.setUserGroups(userGroups);
		assignToGroupAction.setCode(code);
		modelService.save(assignToGroupAction);
		return assignToGroupAction;
	}

	protected void setCurrencies()
	{
		eur = i18nService.getCurrency(EUR);
		usd = i18nService.getCurrency(USD);
	}

	protected void placeAdditionalOrders() throws Exception
	{
		placeRandomOrdersNotFulfillingConditions(30, customerA);
		placeRandomOrdersNotFulfillingConditions(30, customerB);
		placeRandomOrdersNotFulfillingConditions(30, customerC);
	}


	protected void setWebSites()
	{
		websiteA = cmsSiteDao.findCMSSiteById(WEBSITE_A);
		websiteB = cmsSiteDao.findCMSSiteById(WEBSITE_B);
		websiteC = cmsSiteDao.findCMSSiteById(WEBSITE_C);
	}


	protected void setCustomersAndUserGroups()
	{
		customerA = (CustomerModel) userService.getUserForUID(USER_A);
		customerB = (CustomerModel) userService.getUserForUID(USER_B);
		customerC = (CustomerModel) userService.getUserForUID(USER_C);
		anonymous = userService.getUserForUID("anonymous");

		final AddressModel addressA = customerA.getAddresses().iterator().next();
		final AddressModel addressB = customerB.getAddresses().iterator().next();
		final AddressModel addressC = customerC.getAddresses().iterator().next();

		final Calendar cal = Utilities.getDefaultCalendar();
		cal.add(Calendar.YEAR, -20);
		//customerA is 20YR old
		addressA.setDateOfBirth(cal.getTime());
		cal.add(Calendar.YEAR, -20);
		//customerB is 40YR old
		addressB.setDateOfBirth(cal.getTime());
		cal.add(Calendar.YEAR, -20);
		//customerC is 60YR old
		addressC.setDateOfBirth(cal.getTime());

		modelService.save(addressA);
		modelService.save(addressB);
		modelService.save(addressC);

		goodTaste = userService.getUserGroupForUID(GOODTASTE);
		badTaste = userService.getUserGroupForUID(BADTASTE);
	}

	protected void setCatalogVersions()
	{
		online = catalogService.getCatalogVersion(DRINKS, ONLINE);
		staged = catalogService.getCatalogVersion(DRINKS, STAGED);
	}

	protected void placeRandomOrdersNotFulfillingConditions(final int number, final UserModel customer) throws Exception
	{
		final UserModel currentUser = userService.getCurrentUser();
		try
		{
			userService.setCurrentUser(customer);
			for (int i = 0; i < number; i++)
			{
				final int productIndex = i % 10;
				placeTestOrder(NONFULFILLING_PRODUCT_TEMPLATE + productIndex, NONFULFILLING_PRODUCT_TEMPLATE
						+ rotate(productIndex, 4));
			}
		}
		finally
		{
			userService.setCurrentUser(currentUser);
		}
	}

	protected void placeRandomOrdersTotalSum(final int number, final UserModel customer) throws Exception
	{
		final UserModel currentUser = userService.getCurrentUser();
		try
		{
			userService.setCurrentUser(customer);
			for (int i = 0; i < number; i++)
			{
				final int productIndex = i % 10;
				placeTestOrder(NONFULFILLING_PRODUCT_TEMPLATE + productIndex, NONFULFILLING_PRODUCT_TEMPLATE
						+ rotate(productIndex, 4));
			}
		}
		finally
		{
			userService.setCurrentUser(currentUser);
		}
	}

	private int rotate(final int productIndex, final int jump)
	{
		return productIndex < jump ? (productIndex + 10) - jump : productIndex - jump;
	}


	protected BTGOperatorModel createOperator(final String operatorString)
	{
		final BTGOperatorModel operator = expressionService.createOperator(operatorString, getDrinksOnlineCatalogVersion());
		modelService.save(operator);
		return operator;
	}

	protected BTGConditionModel createExpression(final BTGOperandModel leftOperand, final BTGOperatorModel operator,
			final BTGOperandModel rightOperand)
	{
		return expressionService.createExpression(leftOperand, operator, rightOperand, getDrinksOnlineCatalogVersion());
	}

	private <T extends BTGLastOrdersOperandModel> T createOrderOperand(final Class<T> clazz, final Integer lastOrders,
			final String name)
	{
		final T operand = (T) modelService.create(clazz);
		operand.setCode(name);
		operand.setScope(BTGConditionEvaluationScope.OFFLINE);
		operand.setLastOrders(lastOrders);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected <T extends BTGLastOrdersOperandModel> T createOrderOperand(final Class<T> clazz, final int lastOrders,
			final String name)
	{
		return createOrderOperand(clazz, Integer.valueOf(lastOrders), name);
	}

	protected <T extends BTGLastOrdersOperandModel> T createOrderOperand(final Class<T> clazz, final String name)
	{
		return createOrderOperand(clazz, null, name);
	}

	protected BTGAbstractLiteralOperandModel createGenderLiteralOperand(final Gender literalValue)
	{
		final BTGGenderEnumLiteralOperandModel operand = modelService.create(BTGGenderEnumLiteralOperandModel.class);
		operand.setCode("genderLireral");
		operand.setScope(BTGConditionEvaluationScope.OFFLINE);
		operand.setLiteral(literalValue);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGAbstractLiteralOperandModel createDoubleLiteralOperand(final Double literalValue)
	{
		final BTGDoubleLiteralOperandModel operand = modelService.create(BTGDoubleLiteralOperandModel.class);
		operand.setCode("doubleLireral");
		operand.setScope(BTGConditionEvaluationScope.OFFLINE);
		operand.setLiteral(literalValue);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;

	}



	protected BTGAbstractLiteralOperandModel createIntLiteralOperand(final Integer literalValue)
	{
		final BTGIntegerLiteralOperandModel operand = modelService.create(BTGIntegerLiteralOperandModel.class);
		operand.setCode("intLireral");
		operand.setScope(BTGConditionEvaluationScope.OFFLINE);
		operand.setLiteral(literalValue);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGAbstractLiteralOperandModel createStringLiteralOperand(final String literalValue)
	{
		final BTGStringLiteralOperandModel operand = modelService.create(BTGStringLiteralOperandModel.class);
		operand.setCode("stringLireral");
		operand.setScope(BTGConditionEvaluationScope.OFFLINE);
		operand.setLiteral(literalValue);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGAbstractLiteralOperandModel createBooleanLiteralOperand(final boolean value)
	{
		final BTGBooleanLiteralOperandModel operand = modelService.create(BTGBooleanLiteralOperandModel.class);
		operand.setCode("booleanLireral");
		operand.setScope(BTGConditionEvaluationScope.OFFLINE);
		operand.setLiteral(Boolean.valueOf(value));
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGProductsInOrdersOperandModel createProductsInOrdersOperand(final int lastOrders, final String name)
	{
		final BTGProductsInOrdersOperandModel operand = modelService.create(BTGProductsInOrdersOperandModel.class);
		operand.setCode(name);
		operand.setScope(BTGConditionEvaluationScope.OFFLINE);
		operand.setLastOrders(Integer.valueOf(lastOrders));
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGReferencePriceOperandModel createPriceReferenceOperand(final double value, final String currencyIso)
	{
		final BTGReferencePriceOperandModel operand = modelService.create(BTGReferencePriceOperandModel.class);
		operand.setCode("currtencyAwareOperand");
		operand.setScope(BTGConditionEvaluationScope.OFFLINE);
		operand.setCurrency(i18nService.getCurrency(currencyIso));
		operand.setValue(Double.valueOf(value));
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGReferenceProductsOperandModel createProductsOperand(final String... products)
	{
		final BTGReferenceProductsOperandModel operand = modelService.create(BTGReferenceProductsOperandModel.class);
		operand.setCode("referenceProductsOperand");
		operand.setScope(BTGConditionEvaluationScope.OFFLINE);
		operand.setProducts(prepareProductCollection(products));
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}


	private Collection<ProductModel> prepareProductCollection(final String... products)
	{
		final List<ProductModel> result = new ArrayList<ProductModel>();
		for (final String productName : products)
		{
			result.add(productService.getProduct(productName));
		}
		return result;
	}

	private Collection<CategoryModel> prepareCategoriesCollection(final String... categories)
	{
		final List<CategoryModel> result = new ArrayList<CategoryModel>();
		for (final String categoryName : categories)
		{
			result.add(categoryService.getCategory(categoryName));
		}
		return result;
	}

	private Collection<CountryModel> prepareCountriesCollection(final String... isocodes)
	{
		final List<CountryModel> result = new ArrayList<CountryModel>();
		for (final String isocode : isocodes)
		{
			result.add(i18nService.getCountry(isocode));
		}
		return result;
	}

	protected BTGCategoriesInOrdersOperandModel createCategoriesinOrderOperand(final String name, final boolean withSuperCat,
			final int lastOrders)
	{
		final BTGCategoriesInOrdersOperandModel operand = modelService.create(BTGCategoriesInOrdersOperandModel.class);
		operand.setCode(name);
		operand.setScope(BTGConditionEvaluationScope.ONLINE);
		operand.setLastOrders(Integer.valueOf(lastOrders));
		operand.setWithSupercategories(Boolean.valueOf(withSuperCat));
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGReferenceCategoriesOperandModel createBTGReferenceCategoriesOperand(final String... catNames)
	{
		final BTGReferenceCategoriesOperandModel operand = modelService.create(BTGReferenceCategoriesOperandModel.class);
		operand.setCode("catOperand");
		operand.setScope(BTGConditionEvaluationScope.ONLINE);
		operand.setCategories(prepareCategoriesCollection(catNames));
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGReferenceCountriesOperandModel createBTGReferenceCountriesOperandModel(final String... isoCodes)
	{
		final BTGReferenceCountriesOperandModel operand = modelService.create(BTGReferenceCountriesOperandModel.class);
		operand.setCode("referenceCountriesOperand");
		operand.setScope(BTGConditionEvaluationScope.ONLINE);
		operand.setCountries(prepareCountriesCollection(isoCodes));
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGLastOrderDateOperandModel createLastOrderDateOperand(final String name)
	{
		final BTGLastOrderDateOperandModel operand = modelService.create(BTGLastOrderDateOperandModel.class);
		operand.setCode(name);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGOperandModel createReferenceDateOperand(final String name, final IntervalResolution unit, final Integer value)
	{
		final BTGReferenceDateOperandModel operand = modelService.create(BTGReferenceDateOperandModel.class);
		operand.setCode(name);
		operand.setUnit(unit);
		operand.setValue(value);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGReferenceExactDateOperandModel createReferenceExactDateOperand(final String name, final Date fromDate)
	{
		final BTGReferenceExactDateOperandModel operand = modelService.create(BTGReferenceExactDateOperandModel.class);
		operand.setCode(name);
		operand.setReferenceDate(fromDate);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGNumberOfOrdersOperandModel createNumberOfOrdersOperand(final Date operantFrom, final Date operantTo,
			final String name)
	{
		final BTGNumberOfOrdersOperandModel operand = modelService.create(BTGNumberOfOrdersOperandModel.class);
		operand.setCode(name);
		operand.setFrom(operantFrom);
		operand.setTo(operantTo);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(online);
		modelService.save(operand);
		return operand;
	}

	protected BTGOperandModel createOperandModel(final String name, final Class clazz)
	{
		final BTGOperandModel operand = modelService.create(clazz);
		operand.setCode(name);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGRuleModel createRule(final CatalogVersionModel catVersion, final Collection<BTGConditionModel> conditions,
			final String code, final BTGRuleType ruleType)
	{
		final BTGRuleModel rule = modelService.create(BTGRuleModel.class);
		rule.setCatalogVersion(catVersion);
		rule.setUid(UUID.randomUUID().toString());
		rule.setConditions(conditions);
		rule.setCode(code);
		rule.setRuleType(ruleType);
		modelService.save(rule);
		return rule;
	}

	protected void removeUsersOrders(final UserModel user)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {pk} FROM {Order} where {user}=?user");
		query.addQueryParameter("user", user);
		final SearchResult<OrderModel> result = flexibleSearchService.search(query);
		for (final OrderModel order : result.getResult())
		{
			modelService.remove(order);
		}
	}

	protected BTGUserCountryOperandModel createCustomerCountryOperand(final String code)
	{
		final BTGUserCountryOperandModel operand = modelService.create(BTGUserCountryOperandModel.class);
		operand.setCode(code);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGUserAddressPostalCodeOperandModel createCustomerPostalCodeOperand(final String code)
	{
		final BTGUserAddressPostalCodeOperandModel operand = modelService.create(BTGUserAddressPostalCodeOperandModel.class);
		operand.setCode(code);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected AttributeDescriptorModel getAttributeDescriptor(final Class modelClass, final String attributeQualifier)
	{
		return typeService.getAttributeDescriptor(typeService.getComposedType(modelClass), attributeQualifier);
	}

	protected BTGUserGenderOperandModel createCustomerGenderOperand(final String code)
	{
		final BTGUserGenderOperandModel operand = modelService.create(BTGUserGenderOperandModel.class);
		operand.setCode(code);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGQuantityOfProductInCartOperandModel createQuantityOfProductInCartOperand(final String code,
			final String productName)
	{
		return createQuantityOfProductInCartOperand(code, getDrinksOnlineCatalogVersion(), productName);
	}

	protected BTGQuantityOfProductInCartOperandModel createQuantityOfProductInCartOperand(final String code,
			final String catalogVersionName, final String productName)
	{
		return createQuantityOfProductInCartOperand(code, getDrinksCatalogVersion(catalogVersionName), productName);
	}

	private BTGQuantityOfProductInCartOperandModel createQuantityOfProductInCartOperand(final String code,
			final CatalogVersionModel catalogVersion, final String productName)
	{
		final BTGQuantityOfProductInCartOperandModel operand = modelService.create(BTGQuantityOfProductInCartOperandModel.class);
		operand.setCode(code);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(catalogVersion);
		operand.setProduct(productService.getProduct(catalogVersion, productName));
		modelService.save(operand);
		return operand;
	}

	protected void placeOrdersForSegmentA(final UserModel customer) throws Exception
	{
		userService.setCurrentUser(customer);
		//15 orders for user
		//5 random orders
		placeRandomOrdersNotFulfillingConditions(5, customer);
		//10 of total sum > 1000 and each of them > 50
		placeTestOrder(EXPENSIVE_150, NONFULFILLING_PRODUCT_TEMPLATE + 4);
		placeTestOrder(EXPENSIVE_60);
		placeTestOrder(EXPENSIVE_150, TYSKIE);
		placeTestOrder(EXPENSIVE_40, EXPENSIVE_60);
		placeTestOrder(EXPENSIVE_150);
		//Last 5 order contain a product with code = 'augustinerhelles'
		placeTestOrder(EXPENSIVE_150, AUGISTINER);
		placeTestOrder(EXPENSIVE_100);
		placeTestOrder(EXPENSIVE_150);
		placeTestOrder(EXPENSIVE_60, HACKERPSCHORR);
		placeTestOrder(EXPENSIVE_150);
	}


	protected void placeOrdersForSegmentB(final CustomerModel customer) throws Exception
	{
		userService.setCurrentUser(customer);
		//place 5 random orders for customerB
		placeRandomOrdersNotFulfillingConditions(5, customer);
		//Last 5 order contain a product with code = 'HACKERPSCHORR'
		placeTestOrder(EXPENSIVE_150, HACKERPSCHORR);
		placeTestOrder(EXPENSIVE_40);
		placeTestOrder(NONFULFILLING_PRODUCT_TEMPLATE + 3);
		placeTestOrder(EXPENSIVE_60, TYSKIE);
		placeTestOrder(EXPENSIVE_150);
	}

	protected void placeOrdersForUserC() throws Exception
	{
		userService.setCurrentUser(customerC);
		//15 orders for user
		//5 random orders
		placeRandomOrdersNotFulfillingConditions(5, customerC);
		//10 of total sum > 1000 and each of them > 50
		placeTestOrder(EXPENSIVE_150, NONFULFILLING_PRODUCT_TEMPLATE + 4);
		placeTestOrder(EXPENSIVE_60, EXPENSIVE_150);
		placeTestOrder(EXPENSIVE_150, TYSKIE, EXPENSIVE_100, EXPENSIVE_60);
		placeTestOrder(EXPENSIVE_40, EXPENSIVE_60, EXPENSIVE_150);
		placeTestOrder(EXPENSIVE_150, EXPENSIVE_100);
		//Last 5 order contain a product with code = 'augustinerhelles'
		placeTestOrder(EXPENSIVE_150, AUGISTINER, EXPENSIVE_100);
		placeTestOrder(EXPENSIVE_100, EXPENSIVE_150);
		placeTestOrder(EXPENSIVE_150, EXPENSIVE_100);
		placeTestOrder(EXPENSIVE_60, HACKERPSCHORR, EXPENSIVE_150);
		placeTestOrder(EXPENSIVE_150, EXPENSIVE_100);
	}

	protected BTGConditionModel createExpressionInRuleAndSegment(final BTGOperandModel leftOperand,
			final BTGOperatorModel operator, final BTGOperandModel rightOperand)
	{
		final BTGConditionModel orderExpression = createExpression(leftOperand, operator, rightOperand);
		final BTGRuleModel rule = createRule("testRule");
		final BTGSegmentModel segment = createSegment();
		rule.setSegment(segment);
		orderExpression.setRule(rule);

		//YTODO : change to getting from conditionFactory, when availiable:
		modelService.save(orderExpression);
		return orderExpression;
	}

}
