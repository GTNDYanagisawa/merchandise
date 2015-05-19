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
package de.hybris.platform.webservices.util.objectgraphtransformer;



/**
 * For each node type a custom factory can be configured which is taken for node transformation. The factory influences
 * further graph processing in various ways.
 * <ul>
 * <li>When factory returns null, default transformation process takes place (create configured target node)</li>
 * <li>When factory returns not null, returned value is taken as target node on which source graph properties are
 * getting copied into</li>
 * <li>TODO: When factory return not null and calls /here context method call/ no additional source node properties are
 * copied, transformation of current subgraph gets marked finished</li>
 * </ul>
 * 
 * @param <S>
 *           source object which is used to find a target
 * @param <T>
 *           target object which matches the search criteria of source object
 */
public interface NodeFactory<S, T>
{
	/**
	 * Tries to find a target value for a passed source value. Null permitted.
	 * 
	 * @param ctx
	 *           {@link NodeContext}
	 * @param srcValue
	 *           the source node
	 * @return node value or 'null'
	 */
	public T getValue(NodeContext ctx, S srcValue);

}
