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
package de.hybris.platform.cockpit.model.meta;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.cockpit.model.meta.impl.ClassAttributePropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.impl.ClassificationClassPath;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests proper handling of escaped characters (".", "/") in ClassAttributePropertyDescriptor
 */
@IntegrationTest
public class ClassAttributePropertyDescriptorTest extends ServicelayerTest
{

	private static final String MY_CLASS = "myClass";
	private static final String VERSION = "1.0";
	private static final String SYSTEM = "system";

	@Resource
	private ModelService modelService;

	/**
	 * Set up creates 2 classification classes and 3 classification attributes in total.<br>
	 * system | 1.0 | myClass (myAttr, my.attr) ; bla/bla (my.super.attr)
	 * 
	 */
	@Before
	public void setUp() throws Exception
	{

		final ClassificationSystemModel csm = modelService.create(ClassificationSystemModel.class);
		csm.setId(SYSTEM);

		final ClassificationSystemVersionModel cvm = modelService.create(ClassificationSystemVersionModel.class);
		cvm.setCatalog(csm);
		cvm.setVersion(VERSION);

		final ClassificationClassModel ccm = modelService.create(ClassificationClassModel.class);
		ccm.setCatalogVersion(cvm);
		ccm.setCode(MY_CLASS);

		final ClassificationClassModel blablaClass = modelService.create(ClassificationClassModel.class);
		blablaClass.setCatalogVersion(cvm);
		blablaClass.setCode("bla/bla");

		final ClassificationClassModel pcMac = modelService.create(ClassificationClassModel.class);
		pcMac.setCatalogVersion(cvm);
		pcMac.setCode("pc/mac");

		final ClassificationAttributeModel cam = new ClassificationAttributeModel();
		cam.setCode("myAttr");
		cam.setSystemVersion(cvm);

		//Attribute with dot
		final ClassificationAttributeModel cam1 = new ClassificationAttributeModel();
		cam1.setCode("my.attr");
		cam1.setSystemVersion(cvm);

		final ClassificationAttributeModel cam2 = new ClassificationAttributeModel();
		cam2.setCode("my.super.attr");
		cam2.setSystemVersion(cvm);

		final ClassificationAttributeModel cpu = new ClassificationAttributeModel();
		cpu.setCode("cpu");
		cpu.setSystemVersion(cvm);

		final ClassAttributeAssignmentModel caam = modelService.create(ClassAttributeAssignmentModel.class);
		caam.setClassificationAttribute(cam);
		caam.setClassificationClass(ccm);
		caam.setSystemVersion(cvm);


		final ClassAttributeAssignmentModel caam1 = modelService.create(ClassAttributeAssignmentModel.class);
		caam1.setClassificationAttribute(cam1);
		caam1.setClassificationClass(ccm);
		caam1.setSystemVersion(cvm);

		final ClassAttributeAssignmentModel caam2 = modelService.create(ClassAttributeAssignmentModel.class);
		caam2.setClassificationAttribute(cam2);
		caam2.setClassificationClass(blablaClass);
		caam2.setSystemVersion(cvm);

		final ClassAttributeAssignmentModel caam3 = modelService.create(ClassAttributeAssignmentModel.class);
		caam3.setClassificationAttribute(cpu);
		caam3.setClassificationClass(pcMac);
		caam3.setSystemVersion(cvm);


		modelService.saveAll(csm, cvm, ccm, blablaClass, cam, caam, cam1, caam1, cam2, caam2, pcMac, cpu, caam3);

	}

	@Test
	public void testRegularDescriptor()
	{
		final String qualifier = "system/1.0/myClass.myAttr";
		final ClassAttributePropertyDescriptor clAttrDesc = new ClassAttributePropertyDescriptor(qualifier);
		Assert.assertEquals("myAttr", clAttrDesc.getAttributeQualifier());
		Assert.assertEquals("system/1.0/myClass", ClassificationClassPath.getClassCode(clAttrDesc.getClassificationClass()));

	}

	@Test
	public void testDescriptorWithEscapedDots()
	{
		final String qualifier = "system/1.0/myClass.my\\.attr";
		final ClassAttributePropertyDescriptor clAttrDesc = new ClassAttributePropertyDescriptor(qualifier);
		Assert.assertEquals("my.attr", clAttrDesc.getAttributeQualifier());
		Assert.assertEquals("system/1.0/myClass", ClassificationClassPath.getClassCode(clAttrDesc.getClassificationClass()));
	}

	@Test
	public void testDescriptorWith2EscapedDotsAndSlashInClassName()
	{
		final String qualifier = "system/1.0/bla\\/bla.my\\.super\\.attr";
		final ClassAttributePropertyDescriptor clAttrDesc = new ClassAttributePropertyDescriptor(qualifier);
		Assert.assertEquals("my.super.attr", clAttrDesc.getAttributeQualifier());
		Assert.assertEquals("system/1.0/bla\\/bla", ClassificationClassPath.getClassCode(clAttrDesc.getClassificationClass()));
		Assert.assertEquals("bla/bla", clAttrDesc.getAttributeAssignment().getClassificationClass().getCode());
	}

	@Test
	public void testEscapedDotsInClassVersion()
	{
		final String qualifier = "system/1\\.0/pc\\/mac.cpu";
		final ClassAttributePropertyDescriptor clAttrDesc = new ClassAttributePropertyDescriptor(qualifier);
		Assert.assertEquals("cpu", clAttrDesc.getAttributeQualifier());
		Assert.assertEquals("pc/mac", clAttrDesc.getAttributeAssignment().getClassificationClass().getCode());
		Assert.assertEquals("1.0", clAttrDesc.getAttributeAssignment().getSystemVersion().getVersion());
		Assert.assertEquals("system", clAttrDesc.getAttributeAssignment().getSystemVersion().getCatalog().getId());
		Assert.assertEquals("system/1.0/pc\\/mac", ClassificationClassPath.getClassCode(clAttrDesc.getClassificationClass()));
	}


}
