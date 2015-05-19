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

package com.hybris.backoffice.cockpitng.classification;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.UnlocalizedFeature;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.expression.AccessException;

public class FeatureCodeExtractorTest
{
    final ClassificationPropertyAccessor classificationPropertyAccessor = new ClassificationPropertyAccessor();

    private  FeatureCodeExtractor prepare(final String systemId, final String systemVersion, final String classificationClass, final String attribute)
    {
        ClassAttributeAssignmentModel attributeAssignmentModel = Mockito.mock(ClassAttributeAssignmentModel.class);
        final ClassificationSystemVersionModel classificationSystemVersionModel = Mockito.mock(ClassificationSystemVersionModel.class);
        final ClassificationSystemModel classificationSystemModel = Mockito.mock(ClassificationSystemModel.class);

        Mockito.when(attributeAssignmentModel.getSystemVersion()).thenReturn(classificationSystemVersionModel);
        Mockito.when(classificationSystemVersionModel.getCatalog()).thenReturn(classificationSystemModel);
        Mockito.when(classificationSystemModel.getId()).thenReturn(systemId);
        Mockito.when(classificationSystemVersionModel.getVersion()).thenReturn(systemVersion);

        final ClassificationClassModel classificationClassModel = Mockito.mock(ClassificationClassModel.class);
        Mockito.when(classificationClassModel.getCode()).thenReturn(classificationClass);

        final ClassificationAttributeModel classificationAttributeModel = Mockito.mock(ClassificationAttributeModel.class);
        Mockito.when(classificationAttributeModel.getCode()).thenReturn(attribute);
        Mockito.when(attributeAssignmentModel.getClassificationClass()).thenReturn(classificationClassModel);
        Mockito.when(attributeAssignmentModel.getClassificationAttribute()).thenReturn(classificationAttributeModel);

        final Feature feature = new UnlocalizedFeature(attributeAssignmentModel);
        final FeatureCodeExtractor extractor = new FeatureCodeExtractor(feature);
        return extractor;
    }

    @Test
    public void test() throws AccessException
    {
        boolean canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("aa","bb","cc","dd").getCode());
        Assertions.assertThat(canRead).isTrue();

        canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("a","b","c","d").getCode());
        Assertions.assertThat(canRead).isTrue();

        canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("","bb","cc","dd").getCode());
        Assertions.assertThat(canRead).isFalse();
        canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("aa","","cc","dd").getCode());
        Assertions.assertThat(canRead).isFalse();
        canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("aa","bb","","dd").getCode());
        Assertions.assertThat(canRead).isFalse();
        canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("aa","bb","cc","").getCode());
        Assertions.assertThat(canRead).isFalse();

        canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("a.a","b.b","c.c","d.d").getCode());
        Assertions.assertThat(canRead).isTrue();

        canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("a.a.a","b.b.b","c.c.c","d.d.d").getCode());
        Assertions.assertThat(canRead).isTrue();

        canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("a/a/a","b/b/b","c/c/c","d/d/d").getCode());
        Assertions.assertThat(canRead).isTrue();

        canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("a/a.a","b/b.b","c/c.c","d/d.d").getCode());
        Assertions.assertThat(canRead).isTrue();

        final String code = prepare("{a}/{b}/{c}.{d}", "{a}/{b}/{c}.{d}", "{a}/{b}/{c}.{d}", "{a}/{b}/{c}.{d}").getCode();
        canRead = classificationPropertyAccessor.canRead(null, new Object(), code);
        Assertions.assertThat(canRead).isTrue();

        canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("a{aa","b}b","ccc}/","{ddd").getCode());
        Assertions.assertThat(canRead).isTrue();

    }
}
