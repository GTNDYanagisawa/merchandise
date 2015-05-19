/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.cms2lib.cmstags;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.junit.Test;
import org.springframework.mock.web.MockJspWriter;


public class CMSBodyTagTest
{

	/**
	 * Test method for {@link de.hybris.platform.cms2lib.cmstags.CMSBodyTag#doStartTag()}.
	 * 
	 */
	@Test
	public void testDoStartTag() throws IOException, JspException, ServletException
	{

		final PageContext pageContext = mock(PageContext.class);

		final ServletRequest request = mock(ServletRequest.class);

		when(pageContext.getRequest()).thenReturn(request);

		when(request.getAttribute("currentPage")).thenReturn(null);

		final StringWriter writerOutput = new StringWriter();

		final JspWriter writer = new MockJspWriter(writerOutput);

		when(pageContext.getOut()).thenReturn(writer);

		final CMSBodyTag tag = new MyCMSBodyTag();

		tag.setPageContext(pageContext);

		tag.doStartTag();

		final String htmlPart = writerOutput.toString();

		final Pattern pattern = Pattern.compile(
				"<body[^>]*>[\\s\\w\\p{Punct}]*<script[^>]*>[\\s\\w\\p{Punct}]*</script>[\\s\\w\\p{Punct}]*",
				Pattern.CASE_INSENSITIVE);
		final Matcher matcher = pattern.matcher(htmlPart);
		assertTrue("To satisfy XHTML validation rules <script> tag must be included in <body> (must follow <body>) but got: \n"
				+ htmlPart, matcher.matches());

	}

	private class MyCMSBodyTag extends CMSBodyTag
	{
		@Override
		protected boolean isPreviewDataModelValid()
		{
			return true;
		}

		@Override
		protected boolean isLiveEdit()
		{
			return true;
		}

		@Override
		protected String getPreviewTicketId(final ServletRequest httpRequest)
		{
			return "testTicketId" + System.currentTimeMillis();
		}
	}
}
