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
package de.hybris.platform.acceleratorservices.dataimport.batch;

import de.hybris.platform.acceleratorservices.dataimport.batch.converter.SequenceIdTranslator;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Header containing all relevant process information for batch processing. This includes:
 * <ul>
 * <li>sequenceId: the sequenceId used for avoiding multiple imports by comparing the sequenceId stored in the database
 * with the current sequenceId. compare {@link SequenceIdTranslator}</li>
 * <li>file: the input CSV file that should be imported</li>
 * <li>transformedFiles: a list of transformed files to be sent to impex</li>
 * <li>catalog: the catalog to use</li>
 * <li>language: the language to use</li>
 * <li>net: the gross/net setting to use</li>
 * </ul>
 */
public class BatchHeader
{
	private Long sequenceId;
	private File file;
	private List<File> transformedFiles;
	private String encoding;
	private String storeBaseDirectory;
	private String catalog;
	private String language;
	private boolean net;

	/**
	 * @return the sequenceId
	 */
	public Long getSequenceId()
	{
		return sequenceId;
	}

	/**
	 * @param sequenceId
	 *           the sequenceId to set
	 */
	public void setSequenceId(final Long sequenceId)
	{
		this.sequenceId = sequenceId;
	}

	/**
	 * @return the file
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * @param file
	 *           the file to set
	 */
	public void setFile(final File file)
	{
		this.file = file;
	}

	/**
	 * @return the transformedFiles
	 */
	public List<File> getTransformedFiles()
	{
		return transformedFiles;
	}

	/**
	 * Adds a transformed file for further processing
	 * 
	 * @param transformedFile
	 */
	public void addTransformedFile(final File transformedFile)
	{
		if (transformedFiles == null)
		{
			transformedFiles = new LinkedList<File>();
		}
		transformedFiles.add(transformedFile);
	}

	/**
	 * @param transformedFiles
	 *           the transformedFiles to set
	 */
	public void setTransformedFiles(final List<File> transformedFiles)
	{
		this.transformedFiles = transformedFiles;
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding()
	{
		return encoding;
	}

	/**
	 * @param encoding
	 *           the encoding to set
	 */
	public void setEncoding(final String encoding)
	{
		this.encoding = encoding;
	}

	/**
	 * @return the storeBaseDirectory
	 */
	public String getStoreBaseDirectory()
	{
		return storeBaseDirectory;
	}

	/**
	 * @param storeBaseDirectory
	 *           the storeBaseDirectory to set
	 */
	public void setStoreBaseDirectory(final String storeBaseDirectory)
	{
		this.storeBaseDirectory = storeBaseDirectory;
	}

	/**
	 * @return the catalog
	 */
	public String getCatalog()
	{
		return catalog;
	}

	/**
	 * @param catalog
	 *           the catalog to set
	 */
	public void setCatalog(final String catalog)
	{
		this.catalog = catalog;
	}

	/**
	 * @return the language
	 */
	public String getLanguage()
	{
		return language;
	}

	/**
	 * @param language
	 *           the language to set
	 */
	public void setLanguage(final String language)
	{
		this.language = language;
	}

	/**
	 * @return the net
	 */
	public boolean isNet()
	{
		return net;
	}

	/**
	 * @param net
	 *           the net to set
	 */
	public void setNet(final boolean net)
	{
		this.net = net;
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this).append("file", file).append("catalog", catalog)
				.append("language", language).append("net", net).toString();
	}

}
