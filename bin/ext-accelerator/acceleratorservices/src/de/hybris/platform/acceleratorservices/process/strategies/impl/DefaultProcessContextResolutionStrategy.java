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
package de.hybris.platform.acceleratorservices.process.strategies.impl;

import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontProcessModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.jalo.c2l.LocalizableItem;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.site.BaseSiteService;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default strategy to impersonate site and initialize session context from the process model.
 */
public class DefaultProcessContextResolutionStrategy implements ProcessContextResolutionStrategy<BaseSiteModel>
{
	private static final Logger LOG = Logger.getLogger(DefaultProcessContextResolutionStrategy.class);

	private CatalogVersionService catalogVersionService;

	private CMSSiteService cmsSiteService;
	private CommonI18NService commonI18NService;
	private CommerceCommonI18NService commerceCommonI18NService;
	private SessionService sessionService;
	private boolean enableLanguageFallback;
	private BaseSiteService baseSiteService;

	protected CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
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

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected boolean isEnableLanguageFallback()
	{
		return enableLanguageFallback;
	}

	// Optional
	public void setEnableLanguageFallback(final boolean enableLanguageFallback)
	{
		this.enableLanguageFallback = enableLanguageFallback;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	@Override
	public void initializeContext(final BusinessProcessModel businessProcess)
	{
		ServicesUtil.validateParameterNotNull(businessProcess, "businessProcess must not be null");

		final BaseSiteModel baseSite = getCmsSite(businessProcess);
		if (baseSite == null)
		{
			LOG.error("Failed to lookup BaseSite for BusinessProcess [" + businessProcess + "]. Unable to setup session context.");
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Initializing context with site [" + baseSite + "]");
			}

			// Setup the site in the current session, either a CMS Site or a Base Site
			if (baseSite instanceof CMSSiteModel)
			{
				try
				{
					getCmsSiteService().setCurrentSiteAndCatalogVersions((CMSSiteModel) baseSite, true);
				}
				catch (final CMSItemNotFoundException e)
				{
					LOG.error("Failed to initialize session context", e);
				}
			}
			else
			{
				getBaseSiteService().setCurrentBaseSite(baseSite, true);
			}

			final CustomerModel customer = getCustomer(businessProcess);
			final OrderModel order = getOrder(businessProcess);

			if (LOG.isDebugEnabled())
			{
				LOG.debug("Initializing context with customer [" + customer + "] order [" + order + "]");
			}

			final LanguageModel language = computeLanguage(order, customer);
			if (language != null)
			{
				getCommonI18NService().setCurrentLanguage(language);
			}

			final CurrencyModel currency = computeCurrency(order, customer);
			if (currency != null)
			{
				getCommonI18NService().setCurrentCurrency(currency);
			}

			getSessionService().setAttribute(LocalizableItem.LANGUAGE_FALLBACK_ENABLED, Boolean.valueOf(isEnableLanguageFallback()));
			getSessionService().setAttribute(AbstractItemModel.LANGUAGE_FALLBACK_ENABLED_SERVICE_LAYER,
					Boolean.valueOf(isEnableLanguageFallback()));
		}
	}

	protected LanguageModel computeLanguage(final OrderModel order, final CustomerModel customer)
	{
		LanguageModel language = getLanguage(order);
		if (language == null)
		{
			language = getLanguage(customer);
		}
		if (isValidLanguage(language))
		{
			return language;
		}
		return getCommerceCommonI18NService().getDefaultLanguage();
	}

	protected LanguageModel getLanguage(final OrderModel order)
	{
		return order == null ? null : order.getLanguage();
	}

	protected LanguageModel getLanguage(final CustomerModel customer)
	{
		return customer == null ? null : customer.getSessionLanguage();
	}

	protected boolean isValidLanguage(final LanguageModel language)
	{
		return (language != null && getCommerceCommonI18NService().getAllLanguages().contains(language));
	}

	protected CurrencyModel computeCurrency(final OrderModel order, final CustomerModel customer)
	{
		CurrencyModel currency = getCurrency(order);
		if (currency == null)
		{
			currency = getCurrency(customer);
		}
		if (isValidCurrency(currency))
		{
			return currency;
		}
		return getCommerceCommonI18NService().getDefaultCurrency();
	}

	protected CurrencyModel getCurrency(final OrderModel order)
	{
		return order == null ? null : order.getCurrency();
	}

	protected CurrencyModel getCurrency(final CustomerModel customer)
	{
		return customer == null ? null : customer.getSessionCurrency();
	}

	protected boolean isValidCurrency(final CurrencyModel currency)
	{
		return (currency != null && getCommerceCommonI18NService().getAllCurrencies().contains(currency));
	}

	@Override
	public CatalogVersionModel getContentCatalogVersion(final BusinessProcessModel businessProcess)
	{
		ServicesUtil.validateParameterNotNull(businessProcess, "businessProcess must not be null");

		initializeContext(businessProcess);

		final BaseSiteModel baseSite = getCmsSite(businessProcess);
		if (baseSite == null)
		{
			LOG.error("Failed to lookup BaseSite for BusinessProcess [" + businessProcess + "]. Unable to get content catalog.");
		}
		else if (baseSite instanceof CMSSiteModel)
		{
			final List<ContentCatalogModel> contentCatalogs = ((CMSSiteModel) baseSite).getContentCatalogs();
			if (!contentCatalogs.isEmpty())
			{
				return getCatalogVersionService().getSessionCatalogVersionForCatalog(contentCatalogs.get(0).getId()); // Shouldn't be more than one
			}
		}

		return null;
	}

	@Override
	public BaseSiteModel getCmsSite(final BusinessProcessModel businessProcess)
	{
		ServicesUtil.validateParameterNotNull(businessProcess, "businessProcess must not be null");

		if (businessProcess instanceof StoreFrontProcessModel)
		{
			return ((StoreFrontProcessModel) businessProcess).getSite();
		}

		if (businessProcess instanceof OrderProcessModel)
		{
			return ((OrderProcessModel) businessProcess).getOrder().getSite();
		}

		if (businessProcess instanceof ConsignmentProcessModel)
		{
			return ((ConsignmentProcessModel) businessProcess).getConsignment().getOrder().getSite();
		}

		LOG.info("Unsupported BusinessProcess type [" + businessProcess.getClass().getSimpleName() + "] for item ["
				+ businessProcess + "]");
		return null;
	}

	protected OrderModel getOrder(final BusinessProcessModel businessProcess)
	{
		ServicesUtil.validateParameterNotNull(businessProcess, "businessProcess must not be null");

		if (businessProcess instanceof OrderProcessModel)
		{
			return ((OrderProcessModel) businessProcess).getOrder();
		}

		if (businessProcess instanceof ConsignmentProcessModel)
		{
			return (OrderModel) ((ConsignmentProcessModel) businessProcess).getConsignment().getOrder();
		}

		return null;
	}

	protected CustomerModel getCustomer(final BusinessProcessModel businessProcess)
	{
		ServicesUtil.validateParameterNotNull(businessProcess, "businessProcess must not be null");

		if (businessProcess instanceof StoreFrontCustomerProcessModel)
		{
			return ((StoreFrontCustomerProcessModel) businessProcess).getCustomer();
		}

		if (businessProcess instanceof OrderProcessModel)
		{
			return (CustomerModel) ((OrderProcessModel) businessProcess).getOrder().getUser();
		}

		if (businessProcess instanceof ConsignmentProcessModel)
		{
			return (CustomerModel) ((ConsignmentProcessModel) businessProcess).getConsignment().getOrder().getUser();
		}

		return null;
	}
}
