/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.test;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.persistence.security.SaltedMD5PasswordEncoder;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * Test class of {@link SaltedMD5PasswordEncoder}.
 */
@IntegrationTest
public class SaltedMD5PasswordEncoderTest extends HybrisJUnit4Test
{
	private static final Logger LOG = Logger.getLogger(SaltedMD5PasswordEncoderTest.class.getName());

	private static final String UID = "test";
	private static final String PASSWORD = "foobar";

	/**
	 * Checks password encoding and checking
	 */
	@Test
	public void testEncoding()
	{
		final SaltedMD5PasswordEncoder encoder = new SaltedMD5PasswordEncoder();
		final String hashedPassword = encoder.encode(UID, PASSWORD);

		LOG.info("UID: " + UID);
		LOG.info("Password: " + PASSWORD);
		LOG.info("Hashed password: " + hashedPassword);

		assertTrue("Password check failed!", encoder.check(UID, hashedPassword, PASSWORD));

	}
}
