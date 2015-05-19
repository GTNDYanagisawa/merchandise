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
package de.hybris.platform.importcockpit.components.contentbrowser.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.impl.ItemAttributePropertyDescriptor;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class PropertyComparatorTest
{

	private PropertyComparator comparator;

	@Mock
	private TypeService typeService;

	@Mock
	private AttributeDescriptorModel obligatoryAttDescModel;

	@Mock
	private AttributeDescriptorModel nonObligatoryAttDescModel;

	@Mock
	PropertyDescriptor source;

	@Mock
	PropertyDescriptor target;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		comparator = new PropertyComparator(Locale.US);
		comparator.setTypeService(typeService);
		when(obligatoryAttDescModel.getOptional()).thenReturn(Boolean.TRUE);
		when(nonObligatoryAttDescModel.getOptional()).thenReturn(Boolean.FALSE);
	}

	@Test
	public void testCompareTrivial()
	{
		assertThat(comparator.compare(null, null)).isEqualTo(0);
		assertThat(comparator.compare(mock(PropertyDescriptor.class), null)).isEqualTo(1);
		assertThat(comparator.compare(null, mock(PropertyDescriptor.class))).isEqualTo(-1);
	}

	@Test
	public void testCompareDifferentLabels()
	{
		when(source.getName()).thenReturn("A");
		when(target.getName()).thenReturn("B");

		comparator.setTypeService(typeService);

		assertThat(comparator.compare(source, target)).isEqualTo(-1);
		assertThat(comparator.compare(target, source)).isEqualTo(1);
	}

	@Test
	public void testCompareSameLabels()
	{
		when(source.getName()).thenReturn("A");
		when(source.getQualifier()).thenReturn("source");

		when(target.getName()).thenReturn("A");
		when(target.getQualifier()).thenReturn("target");

		final TypeService typeService = mock(TypeService.class);
		when(typeService.getAttributeCodeFromPropertyQualifier("A.source")).thenReturn("source");
		when(typeService.getAttributeCodeFromPropertyQualifier("A.target")).thenReturn("target");

		assertThat(comparator.compare(source, target)).isEqualTo(-1);
		assertThat(comparator.compare(target, source)).isEqualTo(1);
	}

	@Test
	public void testCompareSameLabelsAndAttributeNames()
	{
		when(source.getName()).thenReturn("A");
		when(source.getQualifier()).thenReturn("TypeA.source");

		when(target.getName()).thenReturn("A");
		when(target.getQualifier()).thenReturn("TypeB.source");

		final TypeService typeService = mock(TypeService.class);
		when(typeService.getAttributeCodeFromPropertyQualifier("TypeA.source")).thenReturn("source");
		when(typeService.getAttributeCodeFromPropertyQualifier("TypeB.source")).thenReturn("source");

		assertThat(comparator.compare(source, target)).isEqualTo(-1);
		assertThat(comparator.compare(target, source)).isEqualTo(1);
	}

	@Test
	public void testObligatoryAttributes()
	{
		source = mock(ItemAttributePropertyDescriptor.class);
		when(source.getName()).thenReturn("B");
		when(source.getQualifier()).thenReturn("source");
		when(((ItemAttributePropertyDescriptor) source).getFirstAttributeDescriptor()).thenReturn(nonObligatoryAttDescModel);
		when(target.getName()).thenReturn("A");
		when(target.getQualifier()).thenReturn("target");

		assertThat(comparator.compare(source, target)).isEqualTo(-1);
		assertThat(comparator.compare(target, source)).isEqualTo(1);

		when(((ItemAttributePropertyDescriptor) source).getFirstAttributeDescriptor()).thenReturn(obligatoryAttDescModel);

		assertThat(comparator.compare(source, target)).isEqualTo(1);
		assertThat(comparator.compare(target, source)).isEqualTo(-1);

		target = mock(ItemAttributePropertyDescriptor.class);
		when(target.getName()).thenReturn("A");
		when(target.getQualifier()).thenReturn("target");
		when(((ItemAttributePropertyDescriptor) target).getFirstAttributeDescriptor()).thenReturn(nonObligatoryAttDescModel);

		assertThat(comparator.compare(source, target)).isEqualTo(1);
		assertThat(comparator.compare(target, source)).isEqualTo(-1);

		when(((ItemAttributePropertyDescriptor) target).getFirstAttributeDescriptor()).thenReturn(obligatoryAttDescModel);
		assertThat(comparator.compare(source, target)).isEqualTo(1);
		assertThat(comparator.compare(target, source)).isEqualTo(-1);
	}

}
