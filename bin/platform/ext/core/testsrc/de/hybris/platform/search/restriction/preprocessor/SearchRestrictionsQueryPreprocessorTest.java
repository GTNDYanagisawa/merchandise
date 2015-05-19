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
package de.hybris.platform.search.restriction.preprocessor;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.search.restriction.session.SessionSearchRestriction;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.preprocessor.QueryPreprocessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SearchRestrictionsQueryPreprocessorTest
{

	@InjectMocks
	private final QueryPreprocessor preprocessor = new SearchRestrictionsQueryPreprocessor();
	@Mock
	private SearchRestrictionService searchRestriction;
	@Mock
	private FlexibleSearchQuery query;
	@Mock
	private SessionSearchRestriction restriction;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldNotProcessWhenSessionSearchRestrictionsInQueryObjectIsNull() // NOPMD
	{
		// given
		given(query.getSessionSearchRestrictions()).willReturn(null);

		// when
		preprocessor.process(query);

		// then
		verify(searchRestriction, times(0)).addSessionSearchRestrictions((Collection) anyObject());
	}

	@Test
	public void shouldNotProcessWhenSessionSearchRestrictionsInQueryObjectIsEmpty() // NOPMD
	{
		// given
		given(query.getSessionSearchRestrictions()).willReturn(Collections.EMPTY_LIST);

		// when
		preprocessor.process(query);

		// then
		verify(searchRestriction, times(0)).addSessionSearchRestrictions(Collections.EMPTY_LIST);
	}

	@Test
	public void shouldProcessWhenThereIsCollectionOfSessionSearchRestrictionsInQueryObject() // NOPMD
	{
		// given
		final Collection<SessionSearchRestriction> restrictions = new ArrayList<SessionSearchRestriction>();
		restrictions.add(restriction);
		given(query.getSessionSearchRestrictions()).willReturn(restrictions);

		// when
		preprocessor.process(query);

		// then
		verify(searchRestriction, times(1)).addSessionSearchRestrictions(restrictions);
	}

}
