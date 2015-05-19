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

import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillingHeaderStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.EStatus;


/**
 * Billing status for the header in the sales document
 * 
 */
public class BillingHeaderStatusImpl extends BillingStatusImpl implements BillingHeaderStatus
{


	/**
	 * 
	 */
	public BillingHeaderStatusImpl()
	{
		super();
	}


	/**
	 * Only for unit tests. Other callers need to instantiate via {@link GenericFactory} and call init method}.
	 * 
	 * @param key
	 *           status key
	 */
	public BillingHeaderStatusImpl(final EStatus key)
	{
		super(key);
	}


	/**
	 * Only for unit tests. Other callers should use generic factory and init method
	 * 
	 * @param dlvStatus
	 *           Delivery Status
	 * @param ordInvoiceStatus
	 *           Order Invoice Status
	 * @param dlvInvoiceStatus
	 *           Delivery Invoice Status
	 * @param rjStatus
	 *           Rejected Status
	 */
	public BillingHeaderStatusImpl(final EStatus dlvStatus, final EStatus ordInvoiceStatus, final EStatus dlvInvoiceStatus,
			final EStatus rjStatus)
	{
		super(dlvStatus, ordInvoiceStatus, dlvInvoiceStatus, rjStatus);
	}

	@Override
	public boolean isNotRelevant()
	{
		// FKSAK FKSTK LFSTK
		// ----------------------------------
		// Empty Empty Empty
		// Empty Empty B
		// Empty Empty C
		// -----------------------------------

		return (this.ordInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvInvoiceStatus == EStatus.NOT_RELEVANT && (this.dlvStatus == EStatus.NOT_RELEVANT
				|| this.dlvStatus == EStatus.PARTIALLY_PROCESSED || this.dlvStatus == EStatus.PROCESSED));
	}

	@Override
	public boolean isNotProcessed()
	{
		// FKSAK FKSTK LFSTK
		// ------------------------------------
		// Empty Empty A
		// A Empty Does not matter
		// Empty A Does not matter
		// A A Does not matter
		// ------------------------------------

		return (this.ordInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvStatus == EStatus.NOT_PROCESSED)
				|| (this.ordInvoiceStatus == EStatus.NOT_PROCESSED && this.dlvInvoiceStatus == EStatus.NOT_RELEVANT)
				|| (this.ordInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvInvoiceStatus == EStatus.NOT_PROCESSED)
				|| (this.ordInvoiceStatus == EStatus.NOT_PROCESSED && this.dlvInvoiceStatus == EStatus.NOT_PROCESSED)
				|| (this.ordInvoiceStatus == null && this.dlvInvoiceStatus == null && this.wecStatus == EStatus.NOT_PROCESSED);
	}

	@Override
	public boolean isPartiallyProcessed()
	{
		// FKSAA FKSTA LFSTA
		// ------------------------------------
		// B Does not mater Does not matter
		// C Empty A
		// C Empty B
		// C Empty C
		// C A Does not matter
		// Does not matter B Does not matter
		// Does not matter C B
		// A C Does not matter
		// ------------------------------------
		return this.ordInvoiceStatus == EStatus.PARTIALLY_PROCESSED
				|| (this.ordInvoiceStatus == EStatus.PROCESSED && this.dlvInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvStatus == EStatus.NOT_PROCESSED)
				|| (this.ordInvoiceStatus == EStatus.PROCESSED && this.dlvInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvStatus == EStatus.PARTIALLY_PROCESSED)
				|| (this.ordInvoiceStatus == EStatus.PROCESSED && this.dlvInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvStatus == EStatus.PROCESSED)
				|| (this.ordInvoiceStatus == EStatus.PROCESSED && this.dlvInvoiceStatus == EStatus.NOT_PROCESSED)
				|| this.dlvInvoiceStatus == EStatus.PARTIALLY_PROCESSED
				|| (this.dlvInvoiceStatus == EStatus.PROCESSED && this.dlvStatus == EStatus.PARTIALLY_PROCESSED)
				|| (this.ordInvoiceStatus == EStatus.NOT_PROCESSED && this.dlvInvoiceStatus == EStatus.PROCESSED);

	}

	@Override
	public boolean isProcessed()
	{
		// FKSAA FKSTA LFSTA
		// -------------------------------------
		// C Empty Does not matter
		// Empty C C
		// C C C
		// -------------------------------------
		return (this.ordInvoiceStatus == EStatus.PROCESSED && this.dlvInvoiceStatus == EStatus.NOT_RELEVANT)
				|| (this.ordInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvInvoiceStatus == EStatus.PROCESSED && this.dlvStatus == EStatus.PROCESSED)
				|| (this.ordInvoiceStatus == EStatus.PROCESSED && this.dlvInvoiceStatus == EStatus.PROCESSED && this.dlvStatus == EStatus.PROCESSED);
	}

}
