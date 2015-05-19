/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.hybris.platform.ychinaaccelerator.storefront.security.impl;

import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.web.DefaultRedirectStrategy;


/**
 * A redirect strategy used in
 * {@link de.hybris.platform.ychinaaccelerator.storefront.security.StorefrontAuthenticationSuccessHandler} to handle express
 * checkout case
 */
public class DefaultCommerceRedirectStrategy extends DefaultRedirectStrategy
{

	private CheckoutFlowFacade checkoutFlowFacade;
	private String expressTargetUrl;

	@Override
	public void sendRedirect(final HttpServletRequest request, final HttpServletResponse response, final String url)
			throws IOException
	{
		String redirectUrl = url;

		if (Boolean.valueOf(checkoutFlowFacade.isExpressCheckoutEnabledForStore())
				&& StringUtils.isNotEmpty(request.getParameter("expressCheckoutEnabled")))
		{
			redirectUrl = getExpressTargetUrl();
		}
		super.sendRedirect(request, response, redirectUrl);
	}


	protected String getExpressTargetUrl()
	{
		return expressTargetUrl;
	}

	@Required
	public void setExpressTargetUrl(final String expressTargetUrl)
	{
		this.expressTargetUrl = expressTargetUrl;
	}

	protected CheckoutFlowFacade getCheckoutFlowFacade()
	{
		return checkoutFlowFacade;
	}

	@Required
	public void setCheckoutFlowFacade(final CheckoutFlowFacade checkoutFlowFacade)
	{
		this.checkoutFlowFacade = checkoutFlowFacade;
	}
}
