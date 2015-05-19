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
package de.hybris.platform.addonsupport.setup;

import de.hybris.platform.core.initialization.SystemSetupContext;


public interface AddOnSystemSetupSupport
{
	String IMPORT_SITES = "importSites";
	String IMPORT_SYNC_CATALOGS = "syncProducts&ContentCatalogs";
	String ACTIVATE_SOLR_CRON_JOBS = "activateSolrCronJobs";

	boolean synchronizeContentCatalog(final SystemSetupContext context, final String catalogName);

	boolean synchronizeProductCatalog(final SystemSetupContext context, final String catalogName);

	boolean getBooleanSystemSetupParameter(final SystemSetupContext context, final String key);

	void executeSolrIndexerCronJob(final String solrFacetSearchConfigName, final boolean fullReIndex);
}
