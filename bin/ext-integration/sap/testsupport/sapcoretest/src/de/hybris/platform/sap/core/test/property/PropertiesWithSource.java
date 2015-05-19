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
package de.hybris.platform.sap.core.test.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;



/**
 * Property definition based on a file. <br>
 */
class PropertiesWithSource extends LinkedProperties
{

	private static final long serialVersionUID = -1624316810350885190L;

	/**
	 * Path to the property file.
	 */
	private final String path;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *           parent parent linked properties
	 * @param path
	 *           path to the property file
	 */
	public PropertiesWithSource(final LinkedProperties parent, final String path)
	{
		super(parent);
		this.path = path;
	}


	/**
	 * Returns the path.
	 * 
	 * @return path
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * Returns the canonical path.
	 * 
	 * @return canonical path
	 * @throws IOException
	 *            if file does not exists
	 */
	public String getCanonicalPath() throws IOException
	{
		final File propFile = new File(path);
		return propFile.getCanonicalPath();
	}

	/**
	 * Loads the properties from the file.
	 * 
	 * @throws FileNotFoundException
	 *            {@link FileNotFoundException}
	 * @throws IOException
	 *            {@link IOException}
	 */
	public void loadFromFile() throws FileNotFoundException, IOException
	{
		final File propFile = new File(path);
		super.load(new FileInputStream(propFile));
	}


	@Override
	public String getInfo()
	{

		String fullQualifiedFileName;
		try
		{
			fullQualifiedFileName = this.getCanonicalPath();
		}
		catch (final IOException ex)
		{
			fullQualifiedFileName = "[ERROR] : " + path + " is not a vaild filename. " + ex.getMessage();
		}
		return fullQualifiedFileName;
	}
}
