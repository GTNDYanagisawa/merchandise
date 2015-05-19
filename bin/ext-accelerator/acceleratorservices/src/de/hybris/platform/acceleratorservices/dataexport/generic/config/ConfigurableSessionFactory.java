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
package de.hybris.platform.acceleratorservices.dataexport.generic.config;

import org.springframework.integration.Message;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.file.remote.session.SessionFactory;


/**
 * Service that session configuration from messages.
 */
public interface ConfigurableSessionFactory<F> extends SessionFactory<F>
{
	/**
	 * Get a session using the message.
	 * 
	 * @param message
	 *           spring message
	 * @return session connection
	 */
	Session<F> getSession(Message<?> message);
}
