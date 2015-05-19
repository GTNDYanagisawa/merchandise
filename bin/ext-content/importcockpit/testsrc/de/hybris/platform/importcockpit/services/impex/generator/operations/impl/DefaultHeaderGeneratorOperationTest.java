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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.importcockpit.enums.ImpexImportMode;
import de.hybris.platform.importcockpit.model.mappingview.MappingModel;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.AttributeCollectionMappingLine;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.ComposedTypeMappingLine;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.MappingLineModel;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.TranslatorMappingLine;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.impl.AtomicTypeMapping;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.impl.EnumTypeMapping;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.impl.LocalizedAtomicTypeMapping;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.impl.LocalizedTypeMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


@UnitTest
public class DefaultHeaderGeneratorOperationTest
{

	private static final String LANG_EN = "en";
	private static final String TEST_FILE_NAME = "myTestFileName";
	private static final String ATOMIC_NON_LOCALIZED_UNIQUE_CODE = "atomicNonLocalizedUnique";
	private static final String ATOMIC_LOCALIZED_UNIQUE_CODE = "en";
	private static final String ENUM_NON_LOCALIZED_NON_UNIQUE_CODE = "enumNonLocalizedNonUnique";
	private static final String CV = "catalogVersion";
	private static final String CLASSIFCATION_CLASS_CODE = "ccCode";
	private static final String ATTRIBUTE_CODE = "Product";
	private static final String TEST_CV = "Test";
	private static final String TEST_CATALOG_ID = "TestCatalog";
	private static final String TEST_TRANSLATOR_CLASS = "de.hybris.importcockpit.translator.TestTranslator";
	private static final String LOCALIZED_ENUM_CODE = "localizedEnum";

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private DefaultHeaderGeneratorOperation operation;
	@Mock
	private MappingLineModel atomicMappingLineModel;
	@Mock
	private ComposedTypeMappingLine composedMappingLineModel;
	@Mock
	private AtomicTypeMapping subLineAtomicNonLocalized;
	@Mock
	private LocalizedTypeMapping subLineAtomicLocalized;
	@Mock
	private LocalizedTypeMapping subLineNonAtomicLocalized;
	@Mock
	private EnumTypeMapping enumLineNonLocalized;
	@Mock
	private LocalizedAtomicTypeMapping enumLineLocalized;
	@Mock
	private ClassificationClassModel classificationClass;
	@Mock
	private MappingModel mappingModel;
	@Mock
	private CatalogVersionModel testCatalogVersion;
	@Mock
	private CatalogModel testCatalog;
	@Mock
	private AttributeCollectionMappingLine attributeLine;
	@Mock
	private ComposedTypeModel baseTypeModel;
	@Mock
	private CatalogTypeService catalogTypeService;
	@Mock
	private AttributeDescriptorModel cvAttribute;
	@Mock
	private MappingLineModel localizedEnumsparent;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		when(atomicMappingLineModel.getChildren()).thenReturn(
				Collections.<MappingLineModel> singletonList(subLineAtomicNonLocalized));
		when(Boolean.valueOf(atomicMappingLineModel.isAtomic())).thenReturn(Boolean.TRUE);

		when(Boolean.valueOf(composedMappingLineModel.isAtomic())).thenReturn(Boolean.FALSE);
		when(composedMappingLineModel.getMapping()).thenReturn(mappingModel);
		when(composedMappingLineModel.getAttributeCode()).thenReturn(ATTRIBUTE_CODE);
		when(composedMappingLineModel.getAttributeDescriptor()).thenReturn(cvAttribute);

		when(Boolean.valueOf(subLineAtomicNonLocalized.hasChildren())).thenReturn(Boolean.TRUE);
		when(classificationClass.getCode()).thenReturn(CLASSIFCATION_CLASS_CODE);
		when(mappingModel.getCatalogVersion()).thenReturn(testCatalogVersion);
		when(mappingModel.getBaseTypeModel()).thenReturn(baseTypeModel);
		when(mappingModel.getImpexImportMode()).thenReturn(ImpexImportMode.INSERT);
		when(baseTypeModel.getCatalogVersionAttribute()).thenReturn(cvAttribute);

		when(cvAttribute.getQualifier()).thenReturn(CV);
		when(cvAttribute.getUnique()).thenReturn(Boolean.TRUE);
		when(cvAttribute.getAttributeType()).thenReturn(baseTypeModel);

		when(baseTypeModel.getCode()).thenReturn(ProductModel._TYPECODE);
		when(testCatalogVersion.getVersion()).thenReturn(TEST_CV);
		when(testCatalogVersion.getCatalog()).thenReturn(testCatalog);
		when(testCatalog.getId()).thenReturn(TEST_CATALOG_ID);

		when(Boolean.valueOf(subLineAtomicNonLocalized.isAtomic())).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(subLineAtomicNonLocalized.isUnique())).thenReturn(Boolean.TRUE);
		when(subLineAtomicNonLocalized.getAttributeCode()).thenReturn(ATOMIC_NON_LOCALIZED_UNIQUE_CODE);

		when(Boolean.valueOf(subLineAtomicLocalized.isAtomic())).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(subLineAtomicLocalized.isUnique())).thenReturn(Boolean.TRUE);
		when(subLineAtomicLocalized.getAttributeCode()).thenReturn(ATOMIC_LOCALIZED_UNIQUE_CODE);

		when(Boolean.valueOf(enumLineNonLocalized.isAtomic())).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(enumLineNonLocalized.isUnique())).thenReturn(Boolean.FALSE);
		when(enumLineNonLocalized.getAttributeCode()).thenReturn(ENUM_NON_LOCALIZED_NON_UNIQUE_CODE);

		when(Boolean.valueOf(enumLineLocalized.isAtomic())).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(enumLineLocalized.isUnique())).thenReturn(Boolean.FALSE);
		when(enumLineLocalized.getAttributeCode()).thenReturn(LANG_EN);
		when(enumLineLocalized.getChildren()).thenReturn(Collections.<MappingLineModel> singletonList(enumLineNonLocalized));
		when(enumLineLocalized.getParent()).thenReturn(localizedEnumsparent);
		when(localizedEnumsparent.getAttributeCode()).thenReturn(LOCALIZED_ENUM_CODE);

		when(Boolean.valueOf(subLineNonAtomicLocalized.isAtomic())).thenReturn(Boolean.FALSE);
		when(Boolean.valueOf(attributeLine.isAtomic())).thenReturn(Boolean.FALSE);
		operation.setCatalogTypeService(catalogTypeService);
		when(Boolean.valueOf(catalogTypeService.isCatalogVersionAwareType(ProductModel._TYPECODE))).thenReturn(Boolean.TRUE);
	}

	@Test
	public void testGenerateAttributes4ProductHeaderInvalidMappingLine()
	{
		try
		{
			operation.generateAttributes4ProductHeader(null);
			fail(IllegalArgumentException.class + " expected");
		}
		catch (final IllegalArgumentException e)
		{
			//expected
		}
	}

	@Test
	public void testGenerateAttributes4ProductHeaderMissingId()
	{
		try
		{
			final TranslatorMappingLine translatorMappingLine = Mockito.mock(TranslatorMappingLine.class);
			when(attributeLine.getChildren()).thenReturn(Collections.singletonList((MappingLineModel) translatorMappingLine));
			when(attributeLine.getAttributeCode()).thenReturn(StringUtils.EMPTY);
			operation.generateAttributes4ProductHeader(attributeLine);
			fail(IllegalStateException.class + " expected");
		}
		catch (final IllegalStateException e)
		{
			//expected
		}
	}

	@Test
	public void testGenerateAttributes4ProductHeader()
	{
		final TranslatorMappingLine translatorMappingLine = Mockito.mock(TranslatorMappingLine.class);
		when(attributeLine.getChildren()).thenReturn(Collections.singletonList((MappingLineModel) translatorMappingLine));
		when(translatorMappingLine.getAttributeCode()).thenReturn(ATTRIBUTE_CODE);
		when(translatorMappingLine.getClassificationClass()).thenReturn(classificationClass);

		final Collection<String> headerLine = operation.generateAttributes4ProductHeader(attributeLine);
		assertEquals(1, headerLine.size());
		final String line = headerLine.iterator().next();
		final Pattern compile = Pattern.compile("@" + ATTRIBUTE_CODE + "\\[.*,class='" + CLASSIFCATION_CLASS_CODE + "'\\]");
		assertTrue(compile.matcher(line).matches());
	}

	@Test
	public void testGenerateCatSysVersionMacro()
	{
		final String macro = operation.generateCatSysVersionMacro(mappingModel, CV);
		assertThat(macro).isEqualTo(
				"$" + CV + "=" + CV + "(catalog(id[default='" + TEST_CATALOG_ID + "']),version[default='" + TEST_CV + "'])");
	}

	@Test
	public void testGenerateMacro4ProductAttributes()
	{
		final ComposedTypeMappingLine lvl1 = Mockito.mock(ComposedTypeMappingLine.class);
		final ComposedTypeMappingLine lvl2_cat = Mockito.mock(ComposedTypeMappingLine.class);
		final AtomicTypeMapping lvl2_ver = Mockito.mock(AtomicTypeMapping.class);
		final TranslatorMappingLine translatorMappingLine = Mockito.mock(TranslatorMappingLine.class);
		when(translatorMappingLine.getTranslator()).thenReturn(TEST_TRANSLATOR_CLASS);
		when(Boolean.valueOf(lvl1.hasChildren())).thenReturn(Boolean.TRUE);
		final List<MappingLineModel> childrenLvl1 = Lists.newArrayList(lvl1, translatorMappingLine);
		when(attributeLine.getChildren()).thenReturn(childrenLvl1);
		when(translatorMappingLine.getParent()).thenReturn(attributeLine);
		when(lvl1.getChildren()).thenReturn(Lists.newArrayList(lvl2_cat, lvl2_ver));
		when(lvl1.getParent()).thenReturn(attributeLine);
		when(lvl2_cat.getTranslator()).thenReturn(TEST_TRANSLATOR_CLASS);
		when(lvl1.getAttributeCode()).thenReturn("systemVersion");
		when(lvl2_ver.getAttributeCode()).thenReturn("version");
		when(lvl2_ver.getValue()).thenReturn(TEST_CV);
		when(lvl2_cat.getAttributeCode()).thenReturn("catalog");
		when(Boolean.valueOf(lvl2_cat.hasChildren())).thenReturn(Boolean.TRUE);
		final MappingLineModel catalogAttLine = Mockito.mock(MappingLineModel.class);
		when(lvl2_cat.getChildren()).thenReturn(Collections.singletonList(catalogAttLine));
		when(catalogAttLine.getValue()).thenReturn(TEST_CATALOG_ID);
		final String macro = operation.generateMacro4ProductAttributes(attributeLine);
		assertThat(macro).isEqualTo(
				"$YCL=system='" + TEST_CATALOG_ID + "',version='" + TEST_CV + "',translator=" + TEST_TRANSLATOR_CLASS + "\n");
	}

	@Test
	public void testGenerateImportBeanShellStatement()
	{
		assertThat(operation.generateImportBeanShellStatement(TEST_FILE_NAME)).isEqualTo(
				"#% impex.includeExternalDataMedia(\"" + TEST_FILE_NAME + ".csv\", \"UTF-8\", ';', 0, -1);");
	}

	@Test
	public void testGenerateImpexHeaderBeanShellStatement()
	{
		final Map<Integer, String> result = operation.generateImpexHeaderBeanShellStatement(TEST_FILE_NAME);
		final HashMap<Integer, String> map = Maps.newHashMap();
		map.put(Integer.valueOf(0), "#% impex.includeExternalDataMedia(\"" + TEST_FILE_NAME + ".csv\", \"UTF-8\", ';', 0, -1);");
		assertThat(result).isEqualTo(map);
	}

	@Test
	public void testGenerateImpexMainHeader()
	{
		when(mappingModel.getImpexImportMode()).thenReturn(ImpexImportMode.INSERT);
		when(mappingModel.getEntries()).thenReturn(
				Lists.<MappingLineModel> newArrayList(subLineAtomicNonLocalized, subLineAtomicLocalized, subLineNonAtomicLocalized,
						attributeLine, enumLineNonLocalized, enumLineLocalized));
		final Map<Integer, String> lines = operation.generateImpexMainHeader(mappingModel, Sets.newHashSet(atomicMappingLineModel));
		assertThat(lines.values()).contains("INSERT " + ProductModel._TYPECODE);
		System.out.println(lines.values());
		assertThat(lines.values()).contains(ATOMIC_NON_LOCALIZED_UNIQUE_CODE + "[unique=true]");
		assertThat(lines.values()).contains(ENUM_NON_LOCALIZED_NON_UNIQUE_CODE + "(code)");
		assertThat(lines.values()).contains(LOCALIZED_ENUM_CODE + "[lang=" + LANG_EN + "]");
	}

	@Test
	public void testGenerateImpexHeaderComposedField()
	{
		final HashSet<MappingLineModel> mappingLines = Sets.<MappingLineModel> newLinkedHashSet();
		when(Boolean.valueOf(composedMappingLineModel.isAllowNull())).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(composedMappingLineModel.isUnique())).thenReturn(Boolean.TRUE);
		when(Integer.valueOf(composedMappingLineModel.getLevel())).thenReturn(Integer.valueOf(0));
		when(cvAttribute.getInitial()).thenReturn(Boolean.TRUE);
		final String macro = operation.generateImpexHeaderComposedField(composedMappingLineModel, mappingLines);
		assertThat(macro).matches(
				"[\\w]+\\(.*\\$catalogVersion\\[unique=true\\]\\).*\\[unique=true\\].*\\[forceWrite=true\\].*\\[allowNull=true\\].*");
	}

	@Test
	public void testGenerateImpexHeaderLocalizedSubField()
	{
		when(composedMappingLineModel.getChildren()).thenReturn(
				Collections.<MappingLineModel> singletonList(subLineAtomicLocalized));
		when(subLineAtomicLocalized.getParent()).thenReturn(composedMappingLineModel);
		final String macro = operation.generateImpexHeaderLocalizedSubField(composedMappingLineModel);
		assertThat(macro).matches("\\w+.*\\[lang=en\\].*");
	}

	@Test
	public void testGenerateImpexSubHeaders()
	{
		when(composedMappingLineModel.getCreationType()).thenReturn(baseTypeModel);
		when(composedMappingLineModel.getMapping()).thenReturn(mappingModel);
		final List<Map<Integer, String>> lines = operation.generateImpexSubHeaders(Sets
				.<MappingLineModel> newHashSet(composedMappingLineModel));
		assertThat(lines.size()).isEqualTo(3);
		assertThat(lines.get(0).size()).isEqualTo(2);
		assertThat(lines.get(0).get(Integer.valueOf(0))).isEqualTo("INSERT Product");
		assertThat(lines.get(0).get(Integer.valueOf(1))).isEqualTo("$catalogVersion[unique=true]");
		assertThat(lines.get(1).size()).isEqualTo(1);
		assertThat(lines.get(1).get(Integer.valueOf(0))).isEqualTo(
				"#% impex.includeExternalDataMedia(\"Product.csv\", \"UTF-8\", ';', 0, -1);");
		assertThat(lines.get(2).size()).isEqualTo(0);
	}
}
