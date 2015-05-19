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
package de.hybris.platform.acceleratorservices.dataimport.batch.util;

import de.hybris.platform.acceleratorservices.util.RegexParser;

import java.io.File;

import org.springframework.util.Assert;


/**
 * Strategy to extract the sequenceId from a file name.
 */
public class SequenceIdParser
{
	private RegexParser parser;

	public Long getSequenceId(final File file)
	{
		Long result = null;
		Assert.notNull(file);
		final String fileName = file.getName();
		final String part = parser.parse(fileName, 1);
		if (part != null)
		{
			result = Long.valueOf(part);
		}
		else
		{
			throw new IllegalArgumentException("missing sequenceId in " + fileName);
		}
		return result;
	}

	/**
	 * @param parser
	 *           the parser to set
	 */
	public void setParser(final RegexParser parser)
	{
		this.parser = parser;
	}

	/**
	 * @return the parser
	 */
	protected RegexParser getParser()
	{
		return parser;
	}
}
