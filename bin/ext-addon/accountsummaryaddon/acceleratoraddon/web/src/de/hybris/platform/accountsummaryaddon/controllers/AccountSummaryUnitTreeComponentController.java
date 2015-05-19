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
package de.hybris.platform.accountsummaryaddon.controllers;

import de.hybris.platform.b2bacceleratorfacades.company.B2BCommerceUnitFacade;
import de.hybris.platform.b2bacceleratorfacades.company.data.B2BUnitNodeData;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.cms.AbstractCMSComponentController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import de.hybris.platform.accountsummaryaddon.constants.AccountsummaryaddonConstants;
import de.hybris.platform.accountsummaryaddon.model.AccountSummaryUnitTreeComponentModel;


/**
 * Controller to display the AccountSummaryUnitTree component
 * 
 */
@Controller("AccountSummaryUnitTreeComponentController")
@Scope("tenant")
@RequestMapping(value = "/view/AccountSummaryUnitTreeComponentController")
public class AccountSummaryUnitTreeComponentController extends
		AbstractCMSComponentController<AccountSummaryUnitTreeComponentModel>
{
	@Resource(name = "b2bCommerceUnitFacade")
	protected B2BCommerceUnitFacade b2bCommerceUnitFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final AccountSummaryUnitTreeComponentModel component)
	{
		final B2BUnitNodeData rootNode = b2bCommerceUnitFacade.getParentUnitNode();
		model.addAttribute("rootNode", rootNode);
		model.addAttribute("metaRobots", "no-index,no-follow");

	}

	@Override
	protected String getView(final AccountSummaryUnitTreeComponentModel component)
	{
		return AccountsummaryaddonConstants.ACCOUNT_SUMMARY_UNIT_TREE_PAGE;
	}
}