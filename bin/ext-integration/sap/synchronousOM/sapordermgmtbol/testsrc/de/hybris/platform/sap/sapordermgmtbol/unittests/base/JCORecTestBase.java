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
package de.hybris.platform.sap.sapordermgmtbol.unittests.base;


import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepositoryFactory;
import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.annotation.Resource;


@SuppressWarnings("javadoc")
public class JCORecTestBase extends SapordermanagmentBolSpringJunitTest
{
	/**
	 * JCo mock repository factory. .
	 */
	@Resource(name = "sapCoreJCoMockRepositoryFactory")
	protected JCoMockRepositoryFactory mockReposiotryFactory;

	public final static String JCO_DATA_PATH_PREFIX = "test//";


	protected JCoMockRepository getJCORepository(final String name)
	{

		try
		{
			final URL repositoryResource = this.getClass().getClassLoader().getResource(JCO_DATA_PATH_PREFIX + name + ".xml");
			File file = null;
			try
			{
				file = new File(repositoryResource.toURI());
			}
			catch (final URISyntaxException e)
			{

				e.printStackTrace();
			}

			return mockReposiotryFactory.getMockRepository(file);

		}
		catch (final JCoRecRuntimeException e1)
		{
			throw new RuntimeException("Repository not found", e1);
		}
	}

}
