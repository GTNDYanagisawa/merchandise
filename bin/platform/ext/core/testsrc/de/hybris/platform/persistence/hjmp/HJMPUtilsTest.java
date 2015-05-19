package de.hybris.platform.persistence.hjmp;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.test.TestThreadsHolder;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.util.Config;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class HJMPUtilsTest extends HybrisJUnit4Test
{
	private final PropertyConfigSwitcher optimisticLockingConfigSwitcher = new PropertyConfigSwitcher(
			"hjmp.throw.concurrent.modification.exceptions");

	@Before
	public void setUp() throws Exception
	{
		optimisticLockingConfigSwitcher.switchToValue("false");
	}

	@After
	public void tearDown() throws Exception
	{
		optimisticLockingConfigSwitcher.switchBackToDefault();
	}

	@Test
	public void shouldRunDifferentThreadsWithDifferentOptimisticLockingSettings() throws Exception
	{
		// given
		final TestThreadsHolder<TestRunnerWithStandardOptimisticLocking> holder1 = new TestThreadsHolder<>(10,
				new TestRunnerWithStandardOptimisticLocking(), true);
		final TestThreadsHolder<TestRunnerWithChangedOptimisticLocking> holder2 = new TestThreadsHolder<>(10,
				new TestRunnerWithChangedOptimisticLocking(), true);

		// when
		holder1.startAll();
		holder2.waitForAll(5, TimeUnit.SECONDS);

		// then
		assertThat(holder1.getErrors()).isEmpty();
		assertThat(holder2.getErrors()).isEmpty();
	}

	private class TestRunnerWithStandardOptimisticLocking implements Runnable
	{

		@Override
		public void run()
		{
			assertThat(Config.getBoolean("hjmp.throw.concurrent.modification.exceptions", true)).isFalse();
		}
	}

	private class TestRunnerWithChangedOptimisticLocking implements Runnable
	{

		@Override
		public void run()
		{
			try
			{
				HJMPUtils.enableOptimisticLocking();
				assertThat(Config.getBoolean("hjmp.throw.concurrent.modification.exceptions", true)).isTrue();
			}
			finally
			{
				HJMPUtils.disableOptimisticLocking();
				assertThat(Config.getBoolean("hjmp.throw.concurrent.modification.exceptions", true)).isFalse();
			}
		}
	}


}
