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
package de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf;


/**
 * Interface builds the key for the sales organisation and division mapping. <br>
 *
 * @version 1.0
 */
public interface DivisionMappingKey {

    /**
     * @return sales organisation
     */
    public String getSalesOrg();

    /**
     * @param salesOrg  sales organisation
     */
    public void setSalesOrg(String salesOrg);

    /**
     * @return division
     */
    public String getDivision();

    /**
     * @param division division
     */
    public void setDivision(String division);

}
