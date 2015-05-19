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
package de.hybris.platform.b2bpunchoutaddon.controllers.pages;

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.services.CipherService;
import de.hybris.platform.b2b.punchout.services.PunchOutService;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.b2bpunchoutaddon.constants.B2bpunchoutaddonConstants;
import de.hybris.platform.b2bpunchoutaddon.services.PunchOutUserAuthenticationService;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.pages.AbstractPageController;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.util.GlobalMessages;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.cxml.CXML;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Component
public class DefaultPunchOutController extends AbstractPageController implements PunchOutController
{
	private static final Logger LOG = Logger.getLogger(DefaultPunchOutController.class);

	private static final String CART_CMS_PAGE = "cartPage";
	public static final String REDIRECT_PREFIX = "redirect:";
	public static final String ADDON_PREFIX = "addon:";
	public static final String BASE_ADDON_PAGE_PATH = "/b2bpunchoutaddon/pages";

	@Resource
	private PunchOutSessionService punchoutSessionService;

	@Resource
	private CipherService cipherService;

	@Resource
	private PunchOutService punchOutService;

	@Resource
	private PunchOutUserAuthenticationService punchOutUserAuthenticationService;

	/**
	 * Used to create a new punch out session by authenticating a punch out user.
	 * 
	 * @param key
	 *           Secured key that have been exchanged with the punch out provider (or security hub).
	 * @param sessionId
	 *           the hybris session ID
	 */
	@RequestMapping(value = "/punchout/cxml/session", method = RequestMethod.GET)
	public void handlePunchOutSession(@RequestParam final String key, @RequestParam("sid") final String sessionId,
			final HttpServletRequest request, final HttpServletResponse response)
	{
		final PunchOutSession punchOutSession = punchoutSessionService.load(sessionId);
		LOG.debug("PunchOut session: " + printSessionInfo(punchOutSession));

		final String userId = cipherService.retrieveUserId(key, punchOutSession);

		final HttpSession session = request.getSession();
		session.removeAttribute(B2bpunchoutaddonConstants.PUNCHOUT_USER);

		getPunchOutUserAuthenticationService().authenticate(userId, true, punchOutSession, request, response);

		// if user was authenticated (no exception was thrown), set the current cart with the one from the setup request session
		punchoutSessionService.setCurrentCartFromPunchOutSetup(sessionId);

		session.setAttribute(B2bpunchoutaddonConstants.PUNCHOUT_USER, userId);
	}


	/**
	 * Cancels a requisition (POST) to the punch out provider sending a cancel message.
	 * 
	 * @return Redirect to cart page.
	 * @throws InvalidCartException
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/punchout/cxml/cancel", method = RequestMethod.GET)
	public String cancelRequisition(final Model model, final HttpServletRequest request) throws InvalidCartException,
			CMSItemNotFoundException
	{
		final CXML cXML = punchOutService.processCancelPunchOutOrderMessage();
		processRequisitionMessage(cXML, model, request);

		return ADDON_PREFIX + BASE_ADDON_PAGE_PATH + "/punchout/punchoutSendOrderPage";
	}


	private String printSessionInfo(final PunchOutSession punchoutSession)
	{

		return new ToStringBuilder(punchoutSession, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("operation", punchoutSession.getOperation())
				.append("browserFormPostUrl", punchoutSession.getBrowserFormPostUrl())
				.append("buyerCookie", punchoutSession.getBuyerCookie())
				.append("time", punchoutSession.getTime())
				.append("initiatedBy",
						ToStringBuilder.reflectionToString(punchoutSession.getInitiatedBy(), ToStringStyle.SHORT_PREFIX_STYLE))
				.append("targetedTo",
						ToStringBuilder.reflectionToString(punchoutSession.getTargetedTo(), ToStringStyle.SHORT_PREFIX_STYLE))
				.append("sentBy", ToStringBuilder.reflectionToString(punchoutSession.getSentBy(), ToStringStyle.SHORT_PREFIX_STYLE))
				.append("shippingAddress",
						ToStringBuilder.reflectionToString(punchoutSession.getShippingAddress(), ToStringStyle.SHORT_PREFIX_STYLE))
				.toString();
	}

	/**
	 * Places a requisition (POST) to the punchout provider sending the information of the cart.
	 * 
	 * @return Redirect to cart page.
	 * @throws InvalidCartException
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/punchout/cxml/requisition", method = RequestMethod.GET)
	public String placeRequisition(final Model model, final HttpServletRequest request) throws InvalidCartException,
			CMSItemNotFoundException
	{
		final CXML cXML = punchOutService.processPunchOutOrderMessage();
		processRequisitionMessage(cXML, model, request);

		return ADDON_PREFIX + BASE_ADDON_PAGE_PATH + "/punchout/punchoutSendOrderPage";
	}


	protected void processRequisitionMessage(final CXML cXML, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{

		final String cXMLContents = PunchOutUtils.transformCXMLToBase64(cXML);
		model.addAttribute("orderAsCXML", cXMLContents);
		model.addAttribute("browseFormPostUrl", punchoutSessionService.getCurrentPunchOutSession().getBrowserFormPostUrl());

		GlobalMessages.addConfMessage(model, "punchout.message.redirecting");

		storeCmsPageInModel(model, getContentPageForLabelOrId(CART_CMS_PAGE));

		getPunchOutUserAuthenticationService().logout();
		request.getSession().invalidate();

	}

	@ExceptionHandler(Exception.class)
	public String handleException(final Exception e, final HttpServletRequest request)
	{
		LOG.error(e.getMessage(), e);
		request.setAttribute("error", e);
		request.setAttribute("message", PunchOutException.PUNCHOUT_EXCEPTION_MESSAGE);

		return ADDON_PREFIX + BASE_ADDON_PAGE_PATH + "/error/punchoutError";
	}

	public void setPunchOutService(final PunchOutService punchoutService)
	{
		this.punchOutService = punchoutService;
	}

	public PunchOutUserAuthenticationService getPunchOutUserAuthenticationService()
	{
		return punchOutUserAuthenticationService;
	}

	public void setPunchOutUserAuthenticationService(final PunchOutUserAuthenticationService punchOutUserAuthenticationService)
	{
		this.punchOutUserAuthenticationService = punchOutUserAuthenticationService;
	}
}
