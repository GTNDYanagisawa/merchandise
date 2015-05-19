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
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillingHeaderStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.EStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.OverallStatusOrder;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShippingStatus;


/**
 * Represents the OverallStatusOrder object.
 * 
 * @see OverallStatusOrder
 */
public class OverallStatusOrderImpl extends OverallStatusImpl implements OverallStatusOrder
{
	private BillingStatus billingStatus;
	private ShippingStatus shippingStatus;

	/**
	 * Only for unit tests. Other callers need to instantiate via {@link GenericFactory} and call init method}.
	 * 
	 * @param key
	 *           status key
	 */
	public OverallStatusOrderImpl(final EStatus key)
	{
		super(key);
	}

	/**
	 * Default constructor
	 */
	public OverallStatusOrderImpl()
	{
		super();
	}

	/**
	 * Only for unit tests. Other callers need to instantiate via {@link GenericFactory} and call init method}.
	 * 
	 * @param procStatus
	 *           process (WEC) status
	 * @param shippingStatus
	 *           shipping status
	 * @param billingStatus
	 *           billing status
	 * @param rjStatus
	 *           rejection status
	 */
	public OverallStatusOrderImpl(final EStatus procStatus, final ShippingStatusImpl shippingStatus,
			final BillingStatusImpl billingStatus, final EStatus rjStatus)
	{
		super();
		wecStatus = procStatus;
		this.rjStatus = rjStatus;
		this.billingStatus = billingStatus;
		this.shippingStatus = shippingStatus;
		determineStatus();
	}

	@Override
	public void init(final EStatus procStatus, final ShippingStatus shippingStatus, final BillingStatus billingStatus,
			final EStatus rjStatus)
	{
		wecStatus = procStatus;
		this.rjStatus = rjStatus;
		this.billingStatus = billingStatus;
		this.shippingStatus = shippingStatus;
		determineStatus();
	}

	@Override
	public void init(final EStatus procStatus)
	{
		// Document status
		final ShippingStatus shippingStatus = (ShippingStatus) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_SHIPPING_STATUS);
		shippingStatus.init(procStatus);

		// Document status
		final BillingHeaderStatus billingHeaderStatus = (BillingHeaderStatus) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_BILLING_HEADER_STATUS);
		billingHeaderStatus.init(procStatus);

		init(procStatus, shippingStatus, billingHeaderStatus, null);
	}

	@Override
	public boolean isPartiallyProcessed()
	{
		return (wecStatus == EStatus.PARTIALLY_PROCESSED) || (wecStatus == EStatus.PROCESSED && !isWeCStatusCompleted());
	}

	@Override
	public boolean isProcessed()
	{
		return (wecStatus == EStatus.PROCESSED) && isWeCStatusCompleted();
	}

	@Override
	public boolean isCancelled()
	{
		// ERP: Order is considered cancelled, if rejection status is completed
		// CRM: wecStatus will be already set from wec backendlayer as cancelled
		return rjStatus == EStatus.PROCESSED || wecStatus == EStatus.CANCELLED;
	}

	private boolean isWeCStatusCompleted()
	{
		return (shippingStatus.getStatus() == EStatus.PROCESSED && billingStatus.getStatus() == EStatus.PROCESSED)
				|| (shippingStatus.getStatus() == EStatus.NOT_RELEVANT && billingStatus.getStatus() == EStatus.PROCESSED)
				|| (shippingStatus.getStatus() == EStatus.PROCESSED && billingStatus.getStatus() == EStatus.NOT_RELEVANT);
	}

	@Override
	protected void determineStatus()
	{
		if (isCancelled())
		{
			wecStatus = EStatus.CANCELLED;
		}
		else if (isNotRelevant())
		{
			wecStatus = EStatus.NOT_RELEVANT;
		}
		else if (isNotProcessed())
		{
			// open
			wecStatus = EStatus.NOT_PROCESSED;
		}
		else if (isPartiallyProcessed())
		{
			// in process
			wecStatus = EStatus.PARTIALLY_PROCESSED;
		}
		else if (isProcessed())
		{
			// completed
			wecStatus = EStatus.PROCESSED;
		}
	}

	/**
	 * Performs a deep-copy of the object rather than a shallow copy.
	 * 
	 * @return returns a deep copy
	 */
	@Override
	public Object clone()
	{

		// for immutable fields super clone is fine.
		final OverallStatusOrderImpl myClone = (OverallStatusOrderImpl) super.clone();

		// Date is mutable, so we have to clone it to make the clone
		// independent

		if (null != billingStatus)
		{
			myClone.billingStatus = (BillingStatusImpl) billingStatus.clone();
		}

		if (null != shippingStatus)
		{
			myClone.shippingStatus = (ShippingStatusImpl) shippingStatus.clone();
		}

		return myClone;

	}

}
