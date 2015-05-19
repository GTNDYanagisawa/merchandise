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
package de.hybris.platform.sap.sapordermgmtbol.module.transaction.item.businessobject.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.ConfigurationImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.SimpleItemImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.SimpleItem;

import java.math.BigDecimal;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class ItemSalesDocTest extends TestCase
{

	private final ItemSalesDoc classUnderTest = new ItemSalesDoc();



	public void testTechKeyEquals()
	{
		final SimpleItemImpl item1 = new ItemSalesDoc();
		final SimpleItemImpl item2 = new ItemSalesDoc();
		item1.setTechKey(new TechKey("TECHKEY"));
		item2.setTechKey(new TechKey("TECHKEY"));

		Assert.assertTrue(item1.equals(item2));
	}

	public void testTechKeyNotEquals()
	{
		final SimpleItemImpl item1 = new ItemSalesDoc();
		final SimpleItemImpl item2 = new ItemSalesDoc();
		item1.setTechKey(new TechKey("TECHKEY1"));
		item2.setTechKey(new TechKey("TECHKEY2"));

		Assert.assertFalse(item1.equals(item2));
	}

	public void testNoTechKeyNotEquals()
	{
		final SimpleItem item1 = new ItemSalesDoc();
		final SimpleItem item2 = new ItemSalesDoc();
		item1.setProductId("Product 1");
		item2.setProductId("Product 2");

		Assert.assertFalse(item1.equals(item2));
	}

	public void testNoTechKeyEquals()
	{
		final SimpleItem item1 = new ItemSalesDoc();
		final SimpleItem item2 = new ItemSalesDoc();
		item1.setProductId("Product");
		item1.setQuantity(BigDecimal.TEN);
		item2.setProductId("Product");
		item2.setQuantity(BigDecimal.TEN);

		Assert.assertFalse(item1.equals(item2));
	}

	public void testInstanceEquals()
	{
		final SimpleItemImpl item1 = new ItemSalesDoc();
		final SimpleItem item2 = item1;
		item1.setTechKey(new TechKey("TECHKEY"));

		Assert.assertTrue(item1.equals(item2));
	}

	public void testProductOfItemChanged_NotEquals()
	{
		final TechKey oldProductId = new TechKey("oldGuid");
		final String oldProduct = "oldProduct";
		final String oldUnit = "oldUnit";

		final String newProduct = "newProduct";

		classUnderTest.setProductGuid(oldProductId);
		classUnderTest.setProductId(oldProduct);
		classUnderTest.setUnit(oldUnit);

		assertEquals("Product Id is wrong", oldProductId, classUnderTest.getProductGuid());
		assertEquals("Product is wrong", oldProduct, classUnderTest.getProductId());
		assertEquals("Unit is wrong", oldUnit, classUnderTest.getUnit());

		classUnderTest.setProductId(newProduct);
		assertTrue("It was not determined that product was changed", classUnderTest.isProductChanged());
	}

	public void testProductOfItemChanged_EqualsUpperCase()
	{
		final TechKey oldProductId = new TechKey("oldGuid");
		final String oldProduct = "oldProduct";
		final String oldUnit = "oldUnit";

		final String newProduct = "OLDPRODUCT";

		classUnderTest.setProductGuid(oldProductId);
		classUnderTest.setProductId(oldProduct);
		classUnderTest.setUnit(oldUnit);

		assertEquals("Product Id is wrong", oldProductId, classUnderTest.getProductGuid());
		assertEquals("Product is wrong", oldProduct, classUnderTest.getProductId());
		assertEquals("Unit is wrong", oldUnit, classUnderTest.getUnit());

		classUnderTest.setProductId(newProduct);
		assertFalse("It was not determined that product was changed", classUnderTest.isProductChanged());
	}

	public void testProductOfItemChanged_Equals()
	{
		final TechKey oldProductId = new TechKey("oldGuid");
		final String oldProduct = "oldProduct";
		final String oldUnit = "oldUnit";

		final String newProduct = "oldProduct";

		classUnderTest.setProductGuid(oldProductId);
		classUnderTest.setProductId(oldProduct);
		classUnderTest.setUnit(oldUnit);

		assertEquals("Product Id is wrong", oldProductId, classUnderTest.getProductGuid());
		assertEquals("Product is wrong", oldProduct, classUnderTest.getProductId());
		assertEquals("Unit is wrong", oldUnit, classUnderTest.getUnit());

		classUnderTest.setProductId(newProduct);
		assertFalse("It was not determined that product was changed", classUnderTest.isProductChanged());
	}

	public void testProductOfItemChanged_EqualsBlanks()
	{
		final TechKey oldProductId = new TechKey("oldGuid");
		final String oldProduct = "oldProduct";
		final String oldUnit = "oldUnit";

		final String newProduct = " oldProduct ";

		classUnderTest.setProductGuid(oldProductId);
		classUnderTest.setProductId(oldProduct);
		classUnderTest.setUnit(oldUnit);

		assertEquals("Product Id is wrong", oldProductId, classUnderTest.getProductGuid());
		assertEquals("Product is wrong", oldProduct, classUnderTest.getProductId());
		assertEquals("Unit is wrong", oldUnit, classUnderTest.getUnit());

		classUnderTest.setProductId(newProduct);
		assertFalse("It was not determined that product was changed", classUnderTest.isProductChanged());
	}

	public void testIsGiftCard()
	{
		boolean isGiftCard = true;

		classUnderTest.setGiftCard(isGiftCard);
		assertEquals("Product should be a gift card", isGiftCard, classUnderTest.isGiftCard());

		isGiftCard = false;
		classUnderTest.setGiftCard(isGiftCard);
		assertEquals("Product is not a gift card", isGiftCard, classUnderTest.isGiftCard());
	}

	public void testIsMergeSupported_true()
	{

		classUnderTest.setTechKey(new TechKey("A"));
		classUnderTest.setProductId("HT-1010");
		classUnderTest.setParentId(TechKey.EMPTY_KEY);

		final Item toMergeWith = new ItemSalesDoc();
		toMergeWith.setTechKey(new TechKey("B"));
		toMergeWith.setProductId("HT-1010");
		toMergeWith.setParentId(TechKey.EMPTY_KEY);

		assertEquals("Items should be merged", true, classUnderTest.isMergeSupported(toMergeWith));

	}

	public void testGetQuantityToPay_normal()
	{
		classUnderTest.setQuantity(BigDecimal.TEN);
		classUnderTest.setFreeQuantity(BigDecimal.ZERO);

		final BigDecimal quantity = classUnderTest.getQuantityToPay();

		assertEquals(BigDecimal.TEN, quantity);
	}

	public void testGetQuantityToPay_fg()
	{
		classUnderTest.setQuantity(BigDecimal.TEN);
		classUnderTest.setFreeQuantity(BigDecimal.ONE);

		final BigDecimal quantity = classUnderTest.getQuantityToPay();

		assertEquals(new BigDecimal(9), quantity);
	}

	public void testGetQuantityToPay_null()
	{
		classUnderTest.setQuantity(BigDecimal.TEN);
		classUnderTest.setFreeQuantity(null);

		final BigDecimal quantity = classUnderTest.getQuantityToPay();

		assertEquals(BigDecimal.TEN, quantity);
	}

	@Test
	public void testConfigModel()
	{
		final ConfigModel configModel = EasyMock.createMock(ConfigModel.class);
		EasyMock.replay(configModel);
		classUnderTest.setProductConfiguration(configModel);
	}

	@Test
	public void testProductConfigurationExternal()
	{
		final Configuration externalConfiguration = new ConfigurationImpl();
		classUnderTest.setProductConfigurationExternal(externalConfiguration);
		assertEquals(externalConfiguration, classUnderTest.getProductConfigurationExternal());
	}

	@Test
	public void testProductConfigurationDirtyInitial()
	{
		//initially, configuration is considered to be dirty
		assertTrue(classUnderTest.isProductConfigurationDirty());
	}

	@Test
	public void testProductConfigurationDirty()
	{
		//initially, configuration is considered to be dirty
		classUnderTest.setProductConfigurationDirty(false);
		assertFalse(classUnderTest.isProductConfigurationDirty());
	}



}
