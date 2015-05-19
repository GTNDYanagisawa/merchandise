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
package de.hybris.platform.importcockpit.services.impex.generator.operations.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.importcockpit.model.mappingview.MappingModel;
import de.hybris.platform.importcockpit.model.mappingview.SourceColumnModel;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.ComposedTypeMappingLine;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.MappingLineModel;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AbstractImpexGeneratorOperationTest
{

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private AbstractImpexGeneratorOperation aigo;

	@Mock
	private MappingModel mapping;

	@Mock
	private ComposedTypeMappingLine mappingLine;

	@Mock
	private AttributeDescriptorModel nonCollectionAttributeDescriptor;

	@Mock
	private ComposedTypeModel composedTypeModel;

	@Mock
	private SourceColumnModel sourceColumnModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetCatalogVersionsForMapping()
	{
		when(mapping.getEntries()).thenReturn(Collections.<MappingLineModel> singletonList(mappingLine));
		when(mappingLine.getAttributeDescriptor()).thenReturn(nonCollectionAttributeDescriptor);
		when(nonCollectionAttributeDescriptor.getAttributeType()).thenReturn(composedTypeModel);
		when(composedTypeModel.getCatalogVersionAttribute()).thenReturn(null);
		assertThat(aigo.getCatalogVersionsForMapping(null, mapping)).containsOnly("");
	}

	@Test
	public void testHasMappedSource()
	{
		when(mappingLine.getSource()).thenReturn(sourceColumnModel);
		when(sourceColumnModel.getId()).thenReturn("its_id");
		assertThat(aigo.hasMappedSource(mappingLine)).isTrue();

		when(sourceColumnModel.getId()).thenReturn("");
		when(mappingLine.getChildren()).thenReturn(Collections.<MappingLineModel> emptyList());
		assertThat(aigo.hasMappedSource(mappingLine)).isFalse();

		when(mappingLine.getSource()).thenReturn(null);
		when(mappingLine.getChildren()).thenReturn(Collections.<MappingLineModel> emptyList());
		assertThat(aigo.hasMappedSource(mappingLine)).isFalse();

		final ArrayList<MappingLineModel> values = new ArrayList<MappingLineModel>();
		final MappingLineModel noSourceMappingLine = Mockito.mock(MappingLineModel.class);
		when(noSourceMappingLine.getSource()).thenReturn(null);
		when(noSourceMappingLine.getChildren()).thenReturn(Collections.<MappingLineModel> emptyList());
		final MappingLineModel mappingLineWithSource = Mockito.mock(MappingLineModel.class);
		when(mappingLineWithSource.getSource()).thenReturn(sourceColumnModel);

		values.add(noSourceMappingLine);
		values.add(mappingLineWithSource);

		when(sourceColumnModel.getId()).thenReturn("its_id");
		when(mappingLine.getChildren()).thenReturn(values);
		assertThat(aigo.hasMappedSource(mappingLine)).isTrue();

	}

}
