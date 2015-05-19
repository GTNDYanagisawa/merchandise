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
package sap.hybris.integration.models.services.impl;

import org.springframework.beans.factory.annotation.Autowired;

 

import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.exceptions.SAPModelRuntimeException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import sap.hybris.integration.models.model.ReferenceDistributionChannelMappingModel;
import sap.hybris.integration.models.model.ReferenceDivisionMappingModel;
import sap.hybris.integration.models.services.SalesAreaService;

/**
 * Default sales area service implementation for accessing common distribution channels and divisions.
 */
public class DefaultSalesAreaService implements SalesAreaService{
	
	private BaseStoreService baseStoreService;


	/**
	 * Accessing the tables for common channels and divisions
	 */
	@Autowired
	protected FlexibleSearchService flexibleSearchService;	//NOPMD

	@Override
	public String getSalesOrganization() {
		SAPConfigurationModel configuration = getCurrentSAPConfiguration();
		return configuration.getSapcommon_salesOrganization();
	}

	private SAPConfigurationModel getCurrentSAPConfiguration() {
	BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
	if (baseStore == null){
		throw new SAPModelRuntimeException("No base store available");
	}
		SAPConfigurationModel sapConfiguration = baseStore.getSAPConfiguration();
		if (sapConfiguration == null){
			throw new SAPModelRuntimeException("No SAP configuration available");			
		}
		return sapConfiguration;
	}

	@Override
	public String getDistributionChannel() {
		return getCurrentSAPConfiguration().getSapcommon_distributionChannel();
	}

	@Override
	public String getDistributionChannelForConditions() {
		String distributionChannel = getDistributionChannel();
		String salesOrg = getSalesOrganization();
		return getCommonDistributionChannelConditions(salesOrg, distributionChannel);

	}

	@Override
	public String getDistributionChannelForCustomerMaterial() {
		String distributionChannel = getDistributionChannel();
		String salesOrg = getSalesOrganization();
		return getCommonDistributionChannelCustMaster(salesOrg, distributionChannel);
	}

	@Override
	public String getDivision() {
		return getCurrentSAPConfiguration().getSapcommon_division();
	}

	@Override
	public String getDivisionForConditions() {
		String division = getDivision();
		String salesOrg = getSalesOrganization();
		return getCommonDivisionConditions(salesOrg, division);
	}

	@Override
	public String getDivisionForCustomerMaterial() {
		String division = getDivision();
		String salesOrg = getSalesOrganization();
		return getCommonDivisionCustMaster(salesOrg, division);
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	/**
	 * @param baseStoreService the baseStoreService to set
	 */
	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}
	
	/**
	 * Read common distribution channel for condition maintenance
	 * @param salesOrganization 
	 * @param distributionChannel
	 * @return The common channel for condition maintenance
	 */
	protected String getCommonDistributionChannelConditions(
			final String salesOrganization, final String distributionChannel) {
		final ReferenceDistributionChannelMappingModel referenceDistributionChannel = getCommonDistributionChannel(
				salesOrganization, distributionChannel);
		if (referenceDistributionChannel != null) {
			return referenceDistributionChannel.getRefDistChannelConditions();
		} else {
			return null;
		}
	}
	
	/**
	 * Read common distribution channel for customer and material master
	 * @param salesOrganization
	 * @param distributionChannel
	 * @return The common channel for customer and material master
	 */
	protected String getCommonDistributionChannelCustMaster(
			final String salesOrganization, final String distributionChannel) {
		final ReferenceDistributionChannelMappingModel referenceDistributionChannel = getCommonDistributionChannel(
				salesOrganization, distributionChannel);
		if (referenceDistributionChannel != null) {
			return referenceDistributionChannel.getRefDistChannelCustMat();
		} else {
			return null;
		}
	}	

	/**
	 * @param salesOrganization
	 * @param distributionChannel
	 * @return
	 */
	ReferenceDistributionChannelMappingModel getCommonDistributionChannel(
			final String salesOrganization, final String distributionChannel) {
		final ReferenceDistributionChannelMappingModel example = new ReferenceDistributionChannelMappingModel();
		example.setSalesOrganization(salesOrganization);
		example.setDistChannel(distributionChannel);
		final ReferenceDistributionChannelMappingModel referenceDistributionChannel = flexibleSearchService
				.getModelByExample(example);
		if (referenceDistributionChannel == null){
			throw new SAPModelRuntimeException("No distribution channel mapping found");
		}
		return referenceDistributionChannel;
	}

	/**
	 * Read common division for customer and material master
	 * @param salesOrganization
	 * @param division
	 * @return Common division
	 */
	protected String getCommonDivisionCustMaster(final String salesOrganization,
			final String division) {
		final ReferenceDivisionMappingModel referenceDivision = getCommonDivsion(
				salesOrganization, division);
		if (referenceDivision != null) {
			return referenceDivision.getRefDivisionCustomer();
		} else {
			return null;
		}
	}
	
	/**
	 * Read common division for condition maintenance
	 * @param salesOrganization
	 * @param division
	 * @return Common division
	 */
	protected String getCommonDivisionConditions(final String salesOrganization,
			final String division) {
		final ReferenceDivisionMappingModel referenceDivision = getCommonDivsion(
				salesOrganization, division);
		if (referenceDivision != null) {
			return referenceDivision.getRefDivisionConditions();
		} else {
			return null;
		}
	}	

	/**
	 * @param salesOrganization
	 * @param division
	 * @return
	 */
	ReferenceDivisionMappingModel getCommonDivsion(
			final String salesOrganization, final String division) {
		final ReferenceDivisionMappingModel example = new ReferenceDivisionMappingModel();
		example.setSalesOrganization(salesOrganization);
		example.setDivision(division);
		final ReferenceDivisionMappingModel referenceDivision = flexibleSearchService
				.getModelByExample(example);
		if (referenceDivision == null){
			throw new SAPModelRuntimeException("No distribution channel mapping found");
		}
		
		return referenceDivision;
	}	

}
