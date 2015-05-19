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
 */

package de.hybris.platform.sampledata.setup;

import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.sampledata.constants.ProductcockpitsampledataConstants;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.variants.model.VariantAttributeDescriptorModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.StopWatch;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;


@SystemSetup(extension = ProductcockpitsampledataConstants.EXTENSIONNAME)
public class ProductcockpitsampledataSetup
{
	private List<String> variantTypes = Lists.newArrayList("SweatShirts", "Shoes", "Jeans", "Hats", "MensWear");
	private Map<String, ArrayList<String>> localizedTypeNames = ImmutableMap.of(
			"SweatShirts",Lists.<String>newArrayList("T-Shirt", "T-Shirt"),
			"Shoes", Lists.newArrayList("Shoes", "Schuh"),
			"Jeans", Lists.newArrayList("Jeans", "Jeans"),
			"Hats", Lists.newArrayList("Hats", "M\u00FCtzen"),
			"MensWear", Lists.newArrayList("MensWear", "MensWear"));

	@Resource
	private TypeService typeService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ModelService modelService;

	@SystemSetup(type = SystemSetup.Type.PROJECT, process = SystemSetup.Process.ALL)
	public void createVariantTypes()
	{
		for (final String variantType : variantTypes)
		{
			try
			{
				typeService.getTypeForCode(variantType);
			}
			catch (final UnknownIdentifierException e)
			{
				createVariantType(variantType, getLocalizedVariantTypeName(variantType, "en"),
						getLocalizedVariantTypeName(variantType, "de"), !"Hats".equalsIgnoreCase(variantType));
			}
		}
	}

	private void createVariantType(final String variantType, final String enName, final String deName, final boolean withSize)
	{
		final TypeModel stringTypeModel = typeService.getTypeForCode("java.lang.String");
		final TypeModel locStringTypeModel = typeService.getTypeForCode("localized:java.lang.String");

		final LanguageModel enLanguageModel = commonI18NService.getLanguage("en");
		final LanguageModel deLanguageModel = commonI18NService.getLanguage("de");
		final Locale enLocale = commonI18NService.getLocaleForLanguage(enLanguageModel);
		final Locale deLocale = commonI18NService.getLocaleForLanguage(deLanguageModel);

		final VariantTypeModel variantTypeModel = modelService.create(VariantTypeModel.class);

		variantTypeModel.setName(enName, enLocale);
		variantTypeModel.setName(deName, deLocale);
		variantTypeModel.setCatalogItemType(Boolean.FALSE);
		variantTypeModel.setGenerate(Boolean.FALSE);
		variantTypeModel.setSingleton(Boolean.FALSE);
		variantTypeModel.setCode(variantType);

		modelService.save(variantTypeModel);

		variantTypeModel.setExtensionName(ProductcockpitsampledataConstants.EXTENSIONNAME);

		final List<AttributeDescriptorModel> affectedDescriptors = Lists.newArrayList();
		for (final AttributeDescriptorModel attributeDescriptorModel : variantTypeModel.getInheritedattributedescriptors())
		{
			attributeDescriptorModel.setExtensionName(ProductcockpitsampledataConstants.EXTENSIONNAME);
			affectedDescriptors.add(attributeDescriptorModel);

		}
		modelService.saveAll(affectedDescriptors);

		VariantAttributeDescriptorModel variantAttribute = null;
		if (withSize)
		{
			variantAttribute = createVariantAttributeDescriptor(variantTypeModel, "size", stringTypeModel);
			variantAttribute.setName("Size", enLocale);
			variantAttribute.setName("Gr\u00F6\u00DFe", deLocale);
			variantAttribute.setPosition(0);
			modelService.save(variantAttribute);
		}

		variantAttribute = createVariantAttributeDescriptor(variantTypeModel, "color", locStringTypeModel);
		variantAttribute.setName("Color", enLocale);
		variantAttribute.setName("Farbe", deLocale);
		variantAttribute.setPosition(1);
		modelService.save(variantAttribute);

		modelService.save(variantTypeModel);
	}

	private String getLocalizedVariantTypeName(final String variantType, final String locale)
	{
		String ret = StringUtils.EMPTY;
		if (localizedTypeNames.containsKey(variantType))
		{
			ret = localizedTypeNames.get(variantType).get("en".equalsIgnoreCase(locale) ? 0 : 1);
		}
		return ret;
	}

	private VariantAttributeDescriptorModel createVariantAttributeDescriptor(final VariantTypeModel variantTypeModel,
			final String qualifier, final TypeModel typeModel)
	{

		final VariantAttributeDescriptorModel variantAttribute = modelService.create(VariantAttributeDescriptorModel.class);
		variantAttribute.setQualifier(qualifier);
		variantAttribute.setGenerate(Boolean.FALSE);
		variantAttribute.setPartOf(Boolean.FALSE);
		variantAttribute.setEnclosingType(variantTypeModel);
		variantAttribute.setAttributeType(typeModel);
		variantAttribute.setReadable(Boolean.TRUE);
		variantAttribute.setWritable(Boolean.TRUE);
		variantAttribute.setSearch(Boolean.TRUE);
		variantAttribute.setOptional(Boolean.FALSE);
		variantAttribute.setRemovable(Boolean.FALSE);
		variantAttribute.setExtensionName(ProductcockpitsampledataConstants.EXTENSIONNAME);
		return variantAttribute;

	}
}
