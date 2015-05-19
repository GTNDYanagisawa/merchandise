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


import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.frontend.UiGroupStatus;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.breadcrumb.ProductConfigureBreadcrumbBuilder;
import de.hybris.platform.sap.productconfig.frontend.constants.Sapproductconfigb2baddonConstants;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStatusSync;
import de.hybris.platform.sap.productconfig.runtime.interf.constants.SapproductconfigruntimeinterfaceConstants;
import de.hybris.platform.yb2bacceleratorstorefront.constants.WebConstants;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.annotation.MatchesPattern;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@Scope("tenant")
@RequestMapping()
public class ConfigureProductController extends AbstractProductConfigController
{
	@Resource(name = "sapProductConfigBreadcrumbBuilder")
	private ProductConfigureBreadcrumbBuilder productConfigurationBreadcrumbBuilder;


	@RequestMapping(value = "/**/{configCartItemHandle:.*}/{productCode:.*}/configCartEntry", method = RequestMethod.GET)
	public String configureCartEntry(@PathVariable("configCartItemHandle") final String cartEntryHandle,
			@PathVariable("productCode") final String productCode) throws CMSItemNotFoundException, UnsupportedEncodingException
	{
		final KBKeyData kbKey = new KBKeyData();
		kbKey.setProductCode(productCode);
		final UiStatus uiStatus = getUiStatusFromSession(cartEntryHandle, kbKey);
		final String selectedGroup = getSelectedGroup(uiStatus);

		String redirectURL;
		if (selectedGroup != null)
		{
			redirectURL = "redirect:/" + cartEntryHandle + "/" + productCode + "/configCart?tab=" + selectedGroup;
		}
		else
		{
			redirectURL = "redirect:/" + cartEntryHandle + "/" + productCode + "/configCart";
		}

		return redirectURL;
	}

	/**
	 * Fetch currently selected group from UI status
	 *
	 * @param uiStatus
	 * @return Selected group. Null, if ui status not present
	 */
	protected String getSelectedGroup(final UiStatus uiStatus)
	{
		String selectedGroup = null;
		if (uiStatus != null)
		{
			final List<UiGroupStatus> uiGroupsStatus = uiStatus.getGroups();
			for (final UiGroupStatus uiGroupStatus : uiGroupsStatus)
			{
				if (!uiGroupStatus.isCollapsed())
				{
					if (selectedGroup != null)
					{
						selectedGroup = null;
						break;
					}
					selectedGroup = uiGroupStatus.getId();
				}
			}
		}
		return selectedGroup;
	}

	@RequestMapping(value = "/**/{configCartItemHandle:.*}/{productCode:.*}/configCart", method = RequestMethod.GET)
	public String configureCartEntry(@PathVariable("configCartItemHandle") final String cartItemHandle,
			@PathVariable("productCode") final String productCode,
			@RequestParam(value = "tab", required = false) final String selectedGroup, final Model model,
			final HttpServletRequest request) throws CMSItemNotFoundException, UnsupportedEncodingException
	{
		String configId = null;

		final KBKeyData kbKey = getKBKeyData(productCode, model, request);
		final UiStatus uiStatus = getUiStatusFromSession(cartItemHandle, kbKey);

		if (uiStatus != null)
		{
			configId = uiStatus.getConfigId();
		}

		populateConfigurationModel(kbKey, configId, selectedGroup, model, uiStatus, cartItemHandle);

		LOG.debug("ConfigCart GET received for " + cartItemHandle + " - Current Session: "
				+ getSessionService().getCurrentSession().getSessionId());
		final String viewName = "addon:/" + Sapproductconfigb2baddonConstants.EXTENSIONNAME
				+ "/pages/configuration/configurationPage";
		return viewName;
	}

	/**
	 * Fetches KB key data from product
	 *
	 * @param productCode
	 * @param model
	 * @param request
	 * @return KB key data
	 * @throws CMSItemNotFoundException
	 */
	protected KBKeyData getKBKeyData(final String productCode, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		handleRequestContext(request, productModel);
		populateProductModel(productModel, model);
		updatePageTitle(productModel, model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, productConfigurationBreadcrumbBuilder.getBreadcrumbs(productModel));

		final KBKeyData kbKey = createKBKeyForProduct(productModel);
		return kbKey;
	}


	@RequestMapping(value = "/**/{productCode:.*}/configEntry", method = RequestMethod.GET)
	public String configureProduct(@PathVariable("productCode") final String productCode) throws CMSItemNotFoundException,
			UnsupportedEncodingException
	{


		final UiStatus uiStatus = getSessionService().getAttribute(PRODUCT_SESSION_PREFIX + productCode);
		final String selectedGroup = getSelectedGroup(uiStatus);

		String redirectURL;
		if (selectedGroup != null)
		{
			redirectURL = "redirect:/" + productCode + "/config?tab=" + selectedGroup;
		}
		else
		{
			redirectURL = "redirect:/" + productCode + "/config";
		}

		return redirectURL;
	}

	@RequestMapping(value = "/**/{productCode:.*}/config", method = RequestMethod.GET)
	public String configureProduct(@PathVariable("productCode") final String productCode,
			@RequestParam(value = "tab", required = false) final String selectedGroup, final Model model,
			final HttpServletRequest request) throws CMSItemNotFoundException, UnsupportedEncodingException
	{
		String configId = null;

		final UiStatus uiStatus = getSessionService().getAttribute(PRODUCT_SESSION_PREFIX + productCode);
		if (uiStatus != null)
		{
			configId = uiStatus.getConfigId();
		}

		final KBKeyData kbKey = getKBKeyData(productCode, model, request);

		LOG.debug("Config GET received for " + productCode + " - Current Session: "
				+ getSessionService().getCurrentSession().getSessionId());

		populateConfigurationModel(kbKey, configId, selectedGroup, model, uiStatus, null);

		final String viewName = "addon:/" + Sapproductconfigb2baddonConstants.EXTENSIONNAME
				+ "/pages/configuration/configurationPage";
		return viewName;
	}

	private void handleRequestContext(final HttpServletRequest request, final ProductModel productModel)
	{
		final RequestContextData requestContext = getRequestContextData(request);

		if (requestContext != null)
		{
			requestContext.setProduct(productModel);
		}
	}

	@RequestMapping(value = "/**/{productCode:.*}/config", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView updateConfigureProduct(
			@ModelAttribute(Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE) @Valid @MatchesPattern(Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE) final ConfigurationData configData,
			BindingResult bindingResult, final Model model, final HttpServletRequest request) throws CMSItemNotFoundException,
			UnsupportedEncodingException
	{
		LOG.debug("Config POST for " + configData.getKbKey().getProductCode());

		final UiStatusSync uiStatusSync = new UiStatusSync();
		final UiStatus uiStatus = uiStatusSync.storeUiStatusInSession(configData);
		getSessionService().setAttribute(PRODUCT_SESSION_PREFIX + configData.getKbKey().getProductCode(), uiStatus);

		Map<String, FieldError> userInputToRestore = null;
		final boolean validationErrors = bindingResult.hasErrors();
		if (validationErrors)
		{
			userInputToRestore = handleValidationErrorsBeforeUpdate(configData, bindingResult);
		}

		removeNullCstics(configData.getGroups());
		getConfigFacade().updateConfiguration(configData);
		final ConfigurationData latestConfiguration = getConfigFacade().getConfiguration(configData);

		resetGroupStatus(latestConfiguration);
		if (validationErrors)
		{
			bindingResult = restoreValidationErrorsAfterUpdate(userInputToRestore, latestConfiguration);
			model.addAttribute(BindingResult.MODEL_KEY_PREFIX + Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE, bindingResult);
		}

		getProductConfigurationConflictChecker().checkConflicts(latestConfiguration, bindingResult);
		if (configData.getCartItemPK() != null && !configData.getCartItemPK().isEmpty())
		{
			getProductConfigurationConflictChecker().checkMandatoryFields(configData, bindingResult);
		}

		uiStatusSync.updateUiStatus(configData, uiStatus);
		model.addAttribute(Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE, latestConfiguration);

		final ProductModel productModel = getProductService().getProductForCode(configData.getKbKey().getProductCode());
		handleRequestContext(request, productModel);
		populateProductModel(productModel, model);



		final ModelAndView modelAndView = new ModelAndView("addon:/" + Sapproductconfigb2baddonConstants.EXTENSIONNAME
				+ "/pages/configuration/configurationPageForAJAXRequests");
		return modelAndView;
	}

	@RequestMapping(value = "/**/{productCode:.*}/redirectconfig", method = RequestMethod.POST)
	public String updateConfigureProductAndRedirect(
			@PathVariable("productCode") final String productCode,
			@ModelAttribute(Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE) @Valid @MatchesPattern(Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE) final ConfigurationData configData,
			BindingResult bindingResult, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException,
			UnsupportedEncodingException
	{
		LOG.debug("Config POST for " + configData.getKbKey().getProductCode());

		final UiStatusSync uiStatusSync = new UiStatusSync();
		final UiStatus uiStatus = uiStatusSync.storeUiStatusInSession(configData);
		getSessionService().setAttribute(PRODUCT_SESSION_PREFIX + configData.getKbKey().getProductCode(), uiStatus);

		Map<String, FieldError> userInputToRestore = null;
		final boolean validationErrors = bindingResult.hasErrors();
		if (validationErrors)
		{
			userInputToRestore = handleValidationErrorsBeforeUpdate(configData, bindingResult);
		}

		removeNullCstics(configData.getGroups());
		getConfigFacade().updateConfiguration(configData);
		final ConfigurationData latestConfiguration = getConfigFacade().getConfiguration(configData);

		resetGroupStatus(latestConfiguration);
		if (validationErrors)
		{
			bindingResult = restoreValidationErrorsAfterUpdate(userInputToRestore, latestConfiguration);
			redirectAttributes.addFlashAttribute(
					BindingResult.MODEL_KEY_PREFIX + Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE, bindingResult);
		}

		getProductConfigurationConflictChecker().checkConflicts(latestConfiguration, bindingResult);
		if (configData.getCartItemPK() != null && !configData.getCartItemPK().isEmpty())
		{
			getProductConfigurationConflictChecker().checkMandatoryFields(configData, bindingResult);
		}

		uiStatusSync.updateUiStatus(configData, uiStatus);
		redirectAttributes.addFlashAttribute(Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE, latestConfiguration);
		redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE,
				bindingResult);

		final boolean autoExpand = configData.isAutoExpand();
		redirectAttributes.addFlashAttribute("autoExpand", Boolean.valueOf(autoExpand));
		final String focusId = configData.getFocusId();
		redirectAttributes.addFlashAttribute("focusId", focusId);

		final String redirectURL = "redirect:/" + productCode + "/config?tab=" + configData.getSelectedGroup();

		return redirectURL;
	}

	protected void updatePageTitle(final ProductModel productModel, final Model model)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveProductPageTitle(productModel));
	}

	protected UiStatus getUiStatusFromSession(final String cartItemHandle, final KBKeyData kbKey)
	{
		UiStatus uiStatus = getSessionService().getAttribute(CART_ITEM_HANDLE_SESSION_PREFIX + cartItemHandle);

		//this shall happen only when the cart is restored
		if (uiStatus == null)
		{
			final String configId = getSessionService().getAttribute(
					SapproductconfigruntimeinterfaceConstants.PRODUCT_CONFIG_SESSION_PREFIX + cartItemHandle);
			final ConfigurationData confData = getConfigDataForRestoredProduct(kbKey, configId);

			final UiStatusSync uiStatusSync = new UiStatusSync();
			uiStatus = uiStatusSync.storeUiStatusInSession(confData);
		}

		return uiStatus;
	}

	protected ConfigurationData getConfigDataForRestoredProduct(final KBKeyData kbKey, final String configId)
	{
		ConfigurationData confData = null;
		if (configId == null)
		{
			confData = this.loadNewConfiguration(kbKey, null);
		}
		else
		{
			confData = this.getConfigData(kbKey, configId);
		}
		return confData;
	}
}
