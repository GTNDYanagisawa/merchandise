/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package de.hybris.platform.printsampledata.setup;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.BeanShellUtils;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.printsampledata.constants.PrintsampledataConstants;
import de.hybris.platform.printsampledata.jalo.PrintsampledataManager;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.RedeployUtilities;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import bsh.Interpreter;

/**
 * SystemSetup class that creates Print Sampledata
 *
 * @author  rri
 */
@SystemSetup(extension = PrintsampledataConstants.EXTENSIONNAME)
public class PrintsampledataSystemSetup extends AbstractSystemSetup
{
	private static final Logger LOG = Logger.getLogger(PrintsampledataSystemSetup.class.getName());

	private static final String IMPORT_BASICS = "Import Basics";
	private static final String IMPORT_SAMPLECOMETCONFIG = "Import SampleCometConfiguration";
	private static final String DELETE_DEFAULTCOMETCONFIG = "Delete DefaultCometConfiguration";
	private static final String IMPORT_ELECTRONICS_PUB = "Import Electronics Publication";
	private static final String IMPORT_APPAREL_PUB = "Import Apparel Publication";
	private static final String FIX_HTML_LINEBREAKS_IN_PRODUCT_SAMPLEDATA = "Fix HTML line breaks in product sample data";
	private static final String UPDATE_FILEHANDLING = "Update File Handling";
	private static final String IMPORT_PRINTCOCKPIT = "Import PrintCockpit Sampledata";
	private static final String IMPORT_COMMENTS = "Import Sample Comments";

	private final static String PRINT_SAMPLE_NETSHARE = "sample_root";
	private final static String PRINT_SETPATHPREFIX_SCRIPT = " "//
			+ "import de.hybris.platform.printsampledata.jalo.PrintsampledataManager;\n"//
			+ "import de.hybris.platform.jalo.type.TypeManager;\n"//
			+ "import de.hybris.platform.jalo.JaloItemNotFoundException;\n"//
			+ "import de.hybris.platform.print.jalo.PrintManager;\n"//
			+ "import de.hybris.platform.print.jalo.PathPrefix;\n"//
			+ "\n" //
			+ "private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PrintsampledataManager.class.getName());\n"//
			+ "try\n"//
			+ "{\n"//
			+ "	TypeManager.getInstance().getComposedType(PathPrefix.class);\n"//
			+ "	if( PrintsampledataManager.getInstance().getPrintFilehandlingRoot() != null )\n"//
			+ "	{\n"//
			+ "		PrintManager.getInstance().getOrCreatePathPrefix(\""
			+ PRINT_SAMPLE_NETSHARE
			+ "\")"
			+ ".setPrefixWIN(PrintsampledataManager.getInstance().getPrintFilehandlingRoot().getAbsolutePath());\n"//
			+ "		PrintManager.getInstance().getOrCreatePathPrefix(\""
			+ PRINT_SAMPLE_NETSHARE
			+ "\").setPrefixMAC(PrintsampledataManager.getInstance().getPrintFilehandlingRoot().getAbsolutePath());\n"//
			+ "	}\n"//
			+ "}catch(JaloItemNotFoundException e){log.info(\"Skipped SetPathPrefix script\");}";
	private final static String PRINT_SETFILEHANDLINGATTRS_SCRIPT = " "//
			+ "import de.hybris.platform.printsampledata.jalo.PrintsampledataManager;\n"//
			+ "import de.hybris.platform.jalo.type.TypeManager;\n"//
			+ "import de.hybris.platform.jalo.JaloItemNotFoundException;\n"//
			+ "import de.hybris.platform.print.jalo.PrintManager;\n"//
			+ "import de.hybris.platform.print.jalo.PathPrefix;\n"//
			+ "\n"//
			+ "private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PrintsampledataManager.class.getName());\n"//
			+ "try\n"//
			+ "{\n"//
			+ "	TypeManager.getInstance().getComposedType(PathPrefix.class);\n"//
			+ "	PrintsampledataManager.getInstance().setExampleFileHandlingAttributes(PrintManager.getInstance().getOrCreatePathPrefix(\""
			+ PRINT_SAMPLE_NETSHARE + "\"));\n"//
			+ "}catch(JaloItemNotFoundException e){log.info(\"Skipped SetFileHandling script\");}";



	@SystemSetupParameterMethod
	@Override
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();
		params.add(createBooleanSystemSetupParameter(IMPORT_BASICS, IMPORT_BASICS, true));
		params.add(createBooleanSystemSetupParameter(IMPORT_SAMPLECOMETCONFIG, IMPORT_SAMPLECOMETCONFIG, true));
		// params.add(createBooleanSystemSetupParameter(DELETE_DEFAULTCOMETCONFIG, DELETE_DEFAULTCOMETCONFIG, false));
		params.add(createBooleanSystemSetupParameter(IMPORT_ELECTRONICS_PUB, IMPORT_ELECTRONICS_PUB, true));
		params.add(createBooleanSystemSetupParameter(IMPORT_APPAREL_PUB, IMPORT_APPAREL_PUB, true));
		params.add(createMultivalueSystemSetupParameter(FIX_HTML_LINEBREAKS_IN_PRODUCT_SAMPLEDATA, FIX_HTML_LINEBREAKS_IN_PRODUCT_SAMPLEDATA, "only for selected publications", "only for selected publications", "force all", "no"));
		params.add(createBooleanSystemSetupParameter(UPDATE_FILEHANDLING, UPDATE_FILEHANDLING, true));
		params.add(createBooleanSystemSetupParameter(IMPORT_PRINTCOCKPIT, IMPORT_PRINTCOCKPIT, true));
		params.add(createBooleanSystemSetupParameter(IMPORT_COMMENTS, IMPORT_COMMENTS, true));
		return params;
	}


	@SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
	public void createEssentialData(final SystemSetupContext context)
	{
		// Add Essential Data here as you require
	}


	@SystemSetup(type = SystemSetup.Type.PROJECT, process = SystemSetup.Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{
		final List<String> extensionNames = getExtensionNames();

		// PrintCockpit
		if (extensionNames.contains("printcockpit"))
		{
			if( getBooleanSystemSetupParameter(context, IMPORT_PRINTCOCKPIT) )
			{
				logInfo(context, "Print Sampledata Import: Import PrintCockpit sample data");
				try
				{
					importCSVFromResources("/printsampledata/printcockpit/users.csv", "utf-8");
					importCSVFromResources("/printsampledata/printcockpit/usergroups.csv", "utf-8");
					importCSVFromResources("/printsampledata/printcockpit/ui_component.csv", "utf-8");
				}
				catch (final Exception e)
				{
					logError(context, "Print Sampledata Import: Error while importing PrintCockpit sample data", e);
				}
			}
		}

		// Print
		if (extensionNames.contains("print"))
		{
			if( getBooleanSystemSetupParameter(context, IMPORT_BASICS) )
			{
				// Basics
				logInfo(context, "Print Sampledata Import: Import Basics");
				try
				{
					importCSVFromResources("/printsampledata/print/basics/_import.impex", "utf-8");
				}
				catch (final Exception e)
				{
					logError(context, "Print Sampledata Import: Error while importing Basics", e);
				}
			}

			if( getBooleanSystemSetupParameter(context, IMPORT_SAMPLECOMETCONFIG) )
			{
				// Comet Configuration
				logInfo(context, "Print Sampledata Import: Import SampleCometConfiguration");
				try
				{
					importCSVFromResources("/printsampledata/print/cometconfiguration/_import.impex", "utf-8");
				}
				catch (final Exception e)
				{
					logError(context, "Print Sampledata Import: Error while importing SampleCometConfiguration", e);
				}
			}

			if( getBooleanSystemSetupParameter(context, IMPORT_ELECTRONICS_PUB) )
			{
				// Electronics Publication
				logInfo(context, "Print Sampledata Import: Import Electronics Publication");
				try
				{
					importCSVFromResources("/printsampledata/print/publicationElectronics/_import.impex", "utf-8");
				}
				catch (final Exception e)
				{
					logError(context, "Print Sampledata Import: Error while importing Electronics Publication", e);
				}
			}

			if( getBooleanSystemSetupParameter(context, IMPORT_APPAREL_PUB) )
			{
				// Apparel Publication
				logInfo(context, "Print Sampledata Import: Import Apparel Publication");
				try
				{
					importCSVFromResources("/printsampledata/print/publicationApparel/_import.impex", "utf-8");
				}
				catch (final Exception e)
				{
					logError(context, "Print Sampledata Import: Error while importing Apparel Publication", e);
				}
			}

			if( "only for selected publications".equals(getSystemSetupParameterAsString(context, FIX_HTML_LINEBREAKS_IN_PRODUCT_SAMPLEDATA))
					|| "force all".equals(getSystemSetupParameterAsString(context, FIX_HTML_LINEBREAKS_IN_PRODUCT_SAMPLEDATA)) )
			{
				if( "force all".equals(getSystemSetupParameterAsString(context, FIX_HTML_LINEBREAKS_IN_PRODUCT_SAMPLEDATA))
						|| getBooleanSystemSetupParameter(context, IMPORT_ELECTRONICS_PUB) )
				{
					// Fix the electronics product data
					try
					{
						importCSVFromResources("/printsampledata/print/_FIXES/electronics_products.impex", "utf-8");
					}
					catch (final Exception e)
					{
						logError(context, "Print Sampledata Import: Error while fixing electronics product data", e);
					}
				}
				if( "force all".equals(getSystemSetupParameterAsString(context, FIX_HTML_LINEBREAKS_IN_PRODUCT_SAMPLEDATA))
						|| getBooleanSystemSetupParameter(context, IMPORT_APPAREL_PUB) )
				{
					// Fix the apparel product data
					try
					{
						importCSVFromResources("/printsampledata/print/_FIXES/apparel_products.impex", "utf-8");
					}
					catch (final Exception e)
					{
						logError(context, "Print Sampledata Import: Error while fixing apparel product data", e);
					}
				}
			}

			if( getBooleanSystemSetupParameter(context, UPDATE_FILEHANDLING) )
			{
				// Setup file handling
				logInfo(context, "Print Sampledata Import: Update file handling");
				setupDocumentRootPath();
				setupFileHandlingAttributes();
			}

			// Comments
			if( extensionNames.contains("comments") && getBooleanSystemSetupParameter(context, IMPORT_COMMENTS) )
			{
				logInfo(context, "Print Sampledata Import: Import sample Comments");
				try
				{
					importCSVFromResources("/printsampledata/print/comments/sample_comments.csv", "utf-8");
				}
				catch (final Exception e)
				{
					logError(context, "Print Sampledata Import: Error while importing sample Comments", e);
				}
			}
		}

		logInfo(context, "Finished importing Print Sampledata.");
	}


	protected List<String> getExtensionNames()
	{
		return Registry.getCurrentTenant().getTenantSpecificExtensionNames();
	}

	private void importCSVFromResources(final String csv, String encoding) throws Exception
	{
		if (encoding == null)
		{
			encoding = "windows-1252";
		}
		LOG.info("Importing resource " + csv);
		final InputStream is = PrintsampledataManager.class.getResourceAsStream(csv);
		ImpExManager.getInstance().importData(is, encoding, CSVConstants.HYBRIS_FIELD_SEPARATOR,
				CSVConstants.HYBRIS_QUOTE_CHARACTER, true);
	}

	private void setupDocumentRootPath()
	{
		if (!RedeployUtilities.isShutdownInProgress())
		{
			//it does not make sense to the the path if the platform is shutting down
			//see PLA-12242
			executeScript(PRINT_SETPATHPREFIX_SCRIPT, null);
		}
	}

	private void setupFileHandlingAttributes()
	{
		executeScript(PRINT_SETFILEHANDLINGATTRS_SCRIPT, null);
	}

	private Object executeScript(final String code, final Map<String, Object> ctx)
	{
		try
		{
			final Interpreter bsh = BeanShellUtils.createInterpreter(ctx);
			return bsh.eval(code);
		}
		catch (final Exception e)
		{
			LOG.warn("Sample print document root path could not be set.", e);
			return null;
		}
	}


	public SystemSetupParameter createMultivalueSystemSetupParameter(final String key, final String label, final String defaultValue, final String... values)
	{
		final SystemSetupParameter systemSetupParam = new SystemSetupParameter(key);
		systemSetupParam.setLabel(label);
		for (final String value : values)
		{
			systemSetupParam.addValue(value, (value.equals(defaultValue)));
		}
		return systemSetupParam;
	}

	public String getSystemSetupParameterAsString(final SystemSetupContext context, final String key)
	{
		final String parameterValue = context.getParameter(context.getExtensionName() + "_" + key);
		if (parameterValue != null)
		{
			return parameterValue;
		}

		// Have not been able to determine value from context, fallback to default value
		final List<SystemSetupParameter> initializationOptions = getInitializationOptions();
		if (initializationOptions != null)
		{
			for (final SystemSetupParameter option : initializationOptions)
			{
				if (key.equals(option.getKey()))
				{
					final String[] defaults = option.getDefaults();
					if (defaults != null && defaults.length > 0)
					{
						LOG.warn("Missing setup parameter for key [" + key + "], falling back to defined default [" + defaults[0] + "]");
						return defaults[0];
					}
				}
			}
		}

		LOG.warn("Missing setup parameter for key [" + key + "], falling back to [" + BOOLEAN_FALSE + "]");
		return BOOLEAN_FALSE;
	}

}
