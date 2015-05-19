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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

import java.util.Map;

/**
 * The PricingInfo object contains settings for pricing purposes. This interface
 * is used to communicate with IPC.
 * 
 */
public interface PricingInfo {

    /**
     * Sets the property procedureName
     * 
     * @param procedureName - procedure name
     */
    public void setProcedureName(String procedureName);

    /**
     * Sets the free good procedure.
     * 
     * @param fGProcedureName key of the free good procedure
     */
    public void setFGProcedureName(String fGProcedureName);

    /**
     * Sets the property documentCurrencyUnit
     * 
     * @param documentCurrencyUnit - document currency unit
     */
    public void setDocumentCurrencyUnit(String documentCurrencyUnit);

    /**
     * Sets the property localCurrencyUnit
     * 
     * @param localCurrencyUnit - local currency unit
     */
    public void setLocalCurrencyUnit(String localCurrencyUnit);

    /**
     * Sets the property salesOrganisation
     * 
     * @param salesOrganisation -sales organization
     */
    public void setSalesOrganisation(String salesOrganisation);

    /**
     * Sets the property salesOrganisationCrm
     * 
     * @param salesOrganisationCrm - sales organization CRM
     */
    public void setSalesOrganisationCrm(String salesOrganisationCrm);

    /**
     * Sets the property distributionChannel
     * 
     * @param distributionChannel - distribution channel
     */
    public void setDistributionChannel(String distributionChannel);

    /**
     * Sets the property distributionChannelOriginal
     * 
     * @param distributionChannelOriginal - distribution channel original
     */
    public void setDistributionChannelOriginal(String distributionChannelOriginal);

    /**
     * Sets the property headerAttributes
     * 
     * @param headerAttributes - header attributes
     */
    public void setHeaderAttributes(Map<String, String> headerAttributes);

    /**
     * Gets the property headerAttributes
     * 
     * @return HeaderAttributes
     */
    public Map<String, String> getHeaderAttributes();

    /**
     * Sets the property itemAttributes
     * 
     * @param itemAttributes - item attributes
     */
    public void setItemAttributes(Map<String, Map<String, Map<String, String>>> itemAttributes);

    /**
     * Gets the property itemAttributes
     * 
     * @return ItemAttributes
     */
    public Map<String, Map<String, Map<String, String>>> getItemAttributes();

    /**
     * Returns the property procedureName
     * 
     * @return procedureName
     */
    public String getProcedureName();

    /**
     * Returns the free good procedure.
     * 
     * @return the free good procedure
     */
    public String getFGProcedureName();

    /**
     * Returns the property documentCurrencyUnit
     * 
     * @return documentCurrencyUnit
     */
    public String getDocumentCurrencyUnit();

    /**
     * Returns the property localCurrencyUnit
     * 
     * @return localCurrencyUnit
     */
    public String getLocalCurrencyUnit();

    /**
     * Returns the property salesOrganisation
     * 
     * @return salesOrganisation
     */
    public String getSalesOrganisation();

    /**
     * Returns the property salesOrganisationCrm
     * 
     * @return salesOrganisationCrm
     */
    public String getSalesOrganisationCrm();

    /**
     * Returns the property distributionChannel
     * 
     * @return distributionChannel
     */
    public String getDistributionChannel();

    /**
     * Returns the property distributionChannelOriginal
     * 
     * @return distributionChannelOriginal
     */
    public String getDistributionChannelOriginal();

}
