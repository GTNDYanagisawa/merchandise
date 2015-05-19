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

import de.hybris.platform.commerceservices.setup.CommerceServicesSystemSetup;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;

import java.util.List;
import org.apache.log4j.Logger;

/**
 * This class imports the commercesearch sample data. It does not hook into the normal system setup processing but
 * instead is triggered by the SampleDataImportedEventListener
 */
public class CommerceSearchSampleDataSystemSetup extends CommerceServicesSystemSetup
{
	private static final Logger LOG = Logger.getLogger(CommerceSearchSampleDataSystemSetup.class);

	public void importSampleData(final String importDirectory, final SystemSetupContext context, final List<ImportData> importData)
	{
		importCommonData(context, importDirectory);

		for (final ImportData data : importData)
		{
			final List<String> storeNames = data.getStoreNames();
			for (final String storeName : storeNames)
			{
				importData(context, importDirectory, storeName);
			}
		}
	}

	protected void importData(final SystemSetupContext context, final String importDirectory, final String storeName)
	{
		LOG.info("Begin importing " + importDirectory + " sampledata for store [" + storeName + "]");

		final String importRoot = "/" + importDirectory + "/import";
		importImpexFile(context, importRoot + "/stores/" + storeName + "/commercesearch-data.impex", false);

		LOG.info("Done importing " + importDirectory + " sampledata for store [" + storeName + "]");
	}

	protected void importCommonData(final SystemSetupContext context, final String importDirectory)
	{
		final String importRoot = "/" + importDirectory + "/import";
		importImpexFile(context, importRoot + "/common/common-commercesearch-data.impex", false);
	}
}
