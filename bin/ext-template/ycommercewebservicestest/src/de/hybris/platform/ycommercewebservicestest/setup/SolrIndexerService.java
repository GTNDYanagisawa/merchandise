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
 */
package de.hybris.platform.ycommercewebservicestest.setup;

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearchException;
import de.hybris.platform.jalo.type.JaloTypeException;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.jalo.SolrfacetsearchManager;
import de.hybris.platform.solrfacetsearch.jalo.config.SolrFacetSearchConfig;
import de.hybris.platform.solrfacetsearch.jalo.indexer.cron.SolrIndexerCronJob;
import de.hybris.platform.solrfacetsearch.model.indexer.cron.SolrIndexerCronJobModel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class SolrIndexerService
{
	private static final Logger LOG = Logger.getLogger(SolrIndexerService.class);

	private ModelService modelService;
	private CronJobService cronJobService;

	public void executeSolrIndexerCronJob(final String solrFacetSearchConfigName)
	{
		final SolrFacetSearchConfig solrFacetSearchConfig = getSolrFacetSearchConfigForName(solrFacetSearchConfigName);
		if (solrFacetSearchConfig != null)
		{
			final SolrIndexerCronJobModel solrIndexerJobModel = getSolrIndexerJob(solrFacetSearchConfig, IndexerOperationValues.FULL);
			if (solrIndexerJobModel != null)
			{
				LOG.info("Starting solr " + IndexerOperationValues.FULL + " index operation for [" + solrFacetSearchConfig.getName()
						+ "] ...");

				getCronJobService().performCronJob(solrIndexerJobModel, true);

				LOG.info("Completed solr " + IndexerOperationValues.FULL + " index operation for [" + solrFacetSearchConfig.getName()
						+ "]");
			}
		}
	}

	protected SolrFacetSearchConfig getSolrFacetSearchConfigForName(final String solrFacetSearchConfigName)
	{
		try
		{
			return SolrfacetsearchManager.getInstance().getSolrFacetConfig(solrFacetSearchConfigName);
		}
		catch (final FlexibleSearchException ignore)
		{
			LOG.error(ignore.toString());
		}
		return null;
	}

	protected SolrIndexerCronJobModel getSolrIndexerJob(final SolrFacetSearchConfig solrFacetSearchConfig,
			final IndexerOperationValues indexerOperation)
	{
		SolrIndexerCronJobModel indexerCronJob = getExistingSolrIndexerJob(solrFacetSearchConfig, indexerOperation);
		if (indexerCronJob == null)
		{
			indexerCronJob = createSolrIndexerJob(solrFacetSearchConfig, indexerOperation);
		}
		return indexerCronJob;
	}

	protected SolrIndexerCronJobModel getExistingSolrIndexerJob(final SolrFacetSearchConfig solrFacetSearchConfig,
			final IndexerOperationValues indexerOperation)
	{
		final String indexerCronJobName = buildSolrCronJobCode(solrFacetSearchConfig, indexerOperation);

		try
		{
			final CronJobModel cronJob = getCronJobService().getCronJob(indexerCronJobName);
			if (cronJob instanceof SolrIndexerCronJobModel)
			{
				return (SolrIndexerCronJobModel) cronJob;
			}
		}
		catch (final UnknownIdentifierException ignore)
		{
            LOG.error(ignore.toString());		}
		catch (final AmbiguousIdentifierException ignore)
		{
            LOG.error(ignore.toString());
        }
		return null;
	}

	protected SolrIndexerCronJobModel createSolrIndexerJob(final SolrFacetSearchConfig solrFacetSearchConfig,
			final IndexerOperationValues indexerOperation)
	{
		final String indexerCronJobName = buildSolrCronJobCode(solrFacetSearchConfig, indexerOperation);

		try
		{
			final EnumerationValue indexerOperationEnum = getModelService().getSource(indexerOperation);
			final SolrIndexerCronJob solrIndexerCronJob = SolrfacetsearchManager.getInstance().createSolrIndexerCronJob(
					indexerCronJobName, solrFacetSearchConfig, indexerOperationEnum);
			return getModelService().get(solrIndexerCronJob);
		}
		catch (final JaloTypeException e)
		{
			throw new SystemException("Cannot create indexer job [" + indexerCronJobName + "] due to: " + e.getMessage(), e);
		}
	}

	protected String buildSolrCronJobCode(final SolrFacetSearchConfig solrFacetSearchConfig,
			final IndexerOperationValues indexerOperation)
	{
		return indexerOperation.getCode() + "-" + solrFacetSearchConfig.getName() + "-cronJob";
	}


	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public CronJobService getCronJobService()
	{
		return cronJobService;
	}

	@Required
	public void setCronJobService(final CronJobService cronJobService)
	{
		this.cronJobService = cronJobService;
	}
}
