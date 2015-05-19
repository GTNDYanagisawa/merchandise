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

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedObject;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;

/**
 * The Connected Object represents an entry in the document flow
 * 
 */
abstract public class ConnectedObjectImpl extends BusinessObjectBase implements
		ConnectedObject {

	/**
	 * Document number
	 */
	protected String docNumber = "";
	/**
	 * Item number
	 */
	protected String docItemNumber = "";
	/**
	 * Document type
	 */
	protected DocumentType docType = DocumentType.UNKNOWN;
	/**
	 * Document can be displayed?
	 */
	protected boolean displayable = false;
	/**
	 * Transfer update type
	 */
	protected String transferUpdateType = UNDEFINED;
	/**
	 * Business object type from SAP back end
	 */
	protected String busObjectType = "";

	/**
	 * Returns the document number
	 * 
	 * @return document number is used to identify the document in the backend
	 */
	@Override
	public String getDocNumber() {
		return docNumber;
	}

	/**
	 * Returns the document item number
	 * 
	 * @return document number is used to identify the document item in the
	 *         backend
	 */
	@Override
	public String getDocItemNumber() {
		return docItemNumber;
	}

	/**
	 * Sets the document number
	 */
	@Override
	public void setDocNumber(final String docNumber) {
		this.docNumber = docNumber;
	}

	/**
	 * Sets the document number
	 */
	@Override
	public void setDocItemNumber(final String docItemNumber) {
		this.docItemNumber = docItemNumber;
	}

	/**
	 * Returns the binary transfer update type
	 * 
	 * @return binary transfer update type
	 */
	@Override
	public String getTransferUpdateType() {
		return transferUpdateType;
	}

	/**
	 * Sets the binary transfer update type
	 */
	@Override
	public void setTransferUpdateType(final String transferUpdateType) {
		this.transferUpdateType = transferUpdateType;
	}

	/**
	 * Returns the document type
	 * 
	 * @return document type charaterizes the document in the backend (e.g.,
	 *         order, order template)
	 */
	@Override
	public DocumentType getDocType() {
		return docType;
	}

	/**
	 * Sets the document type
	 */
	@Override
	public void setDocType(final DocumentType docType) {
		if (docType == null) {
			this.docType = DocumentType.UNKNOWN;
		} else {
			this.docType = docType;
		}
	}

	/**
	 * Returns the displayable property
	 * 
	 * @return displayable property determines if the document may be displayed
	 *         in the order status
	 */
	@Override
	public boolean isDisplayable() {
		return displayable;
	}

	/**
	 * Sets the displayable property
	 */
	@Override
	public void setDisplayable(final boolean displayable) {
		this.displayable = displayable;
	}

	/**
	 * Performs a deep-copy of the object rather than a shallow copy.
	 * 
	 * @return returns a deep copy
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
}
