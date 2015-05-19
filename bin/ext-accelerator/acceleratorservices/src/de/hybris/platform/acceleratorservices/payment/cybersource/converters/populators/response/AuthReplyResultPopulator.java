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
package de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.data.AuthReplyData;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import org.apache.commons.lang.StringUtils;


public class AuthReplyResultPopulator extends AbstractResultPopulator<Map<String, String>, CreateSubscriptionResult>
{
	@Override
	public void populate(final Map<String, String> source, final CreateSubscriptionResult target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [Map<String, String>] source cannot be null");
		validateParameterNotNull(target, "Parameter [CreateSubscriptionResult] target cannot be null");

		final AuthReplyData data = new AuthReplyData();
		data.setCcAuthReplyAmount(getBigDecimalForString(source.get("ccAuthReply_amount")));
		data.setCcAuthReplyAuthorizationCode(source.get("ccAuthReply_authorizationCode"));
		data.setCcAuthReplyAuthorizedDateTime(source.get("ccAuthReply_authorizedDateTime"));
		data.setCcAuthReplyAvsCode(source.get("ccAuthReply_avsCode"));
		data.setCcAuthReplyAvsCodeRaw(source.get("ccAuthReply_avsCodeRaw"));
		data.setCcAuthReplyCvCode(source.get("ccAuthReply_cvCode"));
		data.setCcAuthReplyProcessorResponse(source.get("ccAuthReply_processorResponse"));
		data.setCcAuthReplyReasonCode(getIntegerForString(source.get("ccAuthReply_reasonCode")));
		data.setCvnDecision(Boolean.valueOf(StringUtils.equalsIgnoreCase(source.get("ccAuthReply_cvCode"), "M")));

		target.setAuthReplyData(data);
	}
}
