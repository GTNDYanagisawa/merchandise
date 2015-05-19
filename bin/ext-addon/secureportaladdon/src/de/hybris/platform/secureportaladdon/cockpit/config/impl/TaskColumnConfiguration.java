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
package de.hybris.platform.secureportaladdon.cockpit.config.impl;

import de.hybris.platform.cockpit.model.listview.CellRenderer;


public class TaskColumnConfiguration extends de.hybris.platform.cockpit.services.config.impl.TaskColumnConfiguration
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.cockpit.services.config.impl.TaskColumnConfiguration#getCellRenderer()
	 */
	@Override
	public CellRenderer getCellRenderer()
	{
		return new TaskCellRenderer(this);
	}

}
