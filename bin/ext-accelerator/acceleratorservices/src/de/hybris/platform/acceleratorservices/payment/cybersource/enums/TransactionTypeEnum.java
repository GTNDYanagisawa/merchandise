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
package de.hybris.platform.acceleratorservices.payment.cybersource.enums;

/**
 * This Enum represents the different types of transactions that can be requested from the CyberSource Hosted Order Page Service.
 */
public enum TransactionTypeEnum
{
	authorization,
	sale,
	subscription,
	subscription_modify,
	subscription_credit,
	authReversal
}
