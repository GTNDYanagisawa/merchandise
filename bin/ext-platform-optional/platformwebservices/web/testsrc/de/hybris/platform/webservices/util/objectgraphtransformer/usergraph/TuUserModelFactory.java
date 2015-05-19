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
package de.hybris.platform.webservices.util.objectgraphtransformer.usergraph;

import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeFactory;


public class TuUserModelFactory implements NodeFactory<TuUserDTO, TuUserModel>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.util.objectgraph.ObjTreeNodeFinder#createTarget(de.hybris.platform.util.objectgraph.ObjTreeContext
	 * , java.lang.Object)
	 */
	@Override
	public TuUserModel getValue(final NodeContext ctx, final TuUserDTO dto)
	{
		return null;
	}


}
