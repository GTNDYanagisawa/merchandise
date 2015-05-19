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
package de.hybris.platform.sap.sapordermgmtservices.converters.populator;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import java.util.Comparator;


/**
 * @author Administrator
 * 
 */
public class ItemComparator implements Comparator<Item>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final Item o1, final Item o2)
	{
		//When the numberInt is greater the Item was created first so return 1
		if (o1.getNumberInt() > o2.getNumberInt())
		{
			return 1;
		}
		else if (o1.getNumberInt() < o2.getNumberInt())
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}

}
