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
package de.hybris.platform.sap.core.jco.rec.impl;

import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;
import de.hybris.platform.sap.core.jco.rec.VersionReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * Implementation of {@link VersionReader} interface.
 */
public class VersionReaderImpl implements VersionReader
{

	private static final int MAXLINESTOREAD = 5;
	private static final String VERSIONTAGNAME = "RepositoryVersion";

	private BufferedReader br;

	@Override
	public String getVersion(final File file)
	{
		try
		{
			initializeReader(file);

			for (int i = 0; i < MAXLINESTOREAD; i++)
			{
				final String version = readLine();
				if (version != null)
				{
					return version;
				}
			}
		}
		catch (final IOException e)
		{
			throw new JCoRecRuntimeException("An error occured while reading the repository file!", e);
		}
		finally
		{
			destroyReader();
		}
		return null;
	}

	/**
	 * Creates a new BufferedReader.
	 * 
	 * @param file
	 *           the file to read.
	 * @throws FileNotFoundException
	 *            if the file was not found.
	 */
	private void initializeReader(final File file) throws FileNotFoundException
	{
		br = new BufferedReader(new FileReader(file));
	}

	/**
	 * Closes and destroys the current BufferedReader.
	 */
	private void destroyReader()
	{
		if (br != null)
		{
			try
			{
				br.close();
				br = null;
			}
			catch (final IOException e)
			{
				// if closing fails, reader is probably already closed 
				// so nothing left to be done
				//				throw new JCoRecRuntimeException("I/O error occured during closing of the reader!", e);
			}
		}
	}

	/**
	 * Reads one line from the file as long as there is one line left. If the line contains a RepositoryVersion-tag, its
	 * value is returned.
	 * 
	 * @return Returns the value found in the file or null, if no appropriate tag was found.
	 * @throws IOException
	 *            raised by {@link BufferedReader#readLine()}.
	 */
	private String readLine() throws IOException
	{
		final String line = br.readLine();
		// if and of file is reached
		if (line == null)
		{
			return null;
		}

		if (line.contains(VERSIONTAGNAME))
		{
			final String[] parts = line.split(VERSIONTAGNAME);
			if (parts.length < 4)
			{
				//				throw new JCoRecException("Attribute " + VERSIONATTRIBUTENAME + " is malformed!");
			}
			// parts[0] == '...<'
			// parts[1] == '>x</'
			// parts[2] == '>...'

			String version = parts[1];
			version = version.substring(1, version.length() - 2);

			return version;
		}

		return null;
	}

}
