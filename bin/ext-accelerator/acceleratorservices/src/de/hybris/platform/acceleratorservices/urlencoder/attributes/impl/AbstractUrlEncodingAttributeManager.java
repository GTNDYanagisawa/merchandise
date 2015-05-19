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
package de.hybris.platform.acceleratorservices.urlencoder.attributes.impl;


import de.hybris.platform.acceleratorservices.urlencoder.attributes.UrlEncodingAttributeManager;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.storesession.StoreSessionService;
import de.hybris.platform.servicelayer.session.SessionService;

import org.springframework.beans.factory.annotation.Required;

/**
 * Abstract implementation of {@link  }.
 */
public abstract class AbstractUrlEncodingAttributeManager implements UrlEncodingAttributeManager
{
	private StoreSessionService storeSessionService;
	private SessionService sessionService;
	private CMSSiteService cmsSiteService;
	private CommerceCommonI18NService commerceCommonI18NService;

	@Override
	public boolean isValid(final String value)
	{
		return getAllAvailableValues().contains(value);
	}

	protected StoreSessionService getStoreSessionService()
	{
		return storeSessionService;
	}

	@Required
	public void setStoreSessionService(final StoreSessionService StoreSessionService)
	{
		this.storeSessionService = StoreSessionService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	protected CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

}
