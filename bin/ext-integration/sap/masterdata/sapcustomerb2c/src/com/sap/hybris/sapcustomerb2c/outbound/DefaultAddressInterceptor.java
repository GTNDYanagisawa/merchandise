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
package com.sap.hybris.sapcustomerb2c.outbound;

import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.REPLICATEREGISTEREDUSER;

import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.configuration.global.impl.SAPGlobalConfigurationServiceImpl;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.store.services.BaseStoreService;

import org.apache.log4j.Logger;


/**
 * If default shipment address was updated send default shipment address to Data Hub in case of user replication is
 * active and the address is related to a sap consumer. This is indicated by the filled sap contact id.
 * 
 */
public class DefaultAddressInterceptor implements ValidateInterceptor<AddressModel>
{

	private static final Logger LOGGER = Logger.getLogger(com.sap.hybris.sapcustomerb2c.outbound.DefaultAddressInterceptor.class
			.getName());
	private DefaultStoreSessionFacade storeSessionFacade;
	private CustomerExportService customerExportService;
	private BaseStoreService baseStoreService;
	private SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService;

	@Override
	public void onValidate(final AddressModel addressModel, final InterceptorContext ctx) throws InterceptorException
	{
		// only if replication of user is requested start publishing to Data Hub process
		if (getSapCoreSAPGlobalConfigurationService() != null
				&& (Boolean) getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER))
		{
			if (addressModel.getOwner() instanceof CustomerModel)
			{
				final CustomerModel customerModel = ((CustomerModel) addressModel.getOwner());
				// check if default shipment address was updated
				if (ctx.isModified(addressModel, AddressModel.COUNTRY) || ctx.isModified(addressModel, AddressModel.STREETNAME)
						|| ctx.isModified(addressModel, AddressModel.PHONE1) || ctx.isModified(addressModel, AddressModel.FAX)
						|| ctx.isModified(addressModel, AddressModel.TOWN) || ctx.isModified(addressModel, AddressModel.POSTALCODE)
						|| ctx.isModified(addressModel, AddressModel.STREETNUMBER) || ctx.isModified(addressModel, AddressModel.REGION)
						&& customerModel.getSapContactID() != null)
				{
					if (customerModel.getDefaultShipmentAddress() != null && isDefaultShipmentAddress(addressModel))
					{
						final String baseStoreUid = baseStoreService.getCurrentBaseStore() != null ? baseStoreService
								.getCurrentBaseStore().getUid() : null;
						final String sessionLanguage = getStoreSessionFacade().getCurrentLanguage() != null ? getStoreSessionFacade()
								.getCurrentLanguage().getIsocode() : null;
						getCustomerExportService().sendCustomerData(customerModel, baseStoreUid, sessionLanguage, addressModel);
					}
					else if (LOGGER.isDebugEnabled())
					{
						LOGGER.debug("Default shipment address is null or updated address " + addressModel.getPk()
								+ " is not a default shipment address");
					}
				}
				else if (LOGGER.isDebugEnabled())
				{
					LOGGER.debug("Address " + addressModel.getPk() + " was not send to Data Hub.");
					LOGGER.debug("Address country modified =  " + ctx.isModified(addressModel, AddressModel.COUNTRY));
					LOGGER.debug("Address streetname modified = " + ctx.isModified(addressModel, AddressModel.STREETNAME));
					LOGGER.debug("Address phone1 modified = " + ctx.isModified(addressModel, AddressModel.PHONE1));
					LOGGER.debug("Address fax modified = " + ctx.isModified(addressModel, AddressModel.FAX));
					LOGGER.debug("Address town modified = " + ctx.isModified(addressModel, AddressModel.TOWN));
					LOGGER.debug("Address postalcode modified = " + ctx.isModified(addressModel, AddressModel.POSTALCODE));
					LOGGER.debug("Address streetnumber modified = " + ctx.isModified(addressModel, AddressModel.STREETNUMBER));
					LOGGER.debug("Address region modified = " + ctx.isModified(addressModel, AddressModel.REGION));
					LOGGER.debug("Customer sapContactId = " + customerModel.getSapContactID());
				}
			}
		}
		else if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Address " + addressModel.getPk() + " was not send to Data Hub. replicate register user not active");
		}

	}

	/**
	 * @param addressModel
	 * @return <code>true</code> if the address is also assigned as default shipment address to the address owner
	 */
	protected boolean isDefaultShipmentAddress(final AddressModel addressModel)
	{
		return ((CustomerModel) addressModel.getOwner()).getDefaultShipmentAddress().getPk().equals(addressModel.getPk());

	}


	/**
	 * @return storeSessionFacade
	 */
	public DefaultStoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	/**
	 * set storeSessionFacade
	 * 
	 * @param storeSessionFacade
	 */
	public void setStoreSessionFacade(final DefaultStoreSessionFacade storeSessionFacade)
	{
		this.storeSessionFacade = storeSessionFacade;
	}

	/**
	 * @return customerExportService
	 */
	public CustomerExportService getCustomerExportService()
	{
		return customerExportService;
	}

	/**
	 * set customerExportService
	 * 
	 * @param customerExportService
	 */
	public void setCustomerExportService(final CustomerExportService customerExportService)
	{
		this.customerExportService = customerExportService;
	}

	/**
	 * @return baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * set baseStoreService
	 * 
	 * @param baseStoreService
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return sapCoreSAPGlobalConfigurationService
	 */
	public SAPGlobalConfigurationServiceImpl getSapCoreSAPGlobalConfigurationService()
	{
		return sapCoreSAPGlobalConfigurationService;
	}

	/**
	 * set sapCoreSAPGlobalConfigurationService
	 * 
	 * @param sapCoreSAPGlobalConfigurationService
	 */
	public void setSapCoreSAPGlobalConfigurationService(
			final SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService)
	{
		this.sapCoreSAPGlobalConfigurationService = sapCoreSAPGlobalConfigurationService;
	}

}
