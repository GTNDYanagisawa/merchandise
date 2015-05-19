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
package de.hybris.platform.acceleratorservices.dataimport.batch.task;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.HeaderTask;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.mapping.ConverterMapping;
import de.hybris.platform.acceleratorservices.dataimport.batch.util.BatchDirectoryUtils;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;


/**
 * Transformer that retrieves a CSV file and transforms it to an impex file.
 */
public class ImpexTransformerTask implements HeaderTask, ApplicationContextAware
{
	private static final String IMPEX_FILE_PREFIX = "impex_";
	private static final String ERROR_FILE_PREFIX = "error_";
	private static final Logger LOG = Logger.getLogger(ImpexTransformerTask.class);

	private ApplicationContext applicationContext;

	private String encoding = CSVConstants.HYBRIS_ENCODING;
	private int linesToSkip;
	private char fieldSeparator = ';';
	private Map<String, List<ImpexConverter>> converterMap;
	private CleanupHelper cleanupHelper;

	@Override
	public BatchHeader execute(final BatchHeader header) throws UnsupportedEncodingException, FileNotFoundException
	{
		Assert.notNull(header);
		Assert.notNull(header.getFile());
		final File file = header.getFile();
		header.setEncoding(encoding);
		final List<ImpexConverter> converters = getConverters(file);
		int position = 1;
		for (final ImpexConverter converter : converters)
		{
			final File impexFile = getImpexFile(file, position++);
			if (convertFile(header, file, impexFile, converter))
			{
				header.addTransformedFile(impexFile);
			}
			else
			{
				cleanupHelper.cleanupFile(impexFile);
			}
		}
		return header;
	}

	/**
	 * Converts the CSV file to an impex file using the given converter
	 * 
	 * @param header
	 * @param file
	 * @param impexFile
	 * @param converter
	 * @return true, if the file contains at least one converted row
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	protected boolean convertFile(final BatchHeader header, final File file, final File impexFile, final ImpexConverter converter)
			throws UnsupportedEncodingException, FileNotFoundException
	{
		boolean result = false;
		CSVReader csvReader = null;
		PrintWriter writer = null;
		PrintWriter errorWriter = null;
		try
		{
			csvReader = createCsvReader(file);
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(impexFile), encoding)));
			writer.println(getReplacedHeader(header, converter));
			while (csvReader.readNextLine())
			{
				final Map<Integer, String> row = csvReader.getLine();
				if (converter.filter(row))
				{
					try
					{
						writer.println(converter.convert(row, header.getSequenceId()));
						result = true;
					}
					catch (final IllegalArgumentException exc)
					{
						errorWriter = writeErrorLine(file, csvReader, errorWriter, exc);
					}
				}
			}
		}
		finally
		{
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(errorWriter);
			closeQuietly(csvReader);
		}
		return result;
	}

	/**
	 * Returns the header string with all defined replacements:
	 * <ul>
	 * <li>$STORE: store name</li>
	 * <li>$CATALOG: full catalog name</li>
	 * <li>$LANGUAGE$: the language defined in the filename, defaulted to a fallback language (e.g. en)</li>
	 * <li>$NET: the net setting per catalog</li>
	 * <li>$TYPE$: optional type setting to reuse converter definitions where only the type name changes (e.g. Product)</li>
	 * </ul>
	 * 
	 * @param header
	 * @param converter
	 * @return header string with replacements
	 */
	protected String getReplacedHeader(final BatchHeader header, final ImpexConverter converter)
	{
		final Map<String, String> symbols = new TreeMap<String, String>();
		buildReplacementSymbols(symbols, header, converter);
		return replaceSymbolsInText(converter.getHeader(), symbols);
	}

	protected void buildReplacementSymbols(final Map<String, String> symbols, final BatchHeader header,
			final ImpexConverter converter)
	{
		symbols.put("$CATALOG$", header.getCatalog());
		symbols.put("$LANGUAGE$", header.getLanguage());
		symbols.put("$NET$", Boolean.toString(header.isNet()));

		if (converter.getType() != null)
		{
			symbols.put("$TYPE$", converter.getType());
		}

		symbols.put("$BASE_SOURCE_DIR$", header.getStoreBaseDirectory());

		final File headerFile = header.getFile();
		if (headerFile != null)
		{
			final String absoluteFilePath = headerFile.getAbsolutePath();
			final String absoluteFolderPath = headerFile.getParentFile().getAbsolutePath();
			symbols.put("$SOURCE_FILE$", absoluteFilePath);
			symbols.put("$SOURCE_DIR$", absoluteFolderPath);
		}
	}

	protected String replaceSymbolsInText(final String text, final Map<String, String> symbols)
	{
		String result = text;
		if (text != null && !text.isEmpty() && !symbols.isEmpty())
		{
			for (final Map.Entry<String, String> entry : symbols.entrySet())
			{
				if (entry.getKey() != null && entry.getKey().length() > 0 && entry.getValue() != null)
				{
					result = result.replace(entry.getKey(), entry.getValue());
				}
			}
		}
		return result;
	}

	/**
	 * Prints an error line containing the reason and the source line to the error file
	 * 
	 * @param file
	 * @param csvReader
	 * @param errorWriter
	 * @param exc
	 *           the exception
	 * @return error writer
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	protected PrintWriter writeErrorLine(final File file, final CSVReader csvReader, final PrintWriter errorWriter,
			final IllegalArgumentException exc) throws UnsupportedEncodingException, FileNotFoundException
	{
		PrintWriter result = errorWriter;
		if (result == null)
		{
			result = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getErrorFile(file)), encoding)));
		}
		result.print(exc.getMessage());
		result.print(": ");
		result.println(csvReader.getSourceLine());
		return result;
	}

	/**
	 * Creates a CSV Reader
	 * 
	 * @param file
	 * @return a initialised CSV reader
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	protected CSVReader createCsvReader(final File file) throws UnsupportedEncodingException, FileNotFoundException
	{
		final CSVReader csvReader = new CSVReader(file, encoding);
		csvReader.setLinesToSkip(linesToSkip);
		csvReader.setFieldSeparator(new char[]
		{ fieldSeparator });
		return csvReader;
	}

	/**
	 * Returns the impex file
	 * 
	 * @param file
	 * @param position
	 *           file position
	 * @return the impex file
	 */
	protected File getImpexFile(final File file, final int position)
	{
		return new File(file.getParent(), IMPEX_FILE_PREFIX + position + "_" + file.getName());
	}

	/**
	 * Returns the error file
	 * 
	 * @param file
	 * @return the error file
	 */
	protected File getErrorFile(final File file)
	{
		return new File(BatchDirectoryUtils.getRelativeErrorDirectory(file), ERROR_FILE_PREFIX + file.getName());
	}

	/**
	 * Retrieves the converter for a file.
	 * 
	 * @param file
	 * @throws IllegalArgumentException
	 *            if no converter was found
	 * @return the configured converter
	 */
	protected List<ImpexConverter> getConverters(final File file)
	{
		for (final Map.Entry<String, List<ImpexConverter>> entry : converterMap.entrySet())
		{
			if (file.getName().startsWith(entry.getKey()))
			{
				return entry.getValue();
			}
		}
		throw new IllegalArgumentException("File prefix unknown: " + file.getName());
	}

	/**
	 * @param encoding
	 *           the encoding to set
	 */
	public void setEncoding(final String encoding)
	{
		Assert.hasText(encoding);
		this.encoding = encoding;
	}

	public void setLinesToSkip(final int linesToSkip)
	{
		Assert.isTrue(linesToSkip >= 0);
		this.linesToSkip = linesToSkip;
	}

	/**
	 * @param fieldSeparator
	 *           the fieldSeparator to set
	 */
	public void setFieldSeparator(final char fieldSeparator)
	{
		this.fieldSeparator = fieldSeparator;
	}

	/**
	 * Initializes by collecting all beans of type {@link ConverterMapping}
	 */
	public void initConvertersMap()
	{
		final Map<String, ConverterMapping> mappings = applicationContext.getBeansOfType(ConverterMapping.class);
		this.converterMap = new HashMap<String, List<ImpexConverter>>();
		for (final ConverterMapping mapping : mappings.values())
		{
			List<ImpexConverter> converters = null;
			if (converterMap.containsKey(mapping.getMapping()))
			{
				converters = converterMap.get(mapping.getMapping());
				if (!converters.contains(mapping.getConverter()))
				{
					converters.add(mapping.getConverter());
				}
			}
			else
			{
				converterMap.put(mapping.getMapping(), converters = new ArrayList<ImpexConverter>());
				converters.add(mapping.getConverter());
			}
		}
	}

	/**
	 * Method for pure test usage, please use {@link #initConvertersMap()} method
	 */
	protected void setConverterMap(final Map<String, List<ImpexConverter>> converterMap)
	{
		this.converterMap = converterMap;
	}

	/**
	 * Method for pure test usage, please use {@link #initConvertersMap()} method
	 */
	protected Map<String, List<ImpexConverter>> getConverterMap()
	{
		return converterMap;
	}

	/**
	 * @param cleanupHelper
	 *           the cleanupHelper to set
	 */
	@Required
	public void setCleanupHelper(final CleanupHelper cleanupHelper)
	{
		this.cleanupHelper = cleanupHelper;
	}

	protected void closeQuietly(final CSVReader csvReader)
	{
		if (csvReader != null)
		{
			try
			{
				csvReader.close();
			}
			catch (final IOException e)
			{
				LOG.warn("Could not close csvReader" + e);
			}
		}
	}

	@Override
	public void setApplicationContext(final ApplicationContext appCtx) throws BeansException
	{
		this.applicationContext = appCtx;
	}

	/**
	 * @return the encoding
	 */
	protected String getEncoding()
	{
		return encoding;
	}

	/**
	 * @return the linesToSkip
	 */
	protected int getLinesToSkip()
	{
		return linesToSkip;
	}

	/**
	 * @return the fieldSeparator
	 */
	protected char getFieldSeparator()
	{
		return fieldSeparator;
	}

	/**
	 * @return the cleanupHelper
	 */
	protected CleanupHelper getCleanupHelper()
	{
		return cleanupHelper;
	}
}
