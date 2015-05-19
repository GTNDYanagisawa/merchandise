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

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.WordUtils;

import de.hybris.platform.webservices.util.objectgraphtransformer.NodeConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig;


/**
 * Abstract base implementation for {@link NodeConfig}
 */
public abstract class AbstractNodeConfig implements NodeConfig
{
	// PropertyConfig creation 
	// - uses a global (static) lookup cache (yes, static is intended here)
	// - does a factory bring advantages? 
	// (there were several issues with PropertyDescriptor read/write method detection so maybe that algorithm should be replaceable)
	private static Map<Class, Map<String, PropertyConfig>> propConfigMap = new HashMap<Class, Map<String, PropertyConfig>>();

	protected Map<String, PropertyConfig> getPropertiesFor(final Class<?> type)
	{
		Map<String, PropertyConfig> result = propConfigMap.get(type);
		if (result == null)
		{
			result = createPropertiesFor(type);
			propConfigMap.put(type, result);
		}
		return result;
	}


	private static Pattern BEAN_GETTER = Pattern.compile("get(.*)");
	private static Pattern BEAN_BOOLEAN_GETTER = Pattern.compile("is(.*)");
	private static Pattern BEAN_SETTER = Pattern.compile("set(.*)");

	/**
	 * Creates a lookup map containing all properties of passed type.
	 * <p/>
	 * Result maps a property name to a {@link PropertyConfig}.
	 * </p>
	 * Any property which keeps java bean standard is found and used for {@link PropertyConfig} creation. For finding all
	 * properties {@link Introspector} is used which returns general {@link PropertyDescriptor}. But read- and write
	 * methods provided by {@link PropertyDescriptor} are only used as "suggestion" here and are getting post-processed
	 * to assure following criteria:
	 * <p/>
	 * - no bridge or synthetic methods are allowed <br/>
	 * - co-variant return types are handled correctly <br/>
	 * 
	 * @param type
	 * @return
	 */
	private Map<String, PropertyConfig> createPropertiesFor(Class<?> type)
	{
		final Map<String, PropertyConfig> result = new TreeMap<String, PropertyConfig>();
		final Set<String> done = new HashSet<String>();
		while (type != null)
		{
			// we are only interested in declared methods (no bridge/synthetic ones)
			final Method[] methods = type.getDeclaredMethods();
			for (final Method method : methods)
			{
				// only public, non-bridged methods are of interest
				if (!method.isBridge() && Modifier.isPublic(method.getModifiers()))
				{
					// potential bean-getter property?
					if (method.getParameterTypes().length == 0 && method.getReturnType() != void.class)
					{
						// not processed yet?
						final String methodName = method.getName();
						if (!done.contains(methodName))
						{
							done.add(methodName);

							final Matcher matcher = BEAN_GETTER.matcher(methodName);
							String propertyName = null;
							if (matcher.matches())
							{
								propertyName = matcher.group(1);
							}
							else
							{
								if (method.getReturnType().equals(boolean.class))
								{
									final Matcher matcher2 = BEAN_BOOLEAN_GETTER.matcher(methodName);
									if (matcher2.matches())
									{
										propertyName = matcher2.group(1);
									}
								}
							}

							if (propertyName != null)
							{
								propertyName = normalizePropertyName(propertyName);

								// get or create a PropertyConfig
								DefaultPropertyConfig pCfg = (DefaultPropertyConfig) result.get(propertyName);
								if (pCfg == null)
								{
									pCfg = new DefaultPropertyConfig(propertyName, null, null);
									result.put(propertyName, pCfg);
								}
								pCfg.setReadMethod(method);
							}
						}
					}

					// potential bean-setter property?
					if (method.getParameterTypes().length == 1 && method.getReturnType() == void.class)
					{
						// not processed yet?
						final String methodName = method.getName();
						if (!done.contains(methodName))
						{
							done.add(methodName);
							final Matcher setter = BEAN_SETTER.matcher(methodName);
							if (setter.matches())
							{
								String propertyName = setter.group(1);
								propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);

								// get or create a PropertyConfig
								DefaultPropertyConfig pCfg = (DefaultPropertyConfig) result.get(propertyName);
								if (pCfg == null)
								{
									pCfg = new DefaultPropertyConfig(propertyName, null, null);
									result.put(propertyName, pCfg);
								}
								pCfg.setWriteMethod(method);
							}
						}
					}
				}

			}
			type = type.getSuperclass();
		}
		return result;
	}

	protected String normalizePropertyName(final String propertyName)
	{
		return WordUtils.uncapitalize(propertyName);
	}


}
