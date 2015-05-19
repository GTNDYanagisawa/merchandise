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

import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DistChannelMappingKey;

/**
 * Class defines the key for the distribution channel mapping. <br>
 *
 * @version 1.0
 */
public class DistChannelMappingKeyImpl implements DistChannelMappingKey {
      
    protected String salesOrg;
    protected String distChannel;
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((distChannel == null) ? 0 : distChannel.hashCode());
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
        DistChannelMappingKeyImpl other = (DistChannelMappingKeyImpl) obj;
        if (distChannel == null) {
            if (other.distChannel != null)
                return false;
        }
        else if (!distChannel.equals(other.distChannel))
            return false;
        if (salesOrg == null) {
            if (other.salesOrg != null)
                return false;
        }
        else if (!salesOrg.equals(other.salesOrg))
            return false;
        return true;
    }
    public String getSalesOrg() {
        return salesOrg;
    }
    public void setSalesOrg(String salesOrg) {
        this.salesOrg = salesOrg;
    }
    public String getDistChannel() {
        return distChannel;
    }
    public void setDistChannel(String distChannel) {
        this.distChannel = distChannel;
    }
    @Override
    public String toString() {
        return "DistChannelMappingKeyImpl [distChannel=" + distChannel + ", salesOrg=" + salesOrg
               + "]";
    }
   
    
}
