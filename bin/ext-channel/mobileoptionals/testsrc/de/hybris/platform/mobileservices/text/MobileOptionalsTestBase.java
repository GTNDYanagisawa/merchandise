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
package de.hybris.platform.mobileservices.text;

import de.hybris.platform.core.PK;
import de.hybris.platform.mobileservices.enums.MobileMessageStatus;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;
import de.hybris.platform.mobileservices.text.engine.IncomingSMSMessageDTO;
import de.hybris.platform.mobileservices.text.engine.IncomingSMSMessageGateway;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;


public class MobileOptionalsTestBase extends ServicelayerTest
{
	@Resource
	protected ModelService modelService;
	@Resource
	private IncomingSMSMessageGateway incomingSMSMessageGateway;

	protected MobileMessageContextModel getMessage(final String pk)
	{
		final MobileMessageContextModel ctx = modelService.get(PK.parse(pk));
		modelService.refresh(ctx);
		return ctx;
	}

	protected MobileMessageStatus blockUsingModel(final String pk)
	{
		for (int i = 0; i < 1000; i++)
		{
			final MobileMessageContextModel msg = getMessage(pk);
			final MobileMessageStatus status = msg.getStatus();
			if (status != null
					&& (status.equals(MobileMessageStatus.SENT) || status.equals(MobileMessageStatus.DISCARDED) || status
							.equals(MobileMessageStatus.ERROR)))
			{
				return status;
			}

			try
			{
				Thread.sleep(50);
			}
			catch (final InterruptedException x) // NOPMD by willy on 14/05/10 16:45
			{
				//NO SILENTLY SWALLOWED EXCEPTION ALLOWED (if this is too radical, at least LOG the exception)!
				throw new RuntimeException(x);
			}
		}

		//Nothing found, return a value that uniquely indicates this outcome
		return null;

	}

	protected String messageReceived(final String shortcodeCountryIsocode, final String phoneCountryIsocode,
			final String shortcode, final String phone, final String text)
	{
		final IncomingSMSMessageDTO dto = new IncomingSMSMessageDTO();

		dto.setPhoneCountryIsoCode(phoneCountryIsocode);
		dto.setPhoneNumber(phone);

		dto.setShortCodeCountryIsoCode(shortcodeCountryIsocode);
		dto.setShortcode(shortcode);

		dto.setContent(text);

		final PK pk = incomingSMSMessageGateway.messageReceived("testEngine", dto);

		return pk != null ? pk.toString() : null;
	}

	protected boolean isTextServiceIllegalStateReplyFailure(final MobileMessageContextModel message)
	{
		return MessageTestingUtilities.isTextServiceIllegalStateReplyFailure(message);
	}

	protected boolean isTextServiceIllegalStateReplyFailure(final String pk)
	{

		return isTextServiceIllegalStateReplyFailure(getMessage(pk));
	}
}
