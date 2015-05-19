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
package de.hybris.platform.acceleratorcms.component.renderer.impl;

import de.hybris.platform.acceleratorcms.component.renderer.CMSComponentRenderer;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

/**
 */
public class CMSParagraphComponentRenderer implements CMSComponentRenderer<CMSParagraphComponentModel>
{
	@Override
	public void renderComponent(final PageContext pageContext, final CMSParagraphComponentModel component) throws ServletException, IOException
	{
		// <div class="content">${content}</div>
		final JspWriter out = pageContext.getOut();
		out.write("<div class=\"content\">");
		out.write(component.getContent());
		out.write("</div>");
	}
}
