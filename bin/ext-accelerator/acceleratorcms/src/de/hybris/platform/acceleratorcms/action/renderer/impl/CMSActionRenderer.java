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
package de.hybris.platform.acceleratorcms.action.renderer.impl;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;

/**
 */
public interface CMSActionRenderer<T extends AbstractCMSComponentModel>
{
	/**
	 * Render a CMS Action into a action
	 *
	 * @param pageContext The page context to render into
	 * @param action The action to render
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 */
	void renderAction(PageContext pageContext, T action) throws ServletException, IOException;
}
