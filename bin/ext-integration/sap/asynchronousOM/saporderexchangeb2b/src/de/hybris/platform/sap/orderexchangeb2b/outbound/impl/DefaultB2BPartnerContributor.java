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

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerRoles;
import de.hybris.platform.sap.orderexchange.constants.SaporderexchangeConstants;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultPartnerContributor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Partner contributor for B2B orders to be replicated to SAP ERP system. Considers partner roles soldTo (AG), contact
 * (AP), billTo (RE) and shipTo (WE)
 * 
 */
public class DefaultB2BPartnerContributor extends DefaultPartnerContributor
{

	private B2BUnitService<B2BUnitModel, ?> b2bUnitService;

	@Override
	public List<Map<String, Object>> createRows(final OrderModel order)
	{
		return isB2BOrder(order) ? createB2BRows(order) : super.createRows(order);
	}

	protected List<Map<String, Object>> createB2BRows(final OrderModel order)
	{
		final Map<String, Object> row1 = createPartnerRow(order, PartnerRoles.SOLD_TO, soldToFromOrder(order));
		final Map<String, Object> row2 = createPartnerRow(order, PartnerRoles.CONTACT, contactFromOrder(order));
		final Map<String, Object> row3 = createAddressRow(order, PartnerRoles.SHIP_TO, SaporderexchangeConstants.ADDRESS_ONE);
		final Map<String, Object> row4 = createAddressRow(order, PartnerRoles.BILL_TO, SaporderexchangeConstants.ADDRESS_TWO);

		final List<Map<String, Object>> result = (row4 == null) ? Arrays.asList(row1, row2, row3) : Arrays.asList(row1, row2, row3,
				row4);
		return result;
	}

	protected String contactFromOrder(final OrderModel order)
	{
		return ((B2BCustomerModel) order.getUser()).getCustomerID();
	}

	protected String soldToFromOrder(final OrderModel order)
	{
		final CompanyModel rootUnit = getB2bUnitService().getRootUnit(order.getUnit());
		return rootUnit.getUid();
	}

	protected Map<String, Object> createPartnerRow(final OrderModel order, final PartnerRoles partnerRole, final String partnerId)
	{
		final Map<String, Object> row = new HashMap<>();
		row.put(OrderCsvColumns.ORDER_ID, order.getCode());
		row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, partnerRole.getCode());
		row.put(PartnerCsvColumns.PARTNER_CODE, partnerId);
		row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, "");
		return row;
	}

	protected Map<String, Object> createAddressRow(final OrderModel order, final PartnerRoles partnerRole,
			final String addressNumber)
	{
		final AddressModel address = addressForPartnerRole(order, partnerRole);
		Map<String, Object> row = null;
		if (address != null)
		{
			row = new HashMap<>();
			row.put(OrderCsvColumns.ORDER_ID, order.getCode());
			row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, partnerRole.getCode());
			final String sapCustomer = address.getSapCustomerID();
			if (sapCustomer == null || sapCustomer.isEmpty())
			{
				row.put(PartnerCsvColumns.PARTNER_CODE, soldToFromOrder(order));
				row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, addressNumber);
				mapAddressData(order, address, row);
			}
			else
			{
				row.put(PartnerCsvColumns.PARTNER_CODE, sapCustomer);
				row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, "");
			}
		}
		return row;
	}

	protected AddressModel addressForPartnerRole(final OrderModel order, final PartnerRoles partnerRole)
	{
		AddressModel result = null;
		if (partnerRole == PartnerRoles.SHIP_TO)
		{
			result = order.getDeliveryAddress();
		}
		else if (partnerRole == PartnerRoles.BILL_TO)
		{
			result = order.getPaymentAddress();
		}
		return result;
	}

	protected void mapAddressData(final OrderModel order, final AddressModel address, final Map<String, Object> row)
	{
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
	}

	protected boolean isB2BOrder(final OrderModel orderModel)
	{
		return orderModel.getSite().getChannel() == SiteChannel.B2B;
	}

	@SuppressWarnings("javadoc")
	public B2BUnitService<B2BUnitModel, ?> getB2bUnitService()
	{
		return b2bUnitService;
	}

	@SuppressWarnings("javadoc")
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, ?> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

}
