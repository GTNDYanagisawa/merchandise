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
package de.hybris.platform.sap.sapordermgmtservices.converters.populator;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionHelper;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerListEntry;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BusinessStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.OverallStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigInteger;
import java.util.Date;

import org.apache.log4j.Logger;





/**
 * Populates a hybris order DAO from the BOL representation of an order
 * 
 * @param <SOURCE>
 *           BOL representation of an order
 * @param <TARGET>
 *           DAO representation of an order
 */
public class DefaultOrderPopulator<SOURCE extends Order, TARGET extends OrderData> extends
		DefaultAbstractOrderPopulator<SOURCE, TARGET>
{
	private static final Logger LOG = Logger.getLogger(DefaultOrderPopulator.class);

	private Converter<UserModel, CustomerData> b2bCustomerConverter;

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		super.populate(source, target);
		target.setCode(getExternalFormat(source.getTechKey().getIdAsString()));

		final Header header = source.getHeader();

		populateOverallstatus(header, target);
		populateShippingstatus(header, target);
		populateCondensedstatus(target);
		populateCreatedAt(source, target);

		populatePartners(source, target);

	}

	/**
	 * Converts the created at date into a format the UI can understand. We need to pass it as array with size 1 to the
	 * OrderData target object, this makes sure that <spring:theme> can digest it. Otherwise a date with ',' as separator
	 * can lead to issues, as the date then would be interpreted as array with multiple entries.
	 * 
	 * @param source
	 *           BOL order header
	 * @param target
	 *           Order data
	 */
	protected void populateCreatedAt(final SOURCE source, final TARGET target)
	{
		final String[] createdDateSAPFormat = new String[]
		{ convertDateToLongDateString(source.getHeader().getCreatedAt()) };
		target.setCreatedDateSAPFormat(createdDateSAPFormat);
	}

	/**
	 * @param source
	 * @param target
	 */
	protected void populatePartners(final SOURCE source, final TARGET target)
	{
		final PartnerList partnerList = source.getHeader().getPartnerList();

		if (partnerList == null)
		{
			return;
		}

		final PartnerListEntry contact = partnerList.getContactData();
		if (contact != null)
		{

			final CustomerModel b2bCustomer = getSapPartnerService().getB2BCustomerForSapContactId(contact.getPartnerId());

			if (b2bCustomer != null)
			{
				final CustomerData b2bCustomerData = b2bCustomerConverter.convert(b2bCustomer);
				target.setB2bCustomerData(b2bCustomerData);
			}
		}
		else
		{
			LOG.info("No contact person available for order: " + source.getTechKey() + ". No B2BCustomerData available");
		}

	}

	@Override
	protected void populateHeader(final SOURCE source, final TARGET target)
	{
		super.populateHeader(source, target);
		final Header header = source.getHeader();
		target.setPurchaseOrderNumber(header.getPurchaseOrderExt());
		target.setCreated(header.getCreatedAt());
	}

	/**
	 * Populates hybris overall status from BOL sales document header
	 * 
	 * @param source
	 *           BOL sales document header
	 * @param target
	 *           hybris order data
	 */
	protected void populateOverallstatus(final Header source, final TARGET target)
	{
		final OverallStatus overallStatus = source.getOverallStatus();

		if (isCompletedOrCanceled(overallStatus))
		{
			target.setStatus(OrderStatus.COMPLETED);
			target.setStatusDisplay(OrderStatus.COMPLETED.getCode().toLowerCase());
		}
		else
		{
			target.setStatus(OrderStatus.CREATED);
			target.setStatusDisplay(OrderStatus.CREATED.getCode().toLowerCase());
			return;
		}

	}

	private boolean isCompletedOrCanceled(final OverallStatus overallStatus)
	{
		final boolean completed = overallStatus != null && overallStatus.getStatus().getStatus() == 'C';
		final boolean cancelled = overallStatus != null && overallStatus.getStatus().getStatus() == 'D';
		return completed || cancelled;
	}

	/**
	 * Populates hybris shipping (in hybris terminology: delivery) status from BOL sales document header
	 * 
	 * @param source
	 *           BOL sales document header
	 * @param target
	 *           hybris order data
	 */
	protected void populateShippingstatus(final Header source, final TARGET target)
	{
		final BusinessStatus shippingStatus = source.getShippingStatus();

		if (shippingStatus == null)
		{
			target.setDeliveryStatus(DeliveryStatus.NOTSHIPPED);
			target.setDeliveryStatusDisplay(DeliveryStatus.NOTSHIPPED.getCode().toLowerCase());
			return;
		}

		switch (shippingStatus.getStatus().getStatus())
		{
			case 'C':
				target.setDeliveryStatus(DeliveryStatus.SHIPPED);
				target.setDeliveryStatusDisplay(DeliveryStatus.SHIPPED.getCode().toLowerCase());
				break;
			case 'B':
				target.setDeliveryStatus(DeliveryStatus.PARTSHIPPED);
				target.setDeliveryStatusDisplay(DeliveryStatus.PARTSHIPPED.getCode().toLowerCase());
				break;
			default:
				target.setDeliveryStatus(DeliveryStatus.NOTSHIPPED);
				target.setDeliveryStatusDisplay(DeliveryStatus.NOTSHIPPED.getCode().toLowerCase());
		}
	}

	/**
	 * Compiles a condensed status from overall and delivery status, see {@link OrderData#setCondensedStatus(String)}. <br>
	 * In case the overall status is completed, condensed status will also be set to completed, otherwise condensed
	 * status will be equal to delivery status.
	 * 
	 * @param target
	 *           Order data with condensed status
	 */
	protected void populateCondensedstatus(final TARGET target)
	{
		if (target.getStatus() == OrderStatus.CREATED)
		{
			target.setCondensedStatus(target.getDeliveryStatus().getCode().toLowerCase());
			return;
		}
		else if (target.getStatus() == OrderStatus.COMPLETED)
		{
			target.setCondensedStatus(target.getStatus().getCode().toLowerCase());
			return;
		}
	}

	/**
	 * Compiles external format of SAP order ID by just cutting leading zeros in case ID is purely numerical
	 * 
	 * @param orderId
	 *           Technical SAP order ID
	 * @return Order ID in external format
	 */
	protected String getExternalFormat(final String orderId)
	{
		try
		{
			final BigInteger orderIdNumeric = new BigInteger(orderId);
			return orderIdNumeric.toString();
		}
		catch (final NumberFormatException ex)
		{
			return orderId;
		}
	}

	/**
	 * @param b2bCustomerConverter
	 *           the b2bCustomerConverter to set
	 */
	public void setB2bCustomerConverter(final Converter<UserModel, CustomerData> b2bCustomerConverter)
	{
		this.b2bCustomerConverter = b2bCustomerConverter;
	}

	/**
	 * Converts a date to a localized string
	 * 
	 * @param date
	 * @return Date, localized according the session locale
	 */
	protected String convertDateToLongDateString(final Date date)
	{
		return ConversionHelper.convertDateToLocalizedString(date);
	}




}
