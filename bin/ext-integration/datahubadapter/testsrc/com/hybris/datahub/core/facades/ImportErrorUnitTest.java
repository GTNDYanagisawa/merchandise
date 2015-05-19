package com.hybris.datahub.core.facades;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class ImportErrorUnitTest
{
	@Test
	public void testSourceLineIsEmptyWhenSourceLineWasNotFound()
	{
		final ImportError err = ImportError.create(null, "an error happened");
		Assert.assertEquals("", err.getScriptLine());
	}

	@Test
	public void testItemTypeIsNullWhenSourceLineWasNotFound()
	{
		final ImportError err = ImportError.create(null, "something is wrong");
		Assert.assertNull(err.getItemType());
	}

	@Test
	public void testItemIdIsNullWhenSourceLineWasNotFound()
	{
		final ImportError err = ImportError.create(null, "can't find the script");
		Assert.assertNull(err.getCanonicalItemId());
	}

	@Test
	public void testErrorClassificationIsPerformedOnlyBasedOnTheMessage()
	{
		final ImportError err = ImportError.create(null, "unknown attribute 'age' in header 'INSERT_UPDATE Product");

		Assert.assertEquals(ErrorCode.UNKNOWN_ATTRIBUTE, err.getCode());
	}

	@Test
	public void testErrorTypeIsNullWhenErrorCannotBeClassifiedBasedOnTheMessage()
	{
		final ImportError err = ImportError.create(null, "some unexpected message");

		Assert.assertNull(err.getType());
	}

	@Test
	public void testErrorTypeIsHeaderWhenMessageIndicatesAProblemWithTheImpexHeader()
	{
		final ImportError err = ImportError.create(null, "unknown attribute '$catalogVersion' in header 'INSERT_UPDATE Product'");
		Assert.assertEquals(ErrorType.HEADER, err.getType());
	}

	@Test
	public void testNoErrorIsCreatedWhenMessageIsNull()
	{
		final ImportError err = ImportError.create(";1234;dress;", null);

		Assert.assertNull(err);
	}

	@Test
	public void testErrorTypeIsHeaderWhenSourceLineIsAnImpexHeaderLine()
	{
		final ImportError err = ImportError.create("INSERT Product;;code;$catalogVersion",
				"unknown attribute '$catalogVersion' in header 'INSERT_UPDATE Product'");

		Assert.assertEquals(ErrorType.HEADER, err.getType());
	}

	@Test
	public void testItemTypeIsPopulatedWhenSourceLineIsAnUpdateHeader()
	{
		final ImportError err = ImportError.create("UPDATE Product;;code;$catalogVersion",
				"unknown attribute '$catalogVersion' in header 'INSERT_UPDATE Product'");

		Assert.assertEquals("Product", err.getItemType());
	}

	@Test
	public void testItemTypeIsPopulatedWhenSourceLineIsAnImpexInsertUpdateHeader()
	{
		final ImportError err = ImportError.create("INSERT_UPDATE Product;;code;$catalogVersion",
				"unknown attribute '$catalogVersion' in header 'INSERT_UPDATE Product'");

		Assert.assertEquals("Product", err.getItemType());
	}

	@Test
	public void testItemTypeIsPopulatedWhenSourceLineIsAnImpexInsertHeader()
	{
		final ImportError err = ImportError.create("INSERT Product;;code;$catalogVersion",
				"unknown attribute '$catalogVersion' in header 'INSERT_UPDATE Product'");

		Assert.assertEquals("Product", err.getItemType());
	}

	@Test
	public void testItemTypeNotAvailableWhenSourceLineIsNeitherDataNorHeader()
	{
		final ImportError err = ImportError.create("CREATE Product;;code;$catalogVersion", "unknown command 'CREATE'");

		Assert.assertNull(err.getItemType());
	}

	@Test
	public void testItemIdNotAvailableWhenSourceLineIsAnImpexHeaderLine()
	{
		final ImportError err = ImportError.create("INSERT_UPDATE Product;;code;$catalogVersion",
				"unknown attribute '$catalogVersion' in header 'INSERT_UPDATE Product'");

		Assert.assertNull(err.getCanonicalItemId());
	}

	@Test
	public void testItemIdIsPopulatedWhenSourceLineIsAnImpexDataLine()
	{
		final ImportError err = ImportError.create(";123456789;;Material;3912;",
				"item reference a for attribute CatalogVersion.version does not provide enough values at position 1");

		Assert.assertEquals(new Long(123456789), err.getCanonicalItemId());
	}

	@Test
	public void testItemTypeIsNullWhenImpexLineIsADataLine()
	{
		final ImportError err = ImportError.create(";123;;Material;3912;",
				"item reference a for attribute CatalogVersion.version does not provide enough values at position 1");

		Assert.assertNull(err.getItemType());
	}

	@Test
	public void testCreatedErrorHasTheSpecifiedErrorMessage()
	{
		final ImportError err = ImportError.create(";;;Material;3912;", "This is an error");

		Assert.assertEquals("This is an error", err.getMessage());
	}

	@Test
	public void testCreatedErrorHasTheSpecifiedScriptLine()
	{
		final ImportError err = ImportError.create(";;;Material;3912;", "This is an error");

		Assert.assertEquals(";;;Material;3912;", err.getScriptLine());
	}
}
