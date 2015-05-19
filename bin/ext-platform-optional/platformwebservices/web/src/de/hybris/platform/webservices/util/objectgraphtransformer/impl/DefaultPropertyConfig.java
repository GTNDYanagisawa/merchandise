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
package de.hybris.platform.webservices.util.objectgraphtransformer.impl;

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphException;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProperty;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphPropertyInterceptor;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyInterceptor;

import java.lang.reflect.Method;


/**
 * Default {@link PropertyConfig} implementation.
 */
public class DefaultPropertyConfig implements PropertyConfig
{
	// this is the name of the method defined by PropertyInterceptor interface
	private static final String INTERCEPT_METHOD_NAME;
	static
	{
		// should result in "intercept" (fetch method name in a refactoring safe manner)
		INTERCEPT_METHOD_NAME = PropertyInterceptor.class.getDeclaredMethods()[0].getName();
	}

	private String name = null;
	private Method readMethod = null;
	private Method writeMethod = null;
	private PropertyInterceptor<?, ?> readInterceptor = null;
	private PropertyInterceptor<?, ?> writeInterceptor = null;
	private Class<?> readType = null;
	private Class<?> writeType = null;

	private boolean readTypeCheckEnabled = true;
	private boolean writeTypeCheckEnabled = true;

	public DefaultPropertyConfig(final String id, final Method read, final Method write)
	{
		this.name = id;

		if (read != null)
		{
			setReadMethod(read);
		}
		if (write != null)
		{
			setWriteMethod(write);
		}
	}

	protected DefaultPropertyConfig(final DefaultPropertyConfig config)
	{
		this.name = config.name;
		this.readMethod = config.readMethod;
		this.writeMethod = config.writeMethod;
		this.readType = config.readType;
		this.writeType = config.writeType;
		this.readInterceptor = config.readInterceptor;
		this.writeInterceptor = config.writeInterceptor;
		this.readTypeCheckEnabled = config.readTypeCheckEnabled;
		this.writeTypeCheckEnabled = config.writeTypeCheckEnabled;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	@Override
	public Method getReadMethod()
	{
		return readMethod;
	}

	protected void setReadMethod(final Method readMethod)
	{
		this.readMethod = readMethod;

		if (this.readMethod != null)
		{
			if (this.readInterceptor == null)
			{
				this.readType = this.readMethod.getReturnType();
			}

			this.evaluateReadMethodAnnotation();
		}
	}

	@Override
	public Method getWriteMethod()
	{
		return writeMethod;
	}

	protected void setWriteMethod(final Method writeMethod)
	{
		this.writeMethod = writeMethod;
		if (writeMethod != null)
		{
			if (this.writeInterceptor == null)
			{
				this.writeType = this.writeMethod.getParameterTypes()[0];
			}
			this.evaluateWriteMethodAnnotation();

		}

	}

	@Override
	public PropertyInterceptor getReadInterceptor()
	{
		return readInterceptor;
	}

	/**
	 * Sets a {@link PropertyInterceptor} which gets assigned to read-method of this property.
	 * 
	 * @param interceptor
	 *           the interceptor to set
	 */
	public void setReadInterceptor(final PropertyInterceptor interceptor)
	{
		this.readInterceptor = interceptor;
		if (this.readInterceptor != null)
		{
			// TODO: interceptor parameter check against read-method return type
			this.readType = this.getInterceptMethod(readInterceptor).getReturnType();
		}
		else
		{
			this.readType = this.readMethod != null ? this.readMethod.getReturnType() : null;
		}
	}


	@Override
	public PropertyInterceptor getWriteInterceptor()
	{
		return this.writeInterceptor;
	}

	/**
	 * Sets a {@link PropertyInterceptor} which gets assigned to write-method of this property.
	 * 
	 * @param interceptor
	 */
	public void setWriteInterceptor(final PropertyInterceptor interceptor)
	{
		this.writeInterceptor = interceptor;
		if (this.writeInterceptor != null)
		{
			this.writeType = this.getInterceptMethod(writeInterceptor).getParameterTypes()[1];
		}
		else
		{
			this.writeType = this.writeMethod != null ? this.writeMethod.getParameterTypes()[0] : null;
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig#isTypeCheckEnabled()
	 */
	@Override
	public boolean isReadTypeCheckEnabled()
	{
		return this.readTypeCheckEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig#isWriteTypeCheckEnabled()
	 */
	@Override
	public boolean isWriteTypeCheckEnabled()
	{
		return this.writeTypeCheckEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig#getReadType()
	 */
	@Override
	public Class getReadType()
	{
		return this.readType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig#getWriteType()
	 */
	@Override
	public Class getWriteType()
	{
		return this.writeType;
	}


	private void evaluateReadMethodAnnotation()
	{
		final GraphProperty readAnno = readMethod.isAnnotationPresent(GraphProperty.class) ? readMethod
				.getAnnotation(GraphProperty.class) : null;

		if (readAnno != null)
		{
			this.readTypeCheckEnabled = readAnno.typecheck();

			if (this.readInterceptor == null)
			{
				final Class interceptorClass = readAnno.interceptor();
				final PropertyInterceptor interceptor = this.createInterceptor(interceptorClass);
				this.setReadInterceptor(interceptor);

				if (interceptorClass.isAnnotationPresent(GraphPropertyInterceptor.class))
				{
					final GraphPropertyInterceptor interceptorAnno = (GraphPropertyInterceptor) interceptorClass
							.getAnnotation(GraphPropertyInterceptor.class);
					this.readTypeCheckEnabled = interceptorAnno.typecheck();
				}
			}
		}
	}

	private void evaluateWriteMethodAnnotation()
	{
		final GraphProperty writeAnno = writeMethod.isAnnotationPresent(GraphProperty.class) ? writeMethod
				.getAnnotation(GraphProperty.class) : null;

		if (writeAnno != null)
		{
			this.writeTypeCheckEnabled = writeAnno.typecheck();
			if (writeInterceptor == null)
			{
				final Class interceptorClass = writeAnno.interceptor();
				final PropertyInterceptor interceptor = this.createInterceptor(interceptorClass);
				this.setWriteInterceptor(interceptor);

				if (interceptorClass.isAnnotationPresent(GraphPropertyInterceptor.class))
				{
					final GraphPropertyInterceptor interceptorAnno = (GraphPropertyInterceptor) interceptorClass
							.getAnnotation(GraphPropertyInterceptor.class);
					this.writeTypeCheckEnabled = interceptorAnno.typecheck();
				}
			}
		}

	}

	private PropertyInterceptor createInterceptor(final Class interceptor)
	{
		PropertyInterceptor result = null;
		if (interceptor != PropertyInterceptor.class)
		{
			try
			{
				result = (PropertyInterceptor) interceptor.newInstance();
			}
			catch (final Exception e)
			{
				throw new GraphException("Error creating " + PropertyInterceptor.class.getSimpleName() + " for property", e);
			}
		}
		return result;
	}

	/**
	 * Find the declared (non-bridged) 'intercept' method.
	 * 
	 * @param interceptor
	 * @return
	 */
	protected Method getInterceptMethod(final PropertyInterceptor interceptor)
	{
		Method result = null;
		final Method[] declaredMethods = interceptor.getClass().getDeclaredMethods();
		for (final Method m : declaredMethods)
		{
			// synthetic (compiler-generated), bridge (compiler-generated to support generic interfaces).
			if (m.getName().equals(INTERCEPT_METHOD_NAME) && !m.isBridge())
			{
				result = m;
				break;
			}
		}
		return result;
	}


	protected void mergeWith(final PropertyConfig pCfg)
	{
		if (pCfg != null)
		{
			if (this.readMethod == null)
			{
				this.setReadMethod(pCfg.getReadMethod());
			}

			if (this.writeMethod == null)
			{
				this.setWriteMethod(pCfg.getWriteMethod());
			}

			if (this.readInterceptor == null)
			{
				this.setReadInterceptor(pCfg.getReadInterceptor());
			}

			if (this.writeInterceptor == null)
			{
				this.setWriteInterceptor(pCfg.getWriteInterceptor());
			}
		}
	}

	@Override
	public String toString()
	{
		return this.getName();
	}
}
