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
package de.hybris.platform.ycommercewebservices.oauth2;


import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.TokenServicesUserApprovalHandler;



/**
 * Currently not used in OAuth2 config. If you want to auto-approve implicit clients, add this to the config: <bean
 * id="userApprovalHandler" class="de.hybris.platform.ycommercewebservices.oauth2.FunkyApprovalHandler"> <property
 * name="autoApproveClients"> <set> <value>my-less-trusted-autoapprove-client</value> </set> </property> <property
 * name="tokenServices" ref="tokenServices" /> </bean>
 * 
 * <oauth:authorization-server client-details-service-ref="clientDetails" token-services-ref="tokenServices"
 * user-approval-handler-ref="userApprovalHandler"> <oauth:authorization-code /> <oauth:implicit /> <oauth:refresh-token
 * /> <oauth:client-credentials /> <oauth:password /> </oauth:authorization-server>
 * 
 * 
 */
public class HybrisApprovalHandler extends TokenServicesUserApprovalHandler
{
	private Collection<String> autoApproveClients = new HashSet<String>();

	private boolean useTokenServices = true;

	/**
	 * @param useTokenServices
	 *           the useTokenServices to set
	 */
	public void setUseTokenServices(final boolean useTokenServices)
	{
		this.useTokenServices = useTokenServices;
	}

	/**
	 * @param autoApproveClients
	 *           the auto approve clients to set
	 */
	public void setAutoApproveClients(final Collection<String> autoApproveClients)
	{
		this.autoApproveClients = autoApproveClients;
	}


	/**
	 * Allows automatic approval for a white list of clients in the implicit grant case.
	 * 
	 * @param authorizationRequest
	 *           The authorization request.
	 * @param userAuthentication
	 *           the current user authentication
	 * 
	 * @return Whether the specified request has been approved by the current user.
	 */
	@Override
	public boolean isApproved(final AuthorizationRequest authorizationRequest, final Authentication userAuthentication)
	{
		if (useTokenServices && super.isApproved(authorizationRequest, userAuthentication))
		{
			return true;
		}
		if (!userAuthentication.isAuthenticated())
		{
			return false;
		}
		return authorizationRequest.isApproved()
				|| (authorizationRequest.getResponseTypes().contains("token") && autoApproveClients.contains(authorizationRequest
						.getClientId()));
	}
}
