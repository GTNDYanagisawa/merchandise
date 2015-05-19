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
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;


public class DummyTrustManager implements X509TrustManager
{

	@Override
	public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException
	{
		//TODO unimplemented
	}

	@Override
	public void checkServerTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException
	{
		//TODO unimplemented
	}

	@Override
	public X509Certificate[] getAcceptedIssuers()
	{
		return null;
	}
}
