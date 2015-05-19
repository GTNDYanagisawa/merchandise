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
 */
package de.hybris.platform.integration.ondemand.sampledata.event;

import de.hybris.platform.commerceservices.setup.events.SampleDataImportedEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import org.springframework.beans.factory.annotation.Required;


/**
 * Event listener notified when the yacceleratorinitialdata has imported the sample data
 */
public class SampleDataImportedEventListener extends AbstractEventListener<SampleDataImportedEvent>
{

	private OndemandSampleDataSystemSetup ondemandSampleDataSystemSetup;

	@Override
	protected void onEvent(final SampleDataImportedEvent event)
	{
		// Lookup the tenant scoped OmsSampleDataSystemSetup and get it to import its sample data
		getOndemandSampleDataSystemSetup().importSampleData("ondemandsampledata", event.getContext(), event.getImportData());
	}

	protected OndemandSampleDataSystemSetup getOndemandSampleDataSystemSetup()
	{
		return ondemandSampleDataSystemSetup;
	}

	@Required
	public void setOndemandSampleDataSystemSetup(final OndemandSampleDataSystemSetup ondemandSampleDataSystemSetup)
	{
		this.ondemandSampleDataSystemSetup = ondemandSampleDataSystemSetup;
	}
}
