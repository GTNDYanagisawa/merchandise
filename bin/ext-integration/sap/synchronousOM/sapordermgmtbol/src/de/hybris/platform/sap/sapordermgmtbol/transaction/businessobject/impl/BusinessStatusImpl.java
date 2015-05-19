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

import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BusinessStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.EStatus;

/**
 * Status of the sales document
 * 
 */
public abstract class BusinessStatusImpl implements BusinessStatus {

	/**
	 * Delivery status
	 */
	protected EStatus dlvStatus;
	/**
	 * Rejection status
	 */
	protected EStatus rjStatus;

	/**
	 * Overall status
	 */
	protected EStatus wecStatus;

	/**
	 * Factory to access SAP session beans
	 */
	protected GenericFactory genericFactory;

	/**
	 * Default constructor
	 */
	public BusinessStatusImpl() {

	}

	/**
	 * Only for unit tests. Standard calls should use generic factory and init
	 * method
	 * 
	 * @param dlvStatus
	 * @param rjStatus
	 */
	protected BusinessStatusImpl(final EStatus dlvStatus, final EStatus rjStatus) {
		this.dlvStatus = dlvStatus;
		this.rjStatus = rjStatus;
	}

	@Override
	public void init(final EStatus dlvStatus, final EStatus rjStatus) {
		this.dlvStatus = dlvStatus;
		this.rjStatus = rjStatus;
	}

	/**
	 * Only for unit tests. Other callers need to instantiate via
	 * {@link GenericFactory} and call init method}.
	 * 
	 * @param key
	 *            status key
	 */
	public BusinessStatusImpl(final EStatus key) {
		init(key);
	}

	@Override
	public void init(final EStatus key) {
		this.wecStatus = key;
	}

	@Override
	public void init() {
		this.wecStatus = EStatus.UNDEFINED;
	}

	/**
	 * Determines status
	 */
	protected void determineStatus() {
		// not relevant
		if (this.isNotRelevant()) {
			wecStatus = EStatus.NOT_RELEVANT;
		} else if (this.isNotProcessed()) {
			// 'not shipped' or 'not billed'
			wecStatus = EStatus.NOT_PROCESSED;
		} else if (this.isProcessed()) {
			// shipped or billed
			wecStatus = EStatus.PROCESSED;
		} else {
			// partially shipped or partially billed
			wecStatus = EStatus.PARTIALLY_PROCESSED;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl
	 * .BusinessStatus #getStatus()
	 */
	@Override
	public EStatus getStatus() {
		return wecStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl
	 * .BusinessStatus #isNotRelevant()
	 */
	@Override
	public abstract boolean isNotRelevant();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl
	 * .BusinessStatus #isNotProcessed()
	 */
	@Override
	public abstract boolean isNotProcessed();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl
	 * .BusinessStatus #isPartiallyProcessed()
	 */
	@Override
	public abstract boolean isPartiallyProcessed();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl
	 * .BusinessStatus #isProcessed()
	 */
	@Override
	public abstract boolean isProcessed();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl
	 * .BusinessStatus #clone()
	 */
	@Override
	public Object clone() {
		try {
			// we only contain immutable fields so super clone is fine.
			return super.clone();
		} catch (final CloneNotSupportedException ex) {
			// should not happen, because we are clone able
			throw new ApplicationBaseRuntimeException(
					"Failed to clone Object, check whether Cloneable Interface is still implemented",
					ex);
		}
	}

	/**
	 * @param genericFactory
	 */
	public void setGenericFactory(final GenericFactory genericFactory) {
		this.genericFactory = genericFactory;
	}
}
