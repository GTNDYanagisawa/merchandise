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

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.secureportaladdon.constants.SecureportaladdonWebConstants;
import de.hybris.platform.secureportaladdon.data.B2BRegistrationData;
import de.hybris.platform.secureportaladdon.exceptions.CustomerAlreadyExistsException;
import de.hybris.platform.secureportaladdon.facades.B2BRegistrationFacade;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.yb2bacceleratorstorefront.breadcrumb.Breadcrumb;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.util.GlobalMessages;
import de.hybris.secureportaladdon.forms.RegistrationForm;

import java.util.Collections;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Registration page controller: Handles Get and Post request and dispatches relevant wortkflow facades and necessary
 * services
 */
@SuppressWarnings("unused")
@Controller
@RequestMapping(value = SecureportaladdonWebConstants.RequestMappings.ACCOUNT_REGISTRATION)
public class B2BRegistrationController extends AbstractB2BRegistrationController
{

	private final static class MessageKeys
	{
		public static final String REGISTER_SUBMIT_CONFIRMATION = "register.submit.confirmation";
		public static final String REGISTER_ACCOUNT_EXISTING = "register.account.existing";
		public static final String SCP_LINK_CREATE_ACCOUNT = "secureportal.link.createAccount";
	}

	private static final String HOME_REDIRECT = REDIRECT_PREFIX + ROOT;

	@Resource(name = "checkoutFacade")
	private CheckoutFacade checkoutFacade;

	@Resource(name = "b2bRegistrationFacade")
	private B2BRegistrationFacade b2bRegistrationFacade;

	@Resource(name = "modelService")
	private ModelService modelService;

	@RequestMapping(method = RequestMethod.GET)
	public String showRegistrationPage(final HttpServletRequest request, final Model model) throws CMSItemNotFoundException
	{
		if (getCmsSiteService().getCurrentSite().isEnableRegistration())
		{
			return getDefaultRegistrationPage(model, getContentPageForLabelOrId(getRegistrationCmsPage()));
		}
		return HOME_REDIRECT;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String submitRegistration(@Valid final RegistrationForm form, final BindingResult bindingResult, final Model model,
			final HttpServletRequest request, final HttpSession session, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{

		populateModelCmsContent(model, getContentPageForLabelOrId(getRegistrationCmsPage()));
		model.addAttribute(form);

		if (bindingResult.hasErrors())
		{
			return getRegistrationView();
		}

		try
		{
			b2bRegistrationFacade.register(convertFormToData(form));
		}
		catch (final CustomerAlreadyExistsException e)
		{
			GlobalMessages.addErrorMessage(model, Localization.getLocalizedString(MessageKeys.REGISTER_ACCOUNT_EXISTING));
			return getRegistrationView();
		}

		GlobalMessages.addInfoMessage(model, Localization.getLocalizedString(MessageKeys.REGISTER_SUBMIT_CONFIRMATION));

		return getDefaultLoginPage(false, session, model);

	}

	/**
	 * @param form
	 *           Form data as submitted by user
	 * @return A DTO object built from the form instance
	 */
	private B2BRegistrationData convertFormToData(final RegistrationForm form)
	{
		final B2BRegistrationData registrationData = new B2BRegistrationData();
		BeanUtils.copyProperties(form, registrationData);
		return registrationData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.secureportaladdon.controllers.AbstractB2BRegistrationController#getRegistrationView()
	 */
	@Override
	protected String getRegistrationView()
	{
		return SecureportaladdonWebConstants.Views.REGISTRATION_PAGE;
	}


	@Override
	protected String getRegistrationCmsPage()
	{
		return SecureportaladdonWebConstants.CMS_REGISTER_PAGE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.secureportaladdon.controllers.AbstractB2BRegistrationController#populateModelCmsContent(org.
	 * springframework.ui.Model, de.hybris.platform.cms2.model.pages.ContentPageModel)
	 */
	@Override
	protected void populateModelCmsContent(final Model model, final ContentPageModel contentPageModel)
	{

		storeCmsPageInModel(model, contentPageModel);
		setUpMetaDataForContentPage(model, contentPageModel);

		final Breadcrumb registrationBreadcrumbEntry = new Breadcrumb("#",
				Localization.getLocalizedString(MessageKeys.SCP_LINK_CREATE_ACCOUNT), null);
		model.addAttribute("breadcrumbs", Collections.singletonList(registrationBreadcrumbEntry));

	}


	@Override
	protected String getLoginView()
	{
		return SecureportaladdonWebConstants.Views.LOGIN_PAGE;
	}


	@Override
	protected AbstractPageModel getLoginCmsPage() throws CMSItemNotFoundException
	{
		return getContentPageForLabelOrId("login");
	}


	@Override
	protected String getSuccessRedirect(final HttpServletRequest request, final HttpServletResponse response)
	{
		return HOME_REDIRECT;
	}

}