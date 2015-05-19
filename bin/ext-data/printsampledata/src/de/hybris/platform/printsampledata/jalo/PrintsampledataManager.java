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
package de.hybris.platform.printsampledata.jalo;

import de.hybris.platform.core.Registry;
import de.hybris.platform.print.jalo.Chapter;
import de.hybris.platform.print.jalo.Page;
import de.hybris.platform.print.jalo.PathPrefix;
import de.hybris.platform.print.jalo.PrintManager;
import de.hybris.platform.print.jalo.Publication;
import de.hybris.platform.printsampledata.constants.PrintsampledataConstants;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.JspContext;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;



/**
 * This is the extension manager of the Printsampledata extension.
 */
public class PrintsampledataManager extends GeneratedPrintsampledataManager
{
	/** Edit the local|project.properties to change logging behavior (properties 'log4j.*'). */
	private static final Logger LOG = Logger.getLogger(PrintsampledataManager.class.getName());

	private static String INDESIGN_FILE_EXT = ".indd";
	private static String PATH_DELIMITER = "/";


	/**
	 * Get the valid instance of this manager.
	 *
	 * @return the current instance of this manager
	 */
	public static PrintsampledataManager getInstance()
	{
		return (PrintsampledataManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager().getExtension(PrintsampledataConstants.EXTENSIONNAME);
	}


	/**
	 * Never call the constructor of any manager directly, call getInstance() You can place your business logic here -
	 * like registering a jalo session listener. Each manager is created once for each tenant.
	 */
	public PrintsampledataManager() // NOPMD
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("constructor of PrintsampledataManager called.");
		}
	}

	/**
	 * Implement this method to create initial objects. This method will be called by system creator during
	 * initialization and system update. Be sure that this method can be called repeatedly.
	 *
	 * An example usage of this method is to create required cronjobs or modifying the type system (setting e.g some
	 * default values)
	 *
	 * @param params
	 *           the parameters provided by user for creation of objects for the extension
	 * @param jspc
	 *           the jsp context; you can use it to write progress information to the jsp page during creation
	 */
	@Override
	public void createEssentialData(final Map<String, String> params, final JspContext jspc)
	{
		// See PrintsampledataSystemSetup for import of essential data
	}

	/**
	 * Implement this method to create data that is used in your project. This method will be called during the system
	 * initialization.
	 *
	 * An example use is to import initial data like currencies or languages for your project from an csv file.
	 *
	 * @param params
	 *           the parameters provided by user for creation of objects for the extension
	 * @param jspc
	 *           the jsp context; you can use it to write progress information to the jsp page during creation
	 */
	@Override
	public void createProjectData(final Map<String, String> params, final JspContext jspc) throws Exception
	{
		// See PrintsampledataSystemSetup for import of project data
	}


	public File getPrintFilehandlingRoot()
	{
		String root = Config.getString("printsampledata.filehandling.root", null);
		if (root == null)
		{
			final String resourceRelativePath = Config.getString("printsampledata.filehandling.indesign.resource.relativepath", "indesign");
			final URL rootUrl = PrintsampledataManager.class.getClassLoader().getResource(resourceRelativePath);
			if (rootUrl == null)
			{
				LOG.info("For using the printdemo setup the 'printsampledata.filehandling.root' path.");
				return null;
			}
			root = rootUrl.getPath();
		}
		return new File(root);
	}


	public void setExampleFileHandlingAttributes(final PathPrefix pathPrefix)
	{
		final List<Publication> publications = PrintManager.getInstance().getAllPublications();
		for (final Publication p : publications)
		{
			this.setFileHandlingAttributes(p, pathPrefix.getVariableName() + "/" + p.getCode());
		}
	}

	public void setFileHandlingAttributes(final Publication pub, final String root)
	{
		final String pubCode = pub.getCode().toLowerCase();

		final String documentPath = root + "/documents/";
		final String templateFilePath = root + "/templates/" + pubCode + INDESIGN_FILE_EXT;

		if (pub.getDocumentTemplate() == null)
		{
			pub.setDocumentTemplate(templateFilePath);
		}
		if (pub.getFilePath() == null)
		{
			pub.setFilePath(documentPath);
		}
		if (pub.getFileName() == null)
		{
			pub.setFileName(pubCode + INDESIGN_FILE_EXT);
		}

		for (final Chapter chapter : pub.getRootChapters())
		{
			setFileHandlingAttributes(chapter, documentPath, templateFilePath);
		}
	}

	public void setFileHandlingAttributes(final Chapter chapter, final String documentPath, final String templateFilePath)
	{
		final String chapterCode = chapter.getCode().toLowerCase();
		final String chapterDocPath = documentPath + chapterCode + PATH_DELIMITER;

		if (chapter.getDocumentTemplate() == null)
		{
			chapter.setDocumentTemplate(templateFilePath);
		}
		if (chapter.getFilePath() == null)
		{
			chapter.setFilePath(chapterDocPath);
		}
		if (chapter.getFileName() == null)
		{
			chapter.setFileName(chapterCode + INDESIGN_FILE_EXT);
		}

		for (final Chapter subChapster : chapter.getSubChapters())
		{
			setFileHandlingAttributes(subChapster, chapterDocPath, templateFilePath);
		}

		for (final Page page : chapter.getPages())
		{
			setFileHandlingAttributes(page, chapterDocPath, templateFilePath);
		}
	}

	public void setFileHandlingAttributes(final Page page, final String documentPath, final String templateFilePath)
	{
		final String pageCode = page.getCode().toLowerCase();

		if (page.getDocumentTemplate() == null)
		{
			page.setDocumentTemplate(templateFilePath);
		}
		if (page.getFilePath() == null)
		{
			page.setFilePath(documentPath);
		}
		if (page.getFileName() == null)
		{
			page.setFileName(pageCode + INDESIGN_FILE_EXT);
		}
	}
}
