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
package de.hybris.platform.ytelcoacceleratorstorefront.forms;

import java.util.List;


public class PaymentSubscriptionsForm
{
	List<String> subscriptionsToChange;
	String newPaymentMethodId;

	public List<String> getSubscriptionsToChange()
	{
		return subscriptionsToChange;
	}

	public void setSubscriptionsToChange(final List<String> subscriptionsToChange)
	{
		this.subscriptionsToChange = subscriptionsToChange;
	}

	public String getNewPaymentMethodId()
	{
		return newPaymentMethodId;
	}

	public void setNewPaymentMethodId(final String newPaymentMethodId)
	{
		this.newPaymentMethodId = newPaymentMethodId;
	}
}
