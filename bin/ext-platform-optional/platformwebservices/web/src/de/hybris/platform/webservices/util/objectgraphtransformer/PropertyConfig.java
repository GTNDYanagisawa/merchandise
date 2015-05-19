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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;


/**
 * Configuration information for a GraphNode property.
 * <p/>
 * Note: This is similar to a {@link PropertyDescriptor} but whereas a {@link PropertyDescriptor} follows strongly java
 * bean standard a {@link PropertyConfig} this implementation allows various "incompatible" settings and additionally
 * provides {@link GraphTransformer} specific information like available {@link PropertyInterceptor}.
 */
public interface PropertyConfig
{
	/**
	 * Returns the name of this property.
	 * 
	 * @return property name
	 */
	public String getName();

	/**
	 * Returns the read method for that property.
	 * 
	 * @return read method
	 */
	public Method getReadMethod();

	/**
	 * Returns the write method for that property.
	 * 
	 * @return write method
	 */
	public Method getWriteMethod();

	/**
	 * Returns a {@link PropertyInterceptor} which gets applied for the read value of this property.
	 * 
	 * @return {@link PropertyInterceptor}
	 */
	public PropertyInterceptor<Object, Object> getReadInterceptor();

	/**
	 * Returns a {@link PropertyInterceptor} which gets applied for the write value of this property.
	 * 
	 * @return {@link PropertyInterceptor}
	 */
	public PropertyInterceptor<Object, Object> getWriteInterceptor();

	/**
	 * Returns the read-value type.
	 * <p/>
	 * Type is either return type of read-method or if an interceptor is assigned the return type of
	 * {@link PropertyInterceptor#intercept(PropertyContext, Object)}
	 * 
	 * @return read-value type
	 */
	public Class<?> getReadType();

	/**
	 * Returns the write-value type
	 * <p/>
	 * Type is either return type of write-method or if an interceptor is assigned the return type of
	 * {@link PropertyInterceptor#intercept(PropertyContext, Object)}
	 * 
	 * @return write-value type
	 */
	public Class<?> getWriteType();


	/**
	 * True (default) when enabling type check which assures during build-time of graph, that this read-type can be
	 * assigned to a target property write type (not this property write type)
	 * <p/>
	 * Example: read-type: NUMBER; target write-type INTEGER:<br/>
	 * With enabled type check (default) this property will skipped and not processed during graph processing.
	 * 
	 * @return true (default) for enabled type check
	 */
	public boolean isReadTypeCheckEnabled();

	/**
	 * True (default) when checking whether write-type is assignable from a source read-type.
	 * 
	 * @return true (default) for enabled type check
	 */
	public boolean isWriteTypeCheckEnabled();


}
