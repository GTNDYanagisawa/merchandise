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
package de.hybris.platform.webservices;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;



/**
 * A wrapper about a {@link ResponseBuilder} but based on specific operations like GET, PUT, POST, DELETE or, Hybris specific,
 * external Commands.
 */
public interface YResponseBuilder
{

	/**
	 * Same as {@link YResponseBuilder#build()} but wraps response entity into an instance of passed class first. Passed class is
	 * constructed by taking the first appropriate COnstructor whose parameter type is assignable to entity type.
	 * 
	 * @param base
	 *           type of wrapper class
	 * @return {@link Response}
	 */
	Response build(Class<?> base);

	/**
	 * Delegates to {@link ResponseBuilder#build()} but sets appropriate response entity first.
	 * 
	 * @return {@link Response}
	 */
	Response build();

}
