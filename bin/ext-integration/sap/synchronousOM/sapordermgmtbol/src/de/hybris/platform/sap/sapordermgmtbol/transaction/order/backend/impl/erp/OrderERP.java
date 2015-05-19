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
package de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.impl.erp;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.interf.OrderBackend;


/**
 * Back end Object representing an ERP Order document used in checkout (different connection compared to history orders)
 */
@BackendType("ERP")
public class OrderERP extends OrderBaseERP implements OrderBackend
{
	//
}
