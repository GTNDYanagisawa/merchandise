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
package de.hybris.platform.importcockpit.wizzard.mapping.page;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.model.meta.ObjectType;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


@UnitTest
public class MappingTargetPageTest
{
	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private MappingTargetPage page;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	/*
	 * Type hierarchy assumed:
	 * A
	 * -B
	 * C
	 * -D
	 * --E
	 */
	@Test
	public void testFilterNonRootTypes()
	{
		final ObjectType typeA = Mockito.mock(ObjectType.class);
		final ObjectType typeB = Mockito.mock(ObjectType.class);
		final ObjectType typeC = Mockito.mock(ObjectType.class);
		final ObjectType typeD = Mockito.mock(ObjectType.class);
		final ObjectType typeE = Mockito.mock(ObjectType.class);
		when(typeA.getSupertypes()).thenReturn(Collections.<ObjectType> emptySet());
		when(typeA.getSubtypes()).thenReturn(Collections.singleton(typeB));
		when(typeB.getSupertypes()).thenReturn(Collections.singleton(typeA));
		when(typeB.getSubtypes()).thenReturn(Collections.<ObjectType> emptySet());
		when(typeC.getSupertypes()).thenReturn(Collections.<ObjectType> emptySet());
		when(typeC.getSubtypes()).thenReturn(Sets.newHashSet(typeD, typeE));
		when(typeD.getSupertypes()).thenReturn(Collections.singleton(typeC));
		when(typeD.getSubtypes()).thenReturn(Collections.singleton(typeE));
		when(typeE.getSubtypes()).thenReturn(Collections.<ObjectType> emptySet());
		when(typeE.getSupertypes()).thenReturn(Sets.newHashSet(typeC, typeD));
		assertThat(page.filterNonRootTypes(Lists.newArrayList(typeE, typeA, typeB, typeC))).containsExactly(typeA, typeC);
	}

}
