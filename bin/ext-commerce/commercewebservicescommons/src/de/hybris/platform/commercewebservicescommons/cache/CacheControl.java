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
package de.hybris.platform.commercewebservicescommons.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Provides Cache-Control header for Spring MVC controllers.
 * 
 * @see <a href="http://tools.ietf.org/html/rfc7234">RFC 7234</a>
 */
@Target(
{ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheControl
{
	/**
	 * Array of non-parameterized response directives
	 */
	CacheControlDirective[] directive() default
	{ CacheControlDirective.NO_CACHE };

	/**
	 * The "max-age" response directive indicates that the response is to be considered stale after its age is greater
	 * than the specified number of seconds.
	 */
	int maxAge() default -1;

	/**
	 * The "s-maxage" response directive indicates that, in shared caches, the maximum age specified by this directive
	 * overrides the maximum age specified by either the max-age directive or the Expires header field. The s-maxage
	 * directive also implies the semantics of the proxy-revalidate response directive.
	 */
	int sMaxAge() default -1;
}
