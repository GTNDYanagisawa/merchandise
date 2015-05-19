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
package de.hybris.platform.acceleratorcms.component.container;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;

import java.util.List;

/**
 */
public interface CMSComponentContainerStrategy
{
	/**
	 * Get the list of components that should be displayed in place of the container.
	 * For some use cases it is necessary for the CMS Container to be replaced at render time with a
	 * list of child components.
	 * If the CMS Container itself should be rendered then it should return a list containing itself,
	 * see the {@link de.hybris.platform.acceleratorcms.component.container.impl.IdentityCMSComponentContainerStrategy}.
	 *
	 * @param container The container
	 * @return The list of components to display in place of the container
	 */
	List<AbstractCMSComponentModel> getDisplayComponentsForContainer(AbstractCMSComponentContainerModel container);
}
