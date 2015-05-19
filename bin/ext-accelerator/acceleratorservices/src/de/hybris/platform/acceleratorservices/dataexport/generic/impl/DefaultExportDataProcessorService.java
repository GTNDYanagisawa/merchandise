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
package de.hybris.platform.acceleratorservices.dataexport.generic.impl;

import de.hybris.platform.acceleratorservices.dataexport.generic.ExportDataProcessorService;
import de.hybris.platform.acceleratorservices.dataexport.generic.config.PipelineConfig;
import de.hybris.platform.acceleratorservices.dataexport.generic.event.ExportDataEvent;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

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
import org.springframework.integration.annotation.Header;
import org.springframework.integration.handler.ExpressionEvaluatingMessageProcessor;


/**
 * Default implementation of {@link ExportDataProcessorService}.
 */
public class DefaultExportDataProcessorService implements ExportDataProcessorService, BeanFactoryAware
{
	private static final Logger LOG = Logger.getLogger(DefaultExportDataProcessorService.class);

	private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser(new SpelParserConfiguration(true, true));

	private BeanFactory beanFactory;
	private ModelService modelService;

	protected BeanFactory getBeanFactory()
	{
		return beanFactory;
	}

	@Override
	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException
	{
		this.beanFactory = beanFactory;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


	@Override
	public Object lookupBean(final String beanName)
	{
		return getBeanFactory().getBean(beanName);
	}

	@Override
	public String computeFilename(final Message<?> message, @Header(value = "pipelineConfig") final PipelineConfig pipelineConfig)
	{
		return evalExpression(message, pipelineConfig.getFilename(), String.class);
	}

	@Override
	public List<PK> search(final Message<ExportDataEvent> message,
			@Header(value = "pipelineConfig") final PipelineConfig pipelineConfig) throws Throwable
	{
		return pipelineConfig.getQuery().search(message, message.getPayload());
	}

	@Override
	public ItemModel lookupItemForPk(final PK pk)
	{
		return getModelService().get(pk);
	}

	@Override
	public Object convertItem(final Message<? extends ItemModel> message,
			@Header(value = "pipelineConfig") final PipelineConfig pipelineConfig)
	{
		LOG.debug("Converting type: " + message.getPayload().getClass().getName());
		final Converter<Message<? extends ItemModel>, Object> converter = pipelineConfig.getItemConverter();
		return converter.convert(message);
	}

	@Override
	public Object convertOutput(final List<Object> records, @Header(value = "pipelineConfig") final PipelineConfig pipelineConfig)
	{
		LOG.debug("Converting output");
		final Converter<List<Object>, String> converter = pipelineConfig.getOutputConverter();
		return converter.convert(records);
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
