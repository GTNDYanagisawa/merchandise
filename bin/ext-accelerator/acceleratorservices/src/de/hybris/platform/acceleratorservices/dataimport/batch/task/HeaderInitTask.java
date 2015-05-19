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
package de.hybris.platform.acceleratorservices.dataimport.batch.task;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.HeaderTask;
import de.hybris.platform.acceleratorservices.dataimport.batch.util.SequenceIdParser;
import de.hybris.platform.acceleratorservices.util.RegexParser;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Task to initialize the batch header with the sequence id and the language.
 */
public class HeaderInitTask implements HeaderTask
{
	private SequenceIdParser sequenceIdParser;
	private RegexParser languageParser;
	private String fallbackLanguage;

	@Override
	public BatchHeader execute(final BatchHeader header)
	{
		header.setSequenceId(sequenceIdParser.getSequenceId(header.getFile()));
		final String language = languageParser.parse(header.getFile().getName(), 1);
		header.setLanguage(language == null ? fallbackLanguage : language);
		return header;
	}

	/**
	 * @param sequenceIdParser
	 *           the sequenceIdParser to set
	 */
	@Required
	public void setSequenceIdParser(final SequenceIdParser sequenceIdParser)
	{
		this.sequenceIdParser = sequenceIdParser;
	}

	/**
	 * @param fallbackLanguage
	 *           the fallbackLanguage to set
	 */
	@Required
	public void setFallbackLanguage(final String fallbackLanguage)
	{
		Assert.hasText(fallbackLanguage);
		this.fallbackLanguage = fallbackLanguage;
	}

	/**
	 * @param languageParser
	 *           the languageParser to set
	 */
	@Required
	public void setLanguageParser(final RegexParser languageParser)
	{
		this.languageParser = languageParser;
	}

	/**
	 * @return the sequenceIdParser
	 */
	protected SequenceIdParser getSequenceIdParser()
	{
		return sequenceIdParser;
	}

	/**
	 * @return the languageParser
	 */
	protected RegexParser getLanguageParser()
	{
		return languageParser;
	}

	/**
	 * @return the fallbackLanguage
	 */
	protected String getFallbackLanguage()
	{
		return fallbackLanguage;
	}
}
