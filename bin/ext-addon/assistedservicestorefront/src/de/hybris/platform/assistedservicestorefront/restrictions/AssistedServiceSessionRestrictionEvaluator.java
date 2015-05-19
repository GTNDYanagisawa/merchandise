package de.hybris.platform.assistedservicestorefront.restrictions;

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

import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.cms2.model.restrictions.AssistedServiceSessionRestrictionModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Evaluates an ASM agent through session.
 * <p/>
 *
 */
public class AssistedServiceSessionRestrictionEvaluator implements
		CMSRestrictionEvaluator<AssistedServiceSessionRestrictionModel>
{
	private final static Logger LOG = Logger.getLogger(AssistedServiceSessionRestrictionEvaluator.class);

	private AssistedServiceFacade defaultAssistedServiceFacade;

	@Override
	public boolean evaluate(final AssistedServiceSessionRestrictionModel amsSessionRestriction, final RestrictionData context)
	{
		return defaultAssistedServiceFacade.isAssistedServiceAgentLoggedIn();
	}

	@Required
	public void setDefaultAssistedServiceFacade(final AssistedServiceFacade defaultAssistedServiceFacade)
	{
		this.defaultAssistedServiceFacade = defaultAssistedServiceFacade;
	}
}
