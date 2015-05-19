package com.hybris.datahub.core.facades.impl.converter;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class LineLinkedMessagesUnitTest
{
	private final ErrorLogParser.LineLinkedErrorMessages messages = new ErrorLogParser.LineLinkedErrorMessages();

	@Test
	public void testMessagesAreEmptyBeforeAnythingIsAdded()
	{
		Assert.assertTrue(messages.mappings().isEmpty());
	}

	@Test
	public void testExtractsLineNumberAndMessageCorrectly()
	{
		messages.add("17.12.2013 15:48:27: ERROR: line 4 at main script: value is NULL for mandatory attribute Category.code");

		Assert.assertEquals("value is NULL for mandatory attribute Category.code", messages.getMessageForLine(4));
	}

	@Test
	public void testDoesNotKeepMessageWhenLineNumberCannotBeParsed()
	{
		messages.add("at main script: value is NULL for mandatory attribute Category.code");

		Assert.assertTrue(messages.mappings().isEmpty());
	}

	@Test
	public void testGetMessageByLineReturnsNullWhenLineNumberNotFound()
	{
		Assert.assertNull("the messages are empty", messages.getMessageForLine(1));

		messages.add("ERROR: line 4 at main script: an error occured");
		assert !messages.mappings().isEmpty() : "expect to have messages now";

		Assert.assertNull(messages.getMessageForLine(1));
	}

	@Test
	public void testDoesNotOverridePreviousMessageWithTheSameLineNumber()
	{
		messages.add("17.12.2013 15:48:27: ERROR: line 4 at main script: value is NULL for mandatory attribute Category.code");
		messages
				.add("17.12.2013 15:48:27: ERROR: line 4 at main script: Exception 'value is NULL for mandatory attribute Product.code' in handling exception: value is NULL for mandatory attribute Product.code");

		Assert.assertEquals("value is NULL for mandatory attribute Category.code", messages.getMessageForLine(4));
	}
}
