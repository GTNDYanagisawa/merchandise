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
package de.hybris.platform.converters.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import junit.framework.Assert;

import org.junit.Test;


/**
 * For testing {@link AbstractConverter}, especially it's optimizations for <lookup-method> injected beans.
 */
@UnitTest
public class AbstractConverterUnitTest
{
	@Test
	public void testUsingBean() throws Exception
	{
		final AbstractConverter<Object, Object> conv = new TestConverter()
		{
			@Override
			protected Object createTarget()
			{
				return "ThisIsANewBeanInstance-" + System.nanoTime();
			}
		};

		final Object source = "SomeSource";
		final Object target = conv.convert(source);

		Assert.assertNotNull(target);
		Assert.assertEquals(String.class, target.getClass());
		Assert.assertTrue(((String) target).startsWith("ThisIsANewBeanInstance-"));
		Assert.assertSame(source, ((TestConverter) conv).populateCalledWithSource);
		Assert.assertSame(target, ((TestConverter) conv).populateCalledWithTarget);

		final Object source2 = "SomeSource2";
		final Object target2 = conv.convert(source2);
		Assert.assertNotNull(target2);
		Assert.assertNotSame(target, target2);
		Assert.assertEquals(String.class, target2.getClass());
		Assert.assertTrue(((String) target2).startsWith("ThisIsANewBeanInstance-"));
		Assert.assertSame(source2, ((TestConverter) conv).populateCalledWithSource);
		Assert.assertSame(target2, ((TestConverter) conv).populateCalledWithTarget);
	}


	@Test
	public void testUsingInjectedClass() throws Exception
	{
		final AbstractConverter<Object, Object> conv = new TestConverter()
		{
			@Override
			protected Object createTarget()
			{
				Assert.fail();
				return null;
			}
		};
		conv.setTargetClass((Class) String.class);

		final Object source = "SomeSource";
		final Object target = conv.convert(source);

		Assert.assertNotNull(target);
		Assert.assertEquals(new String(), target);
		Assert.assertSame(source, ((TestConverter) conv).populateCalledWithSource);
		Assert.assertSame(target, ((TestConverter) conv).populateCalledWithTarget);

		final Object source2 = "SomeSource2";
		final Object target2 = conv.convert(source2);

		Assert.assertNotNull(target);
		Assert.assertEquals(new String(), target2);
		Assert.assertNotSame(target, target2);
		Assert.assertSame(source2, ((TestConverter) conv).populateCalledWithSource);
		Assert.assertSame(target2, ((TestConverter) conv).populateCalledWithTarget);
	}

	@Test
	public void testBeanSetupErrors() throws Exception
	{
		try
		{
			final AbstractConverter illegalConverter = new AbstractConverter()
			{
				@Override
				public void populate(final Object source, final Object target)
				{
					// nope
				}
			};
			illegalConverter.setBeanName("illegalConverter");
			illegalConverter.afterPropertiesSet();
			Assert.fail("Missing targetClass and missing createTarget() should throw IllegalStateException");
		}
		catch (final IllegalStateException e)
		{
			// expected
		}

		final AbstractConverter legalConverterOldWay = new AbstractConverter()
		{
			@Override
			public void populate(final Object source, final Object target)
			{
				// nope
			}

			@Override
			protected Object createTarget()
			{
				return "newBean" + System.nanoTime();
			}
		};
		legalConverterOldWay.setBeanName("legalConverterOldWay");
		legalConverterOldWay.afterPropertiesSet();

		final AbstractConverter legalConverterNewWay = new AbstractConverter()
		{
			@Override
			public void populate(final Object source, final Object target)
			{
				// nope
			}
		};
		legalConverterNewWay.setTargetClass(String.class);
		legalConverterNewWay.setBeanName("legalConverterNewWay");
		legalConverterNewWay.afterPropertiesSet();
	}

	static class TestConverter extends AbstractConverter<Object, Object>
	{
		Object populateCalledWithSource;
		Object populateCalledWithTarget;

		@Override
		public void populate(final Object source, final Object target)
		{
			this.populateCalledWithSource = source;
			this.populateCalledWithTarget = target;
		}
	}
}
