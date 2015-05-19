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
 * This enumeration represents a status object. <br>
 * 
 */
public enum EStatus
{
	/**
	 * Status is not applicable.
	 */
	NOT_RELEVANT(' '),
	/**
	 * Status is not processed yet.
	 */
	NOT_PROCESSED('A'),
	/**
	 * Status has been processed, but is not yet completely processed.
	 */
	PARTIALLY_PROCESSED('B'),
	/**
	 * Status has already been processed.
	 */
	PROCESSED('C'),
	/**
	 * Status has been cancelled.
	 */
	CANCELLED('D'),
	/**
	 * Status is expired.
	 */
	EXPIRED('E'),
	/**
	 * Status is accepted.
	 */
	ACCEPTED('F'),
	/**
	 * Status is open.
	 */
	OPEN('G'),
	/**
	 * Status Request for Quotation
	 */
	REQUEST_QUOTATION('O'),
	/**
	 * Status is not defined.
	 */
	UNDEFINED('Z');

	private char status;

	EStatus(final char status)
	{
		this.setStatus(status);
	}

	/**
	 * Determines status for given backend status.<br>
	 * 
	 * @param bkndStatus
	 *           Backend status
	 * @return status
	 */
	public static EStatus getStatusType(final char bkndStatus)
	{
		switch (bkndStatus)
		{
			case 'A':
				return NOT_PROCESSED;
			case 'B':
				return PARTIALLY_PROCESSED;
			case 'C':
				return PROCESSED;
			case 'D':
				return CANCELLED;
			case ' ':
				return NOT_RELEVANT;
			case 'E':
				return EXPIRED;
			case 'F':
				return ACCEPTED;
			case 'G':
				return OPEN;
			case 'O':
				return REQUEST_QUOTATION;
			default:
				return UNDEFINED;
		}
	}

	private void setStatus(final char status)
	{
		this.status = status;
	}

	/**
	 * Returns status.<br>
	 * 
	 * @return status
	 */
	public char getStatus()
	{
		return status;
	}

	/**
	 * Cumulates status<br>
	 * 
	 * @param inStatus
	 *           Additional status to be concerned in the cumulation
	 * @param status
	 *           Previously cumulated status
	 * @return cumulated status
	 */
	public static EStatus cumulateStatus(final EStatus inStatus, final EStatus status)
	{

		EStatus result = status;
		switch (status)
		{
			case NOT_RELEVANT:
				result = inStatus;
				break;
			case CANCELLED:
				result = inStatus;
				break;
			case NOT_PROCESSED:
				if (inStatus == EStatus.PARTIALLY_PROCESSED || inStatus == EStatus.PROCESSED)
				{
					result = EStatus.PARTIALLY_PROCESSED;
				}
				break;
			case PARTIALLY_PROCESSED:
				result = EStatus.PARTIALLY_PROCESSED;
				break;
			case PROCESSED:
				if (inStatus == EStatus.NOT_PROCESSED || inStatus == EStatus.PARTIALLY_PROCESSED)
				{
					result = EStatus.PARTIALLY_PROCESSED;
				}
				break;
			default:
				result = status;

		}
		return result;
	}
}
