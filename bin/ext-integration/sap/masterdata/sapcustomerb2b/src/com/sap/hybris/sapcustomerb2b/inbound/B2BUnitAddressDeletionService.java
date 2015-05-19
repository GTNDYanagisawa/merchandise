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

package com.sap.hybris.sapcustomerb2b.inbound;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Service for the consumer notification replication via data hub
 */
public class B2BUnitAddressDeletionService
{

	private static final String MESSAGEFUNCTION_DELETE = "003";

	private FlexibleSearchService flexibleSearchService;

	/**
	 * @return flexible Search Instance
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	private ModelService modelService;

	/**
	 * @return model service instance
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Processes the B2BUnit address deletion triggered via IMPEX from DATAHUB
	 * 
	 * @param messageFunction
	 * @param b2bUnitId
	 * @param sapCustomerId
	 * @param addressUsage
	 * @param addressUsageCounter
	 */
	public void processB2BUnitAddressDeletion(final String messageFunction, final String b2bUnitId, final String sapCustomerId,
			final String addressUsage, final String addressUsageCounter)
	{

		boolean saveNecessary = false;

		// read B2BUnit with transferred B2BUnit ID
		final B2BUnitModel b2bUnit = readB2BUnit(b2bUnitId);
		if (b2bUnit != null)
		{
			// copy existing B2BUnit addresses
			final ArrayList<AddressModel> b2bUnitAddresses = new ArrayList<AddressModel>(b2bUnit.getAddresses());

			// retrieve iterator of B2BUnit addresses for iteration
			final Iterator<AddressModel> b2bUnitAddressesIterator = b2bUnitAddresses.iterator();

			// distinguish between message function "003" (delete) and message function "004" (update)
			if (messageFunction.equalsIgnoreCase(MESSAGEFUNCTION_DELETE))
			{

				// *** DELETION ***
				// remove all addresses with address usage equals transferred address usage and address usage counter equals to transferred address usage counter
				while (b2bUnitAddressesIterator.hasNext())
				{
					final AddressModel address = b2bUnitAddressesIterator.next();
					if (address.getSapAddressUsage().equalsIgnoreCase(addressUsage)
							&& address.getSapAddressUsageCounter().equalsIgnoreCase(addressUsageCounter))
					{
						b2bUnitAddressesIterator.remove();
						saveNecessary = true;
					}
				}

			}
			else
			{
				// *** UPDATE ***
				// remove the address with the address usage equals transferred address usage and address usage counter equals to transferred address usage counter and SAP customerID not equals to transferred customerID
				while (b2bUnitAddressesIterator.hasNext())
				{
					final AddressModel address = b2bUnitAddressesIterator.next();
					if (address.getSapAddressUsage().equalsIgnoreCase(addressUsage)
							&& address.getSapAddressUsageCounter().equalsIgnoreCase(addressUsageCounter)
							&& !address.getSapCustomerID().equalsIgnoreCase(sapCustomerId))
					{
						b2bUnitAddressesIterator.remove();
						saveNecessary = true;
						break;
					}
				}
			}

			if (saveNecessary)
			{
				// set B2BUnit addresses
				b2bUnit.setAddresses(b2bUnitAddresses);

				// prevent empty localized name of B2BUnit
				if (b2bUnit.getLocName() == null)
				{
					b2bUnit.setLocName(b2bUnit.getName());
				}

				// save B2BUnit and its addresses
				modelService.save(b2bUnit);
				modelService.saveAll(b2bUnit.getAddresses());
			}

		}

	}


	/**
	 * Reads the B2BUnit via flexible search with transferred B2BUnitID
	 * 
	 * @param b2bUnitId
	 * @return B2BUnitModel
	 */
	protected B2BUnitModel readB2BUnit(final String b2bUnitId)
	{
		final FlexibleSearchQuery flexibleSearchQuery = getB2BUnitFlexibleSearchQuery(b2bUnitId);

		final B2BUnitModel b2bUnit = this.flexibleSearchService.searchUnique(flexibleSearchQuery);
		if (b2bUnit == null)
		{
			final String msg = "Error while IDoc processing. Called with not existing B2BUnit for UID: " + b2bUnitId;
			throw new IllegalArgumentException(msg);
		}
		return b2bUnit;
	}

	protected FlexibleSearchQuery getB2BUnitFlexibleSearchQuery(final String b2bUnitId)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				"SELECT {c:pk} FROM {B2BUnit AS c} WHERE  {c.UID} = ?b2bUnitId");
		flexibleSearchQuery.addQueryParameter("b2bUnitId", b2bUnitId);
		return flexibleSearchQuery;
	}

}
