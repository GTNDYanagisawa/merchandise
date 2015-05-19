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
import de.hybris.platform.sap.core.jco.rec.RecorderUtils;
import de.hybris.platform.sap.core.jco.rec.RepositoryPlayback;
import de.hybris.platform.sap.core.jco.rec.RepositoryPlaybackFactory;
import de.hybris.platform.sap.core.jco.rec.version000.impl.JCoRecRepository;
import de.hybris.platform.sap.core.jco.rec.version100.RepositoryPlayback100;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;


/**
 * Implementation of {@link RepositoryPlaybackFactory} interface.
 */
public class RepositoryPlaybackFactoryImpl implements RepositoryPlaybackFactory
{
	private static final String V100 = "1.0.0";

	private String version;
	private static File repoFile;

	private JCoRecRepository recRepo;

	/**
	 * Constructor.<br/>
	 * Parses the RepositoryVersion from the given file.
	 * 
	 * @param file
	 *           the file containing the recorded back-end data.
	 */
	public RepositoryPlaybackFactoryImpl(final File file)
	{
		repoFile = file;
		parseRepositoryVersion();
	}

	/**
	 * Uses VersionReader to get the RepositoryVersion from the repository-file.
	 */
	private void parseRepositoryVersion()
	{
		version = new VersionReaderImpl().getVersion(repoFile);
	}

	@Override
	public RepositoryPlayback createRepositoryPlayback()
	{
		if (version == null)
		{
			// version is null because VersionReader couldn't find a version number in the file
			// if no version is in the file, the file is probably an old repository
			return createRepositoryOld(repoFile);
		}
		if (version.equals(V100))
		{
			final Repository repo = createRepository100(repoFile);
			return new RepositoryPlayback100(repo);
		}

		throw new JCoRecRuntimeException("Unsupported version number \"" + version + "\" found in repository file!");
	}

	/**
	 * Creates an old JCoRecRepository.
	 * 
	 * @param file
	 *           the file containing the recorded back-end data.
	 * @return Returns the fresh instance.
	 */
	private JCoRecRepository createRepositoryOld(final File file)
	{
		if (recRepo == null)
		{
			recRepo = new JCoRecRepository(file);
			recRepo.parseRepositoryFile(file);
		}
		return recRepo;
	}

	/**
	 * Creates a Repository100.
	 * 
	 * @param file
	 *           the file containing the recorded back-end data.
	 * @return Returns the fresh instance.
	 */
	private Repository createRepository100(final File file)
	{
		try
		{
			final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			final Schema schema = schemaFactory.newSchema(new File(RecorderUtils.getSchemaFilePath()));
			final JAXBContext context = JAXBContext.newInstance(Repository.class);

			final Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setSchema(schema);

			final InputStream inputStream = new FileInputStream(file);//"input.xml");
			final Reader reader = new InputStreamReader(inputStream, "UTF-8");
			return (Repository) unmarshaller.unmarshal(reader);//file);
		}
		catch (SAXException | JAXBException | FileNotFoundException | UnsupportedEncodingException e)
		{
			throw new JCoRecRuntimeException("An error occured while unmarshalling the xml file!", e);
		}
	}

}
