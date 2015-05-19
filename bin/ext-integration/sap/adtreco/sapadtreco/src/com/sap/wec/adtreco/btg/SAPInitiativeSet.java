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
package com.sap.wec.adtreco.btg;

import java.util.Collection;
import java.util.HashSet;


/**
 *
 */
public class SAPInitiativeSet extends HashSet<String>
{

	/**
	 * 
	 */
	public SAPInitiativeSet()
	{
		super();
	}

	public SAPInitiativeSet(final Collection<? extends String> col)
	{
		super(col);
	}

	public SAPInitiativeSet(final int initialCapacity, final float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}

	public SAPInitiativeSet(final int initialCapacity)
	{
		super(initialCapacity);
	}

}
