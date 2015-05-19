/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.b2b.testframework;

import de.hybris.platform.core.Registry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextLoader;
import org.springframework.util.Assert;

public  class SpringCustomContextLoader
	{
		private static final Logger log = Logger.getLogger(SpringCustomContextLoader.class.getName());

		private static final String DEFAULT_CONTEXT_LOADER_CLASS_NAME = "org.springframework.test.context.support.GenericXmlContextLoader";

		ContextLoader contextLoader = null;
		String[] locations = null;
		Class testClazz;

		public SpringCustomContextLoader(final Class testClazz)
		{
			log.info("SpringCustomContextLoader called for class: " + testClazz);
			if (testClazz != null)
			{
				this.testClazz = testClazz;
				final ContextConfiguration contextConfiguration = (ContextConfiguration) this.testClazz
						.getAnnotation(ContextConfiguration.class);

				if (contextConfiguration == null)
				{
					if (log.isInfoEnabled())
					{
						log.info("@ContextConfiguration not found for class [" + testClazz + "]");
					}
				}
				else
				{
					if (log.isTraceEnabled())
					{
						log.trace("Retrieved @ContextConfiguration [" + contextConfiguration + "] for class [" + testClazz + "]");
					}

					Class<? extends ContextLoader> contextLoaderClass = contextConfiguration.loader();
					if (ContextLoader.class.equals(contextLoaderClass))
					{
						try
						{
							contextLoaderClass = (Class<? extends ContextLoader>) getClass().getClassLoader().loadClass(
									DEFAULT_CONTEXT_LOADER_CLASS_NAME);
						}
						catch (final ClassNotFoundException ex)
						{
							throw new IllegalStateException("Could not load default ContextLoader class ["
									+ DEFAULT_CONTEXT_LOADER_CLASS_NAME + "]. Specify @ContextConfiguration's 'loader' "
									+ "attribute or make the default loader class available.");
						}
					}
					contextLoader = BeanUtils.instantiateClass(contextLoaderClass);
					locations = retrieveContextLocations(contextLoader, testClazz);
				}
			}
		}

		public void loadApplicationContexts(final GenericApplicationContext globalCtx) throws Exception
		{
			if (locations == null)
			{
				return;
			}
			for (final String location : locations)
			{
				try
				{
					final File file = new File(location);
					if (file.isFile() && file.exists())
					{
						log.info("Loading Spring config from (" + location + ")");
						final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(globalCtx);
						xmlReader.loadBeanDefinitions(new FileSystemResource(location));
					}
					else
					{
						String resourceLocation = location;
						if (resourceLocation.charAt(0) != '/')
						{
							resourceLocation = "/" + resourceLocation;
						}
						if (Registry.class.getResource(resourceLocation) != null)
						{
							final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(globalCtx);
							xmlReader.loadBeanDefinitions(new ClassPathResource(resourceLocation, Registry.class.getClassLoader()));
						}
						else
						{
							log.info("Loading Spring config from (" + resourceLocation + ")");
							final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(globalCtx);
							xmlReader.loadBeanDefinitions(location);
						}
					}
				}
				catch (final BeanDefinitionStoreException e)
				{
					log.error("Error while loading application context file " + location, e);
				}
			}
		}

		/**
		 * Retrieve {@link org.springframework.context.ApplicationContext} resource locations for the supplied
		 * {@link Class class}, using the supplied {@link org.springframework.test.context.ContextLoader} to
		 * {@link org.springframework.test.context.ContextLoader#processLocations(Class, String...) process} the
		 * locations.
		 * <p>
		 * Note that the {@link org.springframework.test.context.ContextConfiguration#inheritLocations() inheritLocations}
		 * flag of {@link org.springframework.test.context.ContextConfiguration @ContextConfiguration} will be taken into
		 * consideration. Specifically, if the <code>inheritLocations</code> flag is set to <code>true</code>, locations
		 * defined in the annotated class will be appended to the locations defined in superclasses.
		 *
		 * @param contextLoader
		 *           the ContextLoader to use for processing the locations (must not be <code>null</code>)
		 * @param clazz
		 *           the class for which to retrieve the resource locations (must not be <code>null</code>)
		 * @return the list of ApplicationContext resource locations for the specified class, including locations from
		 *         superclasses if appropriate
		 * @throws IllegalArgumentException
		 *            if {@link org.springframework.test.context.ContextConfiguration @ContextConfiguration} is not
		 *            <em>present</em> on the supplied class
		 */
		protected String[] retrieveContextLocations(final ContextLoader contextLoader, final Class<?> clazz)
		{
			Assert.notNull(contextLoader, "ContextLoader must not be null");
			Assert.notNull(clazz, "Class must not be null");

			final List<String> locationsList = new ArrayList<String>();
			final Class<ContextConfiguration> annotationType = ContextConfiguration.class;
			Class<?> declaringClass = AnnotationUtils.findAnnotationDeclaringClass(annotationType, clazz);
			Assert.notNull(declaringClass, "Could not find an 'annotation declaring class' for annotation type [" + annotationType
					+ "] and class [" + clazz + "]");

			while (declaringClass != null)
			{
				final ContextConfiguration contextConfiguration = declaringClass.getAnnotation(annotationType);
				if (log.isTraceEnabled())
				{
					log.trace("Retrieved @ContextConfiguration [" + contextConfiguration + "] for declaring class [" + declaringClass
							+ "]");
				}
				final String[] locations = contextLoader.processLocations(declaringClass, contextConfiguration.locations());
				locationsList.addAll(0, Arrays.<String> asList(locations));
				declaringClass = contextConfiguration.inheritLocations() ? AnnotationUtils.findAnnotationDeclaringClass(
						annotationType, declaringClass.getSuperclass()) : null;
			}

			return locationsList.toArray(new String[locationsList.size()]);
		}
	}

