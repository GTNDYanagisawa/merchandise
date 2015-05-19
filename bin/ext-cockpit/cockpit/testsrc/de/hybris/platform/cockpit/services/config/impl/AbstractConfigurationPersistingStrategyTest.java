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
package de.hybris.platform.cockpit.services.config.impl;

import static org.mockito.Mockito.when;

import de.hybris.platform.cockpit.model.meta.ObjectTemplate;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.core.model.c2l.LanguageModel;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Base class for all configuration persisting strategy tests.
 */
@Ignore
public abstract class AbstractConfigurationPersistingStrategyTest
{
	@Mock
	protected PropertyDescriptor propName;
	@Mock
	protected PropertyDescriptor propCode;
	@Mock
	protected PropertyDescriptor propCatalogVersion;
	@Mock
	protected PropertyDescriptor propUnitName;
	@Mock
	protected ObjectTemplate templateProduct;
	@Mock
	protected ObjectTemplate templateProductCpu;
	@Mock
	protected ObjectTemplate templateProductVariant;
	@Mock
	protected PropertyDescriptor propMinimal;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		when(propCode.getQualifier()).thenReturn("Propduct.code");
		when(propCode.getOccurence()).thenReturn(PropertyDescriptor.Occurrence.REQUIRED);
		when(propCatalogVersion.getQualifier()).thenReturn("Propduct.catalogVersion");
		when(propCatalogVersion.getOccurence()).thenReturn(PropertyDescriptor.Occurrence.REQUIRED);
		when(propName.getQualifier()).thenReturn("Propduct.name");
		when(Boolean.valueOf(propName.isLocalized())).thenReturn(Boolean.TRUE);
		when(propName.getOccurence()).thenReturn(PropertyDescriptor.Occurrence.OPTIONAL);
		when(propUnitName.getQualifier()).thenReturn("Product.unit.name");
		when(Boolean.valueOf(propUnitName.isLocalized())).thenReturn(Boolean.TRUE);
		when(propUnitName.getOccurence()).thenReturn(PropertyDescriptor.Occurrence.OPTIONAL);
		when(templateProduct.getCode()).thenReturn("Propduct");
		when(templateProductCpu.getCode()).thenReturn("Propduct.CPU");
		when(templateProductVariant.getCode()).thenReturn("VariantPropduct");
		when(propMinimal.getQualifier()).thenReturn("Propduct.simpleText");
		when(propMinimal.getOccurence()).thenReturn(PropertyDescriptor.Occurrence.OPTIONAL);
	}

	@After
	public void tearDown() throws Exception
	{
		//nothing to do
	}

	protected Map<LanguageModel, String> createLabels(final String id)
	{
		final Map<LanguageModel, String> map = new HashMap<LanguageModel, String>(2);
		LanguageModel lang = new LanguageModel();
		lang.setIsocode("en");
		map.put(lang, id + "_en");
		lang = new LanguageModel();
		lang.setIsocode("de");
		map.put(lang, id + "_de");

		return map;
	}

}
