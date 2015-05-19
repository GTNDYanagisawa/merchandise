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
package de.hybris.platform.atddengine.ant.tasks;

import de.hybris.platform.atddengine.framework.RobotTest;
import de.hybris.platform.atddengine.framework.RobotTestSuite;
import de.hybris.platform.atddengine.framework.RobotTestSuiteFactory;
import de.hybris.platform.atddengine.framework.impl.DefaultPythonProvider;
import de.hybris.platform.atddengine.framework.impl.PythonRobotTestSuiteFactory;
import de.hybris.platform.atddengine.templates.TemplateProcessor;
import de.hybris.platform.atddengine.templates.TemplateProcessorFactory;
import de.hybris.platform.atddengine.templates.impl.VelocityTemplateProcessorFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.python.core.PyException;


/**
 * Implementation class for the custom <i>generateproxies</i> ant task.
 */
public class GenerateProxies extends Task
{
	private static final Logger LOG = Logger.getLogger(GenerateProxies.class);

	private RobotTestSuiteFactory testSuiteFactory;

	private String ext;

	private String extensionPath;

	private String rootPackagePath;

	private File targetDir;

	private File generatedDir;

	private File templateFile;

	private File testPath;

	private TemplateProcessorFactory templateProcessorFactory;

	@Override
	public void execute() throws BuildException
	{
		initialize();

		try
		{
			setRobotTestSuiteFactory(new PythonRobotTestSuiteFactory(new DefaultPythonProvider()));
			setTemplateProcessorFactory(new VelocityTemplateProcessorFactory());
			processRobotDir(testPath);
		}
		catch (final IOException e)
		{
			final String message = String.format("Processing robot directory [%s] failed", testPath);
			LOG.error(message, e);
		}
	}

	private void initialize()
	{
		PropertyConfigurator.configure(getProject().getProperty("log4j.configuration"));
		LOG.info("Log4j properly initialized!");

		if (ext != null)
		{
			extensionPath = getProject().getProperty(String.format("ext.%s.path", ext));
			LOG.info("extension path: " + extensionPath);
		}
		if (templateFile == null)
		{
			templateFile = new File(getProject().getProperty("ext.atddengine.path") + "/resources/"
					+ getProject().getProperty("atddengine.proxies.template"));
		}

		if (targetDir == null && extensionPath != null)
		{
			targetDir = new File(extensionPath + "/" + getProject().getProperty("atddengine.proxies.target-path"));
		}

		if (rootPackagePath == null)
		{
			rootPackagePath = getProject().getProperty("atddengine.proxies.root-package-path");
		}

		if (testPath == null && extensionPath != null)
		{
			testPath = new File(extensionPath + "/resources/" + getProject().getProperty("atddengine.tests-path"));
		}

		if (generatedDir == null && extensionPath != null)
		{
			generatedDir = new File(extensionPath + "/genresources/" + getProject().getProperty("atddengine.tests-path"));
		}
	}

	private List<File> collectTestSuiteFiles(final File projectDir)
	{
		final List<File> testSuiteFiles = new ArrayList<File>();

		for (final File testSuiteFile : projectDir.listFiles())
		{
			if (testSuiteFile.isFile())
			{
				testSuiteFiles.add(testSuiteFile);
			}
			else
			{
				if (!testSuiteFile.getName().equalsIgnoreCase(".svn"))
				{
					final String message = String.format("Subdirecotries are currently not supported. Skipping %s", testSuiteFile);
					LOG.warn(message);
				}
			}
		}

		return testSuiteFiles;
	}

	private void generateJUnitProxy(final File packageDir, final File testSuiteFile) throws IOException
	{
		try
		{
			final RobotTestSuite robotTestSuite = getRobotTestSuiteFactory().parseTestSuite(testSuiteFile);

			final String testSuiteName = robotTestSuite.getName().replaceAll(Pattern.quote(" "), "_");

			final Map<String, Object> binding = new HashMap<String, Object>();

			binding.put("packageName", rootPackagePath.replaceAll(Pattern.quote("/"), ".") + "." + packageDir.getName());
			binding.put("testSuiteName", testSuiteName);
			binding.put("testSuitePath", testSuiteFile.getPath().replaceAll(Pattern.quote(File.separator), "/"));
			binding.put("projectName", packageDir.getName());

			final List<String> testNames = new ArrayList<String>();

			for (final RobotTest robotTest : robotTestSuite.getRobotTests())
			{
				testNames.add(robotTest.getName());
			}

			binding.put("testNames", testNames);

			final File targetFile = new File(packageDir, testSuiteName + ".java");

			final TemplateProcessor templateProcessor = getTemplateProcessorFactory().createTemplateProcessor();

			final Writer writer = new FileWriter(targetFile);
			templateProcessor.processTemplate(writer, templateFile.getName(), binding);
			writer.close();
		}
		catch (final PyException e)
		{
			LOG.warn(String.format("Test suite file [%s] is malformed and will be ignored.", testSuiteFile.getName()));
		}
	}

	private void generateJUnitTests(final String projectName, final List<File> testSuiteFiles) throws IOException
	{
		final File parentPackageDir = new File(targetDir, rootPackagePath);

		final File packageDir = new File(parentPackageDir, projectName);

		if (!packageDir.exists() && !packageDir.mkdirs())
		{
			final String message = String.format("Failed to create packageDirectory %s!", packageDir);
			throw new IOException(message);
		}

		for (final File testSuiteFile : testSuiteFiles)
		{
			generateJUnitProxy(packageDir, generateCleanTestSuiteFile(testSuiteFile));
		}
	}

	private File generateCleanTestSuiteFile(final File testSuiteFile) throws IOException, FileNotFoundException
	{
		final File genFile = new File(generatedDir, testSuiteFile.getName());

		final FileWriter writer = new FileWriter(genFile);

		final Reader reader = new FileReader(testSuiteFile);
		final String fileContents = IOUtils.toString(reader);

		writer.write(fileContents.replaceAll(">\\s+<", "><")); // remove whitespace from XML snippets

		writer.close();
		reader.close();

		return genFile;
	}

	private RobotTestSuiteFactory getRobotTestSuiteFactory()
	{
		return testSuiteFactory;
	}

	private TemplateProcessorFactory getTemplateProcessorFactory()
	{
		return templateProcessorFactory;
	}

	private void processProjectDir(final String projectName, final File projectDir) throws IOException
	{
		if (projectDir.isDirectory())
		{
			final List<File> testSuiteFiles = collectTestSuiteFiles(projectDir);

			if (!testSuiteFiles.isEmpty())
			{
				generateJUnitTests(projectName, testSuiteFiles);
			}
		}
	}

	private void processRobotDir(final File robotDir) throws IOException
	{
		if (robotDir.isDirectory())
		{
			prepareGeneratedDirectory();
			for (final String projectName : robotDir.list())
			{
				processProjectDir(projectName, new File(robotDir, projectName));
			}
		}
		else
		{
			final String message = String.format("%s is not a directory. Skipping!", robotDir);
			LOG.error(message);
		}
	}

	private void prepareGeneratedDirectory() throws IOException
	{
		if (generatedDir.exists())
		{
			for (final File testFile : generatedDir.listFiles())
			{
				testFile.delete();
			}
		}
		else
		{
			if (!generatedDir.mkdirs())
			{
				final String message = String.format("Failed to create directory for clean test definitions %s!", generatedDir);
				throw new IOException(message);
			}
		}
	}

	private void setRobotTestSuiteFactory(final RobotTestSuiteFactory robotTestSuiteFactory)
	{
		this.testSuiteFactory = robotTestSuiteFactory;
	}

	public void setRootPackagePath(final String rootPackagePath)
	{
		this.rootPackagePath = rootPackagePath;
	}

	public void setTestPath(final String testPath)
	{
		this.testPath = new File(testPath);
	}

	public void setTargetDir(final String targetDir)
	{
		this.targetDir = new File(targetDir);
	}

	public void setGeneratedDir(final String generatedDir)
	{
		this.generatedDir = new File(generatedDir);
	}

	public void setTemplateFile(final String templateFile)
	{
		this.templateFile = new File(templateFile);
	}

	public void setExt(final String ext)
	{
		this.ext = ext;
	}

	private void setTemplateProcessorFactory(final TemplateProcessorFactory templateProcessorFactory)
	{
		this.templateProcessorFactory = templateProcessorFactory;
	}
}
