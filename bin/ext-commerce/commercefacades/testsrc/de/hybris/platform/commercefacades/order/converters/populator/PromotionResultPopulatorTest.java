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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.PromotionOrderEntryConsumedData;
import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.promotions.PromotionResultService;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import junit.framework.Assert;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


@UnitTest
public class PromotionResultPopulatorTest
{
	private static final String PROMOTION_DESCRIPTION = "promoDesc";

	@Mock
	private PromotionResultService promotionResultService;
	@Mock
	private Converter<AbstractPromotionModel, PromotionData> promotionsConverter;
	@Mock
	private Converter<PromotionOrderEntryConsumedModel, PromotionOrderEntryConsumedData> promotionOrderEntryConsumedConverter;

	private PromotionResultPopulator promotionResultPopulator = new PromotionResultPopulator();
	private AbstractPopulatingConverter<PromotionResultModel, PromotionResultData> promotionResultConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		promotionResultPopulator.setPromotionOrderEntryConsumedConverter(promotionOrderEntryConsumedConverter);
		promotionResultPopulator.setPromotionResultService(promotionResultService);
		promotionResultPopulator.setPromotionsConverter(promotionsConverter);

		promotionResultConverter = new ConverterFactory<PromotionResultModel, PromotionResultData, PromotionResultPopulator>().create(PromotionResultData.class, promotionResultPopulator);
	}


	@Test
	public void testConvert()
	{
		final PromotionResultModel source = mock(PromotionResultModel.class);
		final ProductPromotionModel productPromotionModel = mock(ProductPromotionModel.class);
		final PromotionData promotionData = mock(PromotionData.class);
		final PromotionOrderEntryConsumedModel consumedModel = mock(PromotionOrderEntryConsumedModel.class);
		final PromotionOrderEntryConsumedData consumedData = mock(PromotionOrderEntryConsumedData.class);

		given(source.getConsumedEntries()).willReturn(Collections.singleton(consumedModel));
		given(promotionOrderEntryConsumedConverter.convert(consumedModel)).willReturn(consumedData);
		given(promotionResultService.getDescription(source)).willReturn(PROMOTION_DESCRIPTION);
		given(source.getPromotion()).willReturn(productPromotionModel);
		given(promotionsConverter.convert(productPromotionModel)).willReturn(promotionData);

		final PromotionResultData result = promotionResultConverter.convert(source);

		Assert.assertEquals(PROMOTION_DESCRIPTION, result.getDescription());
		Assert.assertEquals(1, result.getConsumedEntries().size());
		Assert.assertEquals(promotionData, result.getPromotionData());
		Assert.assertEquals(consumedData, result.getConsumedEntries().get(0));
	}
}
