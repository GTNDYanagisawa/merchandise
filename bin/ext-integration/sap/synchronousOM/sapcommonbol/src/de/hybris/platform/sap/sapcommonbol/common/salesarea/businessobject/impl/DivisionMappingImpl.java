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

import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DivisionMapping;

/**
 * Class contains the mapping information for the sales organisation and division <br>
 *
 * @version 1.0
 */
public class DivisionMappingImpl implements DivisionMapping {
  
   
    protected String divisionForCustomers;
    protected String divisionForConditions;
    protected String divisionForDocumentTypes;
    
    
    public String getDivisionForCustomers() {
        return divisionForCustomers;
    }
    public void setDivisionForCustomers(String divisionForCustomers) {
        this.divisionForCustomers = divisionForCustomers;
    }
    public String getDivisionForConditions() {
        return divisionForConditions;
    }
    public void setDivisionForConditions(String divisionForConditions) {
        this.divisionForConditions = divisionForConditions;
    }
    public String getDivisionForDocumentTypes() {
        return divisionForDocumentTypes;
    }
    public void setDivisionForDocumentTypes(String divisionForDocumentTypes) {
        this.divisionForDocumentTypes = divisionForDocumentTypes;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((divisionForConditions == null) ? 0 : divisionForConditions.hashCode());
        result = prime * result
                 + ((divisionForCustomers == null) ? 0 : divisionForCustomers.hashCode());
        result = prime * result
                 + ((divisionForDocumentTypes == null) ? 0 : divisionForDocumentTypes.hashCode());
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
        DivisionMappingImpl other = (DivisionMappingImpl) obj;
        if (divisionForConditions == null) {
            if (other.divisionForConditions != null)
                return false;
        }
        else if (!divisionForConditions.equals(other.divisionForConditions))
            return false;
        if (divisionForCustomers == null) {
            if (other.divisionForCustomers != null)
                return false;
        }
        else if (!divisionForCustomers.equals(other.divisionForCustomers))
            return false;
        if (divisionForDocumentTypes == null) {
            if (other.divisionForDocumentTypes != null)
                return false;
        }
        else if (!divisionForDocumentTypes.equals(other.divisionForDocumentTypes))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "DivisionMappingImpl [divisionForConditions=" + divisionForConditions
               + ", divisionForCustomers=" + divisionForCustomers + ", divisionForDocumentTypes="
               + divisionForDocumentTypes + "]";
    }
}
