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
package de.hybris.platform.acceleratorservices.dataimport.batch;

import java.io.File;
import java.util.Comparator;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;


/**
 * Comparator that compares files based on their file names. On this behalf a priorities are configurable for file name
 * prefixes. Files with a lower priority value are considered more important and imported first. This ensures some files
 * are imported before others (e.g. products before prices).
 */
public class FileOrderComparator implements Comparator<File>, InitializingBean
{
	private static final Integer DEFAULT_PRIORITY = NumberUtils.INTEGER_ZERO;
	private Map<String, Integer> prefixPriority;


	@Override
	public void afterPropertiesSet()
	{
		Assert.notEmpty(prefixPriority);
	}

	@Override
	public int compare(final File file, final File otherFile)
	{
		// invert priority setting so files with higher priority go first
		int result = getPriority(otherFile).compareTo(getPriority(file));
		if (result == 0)
		{
			result = Long.valueOf(file.lastModified()).compareTo(Long.valueOf(otherFile.lastModified()));
		}
		return result;
	}

	/**
	 * Retrieves the priority for a file.
	 * 
	 * @param file
	 *           the file to get priority from
	 * @return the configured priority, if one exists, otherwise the default priority
	 */
	protected Integer getPriority(final File file)
	{
		for (final Map.Entry<String, Integer> prefix : prefixPriority.entrySet())
		{
			if (file.getName().startsWith(prefix.getKey()))
			{
				return prefix.getValue();
			}
		}
		return DEFAULT_PRIORITY;
	}

	/**
	 * @param prefixPriority
	 *           the prefixPriority to set
	 */
	public void setPrefixPriority(final Map<String, Integer> prefixPriority)
	{
		Assert.notEmpty(prefixPriority);
		this.prefixPriority = prefixPriority;
	}

	/**
	 * 
	 * @return prefixPriority
	 */
	protected Map<String, Integer> getPrefixPriority()
	{
		return prefixPriority;
	}

}
