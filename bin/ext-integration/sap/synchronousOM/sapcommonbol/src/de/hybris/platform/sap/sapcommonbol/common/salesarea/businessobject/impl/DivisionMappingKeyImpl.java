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

import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DivisionMappingKey;

/**
 * Class defines the key for the distribution channel mapping. <br>
 *
 * @version 1.0
 */
public class DivisionMappingKeyImpl implements DivisionMappingKey {
   
    protected String salesOrg;
    protected String division;
    
    public String getSalesOrg() {
        return salesOrg;
    }
    public void setSalesOrg(String salesOrg) {
        this.salesOrg = salesOrg;
    }
    public String getDivision() {
        return division;
    }
    public void setDivision(String division) {
        this.division = division;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((division == null) ? 0 : division.hashCode());
        result = prime * result + ((salesOrg == null) ? 0 : salesOrg.hashCode());
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
        DivisionMappingKeyImpl other = (DivisionMappingKeyImpl) obj;
        if (division == null) {
            if (other.division != null)
                return false;
        }
        else if (!division.equals(other.division))
            return false;
        if (salesOrg == null) {
            if (other.salesOrg != null)
                return false;
        }
        else if (!salesOrg.equals(other.salesOrg))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "DivisionMappingKeyImpl [division=" + division + ", salesOrg=" + salesOrg + "]";
    }
    
}
