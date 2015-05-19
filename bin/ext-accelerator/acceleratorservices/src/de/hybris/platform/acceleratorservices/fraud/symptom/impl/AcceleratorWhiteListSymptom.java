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
package de.hybris.platform.acceleratorservices.fraud.symptom.impl;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.fraud.constants.FrauddetectionConstants;
import de.hybris.platform.fraud.impl.FraudServiceResponse;
import de.hybris.platform.fraud.impl.FraudSymptom;
import de.hybris.platform.fraud.symptom.impl.WhiteListSymptom;

import org.apache.commons.lang.StringUtils;


public class AcceleratorWhiteListSymptom extends WhiteListSymptom
{
	@Override
	public FraudServiceResponse recognizeSymptom(final FraudServiceResponse fraudResponse, final AbstractOrderModel order)
	{
		if (getFavoredEmails().contains(getCustomerUid(order.getUser().getUid(), order)))
		{
			fraudResponse.addSymptom(new FraudSymptom(getSymptomName() + ":" + FrauddetectionConstants.USERID, getIncrement()));
		}
		else
		{
			fraudResponse.addSymptom(new FraudSymptom(getSymptomName(), 0));
		}

		return fraudResponse;
	}

	protected String getCustomerUid(final String uid, final AbstractOrderModel order)
	{
		return (order.getUser() instanceof CustomerModel && CustomerType.GUEST.equals(((CustomerModel) order.getUser()).getType()) ? StringUtils
				.substringAfter(uid, "|") : uid);
	}
}
