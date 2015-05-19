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
 * A <b>stateless</b> processor for a single node.
 * <p/>
 * Processes each property of current node and includes all operations which are necessary to move each property value from source
 * node to target node.
 */
public interface NodeProcessor
{

	/**
	 * Processes each property from source node and moves them to target node. Parameter 'target' is either a concrete instance or
	 * null which means that a target internally gets created. If a target node must be created, an optional contract can (not
	 * must) have an influence on the target node type.
	 * <p>
	 * Result is always the target node regardless whether it was already passed or newly created.
	 * @param <T>
	 * @param nodeCtx {@link NodeContext}
	 * @param source source node
	 * @param target target node (null permitted)
	 * @return target instance
	 */
	public <T extends Object> T process(final NodeContext nodeCtx, final Object source, T target);



}
