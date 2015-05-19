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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.hybris.platform.webservices.util.objectgraphtransformer.impl.DefaultGraphTransformer;


@Retention(RetentionPolicy.RUNTIME)
public @interface GraphNode
{
	/**
	 * Maps this source class to a target class. Target class must have a default constructor. A target instance is
	 * created whenever a source to target transformation is requested but no target parameter is available.
	 * <p>
	 * Example: {@link DefaultGraphTransformer#transform(Object)}
	 * 
	 * @return target class
	 */
	Class target();

	/**
	 * A finder can be used as replacement for a target class. A finder must implement {@link NodeFactory}. Finders are
	 * allowed to return 'null'. In that class configured target class is taken.
	 * 
	 * @return {@link NodeFactory} class
	 */
	Class<? extends NodeFactory> factory() default NodeFactory.class;

	/**
	 * Manually adds additional nodes. Generally nodes are detected automatically but in case nodes are elements of
	 * collection elements they can't be recognized.
	 * 
	 * @return list of additional nodes
	 */
	Class[] addNodes() default void.class;

	/**
	 * The runtime values of these properties (as comma separated list) are taken to calculate a unique id for that node
	 * type. When no such properties are configured, 'uid' would be the objects hashcode. If properties are defined,
	 * 'uid' would be created by combining the ID of each property value.
	 * <p/>
	 * To understand the purpose of why these may be useful some basics must be known: <br/>
	 * Graph processing recognizes infinite graphs by just looking, whether a graph node was already processed. This is
	 * done by maintaining a lookup-map of already processed node instances. Graphs with infinite loops/cycles are
	 * therefore no problem.<br/>
	 * This is basically a very easy solution and depends on node identity.
	 * <p/>
	 * There might be another request, which needs nodes to be recognized as "already processed" based on equality. For
	 * instance when using external frameworks which building up a graph (like JAXB) a graph gets created always with new
	 * instances, no node-instance is reused twice although it might logical be the case.<br/>
	 * In other words: XML structures can form infinite graphs too, but JAXB can't handle that and creates always new
	 * node instances instead of recognizing and sharing already created nodes.
	 * <p/>
	 * This is what can be done here, by just defining a set of properties whose values are taken for equality check.
	 * 
	 * @return uid
	 */
	String uidProperties() default "";
}
