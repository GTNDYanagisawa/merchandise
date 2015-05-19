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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillingItemStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.EStatus;


/**
 * Billing Status for the sales document item
 * 
 */
public class BillingItemStatusImpl extends BillingStatusImpl implements BillingItemStatus
{



	/**
		 * 
		 */
	public BillingItemStatusImpl()
	{
		super();
	}


	/**
	 * Constructor
	 * 
	 * @param key
	 *           Status key
	 */
	public BillingItemStatusImpl(final EStatus key)
	{
		super(key);
	}


	/**
	 * Only for unit tests. Other callers should use generic factory and init method
	 * 
	 * @param dlvStatus
	 *           delivery status
	 * @param ordInvoiceStatus
	 *           order invoice status
	 * @param dlvInvoiceStatus
	 *           delivery invoice status
	 * @param rjStatus
	 *           rejection status
	 */
	public BillingItemStatusImpl(final EStatus dlvStatus, final EStatus ordInvoiceStatus, final EStatus dlvInvoiceStatus,
			final EStatus rjStatus)
	{
		super(dlvStatus, ordInvoiceStatus, dlvInvoiceStatus, rjStatus);
	}

	@Override
	public boolean isNotRelevant()
	{
		// FKSAA FKSTA LFSTA
		// ----------------------------------
		// Empty Empty Empty
		// Empty Empty B
		// Empty Empty C
		// -----------------------------------

		return (this.ordInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvInvoiceStatus == EStatus.NOT_RELEVANT && (this.dlvStatus == EStatus.NOT_RELEVANT
				|| this.dlvStatus == EStatus.PARTIALLY_PROCESSED || this.dlvStatus == EStatus.PROCESSED))
				|| this.rjStatus == EStatus.PROCESSED;

	}

	@Override
	public boolean isNotProcessed()
	{
		// FKSAA FKSTA LFSTA
		// ------------------------------------
		// Empty Empty A
		// Empty A Does not matter
		// A Does not matter Does not matter
		// ------------------------------------

		return this.ordInvoiceStatus == EStatus.NOT_PROCESSED
				|| (this.ordInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvInvoiceStatus == EStatus.NOT_PROCESSED)
				|| (this.ordInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvStatus == EStatus.NOT_PROCESSED);
	}

	@Override
	public boolean isPartiallyProcessed()
	{
		// FKSAA FKSTA LFSTA
		// ------------------------------------
		// B Does not matter Does not matter
		// Empty B Does not matter
		// Empty C B
		// ------------------------------------
		return this.ordInvoiceStatus == EStatus.PARTIALLY_PROCESSED
				|| (this.ordInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvInvoiceStatus == EStatus.PARTIALLY_PROCESSED)
				|| (this.ordInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvInvoiceStatus == EStatus.PROCESSED && this.dlvStatus == EStatus.PARTIALLY_PROCESSED);
	}

	@Override
	public boolean isProcessed()
	{
		// FKSAA FKSTA LFSTA
		// -------------------------------------
		// C Does not matter Does not matter
		// Empty C C
		// -------------------------------------
		return this.ordInvoiceStatus == EStatus.PROCESSED
				|| (this.ordInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvInvoiceStatus == EStatus.PROCESSED && this.dlvStatus == EStatus.PROCESSED);
	}

}
