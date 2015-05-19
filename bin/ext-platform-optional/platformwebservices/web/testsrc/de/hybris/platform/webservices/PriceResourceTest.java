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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.imp.ImpExImportReader;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.webservices.dto.price.PriceDTO;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.CharUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


/**
 * Test intended for anonymous user.
 */
public class PriceResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "catalogs/testCatalog1/catalogversions/testVersion1/products/";

	private static final String DEFAULT_PRICE_URI = "/defaultprice";

	private static final String BESTVALUE_PRICE_URI = "/bestvalueprice";

	private static final String LOWESTQUANTITY_PRICE_URI = "/lowestquantityprice";

	private Product defaultPriceProduct;

	private static final double PRICE_LEVEL_1 = 500.0d;

	private static final double PRICE_LEVEL_2 = 900.0d;

	private static final double TAX_LEVEL = 50; //pretty high but ;)

	//
	/**
	 * tax value = (rate * value)/ 1 + rate where rate 1/10 means 10 percent returnig rounded value to
	 */
	int getNettoValue(final double valueBrutto, final double taxlevel)
	{
		final double taxRate = taxlevel / 100d;

		return Double.valueOf(valueBrutto - (valueBrutto * taxRate) / (1 + taxRate)).intValue(); //NOPMD
	}

	/**
	 * discount per item in currency
	 */
	final int discountValue = 100;

	/**
	 * items count threshold to receive the discount
	 */
	final int itemCountLevel = 2;





	@Before
	public void setUpProducts() throws ConsistencyCheckException, JaloBusinessException
	{
		createTestCatalogs();
		createTestClassificationSystem();
		createTestProductsUnits();

		final CatalogManager cmi = CatalogManager.getInstance();
		final CatalogVersion version = cmi.getCatalog("testCatalog1").getCatalogVersion("testVersion1");

		defaultPriceProduct = ProductManager.getInstance().createProduct("testProduct3");
		CatalogManager.getInstance().setCatalogVersion(defaultPriceProduct, version);

		createTestDiscountsTaxes(defaultPriceProduct.getCode());
	}


	/**
	 * creates some test purpose discounts/vouchers/tax row entries
	 */
	private void createTestDiscountsTaxes(final String productCode) throws ImpExException
	{



		final StringBuffer taxBuffer = new StringBuffer(1000);
		taxBuffer.append("insert_update Tax;code[unique=true,allownull=true];currency(isocode);name[lang=testLang1];value;")
				.append(CharUtils.LF);
		taxBuffer.append(";RobinHoodTax;;RobinHood's Tax;" + TAX_LEVEL + ";").append(CharUtils.LF);

		new ImpExImportReader(taxBuffer.toString()).readAll();

		final StringBuffer promoBuffer = new StringBuffer(1000);
		promoBuffer
				.append(
						"insert_update Discount;code[unique=true,forceWrite=true,allownull=true];currency(isocode);name[lang=testLang1];value[allownull=true];")
				.append(CharUtils.LF);
		promoBuffer.append(";TestVoucher;---;TestVoucher;5;").append(CharUtils.LF);

		new ImpExImportReader(promoBuffer.toString()).readAll();

		final StringBuffer discountBuffer = new StringBuffer(1000);
		discountBuffer
				.append(
						"insert_update DiscountRow;catalogVersion(catalog(id),version);currency(isocode);discount(code)[forceWrite=true,allownull=true];pg(code,itemtype(code));product(catalogVersion(catalog(id),version),code)[unique=true];ug(code,itemtype(code));user(uid);value;")
				.append(CharUtils.LF);
		discountBuffer.append(
				";testCatalog1:testVersion1;---;TestVoucher;;testCatalog1:testVersion1:" + productCode + ";;anonymous;"
						+ discountValue + ";").append(CharUtils.LF);

		new ImpExImportReader(discountBuffer.toString()).readAll();

		final StringBuffer taxRowBuffer = new StringBuffer(1000);
		taxRowBuffer
				.append(
						"insert_update TaxRow;catalogVersion(catalog(id),version)[unique=true];currency(isocode);pg(code,itemtype(code));product(catalogVersion(catalog(id),version),code)[unique=true];tax(code)[forceWrite=true,allownull=true];ug(code,itemtype(code));user(uid);value")
				.append(CharUtils.LF);
		taxRowBuffer.append(";;;;testCatalog1:testVersion1:" + productCode + ";RobinHoodTax;;;").append(CharUtils.LF);

		new ImpExImportReader(taxRowBuffer.toString()).readAll();

		final StringBuffer priceRowBuffer = new StringBuffer(1000);
		priceRowBuffer
				.append(
						"insert_update PriceRow;catalogVersion(catalog(id),version);currency(isocode)[allownull=true];giveAwayPrice[allownull=true];matchValue;minqtd[allownull=true,unique=true];net[allownull=true];owner(pk);pg(code,itemtype(code));price[allownull=true];product(catalogVersion(catalog(id),version),code)[unique=true];ug(code,itemtype(code));unit(code)[allownull=true];user(uid)")
				.append(CharUtils.LF);
		priceRowBuffer.append(
				";testCatalog1:testVersion1;---;false;5;" + itemCountLevel + ";false;;;" + PRICE_LEVEL_1
						+ ";testCatalog1:testVersion1:" + productCode + ";;testUnit1;").append(CharUtils.LF);
		priceRowBuffer.append(
				";testCatalog1:testVersion1;---;false;5;0;false;;;" + PRICE_LEVEL_2 + ";testCatalog1:testVersion1:" + productCode
						+ ";;testUnit1;").append(CharUtils.LF);

		new ImpExImportReader(priceRowBuffer.toString()).readAll();

	}


	@Test
	public void testGetProductDefaultPrice()
	{
		final ClientResponse result = webResource.path(URI + defaultPriceProduct.getCode() + DEFAULT_PRICE_URI)
				.cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final PriceDTO price = result.getEntity(PriceDTO.class);

		assertNotNull("price dto is null", price);


		assertEquals("Wrong price value for " + defaultPriceProduct.getCode(), Double.valueOf(getNettoValue(PRICE_LEVEL_1,
				TAX_LEVEL)
				- (itemCountLevel * discountValue)), price.getValue());

		assertEquals("Wrong price currency for " + defaultPriceProduct.getCode(), "---", price.getCurrency());
	}

	@Test
	public void testGetProductLowestQuantityPrice()
	{
		final ClientResponse result = webResource.path(URI + defaultPriceProduct.getCode() + LOWESTQUANTITY_PRICE_URI).cookie(
				tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final PriceDTO price = result.getEntity(PriceDTO.class);

		assertNotNull("price dto is null", price);

		//final double taxRate = taxLevel / 100d;

		assertEquals("Wrong price value for " + defaultPriceProduct.getCode(), Double.valueOf(getNettoValue(PRICE_LEVEL_2,
				TAX_LEVEL)), price.getValue());

		assertEquals("Wrong price currency for " + defaultPriceProduct.getCode(), "---", price.getCurrency());

	}

	@Test
	public void testGetProductBestValuePrice()
	{
		final ClientResponse result = webResource.path(URI + defaultPriceProduct.getCode() + BESTVALUE_PRICE_URI).cookie(
				tenantCookie).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final PriceDTO price = result.getEntity(PriceDTO.class);

		assertNotNull("price dto is null", price);

		assertEquals("Wrong price value for " + defaultPriceProduct.getCode(), Double.valueOf(getNettoValue(PRICE_LEVEL_1,
				TAX_LEVEL)
				- (itemCountLevel * discountValue)), price.getValue());

		assertEquals("Wrong price currency for " + defaultPriceProduct.getCode(), "---", price.getCurrency());
	}
}
