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
package de.hybris.platform.b2b.mock;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BEmailService;
import de.hybris.platform.core.model.order.OrderModel;
import javax.mail.internet.InternetAddress;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;
import org.junit.Ignore;


@Ignore
public class MockB2BEmailService extends DefaultB2BEmailService
{
	private static final Logger LOG = Logger.getLogger(MockB2BEmailService.class);

	@Override
	public void sendEmail(final HtmlEmail email) throws EmailException
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info(String.format("Sending email %s", email));
		}
	}

	@Override
	public HtmlEmail createOrderApprovalEmail(final String emailTemplateCode, final OrderModel order, final B2BCustomerModel user,
			final InternetAddress from, final String subject) throws EmailException
	{
		return new HtmlEmail();
	}

	@Override
	public HtmlEmail createOrderRejectionEmail(final String emailTemplateCode, final OrderModel order,
			final B2BCustomerModel user, final InternetAddress from, final String subject) throws EmailException
	{
		return new HtmlEmail();
	}
}
