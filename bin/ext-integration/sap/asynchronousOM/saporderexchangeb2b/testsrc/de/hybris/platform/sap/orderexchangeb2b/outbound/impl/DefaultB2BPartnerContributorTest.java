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
package de.hybris.platform.sap.orderexchangeb2b.outbound.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerRoles;
import de.hybris.platform.sap.orderexchange.constants.SaporderexchangeConstants;
import de.hybris.platform.sap.orderexchange.outbound.B2CCustomerHelper;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultB2CCustomerHelper;
import de.hybris.platform.store.BaseStoreModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
@SuppressWarnings("javadoc")
public class DefaultB2BPartnerContributorTest
{
	private static final String DE = "DE";
	private static final String REF_CUST = "refCust";
	private static final String USER_ID = "004711";
	private static final String UNIT_ID = "004712";
	private static final String SAP_CUSTOMER_ID_FOR_DELIVERY = "004713";
	private static final String SAP_CUSTOMER_ID_FOR_PAYMENT = "004714";
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	private static final String STREET_NAME = "streetName";
	private static final String TOWN = "town";
	private static final String PHONE1 = "phone1";
	private static final String STREET_NUMBER = "streetNumber";
	private static final String POSTAL_CODE = "postalCode";
	private static final String REGION2 = "region";
	private static final String COUNTRY_ISO_CODE = "country";
	private static final String EMAIL = "email";
	private static final String MIDDLE_NAME = "middleName";
	private static final String MIDDLE_NAME2 = "middleName2";
	private static final String DISTRICT = "district";
	private static final String BUILDING = "building";
	private static final String APPARTMENT = "appartment";
	private static final String POBOX = "pobox";
	private static final String FAX = "fax";
	private static final String TITLE_CODE = "titleCode";
	private static final String CODE = "Code";

	private DefaultB2BPartnerContributor cut;

	@Mock
	private B2BUnitService<B2BUnitModel, ?> b2bUnitService;

	private final B2BUnitModel unit = new B2BUnitModel();
	private final Set groups = new HashSet<PrincipalGroupModel>();
	private final B2BUnitModel unit1 = new B2BUnitModel();
	private OrderModel order;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		cut = new DefaultB2BPartnerContributor();
		cut.setB2bUnitService(b2bUnitService);
		order = new OrderModel();
		unit1.setUid(UNIT_ID);
		groups.add(unit1);
		unit.setGroups(groups);
		unit.setUid("SOME ID");
		order.setUnit(unit);

		Mockito.when(b2bUnitService.getRootUnit(unit)).thenReturn(unit1);
	}

	@Test
	public void testGetColumns()
	{
		final Set<String> columns = cut.getColumns();

		assertTrue(columns.contains(OrderCsvColumns.ORDER_ID));
		assertTrue(columns.contains(PartnerCsvColumns.PARTNER_ROLE_CODE));
		assertTrue(columns.contains(PartnerCsvColumns.PARTNER_CODE));
		assertTrue(columns.contains(PartnerCsvColumns.DOCUMENT_ADDRESS_ID));
		assertTrue(columns.contains(PartnerCsvColumns.FIRST_NAME));
		assertTrue(columns.contains(PartnerCsvColumns.LAST_NAME));
		assertTrue(columns.contains(PartnerCsvColumns.STREET));
		assertTrue(columns.contains(PartnerCsvColumns.CITY));
		assertTrue(columns.contains(PartnerCsvColumns.TEL_NUMBER));
		assertTrue(columns.contains(PartnerCsvColumns.HOUSE_NUMBER));
		assertTrue(columns.contains(PartnerCsvColumns.POSTAL_CODE));
		assertTrue(columns.contains(PartnerCsvColumns.REGION_ISO_CODE));
		assertTrue(columns.contains(PartnerCsvColumns.COUNTRY_ISO_CODE));
		assertTrue(columns.contains(PartnerCsvColumns.EMAIL));
		assertTrue(columns.contains(PartnerCsvColumns.LANGUAGE_ISO_CODE));
		assertTrue(columns.contains(PartnerCsvColumns.MIDDLE_NAME));
		assertTrue(columns.contains(PartnerCsvColumns.MIDDLE_NAME));
		assertTrue(columns.contains(PartnerCsvColumns.DISTRICT));
		assertTrue(columns.contains(PartnerCsvColumns.BUILDING));
		assertTrue(columns.contains(PartnerCsvColumns.APPARTMENT));
		assertTrue(columns.contains(PartnerCsvColumns.POBOX));
		assertTrue(columns.contains(PartnerCsvColumns.FAX));
		assertTrue(columns.contains(PartnerCsvColumns.TITLE));
	}

	@Test
	public void testCreateB2BRows()
	{
		//		final OrderModel order = new OrderModel();
		order.setCode(CODE);

		final AddressModel paymentAddress = new AddressModel();
		fillAddress(paymentAddress);
		paymentAddress.setSapCustomerID(SAP_CUSTOMER_ID_FOR_PAYMENT);
		order.setPaymentAddress(paymentAddress);

		final AddressModel deliveryAddress = new AddressModel();
		fillAddress(deliveryAddress);
		deliveryAddress.setSapCustomerID(SAP_CUSTOMER_ID_FOR_DELIVERY);
		order.setDeliveryAddress(deliveryAddress);

		final BaseSiteModel site = new BaseSiteModel();
		site.setChannel(SiteChannel.B2B);
		order.setSite(site);

		final B2BCustomerModel b2BCustomer = new B2BCustomerModel();
		b2BCustomer.setCustomerID(USER_ID);
		order.setUser(b2BCustomer);
		//
		//		final B2BUnitModel unit = new B2BUnitModel();
		//		final Set groups = new HashSet<PrincipalGroupModel>();
		//		final B2BUnitModel unit1 = new B2BUnitModel();
		//		unit1.setUid(UNIT_ID);
		//		groups.add(unit1);
		//		unit.setGroups(groups);
		//		unit.setUid("SOME ID");
		//
		//		order.setUnit(unit);

		final LanguageModel language_de = new LanguageModel();
		language_de.setIsocode(DE);
		order.setLanguage(language_de);

		final List<Map<String, Object>> rows = cut.createRows(order);

		Map<String, Object> row = rows.get(0);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(null, row.get(PartnerCsvColumns.LANGUAGE_ISO_CODE));
		assertEquals(PartnerRoles.SOLD_TO.getCode(), row.get(PartnerCsvColumns.PARTNER_ROLE_CODE));
		assertEquals(UNIT_ID, row.get(PartnerCsvColumns.PARTNER_CODE));
		assertEquals("", row.get(PartnerCsvColumns.DOCUMENT_ADDRESS_ID));

		row = rows.get(1);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(null, row.get(PartnerCsvColumns.LANGUAGE_ISO_CODE));
		assertEquals(PartnerRoles.CONTACT.getCode(), row.get(PartnerCsvColumns.PARTNER_ROLE_CODE));
		assertEquals(USER_ID, row.get(PartnerCsvColumns.PARTNER_CODE));
		assertEquals("", row.get(PartnerCsvColumns.DOCUMENT_ADDRESS_ID));

		row = rows.get(2);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(null, row.get(PartnerCsvColumns.LANGUAGE_ISO_CODE));
		assertEquals(PartnerRoles.SHIP_TO.getCode(), row.get(PartnerCsvColumns.PARTNER_ROLE_CODE));
		assertEquals(SAP_CUSTOMER_ID_FOR_DELIVERY, row.get(PartnerCsvColumns.PARTNER_CODE));
		assertEquals("", row.get(PartnerCsvColumns.DOCUMENT_ADDRESS_ID));

		row = rows.get(3);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(null, row.get(PartnerCsvColumns.LANGUAGE_ISO_CODE));
		assertEquals(PartnerRoles.BILL_TO.getCode(), row.get(PartnerCsvColumns.PARTNER_ROLE_CODE));
		assertEquals(SAP_CUSTOMER_ID_FOR_PAYMENT, row.get(PartnerCsvColumns.PARTNER_CODE));
		assertEquals("", row.get(PartnerCsvColumns.DOCUMENT_ADDRESS_ID));

	}


	@Test
	public void testCreateB2BRowsWithDocumentAddresses()
	{
		//		final OrderModel order = new OrderModel();
		order.setCode(CODE);

		final AddressModel paymentAddress = new AddressModel();
		fillAddress(paymentAddress);
		order.setPaymentAddress(paymentAddress);

		final AddressModel deliveryAddress = new AddressModel();
		fillAddress(deliveryAddress);
		order.setDeliveryAddress(deliveryAddress);

		final BaseSiteModel site = new BaseSiteModel();
		site.setChannel(SiteChannel.B2B);
		order.setSite(site);

		final B2BCustomerModel b2BCustomer = new B2BCustomerModel();
		b2BCustomer.setCustomerID(USER_ID);
		order.setUser(b2BCustomer);

		//		final B2BUnitModel unit = new B2BUnitModel();
		//		final Set groups = new HashSet<PrincipalGroupModel>();
		//		final B2BUnitModel unit1 = new B2BUnitModel();
		//		unit1.setUid(UNIT_ID);
		//		groups.add(unit1);
		//		unit.setGroups(groups);
		//		unit.setUid("SOME ID");
		//		order.setUnit(unit);

		final LanguageModel language_de = new LanguageModel();
		language_de.setIsocode(DE);
		order.setLanguage(language_de);

		final List<Map<String, Object>> rows = cut.createRows(order);

		Map<String, Object> row = rows.get(0);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(null, row.get(PartnerCsvColumns.LANGUAGE_ISO_CODE));
		assertEquals(PartnerRoles.SOLD_TO.getCode(), row.get(PartnerCsvColumns.PARTNER_ROLE_CODE));
		assertEquals(UNIT_ID, row.get(PartnerCsvColumns.PARTNER_CODE));
		assertEquals("", row.get(PartnerCsvColumns.DOCUMENT_ADDRESS_ID));

		row = rows.get(1);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(null, row.get(PartnerCsvColumns.LANGUAGE_ISO_CODE));
		assertEquals(PartnerRoles.CONTACT.getCode(), row.get(PartnerCsvColumns.PARTNER_ROLE_CODE));
		assertEquals(USER_ID, row.get(PartnerCsvColumns.PARTNER_CODE));
		assertEquals("", row.get(PartnerCsvColumns.DOCUMENT_ADDRESS_ID));

		row = rows.get(2);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(DE, row.get(PartnerCsvColumns.LANGUAGE_ISO_CODE));
		assertEquals(PartnerRoles.SHIP_TO.getCode(), row.get(PartnerCsvColumns.PARTNER_ROLE_CODE));
		assertEquals(UNIT_ID, row.get(PartnerCsvColumns.PARTNER_CODE));
		assertEquals(SaporderexchangeConstants.ADDRESS_ONE, row.get(PartnerCsvColumns.DOCUMENT_ADDRESS_ID));
		checkAddressData(row);

		row = rows.get(3);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(DE, row.get(PartnerCsvColumns.LANGUAGE_ISO_CODE));
		assertEquals(PartnerRoles.BILL_TO.getCode(), row.get(PartnerCsvColumns.PARTNER_ROLE_CODE));
		assertEquals(UNIT_ID, row.get(PartnerCsvColumns.PARTNER_CODE));
		assertEquals(SaporderexchangeConstants.ADDRESS_TWO, row.get(PartnerCsvColumns.DOCUMENT_ADDRESS_ID));
		checkAddressData(row);

	}

	@Test
	public void testCreateB2CRows()
	{
		//		final OrderModel order = new OrderModel();
		order.setCode(CODE);

		final AddressModel paymentAddress = new AddressModel();
		fillAddress(paymentAddress);
		order.setPaymentAddress(paymentAddress);

		final AddressModel deliveryAddress = new AddressModel();
		fillAddress(deliveryAddress);
		order.setDeliveryAddress(deliveryAddress);

		final BaseStoreModel store = new BaseStoreModel();
		order.setStore(store);
		final SAPConfigurationModel SAPconfig = new SAPConfigurationModel();
		SAPconfig.setSapcommon_referenceCustomer(REF_CUST);
		store.setSAPConfiguration(SAPconfig);

		final BaseSiteModel site = new BaseSiteModel();
		site.setChannel(SiteChannel.B2C);
		order.setSite(site);

		final LanguageModel language_de = new LanguageModel();
		language_de.setIsocode(DE);
		order.setLanguage(language_de);


		final B2CCustomerHelper b2CCustomerHelper = org.mockito.Mockito.mock(DefaultB2CCustomerHelper.class);
		cut.setB2CCustomerHelper(b2CCustomerHelper);
		Mockito.when(b2CCustomerHelper.determineB2CCustomer(order)).thenReturn(null);

		final List<Map<String, Object>> rows = cut.createRows(order);

		Map<String, Object> row = rows.get(0);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(DE, row.get(PartnerCsvColumns.LANGUAGE_ISO_CODE));
		assertEquals(PartnerRoles.SHIP_TO.getCode(), row.get(PartnerCsvColumns.PARTNER_ROLE_CODE));
		assertEquals(SaporderexchangeConstants.ADDRESS_TWO, row.get(PartnerCsvColumns.DOCUMENT_ADDRESS_ID));
		checkAddressData(row);

		row = rows.get(1);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(DE, row.get(PartnerCsvColumns.LANGUAGE_ISO_CODE));
		assertEquals(PartnerRoles.BILL_TO.getCode(), row.get(PartnerCsvColumns.PARTNER_ROLE_CODE));
		assertEquals(SaporderexchangeConstants.ADDRESS_ONE, row.get(PartnerCsvColumns.DOCUMENT_ADDRESS_ID));
		checkAddressData(row);

		row = rows.get(2);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(DE, row.get(PartnerCsvColumns.LANGUAGE_ISO_CODE));
		assertEquals(PartnerRoles.SOLD_TO.getCode(), row.get(PartnerCsvColumns.PARTNER_ROLE_CODE));
		assertEquals(SaporderexchangeConstants.ADDRESS_ONE, row.get(PartnerCsvColumns.DOCUMENT_ADDRESS_ID));
		checkAddressData(row);
	}


	private void checkAddressData(final Map<String, Object> row)
	{
		assertEquals(FIRST_NAME, row.get(PartnerCsvColumns.FIRST_NAME));
		assertEquals(LAST_NAME, row.get(PartnerCsvColumns.LAST_NAME));
		assertEquals(STREET_NAME, row.get(PartnerCsvColumns.STREET));
		assertEquals(TOWN, row.get(PartnerCsvColumns.CITY));
		assertEquals(PHONE1, row.get(PartnerCsvColumns.TEL_NUMBER));
		assertEquals(STREET_NUMBER, row.get(PartnerCsvColumns.HOUSE_NUMBER));
		assertEquals(POSTAL_CODE, row.get(PartnerCsvColumns.POSTAL_CODE));
		assertEquals(REGION2, row.get(PartnerCsvColumns.REGION_ISO_CODE));
		assertEquals(COUNTRY_ISO_CODE, row.get(PartnerCsvColumns.COUNTRY_ISO_CODE));
		assertEquals(EMAIL, row.get(PartnerCsvColumns.EMAIL));
		assertEquals(MIDDLE_NAME, row.get(PartnerCsvColumns.MIDDLE_NAME));
		assertEquals(MIDDLE_NAME2, row.get(PartnerCsvColumns.MIDDLE_NAME2));
		assertEquals(DISTRICT, row.get(PartnerCsvColumns.DISTRICT));
		assertEquals(BUILDING, row.get(PartnerCsvColumns.BUILDING));
		assertEquals(APPARTMENT, row.get(PartnerCsvColumns.APPARTMENT));
		assertEquals(POBOX, row.get(PartnerCsvColumns.POBOX));
		assertEquals(FAX, row.get(PartnerCsvColumns.FAX));
		assertEquals(TITLE_CODE, row.get(PartnerCsvColumns.TITLE));
	}


	private void fillAddress(final AddressModel address)
	{
		address.setFirstname(FIRST_NAME);
		address.setLastname(LAST_NAME);
		address.setStreetname(STREET_NAME);
		address.setTown(TOWN);
		address.setPhone1(PHONE1);
		address.setStreetnumber(STREET_NUMBER);
		address.setPostalcode(POSTAL_CODE);

		final RegionModel region = new RegionModel();
		region.setIsocodeShort(REGION2);
		address.setRegion(region);

		final CountryModel country = new CountryModel();
		country.setIsocode(COUNTRY_ISO_CODE);
		address.setCountry(country);

		address.setEmail(EMAIL);
		address.setMiddlename(MIDDLE_NAME);
		address.setMiddlename2(MIDDLE_NAME2);
		address.setDistrict(DISTRICT);
		address.setBuilding(BUILDING);
		address.setAppartment(APPARTMENT);
		address.setPobox(POBOX);
		address.setFax(FAX);

		final TitleModel title = new TitleModel();
		title.setCode(TITLE_CODE);
		address.setTitle(title);
	}

}
