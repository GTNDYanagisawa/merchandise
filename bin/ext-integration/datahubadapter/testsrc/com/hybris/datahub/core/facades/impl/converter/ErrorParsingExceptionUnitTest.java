package com.hybris.datahub.core.facades.impl.converter;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class ErrorParsingExceptionUnitTest
{
	@Test
	public void testInstantiatedExceptionContainsTheRootCause()
	{
		final Exception rootCause = new IOException();
		final ErrorParsingException ex = new ErrorParsingException(rootCause);

		Assert.assertSame(rootCause, ex.getCause());
	}
}
