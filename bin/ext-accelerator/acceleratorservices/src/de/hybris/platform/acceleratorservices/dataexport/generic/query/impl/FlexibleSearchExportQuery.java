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
package de.hybris.platform.acceleratorservices.dataexport.generic.query.impl;

import de.hybris.platform.acceleratorservices.dataexport.generic.event.ExportDataEvent;
import de.hybris.platform.acceleratorservices.dataexport.generic.query.ExportQuery;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.Message;
import org.springframework.integration.handler.ExpressionEvaluatingMessageProcessor;


/**
 * Default implementation of {@link ExportQuery}.
 */
public class FlexibleSearchExportQuery implements ExportQuery, BeanFactoryAware
{
	private static final Logger LOG = Logger.getLogger(FlexibleSearchExportQuery.class);

	private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser(new SpelParserConfiguration(true, true));

	private BeanFactory beanFactory;
	private FlexibleSearchService flexibleSearchService;
	private String query;
	private Map<String, String> parameters;
	private ImpersonationService impersonationService;

	protected BeanFactory getBeanFactory()
	{
		return beanFactory;
	}

	@Override
	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException
	{
		this.beanFactory = beanFactory;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected String getQuery()
	{
		return query;
	}

	@Required
	public void setQuery(final String query)
	{
		this.query = query;
	}

	protected Map<String, String> getParameters()
	{
		return parameters;
	}

	@Required
	public void setParameters(final Map<String, String> parameters)
	{
		this.parameters = parameters;
	}

	public ImpersonationService getImpersonationService()
	{
		return impersonationService;
	}

	@Required
	public void setImpersonationService(final ImpersonationService impersonationService)
	{
		this.impersonationService = impersonationService;
	}

	@Override
	public List<PK> search(final Message<?> message, final ExportDataEvent exportDataEvent) throws Throwable
	{
		final ImpersonationContext impersonationContext = new ImpersonationContext();
		impersonationContext.setCurrency(exportDataEvent.getCurrency());
		impersonationContext.setLanguage(exportDataEvent.getLanguage());
		impersonationContext.setUser(exportDataEvent.getUser());
		impersonationContext.setSite(exportDataEvent.getSite());

		return getImpersonationService().executeInContext(impersonationContext,
				new ImpersonationService.Executor<List<PK>, Throwable>()
				{
					@Override
					public List<PK> execute() throws Exception
					{
						final FlexibleSearchQuery query = new FlexibleSearchQuery(getQuery());
						LOG.debug("Running query: " + query.getQuery());
						query.setResultClassList(Collections.singletonList(PK.class));
						query.addQueryParameters(prepareParameters(message, getParameters()));

						final SearchResult<PK> searchResult = getFlexibleSearchService().search(query);
						LOG.debug("Query executed with " + searchResult.getCount() + " records");
						return searchResult.getResult();
					}
				});
	}

	protected Map<String, Object> prepareParameters(final Message<?> message, final Map<String, String> parameters)
	{
		final Map<String, Object> result = new HashMap<String, Object>();

		for (final Map.Entry<String, String> entry : parameters.entrySet())
		{
			result.put(entry.getKey(), evalExpression(message, entry.getValue(), Object.class));
		}

		return result;
	}

	protected <T> T evalExpression(final Message<?> message, final String expressionString, final Class<T> resultType)
	{
		final Expression expression = EXPRESSION_PARSER.parseExpression(expressionString);
		final ExpressionEvaluatingMessageProcessor<T> targetProcessor = new ExpressionEvaluatingMessageProcessor<T>(expression,
				resultType);
		targetProcessor.setBeanFactory(getBeanFactory());
		return targetProcessor.processMessage(message);
	}
}
