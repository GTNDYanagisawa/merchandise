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
package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerRoles;
import de.hybris.platform.sap.orderexchange.constants.SaporderexchangeConstants;
import de.hybris.platform.sap.orderexchange.outbound.B2CCustomerHelper;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Builds the Row map for the CSV files for the Partner in an Order
 */
public class DefaultPartnerContributor implements RawItemContributor<OrderModel>
{
	private B2CCustomerHelper b2CCustomerHelper;

	@Override
	public Set<String> getColumns()
	{
		return new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID, PartnerCsvColumns.PARTNER_ROLE_CODE,
				PartnerCsvColumns.PARTNER_CODE, PartnerCsvColumns.DOCUMENT_ADDRESS_ID, PartnerCsvColumns.FIRST_NAME,
				PartnerCsvColumns.LAST_NAME, PartnerCsvColumns.STREET, PartnerCsvColumns.CITY, PartnerCsvColumns.TEL_NUMBER,
				PartnerCsvColumns.HOUSE_NUMBER, PartnerCsvColumns.POSTAL_CODE, PartnerCsvColumns.REGION_ISO_CODE,
				PartnerCsvColumns.COUNTRY_ISO_CODE, PartnerCsvColumns.EMAIL, PartnerCsvColumns.LANGUAGE_ISO_CODE,
				PartnerCsvColumns.MIDDLE_NAME, PartnerCsvColumns.MIDDLE_NAME2, PartnerCsvColumns.DISTRICT,
				PartnerCsvColumns.BUILDING, PartnerCsvColumns.APPARTMENT, PartnerCsvColumns.POBOX, PartnerCsvColumns.FAX,
				PartnerCsvColumns.TITLE));
	}

	@Override
	public List<Map<String, Object>> createRows(final OrderModel order)
	{
		final List<Map<String, Object>> result = new ArrayList<>(3);
		final AddressModel paymentAddress = order.getPaymentAddress();
		AddressModel deliveryAddress = order.getDeliveryAddress();
		if (deliveryAddress == null)
		{
			deliveryAddress = paymentAddress;
		}

		final String b2cCustomer = b2CCustomerHelper.determineB2CCustomer(order);
		final String sapcommon_Customer = b2cCustomer != null ? b2cCustomer : order.getStore().getSAPConfiguration()
				.getSapcommon_referenceCustomer();

		final String deliveryAddressNumber = getShipToAddressNumber();
		Map<String, Object> row = mapAddressData(order, deliveryAddress);
		row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, PartnerRoles.SHIP_TO.getCode());
		row.put(PartnerCsvColumns.PARTNER_CODE, sapcommon_Customer);
		row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, deliveryAddressNumber);
		result.add(row);

		String soldToAddressNumber;
		if (paymentAddress != null)
		{
			final String paymentAddressNumber = getSoldToAddressNumber();
			row = mapAddressData(order, paymentAddress);
			row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, PartnerRoles.BILL_TO.getCode());
			row.put(PartnerCsvColumns.PARTNER_CODE, sapcommon_Customer);
			row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, paymentAddressNumber);
			result.add(row);
			soldToAddressNumber = paymentAddressNumber;
			row = mapAddressData(order, paymentAddress);
		}
		else
		{
			soldToAddressNumber = deliveryAddressNumber;
			row = mapAddressData(order, deliveryAddress);
		}

		row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, PartnerRoles.SOLD_TO.getCode());
		row.put(PartnerCsvColumns.PARTNER_CODE, sapcommon_Customer);
		row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, soldToAddressNumber);
		result.add(row);

		return result;
	}

	protected String getSoldToAddressNumber()
	{
		return SaporderexchangeConstants.ADDRESS_ONE;
	}

	protected String getShipToAddressNumber()
	{
		return SaporderexchangeConstants.ADDRESS_TWO;
	}

	protected Map<String, Object> mapAddressData(final OrderModel order, final AddressModel address)
	{
		final Map<String, Object> row = new HashMap<>();
		row.put(OrderCsvColumns.ORDER_ID, order.getCode());
		row.put(PartnerCsvColumns.FIRST_NAME, address.getFirstname());
		row.put(PartnerCsvColumns.LAST_NAME, address.getLastname());
		row.put(PartnerCsvColumns.STREET, address.getStreetname());
		row.put(PartnerCsvColumns.CITY, address.getTown());
		row.put(PartnerCsvColumns.TEL_NUMBER, address.getPhone1());
		row.put(PartnerCsvColumns.HOUSE_NUMBER, address.getStreetnumber());
		row.put(PartnerCsvColumns.POSTAL_CODE, address.getPostalcode());
		row.put(PartnerCsvColumns.REGION_ISO_CODE, (address.getRegion() != null) ? address.getRegion().getIsocodeShort() : "");
		row.put(PartnerCsvColumns.COUNTRY_ISO_CODE, (address.getCountry() != null) ? address.getCountry().getIsocode() : "");
		row.put(PartnerCsvColumns.EMAIL, address.getEmail());
		row.put(PartnerCsvColumns.MIDDLE_NAME, address.getMiddlename());
		row.put(PartnerCsvColumns.MIDDLE_NAME2, address.getMiddlename2());
		row.put(PartnerCsvColumns.DISTRICT, address.getDistrict());
		row.put(PartnerCsvColumns.BUILDING, address.getBuilding());
		row.put(PartnerCsvColumns.APPARTMENT, address.getAppartment());
		row.put(PartnerCsvColumns.POBOX, address.getPobox());
		row.put(PartnerCsvColumns.FAX, address.getFax());
		row.put(PartnerCsvColumns.LANGUAGE_ISO_CODE, order.getLanguage().getIsocode());
		row.put(PartnerCsvColumns.TITLE, (address.getTitle() != null) ? address.getTitle().getCode() : "");

		return row;
	}

	@SuppressWarnings("javadoc")
	public B2CCustomerHelper getB2CCustomerHelper()
	{
		return b2CCustomerHelper;
	}

	@SuppressWarnings("javadoc")
	public void setB2CCustomerHelper(final B2CCustomerHelper b2cCustomerHelper)
	{
		b2CCustomerHelper = b2cCustomerHelper;
	}
}
