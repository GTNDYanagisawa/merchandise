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
package de.hybris.platform.webservices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.jalo.Catalog;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.jalo.Company;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttribute;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttributeUnit;
import de.hybris.platform.catalog.jalo.classification.ClassificationClass;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystem;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystemVersion;
import de.hybris.platform.catalog.jalo.classification.util.Feature;
import de.hybris.platform.catalog.jalo.classification.util.FeatureContainer;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.jalo.CategoryManager;
import de.hybris.platform.core.Constants;
import de.hybris.platform.core.MasterTenant;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.AdvancePaymentInfoModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.enums.ErrorMode;
import de.hybris.platform.cronjob.enums.JobLogLevel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.embeddedserver.api.EmbeddedServer;
import de.hybris.platform.embeddedserver.api.EmbeddedServerBuilder;
import de.hybris.platform.embeddedserver.base.EmbeddedExtension;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Country;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.order.delivery.DeliveryMode;
import de.hybris.platform.jalo.order.payment.PaymentMode;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.jalo.user.Address;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.Employee;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserGroup;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.lucenesearch.model.UpdateIndexJobModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.i18n.I18NDao;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.RootRequestFilter;
import de.hybris.platform.util.Utilities;
import de.hybris.platform.webservices.functional.Case1Test;
import de.hybris.platform.webservices.impl.DefaultWsUtilService;


/**
 * @author andreas.thaler http://blogs.sun.com/enterprisetechtips/entry/consuming_restful_web_services_with
 *         http://blogs.sun.com/naresh/entry/jersey_test_framework_makes_it
 */
@Ignore
public abstract class AbstractWebServicesTest extends ServicelayerTest//NOPMD
{
	public static final String WS_VERSION;

	// when enabled embedded server and junit-tenant is used
	// when disabled embedded server is only used, when no junit-tenant is active (when Tomcat isn't running)
	private static int EMBEDDED_SERVER_PORT = 9998;

	private static final Logger LOG = Logger.getLogger(AbstractWebServicesTest.class);
	protected static final boolean INTERNAL_SERVER = true;
	protected static final String HEADER_AUTH_KEY = "Authorization";
	protected static final String HEADER_AUTH_VALUE_BASIC_ADMIN = "Basic YWRtaW46bmltZGE=";
	protected static final String HEADER_CONTENT_TYPE = "Content-Type";

	/** Holds default location for impex files */
	protected static final String CSV_RESOURCE_DIR = "/test/";

	/** Holds default extension for impex files */
	protected static final String CSV_EXTENSION = ".csv";
	protected static final int METHOD_NOT_ALLOWED = 405;


	protected static final String GET = "GET";
	protected static final String PUT = "PUT";
	protected static final String POST = "POST";
	protected static final String DELETE = "DELETE";

	@Resource
	protected CartService cartService;
	@Resource
	protected ModelService modelService;
	@Resource
	protected UserService userService;
	@Resource
	protected ProductService productService;
	@Resource
	protected CatalogService catalogService;
	@Resource
	protected I18NService i18nService;
	@Resource
	protected CronJobService cronJobService;
	@Resource
	protected MediaService mediaService;
	@Resource
	protected FlexibleSearchService flexibleSearchService;
	@Resource
	protected TypeService typeService;
	@Resource
	private EmbeddedServerBuilder tomcatEmbeddedServerBuilder;


	protected I18NDao i18NDao = (I18NDao) Registry.getApplicationContext().getBean("i18nDao");
	protected WsUtilService wsUtilService = null;

	protected Cookie tenantCookie;

	protected WebResource webResource;

	protected Client jerseyClient;

	private String host = null;
	private int port = 0;

	static
	{
		String currentTenantId = Registry.getCurrentTenant().getTenantID();
		String wsVersion = "ws410";
		if (!MasterTenant.MASTERTENANT_ID.equalsIgnoreCase(currentTenantId))
			wsVersion += "_" + currentTenantId;
		WS_VERSION = wsVersion;
	}

	public AbstractWebServicesTest()
	{
		super();
		final String key = "webservices.test";
		this.host = Registry.getCurrentTenant().getConfig().getString(key + ".host", "localhost");
		this.port = Registry.getCurrentTenant().getConfig().getInt(key + ".port", 9001);
	}



	@Before
	public void setUp() throws Exception
	{
		ensureEmbeddedServerIsRunning();
		final ClientConfig config = new DefaultClientConfig();

		jerseyClient = Client.create(config);
		jerseyClient.addFilter(new LoggingFilter());

		if (INTERNAL_SERVER)
		{
			LOG.info("Using internal embedded server with master tenant and port " + EMBEDDED_SERVER_PORT);
			final String tenant = Registry.getCurrentTenant().getTenantID();
			tenantCookie = new Cookie(RootRequestFilter.DEFAULT_TENANTID_COOKIE_NAME, tenant);
			webResource = jerseyClient.resource(UriBuilder.fromUri("http://localhost/").port(EMBEDDED_SERVER_PORT).path(WS_VERSION)
					.path("rest").build());
		}
		else
		{

			LOG.info("Using external server http://" + this.host + ":" + this.port + "/ with junit tenant");
			tenantCookie = new Cookie(RootRequestFilter.DEFAULT_TENANTID_COOKIE_NAME, "junit");

			webResource = jerseyClient.resource(UriBuilder.fromUri("http://" + this.host + "/").port(this.port).path(WS_VERSION)
					.path("rest").build());
		}
		ensureWebappIsRunning();
		//import users access rights
		importCSVFromResources("Users_access_rights");
	}

	private static EmbeddedServer embeddedServer;

	private void ensureEmbeddedServerIsRunning()
	{
		if (embeddedServer == null)
		{
			EmbeddedServerBuilder builder = getEmbeddedServerBuilder();
			embeddedServer = builder.needEmbeddedServer().runningOnPort(EMBEDDED_SERVER_PORT)
					.withApplication(new EmbeddedExtension(Utilities.getExtensionInfo("platformwebservices")).withContext(WS_VERSION))
					.build();
			embeddedServer.start();
		}

	}

	private EmbeddedServerBuilder getEmbeddedServerBuilder()
	{
		return tomcatEmbeddedServerBuilder;
	}

	private void ensureWebappIsRunning()
	{
		webResource.head();
	}

	protected void createTestCatalogs() throws ConsistencyCheckException
	{
		final Language lang1 = C2LManager.getInstance().createLanguage("testLang1");

		final Catalog catalog1 = CatalogManager.getInstance().createCatalog("testCatalog1");
		CatalogManager.getInstance().createCatalog("testCatalog2");

		final CatalogVersion version1 = CatalogManager.getInstance().createCatalogVersion(catalog1, "testVersion1", lang1);
		CatalogManager.getInstance().createCatalogVersion(catalog1, "testVersion2", lang1);

		final Category category1 = CategoryManager.getInstance().createCategory("testCategory1");
		CatalogManager.getInstance().setCatalogVersion(category1, version1);
		final Category category2 = CategoryManager.getInstance().createCategory("testCategory2");
		CatalogManager.getInstance().setCatalogVersion(category2, version1);
		category1.addToCategories(category2);
		version1.setRootCategories(Collections.singletonList(category1));

	}

	protected void createTestClassificationSystem() throws ConsistencyCheckException
	{
		final Language lang1 = C2LManager.getInstance().getLanguageByIsoCode("testLang1");
		final Category category2 = CategoryManager.getInstance().getCategoriesByCode("testCategory2").iterator().next();

		//Create a Classification System
		final ClassificationSystem testCS = CatalogManager.getInstance().createClassificationSystem("ClassificationSystem1");
		final ClassificationSystemVersion testCSVersion = testCS.createSystemVersion("v1", lang1);
		final ClassificationClass cc1 = testCSVersion.createClass("testClassificationCategory");
		final ClassificationClass cc2 = testCSVersion.createClass(cc1, "testClassificationSubCategory");


		//Create and assign an attribute to a Classification System
		final ClassificationAttribute testAttrString = testCSVersion.createClassificationAttribute("testAttrString");
		testAttrString.setName("testAttrString name");

		final ClassificationAttribute testAttrNumber = testCSVersion.createClassificationAttribute("testAttrNumber");
		testAttrNumber.setName("testAttrNumber name");

		final ClassificationAttributeUnit testAttrUnit1 = testCSVersion.createAttributeUnit("testUnit1", "testUnit1");
		final ClassificationAttributeUnit testAttrUnit2 = testCSVersion.createAttributeUnit("testUnit2", "testUnit2");

		cc1.assignAttribute(testAttrString, // attribute
				CatalogConstants.Enumerations.ClassificationAttributeTypeEnum.STRING, // value type
				testAttrUnit1, // unit
				Collections.EMPTY_LIST, // selectable values
				0 // attribute position since they're orderd
		);

		cc2.assignAttribute(testAttrNumber, // attribute
				CatalogConstants.Enumerations.ClassificationAttributeTypeEnum.NUMBER, // value type
				testAttrUnit2, // unit
				Collections.EMPTY_LIST, // selectable values
				0 // attribute position since they're orderd
		);

		//Attach classification to category hierarchy
		category2.addToSupercategories(cc2);
	}

	protected void createProductFeatures(final Product product) throws ConsistencyCheckException
	{
		//********** Classification API part **********
		final Category testCategory2 = CategoryManager.getInstance().getCategoriesByCode("testCategory2").iterator().next();

		//By assigning product1 to testCategory2 we are also linking product1 with a Classification System!
		testCategory2.addProduct(product);

		final FeatureContainer testContainer = FeatureContainer.loadTyped(product);

		final Feature testAttrString = testContainer
				.getFeature("ClassificationSystem1/v1/testClassificationCategory.testAttrString");
		testAttrString.setValue("testValue1234");

		final Feature testAttrNumber = testContainer
				.getFeature("ClassificationSystem1/v1/testClassificationSubCategory.testAttrNumber");
		testAttrNumber.setValue(Double.valueOf(2.71));

		testContainer.store();
	}

	protected void createTestCompanies() throws ConsistencyCheckException, JaloBusinessException
	{
		final Country country1 = C2LManager.getInstance().createCountry("testCountry1");
		country1.setName("country1");
		final Country country2 = C2LManager.getInstance().createCountry("testCountry2");
		country2.setName("country2");

		final Company company1 = CatalogManager.getInstance().createCompany("testCompany1");
		company1.setName("testCompany1 name");
		company1.setDescription("testCompany1 description");
		company1.setVatID("T1 123456789");
		company1.setCountry(country1);

		final Address address1 = UserManager.getInstance().createAddress(company1);
		address1.setAttribute(Address.STREETNAME, "street1");
		final Address address2 = UserManager.getInstance().createAddress(company1);
		address2.setAttribute(Address.STREETNAME, "street2");

		final Company company2 = CatalogManager.getInstance().createCompany("testCompany2");
		company2.setName("testCompany2 name");
		company2.setDescription("testCompany2 description");
		company2.setVatID("T2 123456789");
		company2.setCountry(country1);

		final Company company3 = CatalogManager.getInstance().createCompany("testCompany3");
		company3.setName("testCompany3 name");
		company3.setDescription("testCompany3 description");
		company3.setVatID("T3 123456789");
		company3.setCountry(country1);
	}

	//Used also for Region(s) testing
	protected void createTestCountries() throws ConsistencyCheckException
	{
		final CountryModel country1 = modelService.create(CountryModel.class);
		country1.setIsocode("testCountry1");
		country1.setName("name");

		final CountryModel country2 = modelService.create(CountryModel.class);
		country2.setIsocode("testCountry2");
		country2.setName("name");

		final RegionModel region1 = modelService.create(RegionModel.class);
		region1.setCountry(country1);
		region1.setIsocode("testRegion1");
		region1.setName("testRegion1 name");

		final RegionModel region2 = modelService.create(RegionModel.class);
		region2.setCountry(country1);
		region2.setIsocode("testRegion2");
		region2.setName("testRegion2 name");

		modelService.saveAll(country1, country2, region1, region2);
	}



	/**
	 * Depends on {@link #createTestCatalogs()}.
	 */
	protected void createTestMedias() throws ConsistencyCheckException, JaloBusinessException
	{
		final CatalogVersion version = CatalogManager.getInstance().getCatalog("testCatalog1").getCatalogVersion("testVersion1");
		final Media media1 = MediaManager.getInstance().createMedia("testMedia1");
		CatalogManager.getInstance().setCatalogVersion(media1, version);
		media1.setAltText("alttext");
		media1.setDescription("description");

		media1.setData(new DataInputStream(new ByteArrayInputStream("test".getBytes())), "foo.txt", (String) null);

		final Media media2 = MediaManager.getInstance().createMedia("testMedia2");
		CatalogManager.getInstance().setCatalogVersion(media2, version);
	}

	protected void createTestCustomers() throws ConsistencyCheckException, JaloBusinessException
	{

		final Language polishLanguage = C2LManager.getInstance().createLanguage("pl");
		//Create "Czech" language for testing
		C2LManager.getInstance().createLanguage("cs");

		final UserGroup userGroup1 = UserManager.getInstance().createUserGroup("testCustomerGroup1");
		final UserGroup userGroup2 = UserManager.getInstance().createUserGroup("testCustomerGroup2");
		final Customer customer1 = UserManager.getInstance().createCustomer("testCustomer1");
		customer1.setName("name1");
		customer1.setSessionLanguage(polishLanguage);
		customer1.setCustomerID("id1");
		customer1.setGroups(new HashSet(Arrays.asList(userGroup1, userGroup2)));

		final Customer customer2 = UserManager.getInstance().createCustomer("testCustomer2");
		customer2.setName("name2");
		customer2.setSessionLanguage(polishLanguage);
		customer2.setCustomerID("id2");

		final Employee employee1 = UserManager.getInstance().createEmployee("testEmployee1");
		employee1.setName("employee_name1");
		employee1.setSessionLanguage(polishLanguage);

		final Employee employee2 = UserManager.getInstance().createEmployee("testEmployee2");
		employee2.setName("employee_name2");
		employee2.setSessionLanguage(C2LManager.getInstance().getLanguageByIsoCode("cs"));

		final Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(User.PASSWORDQUESTION, "My question1");
		paramMap.put(User.PASSWORDANSWER, "My answer1");
		((User) customer1).setAllAttributes(paramMap);
		paramMap.clear();

		paramMap.put(User.PASSWORDQUESTION, "My question2");
		paramMap.put(User.PASSWORDANSWER, "My answer2");
		((User) customer2).setAllAttributes(paramMap);
		paramMap.clear();

		paramMap.put(User.PASSWORDQUESTION, "My question3");
		paramMap.put(User.PASSWORDANSWER, "My answer3");
		((User) employee1).setAllAttributes(paramMap);
		paramMap.clear();

		paramMap.put(User.PASSWORDQUESTION, "My question4");
		paramMap.put(User.PASSWORDANSWER, "My answer4");
		((User) employee2).setAllAttributes(paramMap);
		paramMap.clear();

		final Address address1 = UserManager.getInstance().createAddress(customer1);
		address1.setAttribute(Address.STREETNAME, "street1");
		final Address address2 = UserManager.getInstance().createAddress(customer1);
		address2.setAttribute(Address.STREETNAME, "street2");
		final Address address3 = UserManager.getInstance().createAddress(employee1);
		address3.setAttribute(Address.STREETNAME, "street3");
		final Address address4 = UserManager.getInstance().createAddress(employee2);
		address4.setAttribute(Address.STREETNAME, "street4");
	}

	protected void configureTestUsers() throws ConsistencyCheckException, JaloBusinessException
	{

		final Language polishLanguage = C2LManager.getInstance().createLanguage("pl");
		//Create "Czech" language for testing
		C2LManager.getInstance().createLanguage("cs");

		final Customer customer1 = (Customer) UserManager.getInstance().getUserByLogin("testCustomer1");
		customer1.setName("name1");
		customer1.setSessionLanguage(polishLanguage);
		customer1.setCustomerID("id1");

		final Customer customer2 = (Customer) UserManager.getInstance().getUserByLogin("testCustomer2");
		customer2.setName("name2");
		customer2.setSessionLanguage(polishLanguage);
		customer2.setCustomerID("id2");

		final Employee employee1 = (Employee) UserManager.getInstance().getUserByLogin("testEmployee1");
		employee1.setName("employee_name1");
		employee1.setSessionLanguage(polishLanguage);

		final Employee employee2 = (Employee) UserManager.getInstance().getUserByLogin("testEmployee2");
		employee2.setName("employee_name2");
		employee2.setSessionLanguage(C2LManager.getInstance().getLanguageByIsoCode("cs"));

		final Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(User.PASSWORD_QUESTION, "My question1");
		paramMap.put(User.PASSWORD_ANSWER, "My answer1");
		((User) customer1).setAllAttributes(paramMap);
		paramMap.clear();

		paramMap.put(User.PASSWORD_QUESTION, "My question2");
		paramMap.put(User.PASSWORD_ANSWER, "My answer2");
		((User) customer2).setAllAttributes(paramMap);
		paramMap.clear();

		paramMap.put(User.PASSWORD_QUESTION, "My question3");
		paramMap.put(User.PASSWORD_ANSWER, "My answer3");
		((User) employee1).setAllAttributes(paramMap);
		paramMap.clear();

		paramMap.put(User.PASSWORD_QUESTION, "My question4");
		paramMap.put(User.PASSWORD_ANSWER, "My answer4");
		((User) employee2).setAllAttributes(paramMap);
		paramMap.clear();

		final Address address1 = UserManager.getInstance().createAddress(customer1);
		address1.setAttribute(Address.STREETNAME, "street1");
		final Address address2 = UserManager.getInstance().createAddress(customer2);
		address2.setAttribute(Address.STREETNAME, "street2");
		final Address address3 = UserManager.getInstance().createAddress(customer2);
		address3.setAttribute(Address.STREETNAME, "street3");
		final Address address4 = UserManager.getInstance().createAddress(employee1);
		address4.setAttribute(Address.STREETNAME, "street4");
		final Address address5 = UserManager.getInstance().createAddress(employee2);
		address5.setAttribute(Address.STREETNAME, "street5");
	}

	/**
	 * Depends on {@link #createTestCatalogs()}.
	 * 
	 * @throws ConsistencyCheckException
	 */
	protected void createTestProductsUnits() throws ConsistencyCheckException
	{
		final CatalogManager cmi = CatalogManager.getInstance();
		final CatalogVersion version = cmi.getCatalog("testCatalog1").getCatalogVersion("testVersion1");
		final Product product1 = ProductManager.getInstance().createProduct("testProduct1");
		final Unit unit1 = ProductManager.getInstance().createUnit("packaging-testUnit1", "testUnit1");
		final Unit unit2 = ProductManager.getInstance().createUnit("packaging-testUnit2", "testUnit2");
		product1.setDescription("description");
		product1.setName("product1");
		cmi.setCatalogVersion(product1, version);
		cmi.setOrderQuantityInterval(product1, 1);
		cmi.setOrder(product1, 12);
		cmi.setRemarks(product1, "Remark1");
		final Date date = new Date();
		cmi.setOnlineDate(product1, date);
		cmi.setOfflineDate(product1, date);
		cmi.setManufacturerAID(product1, "manufacturerAID");
		cmi.setErpGroupBuyer(product1, "erpGroupBuyer");
		cmi.setNumberContentUnits(product1, 1.0);
		cmi.setDeliveryTime(product1, 20.0);
		cmi.setPriceQuantity(product1, 2.0);
		cmi.setEan(product1, "ean");
		cmi.setMaxOrderQuantity(product1, 20);
		cmi.setErpGroupSupplier(product1, "erpGroupSupplier");
		cmi.setMinOrderQuantity(product1, 1);
		cmi.setSegment(product1, "segment");
		cmi.setManufacturerTypeDescription(product1, "manufacturerTypeDescription");
		cmi.setManufacturerName(product1, "manufacturerName");
		cmi.setSupplierAlternativeAID(product1, "supplierAlternativeAID");
		product1.setUnit(unit1);
		unit1.setName("testBulk, testLiquid");
		unit1.setConversionFactor(2.0);
		cmi.setContentUnit(product1, unit2);

		final Product product2 = ProductManager.getInstance().createProduct("testProduct2");
		CatalogManager.getInstance().setCatalogVersion(product2, version);
	}

	protected void createTestPaymentInfos()
	{
		final CustomerModel customerModel = (CustomerModel) userService.getUserForUID("testCustomer1");
		final DebitPaymentInfoModel debitPaymentModel = modelService.create(DebitPaymentInfoModel.class);
		debitPaymentModel.setCode("debit1");
		debitPaymentModel.setDuplicate(Boolean.FALSE);
		debitPaymentModel.setBank("bank1");
		debitPaymentModel.setBankIDNumber("123456789");
		debitPaymentModel.setAccountNumber("1000");
		debitPaymentModel.setBaOwner(customerModel.getName());
		debitPaymentModel.setUser(customerModel);
		modelService.save(debitPaymentModel);

		final CreditCardPaymentInfoModel creditCardPaymentModel = modelService.create(CreditCardPaymentInfoModel.class);
		creditCardPaymentModel.setCode("creditCard1");
		creditCardPaymentModel.setDuplicate(Boolean.FALSE);
		creditCardPaymentModel.setType(CreditCardType.VISA);
		creditCardPaymentModel.setValidFromMonth("3");
		creditCardPaymentModel.setValidToMonth("4");
		creditCardPaymentModel.setValidFromYear("2008");
		creditCardPaymentModel.setValidToYear("2009");
		creditCardPaymentModel.setNumber("4111 1111 1111 1111");
		creditCardPaymentModel.setCcOwner(customerModel.getName());
		creditCardPaymentModel.setUser(customerModel);
		modelService.save(creditCardPaymentModel);

		final AdvancePaymentInfoModel advancePaymentModel = modelService.create(AdvancePaymentInfoModel.class);
		advancePaymentModel.setCode("advance1");
		advancePaymentModel.setDuplicate(Boolean.FALSE);
		advancePaymentModel.setUser(customerModel);
		modelService.save(advancePaymentModel);

		final InvoicePaymentInfoModel invoicePaymentModel = modelService.create(InvoicePaymentInfoModel.class);
		invoicePaymentModel.setCode("invoice1");
		invoicePaymentModel.setDuplicate(Boolean.FALSE);
		invoicePaymentModel.setUser(customerModel);
		modelService.save(invoicePaymentModel);

		final List paymentList = new ArrayList<PaymentInfoModel>();
		paymentList.add(debitPaymentModel);
		paymentList.add(creditCardPaymentModel);
		paymentList.add(advancePaymentModel);
		paymentList.add(invoicePaymentModel);

		customerModel.setPaymentInfos(paymentList);

	}

	/**
	 * Tests URI with each passed http method. Expects always 'method not allowed'
	 * 
	 * @param path
	 *           URL
	 * @param methods
	 *           list of http methods (GET, PUT, POST, DELETE)
	 */
	protected void assertMethodNotAllowed(final String path, final String... methods)
	{
		for (final String method : methods)
		{
			final ClientResponse result = webResource.path(path).cookie(tenantCookie)
					.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML)
					.type(MediaType.APPLICATION_XML).method(method, ClientResponse.class);
			result.bufferEntity();

			// test status code
			assertEquals("Wrong HTTP status at response: " + result, METHOD_NOT_ALLOWED, result.getStatus());

			// test content type (as result displays an error type is text)
			assertTrue("Wrong content-type at response: " + result, result.getType().toString().startsWith("text/"));
		}
	}

	/**
	 * Tests whether the response has status OK. Expects correct status (200), content type of 'application/xml'
	 * 
	 * @param response
	 *           {@link ClientResponse}
	 * @param expectEmptyBody
	 *           true - the body is checked for null value
	 */
	protected void assertOk(final ClientResponse response, final boolean expectEmptyBody)
	{
		assertEquals("Wrong HTTP status at response: " + response, Response.Status.OK, response.getResponseStatus());
		assertTrue("Wrong content-type at response: " + response, MediaType.APPLICATION_XML_TYPE.isCompatible(response.getType()));
		if (expectEmptyBody)
		{
			assertTrue("Body should be empty at response: " + response, response.getEntity(String.class).isEmpty());
		}
	}

	/**
	 * Tests whether resource was successfully created. Expects correct status (201), content type of 'application/xml'
	 * 
	 * @param response
	 *           {@link ClientResponse}
	 * @param expectEmptyBody
	 *           true - the body is checked for null value
	 */
	protected void assertCreated(final ClientResponse response, final boolean expectEmptyBody)
	{
		assertEquals("Wrong HTTP status at response: " + response, Response.Status.CREATED, response.getResponseStatus());
		assertTrue("Wrong content-type at response: " + response, MediaType.APPLICATION_XML_TYPE.isCompatible(response.getType()));
		if (expectEmptyBody)
		{
			assertTrue("Body should be empty at response: " + response, response.getEntity(String.class).isEmpty());
		}
	}

	/**
	 * Tests whether the response has status FORBIDDEN. Expects correct status (403), content type of 'application/xml'
	 * 
	 * @param response
	 *           {@link ClientResponse}
	 * @param expectEmptyBody
	 *           true - the body is checked for null value
	 */
	protected void assertForbidden(final ClientResponse response, final boolean expectEmptyBody)
	{
		assertEquals("Wrong HTTP status at response: " + response, Response.Status.FORBIDDEN, response.getResponseStatus());
		if (expectEmptyBody)
		{
			assertTrue("Body should be empty at response: " + response, response.getEntity(String.class).isEmpty());
		}
	}

	/**
	 * Tests whether the response has status BAD_REQUEST. Expects correct status (400), content type of 'application/xml'
	 * 
	 * @param response
	 *           {@link ClientResponse}
	 * @param expectEmptyBody
	 *           true - the body is checked for null value
	 */
	protected void assertBadRequest(final ClientResponse response, final boolean expectEmptyBody)
	{
		assertEquals("Wrong HTTP status at response: " + response, Response.Status.BAD_REQUEST, response.getResponseStatus());
		if (expectEmptyBody)
		{
			assertTrue("Body should be empty at response: " + response, response.getEntity(String.class).isEmpty());
		}
	}

	protected void assertEqual(final Object expectedDto, final ClientResponse actualResponse, final String... properties)
	{
		final Object actualDto = actualResponse.getEntity(expectedDto.getClass());
		assertEqual(expectedDto, actualDto, properties);
	}

	/**
	 * Tests a collection of properties of two objects for equality.
	 * 
	 * @param expectedDto
	 *           expected object (dto)
	 * @param actualDto
	 *           actual object (dto)
	 * @param properties
	 *           properties to check
	 */
	protected void assertEqual(final Object expectedDto, final Object actualDto, final String... properties)
	{
		final Map<String, PropertyDescriptor> pdMap = getPropertyDescriptors(expectedDto.getClass());
		for (final String property : properties)
		{
			final PropertyDescriptor propertyDescriptor = pdMap.get(property);
			if (propertyDescriptor == null)
			{
				Assert.fail("Property '" + property + "' not available");
			}
			Object expectedProp = null;
			Object actualProp = null;
			try
			{
				expectedProp = propertyDescriptor.getReadMethod().invoke(expectedDto, (Object[]) null);
				actualProp = propertyDescriptor.getReadMethod().invoke(actualDto, (Object[]) null);
			}
			catch (final Exception e)
			{
				LOG.error(e.getMessage(), e);
				Assert.fail();
			}

			final String msg = actualDto.getClass().getSimpleName() + ": value of '" + property
					+ "'  does not match expected value (actual: '" + actualProp + "' expected: '" + expectedProp + "')";
			Assert.assertEquals(msg, expectedProp, actualProp);
		}
	}

	/**
	 * Checks if the given properties of the model and dto from response are equal <br/>
	 * The properties must have the same getters <br/>
	 * if at least one is not equal or not found, than {@link AssertionError} is thrown <br/>
	 * 
	 * @param expectedModel
	 *           model object
	 * @param actualResponse
	 *           actual response
	 * @param expectedDtoClass
	 *           expected dto (class)
	 * @param properties
	 *           a list of properties which have to be checked
	 */
	protected void assertEqual(final ItemModel expectedModel, final ClientResponse actualResponse, final Class expectedDtoClass,
			final String... properties)
	{
		final Object actualDto = actualResponse.getEntity(expectedDtoClass);
		assertEqual(expectedModel, actualDto, properties);
	}

	/**
	 * Checks if the given properties of the model and dto are equal <br/>
	 * The properties must have the same getters <br/>
	 * if at least one is not equal or not found, than {@link AssertionError} is thrown <br/>
	 * 
	 * @param expectedModel
	 *           model object
	 * @param actualDto
	 *           expected dto object
	 * @param properties
	 *           a list of properties which have to be checked
	 */
	protected void assertEqual(final ItemModel expectedModel, final Object actualDto, final String... properties)
	{
		//0. check if null
		assertNotNull("No " + actualDto.getClass().getSimpleName() + " within body ", actualDto);
		assertNotNull("No " + expectedModel.getClass().getSimpleName() + " model ", expectedModel);

		//1. get the properties descriptors from model and dto separately because there are different classes
		final Map<String, PropertyDescriptor> pdModelMap = getPropertyDescriptors(expectedModel.getClass());
		final Map<String, PropertyDescriptor> pdDtoMap = getPropertyDescriptors(actualDto.getClass());

		//2. loop on given properties list
		for (final String property : properties)
		{
			//a) check if properties exist in model and dto
			final PropertyDescriptor pdModel = pdModelMap.get(property);
			final PropertyDescriptor pdDto = pdDtoMap.get(property);
			if ((pdModel == null) || (pdDto == null))
			{
				Assert.fail("Property '" + property + "' not available");
			}
			Object expectedModelProp = null;
			Object actualDtoProp = null;

			//b) get the property values from model and dto
			try
			{
				expectedModelProp = pdModel.getReadMethod().invoke(expectedModel, (Object[]) null);
				actualDtoProp = pdDto.getReadMethod().invoke(actualDto, (Object[]) null);
			}
			catch (final Exception e)
			{
				e.printStackTrace();
				Assert.fail();
			}

			//c) compare: error if at least one is not equal or not found
			final String msg = expectedModel.getClass().getSimpleName() + "," + actualDto.getClass().getSimpleName()
					+ ": value of '" + property + "'  does not match expected value (actual: '" + actualDtoProp + "' expected: '"
					+ expectedModelProp + "')";
			Assert.assertEquals(msg, expectedModelProp, actualDtoProp);
		}
	}

	/**
	 * Checks in loop if the given properties of the object are not null </br> if at least one is null, than
	 * AssertionError is thrown </br>
	 * 
	 * @param objectsList
	 *           list of objects to check
	 * @param objectsClass
	 *           class of objects in the list
	 * @param properties
	 *           properties to check
	 */
	protected void assertIsNotNull(final Collection objectsList, final Class objectsClass, final String... properties)
	{
		final Map<String, PropertyDescriptor> pdMap = getPropertyDescriptors(objectsClass);
		for (final Object dto : objectsList)
		{
			for (final String property : properties)
			{
				final PropertyDescriptor propDesc = pdMap.get(property);
				if (propDesc == null)
				{
					Assert.fail("Property '" + property + "' not available");
				}
				Object actualProp = null;
				try
				{
					actualProp = propDesc.getReadMethod().invoke(dto, (Object[]) null); //NOPMD
				}
				catch (final Exception e)
				{
					LOG.error(e.getMessage(), e);
					Assert.fail();
				}

				final String msg = dto.getClass().getSimpleName() + ": value of '" + property + "' is null";
				Assert.assertNotNull(msg, actualProp);
			}
		}
	}

	/**
	 * Checks in loop if the given properties of the object are null <br/>
	 * if at least one is not null, than AssertionError is thrown <br/>
	 * 
	 * @param objectsList
	 *           list of objects to check
	 * @param objectsClass
	 *           class of objects in the list
	 * @param properties
	 *           properties to check
	 */
	protected void assertIsNull(final List objectsList, final Class objectsClass, final String... properties)
	{
		final Map<String, PropertyDescriptor> pdMap = getPropertyDescriptors(objectsClass);
		for (final Object dto : objectsList)
		{
			for (final String property : properties)
			{
				final PropertyDescriptor propDesc = pdMap.get(property);
				if (propDesc == null)
				{
					Assert.fail("Property '" + property + "' not available");
				}
				Object actualProp = null;
				try
				{
					actualProp = propDesc.getReadMethod().invoke(dto, (Object[]) null); //NOPMD
				}
				catch (final Exception e)
				{
					LOG.error(e.getMessage(), e);
					Assert.fail();
				}

				final String msg = dto.getClass().getSimpleName() + ": value of '" + property + "' is null";
				Assert.assertNull(msg, actualProp);
			}
		}
	}

	private Map<String, PropertyDescriptor> getPropertyDescriptors(final Class clazz)
	{
		final Map<String, PropertyDescriptor> result = new HashMap<String, PropertyDescriptor>();
		try
		{
			final PropertyDescriptor[] pdList = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
			for (final PropertyDescriptor pd : pdList)
			{
				result.put(pd.getName(), pd);
			}
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			Assert.fail();
		}
		return result;
	}

	/**
	 * Depends on {@link #createTestProductsUnits()}.
	 */
	protected void createTestCarts() throws ConsistencyCheckException
	{
		final CurrencyModel euro = modelService.create(CurrencyModel.class);
		euro.setActive(Boolean.TRUE);
		euro.setName("Euro");
		euro.setIsocode("EUR");
		euro.setBase(Boolean.TRUE);
		euro.setConversion(Double.valueOf(1));
		euro.setDigits(Integer.valueOf(2));
		euro.setSymbol("�");
		modelService.save(euro);
		//create cart with an entry
		final CartModel cartModel = modelService.create(CartModel.class);
		final UserModel userModel = userService.getUserForUID("admin");
		cartModel.setCurrency(euro);
		cartModel.setDate(new Date());
		cartModel.setUser(userModel);
		cartModel.setCalculated(Boolean.FALSE);
		cartModel.setNet(Boolean.FALSE);
		cartModel.setCode("adminCart");
		modelService.save(cartModel);
		modelService.refresh(cartModel);
		//add entry to cart
		final CartEntryModel cartEntryModel = modelService.create(CartEntryModel.class);
		cartEntryModel.setOrder((CartModel) modelService.get(cartModel.getPk()));
		final CatalogVersionModel catalogVersion = catalogService.getCatalogVersion("testCatalog1", "testVersion1");
		final ProductModel product = productService.getProduct(catalogVersion, "testProduct1");
		cartEntryModel.setProduct(product);
		cartEntryModel.setQuantity(Long.valueOf(1));
		cartEntryModel.setUnit(productService.getUnit("testUnit1"));
		modelService.save(cartEntryModel);
		//create empty cart
		final CartModel cartModel2 = modelService.create(CartModel.class);
		cartModel2.setCurrency(euro);
		cartModel2.setDate(new Date());
		cartModel2.setUser(userModel);
		cartModel2.setCalculated(Boolean.FALSE);
		cartModel2.setNet(Boolean.FALSE);
		cartModel2.setCode("emptyCart");
		modelService.save(cartModel2);
		modelService.refresh(cartModel2);
		//create cart to be modified
		final CartModel cartModel3 = modelService.create(CartModel.class);
		cartModel3.setCurrency(euro);
		cartModel3.setDate(new Date());
		cartModel3.setUser(userModel);
		cartModel3.setCalculated(Boolean.FALSE);
		cartModel3.setNet(Boolean.FALSE);
		cartModel3.setCode("cartForPut");
		modelService.save(cartModel3);
		modelService.refresh(cartModel3);
		//add entries to cart
		final CartEntryModel cartEntryModel2 = modelService.create(CartEntryModel.class);
		cartEntryModel2.setOrder(cartModel3);
		cartEntryModel2.setProduct(product);
		cartEntryModel2.setQuantity(Long.valueOf(1));
		cartEntryModel2.setUnit(productService.getUnit("testUnit1"));
		modelService.save(cartEntryModel2);
	}

	/**
	 * Depends on {@link #createTestProductsUnits()}.
	 */
	protected void createTestProductPrices() throws ConsistencyCheckException
	{
		final CatalogVersionModel catalogVersion = catalogService.getCatalogVersion("testCatalog1", "testVersion1");
		final ProductModel productModel = productService.getProduct(catalogVersion, "testProduct1");
		final PriceRowModel priceRow = modelService.create(PriceRowModel.class);
		priceRow.setCatalogVersion(catalogVersion);
		priceRow.setCurrency(i18nService.getBaseCurrency());
		priceRow.setProduct(productModel);
		priceRow.setPrice(Double.valueOf(10));
		priceRow.setUser(userService.getUserForUID("admin"));
		priceRow.setUnitFactor(Integer.valueOf(1));
		priceRow.setNet(Boolean.FALSE);
		priceRow.setUnit(productService.getUnit("testUnit1"));
		modelService.save(priceRow);
		productModel.setEurope1Prices(Arrays.asList(new PriceRowModel[]
		{ priceRow }));
		modelService.save(productModel);
	}

	protected void createPaymentDeliveryModes() throws ConsistencyCheckException
	{
		final ComposedType info = TypeManager.getInstance().getComposedType(Constants.TYPES.CreditCardPaymentInfo);
		final ComposedType mode = TypeManager.getInstance().getComposedType(PaymentMode.class);
		final PaymentMode paymentMode = OrderManager.getInstance().createPaymentMode(mode, "testPaymentMode1", info);
		final PaymentModeModel paymentModeModel = modelService.get(paymentMode);
		paymentModeModel.setActive(Boolean.TRUE);
		paymentModeModel.setDescription("creditcard payment mode.");
		paymentModeModel.setName("Credit Card");
		modelService.save(paymentModeModel);

		final ComposedType mode2 = TypeManager.getInstance().getComposedType(DeliveryMode.class);
		final DeliveryMode deliveryMode = OrderManager.getInstance().createDeliveryMode(mode2, "testDeliveryMode1");
		final DeliveryModeModel deliveryModeModel = modelService.get(deliveryMode);
		deliveryModeModel.setActive(Boolean.TRUE);
		deliveryModeModel.setDescription("delivery mode.");
		deliveryModeModel.setName("Delivery Mode");
		final List<PaymentModeModel> list = new ArrayList<PaymentModeModel>();
		list.add(paymentModeModel);
		deliveryModeModel.setSupportedPaymentModes(list);
		modelService.save(deliveryModeModel);

	}

	protected void createTestsCronJobs() throws ConsistencyCheckException
	{

		//example for job: UpdateIndexJobModel
		final UpdateIndexJobModel jobModel = modelService.create(UpdateIndexJobModel.class);
		jobModel.setCode("Job1");
		final CronJobModel cronJobModel = modelService.create(CronJobModel.class);
		cronJobModel.setCode("testCronJob1");
		cronJobModel.setJob(jobModel);
		cronJobModel.setLogToDatabase(Boolean.FALSE);
		cronJobModel.setLogToFile(Boolean.FALSE);
		cronJobModel.setEmailAddress("hybris@hybris.de");
		cronJobModel.setRequestAbort(Boolean.FALSE);
		cronJobModel.setChangeRecordingEnabled(Boolean.FALSE);
		cronJobModel.setNodeID(Integer.valueOf(0));
		cronJobModel.setSingleExecutable(Boolean.FALSE);
		cronJobModel.setRemoveOnExit(Boolean.FALSE);
		cronJobModel.setRequestAbortStep(Boolean.FALSE);
		cronJobModel.setPriority(Integer.valueOf(1));
		cronJobModel.setActive(Boolean.FALSE);

		//		final Customer customer1 = UserManager.getInstance().createCustomer("testCustomer1");
		//		customer1.setName("name1");
		final User user1 = UserManager.getInstance().getAdminEmployee();
		final UserModel userModel = modelService.get(user1);
		final LanguageModel languageModel = modelService.get(C2LManager.getInstance().createLanguage("testLang1"));
		final CurrencyModel euro = modelService.create(CurrencyModel.class);
		euro.setActive(Boolean.TRUE);
		euro.setName("Euro");
		euro.setIsocode("EUR");
		euro.setBase(Boolean.TRUE);
		euro.setConversion(Double.valueOf(1));
		euro.setDigits(Integer.valueOf(2));
		euro.setSymbol("�");
		modelService.save(euro);

		cronJobModel.setSessionUser(userModel);
		cronJobModel.setSessionCurrency(euro);
		cronJobModel.setSessionLanguage(languageModel);

		cronJobModel.setLogLevelFile(JobLogLevel.INFO);
		cronJobModel.setLogLevelDatabase(JobLogLevel.WARNING);
		cronJobModel.setErrorMode(ErrorMode.IGNORE);

		modelService.save(cronJobModel);
	}

	public static void createTestsLanguages() throws ConsistencyCheckException
	{

		final Language lang2 = C2LManager.getInstance().createLanguage("testLang2");
		lang2.setActive(true);
		lang2.setName("testLang2Name");

		final Language lang1 = C2LManager.getInstance().createLanguage("testLang1");
		lang1.setActive(false);
		lang1.setName("testLang1Name");
		lang1.setFallbackLanguages(lang2);

	}

	/**
	 * Helper method to import csv from <tt>resources/test/</tt> directory located in b2bcore extension. It expects, that
	 * file has <tt>.csv</tt> extension, and is in standard ImpEx csv format. There is no need to add test/ base
	 * directory or <tt>.csv</tt> extension to parameter when calling this method. Following calls are equals:
	 * 
	 * <pre>
	 * importCSVFromResources(&quot;/test/fooBar.csv&quot;);
	 * importCSVFromResources(&quot;/test/fooBar&quot;);
	 * importCSVFromResources(&quot;fooBar&quot;);
	 * </pre>
	 * 
	 * All of them assumes that there is file called <tt>fooBar.csv</tt> in <tt>resources/testsrc/</tt> directory.
	 * 
	 * @param fileName
	 *           the file name
	 * @throws ImpExException
	 *            - thrown on any problems with importing data. Such problems will be dumped into file in system temp
	 *            directory.
	 */
	public static void importCSVFromResources(final String fileName) throws ImpExException
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
		LOG.info("importing resource " + fileName);
		final InputStream inStr = Case1Test.class.getResourceAsStream(resource.toString());
		ImpExManager.getInstance().importDataLight(inStr, "UTF-8", true);
	}

	/**
	 * Checks for extension.
	 * 
	 * @param fileName
	 *           the file name
	 * @return true, if successful
	 */
	private static boolean hasExtension(final String fileName)
	{
		return FilenameUtils.getExtension(fileName).equals("") ? false : true;
	}

	/**
	 * Checks for resource folder.
	 * 
	 * @param fileName
	 *           the file name
	 * @return true, if successful
	 */
	private static boolean hasResourceFolder(final String fileName)
	{
		return FilenameUtils.getPath(fileName).equals("") ? false : true;
	}

	protected void getWsUtilService()
	{
		wsUtilService = new DefaultWsUtilService();
		((DefaultWsUtilService) wsUtilService).setUserService(userService);
		((DefaultWsUtilService) wsUtilService).setFlexibleSearchService(flexibleSearchService);
		((DefaultWsUtilService) wsUtilService).setI18nDao(i18NDao);
		((DefaultWsUtilService) wsUtilService).setModelService(modelService);
	}
}
