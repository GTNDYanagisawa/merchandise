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
package de.hybris.platform.catalog.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests {@link ProductFeaturePrepareInterceptor} interceptor.
 */
@UnitTest
public class ProductFeaturePrepareInterceptorTest
{

	private ProductFeaturePrepareInterceptor preparer;
	private ProductFeatureModel productFeature;

	private static final String CLASSIFICATION_CLASS_CODE = "myTestClass";
	private static final String CLASSIFICATION_ATTRIBUTE_CODE = "MyTestAttribute";
	private static final String TEST_CATALOG_ID = "MyCatalog";
	private static final String TEST_VERSION = "myVersion";


	@Before
	public void setUp() throws Exception
	{
		preparer = new ProductFeaturePrepareInterceptor();
		productFeature = new ProductFeatureModel();
		final ClassAttributeAssignmentModel caa = new ClassAttributeAssignmentModel();
		final ClassificationClassModel clClass = new ClassificationClassModel();
		final ClassificationAttributeModel clAttribute = new ClassificationAttributeModel();

		clClass.setCode(CLASSIFICATION_CLASS_CODE);
		clAttribute.setCode(CLASSIFICATION_ATTRIBUTE_CODE);

		caa.setClassificationAttribute(clAttribute);
		caa.setClassificationClass(clClass);

		productFeature.setClassificationAttributeAssignment(caa);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.catalog.impl.ProductFeaturePrepareInterceptor#onPrepare(java.lang.Object, de.hybris.platform.servicelayer.interceptor.InterceptorContext)}
	 * .
	 * 
	 * @throws InterceptorException
	 */
	@Test
	public void testOnPrepareForNullCatalogVersion() throws InterceptorException
	{
		//test preparation of product feature qualifier if system catalog version of the assignment is null:
		final String expected = CLASSIFICATION_CLASS_CODE + "." + CLASSIFICATION_ATTRIBUTE_CODE.toLowerCase().intern();

		preparer.onPrepare(productFeature, null);

		Assert.assertEquals("Unexpected qualifier field", expected, productFeature.getQualifier());
	}

	@Test
	public void testOnPrepare() throws InterceptorException
	{

		final ClassificationSystemModel testCatalog = new ClassificationSystemModel();
		testCatalog.setId(TEST_CATALOG_ID);

		final ClassificationSystemVersionModel testVersion = new ClassificationSystemVersionModel();
		testVersion.setCatalog(testCatalog);
		testVersion.setVersion(TEST_VERSION);

		productFeature.getClassificationAttributeAssignment().setSystemVersion(testVersion);
		//test preparation of product feature qualifier if system catalog version of the assignment is not null:

		final String expected = TEST_CATALOG_ID + '/' + TEST_VERSION + '/' + CLASSIFICATION_CLASS_CODE + '.'
				+ CLASSIFICATION_ATTRIBUTE_CODE.toLowerCase().intern();

		preparer.onPrepare(productFeature, null);

		Assert.assertEquals("Unexpected qualifier field", expected, productFeature.getQualifier());
	}
}
