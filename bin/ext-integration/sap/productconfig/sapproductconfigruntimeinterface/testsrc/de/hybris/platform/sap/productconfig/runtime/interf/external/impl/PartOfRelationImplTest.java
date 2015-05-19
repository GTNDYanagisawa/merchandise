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
package de.hybris.platform.sap.productconfig.runtime.interf.external.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@UnitTest
public class PartOfRelationImplTest
{
	PartOfRelationImpl classUnderTest = new PartOfRelationImpl();



	@Test
	public void testPartOfRelationImplAttributes()
	{
		final String author = "8";
		classUnderTest.setAuthor(author);
		assertEquals(author, classUnderTest.getAuthor());
		final String instId = "1";
		classUnderTest.setInstId(instId);
		assertEquals(instId, classUnderTest.getInstId());
		final String objectKey = "KD990MIX";
		classUnderTest.setObjectKey(objectKey);
		assertEquals(objectKey, classUnderTest.getObjectKey());
		final String classType = "300";
		classUnderTest.setClassType(classType);
		assertEquals(classType, classUnderTest.getClassType());
		final String objectType = "MARA";
		classUnderTest.setObjectType(objectType);
		assertEquals(objectType, classUnderTest.getObjectType());
		final String parentInstId = "2";
		classUnderTest.setParentInstId(parentInstId);
		assertEquals(parentInstId, classUnderTest.getParentInstId());
		final String posNr = "10";
		classUnderTest.setPosNr(posNr);
		assertEquals(posNr, classUnderTest.getPosNr());



	}

}
