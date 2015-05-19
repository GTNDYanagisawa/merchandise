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
 * Interface provides the mapping information for the sales organisation and division. <br>
 *
 * @version 1.0
 */
public interface DivisionMapping {

    /**
     * @return alternative division for the customer data
     */
    public String getDivisionForCustomers();

    /**
     * @param divisionForCustomers alternative division for the customer data
     */
    public void setDivisionForCustomers(String divisionForCustomers);

    /**
     * @return division for the condition technique (pricing for instance)
     */
    public String getDivisionForConditions();

    /**
     * @param divisionForConditions division for the condition technique (pricing for instance)
     */
    public void setDivisionForConditions(String divisionForConditions);

    /**
     * @return division for the document management
     */
    public String getDivisionForDocumentTypes();

    /**
     * @param divisionForDocumentTypes division for the document management
     */
    public void setDivisionForDocumentTypes(String divisionForDocumentTypes);

}
