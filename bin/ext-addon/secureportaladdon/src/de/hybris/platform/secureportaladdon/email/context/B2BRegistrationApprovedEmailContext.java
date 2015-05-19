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
package de.hybris.platform.secureportaladdon.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.secureportaladdon.model.B2BRegistrationApprovedProcessModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Email context used to render B2B approval emails with reset password links
 */
public class B2BRegistrationApprovedEmailContext extends B2BRegistrationEmailContext
{

	private String passwordResetToken;

	/**
	 * @return the passwordResetToken
	 */
	public String getPasswordResetToken()
	{
		return passwordResetToken;
	}

	/**
	 * @param passwordResetToken
	 *           the passwordResetToken to set
	 */
	public void setPasswordResetToken(final String passwordResetToken)
	{
		this.passwordResetToken = passwordResetToken;
	}

	/**
	 * @return The url-encoded representation of the reset password token
	 * @throws UnsupportedEncodingException
	 */
	public String getUrlEncodedToken() throws UnsupportedEncodingException
	{
		return URLEncoder.encode(getPasswordResetToken(), "UTF-8");
	}

	/**
	 * @return The full URL used to reset a password
	 * @throws UnsupportedEncodingException
	 */
	public String getSecureResetPasswordUrl() throws UnsupportedEncodingException
	{
		return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), getUrlEncodingAttributes(), true,
				"/login/pw/change", "token=" + getUrlEncodedToken());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext#init(de.hybris.platform.
	 * processengine.model.BusinessProcessModel, de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel)
	 */
	@Override
	public void init(final StoreFrontCustomerProcessModel businessProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(businessProcessModel, emailPageModel);
		if (businessProcessModel instanceof B2BRegistrationApprovedProcessModel)
		{
			final B2BRegistrationApprovedProcessModel registrationProcessModel = (B2BRegistrationApprovedProcessModel) businessProcessModel;
			setPasswordResetToken(registrationProcessModel.getPasswordResetToken());
		}
	}

}