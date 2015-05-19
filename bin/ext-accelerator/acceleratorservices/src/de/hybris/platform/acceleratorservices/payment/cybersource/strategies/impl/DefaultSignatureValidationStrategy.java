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
package de.hybris.platform.acceleratorservices.payment.cybersource.strategies.impl;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.payment.cybersource.constants.CyberSourceConstants;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionInfoData;
import de.hybris.platform.acceleratorservices.payment.strategies.SignatureValidationStrategy;
import de.hybris.platform.acceleratorservices.payment.utils.AcceleratorDigestUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class DefaultSignatureValidationStrategy implements SignatureValidationStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultSignatureValidationStrategy.class);
	private SiteConfigService siteConfigService;
	private AcceleratorDigestUtils digestUtils;

	@Override
	public boolean validateSignature(final SubscriptionInfoData subscriptionInfoData)
	{

		final String data = subscriptionInfoData.getSubscriptionSignedValue();
		final String signature = subscriptionInfoData.getSubscriptionIDPublicSignature();

		try
		{
			final String computedSignature = getDigestUtils().getPublicDigest(data, getSharedSecret());
			return computedSignature.equals(signature);
		}
		catch (final Exception ex)
		{
			LOG.info("Failed to compute signature", ex);
		}
		return false;

	}

	protected String getSiteConfigProperty(final String key)
	{
		return getSiteConfigService().getString(key, "");
	}

	protected SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	@Required
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}

	/**
	 * Gets the CyberSource merchant's shared secret that is used to encrypt and validate connections.
	 * 
	 * @return the shared secret downloaded from the CyberSource Business Centre.
	 */
	protected String getSharedSecret()
	{
		return getSiteConfigProperty(CyberSourceConstants.HopProperties.SHARED_SECRET);
	}


	protected AcceleratorDigestUtils getDigestUtils()
	{
		return digestUtils;
	}

	@Required
	public void setDigestUtils(final AcceleratorDigestUtils digestUtils)
	{
		this.digestUtils = digestUtils;
	}

}
