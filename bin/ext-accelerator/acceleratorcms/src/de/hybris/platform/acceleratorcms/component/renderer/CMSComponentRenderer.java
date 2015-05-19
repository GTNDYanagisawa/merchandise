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
package de.hybris.platform.acceleratorcms.component.renderer;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;

/**
 */
public interface CMSComponentRenderer<T extends AbstractCMSComponentModel>
{
	/**
	 * Render a CMS Component into the page at the current location.
	 *
	 * @param pageContext The page context to render into
	 * @param component The component to render
	 * @throws ServletException
	 * @throws IOException
	 */
	void renderComponent(PageContext pageContext, T component) throws ServletException, IOException;
}
