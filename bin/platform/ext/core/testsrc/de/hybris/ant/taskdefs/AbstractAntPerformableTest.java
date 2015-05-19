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
package de.hybris.ant.taskdefs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.jalo.extension.Extension;
import de.hybris.platform.util.JspContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Test which ensures that :
 * <ul>
 * <li>during system <b>initialization</b> there will be passed into {@link de.hybris.platform.core.Initialization} the
 * same set of arguments which is done by default via admin web page</li>
 * <li>during system <b>update</b> there will be passed into {@link de.hybris.platform.core.Initialization} the same set
 * of arguments which is done by default via admin web page. !!!There will be no project data imported!!!</li>
 * </ul>
 */
@UnitTest
public class AbstractAntPerformableTest
{

	@Test
	public void testJspContextAttributesForUpdate()
	{

		final UpdatePlatformAntPerformableImpl perf = new UpdatePlatformAntPerformableImpl("some tenant")
		{
			@Override
			void activate()
			{
				//
			}

			@Override
			void adjustClusterModeIfNeeded()
			{
				//
			}

			@Override
			void innerStopTimerTask(final boolean flag)
			{
				//
			}

			@Override
			protected void validateTenant(final String tenant)
			{
				//ok
			}

		};
		final Map<String, String> expectedParams = new HashMap<String, String>();
		expectedParams.put("init", "Go");
		expectedParams.put("initmethod", "update");
		expectedParams.put("clearhmc", "true");
		expectedParams.put("essential", "true");
		expectedParams.put("localizetypes", "true");
		expectedParams.put("lucenesearch_rebuild.indexes", "true");

		Assert.assertThat(perf.createJspContext(), new JSPContextMatcher(expectedParams));

	}


	@Test
	public void testJspContextAttributesForInit()
	{
		//prepare extension data
		final List<Extension> extensions = new ArrayList<Extension>();
		final Extension hmcExtension = Mockito.mock(Extension.class);
		BDDMockito.given(hmcExtension.getCreatorName()).willReturn("hmc");
		final Extension luceneExtension = Mockito.mock(Extension.class);
		BDDMockito.given(luceneExtension.getCreatorName()).willReturn("lucenesearch");
		BDDMockito.given(luceneExtension.getCreatorParameterNames()).willReturn(Collections.singletonList("rebuild.indexes"));
		BDDMockito.given(luceneExtension.getCreatorParameterDefault(Mockito.anyString())).willReturn("true");
		final Extension servicelayerExtension = Mockito.mock(Extension.class);
		BDDMockito.given(servicelayerExtension.getCreatorName()).willReturn("servicelayer");
		final Extension sampleDataExtension = Mockito.mock(Extension.class);
		BDDMockito.given(sampleDataExtension.getCreatorName()).willReturn("sampledata");
		extensions.add(hmcExtension);
		extensions.add(servicelayerExtension);
		extensions.add(luceneExtension);
		extensions.add(sampleDataExtension);

		final InitPlatformAntPerformableImpl perf = new InitPlatformAntPerformableImpl("some tenant")
		{
			@Override
			void activate()
			{
				//
			}

			@Override
			void adjustClusterModeIfNeeded()
			{
				//
			}

			@Override
			void innerStopTimerTask(final boolean flag)
			{
				//
			}

			@Override
			protected void validateTenant(final String tenant)
			{
				//ok
			}

			@Override
			protected Iterator<Extension> getExtensions()
			{
				return extensions.iterator();
			}
		};
		final Map<String, String> expectedParams = new HashMap<String, String>();
		expectedParams.put("init", "Go");
		expectedParams.put("initmethod", "init");
		expectedParams.put("essential", "true");
		expectedParams.put("localizetypes", "true");
		expectedParams.put("droptables", "true");
		expectedParams.put("clearhmc", "true");
		expectedParams.put("lucenesearch_rebuild.indexes", "true");
		expectedParams.put("hmc_sample", "true");
		expectedParams.put("servicelayer_sample", "true");
		expectedParams.put("lucenesearch_sample", "true");
		expectedParams.put("sampledata_sample", "true");
		Assert.assertThat(perf.createJspContext(), new JSPContextMatcher(expectedParams));

	}


	class JSPContextMatcher extends BaseMatcher<JspContext>
	{
		private final Map<String, String> expected;

		public JSPContextMatcher(final Map<String, String> expected)
		{
			this.expected = expected;
		}

		@Override
		public boolean matches(final Object item)
		{
			final JspContext given = (JspContext) item;
			final Map<String, String[]> givenParamsMap = given.getServletRequest().getParameterMap();
			Assert.assertThat("Given params map should have the same size as expected one ", Integer.valueOf(givenParamsMap.size()),
					is(Integer.valueOf(expected.size())));
			for (final String key : givenParamsMap.keySet())
			{
				Assert.assertThat("Given paramater value should have corresponding value for the same key ", expected.get(key),
						equalTo(given.getServletRequest().getParameter(key)));
			}

			return true;
		}

		@Override
		public void describeTo(final Description description)
		{
			//
		}

	}
}
