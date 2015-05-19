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
package de.hybris.platform.classificationsystems.jalo;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.classification.ClassificationImportCronJob;
import de.hybris.platform.classificationsystems.constants.ClassificationsystemsConstants;
import de.hybris.platform.cronjob.constants.CronJobConstants;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.servicelayer.ServicelayerTest;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@ManualTest
public class TestClassificationImports extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(TestClassificationImports.class.getName());

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
	}

	private ClassificationImportCronJob createImportJob(final String cronJobCode) throws Exception
	{
		if ((cronJobCode.equals(ClassificationsystemsConstants.ECLASS_4_1_FR)
				|| cronJobCode.equals(ClassificationsystemsConstants.ECLASS_5_0_FR) || cronJobCode
				.equals(ClassificationsystemsConstants.ECLASS_5_1_FR))
				&& CatalogManager.getInstance().getLanguageIfExists("fr") == null)
		{
			Assert.fail("Language fr is missing, cannot execute test!");
		}
		final ClassificationImportCronJob cjob = ClassificationsystemsManager.getInstance().createClassificationImportCronJob(
				cronJobCode);
		Assert.assertNotNull("could not create cronjob!", cjob);

		final EnumerationValue errorMode = EnumerationManager.getInstance().getEnumerationValue(CronJobConstants.TC.ERRORMODE,
				CronJobConstants.Enumerations.ErrorMode.FAIL);
		cjob.setErrorMode(errorMode);

		cjob.getJob().perform(cjob, true);
		if (!cjob.getResult().equals(cjob.getSuccessResult()))
		{
			LOG.error("perform " + cronJobCode + " was not sucessfull!");
			LOG.error(cjob.getUnresolvedDataStore() != null ? cjob.getUnresolvedDataStore().getPreview() : "");
		}
		return cjob;
	}

	@Test
	public void testcreateEClass_4_0_DE_Import() throws Exception //NOPMD
	{
		final ClassificationImportCronJob cjob = createImportJob(ClassificationsystemsConstants.ECLASS_4_0_DE);
		Assert.assertEquals(cjob.getSuccessResult(), cjob.getResult());
	}

	@Test
	public void testCreateEClass_4_1_DE_Import() throws Exception //NOPMD
	{
		final ClassificationImportCronJob cjob = createImportJob(ClassificationsystemsConstants.ECLASS_4_1_DE);
		Assert.assertEquals(cjob.getSuccessResult(), cjob.getResult());
	}

	@Test
	public void testCreateEClass_4_1_EN_Import() throws Exception //NOPMD
	{
		final ClassificationImportCronJob cjob = createImportJob(ClassificationsystemsConstants.ECLASS_4_1_EN);
		Assert.assertEquals(cjob.getSuccessResult(), cjob.getResult());
	}

	@Test
	public void testcreateEClass_4_1_FR_Import() throws Exception //NOPMD
	{
		final ClassificationImportCronJob cjob = createImportJob(ClassificationsystemsConstants.ECLASS_4_1_FR);
		Assert.assertEquals(cjob.getSuccessResult(), cjob.getResult());
	}

	@Test
	public void testcreateEClass_5_0_SP1_DE_Import() throws Exception //NOPMD
	{
		final ClassificationImportCronJob cjob = createImportJob(ClassificationsystemsConstants.ECLASS_5_0_DE);
		Assert.assertEquals(cjob.getSuccessResult(), cjob.getResult());
	}

	@Test
	public void testcreateEClass_5_0_SP1_EN_Import() throws Exception //NOPMD
	{
		final ClassificationImportCronJob cjob = createImportJob(ClassificationsystemsConstants.ECLASS_5_0_EN);
		Assert.assertEquals(cjob.getSuccessResult(), cjob.getResult());
	}

	@Test
	public void testcreateEClass_5_0_SP1_FR_Import() throws Exception //NOPMD
	{
		final ClassificationImportCronJob cjob = createImportJob(ClassificationsystemsConstants.ECLASS_5_0_FR);
		Assert.assertEquals(cjob.getSuccessResult(), cjob.getResult());
	}

	@Test
	public void testcreateEClass_5_1_DE_Import() throws Exception //NOPMD
	{
		final ClassificationImportCronJob cjob = createImportJob(ClassificationsystemsConstants.ECLASS_5_1_DE);
		Assert.assertEquals(cjob.getSuccessResult(), cjob.getResult());
	}

	@Test
	public void testcreateEClass_5_1_EN_Import() throws Exception //NOPMD
	{
		final ClassificationImportCronJob cjob = createImportJob(ClassificationsystemsConstants.ECLASS_5_1_EN);
		Assert.assertEquals(cjob.getSuccessResult(), cjob.getResult());
	}

	@Test
	public void testcreateEClass_5_1_FR_Import() throws Exception //NOPMD
	{
		final ClassificationImportCronJob cjob = createImportJob(ClassificationsystemsConstants.ECLASS_5_1_FR);
		Assert.assertEquals(cjob.getSuccessResult(), cjob.getResult());
	}

	@Test
	public void testcreateETIM_2_0_DE_Import() throws Exception //NOPMD
	{
		final ClassificationImportCronJob cjob = createImportJob(ClassificationsystemsConstants.ETIM_2_0_DE);
		Assert.assertEquals(cjob.getSuccessResult(), cjob.getResult());
	}

	@Test
	public void testcreateETIM_3_0_DE_Import() throws Exception //NOPMD
	{
		final ClassificationImportCronJob cjob = createImportJob(ClassificationsystemsConstants.ETIM_3_0_DE);
		Assert.assertEquals(cjob.getSuccessResult(), cjob.getResult());
	}

	@Test
	public void testcreateProfiClass_3_0_DE_Import() throws Exception //NOPMD
	{
		final ClassificationImportCronJob cjob = createImportJob(ClassificationsystemsConstants.PROFICLASS_3_0_DE);
		Assert.assertEquals(cjob.getSuccessResult(), cjob.getResult());
	}

	@Test
	public void testcreateProfiClass_4_0_DE_Import() throws Exception //NOPMD
	{
		final ClassificationImportCronJob cjob = createImportJob(ClassificationsystemsConstants.PROFICLASS_4_0_DE);
		Assert.assertEquals(cjob.getSuccessResult(), cjob.getResult());
	}

	@Test
	public void testcreateUnspscImportItems() throws Exception
	{
		final ClassificationImportCronJob cjob = createImportJob(ClassificationsystemsConstants.UNSPSC_5_DE_EN);
		Assert.assertEquals(cjob.getSuccessResult(), cjob.getResult());
	}
}
