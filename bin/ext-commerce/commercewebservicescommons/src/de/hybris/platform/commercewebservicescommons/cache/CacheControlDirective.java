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

/**
 * An enumeration of cache control directives
 * 
 * @see <a href="http://tools.ietf.org/html/rfc7234">RFC 7234</a>
 */
public enum CacheControlDirective
{
	/**
	 * The "public" response directive indicates that any cache MAY store the response, even if the response would
	 * normally be non-cacheable or cacheable only within a private cache. (See Section 3.2 for additional details
	 * related to the use of public in response to a request containing Authorization, and Section 3 for details of how
	 * public affects responses that would normally not be stored, due to their status codes not being defined as
	 * cacheable by default; see Section 4.2.2.)
	 */
	PUBLIC("public"),

	/**
	 * The "private" response directive indicates that the response message is intended for a single user and MUST NOT be
	 * stored by a shared cache. A private cache MAY store the response and reuse it for later requests, even if the
	 * response would normally be non-cacheable.
	 */
	PRIVATE("private"),

	/**
	 * The "no-cache" response directive indicates that the response MUST NOT be used to satisfy a subsequent request
	 * without successful validation on the origin server. This allows an origin server to prevent a cache from using it
	 * to satisfy a request without contacting it, even by caches that have been configured to send stale responses.
	 */
	NO_CACHE("no-cache"),

	/**
	 * The "no-store" response directive indicates that a cache MUST NOT store any part of either the immediate request
	 * or response. This directive applies to both private and shared caches. "MUST NOT store" in this context means that
	 * the cache MUST NOT intentionally store the information in non-volatile storage, and MUST make a best-effort
	 * attempt to remove the information from volatile storage as promptly as possible after forwarding it.
	 * 
	 * This directive is NOT a reliable or sufficient mechanism for ensuring privacy. In particular, malicious or
	 * compromised caches might not recognize or obey this directive, and communications networks might be vulnerable to
	 * eavesdropping.
	 */
	NO_STORE("no-store"),

	/**
	 * The "no-transform" response directive indicates that an intermediary (regardless of whether it implements a cache)
	 * MUST NOT transform the payload, as defined in Section 5.7.2 of [RFC7230].
	 */
	NO_TRANSFORM("no-transform"),

	/**
	 * The "must-revalidate" response directive indicates that once it has become stale, a cache MUST NOT use the
	 * response to satisfy subsequent requests without successful validation on the origin server.
	 * 
	 * The must-revalidate directive is necessary to support reliable operation for certain protocol features. In all
	 * circumstances a cache MUST obey the must-revalidate directive; in particular, if a cache cannot reach the origin
	 * server for any reason, it MUST generate a 504 (Gateway Timeout) response.
	 * 
	 * The must-revalidate directive ought to be used by servers if and only if failure to validate a request on the
	 * representation could result in incorrect operation, such as a silently unexecuted financial transaction.
	 */
	MUST_REVALIDATE("must-revalidate"),

	/**
	 * The "proxy-revalidate" response directive has the same meaning as the must-revalidate response directive, except
	 * that it does not apply to private caches.
	 */
	PROXY_REVALIDATE("proxy-revalidate");

	private final String directive;

	private CacheControlDirective(final String directive)
	{
		this.directive = directive;
	}

	@Override
	public String toString()
	{
		return directive;
	}
}
