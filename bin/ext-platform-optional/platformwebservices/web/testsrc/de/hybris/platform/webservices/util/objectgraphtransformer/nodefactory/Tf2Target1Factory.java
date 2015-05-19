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
package de.hybris.platform.webservices.util.objectgraphtransformer.nodefactory;

import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeFactory;


public class Tf2Target1Factory implements NodeFactory<Tf2Source1, Tf2Target1>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.util.objectgraph.ObjTreeNodeFinder#createTarget(de.hybris.platform.util.objectgraph.ObjTreeContext
	 * , java.lang.Object)
	 */
	@Override
	public Tf2Target1 getValue(final NodeContext ctx, final Tf2Source1 dto)
	{
		// id lesser than 100: an instance gets created with id passed
		// any other id: null is returned
		final String id = dto.getId();
		final Tf2Target1 result = (Integer.parseInt(id) < 100) ? new Tf2Target1(id) : null;
		return result;
	}

	//	/*
	//	 * (non-Javadoc)
	//	 * 
	//	 * @see de.hybris.platform.util.objectgraph.SingletonResult#getKey(java.lang.Object)
	//	 */
	//	@Override
	//	public String getValueId(final Tf2Source1 dto)
	//	{
	//		return dto.getId();
	//	}



}
