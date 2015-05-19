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

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

/**
 * A sales document provides factory methods for its sub objects.<br>
 * However in some cases these sub objects should be created prior the sales
 * document itself, hence this factory provides factory methods to create these
 * objects independently from any sales document.<br>
 * 
 * @version 1.0
 */
public interface SalesTransactionsFactory {

    /**
     * Creates a new ship object
     * 
     * @return Newly created shipTo
     */
    public ShipTo createShipTo();

    /**
     * Creates a new billTo object
     * 
     * @return Newly created billTo
     */
    public BillTo createBillTo();

    /**
     * Gets a new Item object<br>
     * 
     * @return new item instance
     * @see de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocumentBase#createItem()
     */
    public Item createSalesDocumentItem();

}
