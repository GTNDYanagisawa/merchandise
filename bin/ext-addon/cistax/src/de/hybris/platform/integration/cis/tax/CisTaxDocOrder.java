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
package de.hybris.platform.integration.cis.tax;

import com.hybris.cis.api.tax.model.CisTaxDoc;
import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * A wrapper object to hold the the cis tax document related return from the cis web call and the order so as to
 * facilitate conversion.
 */
public class CisTaxDocOrder
{
	private CisTaxDoc taxDoc;
	private AbstractOrderModel abstractOrder;

	public CisTaxDocOrder(final CisTaxDoc taxDoc, final AbstractOrderModel abstractOrder)
	{
		this.taxDoc = taxDoc;
		this.abstractOrder = abstractOrder;
	}

	public CisTaxDoc getTaxDoc()
	{
		return taxDoc;
	}

	public void setTaxDoc(final CisTaxDoc taxDoc)
	{
		this.taxDoc = taxDoc;
	}

	public AbstractOrderModel getAbstractOrder()
	{
		return abstractOrder;
	}

	public void setAbstractOrder(final AbstractOrderModel abstractOrder)
	{
		this.abstractOrder = abstractOrder;
	}
}
