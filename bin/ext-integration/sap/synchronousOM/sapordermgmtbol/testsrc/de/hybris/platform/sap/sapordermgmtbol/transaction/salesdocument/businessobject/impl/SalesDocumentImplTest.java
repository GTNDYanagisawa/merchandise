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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.businessobject.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProviderFactory;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.impl.AddressImpl;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerListEntry;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.interf.BasketBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.businessobject.impl.BasketImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.ConnectedDocumentImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.ShipToImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.AlternativeProductImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.AlternativeProduct;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.businessobject.impl.TransactionConfigurationImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.PartnerListEntryImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.PartnerListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.TextImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.SalesDocumentType;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class SalesDocumentImplTest extends SapordermanagmentBolSpringJunitTest
{

	private static final String HANDLE_A = "A";
	private static final TechKey TECHKEY_A = new TechKey(HANDLE_A);
	private static final String EXTENSION_KEY = "PRODUCT";
	private static final String SOLD_TO_ID = "0815";
	private static final TechKey TECH_KEY_SOLD_TO = new TechKey("TechKeySoldTo");
	private SalesDocumentImpl classUnderTest;

	private final String PROCESS_TYPE = "X";
	private TransactionConfiguration config;
	@Resource(name = SapordermgmtbolConstants.ALIAS_BO_CART)
	private BasketImpl basket;

	private final BasketBackend mockedService = EasyMock.createMock(BasketBackend.class);

	@Override
	@Before
	public void setUp()
	{
		final BasketBackend backendService = EasyMock.createNiceMock(BasketBackend.class);
		EasyMock.expect(backendService.getSalesDocumentType()).andReturn(SalesDocumentType.BASKET).anyTimes();
		EasyMock.replay(backendService);
		basket.setBackendService(backendService);
		classUnderTest = basket;

		config = new TransactionConfigurationImpl();
		classUnderTest.setTransactionConfiguration(config);

	}

	@Test
	public void testHandleNotUniqueProductsFalse() throws CommunicationException
	{

		final Item item = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		classUnderTest.addItem(item);

		assertFalse(classUnderTest.applyAlternativeProducts());
	}

	@Test
	public void testHandleNotUniqueProductsTrue() throws CommunicationException
	{

		final Item item = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		final AlternativeProduct altProd1 = new AlternativeProductImpl();
		altProd1.setSystemProductGUID(new TechKey(HANDLE_A));
		item.getAlternativProductList().addAlternativProduct(altProd1);
		final AlternativeProduct altProd2 = new AlternativeProductImpl();
		altProd2.setSystemProductGUID(new TechKey("B"));
		item.getAlternativProductList().addAlternativProduct(altProd2);
		classUnderTest.addItem(item);

		assertTrue(classUnderTest.applyAlternativeProducts());

		assertEquals(new TechKey(HANDLE_A), item.getProductGuid());
	}

	@Test
	public void testHandleNotUniqueProduct_resetTransferItem() throws BusinessObjectException
	{
		final Item item = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		item.setProductId("PRODUCT OLD");

		final AlternativeProduct altProd1 = new AlternativeProductImpl();
		altProd1.setSystemProductGUID(new TechKey(HANDLE_A));
		final String expectedId = "PRODUCT NEW";
		altProd1.setSystemProductId(expectedId);
		altProd1.setSubstitutionReasonId("Any Reason");
		item.getAlternativProductList().addAlternativProduct(altProd1);
		classUnderTest.addItem(item);

		assertTrue("There is a substition product, hence not unique", classUnderTest.applyAlternativeProducts());

	}

	@Test
	public void testHandleNotUniqueProductsFalse_Substitution() throws CommunicationException
	{

		final Item item = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		item.setProductId("PRODUCT OLD");
		final AlternativeProduct altProd1 = new AlternativeProductImpl();
		altProd1.setSystemProductGUID(new TechKey(HANDLE_A));
		final String expectedId = "PRODUCT NEW";
		altProd1.setSystemProductId(expectedId);
		altProd1.setSubstitutionReasonId("Any Reason");
		item.getAlternativProductList().addAlternativProduct(altProd1);
		classUnderTest.addItem(item);

		assertTrue("There is a substition product, hence not unique", classUnderTest.applyAlternativeProducts());

		final Item itemAfterTest = classUnderTest.getItemList().get(0);
		final String actualId = itemAfterTest.getProductId();
		assertEquals(expectedId, actualId);
		assertFalse("No update needed", itemAfterTest.isProductChanged());
	}

	@Test
	public void testCopyAttributesOfItem()
	{
		final Item source = createReferenceItem();
		final Item destination = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);

		classUnderTest.copyAttributesOfItem(source, destination);

		verifyItemAttributes(source, destination);
	}

	private void verifyItemAttributes(final Item source, final Item destination)
	{

		assertEquals("ProductId must be copied", source.getProductGuid(), destination.getProductGuid());
		assertEquals("Product must be copied", source.getProductId(), destination.getProductId());
		assertEquals("Unit must be copied", source.getUnit(), destination.getUnit());
		assertEquals("Quantity must be copied", source.getQuantity(), destination.getQuantity());
		assertEquals("Delivery Priority must be copied", source.getDeliveryPriority(), destination.getDeliveryPriority());
		assertEquals("Text must be copied", source.getText(), destination.getText());
		assertEquals("ConfigType must be copied", source.getConfigType(), destination.getConfigType());
		assertEquals("ParentId must be copied", source.getParentId(), destination.getParentId());

		assertNotSame(source.getShipTo(), destination.getShipTo());
		assertEquals("ShipTo must be copied", source.getShipTo().getId(), destination.getShipTo().getId());

		final PartnerList partnerList = destination.getPartnerListData();
		final PartnerListEntry soldTo = partnerList.getSoldToData();

		assertNotNull("Partnerlist was not copied", soldTo);
		assertEquals(SOLD_TO_ID, soldTo.getPartnerId());
		assertEquals(TECH_KEY_SOLD_TO, soldTo.getPartnerTechKey());

		assertEquals("Configurable attribute must be copied", source.isConfigurable(), destination.isConfigurable());
		assertEquals("Item type usage  must be copied", source.getItmTypeUsage(), destination.getItmTypeUsage());
	}

	private Item createReferenceItem()
	{
		final Item item = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);

		item.setProductGuid(new TechKey("ProductId"));
		item.setProductId("Product");
		item.setUnit("Unit");
		item.setQuantity(BigDecimal.TEN);
		item.setDeliveryPriority("DeliveryPriority");
		item.setText(new TextImpl("Id", "Text"));
		item.setConfigType("ConfigType");
		item.setParentId(new TechKey("ParentId"));
		item.setShipTo(new ShipToImpl());
		item.setItmTypeUsage("itmTypeUsage");
		final PartnerList partnerList = item.getPartnerListData();

		final PartnerListEntry entry = new PartnerListEntryImpl();
		entry.setPartnerId(SOLD_TO_ID);
		entry.setPartnerTechKey(TECH_KEY_SOLD_TO);
		partnerList.setSoldToData(entry);

		return item;
	}

	@Test
	public void testGetSoldToGuid()
	{
		classUnderTest.setSoldToGuid(TECH_KEY_SOLD_TO);
		assertEquals(TECH_KEY_SOLD_TO, classUnderTest.getSoldToGuid());
	}

	@Test
	public void testGetSoldToGuid_PartnerList()
	{
		final PartnerList partnerList = new PartnerListImpl();
		classUnderTest.getHeader().setPartnerList(partnerList);
		final PartnerListEntry entry = new PartnerListEntryImpl();
		entry.setPartnerId(SOLD_TO_ID);
		entry.setPartnerTechKey(TECH_KEY_SOLD_TO);
		partnerList.setSoldToData(entry);

		assertEquals(TECH_KEY_SOLD_TO, classUnderTest.getSoldToGuid());
	}

	@Test
	public void testSetSoldToGuid()
	{
		classUnderTest.setSoldToGuid(TECH_KEY_SOLD_TO, SOLD_TO_ID);

		assertEquals(TECH_KEY_SOLD_TO, classUnderTest.getSoldToGuid());
		final PartnerListEntry soldToData = classUnderTest.getHeader().getPartnerList().getSoldToData();
		assertEquals(TECH_KEY_SOLD_TO, soldToData.getPartnerTechKey());
		assertEquals(SOLD_TO_ID, soldToData.getPartnerId());
	}

	@Test
	public void testSetSoldToGuid_TwoSoldTosWithSameKey()
	{
		classUnderTest.setSoldToGuid(TECH_KEY_SOLD_TO, SOLD_TO_ID);
		classUnderTest.setSoldToGuid(TECH_KEY_SOLD_TO, SOLD_TO_ID);

		assertEquals(TECH_KEY_SOLD_TO, classUnderTest.getSoldToGuid());
		final PartnerListEntry soldToData = classUnderTest.getHeader().getPartnerList().getSoldToData();
		assertEquals(TECH_KEY_SOLD_TO, soldToData.getPartnerTechKey());
		assertEquals(SOLD_TO_ID, soldToData.getPartnerId());
		assertEquals(1, classUnderTest.getHeader().getPartnerList().getList().size());
	}



	private Item createItem(final TechKey key, final String product)
	{
		final Item item = new ItemSalesDoc();
		item.setProductId(product);
		item.setTechKey(key);
		item.setParentId(TechKey.EMPTY_KEY);
		item.getExtensionMap().put(EXTENSION_KEY, product);
		return item;
	}

	@Test
	public void testInit_simple() throws BusinessObjectException
	{
		final SalesDocument source = (SalesDocument) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_ORDER);

		classUnderTest.init(source, PROCESS_TYPE);

		assertEquals(PROCESS_TYPE, classUnderTest.getHeader().getProcessType());
	}

	@Test
	public void testInit_items() throws BusinessObjectException
	{
		final SalesDocument source = (SalesDocument) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_ORDER);
		source.addItem(createItem(new TechKey(HANDLE_A), "Product A"));
		source.addItem(createItem(new TechKey("B"), "Product B"));
		source.addItem(createItem(new TechKey("C"), "Product C"));

		classUnderTest.init(source, PROCESS_TYPE);

		assertEquals("Not all items were copied", source.getItemList().size(), classUnderTest.getItemList().size());
		verifyItem("Product A");
		verifyItem("Product B");
		verifyItem("Product C");
	}


	private void verifyItem(final String product)
	{
		Item foundItem = null;
		for (final Item item : classUnderTest.getItemList())
		{
			if (product.equals(item.getProductId()))
			{
				foundItem = item;
				break;
			}
		}

		assertNotNull("Product has not been copied", foundItem);
		final Object extension = foundItem.getExtensionData(EXTENSION_KEY);
		assertNotNull("Extension has not been copied");
		assertEquals("Wrong extension", product, extension);
	}

	@Test
	public void testGetDublicateItems_true()
	{
		final Item itemA = createItemWithUnit("Product A", "PC");
		classUnderTest.addItem(itemA);

		final Item itemB = createItemWithUnit("Product A", "PC");
		classUnderTest.addItem(itemB);

		final Map<String, List<Item>> dublicateItems = classUnderTest.getDublicatesForItems();

		assertEquals(1, dublicateItems.size());
		final String itemKey = classUnderTest.createItemKey(itemA);
		assertEquals(2, dublicateItems.get(itemKey).size());

	}

	@Test
	public void testGetDublicateItems_false()
	{
		final Item itemA = createItemWithUnit("Product A", "PC");
		classUnderTest.addItem(itemA);

		final Item itemB = createItemWithUnit("Product A", "EACH");
		classUnderTest.addItem(itemB);

		final Map<String, List<Item>> dublicateItems = classUnderTest.getDublicatesForItems();

		assertEquals(2, dublicateItems.size());
		final String itemKey = classUnderTest.createItemKey(itemA);
		assertEquals(1, dublicateItems.get(itemKey).size());

	}

	private Item createItemWithUnit(final String productGuid, final String unit)
	{
		final Item itemA = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		itemA.setParentId(TechKey.EMPTY_KEY);
		itemA.setTechKey(new TechKey(HANDLE_A));
		itemA.setUnit(unit);
		itemA.setProductGuid(new TechKey(productGuid));
		return itemA;
	}

	@Test
	public void testGetDublicateItems_partner()
	{
		final Item itemA = createItemWithUnit("Product A", "PC");
		final PartnerListEntry entry = new PartnerListEntryImpl();
		entry.setPartnerId("0815");
		entry.setPartnerTechKey(new TechKey("X"));
		itemA.getPartnerListData().setPartnerData("ISTP", entry);
		classUnderTest.addItem(itemA);

		final Item itemB = createItemWithUnit("Product A", "PC");
		classUnderTest.addItem(itemB);

		final Map<String, List<Item>> dublicateItems = classUnderTest.getDublicatesForItems();

		assertEquals(2, dublicateItems.size());
		final String itemKey = classUnderTest.createItemKey(itemA);
		assertEquals(1, dublicateItems.get(itemKey).size());
	}

	@Test
	public void testGetDublicateItems_differentPartners()
	{
		final Item itemA = createItemWithUnit("Product A", "PC");
		final PartnerListEntry entry1 = new PartnerListEntryImpl();
		entry1.setPartnerId("0815");
		entry1.setPartnerTechKey(TechKey.EMPTY_KEY);
		itemA.getPartnerListData().setPartnerData("ISTP", entry1);
		classUnderTest.addItem(itemA);

		final Item itemB = createItemWithUnit("Product A", "PC");
		final PartnerListEntry entry2 = new PartnerListEntryImpl();
		entry2.setPartnerId("0816");
		entry2.setPartnerTechKey(TechKey.EMPTY_KEY);
		itemB.getPartnerListData().setPartnerData("ISTP", entry2);
		classUnderTest.addItem(itemB);

		final Map<String, List<Item>> dublicateItems = classUnderTest.getDublicatesForItems();

		assertEquals(2, dublicateItems.size());
		final String itemKey = classUnderTest.createItemKey(itemA);
		assertEquals(1, dublicateItems.get(itemKey).size());

	}

	@Test
	public void testGetDublicateItems_additionalPartner()
	{
		final Item itemA = createItemWithUnit("Product A", "PC");
		final PartnerListEntry entry1 = new PartnerListEntryImpl();
		entry1.setPartnerId("0815");
		entry1.setPartnerTechKey(TechKey.EMPTY_KEY);
		itemA.getPartnerListData().setPartnerData("ISTP", entry1);
		classUnderTest.addItem(itemA);

		final Item itemB = createItemWithUnit("Product A", "PC");
		itemB.getPartnerListData().setPartnerData("ISTP", entry1);

		final PartnerListEntry entry2 = new PartnerListEntryImpl();
		entry2.setPartnerId("4711");
		entry2.setPartnerTechKey(TechKey.EMPTY_KEY);
		itemB.getPartnerListData().setPartnerData("VENDOR", entry2);
		classUnderTest.addItem(itemB);

		final Map<String, List<Item>> dublicateItems = classUnderTest.getDublicatesForItems();

		assertEquals(2, dublicateItems.size());
		final String itemKey = classUnderTest.createItemKey(itemA);
		assertEquals(1, dublicateItems.get(itemKey).size());

	}

	@Test
	public void testRead() throws CommunicationException, BackendException
	{
		EasyMock.replay(mockedService);
		basket.setBackendService(mockedService);
		classUnderTest.setDirty(false);

		classUnderTest.read();

		EasyMock.verify(mockedService);
	}

	@Test
	public void testRead_dirty() throws CommunicationException, BackendException
	{
		expectOneRead();
		EasyMock.replay(mockedService);
		basket.setBackendService(mockedService);
		classUnderTest.setDirty(true);

		classUnderTest.read();

		EasyMock.verify(mockedService);
	}

	@Test
	public void testUpdate() throws CommunicationException, BackendException
	{
		expectOneUpdate();
		EasyMock.replay(mockedService);
		basket.setBackendService(mockedService);

		classUnderTest.update();

		EasyMock.verify(mockedService);
	}

	private void expectOneRead() throws BackendException
	{
		mockedService.readFromBackend(classUnderTest, config, false);
		EasyMock.expectLastCall().times(1);
	}

	private void expectOneUpdate() throws BackendException
	{
		final List<TechKey> emptyList = new ArrayList<TechKey>();
		mockedService.updateInBackend(classUnderTest, config, emptyList);
		EasyMock.expectLastCall().times(1);
	}

	@Test
	public void test_copyAttributesOfItem()
	{
		final Item source = new ItemSalesDoc();
		source.setText(new TextImpl("100", "aText"));
		source.getText().setHandle("123");

		final ShipTo shipTo = new ShipToImpl();
		final Address address = new AddressImpl();
		address.setCountry("US");
		shipTo.setAddress(address);
		source.setShipTo(shipTo);

		final Item destination = new ItemSalesDoc();
		classUnderTest.copyAttributesOfItem(source, destination);

		assertNotSame(source.getText(), destination.getText());
		assertEquals(source.getText().getText(), destination.getText().getText());
		assertEquals(source.getText().getId(), destination.getText().getId());
		assertEquals("", destination.getText().getHandle());
		assertEquals("123", source.getText().getHandle());
		assertFalse(source.getText().hasTextChanged());
		assertTrue(destination.getText().hasTextChanged());

		assertNotSame(source.getShipTo(), destination.getShipTo());
		assertNotSame(source.getShipTo().getAddress(), destination.getShipTo().getAddress());
		assertEquals(source.getShipTo().getAddress().getCountry(), destination.getShipTo().getAddress().getCountry());
	}

	@Test
	public void test_hasPredecessorOfSpecificType()
	{
		assertFalse("Document has a quotation as predecessor", classUnderTest.hasPredecessorOfSpecificType(DocumentType.QUOTATION));

		final ConnectedDocument quotationPredecessor = new ConnectedDocumentImpl();
		quotationPredecessor.setDocType(DocumentType.QUOTATION);
		classUnderTest.getHeader().addPredecessor(quotationPredecessor);

		assertTrue("Document has no quotation as predecessor", classUnderTest.hasPredecessorOfSpecificType(DocumentType.QUOTATION));
	}

	@Test
	public void testCheckQuantityUOM_noRounding() throws Exception
	{
		final Item item = createTestItem(2, 0); // 2
		classUnderTest.addItem(item);
		injectConverter(0);

		final Map<TechKey, Message> messages = classUnderTest.checkQuantityUOM();

		assertEquals("There must not be any message", 0, messages.size());
	}

	@Test
	public void testCheckQuantityUOM_noRoundingScale3() throws Exception
	{
		final Item item = createTestItem(1234, 3); // 1,234
		classUnderTest.addItem(item);
		injectConverter(3);

		final Map<TechKey, Message> messages = classUnderTest.checkQuantityUOM();

		assertEquals("There must not be any message", 0, messages.size());
	}

	@Test
	public void testCheckQuantityUOM_rounding() throws Exception
	{
		final Item item = createTestItem(25, 1); // 2,5
		classUnderTest.addItem(item);
		injectConverter(0);

		final Map<TechKey, Message> messages = classUnderTest.checkQuantityUOM();

		assertEquals("There must be 1 message", 1, messages.size());
	}

	@Test
	public void testCheckQuantityUOM_noRoundingNecessary() throws Exception
	{
		final Item item = createTestItem(20, 1); // 2,0
		classUnderTest.addItem(item);
		injectConverter(0);

		final Map<TechKey, Message> messages = classUnderTest.checkQuantityUOM();

		assertEquals("There must not be any message", 0, messages.size());
	}

	@Test
	public void testCheckQuantityUOM_belowMinimum() throws Exception
	{
		final Item item = createTestItem(6, 3); // 0,006
		classUnderTest.addItem(item);
		injectConverter(2);

		final Map<TechKey, Message> messages = classUnderTest.checkQuantityUOM();

		assertEquals("There must be message", 1, messages.size());
	}

	@Test
	public void testCheckQuantityUOM_wholeUnits() throws Exception
	{
		final Item item = createTestItem(31, 1); // 3,1
		classUnderTest.addItem(item);
		injectConverter(0);

		final Map<TechKey, Message> messages = classUnderTest.checkQuantityUOM();

		assertEquals("There must be message", 1, messages.size());
	}

	private void injectConverter(final int allowedScale) throws BusinessObjectException
	{
		final Converter converter = EasyMock.createNiceMock(Converter.class);
		EasyMock.expect(converter.getUnitScale(EasyMock.anyObject(String.class))).andReturn(allowedScale).anyTimes();
		final BigDecimal min = BigDecimal.valueOf(1, allowedScale);
		EasyMock.expect(converter.getMinimumScaleValue(EasyMock.anyObject(String.class))).andReturn(min).anyTimes();
		EasyMock.replay(converter);
		classUnderTest.setConverter(converter);
	}

	private Item createTestItem(final int unscaled, final int scale)
	{
		final Item item = new ItemSalesDoc();
		item.setProductId("XXX");
		final BigDecimal quantity = BigDecimal.valueOf(unscaled, scale); // 2
		item.setQuantity(quantity);
		item.setUnit("PC");
		return item;
	}

	@Test
	public void testValidate() throws CommunicationException
	{
		classUnderTest.validate();
		//sales document not yet initialized, no message and no backend call
		assertEquals(0, classUnderTest.getMessageList().size());
	}

	@Test
	public void testReleaseConfigurationSessionNoHandle()
	{
		final TechKey key = TECHKEY_A;
		classUnderTest.releaseConfigurationSession(key);
	}


	@Test
	public void testConfigurationProvider()
	{
		final ConfigurationProviderFactory configurationProviderFactory = classUnderTest.getConfigurationProviderFactory();
		assertNotNull(configurationProviderFactory);
	}

	@Test
	public void testGetConfigIDFromHandle()
	{
		final TechKey key = TECHKEY_A;
		final String configId = classUnderTest.getConfigId(key);
		assertNull(configId);
	}

	@Test
	public void testStoreConfigId()
	{
		final TechKey key = TECHKEY_A;
		final String configId = "nxbc";
		classUnderTest.setConfigId(key, configId);
		assertEquals(configId, classUnderTest.getConfigId(key));
	}

	@Test
	public void testReleaseConfigurations()
	{
		final List<TechKey> itemsToDelete = new ArrayList<>();
		itemsToDelete.add(new TechKey(HANDLE_A));
		classUnderTest.releaseConfigurationSession(itemsToDelete);
	}

	@Test
	public void testReleaseConfigurationsForSalesDoc()
	{
		final Item item = new ItemSalesDoc();
		item.setTechKey(TECHKEY_A);
		classUnderTest.addItem(item);
		classUnderTest.releaseConfigurationSessions();
	}

	@Test
	public void testBackendWasUp()
	{
		classUnderTest.setBackendWasUp(true);
		assertTrue(classUnderTest.isBackendWasUp());
	}

	@Test
	public void testBackendWasDown()
	{
		classUnderTest.setBackendWasDown(true);
		assertTrue(classUnderTest.isBackendWasDown());
	}

	@Test
	public void testIsBackendDown()
	{
		assertFalse(classUnderTest.isBackendDown());
	}

	@Test
	public void testCheckBackendWasUp()
	{
		assertTrue(classUnderTest.checkBackendNeverWasUp());
	}

}
