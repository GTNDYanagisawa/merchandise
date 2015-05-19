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
package de.hybris.platform.webservices.util.objectgraphtransformer.misc;

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProperty;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyInterceptor;


@GraphNode(target = GraphPropTarget.class)
public class GraphPropSource
{
	public static class IntToStringConverter implements PropertyInterceptor<Object, Object>
	{
		@Override
		public String intercept(final PropertyContext ctx, final Object source)
		{
			return source != null ? ((Integer) source).toString() : null;
		}
	}

	public static class StringToIntConverter implements PropertyInterceptor<String, Integer>
	{
		@Override
		public Integer intercept(final PropertyContext ctx, final String source)
		{
			return source != null ? Integer.valueOf(source) : null;
		}
	}

	public static class NumberToStringConverter implements PropertyInterceptor<Object, Object>
	{
		@Override
		public String intercept(final PropertyContext ctx, final Object source)
		{
			return source != null ? ((Number) source).toString() : null;
		}
	}

	public static class StringToNumberConverter implements PropertyInterceptor<String, Number>
	{
		@Override
		public Number intercept(final PropertyContext ctx, final String source)
		{
			return source != null ? Integer.valueOf(source) : null;
		}
	}

	// Integer -> conv:String -> String
	private Integer value1;
	// String -> conv:Number -> Integer
	private String value2;
	// Number -> Integer
	private Number value3;
	// Double -> Integer
	private Double value4;

	public GraphPropSource()
	{
		super();
	}

	public GraphPropSource(final Integer value)
	{
		this.value1 = value;
	}

	/**
	 * @return the value
	 */
	@GraphProperty(interceptor = IntToStringConverter.class)
	public Integer getValue1()
	{
		return value1;
	}

	/**
	 * @param value
	 *           the value to set
	 */
	@GraphProperty(interceptor = StringToIntConverter.class)
	public void setValue1(final Integer value)
	{
		this.value1 = value;
	}




	/**
	 * @return the value2
	 */
	@GraphProperty(interceptor = StringToNumberConverter.class, typecheck = false)
	public String getValue2()
	{
		return value2;
	}

	/**
	 * @param value2
	 *           the value2 to set
	 */
	@GraphProperty(interceptor = NumberToStringConverter.class)
	public void setValue2(final String value2)
	{
		this.value2 = value2;
	}

	/**
	 * @return the value3
	 */
	@GraphProperty(typecheck = false)
	public Number getValue3()
	{
		return value3;
	}

	/**
	 * @param value3
	 *           the value3 to set
	 */
	public void setValue3(final Number value3)
	{
		this.value3 = value3;
	}

	/**
	 * @return the value4
	 */
	@GraphProperty(typecheck = false)
	public Double getValue4()
	{
		return value4;
	}

	/**
	 * @param value4
	 *           the value4 to set
	 */
	public void setValue4(final Double value4)
	{
		this.value4 = value4;
	}


}
