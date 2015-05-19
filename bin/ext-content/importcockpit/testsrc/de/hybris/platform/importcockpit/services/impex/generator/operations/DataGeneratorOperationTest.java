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
package de.hybris.platform.importcockpit.services.impex.generator.operations;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.importcockpit.enums.ComposedAttribImportMode;
import de.hybris.platform.importcockpit.model.mappingview.MappingModel;
import de.hybris.platform.importcockpit.model.mappingview.SourceColumnModel;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.CollectionMappingLine;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.MappingLineModel;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.impl.AtomicTypeMapping;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.impl.DefaultComposedTypeMapping;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.impl.LocalizedTypeMapping;
import de.hybris.platform.importcockpit.services.impex.generator.operations.impl.DefaultDataGeneratorOperation;
import de.hybris.platform.importcockpit.services.mapping.MappingLineService;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;


public class DataGeneratorOperationTest
{

	private Map<Integer, String> productLine;

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private DefaultDataGeneratorOperation operation;
	@Mock
	private GeneratorOperationState operationState;
	@Mock
	private MappingModel mappingModel;
	@Mock
	private AtomicTypeMapping productCodeMappingLineModel;
	@Mock
	private LocalizedTypeMapping catalogVersionTypeMaping;
	@Mock
	private DefaultComposedTypeMapping composedCatalogVersionTypeMapping;
	@Mock
	private DefaultComposedTypeMapping composedCatalogTypeMapping;
	@Mock
	private AtomicTypeMapping catalogLineModel;
	@Mock
	private AtomicTypeMapping catalogVersionLineModel;
	@Mock
	private SourceColumnModel productCodeSourceColumn;
	@Mock
	private MappingLineService mappingLineService;
	@Mock
	private CatalogTypeService catalogTypeService;
	@Mock
	private SourceColumnModel catalogLineModelsource;
	@Mock
	private SourceColumnModel catalogVersionLineModel_source;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		productLine = new HashMap<Integer, String>();
		productLine.put(Integer.valueOf(0), "code_0001");
		productLine.put(Integer.valueOf(1), "default");
		productLine.put(Integer.valueOf(2), "staged");
		productLine = Collections.unmodifiableMap(productLine);

		operation.setMappingLineService(mappingLineService);
		operation.setCatalogTypeService(catalogTypeService);

		when(Boolean.valueOf(productCodeMappingLineModel.isAtomic())).thenReturn(Boolean.TRUE);
		when(productCodeMappingLineModel.getValue()).thenReturn(null);
		when(productCodeMappingLineModel.getSource()).thenReturn(productCodeSourceColumn);
		when(productCodeSourceColumn.getId()).thenReturn("0");
	}

	@Test
	public void testGenerateDataLineForMappingModel()
	{

		when(Boolean.valueOf(mappingLineService.isLineOfNumberType(productCodeMappingLineModel))).thenReturn(Boolean.FALSE);

		when(mappingModel.getEntries()).thenReturn(Lists.newArrayList((MappingLineModel) productCodeMappingLineModel));
		final Map<Integer, String> result = operation.generateDataLineForMappingModel(productLine, mappingModel, operationState);
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.get(Integer.valueOf(0))).isEqualTo(productLine.get(Integer.valueOf(0)));
	}

	@Test
	public void testGenerateDataLineForComposedEntry()
	{
		when(Boolean.valueOf(catalogVersionTypeMaping.isAtomic())).thenReturn(Boolean.FALSE);
		when(catalogVersionTypeMaping.getChildren()).thenReturn(
				Lists.newArrayList((MappingLineModel) catalogLineModel, (MappingLineModel) catalogVersionLineModel));

		when(Boolean.valueOf(catalogLineModel.isAtomic())).thenReturn(Boolean.TRUE);
		when(catalogLineModel.getValue()).thenReturn(null);
		when(catalogLineModel.getSource()).thenReturn(catalogLineModelsource);
		when(catalogLineModelsource.getId()).thenReturn("1");

		when(Boolean.valueOf(catalogVersionLineModel.isAtomic())).thenReturn(Boolean.TRUE);
		when(catalogVersionLineModel.getValue()).thenReturn(null);
		when(catalogVersionLineModel.getSource()).thenReturn(catalogVersionLineModel_source);
		when(catalogVersionLineModel_source.getId()).thenReturn("2");

		final String result = operation.generateDataLineForComposedEntry(productLine, catalogVersionTypeMaping, true,
				operationState);
		assertThat(result).isEqualTo(productLine.get(Integer.valueOf(1)) + ":" + productLine.get(Integer.valueOf(2)));
	}

	@Test
	public void testGenerateDataLineForComposedEntry_ComposedTypeMappingLine()
	{
		final Map<Integer, String> inputDataLine = new HashMap<Integer, String>();
		inputDataLine.put(Integer.valueOf(0), "1234");
		inputDataLine.put(Integer.valueOf(1), "topseller,specialoffers");
		final String catalogCode = "default";
		final String catalogVersionCode = "staged";

		when(Boolean.valueOf(mappingModel.isCatalogVersionOption())).thenReturn(Boolean.TRUE);

		final DefaultComposedTypeMapping composedTypeMapping = Mockito.mock(DefaultComposedTypeMapping.class);
		when(composedTypeMapping.getComposedAttribImportMode()).thenReturn(ComposedAttribImportMode.SELECT);
		when(composedTypeMapping.getMapping()).thenReturn(mappingModel);
		when(composedTypeMapping.getChildren()).thenReturn(
				Lists.newArrayList(composedCatalogVersionTypeMapping, (MappingLineModel) productCodeMappingLineModel));
		when(Boolean.valueOf(composedTypeMapping.hasChildren())).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(composedTypeMapping.isComposed())).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(composedTypeMapping.isCollectionElement())).thenReturn(Boolean.TRUE);

		final CollectionMappingLine collectionMappingLine = Mockito.mock(CollectionMappingLine.class);
		when(Boolean.valueOf(collectionMappingLine.hasChildren())).thenReturn(Boolean.TRUE);
		when(collectionMappingLine.getChildren()).thenReturn(Lists.newArrayList((MappingLineModel) composedTypeMapping));
		when(composedTypeMapping.getParent()).thenReturn(collectionMappingLine);

		when(Boolean.valueOf(productCodeMappingLineModel.isAtomic())).thenReturn(Boolean.TRUE);
		when(productCodeMappingLineModel.getValue()).thenReturn(null);
		when(productCodeMappingLineModel.getSource()).thenReturn(productCodeSourceColumn);
		when(productCodeSourceColumn.getId()).thenReturn("1");
		when(Boolean.valueOf(productCodeMappingLineModel.isUnique())).thenReturn(Boolean.TRUE);
		when(productCodeMappingLineModel.getParent()).thenReturn(composedTypeMapping);

		when(Boolean.valueOf(composedCatalogVersionTypeMapping.isAtomic())).thenReturn(Boolean.FALSE);
		when(composedCatalogVersionTypeMapping.getComposedAttribImportMode()).thenReturn(ComposedAttribImportMode.SELECT);
		when(composedCatalogVersionTypeMapping.getChildren()).thenReturn(
				Lists.newArrayList(composedCatalogTypeMapping, (MappingLineModel) catalogVersionLineModel));
		when(Boolean.valueOf(composedCatalogVersionTypeMapping.hasChildren())).thenReturn(Boolean.TRUE);
		when(composedCatalogVersionTypeMapping.getParent()).thenReturn(composedTypeMapping);
		when(composedCatalogVersionTypeMapping.getMapping()).thenReturn(mappingModel);
		when(Boolean.valueOf(composedCatalogVersionTypeMapping.isComposed())).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(composedCatalogVersionTypeMapping.isUnique())).thenReturn(Boolean.TRUE);

		when(Boolean.valueOf(composedCatalogTypeMapping.hasChildren())).thenReturn(Boolean.TRUE);
		when(composedCatalogTypeMapping.getComposedAttribImportMode()).thenReturn(ComposedAttribImportMode.SELECT);
		when(composedCatalogTypeMapping.getChildren()).thenReturn(Lists.newArrayList((MappingLineModel) catalogLineModel));
		when(composedCatalogTypeMapping.getMapping()).thenReturn(mappingModel);
		when(Boolean.valueOf(composedCatalogTypeMapping.isUnique())).thenReturn(Boolean.TRUE);
		when(composedCatalogTypeMapping.getParent()).thenReturn(composedCatalogVersionTypeMapping);

		when(Boolean.valueOf(catalogLineModel.isAtomic())).thenReturn(Boolean.TRUE);
		when(catalogLineModel.getValue()).thenReturn("default");
		when(Boolean.valueOf(catalogLineModel.isUnique())).thenReturn(Boolean.TRUE);
		when(catalogLineModel.getParent()).thenReturn(composedCatalogTypeMapping);

		when(Boolean.valueOf(catalogVersionLineModel.isAtomic())).thenReturn(Boolean.TRUE);
		when(catalogVersionLineModel.getValue()).thenReturn("staged");
		when(Boolean.valueOf(catalogVersionLineModel.isUnique())).thenReturn(Boolean.TRUE);
		when(catalogVersionLineModel.getParent()).thenReturn(composedCatalogVersionTypeMapping);

		final AttributeDescriptorModel attributeDescriptor = Mockito.mock(AttributeDescriptorModel.class);
		final TypeModel typeModel = Mockito.mock(TypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);
		when(composedCatalogTypeMapping.getAttributeDescriptor()).thenReturn(attributeDescriptor);
		when(composedCatalogVersionTypeMapping.getAttributeDescriptor()).thenReturn(attributeDescriptor);

		final Map<String, File> mockMap = Mockito.mock(Map.class);
		when(Boolean.valueOf(mockMap.containsKey(Mockito.anyString()))).thenReturn(Boolean.TRUE);
		when(operationState.getSubDataTempFiles()).thenReturn(mockMap);

		final String[] multiValue = inputDataLine.get(Integer.valueOf(1)).split("\\s*,\\s*");

		final String result = operation
				.generateDataLineForComposedEntry(inputDataLine, collectionMappingLine, true, operationState);
		assertThat(result).isEqualTo(
				catalogCode + ":" + catalogVersionCode + ":" + multiValue[0] + "," + catalogCode + ":" + catalogVersionCode + ":"
						+ multiValue[1]);
	}

	@Test
	public void testGenerateDataLineForProductAttribEntry()
	{
		final String result = operation.generateDataLineForProductAttribEntry(productLine, productCodeMappingLineModel);
		assertThat(result).isEqualTo(productLine.get(Integer.valueOf(0)));
	}
}
