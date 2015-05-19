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
package de.hybris.platform.acceleratorservices.process.email.actions;

import de.hybris.platform.acceleratorservices.email.CMSEmailPageService;
import de.hybris.platform.acceleratorservices.email.EmailGenerationService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.task.RetryLaterException;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * A process action to generate email.
 */
public class GenerateEmailAction extends AbstractSimpleDecisionAction
{
	private static final Logger LOG = Logger.getLogger(GenerateEmailAction.class);

	private CMSEmailPageService cmsEmailPageService;
	private String frontendTemplateName;
	private ProcessContextResolutionStrategy contextResolutionStrategy;
	private EmailGenerationService emailGenerationService;

	protected CMSEmailPageService getCmsEmailPageService()
	{
		return cmsEmailPageService;
	}

	@Required
	public void setCmsEmailPageService(final CMSEmailPageService cmsEmailPageService)
	{
		this.cmsEmailPageService = cmsEmailPageService;
	}

	protected String getFrontendTemplateName()
	{
		return frontendTemplateName;
	}

	@Required
	public void setFrontendTemplateName(final String frontendTemplateName)
	{
		this.frontendTemplateName = frontendTemplateName;
	}

	protected ProcessContextResolutionStrategy getContextResolutionStrategy()
	{
		return contextResolutionStrategy;
	}

	@Required
	public void setContextResolutionStrategy(final ProcessContextResolutionStrategy contextResolutionStrategy)
	{
		this.contextResolutionStrategy = contextResolutionStrategy;
	}


	protected EmailGenerationService getEmailGenerationService()
	{
		return emailGenerationService;
	}

	@Required
	public void setEmailGenerationService(final EmailGenerationService emailGenerationService)
	{
		this.emailGenerationService = emailGenerationService;
	}

	@Override
	public Transition executeAction(final BusinessProcessModel businessProcessModel) throws RetryLaterException
	{
		final CatalogVersionModel contentCatalogVersion = getContextResolutionStrategy().getContentCatalogVersion(
				businessProcessModel);
		if (contentCatalogVersion != null)
		{
			final EmailPageModel emailPageModel = getCmsEmailPageService().getEmailPageForFrontendTemplate(
					getFrontendTemplateName(), contentCatalogVersion);

			if (emailPageModel != null)
			{
				final EmailMessageModel emailMessageModel = getEmailGenerationService()
						.generate(businessProcessModel, emailPageModel);
				if (emailMessageModel != null)
				{
					final List<EmailMessageModel> emails = new ArrayList<EmailMessageModel>();
					emails.addAll(businessProcessModel.getEmails());
					emails.add(emailMessageModel);
					businessProcessModel.setEmails(emails);

					getModelService().save(businessProcessModel);

					LOG.info("Email message generated");
					return Transition.OK;
				}
				else
				{
					LOG.warn("Failed to generate email message");
				}
			}
			else
			{
				LOG.warn("Could not retrieve email page model for " + getFrontendTemplateName() + " and "
						+ contentCatalogVersion.getCatalog().getName() + ":" + contentCatalogVersion.getVersion()
						+ ", cannot generate email content");
			}
		}
		else
		{
			LOG.warn("Could not resolve the content catalog version, cannot generate email content");
		}

		return Transition.NOK;
	}
}
