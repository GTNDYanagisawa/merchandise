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
 */
package de.hybris.platform.ycommercewebservices.formatters;

import java.util.Date;


/**
 * @author michal.flasinski
 * 
 */
public interface WsDateFormatter
{
	Date toDate(String timestamp);

	String toString(Date date);
}
