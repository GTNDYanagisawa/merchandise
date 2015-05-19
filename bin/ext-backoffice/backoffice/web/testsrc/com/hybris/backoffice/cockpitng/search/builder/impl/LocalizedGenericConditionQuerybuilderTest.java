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
package com.hybris.backoffice.cockpitng.search.builder.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.KeywordModel;
import de.hybris.platform.core.GenericCondition;
import de.hybris.platform.core.GenericConditionList;
import de.hybris.platform.core.GenericQuery;
import de.hybris.platform.core.GenericSearchFieldType;
import de.hybris.platform.core.GenericSubQueryCondition;
import de.hybris.platform.core.GenericValueCondition;
import de.hybris.platform.core.Operator;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.hybris.backoffice.widgets.advancedsearch.engine.AdvancedSearchQueryData;
import com.hybris.cockpitng.search.data.SearchAttributeDescriptor;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;



@IntegrationTest
public class LocalizedGenericConditionQuerybuilderTest extends ServicelayerTransactionalTest
{
    private static final String typeCode = "Product";
    private CatalogVersionModel version1, version2;

    @Resource
    private TypeService typeService;
    @Resource
    private I18NService i18nService;
    @Resource
    private CommonI18NService commonI18NService;
    @Resource
    private ModelService modelService;
    private LocalizedGenericConditionQueryBuilder localizedConditionQueryBuilder;

    @Before
    public void prepare()
    {
        localizedConditionQueryBuilder = new LocalizedGenericConditionQueryBuilder();
        localizedConditionQueryBuilder.setTypeService(typeService);
        localizedConditionQueryBuilder.setCommonI18NService(commonI18NService);
        localizedConditionQueryBuilder.setI18nService(i18nService);
        prepareTestObjects();
    }

    private void prepareTestObjects()
    {
        final CatalogModel catalogModel = modelService.create(CatalogModel.class);
        catalogModel.setId("catalogModelId");
        modelService.save(catalogModel);

        version1 = modelService.create(CatalogVersionModel.class);
        version1.setVersion("version1");
        version1.setCatalog(catalogModel);
        modelService.save(version1);

        version2 = modelService.create(CatalogVersionModel.class);
        version2.setVersion("version2");
        version2.setCatalog(catalogModel);
        modelService.save(version2);


    }


    @Test
    public void testLocalizedMultiReferenceManyToManyRelation()
    {
        //given
        final GenericQuery genericQuery = new GenericQuery(typeCode);
        final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(typeCode);
        final Map<SearchAttributeDescriptor, Map.Entry<Object, ValueComparisonOperator>> attributes = new HashMap<>();

        final LanguageModel langModel = modelService.create(LanguageModel.class);
        langModel.setIsocode("fr");

        modelService.save(langModel);
        final KeywordModel keyword = modelService.create(KeywordModel.class);
        keyword.setCatalogVersion(version1);
        keyword.setKeyword("keyword");
        keyword.setLanguage(langModel);
        modelService.save(keyword);


        final Map.Entry<Object, ValueComparisonOperator> entryEquals1 = new AbstractMap.SimpleEntry<Object, ValueComparisonOperator>(
                Collections.singletonMap(commonI18NService.getLocaleForLanguage(langModel), keyword),
                ValueComparisonOperator.CONTAINS);
        attributes.put(new SearchAttributeDescriptor("keywords"), entryEquals1);
        builder.attributes(attributes);
        builder.globalOperator(ValueComparisonOperator.OR);

        final SearchQueryData searchQueryData = builder.build();

        //when
        final List<GenericCondition> genericConditions = localizedConditionQueryBuilder.buildQuery(genericQuery, typeCode,
                new SearchAttributeDescriptor("keywords"), searchQueryData);

        //then
        Assertions.assertThat(genericConditions).isNotNull();
        Assertions.assertThat(genericConditions).hasSize(1);
        Assertions.assertThat(genericConditions.get(0)).isInstanceOf(GenericConditionList.class);
        final GenericConditionList conditionList = (GenericConditionList) genericConditions.get(0);
        final GenericSubQueryCondition subQueryCondition = (GenericSubQueryCondition) conditionList.getConditionList().get(0);
        Assertions.assertThat(subQueryCondition.getSubQuery().getCondition()).isInstanceOf(GenericConditionList.class);

        final GenericConditionList subQueryConditionList = (GenericConditionList) subQueryCondition.getSubQuery().getCondition();

        Assertions.assertThat(subQueryConditionList.getConditionList().get(0)).isInstanceOf(GenericValueCondition.class);
        Assertions.assertThat(((GenericValueCondition) subQueryConditionList.getConditionList().get(0)).getValue().equals(keyword));

        Assertions.assertThat(subQueryConditionList.getConditionList().get(1)).isInstanceOf(GenericValueCondition.class);
        Assertions.assertThat(((GenericValueCondition) subQueryConditionList.getConditionList().get(1)).getValue()
                .equals(langModel));

        Assertions.assertThat(subQueryCondition.getOperator()).isEqualTo(Operator.IN);
    }


    @Test
    public void testSearchByLocalizedStringAttribute()
    {
        //given


        final GenericQuery genericQuery = new GenericQuery(typeCode);
        final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(typeCode);
        final Map<SearchAttributeDescriptor, Map.Entry<Object, ValueComparisonOperator>> attributes = new HashMap<>();

        final LanguageModel langModel = modelService.create(LanguageModel.class);
        langModel.setIsocode("fr");
        modelService.save(langModel);

        final Map.Entry<Object, ValueComparisonOperator> entryStartsWith = new AbstractMap.SimpleEntry<Object, ValueComparisonOperator>(
                Collections.singletonMap(commonI18NService.getLocaleForLanguage(langModel), "abcd"),
                ValueComparisonOperator.STARTS_WITH);
        attributes.put(new SearchAttributeDescriptor("description", 0), entryStartsWith);
        builder.attributes(attributes).globalOperator(ValueComparisonOperator.OR);

        final SearchQueryData searchQueryData = builder.build();



        //when
        final List<GenericCondition> genericConditions = localizedConditionQueryBuilder.buildQuery(genericQuery, typeCode,
                new SearchAttributeDescriptor("description"), searchQueryData);
        //then
        //then
        Assertions.assertThat(genericConditions).isNotNull();
        Assertions.assertThat(genericConditions).hasSize(1);
        Assertions.assertThat(genericConditions.get(0)).isInstanceOf(GenericConditionList.class);
        final GenericConditionList conditionList = (GenericConditionList) genericConditions.get(0);
        final GenericValueCondition genericValueCondition = (GenericValueCondition) conditionList.getConditionList().get(0);
        Assertions.assertThat(genericValueCondition.getOperator()).isEqualTo(Operator.LIKE);
        Assertions.assertThat(genericValueCondition.getField().getFieldTypes()).contains(GenericSearchFieldType.LOCALIZED);
        Assertions.assertThat(genericValueCondition.getField().getLanguagePK()).isEqualTo(langModel.getPk());
    }

}
