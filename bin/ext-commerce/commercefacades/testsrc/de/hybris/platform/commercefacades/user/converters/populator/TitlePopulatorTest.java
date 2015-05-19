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
package de.hybris.platform.commercefacades.user.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.user.TitleModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import junit.framework.Assert;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


@UnitTest
public class TitlePopulatorTest
{
	private AbstractPopulatingConverter<TitleModel, TitleData> titleConverter =
				new ConverterFactory<TitleModel, TitleData, TitlePopulator>().create(TitleData.class, new TitlePopulator());

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testConvert()
	{
		final TitleModel titleModel = mock(TitleModel.class);
		given(titleModel.getCode()).willReturn("titleCode");
		given(titleModel.getName()).willReturn("titleName");
		final TitleData titleData = titleConverter.convert(titleModel);
		Assert.assertEquals("titleCode", titleData.getCode());
		Assert.assertEquals("titleName", titleData.getName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertSourceNull()
	{
		titleConverter.convert(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertPrototypeNull()
	{
		final TitleModel titleModel = mock(TitleModel.class);
		titleConverter.convert(titleModel, null);
	}

}
