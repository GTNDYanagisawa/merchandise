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
package de.hybris.platform.commercesearch.searchandizing.sorting.populators;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.commercesearch.model.AbstractSolrSortConditionModel;
import de.hybris.platform.commercesearch.model.ConditionalSolrSortModel;
import de.hybris.platform.commercesearch.model.SelectedCategoryHierarchySolrSortConditionModel;
import de.hybris.platform.commercesearch.search.solrfacetsearch.populators.ConditionalSortFilteringIndexedTypePopulator;
import de.hybris.platform.commercesearch.searchandizing.sorting.SortEvaluatorService;
import de.hybris.platform.commercesearch.searchandizing.sorting.evaluators.IndexedTypeSortEvaluator;
import de.hybris.platform.commercesearch.searchandizing.sorting.evaluators.impl.SelectedCategoryHierarchyIndexedTypeSortEvaluator;
import de.hybris.platform.commerceservices.model.solrsearch.config.SolrSortModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.solrfacetsearch.config.IndexedType;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Maps;



/**
 * @author rmcotton
 * 
 */
public class ConditionalSortFilteringIndexedTypePopulatorTest
{
	ConditionalSortFilteringIndexedTypePopulator populator = new ConditionalSortFilteringIndexedTypePopulator();

	@Mock
	SortEvaluatorService sortEvaluatorService;

	@Mock
	SolrSortModel visibleSort;

	@Mock
	SolrSortModel invisibleSort;

	@Mock
	ConditionalSolrSortModel condSort;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
		given(visibleSort.getVisible()).willReturn(Boolean.TRUE);
		given(invisibleSort.getVisible()).willReturn(Boolean.FALSE);
		populator.setSortEvaluatorService(sortEvaluatorService);
	}

	@Test
	public void shouldFilterInvisibleSorts()
	{
		//given
		populator.setObserveVisible(true);

		final SolrSearchRequest target = new SolrSearchRequest();
		final IndexedType indexedType = new IndexedType();

		final IndexedTypeSort sortVisible = new IndexedTypeSort();
		sortVisible.setSort(visibleSort);
		final IndexedTypeSort sortInvisible = new IndexedTypeSort();
		sortInvisible.setSort(invisibleSort);

		final List<IndexedTypeSort> sorts = newArrayList(sortVisible, sortInvisible);
		indexedType.setSorts(sorts);
		target.setIndexedType(indexedType);
		//when
		populator.populate(null, target);

		assertThat(((IndexedType) target.getIndexedType()).getSorts()).containsOnly(sortVisible);
	}

	@Test
	public void shouldNotFilterInvisibleSortsIfFlagSetToFalse()
	{
		//given
		populator.setObserveVisible(false);

		final SolrSearchRequest target = new SolrSearchRequest();
		final IndexedType indexedType = new IndexedType();

		final IndexedTypeSort sortVisible = new IndexedTypeSort();
		sortVisible.setSort(visibleSort);
		sortVisible.setName("sortVisible");
		final IndexedTypeSort sortInvisible = new IndexedTypeSort();
		sortInvisible.setSort(invisibleSort);
		sortInvisible.setName("sortInvisible");

		final List<IndexedTypeSort> sorts = newArrayList(sortVisible, sortInvisible);
		indexedType.setSorts(sorts);
		target.setIndexedType(indexedType);
		//when
		populator.populate(null, target);

		assertThat(((IndexedType) target.getIndexedType()).getSorts()).containsOnly(sortVisible, sortInvisible);
	}

	@Test
	public void shouldFilterDuplicatedSorts()
	{
		//given
		populator.setObserveVisible(false);

		final SolrSearchRequest target = new SolrSearchRequest();
		final IndexedType indexedType = new IndexedType();

		final IndexedTypeSort sortVisible = new IndexedTypeSort();
		sortVisible.setSort(visibleSort);
		sortVisible.setName("sortVisible");
		final IndexedTypeSort sortInvisible = new IndexedTypeSort();
		sortInvisible.setSort(invisibleSort);
		sortInvisible.setName("sortInvisible");

		final List<IndexedTypeSort> sortsWithDuplicatedElement = newArrayList(sortVisible, sortInvisible, sortVisible);
		indexedType.setSorts(sortsWithDuplicatedElement);
		target.setIndexedType(indexedType);
		//when
		populator.populate(null, target);

		assertThat(((IndexedType) target.getIndexedType()).getSorts()).containsSequence(sortVisible, sortInvisible);
	}

	@Test
	public void shouldFilterConditionalSorts()
	{
		//given
		populator.setObserveVisible(false);

		final SolrSearchRequest target = new SolrSearchRequest();
		final IndexedType indexedType = new IndexedType();

		final IndexedTypeSort sortVisible = new IndexedTypeSort();
		sortVisible.setSort(condSort);
		sortVisible.setName("sortVisible");
		final IndexedTypeSort sortInvisible = new IndexedTypeSort();
		sortInvisible.setSort(invisibleSort);
		sortInvisible.setName("sortInvisible");

		final List<IndexedTypeSort> sortsWithDuplicatedElement = newArrayList(sortVisible, sortInvisible);
		indexedType.setSorts(sortsWithDuplicatedElement);
		target.setIndexedType(indexedType);

		final LinkedHashMap<AbstractSolrSortConditionModel, IndexedTypeSortEvaluator> values = Maps.newLinkedHashMap();
		final SelectedCategoryHierarchySolrSortConditionModel condition = mock(SelectedCategoryHierarchySolrSortConditionModel.class);
		given(condition.getInverse()).willReturn(Boolean.FALSE);
		final IndexedTypeSortEvaluator evaluator = mock(SelectedCategoryHierarchyIndexedTypeSortEvaluator.class);
		given(Boolean.valueOf(evaluator.evaluateFilter(target, sortVisible, condition))).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(evaluator.evaluateFilter(target, sortInvisible, condition))).willReturn(Boolean.FALSE);

		values.put(condition, evaluator);
		given(sortEvaluatorService.getEvaluatorsForConditionalSort(condSort)).willReturn(values);

		//when
		populator.populate(null, target);

		assertThat(((IndexedType) target.getIndexedType()).getSorts()).containsSequence(sortInvisible);
	}

	@Test
	public void shouldFilterConditionalSortsWithInverse()
	{
		//given
		populator.setObserveVisible(false);

		final SolrSearchRequest target = new SolrSearchRequest();
		final IndexedType indexedType = new IndexedType();

		final IndexedTypeSort sortVisible = new IndexedTypeSort();
		sortVisible.setSort(condSort);
		sortVisible.setName("sortVisible");
		final IndexedTypeSort sortInvisible = new IndexedTypeSort();
		sortInvisible.setSort(invisibleSort);
		sortInvisible.setName("sortInvisible");

		final List<IndexedTypeSort> sortsWithDuplicatedElement = newArrayList(sortVisible, sortInvisible);
		indexedType.setSorts(sortsWithDuplicatedElement);
		target.setIndexedType(indexedType);

		final LinkedHashMap<AbstractSolrSortConditionModel, IndexedTypeSortEvaluator> values = Maps.newLinkedHashMap();
		final SelectedCategoryHierarchySolrSortConditionModel condition = mock(SelectedCategoryHierarchySolrSortConditionModel.class);
		given(condition.getInverse()).willReturn(Boolean.TRUE);

		final IndexedTypeSortEvaluator evaluator = mock(SelectedCategoryHierarchyIndexedTypeSortEvaluator.class);
		given(Boolean.valueOf(evaluator.evaluateFilter(target, sortVisible, condition))).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(evaluator.evaluateFilter(target, sortInvisible, condition))).willReturn(Boolean.FALSE);

		values.put(condition, evaluator);
		given(sortEvaluatorService.getEvaluatorsForConditionalSort(condSort)).willReturn(values);

		//when
		populator.populate(null, target);

		assertThat(((IndexedType) target.getIndexedType()).getSorts()).containsSequence(sortVisible);
	}
}
