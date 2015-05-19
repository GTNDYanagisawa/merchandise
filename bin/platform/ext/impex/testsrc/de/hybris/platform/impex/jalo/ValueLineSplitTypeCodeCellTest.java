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
package de.hybris.platform.impex.jalo;

import de.hybris.platform.impex.jalo.imp.ValueLine;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Test;


/**
 * Test ValueLine's typeCode cell splitting.
 */
public class ValueLineSplitTypeCodeCellTest extends HybrisJUnit4Test
{

	private static final String NULL = "null";
	private static final String LOCATION = "location";
	private static final String TYPECODE = "typeCode";
	private static final String UNRESOLVED_COMMENT = "unresolvedComment";
	private static final String PROCESSED_ITEM_PK = "1234567890";
	private static final String NOT_UNRECOVERABLE = "";
	private static final String UNRECOVERABLE = "true";
	private static final int LINE_NUMBER = 123;
	private static final String TYPECODE_NOT_UNRECOVERABLE_STRING = TYPECODE + "," + PROCESSED_ITEM_PK + "," + NOT_UNRECOVERABLE
			+ "," + UNRESOLVED_COMMENT;
	private static final String TYPECODE_NOT_UNRECOVERABLE_WITHOUT_COMMENT_STRING = TYPECODE + "," + PROCESSED_ITEM_PK + ","
			+ NOT_UNRECOVERABLE;

	private static final String TYPECODE_UNRECOVERABLE_STRING = TYPECODE + "," + PROCESSED_ITEM_PK + "," + UNRECOVERABLE + ","
			+ UNRESOLVED_COMMENT;
	private static final String TYPECODE_UNRECOVERABLE_WITHOUT_COMMENT_STRING = TYPECODE + "," + PROCESSED_ITEM_PK + ","
			+ UNRECOVERABLE;


	@Test
	public void testOnlyTypecode()
	{
		final ValueLine valueLine = new ValueLine(null, TYPECODE, Collections.EMPTY_MAP, LINE_NUMBER, LOCATION);
		Assert.assertEquals(TYPECODE, valueLine.getTypeCode());
		Assert.assertEquals(LINE_NUMBER, valueLine.getLineNumber());
		Assert.assertEquals(LOCATION, valueLine.getLocation());
		Assert.assertEquals(null, valueLine.getProcessedItemPK());
		Assert.assertEquals(false, valueLine.isUnrecoverable());
		Assert.assertEquals(false, valueLine.isUnresolved());
		valueLine.markUnresolved();
		Assert.assertEquals(true, valueLine.isUnresolved());
		Assert.assertEquals(NULL, valueLine.getUnresolvedReason());
	}

	@Test
	public void testTypeCodeNotUnrecoverableWithoutComment()
	{
		final ValueLine valueLine = new ValueLine(null, TYPECODE_NOT_UNRECOVERABLE_WITHOUT_COMMENT_STRING, Collections.EMPTY_MAP,
				LINE_NUMBER, LOCATION);
		Assert.assertEquals(TYPECODE, valueLine.getTypeCode());
		Assert.assertEquals(LINE_NUMBER, valueLine.getLineNumber());
		Assert.assertEquals(LOCATION, valueLine.getLocation());
		Assert.assertEquals(PROCESSED_ITEM_PK, valueLine.getProcessedItemPK().toString());
		Assert.assertEquals(false, valueLine.isUnrecoverable());
		Assert.assertEquals(false, valueLine.isUnresolved());
		valueLine.markUnresolved();
		Assert.assertEquals(true, valueLine.isUnresolved());
		Assert.assertEquals(NULL, valueLine.getUnresolvedReason());
	}

	@Test
	public void testTypeCodeNotUnrecoverableWithComment()
	{
		final ValueLine valueLine = new ValueLine(null, TYPECODE_NOT_UNRECOVERABLE_STRING, Collections.EMPTY_MAP, LINE_NUMBER,
				LOCATION);
		Assert.assertEquals(TYPECODE, valueLine.getTypeCode());
		Assert.assertEquals(LINE_NUMBER, valueLine.getLineNumber());
		Assert.assertEquals(LOCATION, valueLine.getLocation());
		Assert.assertEquals(PROCESSED_ITEM_PK, valueLine.getProcessedItemPK().toString());
		Assert.assertEquals(false, valueLine.isUnrecoverable());
		Assert.assertEquals(false, valueLine.isUnresolved());
		valueLine.markUnresolved();
		Assert.assertEquals(true, valueLine.isUnresolved());
		Assert.assertEquals(UNRESOLVED_COMMENT, valueLine.getUnresolvedReason());
	}

	@Test
	public void testTypeCodeUnrecoverableWithoutComment()
	{
		final ValueLine valueLine = new ValueLine(null, TYPECODE_UNRECOVERABLE_WITHOUT_COMMENT_STRING, Collections.EMPTY_MAP,
				LINE_NUMBER, LOCATION);
		Assert.assertEquals(TYPECODE, valueLine.getTypeCode());
		Assert.assertEquals(LINE_NUMBER, valueLine.getLineNumber());
		Assert.assertEquals(LOCATION, valueLine.getLocation());
		Assert.assertEquals(PROCESSED_ITEM_PK, valueLine.getProcessedItemPK().toString());
		Assert.assertEquals(true, valueLine.isUnrecoverable());
		Assert.assertEquals(false, valueLine.isUnresolved());
		valueLine.markUnresolved();
		Assert.assertEquals(true, valueLine.isUnresolved());
		Assert.assertEquals(NULL, valueLine.getUnresolvedReason());
	}

	@Test
	public void testTypeCodeUnrecoverableWithComment()
	{
		final ValueLine valueLine = new ValueLine(null, TYPECODE_UNRECOVERABLE_STRING, Collections.EMPTY_MAP, LINE_NUMBER, LOCATION);
		Assert.assertEquals(TYPECODE, valueLine.getTypeCode());
		Assert.assertEquals(LINE_NUMBER, valueLine.getLineNumber());
		Assert.assertEquals(LOCATION, valueLine.getLocation());
		Assert.assertEquals(PROCESSED_ITEM_PK, valueLine.getProcessedItemPK().toString());
		Assert.assertEquals(true, valueLine.isUnrecoverable());
		Assert.assertEquals(false, valueLine.isUnresolved());
		valueLine.markUnresolved();
		Assert.assertEquals(true, valueLine.isUnresolved());
		Assert.assertEquals(UNRESOLVED_COMMENT, valueLine.getUnresolvedReason());
	}

}
