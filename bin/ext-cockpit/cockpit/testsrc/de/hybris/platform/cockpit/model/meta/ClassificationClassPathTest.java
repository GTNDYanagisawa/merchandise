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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.jalo.classification.ClassificationClass;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystem;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystemVersion;
import de.hybris.platform.cockpit.model.meta.impl.ClassificationClassPath;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * This class tests parsing ClassificationClassPaths from string and obtaining string codes from classification class
 * path object.
 * <p>
 * PROC-1626
 * </p>
 */
@UnitTest
public class ClassificationClassPathTest
{
	private static final String SYSTEM_CATALOG_ID = "testCatalogId";
	private static final String SYSTEM_CATALOG_VERSION = "testVersion";

	@Mock
	private ClassificationClass mockClassificationClass;

	@Mock
	private ClassificationSystemVersion mockClassificationVersion;

	@Mock
	private ClassificationSystem mockClasificationSystem;


	@Before
	public void setUp()
	{

		MockitoAnnotations.initMocks(this);
		given(mockClasificationSystem.getId()).willReturn(SYSTEM_CATALOG_ID);
		given(mockClassificationVersion.getClassificationSystem()).willReturn(mockClasificationSystem);
		given(mockClassificationVersion.getVersion()).willReturn(SYSTEM_CATALOG_VERSION);
		given(mockClassificationClass.getSystemVersion()).willReturn(mockClassificationVersion);

	}

	@Test
	public void testParseRegularClassificationClassPath()
	{
		final String classCode = "testCatalogId/testVersion/testClass";
		final ClassificationClassPath clClassPath = new ClassificationClassPath(classCode);
		Assert.assertEquals(SYSTEM_CATALOG_ID, clClassPath.getClassSystem());
		Assert.assertEquals(SYSTEM_CATALOG_VERSION, clClassPath.getClassVersion());
		Assert.assertEquals("testClass", clClassPath.getClassClass());

		given(mockClassificationClass.getCode()).willReturn("testClass");

		Assert.assertEquals(classCode, ClassificationClassPath.getClassCode(mockClassificationClass));
	}

	@Test
	public void testEscapedCharactersClassificationClassPath()
	{
		final String classCode = "testCatalogId/testVersion/myClassPart1 \\/ myClassPart2";
		final ClassificationClassPath clClassPath = new ClassificationClassPath(classCode);
		Assert.assertEquals(SYSTEM_CATALOG_ID, clClassPath.getClassSystem());
		Assert.assertEquals(SYSTEM_CATALOG_VERSION, clClassPath.getClassVersion());
		Assert.assertEquals("myClassPart1 / myClassPart2", clClassPath.getClassClass());

		given(mockClassificationClass.getCode()).willReturn("myClassPart1 / myClassPart2");

		Assert.assertEquals(classCode, ClassificationClassPath.getClassCode(mockClassificationClass));

		final String classCode2 = "testCatalogId/testVersion/myClassPart1\\/myClassPart2";
		final ClassificationClassPath clClassPath2 = new ClassificationClassPath(classCode2);
		Assert.assertEquals(SYSTEM_CATALOG_ID, clClassPath2.getClassSystem());
		Assert.assertEquals(SYSTEM_CATALOG_VERSION, clClassPath2.getClassVersion());
		Assert.assertEquals("myClassPart1/myClassPart2", clClassPath2.getClassClass());

		given(mockClassificationClass.getCode()).willReturn("myClassPart1/myClassPart2");

		Assert.assertEquals(classCode2, ClassificationClassPath.getClassCode(mockClassificationClass));

		final String classCode3 = "testCatalogId/testVersion/myClassPart1\\/myClassPart2 \\/ myClassPart3";
		final ClassificationClassPath clClassPath3 = new ClassificationClassPath(classCode3);
		Assert.assertEquals(SYSTEM_CATALOG_ID, clClassPath3.getClassSystem());
		Assert.assertEquals(SYSTEM_CATALOG_VERSION, clClassPath3.getClassVersion());
		Assert.assertEquals("myClassPart1/myClassPart2 / myClassPart3", clClassPath3.getClassClass());

		given(mockClassificationClass.getCode()).willReturn("myClassPart1/myClassPart2 / myClassPart3");

		Assert.assertEquals(classCode3, ClassificationClassPath.getClassCode(mockClassificationClass));
	}


}
