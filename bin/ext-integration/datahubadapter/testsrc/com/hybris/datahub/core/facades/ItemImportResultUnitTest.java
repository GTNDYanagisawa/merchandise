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
package com.hybris.datahub.core.facades;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * A unit test for <code>ItemImportResult</code>
 */
@UnitTest
@SuppressWarnings("javadoc")
public class ItemImportResultUnitTest
{
	private ItemImportResult importResult;

	@Before
	public void setUp()
	{
		importResult = new ItemImportResult();
	}

	@Test
	public void testSuccesfulByDefault()
	{
		Assert.assertTrue(importResult.isSuccessful());
	}

	@Test
	public void testDoesNotContainImportErrorsBeforeTheyAdded()
	{
		final Collection<ImportError> rejected = importResult.getErrors();
		Assert.assertNotNull(rejected);
		Assert.assertTrue(rejected.isEmpty());
	}

	@Test
	public void testAllAddedErrorsCanBeReadBack()
	{
		final ImportError err1 = ImportTestUtils.error("Missing attribute");
		final ImportError err2 = ImportTestUtils.error("Unresolved attribute");
		importResult.addErrors(Arrays.asList(err1, err2));

		final Collection<ImportError> errors = importResult.getErrors();

		Assert.assertEquals(2, errors.size());
		Assert.assertTrue(errors.contains(err1));
		Assert.assertTrue(errors.contains(err2));
	}

	@Test
	public void testSameResultIsReturnedAfterAddingAnErrorToIt()
	{
		final ItemImportResult orig = new ItemImportResult();
		final ItemImportResult returned = orig.addErrors(ImportTestUtils.errors("some error"));

		Assert.assertSame(orig, returned);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddErrorsDoesNotExpectNullBePassedForTheErrorCollection()
	{
		new ItemImportResult().addErrors(null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testImportErrorCollectionCannotBeModifiedFromOutside()
	{
		final ImportError errorBeingAddedAroundItemImportResult = ImportTestUtils.error("Item 1 is rejected");
		importResult.getErrors().add(errorBeingAddedAroundItemImportResult);
	}

	@Test
	public void testResultIsUnsuccessfulWhenAtLeastOneImportErrorWasAdded()
	{
		importResult.addErrors(ImportTestUtils.errors("Some problem"));

		Assert.assertFalse(importResult.isSuccessful());
	}

	@Test
	public void testResultIsUnsuccessfulWhenAnExceptionWasReported()
	{
		final ItemImportResult result = new ItemImportResult("Import exception has occurred");
		Assert.assertFalse(result.isSuccessful());
	}

	@Test
	public void testReportedExceptionCanBeReadBack()
	{
		final Exception ex = new Exception("Import exception has occurred");

		Assert.assertSame(ex.getMessage(), new ItemImportResult(ex.getMessage()).getExceptionMessage());
	}

	@Test
	public void testReportedExceptionWithoutMessageCanBeReadBack()
	{
		final Exception ex = new Exception();

		Assert.assertSame(ex.getClass().getCanonicalName(), new ItemImportResult(ex.getMessage()).getExceptionMessage());
	}

	@Test
	public void toStringContainsSUCCESS_whenResultIsSuccessful()
	{
		final ItemImportResult res = new ItemImportResult();
		assert res.isSuccessful() : "Result with no errors or exception should be successful";

		Assert.assertTrue(res.toString().contains("SUCCESS"));
	}

	@Test
	public void toStringContainsERROR_whenResultIsNotSuccessful()
	{
		final ItemImportResult res = new ItemImportResult().addErrors(ImportTestUtils.errors("an error"));
		assert !res.isSuccessful() : "Result with errors should be unsuccessful";

		Assert.assertTrue(res.toString().contains("ERROR"));
	}

	@Test
	public void toStringPrintsOutExceptionWhenItIsPresentInTheResult()
	{
		final String res = new ItemImportResult((new IOException("cannot read file").getMessage())).toString();

		Assert.assertTrue("Unsuccessful result does not contain ERROR", res.contains("ERROR"));
		Assert.assertTrue("Exception is not printed", res.contains("cannot read file"));
	}
}
