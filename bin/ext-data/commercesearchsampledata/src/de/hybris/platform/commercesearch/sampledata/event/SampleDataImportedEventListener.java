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
package de.hybris.platform.commercesearch.sampledata.event;

import de.hybris.platform.commerceservices.setup.events.SampleDataImportedEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;


/**
 * Event listener notified when the yacceleratorinitialdata has imported the sample data
 */
public class SampleDataImportedEventListener extends AbstractEventListener<SampleDataImportedEvent>
{

	private CommerceSearchSampleDataSystemSetup commerceSearchSampleDataSystemSetup;

	@Override
	protected void onEvent(final SampleDataImportedEvent event)
	{
		getCommerceSearchSampleDataSystemSetup().importSampleData("commercesearchsampledata", event.getContext(),
				event.getImportData());
	}

	public CommerceSearchSampleDataSystemSetup getCommerceSearchSampleDataSystemSetup()
	{
		return commerceSearchSampleDataSystemSetup;
	}

	public void setCommerceSearchSampleDataSystemSetup(
			final CommerceSearchSampleDataSystemSetup commerceSearchSampleDataSystemSetup)
	{
		this.commerceSearchSampleDataSystemSetup = commerceSearchSampleDataSystemSetup;
	}
}
