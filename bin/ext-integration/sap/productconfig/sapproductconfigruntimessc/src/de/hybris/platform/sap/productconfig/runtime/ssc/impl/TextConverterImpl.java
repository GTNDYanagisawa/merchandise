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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.ssc.TextConverter;



public class TextConverterImpl implements TextConverter
{


	public final static String SECTION_DESCRIPTION = "&DESCRIPTION&";
	public final static String SECTION_EXPLAINATION = "&EXPLANATION&";

	@Override
	public String convertLongText(final String formattedText)
	{
		final String deEscapedText = deEscapeString(formattedText);
		final String sectionText = extractSection(deEscapedText);
		final String deFormattedText = removeFormatting(sectionText);
		final String removeMarkupText = removeMarkup(deFormattedText);
		return removeMarkupText;
	}

	protected String removeFormatting(final String formattedText)
	{
		String unformattedText = formattedText.replaceAll("\\<\\w+\\>", "");
		unformattedText = unformattedText.replaceAll("\\<\\/\\w*\\>", "");
		return unformattedText;
	}

	protected String removeMarkup(final String formattedText)
	{
		final String unformattedText = formattedText.replaceAll("\\<.*?\\>", "");

		return unformattedText;
	}

	protected String extractSection(final String textWithSections)
	{

		int startIdx = textWithSections.indexOf(SECTION_DESCRIPTION);
		String sectionText;
		if (startIdx != -1)
		{
			startIdx += SECTION_DESCRIPTION.length();
			int endIdx = textWithSections.indexOf(SECTION_EXPLAINATION, startIdx);
			if (endIdx == -1)
			{
				endIdx = textWithSections.length();
			}
			sectionText = textWithSections.substring(startIdx, endIdx);
		}
		else
		{
			sectionText = textWithSections;
		}
		return sectionText;
	}

	protected String deEscapeString(final String escapedString)
	{
		final String descapedString = escapedString.replaceAll("\\<\\)\\>", "");
		return descapedString.replaceAll("\\<\\(\\>", "");
	}

}
