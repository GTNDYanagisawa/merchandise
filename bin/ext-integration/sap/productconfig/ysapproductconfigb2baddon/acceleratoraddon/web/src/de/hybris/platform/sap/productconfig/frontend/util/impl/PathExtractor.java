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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PathExtractor
{
	private static final Pattern patternGroupCstic = Pattern
			.compile("^groups\\[(\\d+)\\](?:\\.subGroups\\[\\d+\\])*\\.cstics\\[(\\d+)\\](?:\\.value)?");
	private static final Pattern patternSubGroup = Pattern.compile("\\.subGroups\\[(\\d+)\\]");

	private Integer groupIndex = Integer.valueOf(-1);
	private Integer csticsIndex = Integer.valueOf(-1);
	private final List<Integer> subGroupIndices;

	public PathExtractor(final String fieldPath)
	{
		Matcher matcher = patternGroupCstic.matcher(fieldPath);
		if (matcher.find())
		{
			groupIndex = Integer.valueOf(matcher.group(1));
			csticsIndex = Integer.valueOf(matcher.group(2));
		}
		matcher = patternSubGroup.matcher(fieldPath);
		subGroupIndices = new ArrayList<Integer>();
		while (matcher.find())
		{
			final Integer subGroupIndex = Integer.valueOf(matcher.group(1));
			subGroupIndices.add(subGroupIndex);
		}
	}

	/**
	 * @return the groupIndex
	 */
	public int getGroupIndex()
	{
		return groupIndex.intValue();
	}


	/**
	 * @return the csticsIndex
	 */
	public int getCsticsIndex()
	{
		return csticsIndex.intValue();
	}

	/**
	 * @return the size of subGroupIndices
	 */
	public int getSubGroupCount()
	{
		return subGroupIndices.size();
	}


	/**
	 * @return the subGroupIndex
	 */
	public int getSubGroupIndex(final int subGroupNumber)
	{
		return subGroupIndices.get(subGroupNumber).intValue();
	}
}
