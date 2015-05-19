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
package de.hybris.platform.webservices.util.objectgraphtransformer.basic;

import org.apache.log4j.Logger;

import de.hybris.platform.webservices.objectgraphtransformer.ModifiedProperties;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyFilter;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyMapping;


public class ModifiedPropertyFilter implements PropertyFilter
{
	private static final Logger log = Logger.getLogger(ModifiedPropertyFilter.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.webservices.util.objectgraphtransformer.proto.PropertyFilter#isFiltered(de.hybris.platform.
	 * webservices .objectgraphtransformer.ObjectGraphContext, java.lang.Object,
	 * de.hybris.platform.webservices.util.objectgraphtransformer.proto.PropertyProcessor)
	 */
	@Override
	public boolean isFiltered(final PropertyContext ctx, final Object value)
	{
		boolean isFiltered = false;
		final Object node = ctx.getParentContext().getSourceNodeValue();
		if (node instanceof ModifiedProperties)
		{
			final PropertyMapping prop = ctx.getPropertyMapping();
			isFiltered = !((ModifiedProperties) node).getModifiedProperties().contains(prop.getId());
			if (log.isDebugEnabled())
			{
				log.debug("'" + prop.getId() + "' matched filter:" + isFiltered);
			}
		}
		return isFiltered;
	}


}
