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
package de.hybris.platform.sap.sapcommonbol.common.businessobject.impl;

/*****************************************************************************
 Class:        ConverterImpl.java
 Copyright (c) 2010, SAP AG, Germany, All rights reserved.
 Author:       SAP
 Created:      Aug 18, 2010
 Version:      1.0

 *****************************************************************************/


import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectHelper;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapcommonbol.common.backendobject.interf.ConverterBackend;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * Converter implementation. For details see the specifying interface {@link Converter}<br>
 *
 */
@BackendInterface(ConverterBackend.class)
public class ConverterImpl extends BusinessObjectBase implements Converter
{

	/**
	 * ABAP Domain 'DECAN' has interval limit [0 - 14]
	 */
	private static final List<BigDecimal> MIN_SCALE_VALUES;

	static
	{
		final List<BigDecimal> list = new ArrayList<BigDecimal>(15);
		for (int i = 0; i < 15; i++)
		{
			final BigDecimal div = BigDecimal.TEN.pow(i);
			list.add(BigDecimal.ONE.divide(div, i, BigDecimal.ROUND_UNNECESSARY));
		}
		MIN_SCALE_VALUES = Collections.unmodifiableList(list);
	}





	/**
	 * @return the backendService
	 * @throws BackendException
	 */
	public ConverterBackend getBackendService() throws BackendException
	{
		return (ConverterBackend) getBackendBusinessObject();
	}





	@Override
	public String convertUnitID2UnitKey(final String unitID) throws BusinessObjectException
	{
		try
		{
			return getBackendService().convertUnitID2UnitKey(unitID);
		}
		catch (final BackendException e)
		{
			BusinessObjectHelper.splitException(e);
			return null;
		}
	}



	@Override
	public String convertUnitKey2UnitID(final String unitKey) throws BusinessObjectException
	{
		try
		{
			return getBackendService().convertUnitKey2UnitID(unitKey);
		}
		catch (final BackendException e)
		{
			BusinessObjectHelper.splitException(e);
			return null;
		}
	}

	@Override
	public Integer getCurrencyScale(final String sapCurrencyCode) throws BusinessObjectException
	{
		try
		{
			return new Integer(getBackendService().getCurrencyScale(sapCurrencyCode));
		}
		catch (final BackendException e)
		{
			throw new BusinessObjectException("getCurrencyScale", e);

		}
	}

	@Override
	public int getUnitScale(final String unitKey) throws BusinessObjectException
	{
		try
		{
			return getBackendService().getUnitScale(unitKey);
		}
		catch (final BackendException e)
		{
			BusinessObjectHelper.splitException(e);
			return 0;
		}
	}



	@Override
	public BigDecimal getMinimumScaleValue(final String unitKey) throws BusinessObjectException
	{
		return this.getMinimumScaleValue(this.getUnitScale(unitKey));
	}

	protected BigDecimal getMinimumScaleValue(final int scale)
	{
		return MIN_SCALE_VALUES.get(scale);
	}
}
