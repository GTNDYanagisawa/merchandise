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
package de.hybris.platform.secureportaladdon.controllers;

import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.pages.AbstractLoginPageController;
import de.hybris.secureportaladdon.forms.RegistrationForm;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;


/**
 * This class provides an implementation skeleton for a registration page controller for the B2B accelerator
 */
public abstract class AbstractB2BRegistrationController extends AbstractLoginPageController
{

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource(name = "countryConverter")
	private Converter<CountryModel, CountryData> countryConverter;

	/**
	 * Method that ensures the model is populated with all required CMS components in order to properly render the
	 * registration page.
	 */
	protected abstract void populateModelCmsContent(Model model, ContentPageModel contentPageModel);

	/**
	 * Return the name of the view to be used for the registration page
	 */
	protected abstract String getRegistrationView();


	/**
	 * Return the name of the cms page to be used for the registration page
	 */
	protected abstract String getRegistrationCmsPage();

	/**
	 * Calls the implementation of both other methods declared above and adds the required registration form to the
	 * model.
	 */
	protected String getDefaultRegistrationPage(final Model model, final ContentPageModel contentPageModel)
	{
		populateModelCmsContent(model, contentPageModel);
		model.addAttribute(new RegistrationForm());
		return getRegistrationView();
	}

	/**
	 * Spring MVC Model attribute that holds the list of titles used to populate the "Title" dropdown.
	 */
	@ModelAttribute("titles")
	public List<TitleData> getTitles()
	{
		return getUserFacade().getTitles();
	}

	/**
	 * Spring MVC Model attribute that holds the list of countries used to populate the "Country" dropdown.
	 */
	@ModelAttribute("countries")
	public List<CountryData> getCountries()
	{
		final List<CountryData> countries = Converters.convertAll(commonI18NService.getAllCountries(), countryConverter);

		return countries;
	}

	/**
	 * Spring MVC Model attribute that holds the list of countries used to populate the "Country" dropdown.
	 */
	@ModelAttribute("enableCaptcha")
	public boolean getEnableCaptcha()
	{
		return BooleanUtils.toBoolean(baseStoreService.getCurrentBaseStore().getCaptchaCheckEnabled());
	}

}
