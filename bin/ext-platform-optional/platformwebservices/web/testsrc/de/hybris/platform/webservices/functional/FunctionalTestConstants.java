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
package de.hybris.platform.webservices.functional;

import de.hybris.platform.core.dto.order.CartEntryDTO;
import de.hybris.platform.core.dto.product.ProductDTO;
import de.hybris.platform.core.dto.product.UnitDTO;
import de.hybris.platform.core.dto.security.PrincipalGroupDTO;
import de.hybris.platform.core.dto.user.AddressDTO;
import de.hybris.platform.core.dto.user.CustomerDTO;
import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.core.dto.user.UserGroupDTO;
import de.hybris.platform.webservices.AbstractWebServicesTest;

import java.util.Collections;
import java.util.HashSet;



/**
 * 
 */
public class FunctionalTestConstants
{

	public static final String CODE_PRODUCT1 = "testProduct1";
	public static final String CODE_PRODUCT2 = "testProduct2";
	public static final String CODE_PRODUCT3 = "testProduct3";
	public static final String CODE_UNIT1 = "testUnit1";
	public static final String CODE_CATEGORY1 = "testCategory1";
	public static final String UID_USER1 = "testUser1";
	public static final String UID_DELIVERYMODE1 = "testDeliveryMode1";
	public static final String UID_PAYMENTMODE1 = "testPaymentMode1";
	public static final String UID_CUSTOMERGROUP1 = "customergroup";

	public static final String URI_BASE = "http://localhost:9001/" + AbstractWebServicesTest.WS_VERSION + "/rest";
	public static final String URI_ADDRESSES = "/addresses";
	public static final String URI_CATALOGS = "/catalogs";
	public static final String URI_CATALOGVERSIONS = "/catalogversions";
	public static final String URI_CATEGORIES = "/categories";
	public static final String URI_CARTS = "/carts";
	public static final String URI_DELIVERYMODE = "/deliverymodes";
	public static final String URI_PAYMENTMODE = "/paymentmodes";
	public static final String URI_PAYMENTINFO = "/paymentinfos";
	public static final String URI_PAYMENTINFO_CREDITCARD = "/creditcardpaymentinfos";
	public static final String URI_ENTRIES = "/cartentries";
	public static final String URI_USERS = "/users";


	public static final String URI_USERGROUPS = "/usergroups";
	public static final String URI_CUSTOMERS = "/customers";
	public static final String URI_RETRIEVE_PASS = "/retrievepassword";
	public static final String URI_TESTCATALOG1_STAGED = URI_CATALOGS + "/testCatalog1" + URI_CATALOGVERSIONS + "/Staged";
	public static final String URI_PRODUCTS = "/products";
	public static final String URI_PRODUCT1 = URI_PRODUCTS + "/" + CODE_PRODUCT1;
	public static final String URI_PRODUCT2 = URI_PRODUCTS + "/" + CODE_PRODUCT2;
	public static final String URI_PRODUCT3 = URI_PRODUCTS + "/" + CODE_PRODUCT3;
	public static final String URI_CATEGORY1 = "/" + CODE_CATEGORY1;
	public static final String URI_UNITS = "/units";
	public static final String URI_UNIT1 = URI_UNITS + "/" + CODE_UNIT1;

	public static final String URI_SHORT_CATEGORY1 = URI_TESTCATALOG1_STAGED + URI_CATEGORIES + URI_CATEGORY1;
	public static final String URI_SHORT_PRODUCT1 = URI_TESTCATALOG1_STAGED + URI_PRODUCT1;
	public static final String URI_SHORT_PRODUCT2 = URI_TESTCATALOG1_STAGED + URI_PRODUCT2;
	public static final String URI_SHORT_PRODUCT3 = URI_TESTCATALOG1_STAGED + URI_PRODUCT3;
	public static final String URI_FULL_PRODUCT1 = URI_BASE + URI_SHORT_PRODUCT1;
	public static final String URI_FULL_PRODUCT2 = URI_BASE + URI_SHORT_PRODUCT2;
	public static final String URI_FULL_PRODUCT3 = URI_BASE + URI_SHORT_PRODUCT3;
	public static final String URI_FULL_UNIT1 = URI_BASE + URI_UNIT1;

	public static final UserGroupDTO TEST_USERGROUP1;
	static
	{
		TEST_USERGROUP1 = new UserGroupDTO();
		TEST_USERGROUP1.setUid(FunctionalTestConstants.UID_CUSTOMERGROUP1);
		TEST_USERGROUP1.setName("newgroup");
		TEST_USERGROUP1.setDescription("newgroup");

	}
	public static final UserDTO TEST_USER1;
	static
	{
		TEST_USER1 = new UserDTO();
		TEST_USER1.setUid(FunctionalTestConstants.UID_USER1);
		TEST_USER1.setName("testName");
		TEST_USER1.setPasswordQuestion("passwordQuestion");
		TEST_USER1.setPasswordAnswer("passwordAnswer");
		// generated usergroupDTO issue
		//TEST_USER1.setGroups(Collections.singletonList(TEST_USERGROUP1));
		TEST_USER1.setGroups(new HashSet<PrincipalGroupDTO>(Collections.singletonList(TEST_USERGROUP1)));
	}
	public static final CustomerDTO TEST_CUSTOMER1;
	static
	{
		TEST_CUSTOMER1 = new CustomerDTO();
		TEST_CUSTOMER1.setUid(FunctionalTestConstants.UID_USER1);
		TEST_CUSTOMER1.setName("testName");
		TEST_CUSTOMER1.setPasswordQuestion("passwordQuestion");
		TEST_CUSTOMER1.setPasswordAnswer("passwordAnswer");
		// generated usergroupDTO issue
		//TEST_CUSTOMER1.setGroups(Collections.singletonList(TEST_USERGROUP1));
		TEST_CUSTOMER1.setGroups(new HashSet<PrincipalGroupDTO>(Collections.singletonList(TEST_USERGROUP1)));
	}
	public static final CartEntryDTO TEST_ENTRY1;
	static
	{
		TEST_ENTRY1 = new CartEntryDTO();
		TEST_ENTRY1.setQuantity(Long.valueOf(1));
		final ProductDTO product1 = new ProductDTO();
		product1.setCode(CODE_PRODUCT1);
		product1.setUri(URI_FULL_PRODUCT1);
		TEST_ENTRY1.setProduct(product1);
		final UnitDTO unit1 = new UnitDTO();
		unit1.setCode(CODE_UNIT1);
		unit1.setUri(URI_FULL_UNIT1);
		TEST_ENTRY1.setUnit(unit1);
	}
	public static final CartEntryDTO TEST_ENTRY2;
	static
	{
		TEST_ENTRY2 = new CartEntryDTO();
		TEST_ENTRY2.setQuantity(Long.valueOf(1));
		final ProductDTO product2 = new ProductDTO();
		product2.setCode(CODE_PRODUCT2);
		product2.setUri(URI_FULL_PRODUCT2);
		TEST_ENTRY2.setProduct(product2);
		final UnitDTO unit1 = new UnitDTO();
		unit1.setCode(CODE_UNIT1);
		unit1.setUri(URI_FULL_UNIT1);
		TEST_ENTRY2.setUnit(unit1);
	}
	public static final CartEntryDTO TEST_ENTRY3;
	static
	{
		TEST_ENTRY3 = new CartEntryDTO();
		TEST_ENTRY3.setQuantity(Long.valueOf(1));
		final ProductDTO product3 = new ProductDTO();
		product3.setCode(CODE_PRODUCT3);
		product3.setUri(URI_FULL_PRODUCT3);
		TEST_ENTRY3.setProduct(product3);
		final UnitDTO unit1 = new UnitDTO();
		unit1.setCode(CODE_UNIT1);
		unit1.setUri(URI_FULL_UNIT1);
		TEST_ENTRY3.setUnit(unit1);
	}

	public static final CartEntryDTO TEST_ENTRY3Q2;
	static
	{
		TEST_ENTRY3Q2 = new CartEntryDTO();
		TEST_ENTRY3Q2.setQuantity(Long.valueOf(2));
		final ProductDTO product3 = new ProductDTO();
		product3.setCode(CODE_PRODUCT3);
		product3.setUri(URI_FULL_PRODUCT3);
		TEST_ENTRY3Q2.setProduct(product3);
		final UnitDTO unit1 = new UnitDTO();
		unit1.setCode(CODE_UNIT1);
		unit1.setUri(URI_FULL_UNIT1);
		TEST_ENTRY3Q2.setUnit(unit1);
	}
	public static final AddressDTO TEST_ADDRESS1;
	static
	{
		TEST_ADDRESS1 = new AddressDTO();
		TEST_ADDRESS1.setEmail("testb2b@hybris.de");
		TEST_ADDRESS1.setBuilding("Building");
		TEST_ADDRESS1.setStreetname("Streetname");
		TEST_ADDRESS1.setStreetnumber("13c/3");
		TEST_ADDRESS1.setFirstname("TestName");
		TEST_ADDRESS1.setLastname("Lastname");
		final CustomerDTO customer = new CustomerDTO();
		customer.setUid(UID_USER1);
		TEST_ADDRESS1.setOwner(customer);
	}

}
