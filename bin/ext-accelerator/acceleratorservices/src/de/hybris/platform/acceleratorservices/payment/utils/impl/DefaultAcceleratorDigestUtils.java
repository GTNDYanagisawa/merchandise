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
package de.hybris.platform.acceleratorservices.payment.utils.impl;

import de.hybris.platform.acceleratorservices.payment.utils.AcceleratorDigestUtils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Required;


public class DefaultAcceleratorDigestUtils implements AcceleratorDigestUtils
{
	private String  macAlgorithm;

	@Override
	public String getPublicDigest(final String customValues, final String key) throws NoSuchAlgorithmException,
			InvalidKeyException
	{
		final Base64 encoder = new Base64();
		final Mac sha1Mac = Mac.getInstance(getMacAlgorithm());
		final SecretKeySpec publicKeySpec = new SecretKeySpec(key.getBytes(), getMacAlgorithm());
		sha1Mac.init(publicKeySpec);

		final byte[] publicBytes = sha1Mac.doFinal(customValues.getBytes());
		final String publicDigest = new String(encoder.encode(publicBytes));

		return publicDigest.replaceAll("\n", "");
	}

	protected String getMacAlgorithm()
	{
		return macAlgorithm;
	}

	@Required
	public void setMacAlgorithm(final String macAlgorithm)
	{
		this.macAlgorithm = macAlgorithm;
	}
}
