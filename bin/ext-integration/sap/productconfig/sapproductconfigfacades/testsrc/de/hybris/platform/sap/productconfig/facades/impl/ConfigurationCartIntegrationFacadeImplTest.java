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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigConsistenceChecker;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationTestData;
import de.hybris.platform.sap.productconfig.facades.CsticTypeMapper;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UiTypeFinder;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConfigurationCartIntegrationFacadeImplTest
{
	private final static String PRODUCT_CODE = "SAP_SIMPLE_POC";

	private static final String DUMMY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"3\" KBNAME=\"DUMMY_KB\" KBPROFILE=\"DUMMY_KB\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"DUMMY_KB\" OBJ_TXT=\"Dummy KB\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"DUMMY_CSTIC\" CHARC_TXT=\"Dummy CStic\" VALUE=\"8\" VALUE_TXT=\"Value 8\"/></CSTICS></INST><PARTS/><NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE></SOLUTION>";


	private ConfigurationCartIntegrationFacadeImpl configCartIntegrationFacade;

	@Mock
	private CommerceCartService commerceCartService;

	@Mock
	private ProductConfigurationService configService;

	@Mock
	private CartService cartService;

	@Mock
	private ModelService modelService;

	@Mock
	private ProductService productService;

	@Mock
	private CartEntryModel otherCartItem;

	@Mock
	private ConfigPricing configPricing;

	@Mock
	private AbstractOrderEntryModel cartItem;

	private KBKeyData kbKey;

	private CartModel shoppingCart;

	private ProductModel product;

	private UnitModel unit;

	private ConfigurationData configData;

	private final List<AbstractOrderEntryModel> itemsInCart = new ArrayList<>();

	private CommerceCartModification modification;

	private ConfigurationFacadeImpl configFacade;

	@Mock
	private BaseStoreService baseStoreService;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		final ConfigConsistenceChecker configConsistenceChecker = new ConfigConsistenceCheckerImpl();
		final CsticTypeMapper typeMapper = new CsticTypeMapperImpl();
		final UiTypeFinder uiTypeFinder = new UiTypeFinderImpl();
		typeMapper.setUiTypeFinder(uiTypeFinder);
		typeMapper.setValueFormatTranslater(new ValueFormatTranslatorImpl());

		configCartIntegrationFacade = new ConfigurationCartIntegrationFacadeImpl();
		configCartIntegrationFacade.setConfigurationService(configService);
		configCartIntegrationFacade.setCartService(cartService);
		configCartIntegrationFacade.setModelService(modelService);
		configCartIntegrationFacade.setProductService(productService);
		configCartIntegrationFacade.setCommerceCartService(commerceCartService);

		configFacade = new ConfigurationFacadeImpl();
		configFacade.setConfigurationService(configService);
		configFacade.setCsticTypeMapper(typeMapper);
		configFacade.setConfigConsistenceChecker(configConsistenceChecker);
		configFacade.setConfigPricing(configPricing);

		kbKey = new KBKeyData();
		kbKey.setProductCode(PRODUCT_CODE);
		kbKey.setKbName("YSAP_SIMPLE_POC");
		kbKey.setKbLogsys("ABC");
		kbKey.setKbVersion("123");

		shoppingCart = new CartModel();
		shoppingCart.setEntries(itemsInCart);
		product = new ProductModel();
		unit = new UnitModel();

		product.setCode(PRODUCT_CODE);
		product.setUnit(unit);

		configData = new ConfigurationData();
		configData.setKbKey(kbKey);

		final PricingData pricingData = new PricingData();
		pricingData.setBasePrice(ConfigPricing.NO_PRICE);
		pricingData.setSelectedOptions(ConfigPricing.NO_PRICE);
		pricingData.setCurrentTotal(ConfigPricing.NO_PRICE);

		given(cartService.getSessionCart()).willReturn(shoppingCart);
		given(otherCartItem.getPk()).willReturn(PK.parse("1234567890"));
		given(configPricing.getPricingData(any(ConfigModel.class))).willReturn(pricingData);


		cartItem.setProduct(product);
		modification = new CommerceCartModification();
		modification.setEntry(cartItem);

		given(cartItem.getPk()).willReturn(PK.parse("123"));
		given(cartItem.getProduct()).willReturn(product);

		final BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setCatalogs(Collections.EMPTY_LIST);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStore);
		typeMapper.setBaseStoreService(baseStoreService);
	}



	private ConfigModel initializeFirstCall()
	{
		final ConfigModel createdConfigModel = ConfigurationTestData.createConfigModelWithCstic();
		given(configService.createDefaultConfiguration(any(KBKey.class))).willReturn(createdConfigModel);
		given(configService.retrieveConfigurationModel(createdConfigModel.getId())).willReturn(createdConfigModel);
		return createdConfigModel;
	}

	@Test
	public void testAddConfigurationToCart() throws CommerceCartModificationException
	{
		initializeFirstCall();
		final ConfigurationData configContent = configFacade.getConfiguration(kbKey);

		given(configService.retrieveExternalConfiguration(configContent.getConfigId())).willReturn(DUMMY_XML);
		given(productService.getProductForCode(kbKey.getProductCode())).willReturn(product);

		given(commerceCartService.addToCart(shoppingCart, product, 1l, unit, true)).willReturn(modification);

		configCartIntegrationFacade.addConfigurationToCart(configContent);

		Mockito.verify(cartItem).setProduct(product);
		Mockito.verify(modelService).setAttributeValue(any(), anyString(), eq(DUMMY_XML));
		Mockito.verify(modelService).save(cartItem);

	}

	@Test
	public void testCopyConfiguration()
	{
		final String configId = "A";
		final String newConfigId = configCartIntegrationFacade.copyConfiguration(configId);
		assertEquals(configId, newConfigId);
	}


	@Test
	public void testConvertNullToNullPK()
	{
		final PK pk = configCartIntegrationFacade.convertStringToPK(null);
		assertEquals("null value should be mapped to NULL PK", PK.NULL_PK, pk);
	}

	@Test
	public void testConvertEmptyStringToNullPK()
	{
		final PK pk = configCartIntegrationFacade.convertStringToPK("");
		assertEquals("empty value should be mapped to NULL PK", PK.NULL_PK, pk);
	}

	@Test
	public void testConvertStringToPK()
	{
		final PK pk = configCartIntegrationFacade.convertStringToPK("123");
		assertEquals("string pk conversion failed", PK.parse("123"), pk);
	}

	@Test
	public void testGetOrCreateCartItem_newItem() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItem);
		given(commerceCartService.addToCart(shoppingCart, product, 1l, unit, true)).willReturn(modification);
		final AbstractOrderEntryModel cartItem = configCartIntegrationFacade.getOrCreateCartItem(product, configData);
		assertNotNull(cartItem);
	}

	@Test
	public void testGetOrCreateCartItem_updateItem() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItem);
		configData.setCartItemPK(otherCartItem.getPk().toString());

		given(Boolean.valueOf(modelService.isRemoved(otherCartItem))).willReturn(Boolean.FALSE);
		final AbstractOrderEntryModel cartItem = configCartIntegrationFacade.getOrCreateCartItem(product, configData);

		assertNotNull(cartItem);
	}

	@Test
	public void testItemInCart_false() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItem);

		final boolean itemInCart = configCartIntegrationFacade.isItemInCartByKey(cartItem.getPk().toString());

		assertFalse("Item should not be in cart", itemInCart);
	}

	@Test
	public void testItemInCart_true() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItem);
		itemsInCart.add(cartItem);

		final boolean itemInCart = configCartIntegrationFacade.isItemInCartByKey(cartItem.getPk().toString());

		assertTrue("Item should be in cart", itemInCart);
	}




	@Test
	public void testGetOrCreateCartItem_updateRemovedItem() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItem);
		configData.setCartItemPK(otherCartItem.getPk().toString());

		given(Boolean.valueOf(modelService.isRemoved(otherCartItem))).willReturn(Boolean.TRUE);
		given(commerceCartService.addToCart(shoppingCart, product, 1l, unit, true)).willReturn(modification);
		final AbstractOrderEntryModel cartItem = configCartIntegrationFacade.getOrCreateCartItem(product, configData);

		assertNotNull(cartItem);
		assertNotSame("New iteme expeted", otherCartItem, cartItem);
	}

	@Test
	public void testRest()
	{
		configCartIntegrationFacade.resetConfiguration("123");
		Mockito.verify(configService).releaseSession("123");
	}

}
