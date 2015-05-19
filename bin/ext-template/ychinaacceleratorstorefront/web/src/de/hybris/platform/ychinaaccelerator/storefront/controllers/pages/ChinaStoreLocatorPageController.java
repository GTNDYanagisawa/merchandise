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
package de.hybris.platform.ychinaaccelerator.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.StorefinderBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.chinaaccelerator.facades.StoreData;
import de.hybris.platform.chinaaccelerator.facades.data.CityData;
import de.hybris.platform.chinaaccelerator.facades.storefinder.ChinaStoreLocatorFacade;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.ychinaaccelerator.storefront.controllers.ControllerConstants;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * Controller for store locator search.
 */

@Controller
@Scope("tenant")
@RequestMapping(value = "/store-finder")
public class ChinaStoreLocatorPageController extends StoreLocatorPageController
{
	protected static final Logger LOG = Logger.getLogger(ChinaStoreLocatorPageController.class);

	private static final String BAIDU_API_KEY_ID = "baiduMapApiKey";
	private static final String BAIDU_API_VERSION = "baiduMapApiVersion";

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "storefinderBreadcrumbBuilder")
	private StorefinderBreadcrumbBuilder storefinderBreadcrumbBuilder;

	@Resource(name = "storeLocatorFacade")
	private ChinaStoreLocatorFacade storeLocatorFacade;

	@ModelAttribute("baiduMapApiVersion")
	public String getBaiduMapApiVersion()
	{
		return configurationService.getConfiguration().getString(BAIDU_API_VERSION);
	}

	@ModelAttribute("baiduMapApiKey")
	public String getBaiduMapApiKey(final HttpServletRequest request)
	{
		final String baiduMapApiKey = getHostConfigService().getProperty(BAIDU_API_KEY_ID, request.getServerName());
		if (StringUtils.isEmpty(baiduMapApiKey))
		{
			LOG.warn("No Baidu Map API key found for server: " + request.getServerName());
		}
		return baiduMapApiKey;
	}


	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String getStoreFinderPage(final Model model) throws CMSItemNotFoundException
	{
		setUpPageForms(model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, storefinderBreadcrumbBuilder.getBreadcrumbs());
		storeCmsPageInModel(model, getStoreFinderPage());
		setUpMetaDataForContentPage(model, (ContentPageModel) getStoreFinderPage());

		final List<CityData> cities = this.storeLocatorFacade.getCitiesOnlyWithStores();
		model.addAttribute("cities", cities);

		return ControllerConstants.Views.Pages.StoreFinder.StoreFinderSearchPage;
	}


	@RequestMapping(value = "/getStoresByCity", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	List<StoreData> getStoresByCity(@RequestParam("cityId") final long cityId, final Model model)
	{
		if (cityId <= 0)
		{
			return java.util.Collections.EMPTY_LIST;
		}

		final List<StoreData> stores = this.storeLocatorFacade.getStoresByCities(cityId);
		if (null == stores || stores.size() == 0)
		{
			return java.util.Collections.EMPTY_LIST;
		}

		return stores;
	}


	@RequestMapping(value = "/getCitiesAndStoreCounts", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	List<CityData> getCitiesAndStoreCounts(final Model model)
	{
		final List<CityData> cities = this.storeLocatorFacade.getAllCities();
		if (null == cities || cities.size() == 0)
		{
			return java.util.Collections.EMPTY_LIST;
		}

		return cities;
	}
}
