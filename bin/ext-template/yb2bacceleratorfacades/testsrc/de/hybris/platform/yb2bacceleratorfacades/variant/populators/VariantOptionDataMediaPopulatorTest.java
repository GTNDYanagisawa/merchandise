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
package de.hybris.platform.yb2bacceleratorfacades.variant.populators;

import com.google.common.collect.Iterables;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.GenericVariantProductModel;
import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.variants.model.VariantProductModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VariantOptionDataMediaPopulatorTest {

    private static final String FORMAT_QUALIFIER = "FORMAT_QUALIFIER";
    private static final String MEDIA_FORMAT_NAME = "Media Format Name";
    private static final String BASE_PRODUCT_CODE = "product";
    @InjectMocks
    private final VariantOptionDataMediaPopulator variantOptionDataMediaPopulator = new VariantOptionDataMediaPopulator<>();
    @Mock
    VariantOptionDataMediaPopulator<VariantProductModel, VariantOptionData> mediaPopulator;
    @Mock
    private ImageFormatMapping acceleratorImageFormatMapping;

    @Mock
    private List<String> imageFormats;


    @Before
    public void setUp() {
        when(acceleratorImageFormatMapping.getMediaFormatQualifierForImageFormat(FORMAT_QUALIFIER)).thenReturn(MEDIA_FORMAT_NAME);

        variantOptionDataMediaPopulator.setImageFormats(new ArrayList<String>());
        variantOptionDataMediaPopulator.getImageFormats().add(FORMAT_QUALIFIER);
    }


    @Test
    public void shouldPopulateMediaInformation() {
        final VariantProductModel variantProductModel = mockNewVariantModel();
        final MediaModel mockMediaModel = mockMediaModel();
        variantProductModel.getOthers().add(mockMediaModel);

        final VariantOptionData optionData = new VariantOptionData();

        variantOptionDataMediaPopulator.populate(variantProductModel, optionData);


        final VariantOptionQualifierData optionQualifierData = Iterables.get(optionData.getVariantOptionQualifiers(), 0);
        assertThat(variantProductModel.getOthers().size(), is(optionData.getVariantOptionQualifiers().size()));
        assertThat(optionQualifierData.getImage().getUrl(), is(mockMediaModel.getURL()));
        assertThat(optionQualifierData.getImage().getFormat(), is(FORMAT_QUALIFIER));

    }


    private MediaModel mockMediaModel() {
        final MediaModel mediaModel = mock(MediaModel.class);
        when(mediaModel.getURL()).thenReturn("media model url");
        final MediaFormatModel mediaFormat = mock(MediaFormatModel.class);
        when(mediaFormat.getName()).thenReturn(MEDIA_FORMAT_NAME);

        when(mediaModel.getMediaFormat()).thenReturn(mediaFormat);

        return mediaModel;
    }

    private GenericVariantProductModel mockNewVariantModel() {
        GenericVariantProductModel variantModel;
        variantModel = new GenericVariantProductModel();
        variantModel.setStockLevels(new HashSet<StockLevelModel>());
        variantModel.setOthers((Collection) new ArrayList<>());
        variantModel.setCode(BASE_PRODUCT_CODE);

        return variantModel;
    }


}
