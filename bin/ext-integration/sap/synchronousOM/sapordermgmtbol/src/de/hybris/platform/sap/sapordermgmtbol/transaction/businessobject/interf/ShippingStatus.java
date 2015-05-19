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

/**
 * Represents the ShippingStatus object. <br>
 * 
 */
public interface ShippingStatus extends BusinessStatus {

    /**
     * Initializes the ShippingStatus object <br>
     * 
     * @param dlvStatus - Delivery Status
     * @param giStatus - Guts issue status
     * @param rjStatus - Rejection Status
     */
    public void init(EStatus dlvStatus, EStatus giStatus, EStatus rjStatus);

}
