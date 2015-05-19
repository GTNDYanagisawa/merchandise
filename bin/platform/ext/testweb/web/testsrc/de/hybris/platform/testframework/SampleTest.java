package de.hybris.platform.testframework;

import java.io.NotSerializableException;

import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

public class SampleTest
{

	@Ignore
	@Test
	public void testTwoIgnored()
	{
		//
	}

	@Test
	public void testOne() throws InterruptedException
	{
		//
	}

	@Test
	public void testThreeFail()
	{
		junit.framework.Assert.fail("expected");
	}

	@Test
	public void testFourAssumeFails()
	{
		Assume.assumeTrue(false);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testFiveThrowExpectedException()
	{
		throw new UnsupportedOperationException();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testSixThrowUnExpectedException() throws Exception
	{
		throw new NotSerializableException();
	}
}