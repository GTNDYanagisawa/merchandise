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
package de.hybris.platform.sap.sapordermgmtb2bservices.partner;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * Default implementation for {@link SapPartnerService}
 */
public class DefaultSAPPartnerService implements SapPartnerService
{

	private static final Logger LOG = Logger.getLogger(DefaultSAPPartnerService.class);
	private B2BCustomerService b2bCustomerService;
	private B2BUnitService b2bUnitService;


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService#getCurrentSapCustomerId()
	 */
	@Override
	public String getCurrentSapCustomerId()
	{
		final B2BUnitModel parent = determineB2BUnitOfCurrentB2BCustomer();
		final CompanyModel rootB2BUnit = b2bUnitService.getRootUnit(parent);

		if (rootB2BUnit != null)
		{
			return rootB2BUnit.getUid();
		}
		else
		{
			return null;
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService#getCurrentSapContactId()
	 */
	@Override
	public String getCurrentSapContactId()
	{
		final B2BCustomerModel b2bCustomer = (B2BCustomerModel) b2bCustomerService.getCurrentB2BCustomer();
		if (b2bCustomer != null)
		{
			return b2bCustomer.getCustomerID();
		}
		else
		{
			return null;
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService#getHybrisAddressForSAPCustomerId(java.lang
	 * .String)
	 */
	@Override
	public AddressModel getHybrisAddressForSAPCustomerId(final String sapCustomerId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getHybrisAddressForSAPCustomerId for: " + sapCustomerId);
		}
		final B2BUnitModel parent = determineB2BUnitOfCurrentB2BCustomer();

		if (parent != null)
		{
			final Collection<AddressModel> addresses = parent.getAddresses();
			for (final AddressModel address : addresses)
			{
				final String currentSAPCustomerID = address.getSapCustomerID();
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Address model SAP ID: " + currentSAPCustomerID);
				}
				if (currentSAPCustomerID != null && currentSAPCustomerID.equals(sapCustomerId))
				{
					return address;
				}
			}
		}
		return null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService#getAllowedDeliveryAddresses()
	 */
	@Override
	public Collection<AddressModel> getAllowedDeliveryAddresses()
	{
		final B2BUnitModel parent = determineB2BUnitOfCurrentB2BCustomer();

		if (parent != null)
		{
			return parent.getShippingAddresses();
		}
		return Collections.emptyList();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService#getB2BCustomerForSapContactId(java.lang.
	 * String)
	 */
	@Override
	public B2BCustomerModel getB2BCustomerForSapContactId(final String sapContactId)
	{
		final B2BCustomerModel currentB2BCustomer = (B2BCustomerModel) b2bCustomerService.getCurrentB2BCustomer();
		final CompanyModel salesAreaDependentB2BUnit = b2bUnitService.getParent(currentB2BCustomer);
		final CompanyModel rootB2BUnit = b2bUnitService.getRootUnit(salesAreaDependentB2BUnit);

		final Set b2bCustomers = b2bUnitService.getB2BCustomers(salesAreaDependentB2BUnit);
		b2bUnitService.getB2BCustomers(rootB2BUnit);


		for (final Object b2bCustomer : b2bCustomers)
		{
			final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) b2bCustomer;
			if (sapContactId.equals(b2bCustomerModel.getCustomerID()))
			{
				return b2bCustomerModel;
			}
		}
		LOG.info("No hybris B2B customer found for given contact id.");
		return null;
	}


	/**
	 * @return B2BUnit of currently logged on user
	 */
	private B2BUnitModel determineB2BUnitOfCurrentB2BCustomer()
	{
		final B2BCustomerModel b2bCustomer = (B2BCustomerModel) b2bCustomerService.getCurrentB2BCustomer();
		final B2BUnitModel parent = (B2BUnitModel) b2bUnitService.getParent(b2bCustomer);
		return parent;
	}

	public void setB2bCustomerService(final B2BCustomerService b2bCustomerService)
	{
		this.b2bCustomerService = b2bCustomerService;
	}


	public void setB2bUnitService(final B2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

}
