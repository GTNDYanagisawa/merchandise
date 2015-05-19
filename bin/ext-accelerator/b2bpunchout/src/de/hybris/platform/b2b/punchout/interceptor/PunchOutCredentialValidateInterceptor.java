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
package de.hybris.platform.b2b.punchout.interceptor;

import de.hybris.platform.b2b.punchout.jalo.PunchOutCredential;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.springframework.beans.factory.annotation.Required;


/**
 * Validator for entity {@link PunchOutCredential}.
 */
public class PunchOutCredentialValidateInterceptor implements ValidateInterceptor
{
	private PunchOutCredentialService punchOutCredentialService;

	private L10NService l10NService;

	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof PunchOutCredentialModel)
		{
			final PunchOutCredentialModel mapping = (PunchOutCredentialModel) model;
			existentCredential(mapping);
		}
	}

	/**
	 * Check if there is already a credential for the same domain and identity in the system.
	 * 
	 * @param credential
	 *           The credential to be checked.
	 * @throws InterceptorException
	 *            If there is already an existent credential for the same main values.
	 */
	protected void existentCredential(final PunchOutCredentialModel credential) throws InterceptorException
	{
		final PunchOutCredentialModel idFound = punchOutCredentialService.getPunchOutCredential(credential.getDomain(),
				credential.getIdentity());
		if (idFound != null && !credential.equals(idFound))
		{
			throw new InterceptorException(localizeForKey("error.punchoutcredential.existentcredential"));
		}
	}

	private String localizeForKey(final String key)
	{
		return getL10NService().getLocalizedString(key);
	}

	protected L10NService getL10NService()
	{
		return l10NService;
	}

	@Required
	public void setL10NService(final L10NService l10NService)
	{
		this.l10NService = l10NService;
	}

	public PunchOutCredentialService getPunchOutCredentialService()
	{
		return punchOutCredentialService;
	}

	@Required
	public void setPunchOutCredentialService(final PunchOutCredentialService punchOutCredentialService)
	{
		this.punchOutCredentialService = punchOutCredentialService;
	}
}
