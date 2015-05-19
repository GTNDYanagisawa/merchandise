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


public class TfTarget2Factory implements NodeFactory<TfSource2, TfTarget2>
{
	public static TfTarget2 STATIC_TARGETDTO2 = new TfTarget2();

	@Override
	public TfTarget2 getValue(final NodeContext ctx, final TfSource2 dto)
	{
		TfTarget2 result = null;
		if (dto.getValue() > 0)
		{
			result = STATIC_TARGETDTO2;
		}
		return result;
	}

	//	/*
	//	 * (non-Javadoc)
	//	 * 
	//	 * @see de.hybris.platform.util.objectgraph.ObjTreeNodeFinder#getValueId(java.lang.Object)
	//	 */
	//	@Override
	//	public String getValueId(final TfSource2 source)
	//	{
	//		return null;
	//	}

}
