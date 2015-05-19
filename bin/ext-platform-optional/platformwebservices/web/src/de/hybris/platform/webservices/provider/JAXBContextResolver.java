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
package de.hybris.platform.webservices.provider;

import java.util.Collection;
import java.util.HashSet;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;


/**
 * Provides all necessary dto classes to the JAXBContext.
 */
@Provider
public final class JAXBContextResolver implements ContextResolver<JAXBContext>
{
	private static final Logger LOG = Logger.getLogger(JAXBContextResolver.class);

	private JAXBContext context;
	private final Collection<Class> allDtoClasses = new HashSet<Class>();
	private boolean creationJAXBContextRequired = true;

	/**
	 * Method returns new custom JAXBContext based on our DTOs classes or if creation of before mentioned context failed,
	 * then method will return 'null' and default JAXBContext will be used.
	 */
	@Override
	public JAXBContext getContext(final Class<?> aClass)
	{
		if (creationJAXBContextRequired)
		{
			createJAXBContext(allDtoClasses);
			this.creationJAXBContextRequired = false;
			allDtoClasses.clear();
		}
		return context;
	}

	public void setDtoClassContainer(final DtoClassContainer dtoClassContainer)
	{
		allDtoClasses.addAll(dtoClassContainer.getSingleDtoNodes());
		allDtoClasses.addAll(dtoClassContainer.getCollectionDtoNodes());
	}

	public void setDtosNotFromTypeSystem(final Collection<Class> dtosNotFromTypeSystem)
	{
		allDtoClasses.addAll(dtosNotFromTypeSystem);
	}

	private void createJAXBContext(final Collection<Class> allDtoClasses)
	{
		try
		{
			context = JAXBContext.newInstance(allDtoClasses.toArray(new Class[allDtoClasses.size()]));
		}
		catch (final JAXBException jaxbe)
		{
			LOG.error("\nBuilding of the custom JAXBContext failed. The deafult JAXBContext will be taken within current request.\n"
					+ "Using default JAXBContext may lead to errors during processing request(a lack of required classes)."
					+ "Reason: \n\n" + jaxbe.getMessage(), jaxbe);

		}
		catch (final IllegalArgumentException iae)
		{
			LOG.error("\nBuilding of the custom JAXBContext failed. The deafult JAXBContext will be taken within current request.\n"
					+ "Using default JAXBContext may lead to errors during processing request(a lack of required classes)."
					+ "Reason:\n\n" + iae.getMessage(), iae);
		}
	}
}
