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
package de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.impl;

import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DistChannelMapping;


/**
 * Class contains the mapping information for the sales organisation and distribution channel <br>
 *
 * @version 1.0
 */
public class DistChannelMappingImpl implements DistChannelMapping {

    protected  String distChannelForConditions ;
    protected  String distChannelForSalesDocTypes;
    protected  String distChannelForCustomerMatirial;
    protected  String referencePlant;
    protected  String distChainCategory;
    protected  String allowedPricingLevel;
    
    public String getDistChannelForConditions() {
        return distChannelForConditions;
    }
    public void setDistChannelForConditions(String distChannelForConditions) {
        this.distChannelForConditions = distChannelForConditions;
    }
    public String getDistChannelForSalesDocTypes() {
        return distChannelForSalesDocTypes;
    }
    public void setDistChannelForSalesDocTypes(String distChannelForSalesDocTypes) {
        this.distChannelForSalesDocTypes = distChannelForSalesDocTypes;
    }
    public String getDistChannelForCustomerMatirial() {
        return distChannelForCustomerMatirial;
    }
    public void setDistChannelForCustomerMatirial(String distChannelForCustomerMatirial) {
        this.distChannelForCustomerMatirial = distChannelForCustomerMatirial;
    }
    public String getReferencePlant() {
        return referencePlant;
    }
    public void setReferencePlant(String referencePlant) {
        this.referencePlant = referencePlant;
    }
    public String getDistChainCategory() {
        return distChainCategory;
    }
    public void setDistChainCategory(String distChainCategory) {
        this.distChainCategory = distChainCategory;
    }
    public String getAllowedPricingLevel() {
        return allowedPricingLevel;
    }
    public void setAllowedPricingLevel(String allowedPricingLevel) {
        this.allowedPricingLevel = allowedPricingLevel;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((allowedPricingLevel == null) ? 0 : allowedPricingLevel.hashCode());
        result = prime * result + ((distChainCategory == null) ? 0 : distChainCategory.hashCode());
        result = prime * result
                 + ((distChannelForConditions == null) ? 0 : distChannelForConditions.hashCode());
        result = prime
                 * result
                 + ((distChannelForCustomerMatirial == null)
                                                            ? 0
                                                            : distChannelForCustomerMatirial.hashCode());
        result = prime
                 * result
                 + ((distChannelForSalesDocTypes == null) ? 0
                                                         : distChannelForSalesDocTypes.hashCode());
        result = prime * result + ((referencePlant == null) ? 0 : referencePlant.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DistChannelMappingImpl other = (DistChannelMappingImpl) obj;
        if (allowedPricingLevel == null) {
            if (other.allowedPricingLevel != null)
                return false;
        }
        else if (!allowedPricingLevel.equals(other.allowedPricingLevel))
            return false;
        if (distChainCategory == null) {
            if (other.distChainCategory != null)
                return false;
        }
        else if (!distChainCategory.equals(other.distChainCategory))
            return false;
        if (distChannelForConditions == null) {
            if (other.distChannelForConditions != null)
                return false;
        }
        else if (!distChannelForConditions.equals(other.distChannelForConditions))
            return false;
        if (distChannelForCustomerMatirial == null) {
            if (other.distChannelForCustomerMatirial != null)
                return false;
        }
        else if (!distChannelForCustomerMatirial.equals(other.distChannelForCustomerMatirial))
            return false;
        if (distChannelForSalesDocTypes == null) {
            if (other.distChannelForSalesDocTypes != null)
                return false;
        }
        else if (!distChannelForSalesDocTypes.equals(other.distChannelForSalesDocTypes))
            return false;
        if (referencePlant == null) {
            if (other.referencePlant != null)
                return false;
        }
        else if (!referencePlant.equals(other.referencePlant))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "DistChannelMappingImpl [allowedPricingLevel=" + allowedPricingLevel
               + ", distChainCategory=" + distChainCategory + ", distChannelForConditions="
               + distChannelForConditions + ", distChannelForCustomerMatirial="
               + distChannelForCustomerMatirial + ", distChannelForSalesDocTypes="
               + distChannelForSalesDocTypes + ", referencePlant=" + referencePlant + "]";
    }
   
}
