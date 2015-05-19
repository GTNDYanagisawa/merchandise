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
package de.hybris.platform.addonsupport.setup.impl;

import de.hybris.platform.commerceservices.setup.events.SampleDataImportedEvent;
import de.hybris.platform.addonsupport.setup.AddOnSampleDataImportService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import org.springframework.beans.factory.annotation.Required;


public class GenericAddOnSampleDataEventListener extends AbstractEventListener<SampleDataImportedEvent>
{

	private String extensionName;
	private AddOnSampleDataImportService addOnSampleDataImportService;
	private boolean solrReindex;

	@Override
	protected void onEvent(final SampleDataImportedEvent event)
	{
		getAddOnSampleDataImportService().importSampleData(getExtensionName(), event.getContext(), event.getImportData(),
				isSolrReindex());
	}

	public AddOnSampleDataImportService getAddOnSampleDataImportService()
	{
		return addOnSampleDataImportService;
	}

	@Required
	public void setAddOnSampleDataImportService(final AddOnSampleDataImportService addOnSampleDataImportService)
	{
		this.addOnSampleDataImportService = addOnSampleDataImportService;
	}

	public String getExtensionName()
	{
		return extensionName;
	}

	@Required
	public void setExtensionName(final String extensionName)
	{
		this.extensionName = extensionName;
	}

	public boolean isSolrReindex()
	{
		return solrReindex;
	}

	public void setSolrReindex(final boolean solrReindex)
	{
		this.solrReindex = solrReindex;
	}



}
