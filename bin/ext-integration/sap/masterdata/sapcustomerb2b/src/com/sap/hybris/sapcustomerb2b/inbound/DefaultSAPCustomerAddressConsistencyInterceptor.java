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
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import org.apache.log4j.Logger;


/**
 * PrepareInterceptor for the handling of SAP addresses insure that the address for all which are related to a SAP ERP
 * Customer are in sync
 * 
 */
public class DefaultSAPCustomerAddressConsistencyInterceptor implements PrepareInterceptor
{

	private FlexibleSearchService flexibleSearchService;
	private String query;

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected static final Logger LOGGER = Logger.getLogger(DefaultSAPCustomerAddressConsistencyInterceptor.class);

	@Override
	public void onPrepare(final Object model, final InterceptorContext context) throws InterceptorException
	{

		// check whether the model is an address model and whether the model is modified
		if (model instanceof AddressModel && context.isModified(model))
		{

			final AddressModel address = (AddressModel) model;

			// check whether the SAP customer ID is not null
			if (address.getSapCustomerID() != null)
			{

				// check whether the address is new
				if (context.isNew(model))
				{
					// *** INSERT NEW SAP ADDRESS ***
					// determine UID of address owner
					final String uid = getUIDOfAddressOwner(address);

					// check whether SAP customer ID is equal to UID of address owner 
					if (address.getSapCustomerID().equals(uid))
					{
						// update all addresses with the same SAP customer ID
						updateAllAddressesWithSameSAPCustomerID(context, address);
					}
					else
					{
						// fill address with SAP data of existing SAP source address
						fillAddressWithSAPDataOfExistingSAPSourceAddress(context, address);
					}

				}
				else
				{
					// *** UPDATE EXSTING SAP ADDRESS ***
					// update all addresses with the same SAP customer ID
					updateAllAddressesWithSameSAPCustomerID(context, address);

				}
			}
		}
	}

	protected void updateAllAddressesWithSameSAPCustomerID(final InterceptorContext context, final AddressModel newAddress)
	{

		// read all addresses with corresponding SAP customer ID but ignore address copies bound to orders
		final List<AddressModel> addressesToUpdateWithSameSAPCustomerID = findAddressesBySAPCustomerID(
				newAddress.getSapCustomerID(), Boolean.FALSE);
		if (addressesToUpdateWithSameSAPCustomerID != null && addressesToUpdateWithSameSAPCustomerID.size() > 0)
		{
			for (final AddressModel addressToUpdate : addressesToUpdateWithSameSAPCustomerID)
			{
				updateAddressWithSAPDataOfAddressTemplate(context, newAddress, addressToUpdate, true);
			}
		}

	}

	protected void fillAddressWithSAPDataOfExistingSAPSourceAddress(final InterceptorContext context, final AddressModel newAddress)
	{

		// retrieve SAP source address (owner's UID is equal to SAP customer ID)
		final AddressModel addressTemplate = getSAPSourceAddress(newAddress.getSapCustomerID());
		if (addressTemplate != null)
		{
			// update new address with data from address template
			updateAddressWithSAPDataOfAddressTemplate(context, addressTemplate, newAddress, false);
		}

	}

	protected AddressModel getSAPSourceAddress(final String sapCustomerID)
	{

		// read all addresses with corresponding SAP customer ID but ignore address copies bound to orders
		final List<AddressModel> addressesWithSameSapCustomerID = findAddressesBySAPCustomerID(sapCustomerID, Boolean.FALSE);

		// find address with owner's UID equals to SAP customer ID
		for (final AddressModel address : addressesWithSameSapCustomerID)
		{
			// determine UID of address's owner
			final String uid = getUIDOfAddressOwner(address);

			// check whether owner's UID is equal to SAP customer ID
			if (uid != null && uid.equalsIgnoreCase(sapCustomerID))
			{
				return address;
			}
		}

		return null;
	}

	protected String getUIDOfAddressOwner(final AddressModel address)
	{
		String uid = null;
		if (address.getOwner() instanceof UserModel)
		{
			uid = ((UserModel) address.getOwner()).getUid();
		}
		else if (address.getOwner() instanceof B2BUnitModel)
		{
			uid = ((B2BUnitModel) address.getOwner()).getUid();
		}
		return uid;
	}

	protected void updateAddressWithSAPDataOfAddressTemplate(final InterceptorContext context, final AddressModel addressTemplate,
			final AddressModel addressToUpdate, final boolean saveChanges)
	{

		boolean updated = false;
		if (addressTemplate.getPk() == null || !addressTemplate.getPk().equals(addressToUpdate.getPk()))
		{
			if (addressTemplate.getStreetname() != null && !addressTemplate.getStreetname().equals(addressToUpdate.getStreetname())
					|| (addressTemplate.getStreetname() == null && addressToUpdate.getStreetname() != null))
			{
				addressToUpdate.setStreetname(addressTemplate.getStreetname());
				updated = true;
			}
			if (addressTemplate.getStreetnumber() != null
					&& !addressTemplate.getStreetnumber().equals(addressToUpdate.getStreetnumber())
					|| (addressTemplate.getStreetnumber() == null && addressToUpdate.getStreetnumber() != null))
			{
				addressToUpdate.setStreetnumber(addressTemplate.getStreetnumber());
				updated = true;
			}
			if (addressTemplate.getPostalcode() != null && !addressTemplate.getPostalcode().equals(addressToUpdate.getPostalcode())
					|| (addressTemplate.getPostalcode() == null && addressToUpdate.getPostalcode() != null))
			{
				addressToUpdate.setPostalcode(addressTemplate.getPostalcode());
				updated = true;
			}
			if (addressTemplate.getTown() != null && !addressTemplate.getTown().equals(addressToUpdate.getTown())
					|| (addressTemplate.getTown() == null && addressToUpdate.getTown() != null))
			{
				addressToUpdate.setTown(addressTemplate.getTown());
				updated = true;
			}
			if (addressTemplate.getCountry() != null && !addressTemplate.getCountry().equals(addressToUpdate.getCountry())
					|| (addressTemplate.getCountry() == null && addressToUpdate.getCountry() != null))
			{
				addressToUpdate.setCountry(addressTemplate.getCountry());
				updated = true;
			}
			if (addressTemplate.getRegion() != null && !addressTemplate.getRegion().equals(addressToUpdate.getRegion())
					|| (addressTemplate.getRegion() == null && addressToUpdate.getRegion() != null))
			{
				addressToUpdate.setRegion(addressTemplate.getRegion());
				updated = true;
			}
			if (addressTemplate.getPhone1() != null && !addressTemplate.getPhone1().equals(addressToUpdate.getPhone1())
					|| (addressTemplate.getPhone1() == null && addressToUpdate.getPhone1() != null))
			{
				addressToUpdate.setPhone1(addressTemplate.getPhone1());
				updated = true;
			}
			if (addressTemplate.getFax() != null && !addressTemplate.getFax().equals(addressToUpdate.getFax())
					|| (addressTemplate.getFax() == null && addressToUpdate.getFax() != null))
			{
				addressToUpdate.setFax(addressTemplate.getFax());
				updated = true;
			}
			if (addressTemplate.getPobox() != null && !addressTemplate.getPobox().equals(addressToUpdate.getPobox())
					|| (addressTemplate.getPobox() == null && addressToUpdate.getPobox() != null))
			{
				addressToUpdate.setPobox(addressTemplate.getPobox());
				updated = true;
			}
			if (addressTemplate.getCellphone() != null && !addressTemplate.getCellphone().equals(addressToUpdate.getCellphone())
					|| (addressTemplate.getCellphone() == null && addressToUpdate.getCellphone() != null))
			{
				addressToUpdate.setCellphone(addressTemplate.getCellphone());
				updated = true;
			}
			if (addressTemplate.getDistrict() != null && !addressTemplate.getDistrict().equals(addressToUpdate.getDistrict())
					|| (addressTemplate.getDistrict() == null && addressToUpdate.getDistrict() != null))
			{
				addressToUpdate.setDistrict(addressTemplate.getDistrict());
				updated = true;
			}
		}
		// to avoid problem with recursive calls we set a boolean and 
		// only if the value is set to "true" we will trigger a save
		if (updated && saveChanges)
		{
			context.getModelService().save(addressToUpdate);
			updated = false;
		}
	}

	@SuppressWarnings("unchecked")
	public List<AddressModel> findAddressesBySAPCustomerID(final String sapCustomerID, final Boolean duplicates)
	{
		query = "SELECT {pk} from {" + AddressModel._TYPECODE + "} where {" + AddressModel.SAPCUSTOMERID + "} =?kunnr AND {"
				+ AddressModel.DUPLICATE + "} =?duplicate";

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Search for customer addresses with kunnr" + sapCustomerID + " and duplicate " + duplicates);
		}

		final FlexibleSearchQuery fsQuery = getFlexibleSearchQuery();
		fsQuery.addQueryParameter("kunnr", sapCustomerID);
		fsQuery.addQueryParameter("duplicate", duplicates);
		final SearchResult<AddressModel> searchResult = getFlexibleSearchService().search(fsQuery);
		if (searchResult != null)
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Search Result: found " + searchResult.getCount() + " Records");
			}
			return searchResult.getResult();
		}
		else
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Search Result: nothing found");
			}
			return null;
		}
	}

	protected FlexibleSearchQuery getFlexibleSearchQuery()
	{
		return new FlexibleSearchQuery(query);
	}

	protected String getQuery()
	{
		return query;
	}
}
