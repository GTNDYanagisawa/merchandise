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
package de.hybris.platform.cms2.model.restrictions;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@IntegrationTest
public class CMSCatalogRestrictionModelTest extends ServicelayerBaseTest // NOPMD
{

	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;
	@Mock
	private CatalogModel catalog1;
	@Mock
	private CatalogModel catalog2;

	/**
	 * 
	 */
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test method for {@link de.hybris.platform.cms2.model.restrictions.CMSCatalogRestrictionModel#getDescription()}.
	 */
	@Test
	public void shouldHaveDynamicAttributeDescriptor()
	{
		//given
		final ComposedTypeModel type = typeService.getComposedTypeForClass(CMSCatalogRestrictionModel.class);

		//when
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(type,
				CMSCategoryRestrictionModel.DESCRIPTION);

		//then
		assertThat(attributeDescriptor).isNotNull();
		assertThat(attributeDescriptor.getAttributeHandler()).isEqualTo("catalogRestrictionDynamicDescription");
	}

	/**
	 * Test method for {@link de.hybris.platform.cms2.model.restrictions.CMSCatalogRestrictionModel#getDescription()}.
	 */
	@Test
	public void shouldReturnRestrictionDescription()
	{
		//given
		final Collection<CatalogModel> catalogs = new ArrayList<CatalogModel>();
		catalogs.add(catalog1);
		catalogs.add(catalog2);
		given(catalog1.getName()).willReturn("Name1");
		given(catalog2.getName()).willReturn("Name2");
		given(catalog1.getId()).willReturn("Id1");
		given(catalog2.getId()).willReturn("Id2");
		final CMSCatalogRestrictionModel restriction = modelService.create(CMSCatalogRestrictionModel.class);
		restriction.setCatalogs(catalogs);

		//when
		final String description = restriction.getDescription();

		//then
		assertThat(description).isNotNull();
		assertThat(description).isEqualTo("Page only applies on catalogs: Name1 (Id1); Name2 (Id2);");
	}

}
