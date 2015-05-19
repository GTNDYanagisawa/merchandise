/**
 * 
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests;

import java.io.PrintStream;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;


public class TestNamePrinter extends TestWatcher
{
	private final PrintStream ps;
	private static final String format = "Test: %s.%s\n";

	public TestNamePrinter(final PrintStream ps)
	{
		this.ps = ps;
	}

	@Override
	protected void starting(final Description description)
	{
		ps.printf(format, description.getClassName(), description.getMethodName());
	}


	@Override
	protected void succeeded(final Description description)
	{
		System.out.println("SUCCEEDED");
	}

	@Override
	protected void failed(final Throwable e, final Description description)
	{
		System.out.println("FAILED");
		e.printStackTrace(ps);
	}
}
