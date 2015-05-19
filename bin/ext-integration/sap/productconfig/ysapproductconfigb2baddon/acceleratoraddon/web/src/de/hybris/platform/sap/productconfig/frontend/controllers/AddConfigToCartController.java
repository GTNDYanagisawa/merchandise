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
package de.hybris.platform.sap.productconfig.frontend.controllers;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.Sapproductconfigb2baddonConstants;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStatusSync;

import java.io.UnsupportedEncodingException;

import javax.annotation.MatchesPattern;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@Scope("tenant")
public class AddConfigToCartController extends AbstractProductConfigController
{
	private static final Logger LOG = Logger.getLogger(ConfigureProductController.class);

	@RequestMapping(value = "/**/{productCode:.*}/addToCart", method = RequestMethod.POST)
	public String addConfigToCart(
			@PathVariable("productCode") final String productCode,
			@ModelAttribute(Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE) @Valid @MatchesPattern(Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE) final ConfigurationData configData,
			final BindingResult bindingResult, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException,
			UnsupportedEncodingException
	{
		LOG.info("AddConfigToCart POST for " + productCode);

		final String redirectURL = "redirect:/" + productCode + "/config";

		final UiStatusSync uiStatusSync = new UiStatusSync();
		final UiStatus uiStatus = uiStatusSync.storeUiStatusInSession(configData);
		getSessionService().setAttribute(PRODUCT_SESSION_PREFIX + configData.getKbKey().getProductCode(), uiStatus);

		removeNullCstics(configData.getGroups());
		getConfigFacade().updateConfiguration(configData);

		final boolean validationErrors = bindingResult.hasErrors();
		if (validationErrors)
		{
			return redirectURL;
		}

		final ConfigurationData latestConfiguration = getConfigFacade().getConfiguration(configData);
		final Boolean addedToCart = Boolean.valueOf(latestConfiguration.getCartItemPK() == null
				|| latestConfiguration.getCartItemPK().isEmpty());

		final String cartItemKey;
		try
		{
			cartItemKey = getConfigCartFacade().addConfigurationToCart(latestConfiguration);
			getSessionService().setAttribute(PRODUCT_CART_ITEM_SESSION_PREFIX + productCode, cartItemKey);
			getSessionService().setAttribute(CART_ITEM_HANDLE_SESSION_PREFIX + cartItemKey, uiStatus);

			redirectAttributes.addFlashAttribute("addedToCart", addedToCart);
		}
		catch (final CommerceCartModificationException ex)
		{
			//TODO: add generic error to binding results
			LOG.error("Add-To-Cart failed", ex);
		}

		return redirectURL;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/**/{productCode:.*}/reset")
	public String resetConfiguration(@PathVariable("productCode") final String productCode, final String url)
			throws CMSItemNotFoundException, UnsupportedEncodingException
	{
		LOG.debug("Reset POST for " + productCode);

		final UiStatus uiStatus = getUiStatus(productCode);
		final String oldConfigId = uiStatus.getConfigId();

		getSessionService().removeAttribute(PRODUCT_SESSION_PREFIX + productCode);
		getSessionService().removeAttribute(PRODUCT_CART_ITEM_SESSION_PREFIX + productCode);
		getConfigCartFacade().resetConfiguration(oldConfigId);


		final String redirectUrl = "redirect:" + url;
		return redirectUrl;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/**/{productCode:.*}/copy")
	public String copyConfiguration(@PathVariable("productCode") final String productCode, final String url)
			throws CMSItemNotFoundException, UnsupportedEncodingException
	{
		LOG.debug("Copy POST for " + productCode);

		getSessionService().removeAttribute(PRODUCT_CART_ITEM_SESSION_PREFIX + productCode);

		final UiStatus uiStatus = getUiStatus(productCode);
		final String oldConfigId = uiStatus.getConfigId();
		final String newConfigId = getConfigCartFacade().copyConfiguration(oldConfigId);
		checkUiStatus(productCode, uiStatus, oldConfigId, newConfigId);

		final String redirectUrl = "redirect:" + url;
		return redirectUrl;
	}

	protected UiStatus getUiStatus(final String productCode)
	{
		final UiStatus uiStatus = getSessionService().getAttribute(PRODUCT_SESSION_PREFIX + productCode);
		if (uiStatus == null)
		{
			throw new IllegalStateException("Could not get uiStatus for: " + productCode);
		}
		return uiStatus;
	}

	/**
	 * Updates the UI Status if needed (in case a configuration has been copied)
	 * 
	 * @param productCode
	 * @param uiStatus
	 *           existing UI status
	 * @param oldConfigId
	 *           ID of existing CFG
	 * @param newConfigId
	 *           ID of new CFG (might be the same as the old one)
	 */
	protected void checkUiStatus(final String productCode, final UiStatus uiStatus, final String oldConfigId,
			final String newConfigId)
	{
		if (!newConfigId.equals(oldConfigId))
		{
			final UiStatus newUiStatus = new UiStatus();
			newUiStatus.setConfigId(newConfigId);
			newUiStatus.setGroups(uiStatus.getGroups());
			newUiStatus.setPriceSummaryCollapsed(uiStatus.isPriceSummaryCollapsed());
			newUiStatus.setSpecificationTreeCollapsed(uiStatus.isSpecificationTreeCollapsed());
			getSessionService().setAttribute(PRODUCT_SESSION_PREFIX + productCode, newUiStatus);
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Old and new configId: " + oldConfigId + ", " + newConfigId);
		}
	}
}
