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
package com.sap.hybris.reco.common.util;

import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import de.hybris.platform.sap.core.configuration.http.HTTPDestination;
import de.hybris.platform.sap.core.configuration.http.impl.HTTPDestinationServiceImpl;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;

import javax.annotation.Resource;


/**
 *
 */
public class HMCConfigurationReader
{
	//private static final String SERVICE_URL = "https://ldciank.wdf.sap.corp:44300/sap/opu/odata/sap/PROD_RECO_SRV/";
	private static final String SERVICE_URL = "/sap/opu/odata/sap/PROD_RECO_SRV";

	protected String path = "";
	protected String user = "";
	protected String password = "";

	@Resource(name = "sapCoreDefaultSAPGlobalConfigurationService")
	private SAPGlobalConfigurationService globalConfigurationService;
	
	@Resource(name = "sapCoreHTTPDestinationService")
	private HTTPDestinationServiceImpl httpDestinationService;
	private HTTPDestination httpDestination;
	private String httpDestinationId;
	
	@Resource(name = "sapPRIModuleConfigurationAccess")
	private ModuleConfigurationAccess baseStoreConfigurationService;
	
	private String rfcDestinationId;
	
	private String userType;
	private String itemType;


	/**
	 * Get the extensions configuration parameters from the hMC SAP Global Configuration
	 */
	public void loadPRIConfiguration()
	{
		String httpId = (String) globalConfigurationService.getProperty("sapproductrecommendation_httpdest");		
		this.setHttpDestinationId(httpId);
		loadHTTPDestination();
	}

	/**
	 * Get the HTTP Destination details from the hMC SAP Integration HTTP Destination configuration
	 * 
	 */
	public void loadHTTPDestination()
	{

		if (this.httpDestinationService != null)
		{
			this.httpDestination = this.httpDestinationService.getHTTPDestination(this.getHttpDestinationId());
			if (this.httpDestination != null)
			{
				this.path = this.httpDestination.getTargetURL() + SERVICE_URL;
				this.user = this.httpDestination.getUserid();
				this.password = this.httpDestination.getPassword();
			}
		}
	}
	
	/**
	 * Get the RFC Destination details from the hMC SAP Integration HTTP Destination configuration
	 * 
	 */
	public void loadRFCConfiguration()
	{
		String rfcId = (String) globalConfigurationService.getProperty("sapproductrecommendation_rfcdest");
		this.setRfcDestinationId(rfcId);		
	}
	
	/**
	 * Get the User Type from the PRI configuration in the Base Store configuration
	 * 
	 */
	public void loadUserTypeConfiguration()
	{
		String userType = "";
		if (baseStoreConfigurationService!= null) {
			userType = (String) baseStoreConfigurationService.getProperty("sapproductrecommendation_usertype");
		}
		this.setUserType(userType);		
	}
	
	public SAPGlobalConfigurationService getGlobalConfigurationService()
	{
		return globalConfigurationService;
	}

	public void setGlobalConfigurationService(SAPGlobalConfigurationService globalConfigurationService)
	{
		this.globalConfigurationService = globalConfigurationService;
	}
	
	public ModuleConfigurationAccess getBaseStoreConfigurationService()
	{
		return baseStoreConfigurationService;
	}


	public void setBaseStoreConfigurationService(ModuleConfigurationAccess baseStoreConfigurationService)
	{
		this.baseStoreConfigurationService = baseStoreConfigurationService;
	}

	public HTTPDestinationServiceImpl getHttpDestinationService()
	{
		return httpDestinationService;
	}

	public void setHttpDestinationService(HTTPDestinationServiceImpl httpDestinationService)
	{
		this.httpDestinationService = httpDestinationService;
	}

	public HTTPDestination getHttpDestination()
	{
		return httpDestination;
	}

	public void setHttpDestination(HTTPDestination httpDestination)
	{
		this.httpDestination = httpDestination;
	}

	public String getHttpDestinationId()
	{
		return httpDestinationId;
	}

	public void setHttpDestinationId(String httpDestinationId)
	{
		this.httpDestinationId = httpDestinationId;
	}
	

	public String getRfcDestinationId()
	{
		this.loadRFCConfiguration();
		return rfcDestinationId;
	}

	public void setRfcDestinationId(String rfcDestinationId)
	{
		this.rfcDestinationId = rfcDestinationId;
	}

	public String getUserType()
	{
		this.loadUserTypeConfiguration();
		return userType;
	}

	public void setUserType(String userType)
	{
		this.userType = userType;
	}
	
	public void setItemType(String itemType)
	{
		this.itemType = itemType;
	}


	/**
	 * @return the path
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * @param path
	 *           the path to set
	 */
	public void setPath(final String path)
	{
		this.path = path;
	}

	/**
	 * @return the user
	 */
	public String getUser()
	{
		return this.user;
	}

	/**
	 * @param user
	 *           the user to set
	 */
	public void setUser(final String user)
	{
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return this.password;
	}

	/**
	 * @param password
	 *           the password to set
	 */
	public void setPassword(final String password)
	{
		this.password = password;
	}

	public String getFilterCategory()
	{
		// YTODO Auto-generated method stub
		return null;
	}
	



}
